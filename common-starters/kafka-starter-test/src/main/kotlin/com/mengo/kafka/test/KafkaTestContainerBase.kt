package com.mengo.kafka.test

import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@SpringBootTest
@Testcontainers
@ActiveProfiles("integration-kafka")
abstract class KafkaTestContainerBase {
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

        @JvmStatic
        @DynamicPropertySource
        fun kafkaProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.kafka.bootstrap-servers") { kafkaContainer.bootstrapServers }
            registry.add("kafka.producer.properties.schema.registry.url") {
                "http://${schemaRegistryContainer.host}:${schemaRegistryContainer.getMappedPort(8081)}"
            }
            registry.add("kafka.consumer.properties.schema.registry.url") {
                "http://${schemaRegistryContainer.host}:${schemaRegistryContainer.getMappedPort(8081)}"
            }
        }
    }
}