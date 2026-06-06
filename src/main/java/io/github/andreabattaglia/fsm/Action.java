package io.github.andreabattaglia.fsm;

@FunctionalInterface
public interface Action<S, E, C> {

    void execute(TransitionContext<S, E, C> context);

    static <S, E, C> Action<S, E, C> none() {
        return context -> {
        };
    }
}
