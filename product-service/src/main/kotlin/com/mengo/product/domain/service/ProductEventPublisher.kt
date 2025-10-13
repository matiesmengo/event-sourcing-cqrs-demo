package com.mengo.product.domain.service

import com.mengo.product.domain.model.ProductReservedEvent

fun interface ProductEventPublisher {
    fun publishProductReserved(productReservedEvent: ProductReservedEvent)
}
