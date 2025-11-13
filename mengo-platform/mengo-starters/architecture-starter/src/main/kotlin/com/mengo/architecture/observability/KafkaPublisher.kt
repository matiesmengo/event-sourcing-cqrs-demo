package com.mengo.architecture.observability

import io.micrometer.observation.Observation
import io.micrometer.observation.ObservationRegistry
import io.micrometer.tracing.Tracer
import io.opentelemetry.api.baggage.Baggage
import org.springframework.kafka.core.KafkaTemplate

class KafkaPublisher(
    private val kafkaTemplate: KafkaTemplate<String, Any>,
    private val observationRegistry: ObservationRegistry,
    private val tracer: Tracer?,
) {
    fun send(
        topic: String,
        payload: Any,
        sagaId: String? = null,
    ) {
        val sagaIdToUse = sagaId ?: (Baggage.current().getEntryValue("saga.id") ?: "UNKNOWN_SAGA_ID")

        val observation =
            Observation
                .createNotStarted("kafka.producer", observationRegistry)
                .contextualName("kafka.produce.$topic")
                .lowCardinalityKeyValue("topic", topic)
                .lowCardinalityKeyValue("saga.id", sagaIdToUse)
                .start()

        val span =
            tracer
                ?.nextSpan()
                ?.name("kafka.produce.$topic")
                ?.tag("saga.id", sagaIdToUse)
                ?.start()

        try {
            kafkaTemplate.send(topic, sagaIdToUse, payload)
        } finally {
            observation.stop()
            span?.end()
        }
    }
}
