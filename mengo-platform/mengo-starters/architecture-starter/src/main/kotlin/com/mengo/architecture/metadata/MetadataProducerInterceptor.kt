package com.mengo.architecture.metadata

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.producer.ProducerInterceptor
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class MetadataProducerInterceptor : ProducerInterceptor<String, Any> {
    private lateinit var objectMapper: ObjectMapper

    override fun configure(configs: MutableMap<String, *>?) {
        objectMapper = ObjectMapper().findAndRegisterModules()
    }

    override fun onSend(record: ProducerRecord<String, Any>): ProducerRecord<String, Any> {
        val metadata = MetadataContextHolder.get()

        val correlationId = metadata?.correlationId ?: UUID.randomUUID().toString()
        record.headers().remove("correlation-id")
        record.headers().add("correlation-id", correlationId.toByteArray())

        val causationId = UUID.randomUUID().toString()
        record.headers().remove("causation-id")
        record.headers().add("causation-id", causationId.toByteArray())

        val attributesJson = objectMapper.writeValueAsString(metadata?.attributes)
        record.headers().remove("meta-attributes")
        record.headers().add("meta-attributes", attributesJson.toByteArray())

        return record
    }

    override fun onAcknowledgement(
        metadata: RecordMetadata?,
        exception: Exception?,
    ) = Unit

    override fun close() = Unit
}
