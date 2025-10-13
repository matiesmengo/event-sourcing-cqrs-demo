package com.mengo.product.infrastructure.persist.projection

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ProductProjectionMongoRepository : MongoRepository<ProductProjectionEntity, UUID> {
    fun findByProductId(productId: UUID): ProductProjectionEntity?
}
