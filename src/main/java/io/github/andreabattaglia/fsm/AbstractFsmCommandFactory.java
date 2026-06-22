package io.github.andreabattaglia.fsm;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Base class for command factories backed by an enum-to-command map.
 *
 * <p>Validates command map consistency at construction time, provides
 * immutable access to registered commands, and fails fast when a command
 * is missing or mismatched.
 *
 * <p>Usage — template-method style (override {@link #registerCommands}):
 * <pre>{@code
 * class MyCommandFactory extends AbstractFsmCommandFactory<MyState, MyContext> {
 *     MyCommandFactory() { super(MyState.class); }
 *
 *     @Override
 *     protected void registerCommands(Map<MyState, FsmCommand<MyState, MyContext>> target) {
 *         target.put(MyState.PENDING,   new PendingCommand());
 *         target.put(MyState.COMPLETED, new NoOpFsmCommand<>(MyState.COMPLETED));
 *     }
 * }
 * }</pre>
 *
 * @param <E> enum type for the state catalog
 * @param <C> context type for this state machine
 */
public abstract class AbstractFsmCommandFactory<E extends Enum<E>, C extends FsmContext>
    implements FsmCommandFactory<E, C>
{
    private final Map<E, FsmCommand<E, C>> commands;

    /**
     * Template-method constructor.
     *
     * <p>Calls {@link #registerCommands(Map)} to let the subclass populate the
     * registry, then validates that every enum constant has exactly one command
     * with a matching {@link FsmCommand#forStateCode()}.
     *
     * @param enumClass enum class used to initialise the internal {@link EnumMap}
     * @throws IllegalStateException if any enum constant is missing a command or
     *                               if a command's state code does not match its key
     */
    protected AbstractFsmCommandFactory(Class<E> enumClass)
    {
        Objects.requireNonNull(enumClass, "enumClass");
        EnumMap<E, FsmCommand<E, C>> mutable = new EnumMap<>(enumClass);
        registerCommands(mutable);
        validate(enumClass, mutable);
        this.commands = Collections.unmodifiableMap(mutable);
    }

    /**
     * Map-based constructor.
     *
     * <p>Accepts an externally assembled map of commands (e.g. built by a test
     * or a DI container), copies it into an {@link EnumMap}, runs the same
     * validation, and wraps the result in an unmodifiable view.
     *
     * @param enumClass     enum class used to initialise the internal {@link EnumMap}
     * @param registrations externally provided mapping; must cover every constant
     * @throws IllegalStateException if any enum constant is missing or mismatched
     */
    protected AbstractFsmCommandFactory(Class<E> enumClass, Map<E, FsmCommand<E, C>> registrations)
    {
        Objects.requireNonNull(enumClass, "enumClass");
        Objects.requireNonNull(registrations, "registrations");
        EnumMap<E, FsmCommand<E, C>> mutable = new EnumMap<>(enumClass);
        mutable.putAll(registrations);
        validate(enumClass, mutable);
        this.commands = Collections.unmodifiableMap(mutable);
    }

    private static <E extends Enum<E>, C extends FsmContext> void validate(
        Class<E> enumClass,
        Map<E, FsmCommand<E, C>> mutable)
    {
        for (E state : enumClass.getEnumConstants())
        {
            FsmCommand<E, C> command = mutable.get(state);
            if (command == null)
            {
                throw new IllegalStateException(
                    "fsm command not registered for state=" + state);
            }
            if (!state.equals(command.forStateCode()))
            {
                throw new IllegalStateException(
                    "fsm command/state mismatch for state=" + state
                        + ", command.forStateCode()=" + command.forStateCode());
            }
        }
    }

    /**
     * Registers all state-to-command bindings.
     *
     * <p>Override when using the template-method constructor. Left empty
     * when the map-based constructor is used instead.
     *
     * @param target mutable map to populate
     */
    protected void registerCommands(Map<E, FsmCommand<E, C>> target)
    {
    }

    @Override
    public FsmCommand<E, C> commandFor(E stateCode)
    {
        Objects.requireNonNull(stateCode, "stateCode");
        FsmCommand<E, C> command = commands.get(stateCode);
        if (command == null)
        {
            throw new IllegalStateException(
                "fsm command not registered for state=" + stateCode);
        }
        return command;
    }
}
