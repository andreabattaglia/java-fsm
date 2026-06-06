package io.github.andreabattaglia.fsm;

/**
 * Represents the predicate that decides whether a configured transition may be
 * executed for a given {@link TransitionContext}.
 *
 * <p>A guard is evaluated after the state machine has found a transition for
 * the current state and event, but before the transition action is executed.
 * Returning {@code true} allows the transition to proceed. Returning
 * {@code false} rejects the transition and causes
 * {@link StateMachine#fire(Object, Object, Object)} to throw a
 * {@link GuardRejectedException}.</p>
 *
 * <p>Guards should normally be deterministic and side-effect free. They are a
 * good place for business predicates such as checking payload values, required
 * flags, user permissions, or preconditions. Side effects should be implemented
 * in {@link Action} instead, so the transition model remains easy to reason
 * about.</p>
 *
 * <p>The state machine does not catch exceptions thrown by a guard. If a guard
 * throws, the call to {@code fire} fails and the action is not executed.</p>
 *
 * @param <S> the state type handled by the state machine
 * @param <E> the event type handled by the state machine
 * @param <C> the payload/context type passed by the caller when firing events
 */
@FunctionalInterface
public interface Guard<S, E, C> {

    /**
     * Evaluates whether the transition described by the context may proceed.
     *
     * @param context immutable transition context describing the candidate
     *                transition and the caller-provided payload
     * @return {@code true} when the transition is allowed; {@code false} when
     *         it must be rejected
     */
    boolean permits(TransitionContext<S, E, C> context);

    /**
     * Returns a guard that always allows the transition.
     *
     * <p>This is the default guard used by simple transitions. It expresses that
     * the transition is valid whenever the source state and event match.</p>
     *
     * @param <S> the state type handled by the state machine
     * @param <E> the event type handled by the state machine
     * @param <C> the payload/context type passed by the caller when firing events
     * @return a guard that always returns {@code true}
     */
    static <S, E, C> Guard<S, E, C> always() {
        return context -> true;
    }
}
