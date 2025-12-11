package com.mengo.payment.infrastructure.persist.eventstore

import com.mengo.payment.domain.model.events.PaymentAggregate
import com.mengo.payment.domain.model.events.PaymentEvent
import com.mengo.payment.domain.service.PaymentEventStoreRepository
import com.mengo.payment.infrastructure.persist.eventstore.mapers.PaymentEventEntityMapper
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
open class PaymentEventStoreRepositoryService(
    private val paymentRepository: PaymentEventStoreJpaRepository,
    private val paymentMapper: PaymentEventEntityMapper,
) : PaymentEventStoreRepository {
    override fun load(paymentId: UUID): PaymentAggregate? {
        val entities = paymentRepository.findByPaymentIdOrderByAggregateVersionAsc(paymentId)
        val events = entities.map { paymentMapper.toDomain(it) }
        return if (events.isEmpty()) null else PaymentAggregate.rehydrate(events)
    }

    override fun append(event: PaymentEvent) {
        val currentVersion =
            paymentRepository
                .findFirstByPaymentIdOrderByAggregateVersionDesc(event.paymentId)
                ?.aggregateVersion
                ?: -1

        require(event.aggregateVersion == currentVersion + 1) {
            "Concurrency conflict: expected=${event.aggregateVersion}, actual=$currentVersion"
        }

        paymentRepository.save(paymentMapper.toEntity(event))
    }
}
