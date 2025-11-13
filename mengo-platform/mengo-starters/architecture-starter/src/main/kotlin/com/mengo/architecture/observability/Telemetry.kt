package com.mengo.architecture.observability

import java.util.UUID

interface Telemetry {
    fun recordSagaStarted(
        sagaName: String,
        sagaId: UUID,
    )

    fun recordSagaStepSuccess(
        sagaName: String,
        step: String,
    )

    fun recordSagaStepFailure(
        sagaName: String,
        step: String,
        cause: String?,
    )

    fun recordSagaCompleted(sagaName: String)

    fun recordSagaCompensated(
        sagaName: String,
        cause: String,
    )

    fun logStateChange(
        sagaName: String,
        sagaId: UUID,
        step: String,
        state: String,
        cause: String? = null,
    )
}
