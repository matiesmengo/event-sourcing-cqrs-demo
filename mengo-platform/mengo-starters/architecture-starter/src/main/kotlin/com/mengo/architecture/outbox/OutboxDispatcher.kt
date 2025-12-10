package com.mengo.architecture.outbox

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.time.Instant

@Component
class OutboxDispatcher(
    private val repository: OutboxJpaRepository,
    private val kafkaTemplate: KafkaTemplate<String, Any>,
    private val objectMapper: ObjectMapper,
) {
    private val logger = LoggerFactory.getLogger(OutboxDispatcher::class.java)

    // TODO: Test
    // TODO: Transactional
    @Scheduled(fixedDelay = 500)
    fun dispatch() {
        val batch = repository.findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING)
        if (batch.isEmpty()) {
            return
        }

        batch.forEach { entity ->
            try {
                val headersMap: Map<String, String> =
                    entity.headers?.let {
                        objectMapper.readValue(it, object : TypeReference<Map<String, String>>() {})
                    } ?: emptyMap()

                val payloadClass = Class.forName(entity.payloadType)
                val payloadObj: Any = objectMapper.readValue(entity.payload, payloadClass)

                val record =
                    ProducerRecord<String, Any>(
                        entity.topic,
                        entity.key,
                        payloadObj,
                    ).apply {
                        headersMap.forEach { (k, v) ->
                            headers().add(k, v.toByteArray(StandardCharsets.UTF_8))
                        }
                    }

                kafkaTemplate.send(record).get()

                entity.status = OutboxStatus.SENT
                entity.sentAt = Instant.now()
            } catch (ex: Exception) {
                logger.warn(("Error dispatching message ${entity.id}: ${ex.message}"))

                // TODO: Retry + DLQ
                entity.retries += 1
                if (entity.retries >= 8) {
                    entity.status = OutboxStatus.FAILED
                }
            }
        }

        repository.saveAll(batch)
    }
}
