# Stage 1: Build amb Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

COPY ../.. ./

RUN mvn dependency:go-offline
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=build /app/booking-service-orchestrator/target/booking-service-orchestrator-*.jar ./booking-service-orchestrator.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "booking-service-orchestrator.jar"]
