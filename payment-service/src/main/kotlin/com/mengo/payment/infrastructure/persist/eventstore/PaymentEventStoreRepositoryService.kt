package com.mengo.payment.infrastructure.persist.eventstore

import com.mengo.payment.domain.model.PaymentEvent
import com.mengo.payment.domain.service.PaymentEventStoreRepository
import com.mengo.payment.infrastructure.persist.eventstore.mapers.PaymentEventEntityMapper
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
open class PaymentEventStoreRepositoryService(
    private val paymentEventStoreRepository: PaymentEventStoreJpaRepository,
    private val paymentMapper: PaymentEventEntityMapper,
) : PaymentEventStoreRepository {
    override fun findById(paymentId: UUID): PaymentEvent? {
        val entities = paymentEventStoreRepository.findByPaymentId(paymentId)
        if (entities.isEmpty()) return null

        val latest = entities.maxBy { it.aggregateVersion }
        return paymentMapper.toDomain(latest)
    }

    override fun save(paymentEvent: PaymentEvent) {
        val entity = paymentMapper.toEntity(paymentEvent)
        paymentEventStoreRepository.save(entity)
    }
}
