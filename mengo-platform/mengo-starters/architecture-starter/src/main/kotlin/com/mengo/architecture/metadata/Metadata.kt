package com.mengo.architecture.metadata

data class Metadata(
    val correlationId: String,
    val causationId: String,
    val attributes: Map<String, String> = emptyMap(),
    val traceParent: String? = null,
)
