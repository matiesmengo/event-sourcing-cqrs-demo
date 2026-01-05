# Stage 1: Build amb Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

COPY ../.. ./

RUN mvn dependency:go-offline
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=build /app/booking-service-command/target/booking-service-command*.jar ./booking-service-command.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "booking-service-command.jar"]
