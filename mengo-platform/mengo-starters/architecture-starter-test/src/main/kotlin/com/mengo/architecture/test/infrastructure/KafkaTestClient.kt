package com.mengo.architecture.test.infrastructure

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import java.time.Duration
import java.util.Properties
import java.util.UUID
import kotlin.test.fail

class KafkaTestClient(
    private val bootstrapServers: String,
    private val schemaRegistryUrl: String,
) {
    fun <T : SpecificRecord> consumeAndAssert(
        topic: String,
        timeout: Duration = Duration.ofSeconds(15),
        assertion: (ConsumerRecord<String, T>) -> Unit,
    ) {
        val props =
            Properties().apply {
                put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
                put(ConsumerConfig.GROUP_ID_CONFIG, "test-group-${UUID.randomUUID()}")
                put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
                put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
                put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer::class.java)

                put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl)
                put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true")
            }

        KafkaConsumer<String, T>(props).use { consumer ->
            consumer.subscribe(listOf(topic))
            val records = consumer.poll(timeout)

            if (records.isEmpty) {
                fail("No messages received from topic [$topic] after ${timeout.toSeconds()} seconds")
            }

            records.forEach { record ->
                assertion(record)
            }
        }
    }
}
