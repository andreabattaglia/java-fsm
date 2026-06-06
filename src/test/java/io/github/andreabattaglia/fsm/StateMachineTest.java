package io.github.andreabattaglia.fsm;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StateMachineTest {

    @Test
    void firesSimpleTransition() {
        var machine = orderMachineBuilder()
                .transition(OrderState.NEW, OrderEvent.SUBMIT, OrderState.SUBMITTED)
                .build();

        var result = machine.fire(OrderState.NEW, OrderEvent.SUBMIT, new OrderContext(100, new ArrayList<>()));

        assertEquals(OrderState.NEW, result.from());
        assertEquals(OrderEvent.SUBMIT, result.event());
        assertEquals(OrderState.SUBMITTED, result.to());
    }

    @Test
    void executesGuardAndAction() {
        var audit = new ArrayList<String>();
        var machine = orderMachineBuilder()
                .transition(
                        OrderState.SUBMITTED,
                        OrderEvent.APPROVE,
                        OrderState.APPROVED,
                        context -> context.payload().amount() <= 1_000,
                        context -> context.payload().audit().add("approved")
                )
                .build();

        var result = machine.fire(OrderState.SUBMITTED, OrderEvent.APPROVE, new OrderContext(250, audit));

        assertEquals(OrderState.APPROVED, result.to());
        assertEquals(List.of("approved"), audit);
    }

    @Test
    void rejectsTransitionWhenGuardFails() {
        var machine = orderMachineBuilder()
                .transition(
                        OrderState.SUBMITTED,
                        OrderEvent.APPROVE,
                        OrderState.APPROVED,
                        context -> context.payload().amount() <= 1_000,
                        Action.none()
                )
                .build();

        assertThrows(
                GuardRejectedException.class,
                () -> machine.fire(OrderState.SUBMITTED, OrderEvent.APPROVE, new OrderContext(2_000, new ArrayList<>()))
        );
    }

    @Test
    void failsOnMissingTransition() {
        var machine = orderMachineBuilder()
                .transition(OrderState.NEW, OrderEvent.SUBMIT, OrderState.SUBMITTED)
                .build();

        assertThrows(
                TransitionNotFoundException.class,
                () -> machine.fire(OrderState.NEW, OrderEvent.APPROVE, new OrderContext(100, new ArrayList<>()))
        );
    }

    @Test
    void failsOnDuplicateTransition() {
        var builder = orderMachineBuilder()
                .transition(OrderState.NEW, OrderEvent.SUBMIT, OrderState.SUBMITTED);

        assertThrows(
                DuplicateTransitionException.class,
                () -> builder.transition(OrderState.NEW, OrderEvent.SUBMIT, OrderState.REJECTED)
        );
    }

    @Test
    void exposesDefinitionMetadata() {
        var machine = orderMachineBuilder()
                .transition(OrderState.NEW, OrderEvent.SUBMIT, OrderState.SUBMITTED)
                .transition(OrderState.SUBMITTED, OrderEvent.REJECT, OrderState.REJECTED)
                .build();

        assertEquals(OrderState.NEW, machine.initialState());
        assertTrue(machine.definition().states().containsAll(List.of(
                OrderState.NEW,
                OrderState.SUBMITTED,
                OrderState.REJECTED
        )));
        assertEquals(2, machine.definition().transitions().size());
    }

    private static StateMachineBuilder<OrderState, OrderEvent, OrderContext> orderMachineBuilder() {
        return StateMachine.<OrderState, OrderEvent, OrderContext>builder()
                .initialState(OrderState.NEW);
    }

    enum OrderState {
        NEW,
        SUBMITTED,
        APPROVED,
        REJECTED
    }

    enum OrderEvent {
        SUBMIT,
        APPROVE,
        REJECT
    }

    record OrderContext(int amount, List<String> audit) {
    }
}
