package io.github.andreabattaglia.fsm;

/**
 * Base marker for command execution context.
 *
 * <p>The context is the data object passed to every command and to the
 * transition policy during one FSM execution. Concrete jobs expose their
 * own strongly typed context classes that implement this interface.
 */
public interface FsmContext
{
}
