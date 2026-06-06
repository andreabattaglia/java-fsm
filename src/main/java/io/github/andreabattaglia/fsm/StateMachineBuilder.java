package io.github.andreabattaglia.fsm;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class StateMachineBuilder<S, E, C> {

    private final Map<TransitionKey<S, E>, Transition<S, E, C>> transitions = new LinkedHashMap<>();
    private S initialState;

    StateMachineBuilder() {
    }

    public StateMachineBuilder<S, E, C> initialState(S initialState) {
        this.initialState = Objects.requireNonNull(initialState, "initialState must not be null");
        return this;
    }

    public StateMachineBuilder<S, E, C> transition(S from, E event, S to) {
        return transition(Transition.of(from, event, to));
    }

    public StateMachineBuilder<S, E, C> transition(
            S from,
            E event,
            S to,
            Guard<S, E, C> guard,
            Action<S, E, C> action
    ) {
        return transition(Transition.of(from, event, to, guard, action));
    }

    public StateMachineBuilder<S, E, C> transition(Transition<S, E, C> transition) {
        Objects.requireNonNull(transition, "transition must not be null");
        var key = new TransitionKey<>(transition.from(), transition.event());
        if (transitions.containsKey(key)) {
            throw new DuplicateTransitionException(transition.from(), transition.event());
        }
        transitions.put(key, transition);
        return this;
    }

    public StateMachine<S, E, C> build() {
        if (initialState == null) {
            throw new IllegalStateException("initialState must be configured");
        }
        return new StateMachine<>(new StateMachineDefinition<>(initialState, transitions));
    }
}
