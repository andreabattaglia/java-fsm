package io.github.andreabattaglia.fsm;

/**
 * Immutable result produced by one FSM run.
 *
 * <p>Contains two pieces of data: the terminal state reached and the number
 * of state transitions executed.
 *
 * @param <E> enum type for the state catalog
 */
public class FsmExecutionResult<E extends Enum<E>>
{
    private final E finalState;
    private final int steps;

    private FsmExecutionResult(E finalState, int steps)
    {
        this.finalState = finalState;
        this.steps = steps;
    }

    /**
     * Factory method for result creation.
     *
     * @param finalState terminal state reached by the machine
     * @param steps      number of transitions executed
     * @param <E>        enum type for the state catalog
     * @return immutable execution result
     */
    public static <E extends Enum<E>> FsmExecutionResult<E> of(E finalState, int steps)
    {
        return new FsmExecutionResult<>(finalState, steps);
    }

    /**
     * Returns the terminal state reached when the FSM loop stopped.
     *
     * @return the last state code recorded by the FSM engine
     */
    public E getFinalState()
    {
        return finalState;
    }

    /**
     * Returns the number of state transitions executed during this FSM run.
     * A value of {@code 0} means the machine was already in a terminal (or
     * self-looping) state before executing any command.
     *
     * @return the number of transitions executed; always {@code >= 0}
     */
    public int getSteps()
    {
        return steps;
    }
}
