package io.github.andreabattaglia.fsm;

/**
 * Error-mapping strategy used by the orchestrator.
 *
 * <p>When a command throws {@link FsmCommandException}, the orchestrator
 * delegates to this handler to convert the exception into a
 * {@link FsmCommandOutcome}. This keeps error policy centralized and explicit.
 *
 * @param <E> enum type for the state catalog
 * @param <C> context type for this state machine
 */
@FunctionalInterface
public interface FsmExceptionHandler<E extends Enum<E>, C extends FsmContext>
{
    /**
     * Maps a command exception to a normalized outcome.
     *
     * <p>Must never return {@code null}.
     *
     * @param context      execution context for this run
     * @param currentState state where the failure happened
     * @param exception    thrown command exception
     * @return mapped outcome
     */
    FsmCommandOutcome onException(C context, E currentState, FsmCommandException exception);
}
