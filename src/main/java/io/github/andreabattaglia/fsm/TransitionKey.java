package io.github.andreabattaglia.fsm;

import java.util.Objects;

record TransitionKey<S, E>(S state, E event) {

    TransitionKey {
        Objects.requireNonNull(state, "state must not be null");
        Objects.requireNonNull(event, "event must not be null");
    }
}
