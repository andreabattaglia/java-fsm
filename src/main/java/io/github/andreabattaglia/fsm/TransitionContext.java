package io.github.andreabattaglia.fsm;

import java.util.Objects;

/**
 * Immutable description of a transition being evaluated or executed.
 *
 * <p>A transition context is created by {@link StateMachine#fire(Object, Object,
 * Object)} after a matching {@link Transition} has been found. The same context
 * instance is passed first to the transition {@link Guard} and then, if the
 * guard permits the transition, to the transition {@link Action}.</p>
 *
 * <p>The {@code from}, {@code event}, and {@code to} values are guaranteed to be
 * non-null. The {@code payload} value is intentionally allowed to be
 * {@code null}: some state machines do not require runtime context, and forcing
 * callers to allocate placeholder objects would add noise without improving the
 * transition model.</p>
 *
 * @param from the source state from which the transition starts
 * @param event the event that triggered the transition
 * @param to the destination state reached by the transition
 * @param payload caller-provided runtime context, possibly {@code null}
 * @param <S> the state type handled by the state machine
 * @param <E> the event type handled by the state machine
 * @param <C> the payload/context type passed by the caller when firing events
 */
public record TransitionContext<S, E, C>(S from, E event, S to, C payload) {

    /**
     * Creates a transition context.
     *
     * @throws NullPointerException when {@code from}, {@code event}, or
     *                              {@code to} is {@code null}
     */
    public TransitionContext {
        Objects.requireNonNull(from, "from must not be null");
        Objects.requireNonNull(event, "event must not be null");
        Objects.requireNonNull(to, "to must not be null");
    }
}
