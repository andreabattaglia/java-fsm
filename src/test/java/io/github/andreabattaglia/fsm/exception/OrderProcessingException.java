package io.github.andreabattaglia.fsm.exception;

import io.github.andreabattaglia.fsm.FsmCommandException;

public class OrderProcessingException extends FsmCommandException
{
    private final boolean retryable;

    public OrderProcessingException(String message, boolean retryable)
    {
        super(message);
        this.retryable = retryable;
    }

    public OrderProcessingException(String message, Throwable cause, boolean retryable)
    {
        super(message, cause);
        this.retryable = retryable;
    }

    public boolean isRetryable()
    {
        return retryable;
    }
}
