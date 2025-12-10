package com.mengo.orchestrator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication(scanBasePackages = ["com.mengo"])
@EnableJpaRepositories(basePackages = ["com.mengo"])
@EntityScan(basePackages = ["com.mengo"])
class BookingOrchestratorApplication

fun main(args: Array<String>) {
    runApplication<BookingOrchestratorApplication>(*args)
}
