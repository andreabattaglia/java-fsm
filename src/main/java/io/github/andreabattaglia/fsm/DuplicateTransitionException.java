package io.github.andreabattaglia.fsm;

/**
 * Thrown when a {@link StateMachineBuilder} receives two transitions with the
 * same source state and event.
 *
 * <p>A state machine definition identifies transitions by the pair
 * {@code (from, event)}. Allowing more than one transition for that pair would
 * make runtime execution ambiguous, because {@link StateMachine#fire(Object,
 * Object, Object)} would not know which destination, guard, or action to use.
 * The builder therefore fails fast when a duplicate is added.</p>
 */
public class DuplicateTransitionException extends StateMachineException {

    /**
     * Creates an exception describing the duplicated transition key.
     *
     * @param state duplicated source state
     * @param event duplicated event
     */
    public DuplicateTransitionException(Object state, Object event) {
        super("Duplicate transition from state '%s' on event '%s'".formatted(state, event));
    }
}
