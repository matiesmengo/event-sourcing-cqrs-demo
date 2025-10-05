package com.mengo.payment.infrastructure.persist

import com.mengo.payment.domain.model.CompletedPayment
import com.mengo.payment.domain.model.FailedPayment
import com.mengo.payment.domain.model.Payment
import com.mengo.payment.domain.model.PendingPayment
import com.mengo.payment.domain.service.PaymentRepository
import com.mengo.payment.infrastructure.persist.mappers.toDomain
import com.mengo.payment.infrastructure.persist.mappers.toEntity
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
open class PaymentRepositoryService(
    private val paymentRepository: PaymentJpaRepository,
) : PaymentRepository {
    override fun findById(paymentId: UUID): Payment? =
        paymentRepository
            .findById(paymentId)
            .map { it.toDomain() }
            .orElse(null)

    override fun save(payment: PendingPayment): Payment {
        val paymentEntity = payment.toEntity()
        val savedPayment = paymentRepository.save(paymentEntity)
        return savedPayment.toDomain()
    }

    override fun update(payment: Payment) {
        when (payment) {
            is PendingPayment -> throw IllegalArgumentException("PendingPayment update is not allowed")
            is CompletedPayment -> paymentRepository.updateCompletedPayment(payment.toEntity())
            is FailedPayment -> paymentRepository.updateFailedPayment(payment.toEntity())
        }
    }

    override fun deleteById(paymentId: UUID) = paymentRepository.deleteById(paymentId)
}
