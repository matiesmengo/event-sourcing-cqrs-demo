package com.mengo.architecture.config

import com.mengo.architecture.observability.MicrometerTelemetry
import com.mengo.architecture.observability.ObservabilityStepAspect
import com.mengo.architecture.observability.Telemetry
import com.mengo.architecture.observability.TraceMdcEnricher
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.tracing.Tracer
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy

@Configuration(proxyBeanMethods = false)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ConditionalOnClass(Tracer::class, MeterRegistry::class)
open class ObservabilityConfiguration(
    private val meterRegistry: MeterRegistry,
    private val tracer: Tracer,
) {
    @Bean
    open fun traceMdcEnricher(): TraceMdcEnricher = TraceMdcEnricher(tracer)

    @Bean
    open fun telemetry(): Telemetry = MicrometerTelemetry(meterRegistry, tracer)

    @Bean
    open fun observabilityStepAspect(
        telemetry: Telemetry,
        traceMdcEnricher: TraceMdcEnricher,
    ): ObservabilityStepAspect = ObservabilityStepAspect(meterRegistry, tracer, telemetry, traceMdcEnricher)
}
