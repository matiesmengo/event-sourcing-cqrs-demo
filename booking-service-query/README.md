# 🔍 booking-service-query

> Read-optimized service providing a consolidated view of Bookings.
> It implements the **Query Side** of the CQRS pattern by projecting domain events into a denormalized MongoDB store.

This service is responsible for providing a fast, scalable, and queryable representation of Bookings. While the command side focuses on business logic and state transitions, this service focuses on **data retrieval, aggregation, and presentation**.

---

## 🧠 Read Model (Projection)

The service maintains a denormalized view of the booking state in **MongoDB**:

- **Collection**: `booking_projections`
- **Format**: Document-based, optimized for single-lookup retrieval.
- **Dynamic Calculation**: Total price is calculated on-the-fly during retrieval to ensure accuracy across partial updates.

### Data Structure:
- `bookingId`: Unique identifier (Primary Key).
- `status`: Current lifecycle state (CREATED, PAID, FAILED, etc.).
- `items`: Embedded list of products with their quantities and unit prices.
- `paymentReference`: External reference from the payment gateway.
- `cancelReason`: Detail on why a booking was rejected.
- `lastEventTimestamp`: Metadata for tracking projection freshness and handling out-of-order events.

---

## 📥 Event Consumption & Projections

This service reacts to events from multiple domains to build its local state. It listens to the following Kafka topics:

| Topic | Source Service | Responsibility |
| :--- | :--- | :--- |
| `booking.created` | Booking Command | Initializes the base projection document. |
| `product.reserved` | Product Service | Enriches items with their actual unit price. |
| `payment.completed`| Payment Service | Updates payment reference and status to PAID. |
| `payment.failed` | Payment Service | Updates status and adds failure reasons. |
| `booking.completed`| Booking Command | Finalizes the booking status (CONFIRMED). |
| `booking.failed` | Booking Command | Marks the booking as FAILED/CANCELLED. |

---

## 🛡 Concurrency & Out-of-Order Events

In a distributed event-driven system, events can arrive out of sequence. This service implements **Idempotency and Versioning** at the database level:

- **Timestamp Comparison**: Every update query includes a check: `Criteria.where("lastEventTimestamp").lt(data.timestamp)`.
- **Atomic Upserts**: Uses MongoDB's `updateFirst` with `setOnInsert` to handle cases where a "secondary" event (like `product.reserved`) arrives before the "initial" event (`booking.created`).
- **Partial Updates**: If an event arrives for a document that doesn't exist yet, the service performs an **Upsert**, creating a partial record that will be completed by subsequent events.

This guarantees **eventual consistency** without requiring global ordering of events.

---

## 🔀 CQRS & Responsibilities

This service represents the **query side** of the Booking domain.

### It DOES:
- Provide high-performance read APIs.
- Aggregate data from multiple domain events (Booking, Product, Payment).
- Maintain a query-optimized document store in MongoDB.
- Handle out-of-order events gracefully using timestamp-based fencing.

### It DOES NOT:
- Enforce business invariants (handled by the Command side).
- Publish domain events.
- Orchestrate SAGA workflows.
- Modify the authoritative "Source of Truth" (the PostgreSQL Event Store).

---

## 🔗 Service Interactions

### Inbound (Kafka)
Listens to multiple topics to keep the projection in sync:
- `booking.created`
- `product.reserved`
- `payment.completed` / `payment.failed`
- `booking.completed` / `booking.failed`

### Inbound (API)
- **GET** `/bookings/{bookingId}`: Returns the `BookingReadModel` with on-the-fly calculated totals.

---

## 🛠 Tech Stack
- **Database**: MongoDB (Projection Store)
- **Messaging**: Kafka (Avro serialized events)
- **Framework**: Spring Boot + Spring Data MongoDB
- **Observability**: Custom `@ObservabilityStep` for tracing projection latency across the Kafka consumer.