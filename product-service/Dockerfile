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
COPY --from=build /app/product-service/target/product-service-*.jar ./product-service.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "product-service.jar"]
