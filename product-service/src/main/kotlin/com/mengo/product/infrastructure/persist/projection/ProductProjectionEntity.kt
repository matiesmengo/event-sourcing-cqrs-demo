package com.mengo.product.infrastructure.persist.projection

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Document(collection = "product_projection")
data class ProductProjectionEntity(
    @Id
    val productId: UUID,
    val name: String,
    val price: BigDecimal,
    val stockAvailable: Int,
    val reserved: Int,
    val lastEventVersion: Int,
    val updatedAt: Instant,
)
