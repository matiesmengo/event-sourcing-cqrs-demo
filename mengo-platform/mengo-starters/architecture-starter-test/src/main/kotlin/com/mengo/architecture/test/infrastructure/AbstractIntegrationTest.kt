package com.mengo.architecture.test.infrastructure

import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.ProducerRecord
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.KafkaTemplate
import java.util.UUID

@SpringBootTest
abstract class AbstractIntegrationTest : AbstractInfrastructureIntegrationTest() {
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
}
