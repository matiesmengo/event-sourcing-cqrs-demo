package com.mengo.payment.application

import com.mengo.payment.domain.model.command.PaymentCommand
import com.mengo.payment.domain.model.command.SagaCommand
import com.mengo.payment.domain.model.events.PaymentAggregate
import com.mengo.payment.domain.model.events.PaymentEvent
import com.mengo.payment.domain.service.PaymentEventPublisher
import com.mengo.payment.domain.service.PaymentEventStoreRepository
import com.mengo.payment.domain.service.PaymentProcessor
import com.mengo.payment.domain.service.PaymentProcessorResult
import com.mengo.payment.fixtures.PaymentConstants.BOOKING_ID
import com.mengo.payment.fixtures.PaymentConstants.PAYMENT_ID
import com.mengo.payment.fixtures.PaymentConstants.PAYMENT_REASON
import com.mengo.payment.fixtures.PaymentConstants.PAYMENT_REFERENCE
import com.mengo.payment.fixtures.PaymentConstants.TOTAL_PRICE
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class PaymentServiceCommandTest {
    private val processor: PaymentProcessor = mock()
    private val eventStoreRepository: PaymentEventStoreRepository = mock()
    private val eventPublisher: PaymentEventPublisher = mock()

    private val service = PaymentServiceCommand(processor, eventStoreRepository, eventPublisher)

    @Test
    fun `onRequestPayment should append and publish a initiated payment`() {
        // given / when
        service.onRequestPayment(PaymentCommand.BookingPayment(BOOKING_ID, TOTAL_PRICE))

        // then
        argumentCaptor<PaymentEvent>().apply {
            verify(eventStoreRepository).append(capture())
            val event = firstValue as PaymentEvent.Initiated
            assertNotNull(event.paymentId)
            assertEquals(BOOKING_ID, event.bookingId)
            assertEquals(TOTAL_PRICE, event.totalPrice)
            assertEquals(0, event.aggregateVersion)
        }

        argumentCaptor<PaymentCommand.PaymentInitiated>().apply {
            verify(eventPublisher).publishPaymentInitiated(capture())
            val command = firstValue
            assertNotNull(command.paymentId)
            assertEquals(BOOKING_ID, command.bookingId)
            assertEquals(BOOKING_ID, command.bookingId)
            assertEquals(TOTAL_PRICE, command.totalPrice)
        }
    }

    @Test
    fun `onPaymentInitiated should append Completed event and publish PaymentCompleted command if processor succeeds`() {
        // given
        val initialAggregate = createInitialAggregate(PAYMENT_ID)
        whenever(eventStoreRepository.load(PAYMENT_ID)).thenReturn(initialAggregate)
        whenever(processor.executePayment(PAYMENT_ID)).thenReturn(PaymentProcessorResult.Success(PAYMENT_REFERENCE))

        // when
        service.onPaymentInitiated(PaymentCommand.PaymentInitiated(PAYMENT_ID, BOOKING_ID, TOTAL_PRICE))

        // then
        verify(processor).executePayment(PAYMENT_ID)

        argumentCaptor<PaymentEvent>().apply {
            verify(eventStoreRepository).append(capture())
            val completedEvent = firstValue
            assertIs<PaymentEvent.Completed>(completedEvent)
            assertEquals(PAYMENT_ID, completedEvent.paymentId)
            assertEquals(BOOKING_ID, completedEvent.bookingId)
            assertEquals(PAYMENT_REFERENCE, completedEvent.reference)
            assertEquals(1, completedEvent.aggregateVersion)
        }

        argumentCaptor<SagaCommand.PaymentCompleted>().apply {
            verify(eventPublisher).publishPaymentCompleted(capture())
            val sagaCommand = firstValue
            assertEquals(PAYMENT_ID, sagaCommand.paymentId)
            assertEquals(BOOKING_ID, sagaCommand.bookingId)
            assertEquals(PAYMENT_REFERENCE, sagaCommand.reference)
        }

        verify(eventPublisher, never()).publishPaymentFailed(any())
        verify(eventPublisher, never()).publishPaymentInitiated(any())
    }

    @Test
    fun `onPaymentInitiated should append Failed event and publish PaymentFailed command if processor fails`() {
        // given
        val initialAggregate = createInitialAggregate(PAYMENT_ID)
        whenever(eventStoreRepository.load(PAYMENT_ID)).thenReturn(initialAggregate)
        whenever(processor.executePayment(PAYMENT_ID)).thenReturn(PaymentProcessorResult.Failure(PAYMENT_REASON))

        // when
        service.onPaymentInitiated(PaymentCommand.PaymentInitiated(PAYMENT_ID, BOOKING_ID, TOTAL_PRICE))

        // then
        verify(processor).executePayment(PAYMENT_ID)

        argumentCaptor<PaymentEvent>().apply {
            verify(eventStoreRepository).append(capture())
            val failedEvent = firstValue
            assertIs<PaymentEvent.Failed>(failedEvent)
            assertEquals(PAYMENT_ID, failedEvent.paymentId)
            assertEquals(BOOKING_ID, failedEvent.bookingId)
            assertEquals(PAYMENT_REASON, failedEvent.reason)
            assertEquals(1, failedEvent.aggregateVersion)
        }

        argumentCaptor<SagaCommand.PaymentFailed>().apply {
            verify(eventPublisher).publishPaymentFailed(capture())
            val sagaCommand = firstValue
            assertEquals(PAYMENT_ID, sagaCommand.paymentId)
            assertEquals(BOOKING_ID, sagaCommand.bookingId)
            assertEquals(PAYMENT_REASON, sagaCommand.reason)
        }

        verify(eventPublisher, never()).publishPaymentCompleted(any())
        verify(eventPublisher, never()).publishPaymentInitiated(any())
    }

    @Test
    fun `onPaymentInitiated should throw an error if aggregate is not found`() {
        // given
        whenever(eventStoreRepository.load(PAYMENT_ID)).thenReturn(null)

        val command = PaymentCommand.PaymentInitiated(PAYMENT_ID, BOOKING_ID, TOTAL_PRICE)

        // when / then
        assertFailsWith<IllegalStateException>(
            block = {
                service.onPaymentInitiated(command)
            },
        )

        verify(processor, never()).executePayment(any())
        verify(eventPublisher, never()).publishPaymentCompleted(any())
        verify(eventPublisher, never()).publishPaymentFailed(any())
    }

    private fun createInitialAggregate(paymentId: UUID): PaymentAggregate {
        val initiatedEvent =
            PaymentEvent.Initiated(
                paymentId = paymentId,
                bookingId = BOOKING_ID,
                totalPrice = TOTAL_PRICE,
                aggregateVersion = 0,
            )
        return PaymentAggregate.rehydrate(listOf(initiatedEvent))
    }
}
