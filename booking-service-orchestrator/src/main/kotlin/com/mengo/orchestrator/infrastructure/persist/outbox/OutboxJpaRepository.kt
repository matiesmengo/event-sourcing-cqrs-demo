package com.mengo.orchestrator.infrastructure.persist.outbox

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface OutboxJpaRepository : JpaRepository<OutboxEntity, UUID> {
    fun findTop100ByStatusOrderByCreatedAtAsc(status: OutboxStatus): List<OutboxEntity>
}
