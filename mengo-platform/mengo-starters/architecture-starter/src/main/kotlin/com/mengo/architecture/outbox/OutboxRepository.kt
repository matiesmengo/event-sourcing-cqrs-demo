package com.mengo.architecture.outbox

import org.apache.avro.specific.SpecificRecord

fun interface OutboxRepository {
    fun persistOutboxEvent(
        topic: String,
        key: String?,
        payloadJson: SpecificRecord,
    )
}
