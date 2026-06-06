package io.github.andreabattaglia.fsm;

public class DuplicateTransitionException extends StateMachineException {

    public DuplicateTransitionException(Object state, Object event) {
        super("Duplicate transition from state '%s' on event '%s'".formatted(state, event));
    }
}
