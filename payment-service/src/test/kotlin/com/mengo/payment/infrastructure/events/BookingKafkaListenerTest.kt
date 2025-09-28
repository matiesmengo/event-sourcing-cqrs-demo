package com.mengo.payment.infrastructure.events

import com.mengo.booking.events.BookingCreatedEvent
import com.mengo.payment.application.PaymentService
import com.mengo.payment.fixtures.PaymentConstants.BOOKING_ID
import java.util.UUID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.check
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class BookingKafkaListenerTest {
    private val paymentService: PaymentService = mock()
    private val listener = BookingKafkaListener(paymentService)

    @Test
    fun `should call paymentService on consumeBookingCreatedEvent`() {
        // given
        val payload =
            BookingCreatedEvent(
                BOOKING_ID.toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
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
