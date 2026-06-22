package io.github.andreabattaglia.fsm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.github.andreabattaglia.fsm.command.NewOrderCommand;
import io.github.andreabattaglia.fsm.command.ProcessingOrderCommand;
import io.github.andreabattaglia.fsm.command.SkipOrderCommand;
import io.github.andreabattaglia.fsm.command.WrongStateCodeCommand;

class StateMachineTest {
	@Test
	void happyPath_runsFromInitialToCompleted() {
		OrderContext ctx = new OrderContext(100);

		FsmExecutionResult<OrderState> result = new OrderOrchestrator().execute(ctx);

		assertEquals(OrderState.COMPLETED, result.getFinalState());
		assertEquals(2, result.getSteps());
		assertEquals(List.of("new", "processed"), ctx.getAuditLog());
	}

	@Test
	void errorPath_commandException_routesToFailed() {
		OrderContext ctx = new OrderContext(100);
		ctx.setShouldFail(true);

		FsmExecutionResult<OrderState> result = new OrderOrchestrator().execute(ctx);

		assertEquals(OrderState.FAILED, result.getFinalState());
		assertEquals(List.of("new"), ctx.getAuditLog());
	}

	@Test
	void episodicRun_startsFromGivenState_skipsEarlierStates() {
		OrderContext ctx = new OrderContext(100);

		FsmExecutionResult<OrderState> result = new OrderOrchestrator().execute(ctx, OrderState.PROCESSING);

		assertEquals(OrderState.COMPLETED, result.getFinalState());
		assertEquals(1, result.getSteps());
		assertEquals(List.of("processed"), ctx.getAuditLog());
	}

	@Test
	void factoryConstruction_failsWhenCommandNotRegisteredForState() {
		assertThrows(IllegalStateException.class,
				() -> new AbstractFsmCommandFactory<OrderState, OrderContext>(OrderState.class) {
					@Override
					protected void registerCommands(Map<OrderState, FsmCommand<OrderState, OrderContext>> target) {
						target.put(OrderState.NEW, new NewOrderCommand());
					}
				});
	}

	@Test
	void factoryConstruction_failsOnCommandStateCodeMismatch() {
		assertThrows(IllegalStateException.class, () -> new AbstractFsmCommandFactory<>(OrderState.class,
				Map.of(OrderState.NEW, new WrongStateCodeCommand(), OrderState.PROCESSING, new ProcessingOrderCommand(),
						OrderState.COMPLETED, new NoOpFsmCommand<>(OrderState.COMPLETED), OrderState.FAILED,
						new NoOpFsmCommand<>(OrderState.FAILED))) {
		});
	}

	@Test
	void skipOutcome_transitionPolicyRoutesDirectlyToTerminal() {
		FsmCommandFactory<OrderState, OrderContext> factory = new AbstractFsmCommandFactory<>(OrderState.class,
				Map.of(OrderState.NEW, new SkipOrderCommand(), OrderState.PROCESSING, new ProcessingOrderCommand(),
						OrderState.COMPLETED, new NoOpFsmCommand<>(OrderState.COMPLETED), OrderState.FAILED,
						new NoOpFsmCommand<>(OrderState.FAILED))) {
		};

		FsmOrchestratorService<OrderState, OrderContext> orchestrator = new FsmOrchestratorService<>(factory,
				new SkipOrderTransitionPolicy(), new OrderExceptionHandler()) {
		};

		FsmExecutionResult<OrderState> result = orchestrator.execute(new OrderContext(0));

		assertEquals(OrderState.COMPLETED, result.getFinalState());
		assertEquals(1, result.getSteps());
	}
}
