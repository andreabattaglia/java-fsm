package io.github.andreabattaglia.fsm;

import java.util.Set;

/**
 * Transition policy describing the FSM graph and transition rules.
 *
 * <p>This abstraction defines which states exist, which one is initial,
 * which are terminal, and how to move from one state to another after
 * a command outcome.
 *
 * <p>Contract: there is always exactly one initial state; terminal states
 * are one or more.
 *
 * @param <E> enum type for the state catalog
 * @param <C> context type for this state machine
 */
public abstract class FsmTransitionPolicy<E extends Enum<E>, C extends FsmContext>
{
    /**
     * Returns all states that belong to this machine.
     *
     * <p>Used by the orchestrator for structural validation at startup.
     *
     * @return all states declared by this machine graph
     */
    public abstract Set<E> states();

    /**
     * Returns the single entry-point state for this machine.
     *
     * @return unique initial state for this machine graph
     */
    public abstract E initialState();

    /**
     * Tells whether a state is terminal.
     *
     * <p>When this returns {@code true}, the orchestrator stops the loop.
     * One or more states can be terminal.
     *
     * @param state state to evaluate
     * @return true when the given state is terminal
     */
    public abstract boolean isTerminal(E state);

    /**
     * Resolves the next state from the current state and command outcome.
     *
     * <p>This is the core transition function of the FSM. Implementations
     * must never return {@code null}.
     *
     * @param currentState current state code
     * @param outcome      command outcome produced in current state
     * @param context      execution context
     * @return next state code
     */
    public abstract E next(E currentState, FsmCommandOutcome outcome, C context);
}
