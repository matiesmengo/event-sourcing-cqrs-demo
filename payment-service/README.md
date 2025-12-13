# 📦 payment-service

> Payment domain service responsible for executing and tracking booking payments
> using Event Sourcing and SAGA-driven coordination.

This service owns the **Payment aggregate** and encapsulates all payment-related
side effects, including interaction with external payment providers.

---

## 🧠 Domain Model

- Aggregate Root: `PaymentAggregate`
- One payment per booking
- Payment lifecycle is modeled explicitly as domain events

### Payment States
```
INITIATED → COMPLETED | FAILED
```


State transitions are validated at aggregate level to prevent invalid execution flows.

---

## 🗂 Payment Event Store

- Persistence model: **Event Sourcing**
- Storage: PostgreSQL
- One event stream per `paymentId`
- Versioned events with optimistic locking

Aggregate state is reconstructed by replaying all payment events in order.

This guarantees:
- Full audit trail of payment attempts
- Deterministic recovery after failures
- Clear separation between intent and result

---

## 🔄 Payment Execution Flow

1. Receives `RequestPayment` from the SAGA orchestrator
2. Creates a new `PaymentInitiated` event
3. Executes the payment via `PaymentProcessor`
4. Emits either:
    - `PaymentCompleted`
    - `PaymentFailed`
5. Publishes the result back to the orchestrator

Payment execution is **side-effectful** and explicitly isolated from orchestration logic.

---

## 🔌 External Integration (Payment Processor)

The actual payment execution is delegated to a `PaymentProcessor` port.

- Allows multiple implementations (real gateway, sandbox, fake)
- Keeps domain logic isolated from infrastructure concerns
- Enables deterministic testing of success and failure scenarios

A fake processor is provided to simulate:
- Successful payments
- Failed payments
- Random outcomes

---

## 📤 Event Publishing (Outbox Pattern)

All outgoing payment events are published using the **Outbox Pattern**:

- `payment.initiated`
- `payment.completed`
- `payment.failed`

This ensures reliable delivery even if the payment service crashes
after persisting domain state.

---

## 📥 Event Consumption (Inbox Pattern)

Incoming Kafka events are processed using an **Inbox / Idempotency pattern**:

- Duplicate deliveries are ignored
- At-least-once delivery semantics are safely supported

This is critical for handling retries in distributed payment workflows.

---

## 🔀 CQRS & Responsibilities

This service represents the **command side** of the Payment domain.

### It DOES:
- Execute payment attempts
- Persist payment state transitions
- Emit payment outcome events

### It DOES NOT:
- Coordinate booking workflows
- Manage product reservations
- Expose query-optimized read models

---

## 🔗 Service Interactions

### Inbound Events
- `saga.request_payment`
- `payment.initiated`

### Outbound Events
- `payment.completed`
- `payment.failed`

All interactions are asynchronous and event-driven.
