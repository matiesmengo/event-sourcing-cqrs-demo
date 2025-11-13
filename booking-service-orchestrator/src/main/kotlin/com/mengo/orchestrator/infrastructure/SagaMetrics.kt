package com.mengo.orchestrator.infrastructure

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.stereotype.Component

@Component
class SagaMetrics(
    private val meterRegistry: MeterRegistry,
) {
    private val sagaStarted: Counter =
        Counter
            .builder("saga_started_total")
            .description("Total number of SAGA orchestrations started")
            .tags("service", "orchestrator-service")
            .register(meterRegistry)

    private val sagaCompleted: Counter =
        Counter
            .builder("saga_completed_total")
            .description("Total number of successfully completed SAGA orchestrations")
            .tag("service", "orchestrator-service")
            .register(meterRegistry)

    private val sagaFailed: Counter =
        Counter
            .builder("saga_failed_total")
            .description("Total number of failed or compensated SAGA orchestrations")
            .tag("service", "orchestrator-service")
            .register(meterRegistry)

    fun incrementStarted(sagaName: String) {
        sagaStarted.increment()
    }
    // TODO: useless input

    fun incrementCompleted(sagaName: String) {
        sagaCompleted.increment()
    }

    fun incrementFailed(sagaName: String) {
        sagaFailed.increment()
    }
}
