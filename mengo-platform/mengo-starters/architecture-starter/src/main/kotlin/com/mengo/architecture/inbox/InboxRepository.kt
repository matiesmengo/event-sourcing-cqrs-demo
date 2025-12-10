package com.mengo.architecture.inbox

fun interface InboxRepository {
    fun validateIdempotencyEvent(): Boolean
}
