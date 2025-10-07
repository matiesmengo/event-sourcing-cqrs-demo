# 📦 mengo-platform
> ⚡ **Mengo Platform** is the parent project for the microservices ecosystem, providing **common dependencies, configurations, and starters** to ensure consistency across all services.

---

## 🛠️ Purpose

The `mengo-platform` project serves as the **foundation** for all microservices:

- **Centralizes versions and dependency management** through the **BOM** (Bill of Materials).
- Provides a **parent POM** with common build plugins, code style, and configuration defaults.
- Offers **custom Spring Boot starters** to simplify common patterns, libraries, and configuration for microservices.

By using this structure, all services remain **consistent, maintainable, and easy to evolve**.

---

## 📂 Project Structure

```text
mengo-platform/
├─ bom/            # Bill of Materials defining dependency versions
|   └─ pom.xml     
├─ parent/         # Parent POM with common plugins and configurations
|   └─ pom.xml     
├─ starters/       # Custom starters for shared functionality
|   └─ xxxx-starter/
|   |    └─ pom.xml     
|   └─ xxxx-starter-test/          
|   |    └─ pom.xml     
```

### 🔹 Submodules

- **bom**
   - Centralized dependency versions for Java, Kotlin, Spring Boot, and common libraries.
   - Ensures **all microservices use the same versions**.

- **parent**
   - Provides **Maven build configuration**, plugin management, and code quality rules.
   - Can be extended by any microservice POM.

- **starters**
   - Custom Spring Boot starters that include **common dependencies and auto-configuration**.
   - Simplifies setup of new microservices and ensures **best practices are enforced**.

---

## 💡 Benefits

- **Centralized Dependency Management:** Ensures consistent library versions across all microservices.
- **Reduced Boilerplate:** Starters provide ready-to-use features and configurations.
- **Maintainable:** Common configurations live in the parent POM, simplifying upgrades.
- **Fast Onboarding:** New microservices can start quickly by importing the BOM, parent, and starters.
- **Standardized Best Practices:** Enforces coding standards, build plugins, and common patterns.

---

> 🚀 `mengo-platform` is the **foundation of the microservices ecosystem**, enabling consistency, maintainability, and rapid development of new services.