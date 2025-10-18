package com.mengo.orchestrator.infrastructure.persist

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface OrchestratorEventStoreJpaRepository : JpaRepository<OrchestratorEventEntity, UUID> {
    fun findTopByBookingIdOrderByAggregateVersionDesc(bookingId: UUID): OrchestratorEventEntity?
}
