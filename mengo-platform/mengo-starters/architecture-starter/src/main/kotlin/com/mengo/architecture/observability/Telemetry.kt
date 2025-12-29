package com.mengo.architecture.observability

interface Telemetry {
    fun recordSagaStarted(sagaName: String)

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

    fun recordSagaCompensated(sagaName: String)

    fun logStateChange(
        sagaName: String,
        step: String,
        state: String,
    )
}
