# ADR 0008: Implement Inbox Pattern for Idempotent Event Consumption

## Context:

The system relies on Kafka with at-least-once delivery semantics.
Consumers may receive duplicate events due to retries, consumer rebalancing, broker failures, or application restarts.

Without explicit idempotency handling, duplicate event consumption can lead to:

- Multiple executions of the same business logic
- Corrupted aggregates in Event Sourcing
- Invalid SAGA state transitions
- Repeated side effects (e.g. multiple reservations or compensations)

As the platform evolves toward Event Sourcing, CQRS, and SAGA orchestration, idempotent consumption becomes mandatory.

## Decision:

Implement the Inbox Pattern for all Kafka event consumers.

- Each consumed event is persisted in an inbox table before domain processing.
- Events are uniquely identified by a deterministic eventId.
- If an event already exists in the inbox, it is ignored.
- Inbox persistence and domain state mutation occur within the same database transaction.
- Inbox checks are enforced at the application boundary, before invoking domain logic.

## Rationale:

- **Idempotency by design:** Ensures safe reprocessing under Kafka retries.
- **Failure recovery becomes deterministic:** consumers can restart without risking duplicated business effects.
- **Event Sourcing safety** prevents aggregate version corruption caused by duplicated events.
- **SAGA robustness** ensures compensating actions are not executed more than once.
- **Operational simplicity** removes the need for ad-hoc deduplication logic in each handler.

## Consequences:

### Positive

- Guarantees safe message consumption under failure scenarios.
- Simplifies error handling and retry logic.
- Improves confidence in asynchronous workflows.

### Negative

- Requires additional persistence and indexing.
- Slight performance overhead on each consumed event.
- Developers must follow strict conventions when consuming events.