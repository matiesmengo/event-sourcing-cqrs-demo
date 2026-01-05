# 🧪 Engineering Test Suite

> **Reliability is not an accident; it's a verified property.**

> This module contains the specialized suites to validate the system under two dimensions: **Functional Integrity (E2E)** and **Operational Limits (Performance)**.

---

## 🎯 Purpose

The `test-suite` project is a **standalone module** designed to:

* **Full Ecosystem Validation:** Tests the entire flow from REST API requests to Kafka events and Event Store persistence.
* **Clean Decoupling:** Keeps test logic separated from production services to ensure maintainability.
* **Production Parity:** Uses **Testcontainers** to spin up ephemeral, isolated instances of all services, databases, and Kafka.
* **Deterministic Lifecycle:** Provides total control to start, stop, and reset services/data between runs for reproducible results.

---

## ⚡ Performance & Stress Suite (k6)

Located in `/performance`, these scripts use the **k6** engine to push the SAGA orchestrator and Kafka consumers to their limits.

### 📊 Performance Visualization
We use **Grafana** to monitor the system's "vital signs" during stress tests. This allows us to correlate load spikes with service latency and resource consumption.

![Grafana Stress Test Performance](../docs/images/grafana-performance.gif)

⚠️ **Stress Test Insights: Contention & Bottlenecks** The demonstration above showcases a high-pressure scenario designed to test **concurrency limits.** By using only **3 distinct products** to handle **21,562 booking requests,** we intentionally created a massive **database and row-level locking bottleneck.**

In a real-world scenario, high traffic is usually spread across thousands of SKUs. By narrowing it down to 3, thousands of threads are forced to compete for the same database rows simultaneously.

**Key Metrics:**
- **Throughput:** Ingested all requests in just 1 minute and 30 seconds.
- **Execution:** Configured with 6 Kafka partitions and 6 concurrent consumer threads (@KafkaListener) on a single instance.
- **Recovery:** Despite the intense row contention and surge in consumer lag, the system remained stable and fully settled the entire backlog in less than 7 minutes.

### 🎯 Objectives
* **Throughput (RPS):** Measure the maximum concurrent bookings the system can coordinate. In this setup, we achieved an ingestion rate of ~200 requests per second.
* **Latency Distribution:** Monitor p95/p99 response times during traffic spikes to ensure the SAGA state machine remains responsive.
* **Consumer Efficiency:** Validate that the 1:1 ratio between Kafka partitions and consumer threads optimizes "drainage" time and minimizes message lag.
* **Resilience Under Pressure:** Verify data integrity and SAGA completion (including compensations) even under high heavy load.


### 🚀 How to Run
Execute the stress test using Docker to ensure a clean k6 environment:
```bash
Get-Content booking-stress-test.js | docker run --rm -i --add-host=host.docker.internal:host-gateway grafana/k6 run -
```

---

## ⚡ End-to-End Functional Suite (Java/Kotlin)

Located in `src/test/kotlin`, this suite validates the distributed coordination of the **SAGA flows** and **Event Sourcing** integrity.

### ✨ Key Capabilities
* **Eventual Consistency Handling:** Uses `Awaitility` to poll asynchronous processes, avoiding flaky tests.
* **State Audit:** Uses specialized helpers to verify that every event has been persisted in the Event Store with strictly increasing versions.
* **Infrastructure Abstraction:** `AbstractServicesE2ETest` manages the complex lifecycle of spinning up 5+ microservices and their dependencies.

### 🏗️ Test Architecture

The infrastructure is **fully containerized** using **Testcontainers**, simulating a production-like network environment:

* **Microservices:** `booking-command`, `booking-query`, `orchestrator`, `payment`, and `product`.
* **Databases:** Isolated PostgreSQL instances per service with specific Spring DataSources.
* **Event Bus:** Kafka cluster (with `earliest` offset reset) and Confluent Schema Registry.
* **Health Checks:** Log-based `waitingFor` strategies to ensure services are ready before tests start.

### 🧩 Example Scenarios
| Scenario             | Focus             | Expected Outcome                                                                   |
|:---------------------|:------------------|:-----------------------------------------------------------------------------------|
| **Happy Path**       | Standard Booking  | All services commit; `booking.completed` event emitted; stock reduced.             |
| **Payment Rejected** | SAGA Compensation | Booking cancelled; Product stock restored via compensating transaction.            |
| **Stock Shortage**   | Edge Case         | SAGA fails at the first step; Payment is never triggered; System stays consistent. |


### 🚀 How to Run

* **Docker** installed and running.
* Built Docker images for all services (`booking-service-command`,`booking-service-query`, `booking-service-orchestrator`, `payment-service`, `product-service`).
* No Artifactory or private credentials needed – all images are local builds.


```bash
# Compile all services in the monorepo
mvn clean package

# Build Docker images
docker build -t booking-service-orchestrator:latest -f booking-service-orchestrator/Dockerfile .
docker build -t booking-service-command:latest -f booking-service-command/Dockerfile .
docker build -t booking-service-query:latest -f booking-service-query/Dockerfile .
docker build -t payment-service:latest -f payment-service/Dockerfile .
docker build -t product-service:latest -f product-service/Dockerfile .

# Run the end-to-end tests
mvn clean test -pl test-suite
```
---
