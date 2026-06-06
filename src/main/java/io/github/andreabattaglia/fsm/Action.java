package io.github.andreabattaglia.fsm;

/**
 * Represents the side effect executed when a transition is accepted by a
 * {@link StateMachine}.
 *
 * <p>An action is invoked after the target transition has been found and after
 * its {@link Guard} has returned {@code true}. The action receives the same
 * immutable {@link TransitionContext} used by the guard, so it can inspect the
 * source state, the event, the destination state, and the caller-provided
 * payload.</p>
 *
 * <p>The state machine does not catch exceptions thrown by an action. This is
 * intentional: action failures normally represent application-level failures
 * that the caller must handle explicitly. If an action throws, the call to
 * {@link StateMachine#fire(Object, Object, Object)} fails and no
 * {@link TransitionResult} is returned.</p>
 *
 * <p>The type parameters are deliberately unconstrained. States and events are
 * commonly enums, but they may be any non-null Java type with stable equality
 * semantics. The payload may be any application-specific context object and may
 * also be {@code null} if the caller does not need to pass context.</p>
 *
 * @param <S> the state type handled by the state machine
 * @param <E> the event type handled by the state machine
 * @param <C> the payload/context type passed by the caller when firing events
 */
@FunctionalInterface
public interface Action<S, E, C> {

    /**
     * Executes the side effect associated with an accepted transition.
     *
     * <p>The provided context is never {@code null}. Its {@code from},
     * {@code event}, and {@code to} components are never {@code null}. Its
     * {@code payload} component may be {@code null}, because payload nullability
     * is controlled by the caller and by the application model.</p>
     *
     * @param context immutable transition context describing the accepted
     *                transition and the caller-provided payload
     */
    void execute(TransitionContext<S, E, C> context);

    /**
     * Returns an action that deliberately performs no work.
     *
     * <p>This is the default action used by simple transitions. It is useful
     * when a state change is purely declarative and no audit, persistence,
     * notification, or other side effect is required.</p>
     *
     * @param <S> the state type handled by the state machine
     * @param <E> the event type handled by the state machine
     * @param <C> the payload/context type passed by the caller when firing events
     * @return an action that ignores its context and completes normally
     */
    static <S, E, C> Action<S, E, C> none() {
        return context -> {
        };
    }
}
