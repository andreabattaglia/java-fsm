package io.github.andreabattaglia.fsm;

import java.util.Map;

import io.github.andreabattaglia.fsm.command.NewOrderCommand;
import io.github.andreabattaglia.fsm.command.ProcessingOrderCommand;

class OrderCommandFactory extends AbstractFsmCommandFactory<OrderState, OrderContext>
{
    OrderCommandFactory()
    {
        super(OrderState.class);
    }

    @Override
    protected void registerCommands(Map<OrderState, FsmCommand<OrderState, OrderContext>> target)
    {
        target.put(OrderState.NEW,        new NewOrderCommand());
        target.put(OrderState.PROCESSING, new ProcessingOrderCommand());
        target.put(OrderState.COMPLETED,  new NoOpFsmCommand<>(OrderState.COMPLETED));
        target.put(OrderState.FAILED,     new NoOpFsmCommand<>(OrderState.FAILED));
    }
}
