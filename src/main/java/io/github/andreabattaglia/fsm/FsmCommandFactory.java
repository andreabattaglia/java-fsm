package io.github.andreabattaglia.fsm;

/**
 * Factory responsible for resolving commands by state code.
 *
 * <p>This indirection keeps the orchestrator independent from how commands
 * are instantiated or stored (plain map, Spring beans, service locator, etc.).
 *
 * @param <E> enum type for the state catalog
 * @param <C> context type for this state machine
 */
public interface FsmCommandFactory<E extends Enum<E>, C extends FsmContext>
{
    /**
     * Resolves the command for a specific state.
     *
     * <p>Implementations must never return {@code null}.
     *
     * @param stateCode state code to resolve
     * @return command bound to the state code
     */
    FsmCommand<E, C> commandFor(E stateCode);
}
