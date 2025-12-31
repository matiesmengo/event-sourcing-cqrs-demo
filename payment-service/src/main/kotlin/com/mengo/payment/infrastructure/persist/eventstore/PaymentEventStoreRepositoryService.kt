package com.mengo.payment.infrastructure.persist.eventstore

import com.mengo.payment.domain.model.events.PaymentAggregate
import com.mengo.payment.domain.model.events.PaymentEvent
import com.mengo.payment.domain.service.PaymentEventStoreRepository
import com.mengo.payment.infrastructure.persist.eventstore.mapers.PaymentEventEntityMapper
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Repository
open class PaymentEventStoreRepositoryService(
    private val entityManager: EntityManager,
    private val paymentRepository: PaymentEventStoreJpaRepository,
    private val paymentMapper: PaymentEventEntityMapper,
) : PaymentEventStoreRepository {
    @Transactional(propagation = Propagation.MANDATORY)
    override fun load(paymentId: UUID): PaymentAggregate? {
        val lockId = paymentId.mostSignificantBits
        entityManager
            .createNativeQuery("SELECT pg_advisory_xact_lock(:lockId)")
            .setParameter("lockId", lockId)
            .singleResult

        val entities = paymentRepository.findByPaymentIdOrderByAggregateVersionAsc(paymentId)
        val events = entities.map { paymentMapper.toDomain(it) }
        return if (events.isEmpty()) null else PaymentAggregate.rehydrate(events)
    }

    @Transactional(propagation = Propagation.MANDATORY)
    override fun append(event: PaymentEvent) {
        paymentRepository.save(paymentMapper.toEntity(event))
    }
}
