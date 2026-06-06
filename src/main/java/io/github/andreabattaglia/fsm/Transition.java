package io.github.andreabattaglia.fsm;

import java.util.Objects;

/**
 * Immutable definition of a state transition.
 *
 * <p>A transition binds a source state and an event to a destination state. It
 * may also define a {@link Guard}, used to decide whether the transition is
 * allowed for the runtime payload, and an {@link Action}, used to execute side
 * effects after the guard has accepted the transition.</p>
 *
 * <p>Transitions are identified inside a {@link StateMachineDefinition} by the
 * pair {@code (from, event)}. For this reason a single state machine cannot
 * contain two transitions with the same source state and event. Attempting to
 * add a duplicate transition through {@link StateMachineBuilder} throws a
 * {@link DuplicateTransitionException}.</p>
 *
 * <p>The state machine itself is stateless: it does not store the current state.
 * The caller passes the current state to {@link StateMachine#fire(Object,
 * Object, Object)}, receives a {@link TransitionResult}, and decides how to
 * persist or propagate the returned destination state.</p>
 *
 * @param from the source state from which this transition starts
 * @param event the event that triggers this transition
 * @param to the destination state reached when this transition is accepted
 * @param guard predicate that must return {@code true} before the action runs
 * @param action side effect executed after the guard accepts the transition
 * @param <S> the state type handled by the state machine
 * @param <E> the event type handled by the state machine
 * @param <C> the payload/context type passed by the caller when firing events
 */
public record Transition<S, E, C>(S from, E event, S to, Guard<S, E, C> guard, Action<S, E, C> action) {

    /**
     * Creates a transition definition.
     *
     * @throws NullPointerException when any transition component except the
     *                              runtime payload is {@code null}
     */
    public Transition {
        Objects.requireNonNull(from, "from must not be null");
        Objects.requireNonNull(event, "event must not be null");
        Objects.requireNonNull(to, "to must not be null");
        Objects.requireNonNull(guard, "guard must not be null");
        Objects.requireNonNull(action, "action must not be null");
    }

    /**
     * Creates a simple transition with an always-true guard and a no-op action.
     *
     * <p>Use this factory for transitions whose only purpose is to move from one
     * state to another when a specific event is received.</p>
     *
     * @param from the source state
     * @param event the triggering event
     * @param to the destination state
     * @param <S> the state type handled by the state machine
     * @param <E> the event type handled by the state machine
     * @param <C> the payload/context type passed by the caller when firing events
     * @return a transition with {@link Guard#always()} and {@link Action#none()}
     */
    public static <S, E, C> Transition<S, E, C> of(S from, E event, S to) {
        return new Transition<>(from, event, to, Guard.always(), Action.none());
    }

    /**
     * Creates a transition with an explicit guard and action.
     *
     * @param from the source state
     * @param event the triggering event
     * @param to the destination state
     * @param guard predicate that must accept the transition
     * @param action side effect executed after guard acceptance
     * @param <S> the state type handled by the state machine
     * @param <E> the event type handled by the state machine
     * @param <C> the payload/context type passed by the caller when firing events
     * @return a transition containing the provided components
     */
    public static <S, E, C> Transition<S, E, C> of(
            S from,
            E event,
            S to,
            Guard<S, E, C> guard,
            Action<S, E, C> action
    ) {
        return new Transition<>(from, event, to, guard, action);
    }
}
