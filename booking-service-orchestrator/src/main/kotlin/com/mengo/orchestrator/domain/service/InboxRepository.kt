package com.mengo.orchestrator.domain.service

fun interface InboxRepository {
    fun validateIdempotencyEvent(): Boolean
}
