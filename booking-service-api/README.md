# 📦 booking-service-api

This microservice serves as the **API contract** for the booking domain. It defines and exposes REST interfaces and generates DTOs consumed by other services through **Feign clients**.

> ⚡ **Goal:** Separate API definition from implementation to ensure **type-safe**, **consistent**, and **reliable** communication between microservices.

---

## 🛠️ Technologies & Versions

| Component       | Version       | Why This Version |
|-----------------|---------------|----------------|
| **Spring Cloud OpenFeign** | 4.x | Type-safe inter-service clients based on generated DTOs. |
| **OpenAPI 3 / Swagger**    | 2.x | Defines API contracts and generates DTOs automatically. |

> ⚠️ **Note:** Versions are chosen for **full compatibility** with Spring Boot 3.5.6 and Kotlin 1.9.25, avoiding issues from bleeding-edge versions.

---

## ✨ Key Functionality

- **OpenAPI 3**
    - Defines REST endpoints and data models.
    - Generates Kotlin DTOs automatically, keeping all consumers consistent.

- **Feign Clients**
    - Uses generated DTOs for type-safe communication.
    - Guarantees all consumers follow the same API contract.

- **Automatic DTO Generation**
    - Uses `openapi-generator-maven-plugin` to generate DTOs.
    - Centralizes API definitions and keeps them synchronized across services.

---

## 💡 Why This Project Structure

- ✅ **Consistency Across Services:** Single source of truth for all booking-related APIs.
- ✅ **Type-Safety:** Compile-time checks prevent runtime errors.
- ✅ **Decoupled Architecture:** API definitions live separately from implementations.
- ✅ **Reduced Maintenance Overhead:** Updates propagate automatically to all consumers.
- ✅ **Faster Onboarding:** Developers can generate clients without reading the full implementation.
- ✅ **Future-Proof:** Easy to introduce new clients or migrate services without breaking consumers.

---

## ⚙️ How to Generate DTOs

```bash
mvn clean install
```