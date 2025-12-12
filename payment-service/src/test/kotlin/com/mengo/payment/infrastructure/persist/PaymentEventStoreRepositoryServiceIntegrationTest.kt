package com.mengo.payment.infrastructure.persist

import com.mengo.architecture.test.infrastructure.AbstractIntegrationTest
import com.mengo.payment.domain.model.events.PaymentEvent
import com.mengo.payment.domain.model.events.PaymentState
import com.mengo.payment.fixtures.PaymentConstants.BOOKING_ID
import com.mengo.payment.fixtures.PaymentConstants.PAYMENT_ID
import com.mengo.payment.fixtures.PaymentConstants.PAYMENT_REFERENCE
import com.mengo.payment.fixtures.PaymentConstants.TOTAL_PRICE
import com.mengo.payment.infrastructure.persist.eventstore.PaymentEventStoreJpaRepository
import com.mengo.payment.infrastructure.persist.eventstore.PaymentEventStoreRepositoryService
import com.mengo.payment.infrastructure.persist.eventstore.mapers.PaymentEventEntityMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.InvalidDataAccessApiUsageException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class PaymentEventStoreRepositoryServiceIntegrationTest : AbstractIntegrationTest() {
    @Autowired
    private lateinit var jpaRepository: PaymentEventStoreJpaRepository

    @Autowired
    private lateinit var mapper: PaymentEventEntityMapper

    @Autowired
    private lateinit var repository: PaymentEventStoreRepositoryService

    @BeforeEach
    fun cleanup() {
        jpaRepository.deleteAll()
    }

    @Test
    fun `load should return null when no events exist`() {
        val result = repository.load(PAYMENT_ID)
        assertNull(result)
    }

    @Test
    fun `load should rehydrate PaymentAggregate from stored events`() {
        // given
        val createdEvent = PaymentEvent.Initiated(PAYMENT_ID, BOOKING_ID, TOTAL_PRICE, 0)
        val completedEvent = PaymentEvent.Completed(PAYMENT_ID, BOOKING_ID, PAYMENT_REFERENCE, 1)

        jpaRepository.saveAll(
            listOf(
                mapper.toEntity(createdEvent),
                mapper.toEntity(completedEvent),
            ),
        )

        // when
        val aggregate = repository.load(PAYMENT_ID)

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

        // when
        repository.append(createdEvent)

        // then
        val stored = jpaRepository.findByPaymentIdOrderByAggregateVersionAsc(PAYMENT_ID)
        assertEquals(1, stored.size)
        assertEquals(0, stored.first().aggregateVersion)
    }

    @Test
    fun `append should throw on concurrency conflict`() {
        val firstEvent = PaymentEvent.Initiated(PAYMENT_ID, BOOKING_ID, TOTAL_PRICE, 0)
        val secondEvent = PaymentEvent.Completed(PAYMENT_ID, BOOKING_ID, PAYMENT_REFERENCE, 5)

        // when
        jpaRepository.save(mapper.toEntity(firstEvent))

        // when + then
        val ex = assertFailsWith<InvalidDataAccessApiUsageException> { repository.append(secondEvent) }
        assert(ex.message!!.contains("Concurrency conflict"))
    }
}
