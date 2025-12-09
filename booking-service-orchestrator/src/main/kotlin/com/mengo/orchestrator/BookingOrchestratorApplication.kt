package com.mengo.orchestrator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class BookingOrchestratorApplication

fun main(args: Array<String>) {
    runApplication<BookingOrchestratorApplication>(*args)
}
