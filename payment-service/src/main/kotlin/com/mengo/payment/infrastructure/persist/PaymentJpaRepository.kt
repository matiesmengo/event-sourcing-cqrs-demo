package com.mengo.payment.infrastructure.persist

import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentJpaRepository : JpaRepository<PaymentEntity, UUID>
