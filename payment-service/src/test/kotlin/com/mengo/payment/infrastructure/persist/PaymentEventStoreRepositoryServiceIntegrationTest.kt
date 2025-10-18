package com.mengo.payment.infrastructure.persist

import com.mengo.payment.domain.model.PaymentCompletedEvent
import com.mengo.payment.domain.model.PaymentEvent
import com.mengo.payment.domain.model.PaymentFailedEvent
import com.mengo.payment.domain.model.PaymentInitiatedEvent
import com.mengo.payment.infrastructure.persist.eventstore.PaymentEventStoreRepositoryService
import com.mengo.postgres.test.PostgresTestContainerBase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal
import java.util.UUID
import java.util.stream.Stream

class PaymentEventStoreRepositoryServiceIntegrationTest : PostgresTestContainerBase() {
    @Autowired
    private lateinit var paymentEventStoreRepository: PaymentEventStoreRepositoryService

    @ParameterizedTest
    @MethodSource("paymentEventsProvider")
    fun `save and find PaymentEvent should persist and return event`(event: PaymentEvent) {
        // when
        paymentEventStoreRepository.save(event)

        // then
        val fetched = paymentEventStoreRepository.findById(event.paymentId)
        assertNotNull(fetched)
        assertEquals(event.paymentId, fetched?.paymentId)
        assertEquals(event.bookingId, fetched?.bookingId)
    }

    companion object {
        @JvmStatic
        fun paymentEventsProvider(): Stream<PaymentEvent> {
            val bookingId = UUID.randomUUID()
            return Stream.of(
                PaymentInitiatedEvent(
                    paymentId = UUID.randomUUID(),
                    bookingId = bookingId,
                    totalPrice = BigDecimal("123.45"),
                    aggregateVersion = 1,
                ),
                PaymentCompletedEvent(
                    paymentId = UUID.randomUUID(),
                    bookingId = bookingId,
                    reference = "ref-123",
                    aggregateVersion = 2,
                ),
                PaymentFailedEvent(
                    paymentId = UUID.randomUUID(),
                    bookingId = bookingId,
                    reason = "insufficient funds",
                    aggregateVersion = 2,
                ),
            )
        }
    }
}
