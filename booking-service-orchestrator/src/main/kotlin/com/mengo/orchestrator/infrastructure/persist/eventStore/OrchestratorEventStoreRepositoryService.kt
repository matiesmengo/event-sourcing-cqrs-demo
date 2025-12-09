package com.mengo.orchestrator.infrastructure.persist.eventStore

import com.mengo.orchestrator.domain.model.events.OrchestratorAggregate
import com.mengo.orchestrator.domain.model.events.OrchestratorEvent
import com.mengo.orchestrator.domain.service.OrchestratorEventStoreRepository
import com.mengo.orchestrator.infrastructure.persist.eventStore.mapper.OrchestratorEventEntityMapper
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
open class OrchestratorEventStoreRepositoryService(
    private val orchestratorRepository: OrchestratorEventStoreJpaRepository,
    private val orchestratorEventMapper: OrchestratorEventEntityMapper,
) : OrchestratorEventStoreRepository {
    override fun load(bookingId: UUID): OrchestratorAggregate? {
        val entities = orchestratorRepository.findByBookingIdOrderByAggregateVersionAsc(bookingId)
        val events = entities.map { orchestratorEventMapper.toDomain(it) }
        return if (events.isEmpty()) null else OrchestratorAggregate.rehydrate(events)
    }

    override fun append(event: OrchestratorEvent) {
        val currentVersion =
            orchestratorRepository
                .findFirstByBookingIdOrderByAggregateVersionDesc(event.bookingId)
                ?.aggregateVersion
                ?: -1

        require(event.aggregateVersion == currentVersion + 1) {
            "Concurrency conflict: expected=${event.aggregateVersion}, actual=$currentVersion"
        }

        orchestratorRepository.save(orchestratorEventMapper.toEntity(event))
    }
}
