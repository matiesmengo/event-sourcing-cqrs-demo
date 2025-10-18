package com.mengo.orchestrator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BookingOrchestratorApplication

fun main(args: Array<String>) {
    runApplication<BookingOrchestratorApplication>(*args)
}
