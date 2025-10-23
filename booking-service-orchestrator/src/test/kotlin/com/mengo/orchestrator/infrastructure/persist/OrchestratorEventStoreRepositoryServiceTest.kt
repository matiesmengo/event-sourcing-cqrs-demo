package com.mengo.orchestrator.infrastructure.persist

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.mengo.orchestrator.domain.model.Product
import com.mengo.orchestrator.domain.model.events.OrchestratorEvent
import com.mengo.orchestrator.fixtures.OrchestratorConstants.BOOKING_ID
import com.mengo.orchestrator.fixtures.OrchestratorConstants.PRODUCT_ID
import com.mengo.orchestrator.infrastructure.persist.mapper.OrchestratorEventEntityMapper
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.assertNull
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class OrchestratorEventStoreRepositoryServiceTest {
    private val jpaRepository: OrchestratorEventStoreJpaRepository = mock()
    private val mapper =
        OrchestratorEventEntityMapper(
            ObjectMapper().apply {
                registerModule(KotlinModule.Builder().build())
                registerModule(JavaTimeModule())
                disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            },
        )
    private val repository = OrchestratorEventStoreRepositoryService(jpaRepository, mapper)

    @Test
    fun `load should return null when no events exist`() {
        // given
        whenever(jpaRepository.findByBookingIdOrderByAggregateVersionAsc(BOOKING_ID))
            .thenReturn(emptyList())

        // when
        val result = repository.load(BOOKING_ID)

        // then
        assertNull(result)
        verify(jpaRepository).findByBookingIdOrderByAggregateVersionAsc(BOOKING_ID)
    }

    @Test
    fun `load should rehydrate OrchestratorAggregate from stored events`() {
        // given
        val product1 = Product(UUID.randomUUID(), 2, BigDecimal.TEN)
        val product2 = Product(UUID.randomUUID(), 3, BigDecimal.ONE)

        val createdEvent = OrchestratorEvent.Created(BOOKING_ID, setOf(product1, product2), 0)
        val createdEntity = mapper.toEntity(createdEvent)

        val reservedEvent = OrchestratorEvent.ProductReserved(BOOKING_ID, product1, 1)
        val reservedEntity = mapper.toEntity(reservedEvent)

        whenever(jpaRepository.findByBookingIdOrderByAggregateVersionAsc(BOOKING_ID))
            .thenReturn(listOf(createdEntity, reservedEntity))

        // when
        val aggregate = repository.load(BOOKING_ID)

        // then
        assertNotNull(aggregate)
        assertEquals(BOOKING_ID, aggregate.bookingId)
        assertEquals(2, aggregate.expectedProducts.size)
        assertEquals(1, aggregate.reservedProducts.size)
        assertEquals(1, aggregate.lastEventVersion)
    }

    @Test
    fun `append should persist event when version matches`() {
        // given
        val product = Product(PRODUCT_ID, 1, BigDecimal.ONE)
        val event = OrchestratorEvent.Created(BOOKING_ID, setOf(product), 0)

        whenever(jpaRepository.findFirstByBookingIdOrderByAggregateVersionDesc(BOOKING_ID))
            .thenReturn(null)
        whenever(jpaRepository.save(any())).thenAnswer { it.arguments[0] }

        // when
        repository.append(event)

        // then
        verify(jpaRepository).findFirstByBookingIdOrderByAggregateVersionDesc(BOOKING_ID)
        verify(jpaRepository).save(any())
    }

    @Test
    fun `append should throw on concurrency conflict`() {
        // given
        val existingEntity =
            OrchestratorEventEntity(
                eventId = UUID.randomUUID(),
                bookingId = BOOKING_ID,
                eventType = "BookingCreatedEvent",
                eventData = "{}",
                aggregateVersion = 1,
                createdAt = Instant.now(),
            )
        val newEvent = OrchestratorEvent.PaymentCompleted(BOOKING_ID, 5)

        whenever(jpaRepository.findFirstByBookingIdOrderByAggregateVersionDesc(BOOKING_ID))
            .thenReturn(existingEntity)

        // when + then
        val ex = assertThrows(IllegalStateException::class.java) { repository.append(newEvent) }

        assertTrue(ex.message!!.contains("Concurrency conflict"))
        verify(jpaRepository)
            .findFirstByBookingIdOrderByAggregateVersionDesc(BOOKING_ID)
        verify(jpaRepository, never()).save(any())
    }
}
