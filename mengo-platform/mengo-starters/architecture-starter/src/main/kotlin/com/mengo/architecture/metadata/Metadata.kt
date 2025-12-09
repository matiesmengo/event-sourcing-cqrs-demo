package com.mengo.architecture.metadata

import java.util.UUID

data class Metadata(
    val correlationId: UUID,
    val causationId: UUID? = null,
    val attributes: Map<String, String> = emptyMap(),
    val traceParent: String? = null,
)
