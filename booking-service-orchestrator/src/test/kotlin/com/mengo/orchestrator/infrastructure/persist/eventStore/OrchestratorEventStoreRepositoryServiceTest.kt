package com.mengo.orchestrator.infrastructure.persist.eventStore

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.mengo.orchestrator.domain.model.Product
import com.mengo.orchestrator.domain.model.events.OrchestratorEvent
import com.mengo.orchestrator.fixtures.OrchestratorConstants.BOOKING_ID
import com.mengo.orchestrator.fixtures.OrchestratorConstants.PRODUCT_ID
import com.mengo.orchestrator.infrastructure.persist.eventStore.mapper.OrchestratorEventEntityMapper
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class OrchestratorEventStoreRepositoryServiceTest {
    private val query: jakarta.persistence.Query = mock()
    private val entityManager: EntityManager = mock()
    private val jpaRepository: OrchestratorEventStoreJpaRepository = mock()
    private val mapper =
        OrchestratorEventEntityMapper(
            ObjectMapper().apply {
                registerModule(KotlinModule.Builder().build())
                registerModule(JavaTimeModule())
                disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            },
        )
    private val repository = OrchestratorEventStoreRepositoryService(entityManager, jpaRepository, mapper)

    @Test
    fun `load should return null when no events exist`() {
        // given
        whenever(entityManager.createNativeQuery(any())).thenReturn(query)
        whenever(query.setParameter(any<String>(), any())).thenReturn(query)
        whenever(query.singleResult).thenReturn(1)
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
        whenever(entityManager.createNativeQuery(any())).thenReturn(query)
        whenever(query.setParameter(any<String>(), any())).thenReturn(query)
        whenever(query.singleResult).thenReturn(1)

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

        whenever(jpaRepository.save(any())).thenAnswer { it.arguments[0] }

        // when
        repository.append(event)

        // then
        verify(jpaRepository).save(any())
    }
}
