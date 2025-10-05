package com.mengo.e2e.clients

import com.mengo.booking.model.BookingResponse
import com.mengo.booking.model.CreateBookingRequest
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(
    name = "BookingServiceClient",
    url = "\${booking-service-url}",
)
interface BookingFeignClient {
    @PostMapping("/bookings", consumes = ["application/json"], produces = ["application/json"])
    fun bookingsPost(
        @RequestBody createBookingRequest: CreateBookingRequest,
    ): BookingResponse
}
