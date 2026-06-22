package io.github.andreabattaglia.fsm.command;

import io.github.andreabattaglia.fsm.FsmCommand;
import io.github.andreabattaglia.fsm.FsmCommandException;
import io.github.andreabattaglia.fsm.FsmCommandOutcome;
import io.github.andreabattaglia.fsm.OrderContext;
import io.github.andreabattaglia.fsm.OrderState;
import io.github.andreabattaglia.fsm.exception.OrderProcessingException;

public class ProcessingOrderCommand implements FsmCommand<OrderState, OrderContext>
{
    @Override
    public OrderState forStateCode()
    {
        return OrderState.PROCESSING;
    }

    @Override
    public FsmCommandOutcome execute(OrderContext ctx) throws FsmCommandException
    {
        if (ctx.isShouldFail())
        {
            throw new OrderProcessingException("processing failed", false);
        }
        ctx.getAuditLog().add("processed");
        return FsmCommandOutcome.SUCCESS;
    }
}
