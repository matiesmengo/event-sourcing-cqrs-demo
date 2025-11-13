package com.mengo.architecture.observability

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "observability")
data class ObservabilityProperties(
    var serviceName: String = "unknown-service",
    var environment: String = "local",
)
