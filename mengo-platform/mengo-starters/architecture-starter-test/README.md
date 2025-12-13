# 🧪 Architecture Test Starter

**The Quality & Integration Engine**  
This module provides a **robust, production-like testing framework** for distributed systems, event-driven flows, and architectural integrity—all with **zero manual setup**.

---

## 🌟 Core Features

### 🚀 Automated Infrastructure (Testcontainers)

No more *"It works on my machine."*  
Spin up a **production-like environment** automatically with `AbstractInfrastructureIntegrationTest`:

* **Kafka & Schema Registry**  
  Fully configured Avro-capable event bus for testing asynchronous flows.

* **PostgreSQL**  
  Isolated, ephemeral database instances for Event Sourcing and SAGA state.

* **Dynamic Property Injection**  
  Container ports and configurations are automatically bridged to Spring Boot properties at runtime.

---

### 📚 Event Store Auditing

Validate your **Write Model** reliably with `MengoEventStoreAudit`:

* **Event Verification** – Assert that domain events are persisted correctly.
* **Version Integrity** – Ensure aggregate versions increment consistently, preventing concurrency regressions.

---

### 🎯 High-Level Kafka Testing

Simplify asynchronous assertions with `KafkaTestClient`:

* **Wait-and-Assert** – Fluent API to consume from topics and assert outcomes with configurable timeouts.
* **Avro Integration** – Handles complex `SpecificRecord` types automatically via Schema Registry.

---

### 🏗️ Architectural Guardrails (ArchUnit)

Enforce **hexagonal architecture** and code quality automatically:

* **Hexagonal Enforcement** – Domain layer stays pure, Application layer isolated, Infrastructure doesn’t leak.
* **Policy Checking** – `ProhibitedImportsTest` prevents misuse of forbidden libraries and enforces standardized patterns.

---

## ⚙️ Technical Deep Dive

### 🔹 Integration Test Hierarchy

* **AbstractInfrastructureIntegrationTest** – Manages the lifecycle of Docker containers (Kafka, Postgres, Schema Registry).
* **AbstractIntegrationTest** – Provides pre-configured `KafkaTemplate` and `KafkaConsumer` for business-level tests.

---

### 📝 Domain-Driven Kafka Records

The `buildProducerRecord` helper ensures test messages carry **realistic metadata**:

* `correlationId`, `causationId`, `traceparent`
* Fully exercises `architecture-starter` interceptors, simulating production traffic.

---

## 💡 Key Benefits

* **Reproducible E2E Scenarios** – Test a full SAGA flow, from REST command to Event Store persistence, in one JUnit run.
* **Contract Validation** – Real Schema Registry ensures Avro schema backward/forward compatibility.
* **Fast Feedback** – Instant detection of architectural violations (e.g., Domain class importing JPA).

---

> With `architecture-starter-test`, developers get a **reliable, reproducible, and fully integrated testing environment** that mirrors production systems while enforcing architectural integrity.
