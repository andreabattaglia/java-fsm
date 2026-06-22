package io.github.andreabattaglia.fsm.command;

import io.github.andreabattaglia.fsm.FsmCommand;
import io.github.andreabattaglia.fsm.FsmCommandOutcome;
import io.github.andreabattaglia.fsm.OrderContext;
import io.github.andreabattaglia.fsm.OrderState;

public class SkipOrderCommand implements FsmCommand<OrderState, OrderContext>
{
    @Override
    public OrderState forStateCode()
    {
        return OrderState.NEW;
    }

    @Override
    public FsmCommandOutcome execute(OrderContext ctx)
    {
        return FsmCommandOutcome.SKIP;
    }
}
