package com.mengo.product.infrastructure.persist.eventstore

import com.mengo.product.domain.model.eventstore.ProductAggregate
import com.mengo.product.domain.model.eventstore.ProductEvent
import com.mengo.product.domain.service.ProductEventStoreRepository
import com.mengo.product.infrastructure.persist.eventstore.mappers.ProductEventEntityMapper
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
open class ProductEventStoreRepositoryService(
    private val productRepository: ProductEventStoreJpaRepository,
    private val productMapper: ProductEventEntityMapper,
) : ProductEventStoreRepository {
    override fun load(productId: UUID): ProductAggregate? {
        val entities = productRepository.findByProductIdOrderByAggregateVersionAsc(productId)
        if (entities.isEmpty()) return null

        val events = entities.map(productMapper::toDomain)
        return ProductAggregate.rehydrate(events)
    }

    override fun append(event: ProductEvent) {
        val currentVersion =
            productRepository
                .findFirstByProductIdOrderByAggregateVersionDesc(event.productId)
                ?.aggregateVersion
                ?: -1

        require(event.aggregateVersion == currentVersion + 1) {
            "Concurrency conflict: expected=${event.aggregateVersion}, actual=$currentVersion"
        }

        productRepository.save(productMapper.toEntity(event))
    }
}
