package com.mengo.booking

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.mengo"])
@EntityScan(basePackages = ["com.mengo"])
open class BookingQueryApplication

fun main(args: Array<String>) {
    runApplication<BookingQueryApplication>(*args)
}
