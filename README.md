# Java FSM Core

Dependency-free finite state machine core for Java 21.

## Goals

- Java 21 standard library only at runtime
- No Spring, Quarkus, CDI, reflection, annotations, or framework lifecycle
- Strongly typed states, events, payloads, guards, and actions
- Small API suitable for reuse in application, batch, or library code

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
enum OrderState { NEW, SUBMITTED, APPROVED }
enum OrderEvent { SUBMIT, APPROVE }
record OrderContext(int amount) {}

var machine = StateMachine.<OrderState, OrderEvent, OrderContext>builder()
        .initialState(OrderState.NEW)
        .transition(OrderState.NEW, OrderEvent.SUBMIT, OrderState.SUBMITTED)
        .transition(
                OrderState.SUBMITTED,
                OrderEvent.APPROVE,
                OrderState.APPROVED,
                context -> context.payload().amount() <= 1_000,
                context -> System.out.println("approved")
        )
        .build();

var result = machine.fire(OrderState.NEW, OrderEvent.SUBMIT, new OrderContext(100));
OrderState nextState = result.to();
```

## Concepts

- `StateMachine<S, E, C>` executes transitions from a current state and event.
- `S` is the state type, usually an enum owned by the caller.
- `E` is the event type, usually an enum owned by the caller.
- `C` is the payload/context type passed to guards and actions.
- `Guard` decides whether a transition is allowed.
- `Action` executes side effects after the guard permits the transition.
- `TransitionResult` returns the source state, event, target state, and payload.

## Build

```bash
mvn clean verify
```
