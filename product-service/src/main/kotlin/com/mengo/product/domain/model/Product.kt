package com.mengo.product.domain.model

import java.math.BigDecimal
import java.util.UUID

data class Product(
    val productId: UUID,
    val name: String,
    val price: BigDecimal,
    val stock: Int,
)
