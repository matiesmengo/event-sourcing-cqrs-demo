package com.mengo.orchestrator.infrastructure.persist

import com.mengo.orchestrator.domain.model.events.OrchestratorEvent
import com.mengo.orchestrator.domain.service.OrchestratorEventStoreRepository
import com.mengo.orchestrator.infrastructure.persist.mapper.OrchestratorEventEntityMapper
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
open class OrchestratorEventStoreRepositoryService(
    private val orchestratorRepository: OrchestratorEventStoreJpaRepository,
    private val orchestratorEventMapper: OrchestratorEventEntityMapper,
) : OrchestratorEventStoreRepository {
    override fun save(domain: OrchestratorEvent) {
        val lastVersion =
            orchestratorRepository
                .findTopByBookingIdOrderByAggregateVersionDesc(domain.bookingId)
                ?.aggregateVersion ?: 0

        val entity = orchestratorEventMapper.toEntity(domain, lastVersion + 1)
        orchestratorRepository.save(entity)
    }

    override fun findByBookingId(bookingId: UUID): OrchestratorEvent? {
        val latestEntity =
            orchestratorRepository
                .findTopByBookingIdOrderByAggregateVersionDesc(bookingId)
                ?: return null

        return orchestratorEventMapper.toDomain(latestEntity)
    }
}
