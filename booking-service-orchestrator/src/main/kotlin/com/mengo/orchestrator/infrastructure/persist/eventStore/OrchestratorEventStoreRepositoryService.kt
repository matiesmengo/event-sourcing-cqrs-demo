package com.mengo.orchestrator.infrastructure.persist.eventStore

import com.mengo.orchestrator.domain.model.events.OrchestratorAggregate
import com.mengo.orchestrator.domain.model.events.OrchestratorEvent
import com.mengo.orchestrator.domain.service.OrchestratorEventStoreRepository
import com.mengo.orchestrator.infrastructure.persist.eventStore.mapper.OrchestratorEventEntityMapper
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Repository
open class OrchestratorEventStoreRepositoryService(
    private val entityManager: EntityManager,
    private val orchestratorRepository: OrchestratorEventStoreJpaRepository,
    private val orchestratorEventMapper: OrchestratorEventEntityMapper,
) : OrchestratorEventStoreRepository {
    @Transactional(propagation = Propagation.MANDATORY)
    override fun load(bookingId: UUID): OrchestratorAggregate? {
        val lockId = bookingId.mostSignificantBits
        entityManager
            .createNativeQuery("SELECT pg_advisory_xact_lock(:lockId)")
            .setParameter("lockId", lockId)
            .singleResult

        val entities = orchestratorRepository.findByBookingIdOrderByAggregateVersionAsc(bookingId)
        val events = entities.map { orchestratorEventMapper.toDomain(it) }
        return if (events.isEmpty()) null else OrchestratorAggregate.rehydrate(events)
    }

    @Transactional(propagation = Propagation.MANDATORY)
    override fun append(event: OrchestratorEvent) {
        orchestratorRepository.save(orchestratorEventMapper.toEntity(event))
    }
}
