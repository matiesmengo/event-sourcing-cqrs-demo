# 📦 booking-service

> ⚡ **Booking Service Implementation** handles **booking business logic**, **data persistence**, and **event publishing** to Kafka for downstream services.  
> 🗄️ Uses a **dedicated database schema `booking`** to keep booking data isolated and maintainable.

---

## 🛠️ Purpose

This service implements the business logic behind bookings:

- Expose endpoints defined in **booking-service-api** via **Feign clients** for other services.
- Communicate asynchronously with other microservices using **Kafka events**.
- Persist booking data in **PostgreSQL**, with migrations handled via **Flyway**.
- Manage **create, update, delete, and query operations** for bookings.

---

## ✨ Key Functionality

### 🧩 Domain Logic
- Implements booking operations and business rules.
- Validates incoming booking requests to prevent invalid data.

### 🗄️ Persistence
- Uses **Spring Data JPA** repositories for database interactions.
- Flyway ensures schema consistency and smooth migrations.
- All booking tables reside in a dedicated **`booking` schema**, separating concerns from other services.

### 🚀 Event-Driven Communication
- Publishes booking events (created, updated, canceled) to **Kafka topics**.
- Enables other services to react asynchronously and scale independently.

### 🔗 Integration with booking-service-api
- Feign clients consume API contracts from **booking-service-api**.
- Guarantees **type-safe and consistent communication** across services.

## ⚙️ How to Run

```bash
# Start PostgreSQL and Kafka
docker-compose up

# Build the service
mvn clean install

# Run the booking service
mvn spring-boot:run
```

## 💡 Benefits

- **Decoupled & Maintainable:** Domain logic is separated from API contracts.
- **Scalable:** Event-driven design via Kafka enables horizontal scaling.
- **Reliable Data Persistence:** Flyway-managed `booking` schema ensures consistency across environments.
- **Type-Safe Communication:** Feign clients enforce API contract consistency.
- **Isolated Database:** Dedicated `booking` schema keeps data modular and avoids interference with other services.

---

> 🚀 The `booking-service` is the **core of the booking domain**, providing reliable, scalable, and maintainable operations while integrating seamlessly with other microservices.
