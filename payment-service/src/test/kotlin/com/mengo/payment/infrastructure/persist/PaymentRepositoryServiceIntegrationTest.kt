package com.mengo.payment.infrastructure.persist

import com.mengo.payment.domain.model.CompletedPayment
import com.mengo.payment.domain.model.FailedPayment
import com.mengo.payment.fixtures.PaymentTestData
import com.mengo.postgres.test.PostgresTestContainerBase
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.temporal.ChronoUnit
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PaymentRepositoryServiceIntegrationTest : PostgresTestContainerBase() {
    @Autowired
    private lateinit var paymentRepositoryService: PaymentRepositoryService

    @Test
    fun `save and findById PendingPayment should persist and return payment`() {
        // given
        val pendingPayment = PaymentTestData.buildPendingPayment()

        // when
        val saved = paymentRepositoryService.save(pendingPayment)
        val fetched = paymentRepositoryService.findById(saved.paymentId)
        paymentRepositoryService.deleteById(saved.paymentId)

        // then
        assertNotNull(fetched)
        assertEquals(saved.paymentId, fetched.paymentId)
        assertEquals(saved.bookingId, fetched.bookingId)
        assertEquals(
            saved.createdAt.truncatedTo(ChronoUnit.MINUTES),
            fetched.createdAt.truncatedTo(ChronoUnit.MINUTES),
        )
    }

    @Test
    fun `save and findById CompletedPayment should persist and return payment`() {
        // given
        val pendingPayment = PaymentTestData.buildPendingPayment()
        val completedPayment = PaymentTestData.buildCompletedPayment()

        // when
        paymentRepositoryService.save(pendingPayment)
        paymentRepositoryService.update(completedPayment)
        val fetched = paymentRepositoryService.findById(completedPayment.paymentId)
        paymentRepositoryService.deleteById(completedPayment.paymentId)

        // then
        assertTrue(fetched is CompletedPayment)
        assertEquals(completedPayment.paymentId, fetched.paymentId)
        assertEquals(completedPayment.bookingId, fetched.bookingId)
        assertEquals(completedPayment.reference, fetched.reference)
        assertEquals(
            completedPayment.createdAt.truncatedTo(ChronoUnit.MINUTES),
            fetched.createdAt.truncatedTo(ChronoUnit.MINUTES),
        )
    }

    @Test
    fun `save and findById FailedPayment should persist and return payment`() {
        // given
        val pendingPayment = PaymentTestData.buildPendingPayment()
        val failedPayment = PaymentTestData.buildFailedPayment()

        // when
        paymentRepositoryService.save(pendingPayment)
        paymentRepositoryService.update(failedPayment)
        val fetched = paymentRepositoryService.findById(failedPayment.paymentId)
        paymentRepositoryService.deleteById(failedPayment.paymentId)

        // then
        assertTrue(fetched is FailedPayment)
        assertEquals(failedPayment.paymentId, fetched.paymentId)
        assertEquals(failedPayment.bookingId, fetched.bookingId)
        assertEquals(failedPayment.reason, fetched.reason)
        assertEquals(
            failedPayment.createdAt.truncatedTo(ChronoUnit.MINUTES),
            fetched.createdAt.truncatedTo(ChronoUnit.MINUTES),
        )
    }

    @Test
    fun `findById should return null when payment id not exist`() {
        // when
        val fetched = paymentRepositoryService.findById(UUID.randomUUID())

        // then
        assertNull(fetched)
    }
}
