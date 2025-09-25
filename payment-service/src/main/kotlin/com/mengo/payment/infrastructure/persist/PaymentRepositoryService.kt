package com.mengo.payment.infrastructure.persist

import com.mengo.payment.domain.model.Payment
import com.mengo.payment.domain.service.PaymentRepository
import com.mengo.payment.infrastructure.persist.mappers.toDomain
import com.mengo.payment.infrastructure.persist.mappers.toEntity
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class PaymentRepositoryService(
    private val paymentRepository: PaymentJpaRepository,
) : PaymentRepository {
    override fun save(payment: Payment): Payment = paymentRepository.save(payment.toEntity()).toDomain()

    override fun findById(paymentId: UUID): Payment? =
        paymentRepository
            .findById(paymentId)
            .map { it.toDomain() }
            .orElse(null)
}
