# Stage 1: Build amb Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copiem TOT el monorepo dins del build context
COPY ../.. ./

# Instal·lem dependències
RUN mvn dependency:go-offline

# Buildem tot el monorepo sense tests
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Instal·lem bash si cal per les proves
RUN apk update && apk add --no-cache bash

# Copiem només el JAR final del microservei
COPY --from=build /app/booking-service-orchestrator/target/booking-service-orchestrator-*.jar ./booking-service-orchestrator.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "booking-service-orchestrator.jar"]
