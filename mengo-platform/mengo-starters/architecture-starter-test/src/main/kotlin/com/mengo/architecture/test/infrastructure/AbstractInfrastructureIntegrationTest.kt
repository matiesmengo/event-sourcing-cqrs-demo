package com.mengo.architecture.test.infrastructure

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
abstract class AbstractInfrastructureIntegrationTest {
    companion object {
        val network: Network = Network.newNetwork()

        @Container
        val kafkaContainer: KafkaContainer = TestContainerFactory.createKafka(network)

        @Container
        val schemaRegistryContainer: GenericContainer<*> =
            TestContainerFactory.createSchemaRegistry(network, kafkaContainer)

        @Container
        val postgresContainer: PostgreSQLContainer<*> =
            TestContainerFactory
                .createPostgres(
                    network = network,
                    dbName = "testdb",
                    alias = "postgres-db",
                ).apply {
                    withUsername("test")
                    withPassword("test")
                }

        @JvmStatic
        @DynamicPropertySource
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.kafka.bootstrap-servers") { kafkaContainer.bootstrapServers }
            registry.add("spring.kafka.consumer.auto-offset-reset") { "earliest" }
            registry.add("spring.kafka.consumer.value-deserializer") { "io.confluent.kafka.serializers.KafkaAvroDeserializer" }
            registry.add("spring.kafka.producer.value-serializer") { "io.confluent.kafka.serializers.KafkaAvroSerializer" }

            registry.add("spring.kafka.consumer.properties.schema.registry.url") {
                "http://${schemaRegistryContainer.host}:${schemaRegistryContainer.getMappedPort(8081)}"
            }
            registry.add("spring.kafka.producer.properties.schema.registry.url") {
                "http://${schemaRegistryContainer.host}:${schemaRegistryContainer.getMappedPort(8081)}"
            }

            registry.add("spring.datasource.url") { postgresContainer.jdbcUrl }
            registry.add("spring.datasource.username") { postgresContainer.username }
            registry.add("spring.datasource.password") { postgresContainer.password }
            registry.add("spring.datasource.driver-class-name") { postgresContainer.driverClassName }

            registry.add("spring.flyway.schemas") { "testschema" }
            registry.add("spring.jpa.properties.hibernate.default_schema") { "testschema" }
            registry.add("spring.jpa.hibernate.ddl-auto") { "update" }
        }
    }
}
