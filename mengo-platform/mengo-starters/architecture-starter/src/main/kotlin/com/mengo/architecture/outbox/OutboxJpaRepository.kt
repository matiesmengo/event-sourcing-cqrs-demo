package com.mengo.architecture.outbox

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface OutboxJpaRepository : JpaRepository<OutboxEntity, UUID> {
    fun findTop100ByStatusOrderByCreatedAtAsc(status: OutboxStatus): List<OutboxEntity>
}
