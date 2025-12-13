# 📦 booking-service-orchestrator

> Stateful SAGA orchestrator coordinating the booking workflow across multiple services.

This service manages the **distributed booking transaction** by explicitly
orchestrating interactions between Booking, Product, and Payment services.

The orchestration state is persisted using **Event Sourcing**, making the SAGA
fully recoverable and auditable.

---

## 🧠 Orchestration Model

- One orchestrator instance per `bookingId`
- Aggregate Root: `OrchestratorAggregate`
- State machine persisted as immutable events

### SAGA States
```
CREATED → WAITING_STOCK → WAITING_PAYMENT → COMPLETED
                ↓                ↓
                ↓         COMPENSATING    → CANCELLED
           COMPENSATING → CANCELLED
```

All state transitions are validated at domain level.

---

## 🗂 Orchestrator Event Store

- Persistence model: **Event Sourcing**
- Storage: PostgreSQL
- One event stream per booking SAGA
- Versioned events with optimistic locking

This allows:
- Safe restarts without losing orchestration progress
- Deterministic recovery after crashes
- Full traceability of distributed decisions

---

## 🔀 Orchestration Flow

1. Receives `BookingCreated`
2. Requests product reservations (one command per product)
3. Waits until all products are reserved
4. Requests payment for the aggregated total
5. On success → confirms booking
6. On failure → triggers compensations and cancels booking

All decisions are driven by the **current orchestrator state**.

---

## 🔄 Compensation Strategy

Compensation is explicit and deterministic:

- If product reservation fails:
    - Release already reserved products
    - Cancel booking
- If payment fails:
    - Release all reserved products
    - Cancel booking

Compensation commands are emitted as SAGA events and executed asynchronously.

---

## 📤 Event Publishing (Outbox Pattern)

All outgoing SAGA commands are published using the **Outbox Pattern**:

- Request stock reservation
- Request payment
- Release stock
- Confirm booking
- Cancel booking

This guarantees reliable delivery even in case of partial failures.

---

## 📥 Event Consumption (Inbox Pattern)

Incoming events from other services are processed using an **Inbox / Idempotency pattern**:

- Duplicate Kafka deliveries are safely ignored
- Enables at-least-once delivery semantics without side effects

---

## 📊 Observability

- SAGA lifecycle metrics:
    - started
    - completed
    - failed
- Observability hooks around each orchestration step
- Each Kafka listener is traced as an independent orchestration phase

---

## 🚫 Explicit Non-Responsibilities

This service deliberately does **not**:
- Own business data for booking, product, or payment
- Execute domain logic of other services
- Persist read models for querying

Its sole responsibility is **coordination**.

---

## 🔗 Service Interactions

### Inbound Events
- `booking.created`
- `product.reserved`
- `product.reservation_failed`
- `payment.completed`
- `payment.failed`

### Outbound SAGA Commands
- `saga.request_stock`
- `saga.request_payment`
- `saga.release_stock`
- `saga.confirm_booking`
- `saga.cancel_booking`

