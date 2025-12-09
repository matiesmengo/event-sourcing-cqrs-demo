package com.mengo.orchestrator.domain.service

import com.mengo.orchestrator.domain.model.command.SagaCommand

fun interface OutboxRepository {
    fun persistOutboxEvent(
        topic: String,
        payloadType: Class<*>,
        key: String?,
        message: SagaCommand,
    )
}
