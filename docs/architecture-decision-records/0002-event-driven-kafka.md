# ADR 0002: Initial Event-Driven Architecture with Kafka

## Context:
Synchronous REST calls between microservices created tight coupling, reduced scalability, and increased fragility. A more decoupled, reactive architecture was desired.

## Decision:
Adopt Event-Driven Architecture (EDA) with Kafka as the messaging backbone, using Confluent Schema Registry to enforce schema validation for messages.

## Rationale:
- **Decoupling**: Services communicate asynchronously, reducing inter-service dependencies.
- **Scalability**: Kafka can handle high-volume, real-time event streams across multiple partitions and consumer groups.
- **Durability and Reliability**: Messages are persisted on disk, enabling recovery from failures without data loss.
- **Event Schema Management**: Using Schema Registry ensures compatibility between producers and consumers, avoids breaking changes, and enforces data consistency.
- **Elasticity**: Kafka brokers can scale horizontally to manage higher load without service disruption.
- **Ecosystem Integration**: Kafka integrates with stream processing frameworks, databases, and monitoring tools, enabling advanced analytics and CQRS patterns in the future.
- **Auditability**: All events are logged, providing a complete history of domain changes for debugging, auditing, and potential Event Sourcing migration.

## Consequences:
- Must handle eventual consistency, which requires careful design for state synchronization.
- Adds operational complexity: cluster management, schema evolution, and monitoring.
- Requires team familiarity with Kafka concepts: topics, partitions, consumer groups, offsets, and Schema Registry management.
- Provides a robust foundation for future adoption of Event Sourcing + CQRS, allowing projections and replayable events.