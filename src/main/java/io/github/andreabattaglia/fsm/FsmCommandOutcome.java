package io.github.andreabattaglia.fsm;

/**
 * Normalized result produced by a command execution.
 *
 * <p>The outcome is consumed by {@link FsmTransitionPolicy#next(Enum, FsmCommandOutcome, FsmContext)}
 * to decide the next state.
 */
public enum FsmCommandOutcome
{
    /** Command completed correctly. */
    SUCCESS,

    /** Command failed with a temporary issue and can be retried. */
    RETRYABLE_ERROR,

    /** Command failed with a non-recoverable issue. */
    FATAL_ERROR,

    /** Command intentionally skipped processing. */
    SKIP
}
