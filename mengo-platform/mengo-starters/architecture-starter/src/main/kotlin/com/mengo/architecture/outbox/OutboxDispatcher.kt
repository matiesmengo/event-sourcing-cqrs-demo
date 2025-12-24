package com.mengo.architecture.outbox

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.transaction.annotation.Transactional
import java.nio.charset.StandardCharsets
import java.time.Instant

open class OutboxDispatcher(
    private val repository: OutboxJpaRepository,
    private val kafkaTemplate: KafkaTemplate<String, SpecificRecord>,
    private val objectMapper: ObjectMapper,
    private val meterRegistry: MeterRegistry,
    @Value("\${outbox.dispatcher.max-retries:5}")
    private val maxRetries: Int = 5,
) {
    private val logger = LoggerFactory.getLogger(OutboxDispatcher::class.java)

    private val failedMessageCounter =
        Counter
            .builder("outbox.messages.failed.total")
            .description("Total messages that have gone to FAILED status (DLQ)")
            .tag("component", "outbox-dispatcher")
            .register(meterRegistry)

    @Scheduled(fixedDelay = 500)
    fun dispatch() {
        dispatchBatch()
    }

    @Transactional
    open fun dispatchBatch() {
        val batch = repository.findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING)
        if (batch.isEmpty()) return

        val entitiesToSave = mutableListOf<OutboxEntity>()
        batch.forEach { entity ->

            if (publishKafkaEvent(entity)) {
                entity.status = OutboxStatus.SENT
                entity.sentAt = Instant.now()
            } else {
                // TODO: Backoff
                entity.retries += 1
                if (entity.retries >= maxRetries) {
                    entity.status = OutboxStatus.FAILED
                    failedMessageCounter.increment()
                    logger.error("Message ID ${entity.id} moved to FAILED status after ${entity.retries} retries.")
                }
            }
            entitiesToSave.add(entity)
        }

        repository.saveAll(entitiesToSave)
    }

    private fun publishKafkaEvent(entity: OutboxEntity): Boolean =
        try {
            val headersMap: Map<String, String> =
                entity.headers?.let {
                    objectMapper.readValue(it, object : TypeReference<Map<String, String>>() {})
                } ?: emptyMap()

            val payloadClass = Class.forName(entity.payloadType)
            val payloadObj: Any = objectMapper.readValue(entity.payload, payloadClass)

            val record: ProducerRecord<String, SpecificRecord> =
                ProducerRecord<String, SpecificRecord>(
                    entity.topic,
                    entity.key,
                    payloadObj as SpecificRecord,
                ).apply {
                    headersMap.forEach { (k, v) ->
                        headers().add(k, v.toByteArray(StandardCharsets.UTF_8))
                    }
                }

            kafkaTemplate.send(record).get()
            true
        } catch (ex: Exception) {
            logger.warn(("Error dispatching message ${entity.id}: ${ex.message}"))
            false
        }
}
