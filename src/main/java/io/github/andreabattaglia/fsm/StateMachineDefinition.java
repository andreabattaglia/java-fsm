package io.github.andreabattaglia.fsm;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Immutable structural definition of a {@link StateMachine}.
 *
 * <p>The definition contains the initial state metadata and the configured set
 * of transitions. It is separated from {@link StateMachine} so callers can
 * inspect the model without firing events. This is useful for tests,
 * diagnostics, documentation generation, UI graph rendering, or application
 * startup validation.</p>
 *
 * <p>Instances are created by {@link StateMachineBuilder}. The transition map is
 * copied during construction, so the definition is not affected by later changes
 * to the builder.</p>
 *
 * @param <S> the state type handled by the state machine
 * @param <E> the event type handled by the state machine
 * @param <C> the payload/context type passed by the caller when firing events
 */
public final class StateMachineDefinition<S, E, C> {

    private final S initialState;
    private final Map<TransitionKey<S, E>, Transition<S, E, C>> transitions;

    StateMachineDefinition(S initialState, Map<TransitionKey<S, E>, Transition<S, E, C>> transitions) {
        this.initialState = Objects.requireNonNull(initialState, "initialState must not be null");
        this.transitions = Map.copyOf(transitions);
    }

    /**
     * Returns the initial state configured in the builder.
     *
     * <p>This value is metadata only. The state machine remains stateless and
     * does not automatically mutate from this state.</p>
     *
     * @return the non-null initial state
     */
    public S initialState() {
        return initialState;
    }

    /**
     * Finds the transition configured for the exact source state and event.
     *
     * <p>The lookup is exact and uses the equality semantics of the supplied
     * state and event types. No wildcard, hierarchy, case normalization, or
     * fallback lookup is applied.</p>
     *
     * @param state source state to look up
     * @param event event to look up
     * @return optional transition for the pair {@code (state, event)}
     * @throws NullPointerException when {@code state} or {@code event} is
     *                              {@code null}
     */
    public Optional<Transition<S, E, C>> transitionFor(S state, E event) {
        return Optional.ofNullable(transitions.get(new TransitionKey<>(state, event)));
    }

    /**
     * Returns the configured transitions.
     *
     * <p>The returned collection is backed by the immutable definition content.
     * It should be treated as read-only. Transition ordering is not part of the
     * public contract and callers must not rely on it for behavior.</p>
     *
     * @return read-only collection of configured transitions
     */
    public Collection<Transition<S, E, C>> transitions() {
        return transitions.values();
    }

    /**
     * Returns all states mentioned by the definition.
     *
     * <p>The returned set contains the initial state and every source and
     * destination state referenced by configured transitions. It is derived from
     * the transition definition, so isolated states that are not the initial
     * state and are not referenced by any transition are not represented.</p>
     *
     * @return read-only set of states known to this definition
     */
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
