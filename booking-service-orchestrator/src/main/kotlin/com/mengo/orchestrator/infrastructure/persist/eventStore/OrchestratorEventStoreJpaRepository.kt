package com.mengo.orchestrator.infrastructure.persist.eventStore

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface OrchestratorEventStoreJpaRepository : JpaRepository<OrchestratorEventEntity, UUID> {
    fun findFirstByBookingIdOrderByAggregateVersionDesc(bookingId: UUID): OrchestratorEventEntity?

    fun findByBookingIdOrderByAggregateVersionAsc(bookingId: UUID): List<OrchestratorEventEntity>
}
