package com.mengo.orchestrator.infrastructure.persist.eventStore

import com.mengo.architecture.test.infrastructure.AbstractIntegrationTest
import com.mengo.orchestrator.domain.model.Product
import com.mengo.orchestrator.domain.model.events.OrchestratorEvent
import com.mengo.orchestrator.fixtures.OrchestratorConstants.BOOKING_ID
import com.mengo.orchestrator.infrastructure.persist.eventStore.mapper.OrchestratorEventEntityMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.InvalidDataAccessApiUsageException
import java.math.BigDecimal
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class OrchestratorEventStoreRepositoryServiceIntegrationTest : AbstractIntegrationTest() {
    @Autowired private lateinit var orchestratorRepository: OrchestratorEventStoreJpaRepository

    @Autowired private lateinit var orchestratorEventMapper: OrchestratorEventEntityMapper

    @Autowired private lateinit var repository: OrchestratorEventStoreRepositoryService

    @BeforeEach
    fun cleanup() {
        orchestratorRepository.deleteAll()
    }

    @Test
    fun `load should return null when no events exist`() {
        val result = repository.load(BOOKING_ID)
        assertNull(result)
    }

    @Test
    fun `load should rehydrate OrchestratorAggregate from stored events`() {
        // given
        val product = Product(UUID.randomUUID(), 2, BigDecimal.TEN)
        val createdEvent = OrchestratorEvent.Created(BOOKING_ID, setOf(product), 0)
        val reservedEvent = OrchestratorEvent.ProductReserved(BOOKING_ID, product, 1)

        // persist events
        orchestratorRepository.saveAll(
            listOf(
                orchestratorEventMapper.toEntity(createdEvent),
                orchestratorEventMapper.toEntity(reservedEvent),
            ),
        )

        // when
        val aggregate = repository.load(BOOKING_ID)

        // then
        assertNotNull(aggregate)
        assertEquals(BOOKING_ID, aggregate.bookingId)
        assertEquals(1, aggregate.lastEventVersion)
        assertEquals(1, aggregate.reservedProducts.size)
    }

    @Test
    fun `append should persist event when version matches`() {
        // given
        val product = Product(UUID.randomUUID(), 1, BigDecimal.ONE)
        val event = OrchestratorEvent.Created(BOOKING_ID, setOf(product), 0)

        // when
        repository.append(event)

        // then
        val stored = orchestratorRepository.findByBookingIdOrderByAggregateVersionAsc(BOOKING_ID)
        assertEquals(1, stored.size)
        assertEquals(0, stored.first().aggregateVersion)
    }

    @Test
    fun `append should throw on concurrency conflict`() {
        val product = Product(UUID.randomUUID(), 1, BigDecimal.ONE)
        val firstEvent = OrchestratorEvent.Created(BOOKING_ID, setOf(product), 0)
        val secondEvent = OrchestratorEvent.Created(BOOKING_ID, setOf(product), 5)

        // when
        orchestratorRepository.save(orchestratorEventMapper.toEntity(firstEvent))

        // when + then
        val ex = assertFailsWith<InvalidDataAccessApiUsageException> { repository.append(secondEvent) }
        assert(ex.message!!.contains("Concurrency conflict"))
    }
}
