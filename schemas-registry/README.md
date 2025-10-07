# 📦 schemas-registry

> ⚡ **Schema Registry Service** manages and validates **Avro schemas** for Kafka topics in the microservices ecosystem.  
> Ensures that all events adhere to a **strict contract**, preventing breaking changes between services.

---

## 🛠️ Purpose

The `schemas-registry` project serves as a **centralized registry for event schemas**:

- Maintain and version **Avro schemas** for Kafka events.
- Validate messages produced or consumed by microservices.
- Ensure **compatibility and consistency** between producers and consumers.
- Prevent breaking changes in event-driven communication.

> 🧩 This allows all microservices to safely publish and consume events without risking data mismatch or runtime errors.

---

## ✨ Key Functionality

### 📑 Schema Management
- Stores Avro schemas for each Kafka topic.
- Supports versioning to manage schema evolution over time.
- Validates schema compatibility (backward, forward, or full).

### 🚀 Event Validation
- Ensures that all events published to Kafka conform to the registered schema.
- Protects consumers from invalid or incompatible messages.

### 🔗 Integration with Microservices
- Microservices interact with the schema registry via REST API or Kafka serializer/deserializer.
- Guarantees type-safe and contract-driven communication across the ecosystem.

---

## ⚙️ How to Install
```bash
mvn clean install
```