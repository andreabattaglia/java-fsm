package io.github.andreabattaglia.fsm;

/**
 * Executable unit of work bound to one FSM state.
 *
 * <p>Think of a command as "what the machine does while it is in state X".
 * A command does not decide transitions directly. It only returns an
 * {@link FsmCommandOutcome}; the transition decision is delegated to
 * {@link FsmTransitionPolicy}.
 *
 * @param <E> enum type for the state catalog
 * @param <C> context type for this state machine
 */
public interface FsmCommand<E extends Enum<E>, C extends FsmContext>
{
    /**
     * Declares the state code handled by this command.
     *
     * @return state code this command is responsible for
     */
    E forStateCode();

    /**
     * Executes the business step for this state.
     *
     * @param context execution context for the current FSM run
     * @return normalized command outcome used by the transition policy
     * @throws FsmCommandException when the step fails and must be mapped
     *                             by {@link FsmExceptionHandler}
     */
    FsmCommandOutcome execute(C context) throws FsmCommandException;
}
