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
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import kotlin.test.assertNull

class ProductEventStoreRepositoryServiceTest {
    private val query: jakarta.persistence.Query = mock()
    private val entityManager: EntityManager = mock()
    private val jpaRepository: ProductEventStoreJpaRepository = mock()
    private val mapper =
        ProductEventEntityMapper(
            ObjectMapper().apply {
                registerModule(KotlinModule.Builder().build())
                registerModule(JavaTimeModule())
                disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            },
        )
    private val repository = ProductEventStoreRepositoryService(entityManager, jpaRepository, mapper)

    @Test
    fun `load should return null when no events exist`() {
        // given
        whenever(entityManager.createNativeQuery(any())).thenReturn(query)
        whenever(query.setParameter(any<String>(), any())).thenReturn(query)
        whenever(query.singleResult).thenReturn(1)
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

        whenever(entityManager.createNativeQuery(any())).thenReturn(query)
        whenever(query.setParameter(any<String>(), any())).thenReturn(query)
        whenever(query.singleResult).thenReturn(1)
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

        whenever(jpaRepository.save(any())).thenReturn(createdEntity)

        // when
        repository.append(createdEvent)

        // then
        verify(jpaRepository, times(1)).save(any())
    }
}
