package com.mengo.e2e.infrastructure

import com.mengo.e2e.infrastructure.AbstractInfrastructureE2ETest.Companion.schemaRegistry
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import java.time.Duration
import java.util.Properties
import java.util.UUID
import kotlin.test.fail

class KafkaTestConsumer(
    private val bootstrapServers: String,
) {
    fun consumeAndAssert(
        topic: String,
        assertion: (ConsumerRecord<String, SpecificRecord>) -> Unit,
    ) {
        val props =
            Properties().apply {
                put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
                put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-${UUID.randomUUID()}")
                put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
                put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
                put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer::class.java)
                put("schema.registry.url", "http://${schemaRegistry.host}:${schemaRegistry.getMappedPort(8081)}")
                put("specific.avro.reader", "true")
            }

        KafkaConsumer<String, SpecificRecord>(props).use { consumer ->
            consumer.subscribe(listOf(topic))
            val records = consumer.poll(Duration.ofSeconds(20))
            if (records.isEmpty) {
                fail("No messages received from topic: $topic")
            }
            records.forEach { record -> assertion(record) }
        }
    }
}
