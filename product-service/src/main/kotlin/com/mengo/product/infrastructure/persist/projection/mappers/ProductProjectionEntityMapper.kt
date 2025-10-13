package com.mengo.product.infrastructure.persist.projection.mappers

import com.mengo.product.domain.model.Product
import com.mengo.product.infrastructure.persist.projection.ProductProjectionEntity

fun ProductProjectionEntity.toDomain(): Product =
    Product(
        productId = productId,
        name = name,
        price = price,
        stock = stockAvailable,
    )
