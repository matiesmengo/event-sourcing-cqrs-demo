package com.mengo.product.infrastructure.persist.eventstore

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.mengo.product.domain.model.eventstore.ProductCreatedEvent
import com.mengo.product.domain.model.eventstore.ProductReservedEvent
import com.mengo.product.fixtures.ProductConstants.BOOKING_ID
import com.mengo.product.fixtures.ProductConstants.PRODUCT_ID
import com.mengo.product.infrastructure.persist.eventstore.mappers.ProductEventEntityMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ProductEventStoreRepositoryServiceTest {
    private val jpaRepository: ProductEventStoreJpaRepository = mock()
    private val mapper =
        ProductEventEntityMapper(
            ObjectMapper().apply {
                registerModule(KotlinModule.Builder().build())
                registerModule(JavaTimeModule())
                disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            },
        )
    private val repository = ProductEventStoreRepositoryService(jpaRepository, mapper)

    @Test
    fun `load should return null when no events exist`() {
        // given
        whenever(jpaRepository.findByProductIdOrderByAggregateVersionAsc(PRODUCT_ID)) doReturn listOf()

        // when
        val result = repository.load(PRODUCT_ID)

        // then
        assertNull(result)
        verify(jpaRepository, times(1)).findByProductIdOrderByAggregateVersionAsc(PRODUCT_ID)
    }

    @Test
    fun `load should rehydrate ProductAggregate from stored events`() {
        // given
        val createdEvent = ProductCreatedEvent(PRODUCT_ID, BigDecimal.TEN, 10, 0)
        val createdEntity = mapper.toEntity(createdEvent)

        val reservedEvent = ProductReservedEvent(PRODUCT_ID, BOOKING_ID, 1, 1)
        val reservedEntity = mapper.toEntity(reservedEvent)

        whenever(jpaRepository.findByProductIdOrderByAggregateVersionAsc(PRODUCT_ID))
            .thenReturn(listOf(createdEntity, reservedEntity))

        // when
        val aggregate = repository.load(PRODUCT_ID)

        // then
        assertNotNull(aggregate)
        assertEquals(PRODUCT_ID, aggregate?.productId)
        assertEquals(1, aggregate?.lastEventVersion)
    }

    @Test
    fun `append should persist event when version is correct`() {
        // given
        val createdEvent = ProductCreatedEvent(PRODUCT_ID, BigDecimal.TEN, 10, 0)
        val createdEntity = mapper.toEntity(createdEvent)

        whenever(jpaRepository.findFirstByProductIdOrderByAggregateVersionDesc(PRODUCT_ID))
            .thenReturn(null)
        whenever(jpaRepository.save(any())).thenReturn(createdEntity)

        // when
        repository.append(createdEvent)

        // then
        verify(jpaRepository, times(1)).findFirstByProductIdOrderByAggregateVersionDesc(PRODUCT_ID)
        verify(jpaRepository, times(1)).save(any())
    }

    @Test
    fun `append should throw on concurrency conflict`() {
        // given
        val existingEntity =
            ProductEventEntity(
                eventId = UUID.randomUUID(),
                productId = PRODUCT_ID,
                eventType = "ProductCreatedEvent",
                eventData = "{}",
                aggregateVersion = 1,
                createdAt = Instant.now(),
            )
        val newEvent = ProductReservedEvent(PRODUCT_ID, BOOKING_ID, 1, 10)

        whenever(jpaRepository.findFirstByProductIdOrderByAggregateVersionDesc(BOOKING_ID))
            .thenReturn(existingEntity)

        // when & then
        val ex = assertThrows(IllegalArgumentException::class.java) { repository.append(newEvent) }

        assertTrue(ex.message!!.contains("Concurrency conflict"))
        verify(jpaRepository, times(1))
            .findFirstByProductIdOrderByAggregateVersionDesc(PRODUCT_ID)
        verify(jpaRepository, never()).save(any())
    }
}
