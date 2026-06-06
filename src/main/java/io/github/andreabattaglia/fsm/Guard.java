package io.github.andreabattaglia.fsm;

@FunctionalInterface
public interface Guard<S, E, C> {

    boolean permits(TransitionContext<S, E, C> context);

    static <S, E, C> Guard<S, E, C> always() {
        return context -> true;
    }
}
