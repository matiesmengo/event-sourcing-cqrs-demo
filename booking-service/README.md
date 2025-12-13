# 📦 booking-service

> Core domain service implementing the Booking aggregate using Event Sourcing,
> Kafka-based SAGA orchestration, and transactional messaging patterns.

This service is the **authoritative owner of the Booking domain**.
All state transitions are represented as immutable domain events and persisted
in an event store backed by PostgreSQL.

---

## 🧠  Domain Model

- Aggregate Root: `BookingAggregate`
- State is derived exclusively by **rehydrating domain events**
- Business invariants are enforced inside the aggregate

The aggregate lifecycle is strictly controlled:
```
CREATED →  CONFIRMED | FAILED
```

Invalid state transitions are rejected at domain level.

---

## 🗂 Event Store & Concurrency

- Persistence model: **Event Sourcing**
- Storage: PostgreSQL
- Schema: `booking`
- One event stream per `bookingId`
- Events are ordered and versioned

Concurrency is handled via **optimistic locking**:
- Each event carries an `aggregateVersion`
- Writes fail if the expected version does not match the current stream head

This guarantees consistency without distributed locks.

---

## 🔄 Aggregate Rehydration

Aggregate state is reconstructed by replaying events in order:

- Events are loaded sorted by version
- The first event must be `BookingCreated`
- Each subsequent event mutates the aggregate state

This ensures:
- Full auditability
- Deterministic state reconstruction
- Easy replay for debugging or rebuilding projections

---

## 📤 Event Publishing (Outbox Pattern)

Domain events are published to Kafka using the **Outbox Pattern**.

- Events are persisted and published within the same transaction
- Kafka publication happens asynchronously
- Guarantees **no lost events** even in case of broker failure

Published topics:
- `booking.created`
- `booking.completed`
- `booking.failed`

Events are serialized using **Avro** and registered in the Schema Registry.

Delivery semantics:
- At-least-once
- Downstream consumers must be idempotent

---

## 📥 Event Consumption (Inbox Pattern)

Incoming Kafka events are processed using an **Inbox / Idempotency pattern**.

- Each consumed event is validated before processing
- Duplicate deliveries are safely ignored
- Enables reliable at-least-once consumption

This is used for handling SAGA callbacks such as:
- payment confirmation
- payment failure

---

## 🔀 CQRS & Responsibilities

This service represents the **command side** of the Booking domain.

### It DOES:
- Handle booking-related commands
- Persist booking state transitions as events
- Publish domain events

### It DOES NOT:
- Execute payment logic
- Manage product inventory
- Perform cross-service orchestration
- Expose query-optimized read models

Read models and cross-service workflows are handled by dedicated services.

---

## 🔗 Service Interactions

### Inbound
- Command APIs exposed via `booking-service-api`
- Kafka events:
    - `saga.confirm_booking`
    - `saga.cancel_booking`

### Outbound
- Kafka domain events consumed by:
    - payment-service
    - product-service
    - booking-service-orchestrator

---
