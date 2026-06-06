package io.github.andreabattaglia.fsm;

import java.util.Objects;

public record Transition<S, E, C>(S from, E event, S to, Guard<S, E, C> guard, Action<S, E, C> action) {

    public Transition {
        Objects.requireNonNull(from, "from must not be null");
        Objects.requireNonNull(event, "event must not be null");
        Objects.requireNonNull(to, "to must not be null");
        Objects.requireNonNull(guard, "guard must not be null");
        Objects.requireNonNull(action, "action must not be null");
    }

    public static <S, E, C> Transition<S, E, C> of(S from, E event, S to) {
        return new Transition<>(from, event, to, Guard.always(), Action.none());
    }

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
