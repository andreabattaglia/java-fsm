package io.github.andreabattaglia.fsm;

import java.util.Objects;

public record TransitionResult<S, E, C>(S from, E event, S to, C payload) {

    public TransitionResult {
        Objects.requireNonNull(from, "from must not be null");
        Objects.requireNonNull(event, "event must not be null");
        Objects.requireNonNull(to, "to must not be null");
    }
}
