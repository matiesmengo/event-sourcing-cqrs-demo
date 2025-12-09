package com.mengo.architecture.metadata

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.producer.ProducerInterceptor
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.header.Headers
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class MetadataProducerInterceptor : ProducerInterceptor<String, Any> {
    private lateinit var objectMapper: ObjectMapper

    // TODO: migrate to outbox
    override fun configure(configs: MutableMap<String, *>?) {
        objectMapper = ObjectMapper().findAndRegisterModules()
    }

    override fun onSend(record: ProducerRecord<String, Any>): ProducerRecord<String, Any> {
        val metadata = MetadataContextHolder.get() ?: error("MetadataContextHolder is lost")

        record.headers().setHeader("message-id", UUID.randomUUID().toString())
        record.headers().setHeader("correlation-id", metadata.correlationId.toString())
        metadata.causationId?.let { record.headers().setHeader("causation-id", it.toString()) }
        record.headers().setHeader("meta-attributes", objectMapper.writeValueAsString(metadata.attributes))
        return record
    }

    override fun onAcknowledgement(
        metadata: RecordMetadata?,
        exception: Exception?,
    ) = Unit

    override fun close() = Unit

    fun Headers.setHeader(
        key: String,
        value: String,
    ) {
        remove(key)
        add(key, value.toByteArray())
    }
}
