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
import com.mengo.payment.infrastructure.persist.eventstore.PaymentEventStoreJpaRepository
import com.mengo.payment.infrastructure.persist.eventstore.PaymentEventStoreRepositoryService
import com.mengo.payment.infrastructure.persist.eventstore.mapers.PaymentEventEntityMapper
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class PaymentEventStoreRepositoryServiceTest {
    private val query: jakarta.persistence.Query = mock()
    private val entityManager: EntityManager = mock()
    private val paymentRepository: PaymentEventStoreJpaRepository = mock()
    private val paymentEventMapper =
        PaymentEventEntityMapper(
            ObjectMapper().apply {
                registerModule(KotlinModule.Builder().build())
                registerModule(JavaTimeModule())
                disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            },
        )
    private val repository = PaymentEventStoreRepositoryService(entityManager, paymentRepository, paymentEventMapper)

    @Test
    fun `load should return null when no events exist`() {
        // given
        whenever(entityManager.createNativeQuery(any())).thenReturn(query)
        whenever(query.setParameter(any<String>(), any())).thenReturn(query)
        whenever(query.singleResult).thenReturn(1)
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

        whenever(entityManager.createNativeQuery(any())).thenReturn(query)
        whenever(query.setParameter(any<String>(), any())).thenReturn(query)
        whenever(query.singleResult).thenReturn(1)
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

        whenever(paymentRepository.save(any())).thenReturn(createdEntity)

        // when
        repository.append(createdEvent)

        // then
        verify(paymentRepository).save(any())
    }
}
