package com.mengo.booking.infrastructure.events

import com.mengo.booking.application.BookingServiceAdapter
import com.mengo.booking.fixtures.BookingConstants.BOOKING_ID
import com.mengo.booking.fixtures.BookingConstants.PAYMENT_ID
import com.mengo.payment.events.PaymentCompletedEvent
import com.mengo.payment.events.PaymentFailedEvent
import org.junit.jupiter.api.Test
import org.mockito.kotlin.check
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.test.assertEquals

class PaymentKafkaListenerTest {
    private val bookingServiceAdapter: BookingServiceAdapter = mock()
    private val listener = PaymentKafkaListener(bookingServiceAdapter)

    @Test
    fun `should call bookingService on consumePaymentCompletedEvent`() {
        // given
        val payload = PaymentCompletedEvent(PAYMENT_ID.toString(), BOOKING_ID.toString(), "ref-123")

        // when
        listener.consumePaymentCompletedEvent(payload)

        // then
        verify(bookingServiceAdapter).onPaymentCompleted(
            check { assertEquals(PAYMENT_ID, it.paymentId) },
        )
    }

    @Test
    fun `should call bookingService on consumePaymentFailedEvent`() {
        // given
        val payload = PaymentFailedEvent(PAYMENT_ID.toString(), BOOKING_ID.toString(), "reason")

        // when
        listener.consumePaymentFailedEvent(payload)

        // then
        verify(bookingServiceAdapter).onPaymentFailed(
            check { assertEquals(PAYMENT_ID, it.paymentId) },
        )
    }
}
