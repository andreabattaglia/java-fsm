package io.github.andreabattaglia.fsm;

import io.github.andreabattaglia.fsm.exception.OrderProcessingException;

class OrderExceptionHandler extends AbstractFsmExceptionHandler<OrderState, OrderContext>
{
    @Override
    protected FsmCommandOutcome mapException(
        OrderContext context,
        OrderState currentState,
        FsmCommandException exception)
    {
        if (exception instanceof OrderProcessingException e)
        {
            return e.isRetryable() ? FsmCommandOutcome.RETRYABLE_ERROR : FsmCommandOutcome.FATAL_ERROR;
        }
        return FsmCommandOutcome.FATAL_ERROR;
    }
}
