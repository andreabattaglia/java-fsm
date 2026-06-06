package io.github.andreabattaglia.fsm;

/**
 * Thrown when a matching transition is found but its {@link Guard} rejects the
 * runtime context.
 *
 * <p>This exception is different from {@link TransitionNotFoundException}. A
 * rejected guard means the transition exists in the model, but application
 * rules say it cannot be taken for the current payload. A missing transition
 * means the model does not define any transition for the requested state and
 * event.</p>
 *
 * <p>When this exception is thrown, the transition {@link Action} is not
 * executed and no {@link TransitionResult} is returned.</p>
 */
public class GuardRejectedException extends StateMachineException {

    /**
     * Creates an exception describing the rejected transition attempt.
     *
     * @param state source state from which the event was fired
     * @param event event rejected by the transition guard
     */
    public GuardRejectedException(Object state, Object event) {
        super("Transition guard rejected event '%s' from state '%s'".formatted(event, state));
    }
}
