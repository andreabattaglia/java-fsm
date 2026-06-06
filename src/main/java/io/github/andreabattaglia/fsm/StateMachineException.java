package io.github.andreabattaglia.fsm;

/**
 * Base unchecked exception for failures produced by this finite state machine
 * library.
 *
 * <p>The library uses unchecked exceptions because transition execution usually
 * belongs to application control flow: a missing transition, a rejected guard,
 * or an invalid definition normally indicates that the caller attempted an
 * unsupported operation for the current workflow state. Callers that want to
 * treat those cases as recoverable may catch this base type or one of its more
 * specific subclasses.</p>
 *
 * <p>Exceptions thrown by user-provided {@link Guard guards} or {@link Action
 * actions} are not wrapped by the state machine. They propagate as originally
 * thrown so application code can preserve domain-specific exception types.</p>
 */
public class StateMachineException extends RuntimeException {

    /**
     * Creates a state machine exception with a detail message.
     *
     * @param message human-readable failure description
     */
    public StateMachineException(String message) {
        super(message);
    }

    /**
     * Creates a state machine exception with a detail message and cause.
     *
     * @param message human-readable failure description
     * @param cause original cause of the failure
     */
    public StateMachineException(String message, Throwable cause) {
        super(message, cause);
    }
}
