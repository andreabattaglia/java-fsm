package io.github.andreabattaglia.fsm;

import java.util.Objects;

public final class StateMachine<S, E, C> {

    private final StateMachineDefinition<S, E, C> definition;

    StateMachine(StateMachineDefinition<S, E, C> definition) {
        this.definition = Objects.requireNonNull(definition, "definition must not be null");
    }

    public static <S, E, C> StateMachineBuilder<S, E, C> builder() {
        return new StateMachineBuilder<>();
    }

    public S initialState() {
        return definition.initialState();
    }

    public StateMachineDefinition<S, E, C> definition() {
        return definition;
    }

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
