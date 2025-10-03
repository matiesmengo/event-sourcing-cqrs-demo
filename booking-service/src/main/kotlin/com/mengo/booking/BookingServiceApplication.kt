package com.mengo.booking

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class BookingServiceApplication

fun main(args: Array<String>) {
    runApplication<BookingServiceApplication>(*args)
}
