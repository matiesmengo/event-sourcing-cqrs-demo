package com.mengo.orchestrator.domain.service

import OrchestratorEvent
import com.mengo.orchestrator.domain.model.events.OrchestratorAggregate
import java.util.UUID

interface OrchestratorEventStoreRepository {
    fun load(bookingId: UUID): OrchestratorAggregate?

    fun append(event: OrchestratorEvent)
}
