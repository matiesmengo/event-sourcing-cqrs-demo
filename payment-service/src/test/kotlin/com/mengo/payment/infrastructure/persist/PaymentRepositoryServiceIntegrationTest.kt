package com.mengo.payment.infrastructure.persist

import com.mengo.payment.domain.model.CompletedPayment
import com.mengo.payment.domain.model.FailedPayment
import com.mengo.payment.fixtures.PaymentTestData
import com.mengo.postgres.test.PostgresTestContainerBase
import java.time.temporal.ChronoUnit
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

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
        assertEquals(saved.createdAt.truncatedTo(ChronoUnit.MINUTES),
            fetched.createdAt.truncatedTo(ChronoUnit.MINUTES))
    }

    @Test
    fun `save and findById CompletedPayment should persist and return payment`() {
       // given
        val completedPayment = PaymentTestData.buildCompletedPayment()

        // when
        val saved = paymentRepositoryService.save(completedPayment)
        val fetched = paymentRepositoryService.findById(saved.paymentId)
        paymentRepositoryService.deleteById(saved.paymentId)

        // then
        assertTrue(saved is CompletedPayment)
        assertTrue(fetched is CompletedPayment)
        assertEquals(saved.paymentId, fetched.paymentId)
        assertEquals(saved.bookingId, fetched.bookingId)
        assertEquals(saved.reference, fetched.reference)
        assertEquals(saved.createdAt.truncatedTo(ChronoUnit.MINUTES),
            fetched.createdAt.truncatedTo(ChronoUnit.MINUTES))
    }

    @Test
    fun `save and findById FailedPayment should persist and return payment`() {
       // given
        val failedPayment = PaymentTestData.buildFailedPayment()

        // when
        val saved = paymentRepositoryService.save(failedPayment)
        val fetched = paymentRepositoryService.findById(saved.paymentId)
        paymentRepositoryService.deleteById(saved.paymentId)

        // then
        assertTrue(saved is FailedPayment)
        assertTrue(fetched is FailedPayment)
        assertEquals(saved.paymentId, fetched.paymentId)
        assertEquals(saved.bookingId, fetched.bookingId)
        assertEquals(saved.reason, fetched.reason)
        assertEquals(saved.createdAt.truncatedTo(ChronoUnit.MINUTES),
            fetched.createdAt.truncatedTo(ChronoUnit.MINUTES))
    }


    @Test
    fun `findById should return null when payment id not exist`() {
        // when
        val fetched = paymentRepositoryService.findById(UUID.randomUUID())

        // then
        assertNull(fetched)
    }
}
