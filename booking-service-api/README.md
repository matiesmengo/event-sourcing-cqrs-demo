# 📦 booking-service-api

> ⚡ **API Contract for the Booking Domain**  
> This microservice defines and exposes **REST interfaces** and generates DTOs that are consumed by other services via **Feign clients**.  
> It serves as a **single source of truth** for the booking API.

---

## 🎯 Purpose

The `booking-service-api` module exists to:

* **Separate API contract from implementation** – allowing independent evolution of the service without breaking consumers.
* **Provide type-safe communication** between microservices.
* **Centralize DTO definitions** for consistency and maintainability.
* **Simplify onboarding** – developers can generate clients automatically without understanding the full service logic.

> 💡 By decoupling API from implementation, the platform reduces runtime errors and enforces a contract-driven approach for all booking-related interactions.

---

## 🛠️ Technologies & Versions

| Component                  | Version | Purpose |
|----------------------------|---------|---------|
| **Spring Cloud OpenFeign** | 4.x     | Type-safe inter-service clients using generated DTOs. |
| **OpenAPI 3 / Swagger**    | 3.x     | Defines REST endpoints and generates DTOs automatically. |

> ⚠️ Versions are chosen for **compatibility with Spring Boot 3.4.0 and Kotlin 2.0.21**, ensuring stability across all services.

---

## ✨ Key Functionality

### OpenAPI 3 Contract

* Defines all booking-related REST endpoints.
* Generates Kotlin DTOs for requests, responses, and domain models.
* Ensures consistency across all consumers by using the same source of truth.

### Feign Clients

* Uses generated DTOs to create **type-safe HTTP clients**.
* Guarantees compile-time checks for inter-service communication.
* Automatically propagates changes in API contracts to all consumers.

### Automatic DTO Generation

* Built using `openapi-generator-maven-plugin`.
* DTOs are regenerated with every build, keeping all services synchronized.
* Simplifies cross-team collaboration: no manual mapping or copy-paste needed.

---

## 💡 Why This Structure Matters

* ✅ **Consistency Across Services:** All booking APIs come from a single source of truth.
* ✅ **Type-Safety:** Compile-time verification prevents runtime errors due to schema mismatches.
* ✅ **Decoupled Architecture:** Service implementation evolves independently of API clients.
* ✅ **Reduced Maintenance:** Updating DTOs in one place automatically propagates to consumers.
* ✅ **Faster Onboarding:** New developers can generate API clients without reading service code.
* ✅ **Future-Proof:** Adding new clients or migrating services does not break existing consumers.

---

## ⚙️ How to Generate DTOs

```bash
mvn clean install
```

> After running the command, generated DTOs are available for consumption by other microservices via Feign clients.

---
