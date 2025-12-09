package com.mengo.orchestrator.infrastructure.persist.inbox

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

interface InboxJpaRepository : JpaRepository<InboxEntity, Long> {
    /**
     * Inserts a new entry or does nothing if the causationId already exists.
     * Returns 1 if a new row was inserted, or 0 if a conflict was detected and ignored.
     */
    @Transactional
    @Modifying
    @Query(
        """
            INSERT INTO orchestrator.processed_events (causation_id, aggregate_id, processed_at)
            VALUES (:causationId, :aggregateId, NOW())
            ON CONFLICT (causation_id) DO NOTHING
        """,
        nativeQuery = true,
    )
    fun insertOnConflictDoNothing(
        causationId: UUID,
        aggregateId: UUID,
    ): Int

    fun countByCausationId(causationId: UUID): Long
}
