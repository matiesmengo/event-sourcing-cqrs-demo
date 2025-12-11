package com.mengo.architecture.outbox

import com.fasterxml.jackson.databind.ObjectMapper
import com.mengo.architecture.test.ContainerBase
import io.micrometer.core.instrument.MeterRegistry
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class OutboxDispatcherIntegrationTest
    @Autowired
    constructor(
        private val outboxDispatcher: OutboxDispatcher,
        private val outboxJpaRepository: OutboxJpaRepository,
        private val meterRegistry: MeterRegistry,
    ) : ContainerBase() {
        @Autowired
        lateinit var objectMapper: ObjectMapper

        companion object {
            const val TEST_TOPIC = "test.outbox.topic"
            const val TEST_KEY = "test-key"
            val CORRELATION_ID = UUID.randomUUID()

            val AVRO_PAYLOAD = TestSpecificRecord("A147")

            val VALID_PAYLOAD_JSON = AVRO_PAYLOAD.toString()
            val INVALID_PAYLOAD_JSON = """{"description":"Test Dispatch", "extra_field": 123}"""
        }

        @Test
        fun `should dispatch pending message to Kafka and mark as SENT`() {
            // given
            val entity = createPendingEntity(TEST_TOPIC, VALID_PAYLOAD_JSON, retries = 0)

            // when
            outboxDispatcher.dispatchBatch()

            // then
            kafkaConsumer.subscribe(listOf(TEST_TOPIC))
            val records = kafkaConsumer.poll(Duration.ofMillis(500))
            assertTrue(records.count() >= 1)

            assertTrue(records.count() >= 1)

            val record = records.records(TEST_TOPIC).first()
            assertEquals(TEST_KEY, record.key())
            assertEquals(VALID_PAYLOAD_JSON, record.value().toString())

            val correlationIdHeader = record.headers().lastHeader("correlation-id")
            assertNotNull(correlationIdHeader)

            val updatedEntity = outboxJpaRepository.findById(entity.id).get()
            assertEquals(OutboxStatus.SENT, updatedEntity.status)
            assertNotNull(updatedEntity.sentAt)
        }

        @Test
        fun `should move message to FAILED state after max retries due to payload deserialization error (DLQ)`() {
            // given
            val initialRetries = 4
            val entity = createPendingEntity(TEST_TOPIC, INVALID_PAYLOAD_JSON, retries = initialRetries)

            // when
            outboxDispatcher.dispatchBatch()

            // then
            kafkaConsumer.subscribe(listOf(TEST_TOPIC))
            val records = kafkaConsumer.poll(Duration.ofMillis(100))

            assertEquals(0, records.count())

            val failedEntity = outboxJpaRepository.findById(entity.id).get()
            assertEquals(OutboxStatus.FAILED, failedEntity.status)
            assertEquals(5, failedEntity.retries)

            val failedCount = meterRegistry.counter("outbox.messages.failed.total", "component", "outbox-dispatcher").count()
            assertTrue(failedCount > 0.0, "El comptador de FAILED ha d'haver-se incrementat (DLQ).")
        }

        private fun createPendingEntity(
            topic: String,
            payloadJson: String,
            retries: Int = 0,
        ): OutboxEntity {
            val headers = mapOf("correlation-id" to CORRELATION_ID.toString())

            return outboxJpaRepository.save(
                OutboxEntity(
                    topic = topic,
                    key = TEST_KEY,
                    payloadType = TestSpecificRecord::class.java.name,
                    payload = payloadJson,
                    status = OutboxStatus.PENDING,
                    retries = retries,
                    headers = objectMapper.writeValueAsString(headers),
                    createdAt = Instant.now(),
                ),
            )
        }
    }
