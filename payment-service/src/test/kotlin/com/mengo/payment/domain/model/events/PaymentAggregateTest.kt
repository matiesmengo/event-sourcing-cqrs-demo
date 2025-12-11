package com.mengo.payment.domain.model.events
import com.mengo.payment.fixtures.PaymentConstants.BOOKING_ID
import com.mengo.payment.fixtures.PaymentConstants.PAYMENT_ID
import com.mengo.payment.fixtures.PaymentConstants.PAYMENT_REASON
import com.mengo.payment.fixtures.PaymentConstants.PAYMENT_REFERENCE
import com.mengo.payment.fixtures.PaymentConstants.TOTAL_PRICE
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PaymentAggregateTest {
    @Test
    fun `createPaymentEvent should return Initiated event with correct data and version 0`() {
        val event = PaymentAggregate.createPaymentEvent(BOOKING_ID, TOTAL_PRICE)

        event as PaymentEvent.Initiated
        assertNotNull(event.paymentId)
        assertEquals(BOOKING_ID, event.bookingId)
        assertEquals(TOTAL_PRICE, event.totalPrice)
        assertEquals(0, event.aggregateVersion)
    }

    @Test
    fun `rehydrate should throw when event list is empty`() {
        val ex =
            assertThrows<IllegalArgumentException> {
                PaymentAggregate.rehydrate(emptyList())
            }
        assertTrue(ex.message!!.contains("No events provided"))
    }

    @Test
    fun `rehydrate should rebuild aggregate from Initiated event`() {
        val initiated =
            PaymentEvent.Initiated(PAYMENT_ID, BOOKING_ID, TOTAL_PRICE, aggregateVersion = 0)

        val aggregate = PaymentAggregate.rehydrate(listOf(initiated))

        assertEquals(PAYMENT_ID, aggregate.paymentId)
        assertEquals(BOOKING_ID, aggregate.bookingId)
        assertEquals(PaymentState.INITIATED, aggregate.status)
        assertEquals(0, aggregate.lastEventVersion)
    }

    @Test
    fun `rehydrate should rebuild aggregate with Completed status and version`() {
        val initiated =
            PaymentEvent.Initiated(PAYMENT_ID, BOOKING_ID, TOTAL_PRICE, aggregateVersion = 0)
        val completed =
            PaymentEvent.Completed(PAYMENT_ID, BOOKING_ID, PAYMENT_REFERENCE, aggregateVersion = 1)

        val aggregate = PaymentAggregate.rehydrate(listOf(completed, initiated))

        assertEquals(PaymentState.COMPLETED, aggregate.status)
        assertEquals(1, aggregate.lastEventVersion)
        assertEquals(PAYMENT_ID, aggregate.paymentId)
    }

    @Test
    fun `rehydrate should rebuild aggregate with Failed status and version`() {
        val initiated =
            PaymentEvent.Initiated(PAYMENT_ID, BOOKING_ID, TOTAL_PRICE, aggregateVersion = 0)
        val failed =
            PaymentEvent.Failed(PAYMENT_ID, BOOKING_ID, PAYMENT_REASON, aggregateVersion = 2)

        val aggregate = PaymentAggregate.rehydrate(listOf(initiated, failed))

        assertEquals(PaymentState.FAILED, aggregate.status)
        assertEquals(2, aggregate.lastEventVersion)
    }

    @Test
    fun `confirmPayment should create Completed event when status is INITIATED`() {
        val aggregate =
            PaymentAggregate(
                paymentId = PAYMENT_ID,
                bookingId = BOOKING_ID,
                status = PaymentState.INITIATED,
                lastEventVersion = 5,
            )

        val event = aggregate.confirmPayment(PAYMENT_REFERENCE)

        assertEquals(PAYMENT_ID, event.paymentId)
        assertEquals(BOOKING_ID, event.bookingId)
        assertEquals(PAYMENT_REFERENCE, event.reference)
        assertEquals(6, event.aggregateVersion)
    }

    @Test
    fun `confirmPayment should throw when status is COMPLETED`() {
        val aggregate =
            PaymentAggregate(
                paymentId = PAYMENT_ID,
                bookingId = BOOKING_ID,
                status = PaymentState.COMPLETED,
                lastEventVersion = 2,
            )

        val ex = assertThrows<IllegalStateException> { aggregate.confirmPayment(PAYMENT_REFERENCE) }
        assertTrue(ex.message!!.contains("Cannot mark as confirmed a payment in state COMPLETED"))
    }

    @Test
    fun `confirmPayment should throw when status is FAILED`() {
        val aggregate =
            PaymentAggregate(
                paymentId = PAYMENT_ID,
                bookingId = BOOKING_ID,
                status = PaymentState.FAILED,
                lastEventVersion = 2,
            )

        val ex = assertThrows<IllegalStateException> { aggregate.confirmPayment(PAYMENT_REFERENCE) }
        assertTrue(ex.message!!.contains("Cannot mark as confirmed a payment in state FAILED"))
    }

    @Test
    fun `failPayment should create Failed event when status is INITIATED`() {
        // Estat inicial: INITIATED, Versió 3
        val aggregate =
            PaymentAggregate(
                paymentId = PAYMENT_ID,
                bookingId = BOOKING_ID,
                status = PaymentState.INITIATED,
                lastEventVersion = 3,
            )

        val event = aggregate.failPayment(PAYMENT_REASON)

        assertEquals(PAYMENT_ID, event.paymentId)
        assertEquals(BOOKING_ID, event.bookingId)
        assertEquals(PAYMENT_REASON, event.reason)
        assertEquals(4, event.aggregateVersion) // 3 + 1
    }

    @Test
    fun `failPayment should throw when status is COMPLETED`() {
        val aggregate =
            PaymentAggregate(
                paymentId = PAYMENT_ID,
                bookingId = BOOKING_ID,
                status = PaymentState.COMPLETED,
                lastEventVersion = 3,
            )

        val ex = assertThrows<IllegalStateException> { aggregate.failPayment(PAYMENT_REASON) }
        assertTrue(ex.message!!.contains("Cannot mark as failed a payment in state COMPLETED"))
    }

    @Test
    fun `failPayment should throw when status is FAILED`() {
        val aggregate =
            PaymentAggregate(
                paymentId = PAYMENT_ID,
                bookingId = BOOKING_ID,
                status = PaymentState.FAILED,
                lastEventVersion = 3,
            )

        val ex = assertThrows<IllegalStateException> { aggregate.failPayment(PAYMENT_REASON) }
        assertTrue(ex.message!!.contains("Cannot mark as failed a payment in state FAILED"))
    }
}
