package com.mengo.architecture.observability

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import io.micrometer.tracing.Tracer
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.MDC

@Aspect
class ObservabilityStepAspect(
    private val meterRegistry: MeterRegistry,
    private val tracer: Tracer,
    private val telemetry: Telemetry,
) {
    @Around("@annotation(step)")
    fun aroundSagaStep(
        pjp: ProceedingJoinPoint,
        step: ObservabilityStep,
    ): Any? {
        val currentTraceId = tracer.currentSpan()?.context()?.traceId() ?: "no-trace"
        MDC.put("traceId", currentTraceId)

        telemetry.logStateChange(step.sagaName, step.name, "STARTED")
        val timerSample = Timer.start(meterRegistry)

        return try {
            val result = pjp.proceed()
            stopTimer(timerSample, step, "SUCCESS")
            telemetry.recordSagaStepSuccess(step.sagaName, step.name)
            result
        } catch (ex: Throwable) {
            stopTimer(timerSample, step, "FAILED")
            telemetry.recordSagaStepFailure(step.sagaName, step.name, ex.message)
            tracer.currentSpan()?.error(ex)
            throw ex
        } finally {
            MDC.remove("sagaId")
        }
    }

    private fun stopTimer(
        sample: Timer.Sample,
        step: ObservabilityStep,
        status: String,
    ) {
        sample.stop(
            Timer
                .builder("saga_step_duration_seconds")
                .tag("saga_name", step.sagaName)
                .tag("step", step.name)
                .tag("status", status)
                .publishPercentileHistogram()
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry),
        )
    }
}
