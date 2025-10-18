package com.mengo.orchestrator.domain.service

import com.mengo.orchestrator.domain.model.events.OrchestratorEvent
import java.util.UUID

interface OrchestratorEventStoreRepository {
    fun save(domain: OrchestratorEvent)

    fun findByBookingId(bookingId: UUID): OrchestratorEvent?
}
