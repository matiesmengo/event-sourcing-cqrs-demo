package com.mengo.architecture.inbox

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface InboxJpaRepository : JpaRepository<InboxEntity, Long> {
    fun countByCausationId(causationId: UUID): Long
}
