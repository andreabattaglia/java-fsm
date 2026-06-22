package io.github.andreabattaglia.fsm;

class OrderOrchestrator extends FsmOrchestratorService<OrderState, OrderContext>
{
    OrderOrchestrator()
    {
        super(new OrderCommandFactory(), new OrderTransitionPolicy(), new OrderExceptionHandler());
    }
}
