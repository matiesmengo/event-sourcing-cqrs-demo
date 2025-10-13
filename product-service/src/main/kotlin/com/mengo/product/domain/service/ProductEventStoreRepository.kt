package com.mengo.product.domain.service

import com.mengo.product.domain.model.ProductEvent
import java.util.UUID

interface ProductEventStoreRepository {
    fun findByProductIdOrderByAggregateVersionAsc(productId: UUID): List<ProductEvent>

    fun save(productEvent: ProductEvent)
}
