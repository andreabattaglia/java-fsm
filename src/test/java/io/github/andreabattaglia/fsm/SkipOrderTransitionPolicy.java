package io.github.andreabattaglia.fsm;

import java.util.EnumSet;
import java.util.Set;

class SkipOrderTransitionPolicy extends FsmTransitionPolicy<OrderState, OrderContext>
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
            return outcome == FsmCommandOutcome.SKIP ? OrderState.COMPLETED : OrderState.PROCESSING;
        }
        return outcome == FsmCommandOutcome.SUCCESS ? OrderState.COMPLETED : OrderState.FAILED;
    }
}
