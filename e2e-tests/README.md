# 🧪 e2e-test

> ⚡ **End-to-End Test Project** for the Mengo microservices ecosystem.  
> Provides a **dedicated playground** to validate all services working together in a realistic, production-like environment.

---

## 🎯 Purpose

The `e2e-test` project is a **standalone module** designed to:

* **Test the full ecosystem end-to-end** – from REST API requests to Kafka events and Event Store persistence.
* **Decouple test code from production services** – keep service code clean and maintainable.
* **Simulate realistic environments** – ephemeral Docker containers via **Testcontainers** spin up isolated instances of all services, databases, and Kafka.
* **Control the service lifecycle** – start, stop, and reset services and data between test runs for consistent and reproducible results.

> 🐳 **Why Testcontainers?**  
> Ensures **ephemeral, reproducible environments** for each test run without affecting local development setups.

---

## ✨ Key Functionality

### 🐳 Containerized Services

The infrastructure is **fully containerized**:

* **Microservices**: `booking-service`, `booking-service-orchestrator`, `payment-service`, `product-service`.
* **Databases**: Isolated PostgreSQL instances for each service (`booking`, `orchestrator`, `payment`, `product`).
* **Event Bus**: Kafka cluster and Confluent Schema Registry for Avro serialization.
* **Network isolation**: All containers share a dedicated Docker network to simulate real service-to-service communication.

Each service container is configured with:

* Spring DataSource pointing to its respective PostgreSQL.
* Kafka bootstrap servers and Schema Registry URL.
* Automatic `earliest` offset reset for Kafka consumers.
* Log-based health checks (`waitingFor`) to ensure the service is fully started.

---

### 🔗 End-to-End Testing

The e2e tests verify:

* **SAGA flows across multiple services**.
* **Event persistence and versioning** in each Event Store.
* **Kafka events** published with correct payloads and headers.
* **Failure scenarios** such as payment rejection or stock unavailability.

> `Awaitility` is used to **poll asynchronous processes**, ensuring robust assertions.

---

### 🛠 Infrastructure Management

The test framework provides:

* **AbstractInfrastructureE2ETest**: spins up core infrastructure containers:
    * Kafka broker
    * Schema Registry
    * PostgreSQL instances
* **AbstractServicesE2ETest**: spins up all microservice containers with proper dependencies and environment variables.
* **KafkaTestClient**: helper to consume/produce messages with Avro serialization in tests.
* **Clean slate between tests**: database tables are cleaned automatically before each test run.

---

### 🧩 Example Test Scenarios

#### ✅ Booking Completed

1. A booking request is submitted with products.
2. Payment is forced to **SUCCESS**.
3. Assertions verify:
    * Events in Booking, Orchestrator, Product, and Payment Event Stores.
    * Kafka message sent to `booking.completed`.
    * Aggregate versions strictly increase.

#### ❌ Booking Failed (Payment Regression)

1. A booking request is submitted with products.
2. Payment is forced to **FAILURE**.
3. Assertions verify:
    * Correct failure events emitted.
    * Kafka message sent to `booking.failed`.
    * Product reservation still occurs, ensuring compensating actions work.

---

## ⚙️ Requirements

* **Docker** installed and running.
* Built Docker images for all services (`booking-service`, `booking-service-orchestrator`, `payment-service`, `product-service`).
* No Artifactory or private credentials needed – all images are local builds.

---

## 🚀 How to Run

```bash
# Compile all services in the monorepo
mvn clean package

# Build Docker images
docker build -t booking-service-orchestrator:latest -f booking-service-orchestrator/Dockerfile .
docker build -t booking-service:latest -f booking-service/Dockerfile .
docker build -t payment-service:latest -f payment/Dockerfile .
docker build -t product-service:latest -f product/Dockerfile .

# Run the end-to-end tests
mvn clean test
```

---


## 💡 Key Benefits

- Reproducible E2E scenarios for the entire microservice ecosystem.
- Full SAGA coverage, validating both success and failure flows.
- Safe isolation, with ephemeral containers and clean state per test.
- Realistic environment, mimicking production as closely as possible.
- Developer confidence, knowing that domain logic, orchestration, and messaging are correctly wired.

---
