package com.mengo.orchestrator.infrastructure.events

import com.mengo.orchestrator.application.OrchestratorServiceCommand
import com.mengo.orchestrator.fixtures.OrchestratorConstants.BOOKING_ID
import com.mengo.orchestrator.fixtures.OrchestratorConstants.PAYMENT_ID
import com.mengo.orchestrator.fixtures.OrchestratorConstants.PAYMENT_REASON
import com.mengo.orchestrator.fixtures.OrchestratorConstants.PAYMENT_REFERENCE
import com.mengo.orchestrator.fixtures.OrchestratorConstants.PRODUCT_ID
import com.mengo.orchestrator.fixtures.OrchestratorConstants.PRODUCT_PRICE
import com.mengo.orchestrator.fixtures.OrchestratorConstants.PRODUCT_QUANTITY
import com.mengo.orchestrator.fixtures.PayloadTestData.buildBookingCreatedPayload
import com.mengo.orchestrator.fixtures.PayloadTestData.buildPaymentCompletedPayload
import com.mengo.orchestrator.fixtures.PayloadTestData.buildPaymentFailedPayload
import com.mengo.orchestrator.fixtures.PayloadTestData.buildProductReservationFailedPayload
import com.mengo.orchestrator.fixtures.PayloadTestData.buildProductReservedPayload
import org.mockito.kotlin.check
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OrchestratorKafkaListenerTest {
    private val serviceCommand: OrchestratorServiceCommand = mock()
    private val listener = OrchestratorKafkaListener(serviceCommand)

    @Test
    fun `onBookingCreated should call bookingService`() {
        // given
        val payload = buildBookingCreatedPayload()

        // when
        listener.onBookingCreated(payload)

        // then
        verify(serviceCommand).handleBookingCreated(
            check {
                assertEquals(BOOKING_ID, it.bookingId)
                assertTrue(it.products.any { product -> product.productId == PRODUCT_ID })
                assertTrue(it.products.any { product -> product.quantity == PRODUCT_QUANTITY })
            },
        )
    }

    @Test
    fun `onProductReserved should call bookingService`() {
        // given
        val payload = buildProductReservedPayload()

        // when
        listener.onProductReserved(payload)

        // then
        verify(serviceCommand).handleProductReserved(
            check {
                assertEquals(BOOKING_ID, it.bookingId)
                assertEquals(PRODUCT_ID, it.productId)
                assertEquals(PRODUCT_QUANTITY, it.quantity)
                assertEquals(PRODUCT_PRICE, it.price)
            },
        )
    }

    @Test
    fun `onProductReservationFailed should call bookingService`() {
        // given
        val payload = buildProductReservationFailedPayload()

        // when
        listener.onProductReservationFailed(payload)

        // then
        verify(serviceCommand).handleProductReservationFailed(
            check {
                assertEquals(BOOKING_ID, it.bookingId)
                assertEquals(PRODUCT_ID, it.productId)
            },
        )
    }

    @Test
    fun `onPaymentCompleted should call bookingService`() {
        // given
        val payload = buildPaymentCompletedPayload()

        // when
        listener.onPaymentCompleted(payload)

        // then
        verify(serviceCommand).handlePaymentCompleted(
            check {
                assertEquals(BOOKING_ID, it.bookingId)
                assertEquals(PAYMENT_ID, it.paymentId)
                assertEquals(PAYMENT_REFERENCE, it.reference)
            },
        )
    }

    @Test
    fun `onPaymentFailed should call bookingService`() {
        // given
        val payload = buildPaymentFailedPayload()

        // when
        listener.onPaymentFailed(payload)

        // then
        verify(serviceCommand).handlePaymentFailed(
            check {
                assertEquals(BOOKING_ID, it.bookingId)
                assertEquals(PAYMENT_ID, it.paymentId)
                assertEquals(PAYMENT_REASON, it.reason)
            },
        )
    }
}
