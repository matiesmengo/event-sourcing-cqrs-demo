package com.mengo.architecture.config

import com.mengo.architecture.observability.MicrometerTelemetry
import com.mengo.architecture.observability.ObservabilityProperties
import com.mengo.architecture.observability.ObservabilityStepAspect
import com.mengo.architecture.observability.Telemetry
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.tracing.Tracer
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ObservabilityProperties::class)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ConditionalOnClass(Tracer::class, MeterRegistry::class)
open class ObservabilityConfiguration(
    private val props: ObservabilityProperties,
) {
    @Bean
    fun metricsCommonTags(): MeterRegistryCustomizer<MeterRegistry> =
        MeterRegistryCustomizer { registry ->
            registry.config().commonTags("service", props.serviceName, "env", props.environment)
        }

    @Bean
    open fun telemetry(
        meterRegistry: MeterRegistry,
        tracer: Tracer,
    ): Telemetry = MicrometerTelemetry(meterRegistry, tracer)

    @Bean
    open fun observabilityStepAspect(
        meterRegistry: MeterRegistry,
        tracer: Tracer,
        telemetry: Telemetry,
    ): ObservabilityStepAspect = ObservabilityStepAspect(meterRegistry, tracer, telemetry)
}
