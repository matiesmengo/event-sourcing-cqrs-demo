package com.mengo.product.infrastructure.persist.eventstore

import com.mengo.product.domain.model.eventstore.ProductAggregate
import com.mengo.product.domain.model.eventstore.ProductEvent
import com.mengo.product.domain.service.ProductEventStoreRepository
import com.mengo.product.infrastructure.persist.eventstore.mappers.ProductEventEntityMapper
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Repository
open class ProductEventStoreRepositoryService(
    private val entityManager: EntityManager,
    private val productRepository: ProductEventStoreJpaRepository,
    private val productMapper: ProductEventEntityMapper,
) : ProductEventStoreRepository {
    @Transactional(propagation = Propagation.MANDATORY)
    override fun load(productId: UUID): ProductAggregate? {
        val lockId = productId.mostSignificantBits
        entityManager
            .createNativeQuery("SELECT pg_advisory_xact_lock(:lockId)")
            .setParameter("lockId", lockId)
            .singleResult

        val entities = productRepository.findByProductIdOrderByAggregateVersionAsc(productId)
        if (entities.isEmpty()) return null

        val events = entities.map(productMapper::toDomain)
        return ProductAggregate.rehydrate(events)
    }

    @Transactional(propagation = Propagation.MANDATORY)
    override fun append(event: ProductEvent) {
        productRepository.save(productMapper.toEntity(event))
    }
}
