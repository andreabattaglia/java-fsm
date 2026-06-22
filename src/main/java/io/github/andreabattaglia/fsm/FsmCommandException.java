package io.github.andreabattaglia.fsm;

/**
 * Domain exception thrown by FSM commands.
 *
 * <p>A command throws this exception when it cannot complete its work.
 * The orchestrator catches it and delegates the decision to the
 * {@link FsmExceptionHandler}, which maps the error to a
 * {@link FsmCommandOutcome}.
 */
public class FsmCommandException extends Exception
{
    public FsmCommandException()
    {
        super();
    }

    public FsmCommandException(String message)
    {
        super(message);
    }

    public FsmCommandException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public FsmCommandException(Throwable cause)
    {
        super(cause);
    }
}
