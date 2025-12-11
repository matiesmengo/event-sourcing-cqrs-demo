package com.mengo.payment.infrastructure.persist.eventstore

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PaymentEventStoreJpaRepository : JpaRepository<PaymentEventEntity, UUID> {
    fun findByPaymentIdOrderByAggregateVersionAsc(paymentId: UUID): List<PaymentEventEntity>

    fun findFirstByPaymentIdOrderByAggregateVersionDesc(paymentId: UUID): PaymentEventEntity?
}
