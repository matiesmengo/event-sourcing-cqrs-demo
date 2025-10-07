# 📦 e2e-test

> ⚡ **End-to-End Test Project** for the microservices ecosystem.  
> Provides a **dedicated space for testing all services together** in a realistic environment.

---

## 🛠️ Purpose

The `e2e-test` project was created as a **standalone module** for the following reasons:

- To **test the full microservices ecosystem** end-to-end, covering interactions between services.
- To avoid mixing production/service code with testing code in each service.
- To simulate **realistic environments** with Docker containers using **Testcontainers**.
- To allow **full control over service lifecycle** during tests (start, stop, reset state).

> 🧪 **Why Testcontainers?**  
> Testcontainers allows creating **ephemeral containers for databases, Kafka, and services**, ensuring a clean and reproducible environment for each test run.

---

## ✨ Key Functionality

### 🐳 Containerized Services
- Each service is started in a Docker container via Testcontainers.
- Databases and Kafka are also spun up as isolated containers.

### 🔗 End-to-End Testing
- Executes scenarios across multiple services.
- Verifies that **API contracts, event flows, and persistence** behave correctly in an integrated environment.

### 🔧 Independent Test Environment
- Each test run uses fresh containers for clean state.
- Avoids interference with local development databases or services.

---

## ⚙️ Requirements

- **Docker** installed and running.
- Built Docker images for all services (`booking-service`, `payment-service`, etc.) to run them in the e2e tests.
- No use of **Artifactory** is required because this repository avoids **introducing credentials or private clients**; all images are local builds.
---

## ⚙️ How to Run

```bash
# Compile all monorepo project
mvn clean package

# Docker images build
docker build -t booking-service ./booking-service-impl
docker build -t payment-service ./payment-service

# Run e2e tests
mvn clean test
```
