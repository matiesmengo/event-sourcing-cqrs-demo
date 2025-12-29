# ADR 0012: Performance Benchmarking with k6

## Context:

Verifying the performance and resilience of a distributed SAGA architecture requires:

- Simulating high concurrent user traffic.
- Measuring system throughput (Requests per Second).
- Identifying bottlenecks in Kafka consumer lag and database persistence.
- Stress testing the system to find its breaking point.

## Decision:

Use **k6** as the primary tool for performance benchmarking and stress testing.

- Implement test scenarios as JavaScript scripts.
- Execute tests against the full Docker-based environment.
- Monitor real-time metrics in Grafana during test execution.
- Focus on measuring "SAGA Completion Time" and "Kafka Consumer Lag" under load.

## Rationale:

- **Efficiency:** Built in Go, allowing thousands of concurrent virtual users (VUs) with low local resource consumption.
- **Flexibility:** Scripting allows for complex scenarios (ramping up load, simulating random failures, spikes).
- **Observability Integration:** Naturally aligns with our Grafana/Prometheus stack to correlate load with system health.
- **Developer Friendly:** "Test as Code" approach fits perfectly with our dedicated testing and DevOps strategy.

## Consequences:
### Positive

- Clear visibility into the system's maximum capacity.
- Ability to validate resilience and auto-recovery (SAGA compensations) under stress.
- Quantifiable data for architectural comparisons (e.g., impact of adding partitions).

### Negative

- Requires maintaining JavaScript-based test scripts alongside Java code.
- High-intensity tests can temporarily saturate the local development environment's resources.
- Results can vary depending on the host machine's hardware.