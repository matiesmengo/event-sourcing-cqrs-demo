# Stage 1: Build amb Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

COPY ../.. ./

RUN mvn dependency:go-offline
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=build /app/product-service/target/product-service-*.jar ./product-service.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "product-service.jar"]
