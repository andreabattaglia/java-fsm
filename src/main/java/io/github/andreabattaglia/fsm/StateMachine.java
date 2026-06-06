package io.github.andreabattaglia.fsm;

import java.util.Objects;

/**
 * Stateless executor for a finite state machine definition.
 *
 * <p>This class represents the runtime API used to fire events against a
 * configured set of transitions. It deliberately does not keep mutable current
 * state. The caller provides the current state on every invocation of
 * {@link #fire(Object, Object, Object)} and receives the destination state in
 * the returned {@link TransitionResult}. This design keeps the class thread-safe
 * and makes persistence decisions explicit in the application layer.</p>
 *
 * <p>A state machine is created through {@link #builder()}. The builder collects
 * an initial state and a set of transitions, then produces an immutable
 * {@link StateMachineDefinition}. Once built, the machine can be shared across
 * threads as long as the caller-provided guards and actions are themselves safe
 * for the intended use.</p>
 *
 * <p>The event firing algorithm is intentionally simple and predictable:</p>
 *
 * <ol>
 *     <li>Validate that the current state and event are not {@code null}.</li>
 *     <li>Find the transition keyed by the pair {@code (currentState, event)}.</li>
 *     <li>Build a {@link TransitionContext} with the transition and payload.</li>
 *     <li>Evaluate the transition guard.</li>
 *     <li>Execute the transition action.</li>
 *     <li>Return a successful {@link TransitionResult}.</li>
 * </ol>
 *
 * <p>No fallback transition, wildcard event, implicit state mutation, retry, or
 * error swallowing is performed. Applications that need those policies should
 * implement them around this small core.</p>
 *
 * @param <S> the state type handled by the state machine
 * @param <E> the event type handled by the state machine
 * @param <C> the payload/context type passed by the caller when firing events
 */
public final class StateMachine<S, E, C> {

    private final StateMachineDefinition<S, E, C> definition;

    StateMachine(StateMachineDefinition<S, E, C> definition) {
        this.definition = Objects.requireNonNull(definition, "definition must not be null");
    }

    /**
     * Creates a new builder for a state machine.
     *
     * <p>The returned builder is mutable and is intended to be used during
     * application setup. The object produced by {@link StateMachineBuilder#build()}
     * is immutable and safe to reuse.</p>
     *
     * @param <S> the state type handled by the state machine
     * @param <E> the event type handled by the state machine
     * @param <C> the payload/context type passed by the caller when firing events
     * @return a new empty state machine builder
     */
    public static <S, E, C> StateMachineBuilder<S, E, C> builder() {
        return new StateMachineBuilder<>();
    }

    /**
     * Returns the initial state declared in the state machine definition.
     *
     * <p>The initial state is metadata. The state machine does not automatically
     * start from it, because this class does not own mutable current state.
     * Callers typically use this value when creating a new workflow instance or
     * when initializing persistent state for the first time.</p>
     *
     * @return the non-null initial state configured in the builder
     */
    public S initialState() {
        return definition.initialState();
    }

    /**
     * Returns the immutable definition backing this state machine.
     *
     * <p>The definition exposes useful metadata such as configured states and
     * transitions. It can be used for diagnostics, graph rendering,
     * documentation, tests, or validation tooling.</p>
     *
     * @return the immutable state machine definition
     */
    public StateMachineDefinition<S, E, C> definition() {
        return definition;
    }

    /**
     * Fires an event from the provided current state and returns the accepted
     * transition result.
     *
     * <p>The method succeeds only when a transition exists for the exact
     * {@code (currentState, event)} pair and when that transition guard returns
     * {@code true}. The transition action is executed before the result is
     * returned. If the action throws an exception, that exception propagates to
     * the caller and no result is returned.</p>
     *
     * <p>The payload is passed unchanged to the guard, the action, and the final
     * result. It may be {@code null}. The state machine does not copy or mutate
     * it.</p>
     *
     * @param currentState current state supplied by the caller
     * @param event event to apply from the current state
     * @param payload runtime payload/context passed to guard and action,
     *                possibly {@code null}
     * @return successful transition result containing source state, event,
     *         destination state, and payload
     * @throws NullPointerException when {@code currentState} or {@code event} is
     *                              {@code null}
     * @throws TransitionNotFoundException when no transition exists for the
     *                                     provided state and event
     * @throws GuardRejectedException when the matching transition guard returns
     *                                {@code false}
     */
    public TransitionResult<S, E, C> fire(S currentState, E event, C payload) {
        Objects.requireNonNull(currentState, "currentState must not be null");
        Objects.requireNonNull(event, "event must not be null");

        var transition = definition.transitionFor(currentState, event)
                .orElseThrow(() -> new TransitionNotFoundException(currentState, event));
        var context = new TransitionContext<>(transition.from(), transition.event(), transition.to(), payload);

        if (!transition.guard().permits(context)) {
            throw new GuardRejectedException(currentState, event);
        }

        transition.action().execute(context);
        return new TransitionResult<>(transition.from(), transition.event(), transition.to(), payload);
    }
}
