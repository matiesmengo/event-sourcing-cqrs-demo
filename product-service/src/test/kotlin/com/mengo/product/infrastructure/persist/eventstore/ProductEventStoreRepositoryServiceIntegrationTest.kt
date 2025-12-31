package com.mengo.product.infrastructure.persist.eventstore

import com.mengo.architecture.test.infrastructure.AbstractIntegrationTest
import com.mengo.product.domain.model.eventstore.ProductCreatedEvent
import com.mengo.product.domain.model.eventstore.ProductReservedEvent
import com.mengo.product.fixtures.ProductConstants.BOOKING_ID
import com.mengo.product.fixtures.ProductConstants.PRODUCT_ID
import com.mengo.product.fixtures.ProductConstants.PRODUCT_PRICE
import com.mengo.product.infrastructure.persist.eventstore.mappers.ProductEventEntityMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ProductEventStoreRepositoryServiceIntegrationTest : AbstractIntegrationTest() {
    @Autowired
    private lateinit var jpaRepository: ProductEventStoreJpaRepository

    @Autowired
    private lateinit var mapper: ProductEventEntityMapper

    @Autowired
    private lateinit var repository: ProductEventStoreRepositoryService

    @BeforeEach
    fun cleanup() {
        jpaRepository.deleteAll()
    }

    val createdEvent = ProductCreatedEvent(PRODUCT_ID, PRODUCT_PRICE, 10, 0)
    val reservedEvent = ProductReservedEvent(PRODUCT_ID, BOOKING_ID, 5, 1)

    @Test
    @Transactional
    fun `load should return null when no events exist`() {
        val result = repository.load(PRODUCT_ID)
        assertNull(result)
    }

    @Test
    @Transactional
    fun `load should rehydrate BookingAggregate from stored events`() {
        // given
        jpaRepository.saveAll(
            listOf(
                mapper.toEntity(createdEvent),
                mapper.toEntity(reservedEvent),
            ),
        )

        // when
        val aggregate = repository.load(PRODUCT_ID)

        // then
        assertNotNull(aggregate)
        assertEquals(PRODUCT_ID, aggregate.productId)
        assertEquals(1, aggregate.lastEventVersion)
    }

    @Test
    @Transactional
    fun `append should persist event when version is correct`() {
        // given
        // when
        repository.append(createdEvent)

        // then
        val stored = jpaRepository.findByProductIdOrderByAggregateVersionAsc(PRODUCT_ID)
        assertEquals(1, stored.size)
        assertEquals(0, stored.first().aggregateVersion)
    }
}
