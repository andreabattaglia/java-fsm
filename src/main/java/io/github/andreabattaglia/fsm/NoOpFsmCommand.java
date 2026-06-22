package io.github.andreabattaglia.fsm;

/**
 * No-op {@link FsmCommand} for terminal states.
 *
 * <p>The {@link FsmOrchestratorService} validates that every declared state —
 * including terminal ones — has a registered command. Terminal states are
 * never executed by the engine loop, but the registry must still be complete
 * to pass validation. Use this class as a placeholder when registering
 * terminal states in an {@link AbstractFsmCommandFactory}.
 *
 * <p>Example:
 * <pre>{@code
 * target.put(MyState.COMPLETED, new NoOpFsmCommand<>(MyState.COMPLETED));
 * target.put(MyState.FAILED,    new NoOpFsmCommand<>(MyState.FAILED));
 * }</pre>
 *
 * @param <E> enum type for the state catalog
 * @param <C> context type for this state machine
 */
public final class NoOpFsmCommand<E extends Enum<E>, C extends FsmContext>
    implements FsmCommand<E, C>
{
    private final E stateCode;

    public NoOpFsmCommand(E stateCode)
    {
        this.stateCode = stateCode;
    }

    @Override
    public E forStateCode()
    {
        return stateCode;
    }

    @Override
    public FsmCommandOutcome execute(C context) throws FsmCommandException
    {
        return FsmCommandOutcome.SUCCESS;
    }
}
