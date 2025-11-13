package com.mengo.architecture.observability

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.tracing.Tracer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class MicrometerTelemetry(
    private val meterRegistry: MeterRegistry,
    private val tracer: Tracer,
) : Telemetry {
    // TODO: Reuse timers and counters
    private val logger = LoggerFactory.getLogger(MicrometerTelemetry::class.java)

    override fun recordSagaStarted(
        sagaName: String,
        sagaId: UUID,
    ) {
        Counter
            .builder("saga_instances_started_total")
            .tag("saga_name", sagaName)
            .register(meterRegistry)
            .increment()

        logger.info("SAGA started sagaName={} sagaId={}", sagaName, sagaId)

        tracer.currentSpan()?.tag("saga.name", sagaName)
        tracer.currentSpan()?.tag("saga.id", sagaId.toString())
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

    override fun recordSagaCompensated(
        sagaName: String,
        cause: String,
    ) {
        Counter
            .builder("saga_compensated_total")
            .tag("saga_name", sagaName)
            .tag("cause", cause)
            .register(meterRegistry)
            .increment()

        logger.warn("SAGA compensated sagaName={} cause={}", sagaName, cause)
        tracer.currentSpan()?.tag("saga.compensated", "true")
        tracer.currentSpan()?.tag("saga.compensation.cause", cause)
    }

    override fun logStateChange(
        sagaName: String,
        sagaId: UUID,
        step: String,
        state: String,
        cause: String?,
    ) {
        logger.info("SAGA state sagaName={} sagaId={} step={} state={} cause={}", sagaName, sagaId, step, state, cause ?: "-")
    }
}
