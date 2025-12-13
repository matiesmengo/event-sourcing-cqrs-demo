# 🛠️ Architecture Starter

**The Resilience & Observability Chassis**  
This module is the backbone of cross-cutting concerns in the Mengo microservices ecosystem.  
It provides **resilience, distributed context propagation, and zero-boilerplate telemetry** for every service.

---

## 🌟 Core Features

### 🔄 Resilience: Inbox Patterns & Transactional Outbox

Ensure **data integrity** and **strict idempotency** in distributed systems:

* **Idempotency Guard (Inbox Pattern)**  
  Tracks processed events in `processed_events` using a unique `causation_id`.  
  SQL-level atomicity with `ON CONFLICT DO NOTHING` prevents duplicated event processing.

* **Transactional Outbox Dispatcher**  
  A high-throughput `@Scheduled` relay dispatches message batches from PostgreSQL to Kafka.  
  Guarantees **at-least-once delivery** by tying database updates and event publishing atomically.

---

### 🌐 Distributed Context: Metadata & Tracing

Propagate distributed context seamlessly across synchronous and asynchronous boundaries:

* **Metadata Interceptors**  
  For Spring WebMvc and Kafka Consumers. Preserve headers like `correlation-id`, `causation-id`, and `traceparent`.

* **Unified MDC Management**  
  Injects contextual metadata into every log line for traceability.  
  Enables end-to-end correlation in Grafana Loki or similar logging platforms.

---

### 📊 Observability: AOP-Driven Telemetry

A **zero-boilerplate telemetry system** leveraging Aspect-Oriented Programming:

* **`@ObservabilityStep` Annotation**  
  Automatically intercepts methods to capture:

  * **Execution Latency** – Micrometer timers with 0.5, 0.95, and 0.99 percentile distributions.
  * **Business Success/Failure** – Automated counters for each SAGA step.
  * **Automatic Logging Enrichment** – Injects `sagaId`, `traceId`, and `correlationId` into MDC.
  * **SAGA Metrics** – Tracks started, completed, and compensated SAGA instances out-of-the-box.

---

## ⚙️ Technical Deep Dive

### 📨 Outbox Dispatcher Logic

Production-grade design for Kafka message dispatch:

* **Batch Processing** – Fetches batches (default 100) to reduce database roundtrips.
* **Retry Strategy & DLQ** – Messages exceeding `max-retries` go to `FAILED` status, triggering alerts.
* **Metadata Injection** – Internal `MetadataContext` is automatically mapped into Kafka headers during dispatch.

---

### 🔒 Atomic Idempotency with InboxRepository

```kotlin
// SQL-level atomic idempotency check
INSERT INTO processed_events(causation_id, aggregate_id, processed_at)
VALUES(:causationId, :correlationId, NOW())
ON CONFLICT (causation_id) DO NOTHING
```

This ensures resilience even under heavy Kafka redelivery, preventing side effects from duplicate events.

### 👨‍💻 Developer Experience (DX)

Zero Boilerplate – Simply include the starter and annotate business methods.

Standardization – Unified metrics (saga_step_duration_seconds) for dashboards and alerts across services.

Hexagonal Architecture Friendly – Keeps domain logic clean from infrastructure concerns (messaging, telemetry, etc.).

> With architecture-starter, every microservice gains resilience, observability, and traceability automatically—without writing extra plumbing code.