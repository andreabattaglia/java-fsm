package io.github.andreabattaglia.fsm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

/**
 * Abstract state-machine engine.
 *
 * <p>The orchestration algorithm is: read initial state from policy, loop
 * until terminal state, execute the command bound to current state, map
 * command output to next state through policy, repeat.
 *
 * <p>Before running, the orchestrator validates structural consistency:
 * every declared state must have a command and command/state codes must match.
 *
 * <p>Expected policy invariants: exactly one initial state, one or more
 * terminal states.
 *
 * @param <E> enum type for the state catalog
 * @param <C> context type for this state machine
 */
public abstract class FsmOrchestratorService<E extends Enum<E>, C extends FsmContext>
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final FsmCommandFactory<E, C> commandFactory;
    private final FsmTransitionPolicy<E, C> transitionPolicy;
    private final FsmExceptionHandler<E, C> exceptionHandler;

    protected FsmOrchestratorService(
        FsmCommandFactory<E, C> commandFactory,
        FsmTransitionPolicy<E, C> transitionPolicy,
        FsmExceptionHandler<E, C> exceptionHandler)
    {
        this.commandFactory = Objects.requireNonNull(commandFactory, "commandFactory");
        this.transitionPolicy = Objects.requireNonNull(transitionPolicy, "transitionPolicy");
        this.exceptionHandler = Objects.requireNonNull(exceptionHandler, "exceptionHandler");
    }

    /**
     * Executes one full FSM run starting from the policy's initial state.
     *
     * @param context execution context shared across states
     * @return immutable execution result containing terminal state and step count
     */
    public FsmExecutionResult<E> execute(C context)
    {
        Objects.requireNonNull(context, "context");
        validateGraphCoverage();

        E initial = transitionPolicy.initialState();
        if (initial == null)
        {
            throw new IllegalStateException("fsm initial state cannot be null");
        }
        if (!transitionPolicy.states().contains(initial))
        {
            throw new IllegalStateException(
                "fsm initial state not declared in graph: " + initial);
        }
        return runLoop(context, initial, false);
    }

    /**
     * Executes one episodic FSM run starting from {@code fromState}.
     *
     * <p>Unlike {@link #execute(FsmContext)}, this overload supports self-loops:
     * when the transition policy resolves to the same state that was just executed,
     * the episode terminates without executing the terminal command. This models
     * the "nothing more to do in this run, resume next time" semantic.
     *
     * @param context   execution context shared across states
     * @param fromState the state to start from (must be declared in the policy)
     * @return immutable execution result containing the last state and step count
     */
    public FsmExecutionResult<E> execute(C context, E fromState)
    {
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(fromState, "fromState");
        validateGraphCoverage();

        if (!transitionPolicy.states().contains(fromState))
        {
            throw new IllegalStateException(
                "fsm fromState not declared in graph: " + fromState);
        }
        return runLoop(context, fromState, true);
    }

    private FsmExecutionResult<E> runLoop(C context, E start, boolean episodic)
    {
        Set<E> declaredStates = transitionPolicy.states();
        E current = start;
        int steps = 0;

        while (!transitionPolicy.isTerminal(current))
        {
            FsmCommand<E, C> command = requireCommand(current);
            String inputSnapshot = contextSnapshot(context);

            FsmCommandOutcome outcome;
            try
            {
                outcome = command.execute(context);
            }
            catch (FsmCommandException ex)
            {
                outcome = exceptionHandler.onException(context, current, ex);
                if (outcome == null)
                {
                    throw new IllegalStateException(
                        "fsm exception handler returned null outcome for state=" + current);
                }
            }

            E next = transitionPolicy.next(current, outcome, context);
            if (next == null)
            {
                throw new IllegalStateException(
                    "fsm next state cannot be null for state=" + current
                        + ", outcome=" + outcome);
            }
            if (!declaredStates.contains(next))
            {
                throw new IllegalStateException(
                    "fsm next state is not declared in graph: " + next);
            }

            log.debug("FSM_STEP from={} command={} input={} outcome={} to={} output={}",
                current,
                command.getClass().getSimpleName(),
                inputSnapshot,
                outcome,
                next,
                contextSnapshot(context));

            if (episodic && next.equals(current))
            {
                break; // self-loop: episode complete, state unchanged
            }

            current = next;
            steps++;
        }

        if (transitionPolicy.isTerminal(current))
        {
            FsmCommand<E, C> terminalCommand = requireCommand(current);
            try
            {
                terminalCommand.execute(context);
            }
            catch (FsmCommandException ex)
            {
                log.warn("fsm terminal command failed state={}: {}", current, ex.getMessage());
            }
        }

        return FsmExecutionResult.of(current, steps);
    }

    private void validateGraphCoverage()
    {
        Set<E> states = transitionPolicy.states();
        if (states == null || states.isEmpty())
        {
            throw new IllegalStateException("fsm states cannot be null or empty");
        }
        for (E state : states)
        {
            FsmCommand<E, C> command = requireCommand(state);
            E commandStateCode = command.forStateCode();
            if (!state.equals(commandStateCode))
            {
                throw new IllegalStateException(
                    "fsm command/state mismatch for state=" + state
                        + ", command.forStateCode()=" + commandStateCode);
            }
        }
    }

    private FsmCommand<E, C> requireCommand(E state)
    {
        FsmCommand<E, C> command = commandFactory.commandFor(state);
        if (command == null)
        {
            throw new IllegalStateException(
                "fsm command cannot be null for state=" + state);
        }
        return command;
    }

    private String contextSnapshot(C context)
    {
        if (context == null)
        {
            return "<null>";
        }
        StringJoiner joiner = new StringJoiner(", ", "{", "}");
        Class<?> type = context.getClass();
        while (type != null && type != Object.class)
        {
            for (Field field : type.getDeclaredFields())
            {
                if (field.isSynthetic() || Modifier.isStatic(field.getModifiers()))
                {
                    continue;
                }
                try
                {
                    field.setAccessible(true);
                    Object value = field.get(context);
                    joiner.add(field.getName() + "=" + compactValue(value));
                }
                catch (IllegalAccessException ex)
                {
                    joiner.add(field.getName() + "=<inaccessible>");
                }
            }
            type = type.getSuperclass();
        }
        return joiner.toString();
    }

    private String compactValue(Object value)
    {
        if (value == null)
        {
            return "null";
        }
        if (value instanceof Collection<?> collection)
        {
            return "Collection(size=" + collection.size() + ")";
        }
        if (value instanceof Map<?, ?> map)
        {
            return "Map(size=" + map.size() + ")";
        }
        if (value.getClass().isArray())
        {
            return "Array";
        }
        String rendered = String.valueOf(value);
        if (rendered.length() > 300)
        {
            return rendered.substring(0, 300) + "...";
        }
        return rendered;
    }
}
