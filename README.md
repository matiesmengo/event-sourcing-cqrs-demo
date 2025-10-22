# Event-Sourcing + CQRS + SAGA Demo

![Spring Boot](https://img.shields.io/badge/SpringBoot-3.5.6-brightgreen)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9.25-blue)
![Java](https://img.shields.io/badge/Java-21-orange)
![OpenAPI](https://img.shields.io/badge/OpenAPI-3.0.3-brightgreen)
![Feign](https://img.shields.io/badge/Feign-4.3.0-blue)
![Flyway](https://img.shields.io/badge/Flyway-10.20.1-orange)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)
![Kafka](https://img.shields.io/badge/Kafka-3.6.0-orange)
![Testcontainers](https://img.shields.io/badge/Testcontainers-1.19.0-brightgreen)
![Avro](https://img.shields.io/badge/Avro-1.11.2-orange)
![Maven](https://img.shields.io/badge/Maven-3.9.11-brightgreen)
![Docker](https://img.shields.io/badge/Docker-required-blue)

This project is a technical demonstration of an event-sourcing microservices architecture, designed to showcase advanced
backend engineering concepts such as CQRS, SAGA orchestration, idempotency, event versioning, distributed tracing, and
resilience patterns.

The objective is to illustrate how to design, implement, and operate a production-grade distributed system based on
asynchronous communication using Apache Kafka, ensuring eventual consistency, traceability, and observability across
services.

---

## 🧠 Key Concepts Demonstrated

| Category          | Concept                    | Description                                                                                                                                                                                         |
|-------------------|----------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Architecture**  | Hexagonal                  | Services follow the hexagonal design, decoupling core domain logic from infrastructure like Kafka, REST APIs, and databases for modularity, testability, and maintainability.                       |
| **Data Pattern**  | Event Sourcing             | Application state is captured as a sequence of domain events **published to Kafka**. Events are persisted in PostgreSQL for durability and can be replayed or reconstructed for full auditability.  |
| **Data Pattern**  | CQRS (Command / Query)     | Separates commands (writes) from queries (reads). Commands update PostgreSQL events; queries are served from MongoDB projections, enabling independent scaling and optimized read/write operations. |
| **Orchestration** | SAGA Pattern               | Centralized coordination of distributed workflows using the Booking Service Orchestrator, with compensating transactions to handle failures and ensure eventual consistency.                        |
| **Consistency**   | Eventual consistency       | Each service maintains local state derived from domain events, ensuring eventual consistency across the system.                                                                                     |
| **Reliability**   | Idempotent consumers       | Guarantees events are processed logically exactly once, even if received multiple times.                                                                                                            |
| **Contracts**     | Schema Registry            | Central repository of Avro schemas for validation, versioning, and backward/forward compatibility of events.                                                                                        |
| **Testing**       | Testcontainers             | End-to-end testing with ephemeral Kafka, PostgreSQL, and MongoDB instances to ensure reliable integration and system behavior.                                                                      |
| **Resilience**    | Retry, DLQ, Outbox Pattern | Ensures reliable message delivery under transient failures, preventing lost or duplicate events.                                                                                                    |
| **Observability** | Tracing & Metrics          | Distributed tracing and metrics collection using OpenTelemetry and Micrometer for monitoring, debugging, and performance analysis across services.                                                  |

---

## 🧩 Project Structure

| Directory                                                                                                          | Description                                                                                                            |
|--------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------|
| [`mengo-platform/`🔗 ](mengo-platform/README.md)                                                                   | Core platform layer providing unified dependency management and custom Spring Boot starters for shared infrastructure. |
| [`schemas-registry` 🔗 ](schemas-registry/README.md)                                                               | Centralized Avro schemas for all domain events, used for validation and compatibility.                                 |
| [`booking-service-orchestrator/` 🔗 ](booking-service-orchestrator/README.md)                                      | Implements SAGA orchestration; coordinates bookings, product reservations, and payments using CQRS and Kafka.          |
| [`booking-service-api/`🔗 ](booking-service-api/README.md)                                                         | OpenAPI definition and Feign clients for synchronous communication.                                                    |
| [`booking-service/`🔗 ](booking-service/README.md)                                                                 | Handles booking creation; receives REST request from api; consumes orchestrator events and emits payment outcomes.     |
| [`payment-service/`🔗 ](payment-service/README.md)                                                                 | Handles payment workflows; consumes orchestrator events and emits payment outcomes.                                    |
| [`product-service/`🔗 ](product-service/README.md)                                                                 | Handles product workflows; consumes orchestrator events and emits product outcomes.                                    |
| [`e2e-tests/`🔗 ](e2e-tests/README.md)                                                                             | End-to-end tests using Testcontainers and Docker Compose.                                                              |
| [`docs/`🔗 ](https://github.com/matiesmengo/event-sourcing-cqrs-demo/tree/main/docs/architecture-decision-records) | Architecture documentation, ADRs, and UML diagrams.                                                                    |

---

## 🧱 Architecture Overview

### Core Design Principles

- *Loose Coupling:* Each service owns its own database and publishes/consumes domain events.
- *Asynchronous Communication:* Kafka is the main message backbone.
- *Contract-first Schema Evolution:* Every event schema is versioned and validated at runtime.
- *Observability-first:* Tracing, logging, and metrics are part of the architecture from day one.
- *Resilience by Design:* Outbox pattern, retries, idempotent handlers, and DLQs ensure robustness.
- *Centralized Orchestration:* The Booking Service Orchestrator coordinates distributed workflows using the SAGA pattern
  to ensure eventual consistency.

### High-Level Architecture

![Architecture Diagram](./docs/architecture.png)

---

## 🔄 Event Flow

![Event Flow Diagram](./docs/SAGA-diagram.png)


---

## 🧰 Tech Stack

| Layer / Purpose                | Technology & Version                        | Description / Role                                                                                                |
|--------------------------------|---------------------------------------------|-------------------------------------------------------------------------------------------------------------------|
| **Language & Framework**       | Java 21 / Kotlin 1.9.25 / Spring Boot 3.5.x | Core language and framework for microservices, dependency injection, REST, and application bootstrapping.         |
| **Messaging / Event Bus**      | Apache Kafka                                | Asynchronous backbone for event-driven communication between microservices.                                       |
| **Schema Management**          | Confluent Schema Registry                   | Centralized Avro schemas for validation, evolution, and backward/forward compatibility.                           |
| **Persistence**                | PostgreSQL                                  | Relational database for persisting events, snapshots, and transactional state in each service.                    |
| **Read Model / Projection DB** | MongoDB                                     | Stores read-model projections for efficient queries in CQRS, enabling fast and flexible read operations.          |
| **Testing / CI**               | Testcontainers                              | Ephemeral Kafka, PostgreSQL, and MongoDB environments for unit, integration, and end-to-end testing.              |
| **Containerization / DevOps**  | Docker Compose                              | Local orchestration of microservices, databases, and Kafka for reproducible development and testing environments. |
| **Observability / Metrics**    | OpenTelemetry / Micrometer                  | Distributed tracing and metrics collection for monitoring, performance, and troubleshooting.                      |

---

## 🚀 Getting Started

### Requirements

* **Java 21+**
* **Docker**
* **Maven 3.9+**

### Run Locally

```bash
# 1. Local docker 
docker-compose up -d

# 2. Build all modules
mvn clean package

# 3. Launch run time services
cd booking-service && mvn spring-boot:run
cd payment-service && mvn spring-boot:run
cd product-service && mvn spring-boot:run
cd booking-service-orchestration && mvn spring-boot:run
```

### Access points

* **Booking API:** [http://localhost:8080/api/bookings](http://localhost:8080/api/bookings)

* **Booking Service:** executable demo

```bash
curl --location 'localhost:8080/bookings' \
--header 'Content-Type: application/json' \
--data '{
    "userId": "1881b6c3-1786-4b89-b213-84ab680e03ff",
    "products": [
        {
            "productId": "11111111-1111-1111-1111-111111111111",
            "quantity": 10
        },
        {
            "productId": "22222222-2222-2222-2222-222222222222",
            "quantity": 20
        }
    ]
}'
```

---

## 🧪 Testing and Quality

* **Unit tests:** validate internal business logic.


* **Integration tests:** validate Kafka and Postgres conections using Testcontainers.

```bash
# Run unit and integration tests
docker-compose up -d
mvn clean verify
```

* **End to End tests:** validate feign request, kafka topics and postgres persistence using Testcontainers.

```bash
# Run e2e tests
# 1. Start docker engine

# 2. Build all modules
mvn clean package

# 3. Build images
docker build -t orchestration-service:latest -f booking-service-orchestration/Dockerfile .
docker build -t booking-service:latest -f booking-service/Dockerfile .
docker build -t payment-service:latest -f payment-service/Dockerfile .
docker build -t product-service:latest -f product-service/Dockerfile .

# 4. Execute tests
mvn -pl e2e-tests test
```

---

## 🧭 To do

| Area              | Next Step                                                                      |
|-------------------|--------------------------------------------------------------------------------|
| **Persistence**   | Implement Queries and update projections (CQRS)                                |
| **Resilience**    | Implement retries, DLQ, and idempotent message handling                        |
| **Observability** | Integrate Micrometer + Prometheus + OpenTelemetry tracing                      |
| **CI/CD**         | Automate tests and build with GitHub Actions                                   |
| **Performance**   | Implement load and performance tests to evaluate system throughput and latency |

---

## 👤 Author

**Maties Mengo**

*Senior Backend Engineer*

🌐 [GitHub — matiesmengo](https://github.com/matiesmengo)

🔗 [LinkedIn — matiesmengo](https://www.linkedin.com/in/matiesmengo)
