package com.mengo.architecture.observability

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.tracing.Tracer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class MicrometerTelemetry(
    private val meterRegistry: MeterRegistry,
    private val tracer: Tracer,
) : Telemetry {
    // TODO: Reuse timers and counters
    private val logger = LoggerFactory.getLogger(MicrometerTelemetry::class.java)

    init {
        val defaultSagas = listOf("booking_saga")
        defaultSagas.forEach { name ->
            Counter
                .builder("saga_instances_started_total")
                .tag("saga_name", name)
                .register(meterRegistry)

            Counter
                .builder("saga_completed_total")
                .tag("saga_name", name)
                .register(meterRegistry)

            Counter
                .builder("saga_compensated_total")
                .tag("saga_name", name)
                .register(meterRegistry)
        }
    }

    override fun recordSagaStarted(sagaName: String) {
        Counter
            .builder("saga_instances_started_total")
            .tag("saga_name", sagaName)
            .register(meterRegistry)
            .increment()

        logger.info("SAGA started sagaName={}", sagaName)

        tracer.currentSpan()?.apply {
            tag("saga.name", sagaName)
        }
    }

    override fun recordSagaStepSuccess(
        sagaName: String,
        step: String,
    ) {
        Counter
            .builder("saga_step_success_total")
            .tag("saga_name", sagaName)
            .tag("step", step)
            .register(meterRegistry)
            .increment()

        tracer.currentSpan()?.tag("saga.step", step)
    }

    override fun recordSagaStepFailure(
        sagaName: String,
        step: String,
        cause: String?,
    ) {
        Counter
            .builder("saga_step_failure_total")
            .tag("saga_name", sagaName)
            .tag("step", step)
            .tag("cause", cause ?: "unknown")
            .register(meterRegistry)
            .increment()

        logger.error("SAGA error sagaName={} step={} cause={}", sagaName, step, cause)
        tracer.currentSpan()?.tag("saga.step", step)
        tracer.currentSpan()?.tag("saga.step.error", cause ?: "")
    }

    override fun recordSagaCompleted(sagaName: String) {
        Counter
            .builder("saga_completed_total")
            .tag("saga_name", sagaName)
            .register(meterRegistry)
            .increment()
    }

    override fun logStateChange(
        sagaName: String,
        step: String,
        state: String,
    ) {
        logger.info("Tracer sagaName={} step={} state={}", sagaName, step, state)
    }

    override fun recordSagaCompensated(sagaName: String) {
        Counter
            .builder("saga_compensated_total")
            .tag("saga_name", sagaName)
            .register(meterRegistry)
            .increment()

        logger.warn("SAGA compensated sagaName={}", sagaName)
        tracer.currentSpan()?.tag("saga.compensated", "true")
    }
}
