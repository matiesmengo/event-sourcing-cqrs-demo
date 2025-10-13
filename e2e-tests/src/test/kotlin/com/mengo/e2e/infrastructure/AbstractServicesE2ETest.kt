package com.mengo.e2e.infrastructure

import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container

abstract class AbstractServicesE2ETest : AbstractInfrastructureE2ETest() {
    // TODO: migrate from "waitingFor" to health checks
    companion object {
        val bookingService =
            GenericContainer("booking-service:latest")
                .withExposedPorts(8080)
                .dependsOn(bookingPostgres, kafka, schemaRegistry)
                .withNetwork(network)
                .withEnv("SPRING_DATASOURCE_URL", "jdbc:postgresql://booking-postgres:5432/booking")
                .withEnv("SPRING_DATASOURCE_USERNAME", "user")
                .withEnv("SPRING_DATASOURCE_PASSWORD", "pass")
                .withEnv("SPRING_KAFKA_BOOTSTRAP_SERVERS", "kafka:9092")
                .withEnv("SCHEMA_REGISTRY_URL", "http://schema-registry:8081")
                .withEnv("SPRING_KAFKA_CONSUMER_PROPERTIES_SCHEMA_REGISTRY_URL", "http://schema-registry:8081")
                .withEnv("SPRING_KAFKA_PRODUCER_PROPERTIES_SCHEMA_REGISTRY_URL", "http://schema-registry:8081")
                .withEnv("SPRING_KAFKA_CONSUMER_AUTO_OFFSET_RESET", "earliest")
                .waitingFor(Wait.forLogMessage(".*Started BookingServiceApplication.*", 1))

        @Container
        val paymentService =
            GenericContainer("payment-service:latest")
                .withExposedPorts(8083)
                .dependsOn(paymentPostgres, kafka, schemaRegistry)
                .withNetwork(network)
                .withEnv("SPRING_DATASOURCE_URL", "jdbc:postgresql://payment-postgres:5432/payment")
                .withEnv("SPRING_DATASOURCE_USERNAME", "user")
                .withEnv("SPRING_DATASOURCE_PASSWORD", "pass")
                .withEnv("SPRING_KAFKA_BOOTSTRAP_SERVERS", "kafka:9092")
                .withEnv("SCHEMA_REGISTRY_URL", "http://schema-registry:8081")
                .withEnv("SPRING_KAFKA_CONSUMER_PROPERTIES_SCHEMA_REGISTRY_URL", "http://schema-registry:8081")
                .withEnv("SPRING_KAFKA_PRODUCER_PROPERTIES_SCHEMA_REGISTRY_URL", "http://schema-registry:8081")
                .waitingFor(Wait.forLogMessage(".*Started PaymentServiceApplication.*", 1))

        @Container
        val productService =
            GenericContainer("product-service:latest")
                .withExposedPorts(8084)
                .dependsOn(productPostgres, kafka, schemaRegistry)
                .withNetwork(network)
                .withEnv("SPRING_DATASOURCE_URL", "jdbc:postgresql://product-postgres:5432/product")
                .withEnv("SPRING_DATASOURCE_USERNAME", "user")
                .withEnv("SPRING_DATASOURCE_PASSWORD", "pass")
                .withEnv("SPRING_KAFKA_BOOTSTRAP_SERVERS", "kafka:9092")
                .withEnv("SCHEMA_REGISTRY_URL", "http://schema-registry:8081")
                .withEnv("SPRING_KAFKA_CONSUMER_PROPERTIES_SCHEMA_REGISTRY_URL", "http://schema-registry:8081")
                .withEnv("SPRING_KAFKA_PRODUCER_PROPERTIES_SCHEMA_REGISTRY_URL", "http://schema-registry:8081")
                .waitingFor(Wait.forLogMessage(".*Started ProductServiceApplication.*", 1))
    }
}
