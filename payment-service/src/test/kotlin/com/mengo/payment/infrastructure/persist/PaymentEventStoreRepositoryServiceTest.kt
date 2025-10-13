package com.mengo.payment.infrastructure.persist

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.mengo.payment.domain.model.PaymentCompletedEvent
import com.mengo.payment.domain.model.PaymentFailedEvent
import com.mengo.payment.domain.model.PaymentInitiatedEvent
import com.mengo.payment.infrastructure.persist.eventstore.PaymentEventStoreJpaRepository
import com.mengo.payment.infrastructure.persist.eventstore.PaymentEventStoreRepositoryService
import com.mengo.payment.infrastructure.persist.eventstore.mapers.PaymentEventEntityMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.check
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal
import java.util.UUID

class PaymentEventStoreRepositoryServiceTest {
    private lateinit var jpaRepository: PaymentEventStoreJpaRepository

    @Autowired
    private lateinit var mapper: PaymentEventEntityMapper
    private lateinit var repository: PaymentEventStoreRepositoryService

    @BeforeEach
    fun setUp() {
        jpaRepository = mock()
        // TODO: refactor mapper
        mapper =
            PaymentEventEntityMapper(
                ObjectMapper().apply {
                    registerModule(KotlinModule.Builder().build())
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                },
            )
        repository = PaymentEventStoreRepositoryService(jpaRepository, mapper)
    }

    @Test
    fun `save should persist PaymentInitiatedEvent`() {
        // given
        val event =
            PaymentInitiatedEvent(
                paymentId = UUID.randomUUID(),
                bookingId = UUID.randomUUID(),
                totalAmount = BigDecimal("123.45"),
                aggregateVersion = 1,
            )

        // when
        repository.save(event)

        // then
        verify(jpaRepository).save(
            check { entity ->
                assert(entity.paymentId == event.paymentId)
                assert(entity.aggregateVersion == 1)
            },
        )
    }

    @Test
    fun `save should persist PaymentCompletedEvent`() {
        val event =
            PaymentCompletedEvent(
                paymentId = UUID.randomUUID(),
                bookingId = UUID.randomUUID(),
                reference = "ref-123",
                aggregateVersion = 2,
            )

        repository.save(event)

        verify(jpaRepository).save(
            check { entity ->
                assert(entity.paymentId == event.paymentId)
                assert(entity.aggregateVersion == 2)
            },
        )
    }

    @Test
    fun `save should persist PaymentFailedEvent`() {
        val event =
            PaymentFailedEvent(
                paymentId = UUID.randomUUID(),
                bookingId = UUID.randomUUID(),
                reason = "insufficient funds",
                aggregateVersion = 2,
            )

        repository.save(event)

        verify(jpaRepository).save(
            check { entity ->
                assert(entity.paymentId == event.paymentId)
                assert(entity.aggregateVersion == 2)
            },
        )
    }
}
