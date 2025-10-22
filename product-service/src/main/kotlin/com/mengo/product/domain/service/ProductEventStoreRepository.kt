package com.mengo.product.domain.service

import com.mengo.product.domain.model.eventstore.ProductAggregate
import com.mengo.product.domain.model.eventstore.ProductEvent
import java.util.UUID

interface ProductEventStoreRepository {
    fun load(productId: UUID): ProductAggregate?

    fun append(event: ProductEvent)
}
