package io.github.andreabattaglia.fsm;

public class TransitionNotFoundException extends StateMachineException {

    public TransitionNotFoundException(Object state, Object event) {
        super("No transition found from state '%s' on event '%s'".formatted(state, event));
    }
}
