package com.mengo.orchestrator.domain.model

import java.math.BigDecimal
import java.util.UUID

data class Product(
    val productId: UUID,
    val quantity: Int,
    val price: BigDecimal? = null,
)
