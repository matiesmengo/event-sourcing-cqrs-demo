package com.mengo.orchestrator.domain.service

import com.mengo.orchestrator.domain.model.events.OrchestratorAggregate
import com.mengo.orchestrator.domain.model.events.OrchestratorEvent
import java.util.UUID

interface OrchestratorEventStoreRepository {
    fun load(bookingId: UUID): OrchestratorAggregate?

    fun append(event: OrchestratorEvent)
}
