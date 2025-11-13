package com.mengo.architecture.config

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener

@AutoConfiguration
class LoggerConfiguration {
    private val log = LoggerFactory.getLogger(javaClass)

    // TODO: Custom lancher message

    @EventListener(ApplicationReadyEvent::class)
    fun logStartup() {
        val appName = System.getProperty("spring.application.name") ?: "unknown"
        val logPath = System.getenv("LOG_PATH") ?: "./observability/temp/$appName.log"
        log.info("Observability starter configured for service '{}', log file: {}", appName, logPath)
    }
}
