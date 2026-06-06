package io.github.andreabattaglia.fsm;

import java.util.Objects;

/**
 * Immutable result returned after a transition has been successfully executed.
 *
 * <p>A result is returned only when all transition steps complete: the matching
 * transition is found, its guard accepts the context, and its action completes
 * normally. Missing transitions, rejected guards, and action failures are
 * represented by exceptions rather than by negative result objects.</p>
 *
 * <p>The result repeats the source state, event, destination state, and payload
 * so callers can use it directly for logging, persistence, audit trails, or
 * workflow orchestration without needing to keep a separate copy of the
 * transition context.</p>
 *
 * @param from the source state from which the transition started
 * @param event the event that triggered the transition
 * @param to the destination state reached by the transition
 * @param payload caller-provided runtime context, possibly {@code null}
 * @param <S> the state type handled by the state machine
 * @param <E> the event type handled by the state machine
 * @param <C> the payload/context type passed by the caller when firing events
 */
public record TransitionResult<S, E, C>(S from, E event, S to, C payload) {

    /**
     * Creates a successful transition result.
     *
     * @throws NullPointerException when {@code from}, {@code event}, or
     *                              {@code to} is {@code null}
     */
    public TransitionResult {
        Objects.requireNonNull(from, "from must not be null");
        Objects.requireNonNull(event, "event must not be null");
        Objects.requireNonNull(to, "to must not be null");
    }
}
