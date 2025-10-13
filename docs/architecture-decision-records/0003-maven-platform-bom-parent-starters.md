# ADR 0003: Maven BOM, Parent POM, and Starters via /mengo-platform

## Context:
The project consists of multiple microservices, each with its own dependencies and configuration. Without standardization, maintaining consistent versions and configurations across services can lead to:
- Dependency conflicts.
- Divergent build configurations.
- Hard-to-reproduce setups across environments.

## Decision:
Adopt a centralized Maven platform (/mengo-platform) that provides:
- **Parent POM**: Defines common plugins, build configurations, and properties shared across all microservices.
- **Maven BOM (Bill of Materials)**: Manages dependency versions consistently, ensuring compatibility across modules.
- **Starters**: Provides preconfigured dependencies and sensible defaults for typical service needs (Spring Boot, Kafka, PostgreSQL, Testcontainers, etc.).

Each microservice inherits from /mengo-platform parent POM and imports the BOM to avoid manually managing dependency versions.

## Rationale:
- **Consistency**: All services use the same dependency versions, reducing "works on my machine" issues.
- **Maintainability**: Updating a dependency in the BOM propagates to all services automatically.
- **Productivity**: Starters simplify project setup and onboarding by providing ready-to-use configurations.
- **Centralized control**: Build and plugin management is unified, ensuring CI/CD consistency.

## Consequences:
- **Learning curve**: Developers must understand inheritance and BOM usage in Maven.
- **Initial setup overhead**: Creating and maintaining /mengo-platform requires careful design.
- **Simplified maintenance long-term**: Once established, adding or upgrading services becomes faster and less error-prone.
- **Better scalability of the platform**: Ensures all services evolve in a coordinated, controlled manner.