package io.github.andreabattaglia.fsm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Base implementation for FSM exception handlers.
 *
 * <p>Centralizes two recurring behaviors: a default outcome mapping
 * (default: {@link FsmCommandOutcome#FATAL_ERROR}) and consistent exception
 * logging. Subclasses can keep the default or override
 * {@link #mapException(FsmContext, Enum, FsmCommandException)} to implement
 * custom mapping rules.
 *
 * @param <E> enum type for the state catalog
 * @param <C> context type for this state machine
 */
public abstract class AbstractFsmExceptionHandler<E extends Enum<E>, C extends FsmContext>
    implements FsmExceptionHandler<E, C>
{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final FsmCommandOutcome defaultOutcome;

    /** Builds a handler with {@link FsmCommandOutcome#FATAL_ERROR} as fallback. */
    protected AbstractFsmExceptionHandler()
    {
        this(FsmCommandOutcome.FATAL_ERROR);
    }

    /**
     * Builds a handler with an explicit fallback outcome.
     *
     * @param defaultOutcome fallback outcome when no custom mapping is applied
     */
    protected AbstractFsmExceptionHandler(FsmCommandOutcome defaultOutcome)
    {
        this.defaultOutcome = Objects.requireNonNull(defaultOutcome, "defaultOutcome");
    }

    @Override
    public final FsmCommandOutcome onException(
        C context,
        E currentState,
        FsmCommandException exception)
    {
        logException(context, currentState, exception);

        FsmCommandOutcome outcome = mapException(context, currentState, exception);
        if (outcome == null)
        {
            throw new IllegalStateException(
                "fsm exception handler returned null outcome for state=" + currentState);
        }
        return outcome;
    }

    /**
     * Maps an exception to an FSM outcome.
     *
     * <p>Default behavior returns the configured fallback outcome.
     * Subclasses may override to implement state-aware or exception-aware mapping.
     *
     * @param context      execution context
     * @param currentState state where the exception happened
     * @param exception    thrown command exception
     * @return non-null mapped outcome
     */
    protected FsmCommandOutcome mapException(
        C context,
        E currentState,
        FsmCommandException exception)
    {
        return defaultOutcome;
    }

    private void logException(C context, E currentState, FsmCommandException exception)
    {
        if (exception == null)
        {
            log.error(
                "FSM exception handler invoked with NULL exception | handler={} state={} context={} dump={}",
                getClass().getName(),
                currentState,
                context,
                buildContextDump(context));
            return;
        }

        Throwable rootCause = exception.getCause() != null ? exception.getCause() : exception;
        log.error(
            "FSM command exception | handler={} state={} mappedOutcome={} exception={} msg={} rootCause={} rootMsg={} context={} dump={}",
            getClass().getName(),
            currentState,
            defaultOutcome,
            exception.getClass().getName(),
            exception.getMessage(),
            rootCause.getClass().getName(),
            rootCause.getMessage(),
            context,
            buildContextDump(context),
            exception);
    }

    private String buildContextDump(C context)
    {
        if (context == null)
        {
            return "null";
        }
        StringJoiner joiner = new StringJoiner(", ", "{", "}");
        Class<?> type = context.getClass();
        while (type != null && type != Object.class)
        {
            for (Field field : type.getDeclaredFields())
            {
                field.setAccessible(true);
                Object value;
                try
                {
                    value = field.get(context);
                }
                catch (IllegalAccessException e)
                {
                    value = "<unavailable>";
                }
                joiner.add(field.getName() + "=" + value);
            }
            type = type.getSuperclass();
        }
        return joiner.toString();
    }
}
