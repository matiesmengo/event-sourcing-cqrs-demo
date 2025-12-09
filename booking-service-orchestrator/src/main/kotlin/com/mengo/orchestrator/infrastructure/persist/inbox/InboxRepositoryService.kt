package com.mengo.orchestrator.infrastructure.persist.inbox

import com.mengo.architecture.metadata.MetadataContextHolder
import com.mengo.orchestrator.domain.service.InboxRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Repository
open class InboxRepositoryService(
    private val repository: InboxJpaRepository,
) : InboxRepository {
    /**
     * Registry of an Idempotency Guard events from processed_events table.
     * Return true if a new event.
     * Return false if duplicated.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun validateIdempotencyEvent(): Boolean {
        val metadata = MetadataContextHolder.get() ?: error("MetadataContextHolder is lost")
        val causationId = metadata.causationId ?: error("MetadataContextHolder.CausationId is lost")
        return repository.insertOnConflictDoNothing(causationId, metadata.correlationId) == 1
    }
}
