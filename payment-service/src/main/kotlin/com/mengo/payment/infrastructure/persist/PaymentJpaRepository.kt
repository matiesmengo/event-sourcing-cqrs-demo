package com.mengo.payment.infrastructure.persist

import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface PaymentJpaRepository : JpaRepository<PaymentEntity, UUID> {
    @Modifying
    @Transactional
    @Query(
        """
        UPDATE PaymentEntity p 
        SET p.reference = :#{#completed.reference}, 
        p.paymentStatus = 'COMPLETED'
        WHERE p.paymentId = :#{#completed.paymentId}
        """,
    )
    fun updateCompletedPayment(
        @Param("completed") completed: PaymentEntity,
    ): Int

    @Modifying
    @Transactional
    @Query(
        """
        UPDATE PaymentEntity p 
        SET p.reason = :#{#failed.reason}, 
        p.paymentStatus = 'FAILED'
        WHERE p.paymentId = :#{#failed.paymentId}
        """,
    )
    fun updateFailedPayment(
        @Param("failed") failed: PaymentEntity,
    ): Int
}
