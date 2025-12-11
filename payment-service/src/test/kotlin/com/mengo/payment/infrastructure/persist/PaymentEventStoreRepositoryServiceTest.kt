package com.mengo.payment.infrastructure.persist

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.mengo.payment.domain.model.events.PaymentEvent
import com.mengo.payment.domain.model.events.PaymentState
import com.mengo.payment.fixtures.PaymentConstants.BOOKING_ID
import com.mengo.payment.fixtures.PaymentConstants.PAYMENT_ID
import com.mengo.payment.fixtures.PaymentConstants.PAYMENT_REFERENCE
import com.mengo.payment.fixtures.PaymentConstants.TOTAL_PRICE
import com.mengo.payment.infrastructure.persist.eventstore.PaymentEventEntity
import com.mengo.payment.infrastructure.persist.eventstore.PaymentEventStoreJpaRepository
import com.mengo.payment.infrastructure.persist.eventstore.PaymentEventStoreRepositoryService
import com.mengo.payment.infrastructure.persist.eventstore.mapers.PaymentEventEntityMapper
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PaymentEventStoreRepositoryServiceTest {
    private val paymentRepository: PaymentEventStoreJpaRepository = mock()
    private val paymentEventMapper =
        PaymentEventEntityMapper(
            ObjectMapper().apply {
                registerModule(KotlinModule.Builder().build())
                registerModule(JavaTimeModule())
                disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            },
        )
    private val repository = PaymentEventStoreRepositoryService(paymentRepository, paymentEventMapper)

    @Test
    fun `load should return null when no events exist`() {
        // given
        whenever(paymentRepository.findByPaymentIdOrderByAggregateVersionAsc(PAYMENT_ID)) doReturn emptyList()

        // when
        val result = repository.load(PAYMENT_ID)

        // then
        assertNull(result)
        verify(paymentRepository).findByPaymentIdOrderByAggregateVersionAsc(PAYMENT_ID)
    }

    @Test
    fun `load should rehydrate BookingAggregate from stored events`() {
        // given
        val createdEvent = PaymentEvent.Initiated(PAYMENT_ID, BOOKING_ID, TOTAL_PRICE, 0)
        val confirmedEvent = PaymentEvent.Completed(PAYMENT_ID, BOOKING_ID, PAYMENT_REFERENCE, 1)

        val createdEntity = paymentEventMapper.toEntity(createdEvent)
        val confirmedEntity = paymentEventMapper.toEntity(confirmedEvent)

        whenever(paymentRepository.findByPaymentIdOrderByAggregateVersionAsc(BOOKING_ID))
            .thenReturn(listOf(createdEntity, confirmedEntity))

        // when
        val aggregate = repository.load(BOOKING_ID)

        // then
        assertNotNull(aggregate)
        assertEquals(PAYMENT_ID, aggregate.paymentId)
        assertEquals(BOOKING_ID, aggregate.bookingId)
        assertEquals(PaymentState.COMPLETED, aggregate.status)
        assertEquals(1, aggregate.lastEventVersion)
    }

    @Test
    fun `append should persist event when version is correct`() {
        // given
        val createdEvent = PaymentEvent.Initiated(PAYMENT_ID, BOOKING_ID, TOTAL_PRICE, 0)
        val createdEntity = paymentEventMapper.toEntity(createdEvent)

        whenever(paymentRepository.findFirstByPaymentIdOrderByAggregateVersionDesc(PAYMENT_ID))
            .thenReturn(null)
        whenever(paymentRepository.save(any())).thenReturn(createdEntity)

        // when
        repository.append(createdEvent)

        // then
        verify(paymentRepository).findFirstByPaymentIdOrderByAggregateVersionDesc(PAYMENT_ID)
        verify(paymentRepository).save(any())
    }

    @Test
    fun `append should throw on concurrency conflict`() {
        // given
        val existingEntity =
            PaymentEventEntity(
                eventId = UUID.randomUUID(),
                paymentId = PAYMENT_ID,
                eventType = "Completed",
                eventData = "{}",
                aggregateVersion = 1,
                createdAt = Instant.now(),
            )
        val newEvent = PaymentEvent.Completed(PAYMENT_ID, BOOKING_ID, PAYMENT_REFERENCE, 5)

        whenever(paymentRepository.findFirstByPaymentIdOrderByAggregateVersionDesc(PAYMENT_ID))
            .thenReturn(existingEntity)

        // when + then
        val ex =
            assertThrows(IllegalArgumentException::class.java) {
                repository.append(newEvent)
            }

        assertTrue(ex.message!!.contains("Concurrency conflict"))
        verify(paymentRepository).findFirstByPaymentIdOrderByAggregateVersionDesc(PAYMENT_ID)
        verify(paymentRepository, never()).save(any())
    }
}
