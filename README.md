# Java FSM Core

Dependency-free finite state machine core for Java 21, based on the **command-per-state** pattern.

## Model

- Each state has one **command** (`FsmCommand`) that executes business logic and returns an **outcome** (`FsmCommandOutcome`).
- A **transition policy** (`FsmTransitionPolicy`) maps `(currentState, outcome)` → `nextState`. It can also inspect the context.
- The **orchestrator** (`FsmOrchestratorService`) runs the loop automatically: execute command → get outcome → resolve next state → repeat until terminal.
- Errors thrown by commands are caught and mapped to an outcome by a dedicated **exception handler** (`FsmExceptionHandler`).

This model is better suited to sequential workflows than to purely event-driven machines.

## Maven

```xml
<dependency>
    <groupId>io.github.andreabattaglia</groupId>
    <artifactId>fsm-core</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

## Usage

```java
// 1. States
enum OrderState { NEW, PROCESSING, COMPLETED, FAILED }

// 2. Context
class OrderContext implements FsmContext {
    int amount;
    boolean approved;
}

// 3. Commands — one per state
class NewOrderCommand implements FsmCommand<OrderState, OrderContext> {
    @Override
    public OrderState forStateCode() { return OrderState.NEW; }

    @Override
    public FsmCommandOutcome execute(OrderContext ctx) {
        // validate order
        return FsmCommandOutcome.SUCCESS;
    }
}

// 4. Factory — registers one command per state
class OrderCommandFactory extends AbstractFsmCommandFactory<OrderState, OrderContext> {
    OrderCommandFactory() { super(OrderState.class); }

    @Override
    protected void registerCommands(Map<OrderState, FsmCommand<OrderState, OrderContext>> target) {
        target.put(OrderState.NEW,        new NewOrderCommand());
        target.put(OrderState.PROCESSING, new ProcessingOrderCommand());
        target.put(OrderState.COMPLETED,  new NoOpFsmCommand<>(OrderState.COMPLETED));
        target.put(OrderState.FAILED,     new NoOpFsmCommand<>(OrderState.FAILED));
    }
}

// 5. Transition policy — defines graph and routing
class OrderTransitionPolicy extends FsmTransitionPolicy<OrderState, OrderContext> {
    @Override public Set<OrderState> states()      { return EnumSet.allOf(OrderState.class); }
    @Override public OrderState initialState()     { return OrderState.NEW; }
    @Override public boolean isTerminal(OrderState s) {
        return s == OrderState.COMPLETED || s == OrderState.FAILED;
    }

    @Override
    public OrderState next(OrderState current, FsmCommandOutcome outcome, OrderContext ctx) {
        if (current == OrderState.NEW) return OrderState.PROCESSING;
        if (current == OrderState.PROCESSING) {
            return outcome == FsmCommandOutcome.SUCCESS ? OrderState.COMPLETED : OrderState.FAILED;
        }
        throw new IllegalStateException("no transition from " + current);
    }
}

// 6. Orchestrator — concrete subclass names the service
class OrderOrchestrator extends FsmOrchestratorService<OrderState, OrderContext> {
    OrderOrchestrator() {
        super(new OrderCommandFactory(), new OrderTransitionPolicy(),
              new AbstractFsmExceptionHandler<>() {});
    }
}

// 7. Execution
OrderContext ctx = new OrderContext();
FsmExecutionResult<OrderState> result = new OrderOrchestrator().execute(ctx);
OrderState finalState = result.getFinalState(); // COMPLETED or FAILED
int steps = result.getSteps();
```

## Concepts

| Type | Role |
|---|---|
| `FsmContext` | Marker interface for the execution context |
| `FsmCommandOutcome` | Enum: `SUCCESS`, `RETRYABLE_ERROR`, `FATAL_ERROR`, `SKIP` |
| `FsmCommand<E,C>` | Unit of work bound to one state |
| `FsmCommandFactory<E,C>` | Resolves commands by state code |
| `AbstractFsmCommandFactory<E,C>` | Base factory with `EnumMap` validation at construction |
| `FsmTransitionPolicy<E,C>` | Defines graph: `states()`, `initialState()`, `isTerminal()`, `next()` |
| `FsmExceptionHandler<E,C>` | Maps `FsmCommandException` to `FsmCommandOutcome` |
| `AbstractFsmExceptionHandler<E,C>` | Base handler with logging and configurable default outcome |
| `FsmExecutionResult<E>` | Immutable result: `getFinalState()`, `getSteps()` |
| `FsmOrchestratorService<E,C>` | Abstract engine that runs the loop |
| `NoOpFsmCommand<E,C>` | Placeholder for terminal states |
| `FsmCommandException` | Checked exception thrown by commands |

## Build

```bash
mvn clean verify
```
