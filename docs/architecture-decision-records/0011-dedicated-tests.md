# ADR 0011: Dedicated Testing Module

## Context:

Embedding E2E tests inside individual services leads to:

- Polluted production codebases.
- Tight coupling between test logic and service internals.
- Difficult lifecycle management of multiple services.

## Decision:

Create a **dedicated test-suite** module.

- Runs all services as Docker containers.
- Uses Testcontainers for Kafka, databases, and infrastructure.
- Validates full SAGA flows, persistence, and Kafka events.
- No mocks, no embedded brokers.

## Rationale:

- **Realism:** Tests reflect production behavior.
- **Isolation:** Clear separation between test and production code.
- **Confidence:** Validates full distributed workflows.
- **Reusability:** Can be reused in CI/CD pipelines.

## Consequences:
### Positive

- High confidence in system correctness.
- Catches integration issues early.
- Clean separation of concerns.

### Negative

- Slower execution time.
- Requires Docker and more resources.
- Higher maintenance cost.