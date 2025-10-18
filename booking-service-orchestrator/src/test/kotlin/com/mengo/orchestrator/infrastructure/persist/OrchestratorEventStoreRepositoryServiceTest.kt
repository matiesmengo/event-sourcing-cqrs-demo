package com.mengo.orchestrator.infrastructure.persist

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.mengo.orchestrator.domain.model.events.OrchestratorEvent
import com.mengo.orchestrator.fixtures.DomainTestData.buildCompensating
import com.mengo.orchestrator.fixtures.DomainTestData.buildCompleted
import com.mengo.orchestrator.fixtures.DomainTestData.buildCreated
import com.mengo.orchestrator.fixtures.DomainTestData.buildWaitingPayment
import com.mengo.orchestrator.fixtures.DomainTestData.buildWaitingStock
import com.mengo.orchestrator.fixtures.OrchestratorConstants.BOOKING_ID
import com.mengo.orchestrator.infrastructure.persist.mapper.OrchestratorEventEntityMapper
import org.junit.jupiter.api.Test
import org.mockito.kotlin.argThat
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

class OrchestratorEventStoreRepositoryServiceTest {
    private val orchestratorRepository: OrchestratorEventStoreJpaRepository = mock()
    private val orchestratorEventMapper =
        OrchestratorEventEntityMapper(
            ObjectMapper().apply {
                registerModule(KotlinModule.Builder().build())
                registerModule(JavaTimeModule())
                disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            },
        )
    private val repository = OrchestratorEventStoreRepositoryService(orchestratorRepository, orchestratorEventMapper)

    @Test
    fun `save should persist OrchestratorEvent`() {
        // given
        val event = buildCreated()
        whenever(orchestratorRepository.findTopByBookingIdOrderByAggregateVersionDesc(event.bookingId))
            .thenReturn(null)

        // when
        repository.save(event)

        // then
        verify(orchestratorRepository).findTopByBookingIdOrderByAggregateVersionDesc(event.bookingId)
        verify(orchestratorRepository).save(
            argThat {
                bookingId == event.bookingId && eventType == event::class.simpleName
            },
        )
    }

    @Test
    fun `save should persist WaitingStock event`() {
        val event = buildWaitingStock()
        whenever(orchestratorRepository.findTopByBookingIdOrderByAggregateVersionDesc(event.bookingId))
            .thenReturn(null)

        repository.save(event)

        verify(orchestratorRepository).save(
            argThat {
                bookingId == event.bookingId && eventType == event::class.simpleName
            },
        )
    }

    @Test
    fun `save should persist WaitingPayment event`() {
        val event = buildWaitingPayment()
        whenever(orchestratorRepository.findTopByBookingIdOrderByAggregateVersionDesc(event.bookingId))
            .thenReturn(null)

        repository.save(event)

        verify(orchestratorRepository).save(
            argThat {
                bookingId == event.bookingId && eventType == event::class.simpleName
            },
        )
    }

    @Test
    fun `save should persist Completed event`() {
        val event = buildCompleted()
        whenever(orchestratorRepository.findTopByBookingIdOrderByAggregateVersionDesc(event.bookingId))
            .thenReturn(null)

        repository.save(event)

        verify(orchestratorRepository).save(
            argThat {
                bookingId == event.bookingId && eventType == event::class.simpleName
            },
        )
    }

    @Test
    fun `save should persist Compensating event`() {
        val event = buildCompensating()
        whenever(orchestratorRepository.findTopByBookingIdOrderByAggregateVersionDesc(event.bookingId))
            .thenReturn(null)

        repository.save(event)

        verify(orchestratorRepository).save(
            argThat {
                bookingId == event.bookingId && eventType == event::class.simpleName
            },
        )
    }

    @Test
    fun `findByBookingId should return correct event type`() {
        val createdEntity =
            buildCreated().let { event ->
                orchestratorEventMapper.toEntity(event, 1)
            }
        whenever(orchestratorRepository.findTopByBookingIdOrderByAggregateVersionDesc(BOOKING_ID))
            .thenReturn(createdEntity)

        val result = repository.findByBookingId(BOOKING_ID)
        assertEquals(OrchestratorEvent.Created::class, result!!::class)
    }
}
