package com.mengo.architecture.test

import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.ProducerRecord
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.util.UUID

@SpringBootTest
@Testcontainers
abstract class ContainerBase {
    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, SpecificRecord>

    @Autowired
    private lateinit var consumerFactory: ConsumerFactory<String, SpecificRecord>
    lateinit var kafkaConsumer: KafkaConsumer<String, SpecificRecord>

    @BeforeEach
    fun setup() {
        kafkaConsumer = consumerFactory.createConsumer() as KafkaConsumer<String, SpecificRecord>
    }

    @AfterEach
    fun clean() {
        kafkaConsumer.close()
    }

    fun buildProducerRecord(
        topic: String,
        key: String,
        payload: SpecificRecord,
        messageId: UUID? = UUID.randomUUID(),
        correlationId: UUID? = UUID.randomUUID(),
        causationId: UUID? = UUID.randomUUID(),
    ): ProducerRecord<String, SpecificRecord> =
        ProducerRecord(topic, key, payload).apply {
            headers().add("message-id", messageId.toString().toByteArray())
            headers().add("correlation-id", correlationId.toString().toByteArray())
            headers().add("causation-id", causationId.toString().toByteArray())
            headers().add("traceparent", "00-aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa-bbbbbbbbbbbbbbbb-01".toByteArray())
            headers().add("meta-attributes", """{"source":"test"}""".toByteArray())
        }

    companion object {
        @Container
        val kafkaContainer = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.1"))

        @Container
        val schemaRegistryContainer =
            GenericContainer(DockerImageName.parse("confluentinc/cp-schema-registry:7.5.1"))
                .apply {
                    withExposedPorts(8081)
                    withEnv("SCHEMA_REGISTRY_LISTENERS", "http://0.0.0.0:8081")
                }

        @Container
        val postgresContainer =
            PostgreSQLContainer<Nothing>("postgres:15.3").apply {
                withDatabaseName("testdb")
                withUsername("test")
                withPassword("test")
            }

        @JvmStatic
        @DynamicPropertySource
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.kafka.bootstrap-servers") { kafkaContainer.bootstrapServers }
            registry.add("spring.kafka.consumer.auto-offset-reset") { "earliest" }
            registry.add("kafka.producer.properties.schema.registry.url") {
                "http://${schemaRegistryContainer.host}:${schemaRegistryContainer.getMappedPort(8081)}"
            }
            registry.add("kafka.consumer.properties.schema.registry.url") {
                "http://${schemaRegistryContainer.host}:${schemaRegistryContainer.getMappedPort(8081)}"
            }

            registry.add("spring.datasource.url") { postgresContainer.jdbcUrl }
            registry.add("spring.datasource.username") { postgresContainer.username }
            registry.add("spring.datasource.password") { postgresContainer.password }
            registry.add("spring.datasource.driver-class-name") { postgresContainer.driverClassName }
            registry.add("spring.jpa.properties.hibernate.default_schema") { "testschema" }
            registry.add("spring.jpa.hibernate.ddl-auto") { "update" }
            registry.add("spring.flyway.schemas") { "testschema" }
        }
    }
}
