package com.mengo.product.domain.service

import com.mengo.product.domain.model.Product
import java.util.UUID

fun interface ProductProjectionRepository {
    fun findById(paymentId: UUID): Product?
}
