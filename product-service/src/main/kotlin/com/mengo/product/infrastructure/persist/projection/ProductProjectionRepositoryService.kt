package com.mengo.product.infrastructure.persist.projection

import com.mengo.product.domain.model.Product
import com.mengo.product.domain.service.ProductProjectionRepository
import com.mengo.product.infrastructure.persist.projection.mappers.toDomain
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
open class ProductProjectionRepositoryService(
    private val productProjectionMongoRepository: ProductProjectionMongoRepository,
) : ProductProjectionRepository {
    override fun findById(paymentId: UUID): Product? {
        val entity = productProjectionMongoRepository.findByProductId(productId = paymentId)
        return entity?.toDomain()
    }
}
