# 🚀 Mengo Platform Foundation

**The Backbone of the Ecosystem**  
`mengo-platform` centralizes governance across all microservices: dependency management, build lifecycles, and architectural standards. It ensures consistency, maintainability, and developer productivity across the platform.

---

## 🏗️ Maven Hierarchy & Architecture

The platform uses a **multi-tiered inheritance model** to decouple dependency versions from build configuration.  
Services inherit standard settings, libraries, and architectural patterns without duplication.

---

## 🧩 Components Breakdown

| Artifact                      | Purpose & Highlights                                                                                                                       |
|-------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------|
| **mengo-bom**                 | Bill of Materials: centralizes all library versions (Spring Boot, Kotlin, Avro, etc.), preventing version mismatches.                     |
| **mengo-parent**              | Defines build lifecycle, Kotlin compiler plugins (`all-open`, `jpa`), and global repositories (Confluent, Maven Central).                 |
| **mengo-parent-services**     | Parent for business microservices. Injects `architecture-starter`, configures Spring Boot repackaging, Docker layering, and common plugins. |
| **architecture-starter**      | Implements Outbox/Inbox patterns, AOP telemetry, and distributed tracing for microservices.                                             |
| **architecture-starter-test** | Provides Testcontainers factories, ArchUnit rules, Kafka/EventStore audit helpers, and ensures hexagonal architecture compliance.       |

---

## 🔄 Dependency & Inheritance Flow

The structure ensures that every microservice inherits a consistent set of **dependencies, build plugins, and architectural rules**.

![Maven Diagram](../docs/maven-structure.png)

- **BOM (`mengo-bom`)** → controls library versions.
- **Parent POM (`mengo-parent`)** → defines build lifecycle and plugins.
- **Service Parent (`mengo-parent-services`)** → injects architecture patterns and Spring Boot optimizations.
- **Microservices** → inherit the full stack automatically (`booking-service-command`, `payment-service`, etc.).

> Each layer is intentionally separated to allow **independent evolution** of dependencies, build rules, and architectural patterns.

---

## ⚙️ Implementation Details

### 🐳 Optimized Docker Layering

Spring Boot layers are enabled in `mengo-parent-services`:

```xml
<configuration>
    <layers>
        <enabled>true</enabled>
    </layers>
</configuration>
