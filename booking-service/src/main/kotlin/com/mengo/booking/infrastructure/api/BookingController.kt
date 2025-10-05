package com.mengo.booking.infrastructure.api

import com.mengo.booking.api.BookingsApi
import com.mengo.booking.domain.service.BookingService
import com.mengo.booking.infrastructure.api.mappers.toApi
import com.mengo.booking.infrastructure.api.mappers.toDomain
import com.mengo.booking.model.BookingResponse
import com.mengo.booking.model.CreateBookingRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.RestController

@RestController
class BookingController(
    private val bookingService: BookingService,
) : BookingsApi {
    override fun bookingsPost(createBookingRequest: @Valid CreateBookingRequest): ResponseEntity<BookingResponse>? {
        val response = bookingService.createBooking(createBookingRequest.toDomain())
        return ok(response.toApi())
    }
}
