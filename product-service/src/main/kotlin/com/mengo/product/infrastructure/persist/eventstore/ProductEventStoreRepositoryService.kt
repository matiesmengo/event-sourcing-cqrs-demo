package com.mengo.product.infrastructure.persist.eventstore

import com.mengo.product.domain.model.ProductEvent
import com.mengo.product.domain.service.ProductEventStoreRepository
import com.mengo.product.infrastructure.persist.eventstore.mappers.ProductEventEntityMapper
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
open class ProductEventStoreRepositoryService(
    private val productEventStoreRepository: ProductEventStoreJpaRepository,
    private val productMapper: ProductEventEntityMapper,
) : ProductEventStoreRepository {
    override fun findByProductIdOrderByAggregateVersionAsc(productId: UUID): List<ProductEvent> {
        val entities = productEventStoreRepository.findByProductIdOrderByAggregateVersionAsc(productId)
        return entities.map { productMapper.toDomain(it) }
    }

    override fun save(productEvent: ProductEvent) {
        val entity = productMapper.toEntity(productEvent)
        productEventStoreRepository.save(entity)
    }
}
