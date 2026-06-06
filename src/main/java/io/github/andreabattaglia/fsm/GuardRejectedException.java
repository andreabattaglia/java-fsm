package io.github.andreabattaglia.fsm;

public class GuardRejectedException extends StateMachineException {

    public GuardRejectedException(Object state, Object event) {
        super("Transition guard rejected event '%s' from state '%s'".formatted(event, state));
    }
}
