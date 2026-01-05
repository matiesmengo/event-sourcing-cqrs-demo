package com.mengo.e2e.infrastructure

import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import java.time.Duration

abstract class AbstractServicesE2ETest : AbstractInfrastructureE2ETest() {
    companion object {
        @Container
        val orchestratorService =
            GenericContainer("booking-service-orchestrator:latest")
                .withExposedPorts(8085)
                .dependsOn(orchestratorPostgres, kafka, schemaRegistry)
                .withNetwork(network)
                .withEnv("SPRING_DATASOURCE_URL", "jdbc:postgresql://orchestrator-postgres:5432/orchestrator")
                .withEnv("SPRING_DATASOURCE_USERNAME", "user")
                .withEnv("SPRING_DATASOURCE_PASSWORD", "pass")
                .withEnv("SPRING_KAFKA_BOOTSTRAP_SERVERS", "kafka:9092")
                .withEnv("SCHEMA_REGISTRY_URL", "http://schema-registry:8081")
                .withEnv("SPRING_KAFKA_CONSUMER_PROPERTIES_SCHEMA_REGISTRY_URL", "http://schema-registry:8081")
                .withEnv("SPRING_KAFKA_PRODUCER_PROPERTIES_SCHEMA_REGISTRY_URL", "http://schema-registry:8081")
                .withEnv("SPRING_KAFKA_CONSUMER_AUTO_OFFSET_RESET", "earliest")
                .waitingFor(
                    Wait
                        .forHttp("/actuator/health")
                        .forPort(8085)
                        .forStatusCode(200)
                        .withStartupTimeout(Duration.ofSeconds(45)),
                )

        @Container
        val bookingService =
            GenericContainer("booking-service-command:latest")
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
                .waitingFor(
                    Wait
                        .forHttp("/actuator/health")
                        .forPort(8080)
                        .forStatusCode(200)
                        .withStartupTimeout(Duration.ofSeconds(45)),
                )

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
                .withEnv("SPRING_KAFKA_CONSUMER_AUTO_OFFSET_RESET", "earliest")
                .waitingFor(
                    Wait
                        .forHttp("/actuator/health")
                        .forPort(8083)
                        .forStatusCode(200)
                        .withStartupTimeout(Duration.ofSeconds(45)),
                )

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
                .withEnv("SPRING_KAFKA_CONSUMER_AUTO_OFFSET_RESET", "earliest")
                .waitingFor(
                    Wait
                        .forHttp("/actuator/health")
                        .forPort(8084)
                        .forStatusCode(200)
                        .withStartupTimeout(Duration.ofSeconds(45)),
                )
    }
}
