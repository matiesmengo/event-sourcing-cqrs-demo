package com.mengo.architecture.outbox

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.mengo.architecture.metadata.Metadata
import com.mengo.architecture.metadata.MetadataContextHolder
import com.mengo.architecture.test.ContainerBase
import org.apache.avro.Schema
import org.apache.avro.specific.SpecificRecord
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.InvalidDataAccessApiUsageException
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class OutboxRepositoryServiceIntegrationTest
    @Autowired
    constructor(
        private val outboxRepositoryService: OutboxRepositoryService,
        private val outboxJpaRepository: OutboxJpaRepository,
    ) : ContainerBase() {
        @Autowired
        lateinit var objectMapper: ObjectMapper

        companion object {
            const val TEST_TOPIC = "test.outbox.topic"
            const val TEST_KEY = "test-key"
            val CORRELATION_ID = UUID.randomUUID()
            val CAUSATION_ID = UUID.randomUUID()

            val AVRO_PAYLOAD = TestSpecificRecord("B123")
        }

        @Test
        fun `should persist OutboxEntity with correct mapping and metadata headers`() {
            // given
            val metadata = Metadata(correlationId = CORRELATION_ID, causationId = CAUSATION_ID)
            MetadataContextHolder.set(metadata)

            // when
            outboxRepositoryService.persistOutboxEvent(topic = TEST_TOPIC, key = TEST_KEY, payloadJson = AVRO_PAYLOAD)

            // then
            val entities = outboxJpaRepository.findAll()
            assertTrue(entities.isNotEmpty())
            val savedEntity = entities.first()

            assertEquals(TEST_TOPIC, savedEntity.topic)
            assertEquals(TEST_KEY, savedEntity.key)
            assertEquals(AVRO_PAYLOAD::class.java.name, savedEntity.payloadType)
            assertEquals(AVRO_PAYLOAD.toString(), savedEntity.payload)

            val actualHeaders: Map<String, String> =
                savedEntity.headers?.let {
                    objectMapper.readValue(it, object : TypeReference<Map<String, String>>() {})
                } ?: emptyMap()

            assertNotNull(actualHeaders["message-id"])
            assertEquals(CORRELATION_ID.toString(), actualHeaders["correlation-id"])
            assertEquals(CAUSATION_ID.toString(), actualHeaders["causation-id"])

            MetadataContextHolder.clear()
        }

        @Test
        fun `should throw error and rollback if MetadataContextHolder is lost`() {
            // given
            // when
            assertThrows<InvalidDataAccessApiUsageException> {
                outboxRepositoryService.persistOutboxEvent(topic = TEST_TOPIC, key = TEST_KEY, payloadJson = AVRO_PAYLOAD)
            }

            // then
            val countAfterFailure = outboxJpaRepository.count()
            assertEquals(0, countAfterFailure)
        }
    }

data class TestSpecificRecord(
    val id: String,
) : SpecificRecord {
    override fun toString(): String = """{"id": "$id"}"""

    override fun getSchema(): Schema = Schema.create(Schema.Type.STRING)

    override fun get(i: Int): Any? = error("Not needed for Outbox test")

    override fun put(
        i: Int,
        v: Any?,
    ) {
    }
}
