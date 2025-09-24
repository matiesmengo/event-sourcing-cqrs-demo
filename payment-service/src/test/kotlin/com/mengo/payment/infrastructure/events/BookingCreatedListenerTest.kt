package com.mengo.payment.infrastructure.events

import com.mengo.booking.events.BookingCreatedEvent
import com.mengo.payment.application.PaymentService
import com.mengo.payment.fixtures.PaymentConstants.BOOKING_ID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.check
import org.mockito.kotlin.verify
import java.time.Instant
import java.util.UUID

class BookingCreatedListenerTest {
    private val paymentService: PaymentService = mock()
    private val listener = BookingCreatedListener(paymentService)

    @Test
    fun `should call paymentService on consumeBookingCreatedEvent`() {
        // given
        val payload =
            BookingCreatedEvent(
                BOOKING_ID.toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                "CREATED",
                Instant.now(),
            )

        // when
        listener.consumeBookingCreatedEvent(payload)

        // then
        verify(paymentService).onBookingCreated(
            check {
                assertEquals(BOOKING_ID, it.bookingId)
            },
        )
    }
}
