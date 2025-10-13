package com.mengo.product.infrastructure.persist.eventstore

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ProductEventStoreJpaRepository : JpaRepository<ProductEventEntity, UUID> {
    fun findByProductIdOrderByAggregateVersionAsc(productId: UUID): List<ProductEventEntity>
}
