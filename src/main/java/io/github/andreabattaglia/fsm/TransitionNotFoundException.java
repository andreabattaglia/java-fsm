package io.github.andreabattaglia.fsm;

/**
 * Thrown when a {@link StateMachine} cannot find a transition for the requested
 * source state and event.
 *
 * <p>Lookups are exact: the state and event passed to
 * {@link StateMachine#fire(Object, Object, Object)} must match a transition
 * configured through {@link StateMachineBuilder}. The library does not apply
 * fallback transitions, wildcard events, string normalization, enum name
 * conversion, or hierarchy-based resolution.</p>
 *
 * <p>This exception indicates a modeling or caller-flow issue. Either the
 * transition is missing from the definition, or the caller supplied a state that
 * is not valid for the event being fired.</p>
 */
public class TransitionNotFoundException extends StateMachineException {

    /**
     * Creates an exception describing the missing transition key.
     *
     * @param state source state used for the lookup
     * @param event event used for the lookup
     */
    public TransitionNotFoundException(Object state, Object event) {
        super("No transition found from state '%s' on event '%s'".formatted(state, event));
    }
}
