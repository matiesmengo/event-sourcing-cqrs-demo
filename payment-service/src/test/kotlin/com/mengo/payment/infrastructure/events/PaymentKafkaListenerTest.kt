package com.mengo.payment.infrastructure.events

import com.mengo.payment.application.PaymentServiceCommand
import com.mengo.payment.fixtures.PayloadTestData.buildOrchestratorRequestPaymentPayload
import com.mengo.payment.fixtures.PaymentConstants.BOOKING_ID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.check
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.math.BigDecimal

class PaymentKafkaListenerTest {
    private val paymentServiceCommand: PaymentServiceCommand = mock()
    private val listener = PaymentKafkaListener(paymentServiceCommand)

    @Test
    fun `should consume OrchestratorRequestPaymentPayload and call payment service`() {
        // given
        val payload = buildOrchestratorRequestPaymentPayload()

        // when
        listener.onRequestPayment(payload)

        // then
        verify(paymentServiceCommand).onRequestPayment(
            check {
                assertEquals(BOOKING_ID, it.bookingId)
                assertTrue(it.totalPrice.compareTo(BigDecimal.TEN) == 0)
            },
        )
    }
}
