# ADR 0001: Microservices with Kotlin + Spring Boot and Hexagonal Architecture

## Context:
The project aims to demonstrate a modern, scalable architecture for a distributed system demo.
- Create microservices to encapsulate separate bounded contexts.
- Have independent PostgreSQL databases per service to avoid tight coupling.
- Ensure high code quality and maintainability via clear architecture.
- Allow future evolution towards an event-driven system.
  
## Decision:
- Implement multiple microservices using Kotlin + Spring Boot.
- Apply Hexagonal Architecture to separate domain logic from frameworks and external adapters.
- Each microservice has its own PostgreSQL database.
- Inter-service communication is done via Feign Clients for declarative HTTP calls.
- Rich domain modeling with entities, aggregates, domain services, and DTOs for input/output.

## Rationale:
- **Productivity and maintainability**: Kotlin + Spring Boot allows rapid development with type safety and Spring ecosystem integration.
- **Domain isolation**: Independent databases allow deploying and scaling services individually.
- **Testability**: Hexagonal Architecture enables unit and integration tests by isolating domain logic from infrastructure.
- **Future-proofing**: This setup supports migration to Event Sourcing + CQRS without major domain changes.
- **Service communication**: Feign simplifies HTTP clients and centralizes inter-service call management.

## Consequences:
- **Initial complexity**: More effort required to define domain models and adapters.
- **Operational overhead**: Multiple databases and services increase deployment complexity.
- **Learning curve**: Team must be familiar with Kotlin, Spring Boot, Hexagonal Architecture, Feign, and microservices.
- **Long-term benefits**: Easier service scaling, domain auditing, isolated component testing, and future migration to Event Sourcing + CQRS.