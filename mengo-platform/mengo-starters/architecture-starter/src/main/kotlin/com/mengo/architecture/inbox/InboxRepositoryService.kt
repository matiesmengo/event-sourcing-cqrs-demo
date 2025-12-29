package com.mengo.architecture.inbox

import com.mengo.architecture.metadata.MetadataContextHolder
import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

open class InboxRepositoryService(
    private val entityManager: EntityManager,
    @Value("\${spring.jpa.properties.hibernate.default_schema}")
    private val localSchemaName: String,
) : InboxRepository {
    /**
     * Registry of an Idempotency Guard events from processed_events table.
     * Return true if a new event.
     * Return false if duplicated.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun validateIdempotencyEvent(): Boolean {
        val metadata = MetadataContextHolder.get() ?: error("IdempotencyEvent - MetadataContextHolder is lost")

        val sql =
            """
            INSERT INTO $localSchemaName.processed_events 
                (causation_id, aggregate_id, processed_at)
            VALUES 
                (:causationId, :correlationId, NOW())
            ON CONFLICT (causation_id) DO NOTHING
            """.trimIndent()

        return entityManager
            .createNativeQuery(sql)
            .setParameter("causationId", metadata.causationId)
            .setParameter("correlationId", metadata.correlationId)
            .executeUpdate() == 1
    }
}
