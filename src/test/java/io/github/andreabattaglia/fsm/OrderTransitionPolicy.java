package io.github.andreabattaglia.fsm;

import java.util.EnumSet;
import java.util.Set;

class OrderTransitionPolicy extends FsmTransitionPolicy<OrderState, OrderContext>
{
    @Override
    public Set<OrderState> states()
    {
        return EnumSet.allOf(OrderState.class);
    }

    @Override
    public OrderState initialState()
    {
        return OrderState.NEW;
    }

    @Override
    public boolean isTerminal(OrderState state)
    {
        return state == OrderState.COMPLETED || state == OrderState.FAILED;
    }

    @Override
    public OrderState next(OrderState current, FsmCommandOutcome outcome, OrderContext ctx)
    {
        if (current == OrderState.NEW)
        {
            return OrderState.PROCESSING;
        }
        if (current == OrderState.PROCESSING)
        {
            return outcome == FsmCommandOutcome.SUCCESS ? OrderState.COMPLETED : OrderState.FAILED;
        }
        throw new IllegalStateException("no transition from " + current);
    }
}
