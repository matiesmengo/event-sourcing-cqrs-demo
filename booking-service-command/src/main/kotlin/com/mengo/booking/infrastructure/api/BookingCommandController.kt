package com.mengo.booking.infrastructure.api

import com.mengo.api.booking.BookingCommandApi
import com.mengo.api.booking.model.BookingResponse
import com.mengo.api.booking.model.CreateBookingRequest
import com.mengo.booking.domain.service.BookingService
import com.mengo.booking.infrastructure.api.mappers.toApi
import com.mengo.booking.infrastructure.api.mappers.toDomain
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.RestController

@RestController
class BookingCommandController(
    private val bookingService: BookingService,
) : BookingCommandApi {
    override fun createBooking(createBookingRequest: @Valid CreateBookingRequest): ResponseEntity<BookingResponse>? {
        val bookingDomain = createBookingRequest.toDomain()
        bookingService.onCreateBooking(bookingDomain)
        return ok(bookingDomain.toApi())
    }
}
