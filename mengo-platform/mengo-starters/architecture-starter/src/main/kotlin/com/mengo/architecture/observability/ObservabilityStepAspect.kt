package com.mengo.architecture.observability

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import io.micrometer.tracing.Tracer
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.MDC
import org.springframework.stereotype.Component
import java.util.UUID
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties

@Aspect
@Component
class ObservabilityStepAspect(
    private var meterRegistry: MeterRegistry? = null,
    private var tracer: Tracer? = null,
    private var telemetry: Telemetry? = null,
    private var traceMdcEnricher: TraceMdcEnricher? = null,
) {
    // TODO: Reuse timers and counters

    fun init(
        meterRegistry: MeterRegistry,
        tracer: Tracer,
        telemetry: Telemetry,
        traceMdcEnricher: TraceMdcEnricher,
    ) {
        this.meterRegistry = meterRegistry
        this.tracer = tracer
        this.telemetry = telemetry
        this.traceMdcEnricher = traceMdcEnricher
    }

    @Around("@annotation(step)")
    fun aroundSagaStep(
        pjp: ProceedingJoinPoint,
        step: ObservabilityStep,
    ): Any? {
        traceMdcEnricher?.enrich()
        val sagaId = extractSagaId(pjp.args) ?: UUID.randomUUID()
        MDC.put("sagaId", sagaId.toString())

        telemetry?.logStateChange(step.sagaName, sagaId, step.name, "STARTED")

        val timerSample = Timer.start(meterRegistry!!)
        return try {
            val result = pjp.proceed()

            timerSample.stop(
                Timer
                    .builder("saga_step_duration_seconds")
                    .tag("saga_name", step.sagaName)
                    .tag("step", step.name)
                    .publishPercentileHistogram()
                    .publishPercentiles(0.5, 0.95, 0.99)
                    .register(meterRegistry!!),
            )

            telemetry?.recordSagaStepSuccess(step.sagaName, step.name)
            telemetry?.logStateChange(step.sagaName, sagaId, step.name, "SUCCESS")

            result
        } catch (ex: Throwable) {
            timerSample.stop(
                Timer
                    .builder("saga_step_duration_seconds")
                    .tag("saga_name", step.sagaName)
                    .tag("step", step.name)
                    .register(meterRegistry!!),
            )

            telemetry?.recordSagaStepFailure(step.sagaName, step.name, ex.message)
            telemetry?.logStateChange(step.sagaName, sagaId, step.name, "FAILED", ex.message)

            tracer?.currentSpan()?.error(ex)
            throw ex
        } finally {
            MDC.remove("sagaId")
        }
    }

    private fun extractSagaId(args: Array<Any?>): UUID? {
        args.forEach { arg ->
            if (arg == null) return@forEach
            val prop =
                arg::class.declaredMemberProperties.firstOrNull {
                    it.name in listOf("sagaId", "bookingId", "aggregateId")
                } as? KProperty1<Any, *>
            prop?.let {
                try {
                    val value = it.get(arg)
                    return when (value) {
                        is UUID -> value
                        is String ->
                            try {
                                UUID.fromString(value)
                            } catch (_: Exception) {
                                null
                            }
                        else -> null
                    }
                } catch (_: Exception) {
                    // ignore
                }
            }
        }
        return null
    }
}
