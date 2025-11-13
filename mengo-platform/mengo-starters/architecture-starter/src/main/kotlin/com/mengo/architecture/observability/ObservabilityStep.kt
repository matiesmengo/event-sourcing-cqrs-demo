package com.mengo.architecture.observability

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ObservabilityStep(
    val name: String,
    val sagaName: String = "bookingFlow",
)
