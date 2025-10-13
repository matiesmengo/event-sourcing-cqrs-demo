package com.mengo.booking.infrastructure.events

import com.mengo.booking.application.BookingServiceCommand
import com.mengo.booking.fixtures.BookingConstants.PAYMENT_ID
import com.mengo.booking.fixtures.PayloadTestData.buildPaymentCompletedPayload
import com.mengo.booking.fixtures.PayloadTestData.buildPaymentFailedPayload
import org.junit.jupiter.api.Test
import org.mockito.kotlin.check
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.test.assertEquals

class PaymentKafkaListenerTest {
    private val bookingServiceCommand: BookingServiceCommand = mock()
    private val listener = PaymentKafkaListener(bookingServiceCommand)

    @Test
    fun `should call bookingService on consumePaymentCompletedEvent`() {
        // given
        val payload = buildPaymentCompletedPayload()

        // when
        listener.consumePaymentCompletedEvent(payload)

        // then
        verify(bookingServiceCommand).onPaymentCompleted(
            check { assertEquals(PAYMENT_ID, it.paymentId) },
        )
    }

    @Test
    fun `should call bookingService on consumePaymentFailedEvent`() {
        // given
        val payload = buildPaymentFailedPayload()

        // when
        listener.consumePaymentFailedEvent(payload)

        // then
        verify(bookingServiceCommand).onPaymentFailed(
            check { assertEquals(PAYMENT_ID, it.paymentId) },
        )
    }
}
