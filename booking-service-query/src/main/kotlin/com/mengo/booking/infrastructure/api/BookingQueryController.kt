package com.mengo.booking.infrastructure.api

import com.mengo.api.booking.BookingQueryApi
import com.mengo.api.booking.model.BookingRetrievedResponse
import com.mengo.booking.domain.service.QueryService
import com.mengo.booking.infrastructure.api.mappers.toApi
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class BookingQueryController(
    private val bookingService: QueryService,
) : BookingQueryApi {
    override fun getBookingById(bookingId: UUID): ResponseEntity<BookingRetrievedResponse?>? {
        val result = bookingService.findBookingById(bookingId)
        return ok(result.toApi())
    }
}
