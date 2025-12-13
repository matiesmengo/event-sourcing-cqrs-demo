# product-service

> Product domain service responsible for managing stock availability,
> reservations, and releases using Event Sourcing and SAGA-driven orchestration.

This service is the **single source of truth for product stock**.  
It persists all stock changes as **immutable events** and integrates with the booking SAGA.

---

## 🧠 Domain Model

- Aggregate Root: `ProductAggregate`
- Tracks:
    - `stockTotal` (total stock)
    - `reserved` (units currently reserved)
    - `availableStock` (computed = total - reserved)
- State transitions:
    - `ProductReservedEvent`
    - `ProductReleasedEvent`

State transitions are validated to prevent overbooking.

---

## 🗂 Event Store

- Persistence model: **Event Sourcing**
- Storage: PostgreSQL
- One event stream per `productId`
- Versioned events for optimistic concurrency

Allows:
- Deterministic recovery after failures
- Full audit trail of stock movements
- Historical insights into product usage

---

## 🔄 Stock Flow

### Reserve Product
1. Receives `ReserveProduct` command from orchestrator
2. Checks available stock
3. If enough stock:
    - Creates `ProductReservedEvent`
    - Publishes `product.reserved` event
4. If not enough stock:
    - Publishes `product.reservation_failed` event

### Release Product
1. Receives `ReleaseProduct` command from orchestrator
2. Creates `ProductReleasedEvent`
3. Updates aggregate state
4. No event failure is expected

---

## 📤 Event Publishing (Outbox Pattern)

- `product.reserved`
- `product.reservation_failed`

Ensures **reliable delivery** and decouples domain persistence from messaging.

---

## 📥 Event Consumption (Inbox Pattern)

- `saga.request_stock`
- `saga.release_stock`

- Validates idempotency
- Handles at-least-once delivery semantics
- Prevents double reservation or accidental release

---

## 🔀 CQRS & Responsibilities

This service represents the **command side** of Product domain.

### It DOES:
- Validate stock availability
- Persist reservations and releases
- Publish product events

### It DOES NOT:
- Coordinate booking workflows
- Handle payments
- Expose read models (Mongo projection TODO)

---

## 🔗 Service Interactions

### Inbound Events
- `saga.request_stock`
- `saga.release_stock`

### Outbound Events
- `product.reserved`
- `product.reservation_failed`

All interactions are asynchronous and event-driven.

---
