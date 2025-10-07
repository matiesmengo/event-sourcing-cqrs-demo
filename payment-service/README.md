# 📦 payment-service

> ⚡ **Payment Service** handles **payment processing**, **data persistence**, and **publishing payment events** to Kafka for downstream services.  
> 🗄️ Uses a **dedicated database schema `payment`** to keep payment data isolated and maintainable.

---

## 🛠️ Purpose

The `payment-service` is responsible for all payment-related operations:

- Expose payment endpoints to other microservices via **Feign clients**.
- Communicate asynchronously using **Kafka events** (e.g., payment completed, payment failed).
- Persist payment data in **PostgreSQL**, using a dedicated **`payment` schema** with **Flyway** migrations.
- Handle **create, update, and query payment transactions** with proper validations and business logic.

---

## ✨ Key Functionality

### 🧩 Domain Logic
- Implements payment operations and business rules.
- Validates incoming payment requests to prevent fraud or invalid transactions.

### 🗄️ Persistence
- Uses **Spring Data JPA** for repository management.
- Flyway ensures database schema consistency and versioning.
- All payment tables reside in the dedicated **`payment` schema**.

### 🚀 Event-Driven Communication
- Publishes payment events to **Kafka topics** (e.g., `payment.completed`, `payment.failed`).
- Enables downstream services to react asynchronously and scale independently.

---

## ⚙️ How to Run

```bash
# Start PostgreSQL and Kafka
docker-compose up

# Build the service
mvn clean install

# Run the payment service
mvn spring-boot:run
```

## 💡 Benefits

- **Decoupled & Maintainable:** Domain logic is separated from API contracts.
- **Scalable:** Event-driven design via Kafka enables horizontal scaling.
- **Reliable Data Persistence:** Flyway-managed `payment` schema ensures consistency across environments.
- **Isolated Database:** Dedicated `payment` schema keeps data modular and avoids interference with other services.

---

> 🚀 The `payment-service` is the **core of the payment domain**, providing reliable, scalable, and maintainable operations while integrating seamlessly with other microservices.
