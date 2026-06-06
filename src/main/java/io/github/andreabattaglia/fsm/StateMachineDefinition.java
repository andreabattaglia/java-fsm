package io.github.andreabattaglia.fsm;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class StateMachineDefinition<S, E, C> {

    private final S initialState;
    private final Map<TransitionKey<S, E>, Transition<S, E, C>> transitions;

    StateMachineDefinition(S initialState, Map<TransitionKey<S, E>, Transition<S, E, C>> transitions) {
        this.initialState = Objects.requireNonNull(initialState, "initialState must not be null");
        this.transitions = Map.copyOf(transitions);
    }

    public S initialState() {
        return initialState;
    }

    public Optional<Transition<S, E, C>> transitionFor(S state, E event) {
        return Optional.ofNullable(transitions.get(new TransitionKey<>(state, event)));
    }

    public Collection<Transition<S, E, C>> transitions() {
        return transitions.values();
    }

    public Set<S> states() {
        var states = new LinkedHashSet<S>();
        states.add(initialState);
        transitions.values().forEach(transition -> {
            states.add(transition.from());
            states.add(transition.to());
        });
        return Set.copyOf(states);
    }
}
