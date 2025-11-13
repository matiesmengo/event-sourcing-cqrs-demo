package com.mengo.architecture.config

import com.mengo.architecture.observability.ObservabilityProperties
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ObservabilityProperties::class)
class MeterConfiguration(
    private val props: ObservabilityProperties,
) {
    @Bean
    fun metricsCommonTags(): MeterRegistryCustomizer<MeterRegistry> =
        MeterRegistryCustomizer { registry ->
            registry.config().commonTags(
                "service",
                props.serviceName,
                "env",
                props.environment,
            )
        }
}
