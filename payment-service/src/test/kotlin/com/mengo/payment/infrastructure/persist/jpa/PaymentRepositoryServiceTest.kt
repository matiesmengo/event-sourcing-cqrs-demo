package com.mengo.payment.infrastructure.persist.jpa

import com.mengo.payment.domain.model.CompletedPayment
import com.mengo.payment.domain.model.FailedPayment
import com.mengo.payment.domain.model.Payment
import com.mengo.payment.domain.model.PendingPayment
import com.mengo.payment.infrastructure.persist.jpa.mappers.toEntity
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.Instant
import java.util.Optional
import java.util.UUID
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class PaymentRepositoryServiceTest {
    private val jpaRepository: PaymentJpaRepository = mock()
    private val repository = PaymentRepositoryService(jpaRepository)

    @ParameterizedTest
    @MethodSource("paymentsProvider")
    fun `save should persist and return domain`(payment: Payment) {
        // mock JPA repository save
        whenever(jpaRepository.save(any())).thenAnswer { it.arguments[0] as PaymentEntity }

        val result = repository.save(payment)

        verify(jpaRepository).save(any())
        assertEquals(payment.paymentId, result.paymentId)
        assertEquals(payment.bookingId, result.bookingId)
    }

    @ParameterizedTest
    @MethodSource("paymentsProvider")
    fun `findById should return domain when entity exists`(payment: Payment) {
        val entity = payment.toEntity()
        whenever(jpaRepository.findById(payment.paymentId)).thenReturn(Optional.of(entity))

        val result = repository.findById(payment.paymentId)

        verify(jpaRepository).findById(payment.paymentId)
        assertEquals(payment.paymentId, result?.paymentId)
        assertEquals(payment.bookingId, result?.bookingId)
    }

    @ParameterizedTest
    @MethodSource("paymentsProvider")
    fun `findById should return null when entity does not exist`(payment: Payment) {
        whenever(jpaRepository.findById(payment.paymentId)).thenReturn(Optional.empty())

        val result = repository.findById(payment.paymentId)

        verify(jpaRepository).findById(payment.paymentId)
        assertNull(result)
    }

    @Test
    fun `toDomain should throw IllegalArgumentException for unknown entity`() {
        val unknownEntity =
            object : PaymentEntity(
                paymentId = UUID.randomUUID(),
                bookingId = UUID.randomUUID(),
                createdAt = Instant.now(),
            ) {}

        whenever(jpaRepository.findById(any())).thenReturn(Optional.of(unknownEntity))

        val exception =
            assertFailsWith<IllegalArgumentException> {
                repository.findById(unknownEntity.paymentId)
            }

        assertEquals(
            "Unknown PaymentEntity subtype: ${unknownEntity::class}",
            exception.message,
        )
    }

    companion object {
        @JvmStatic
        fun paymentsProvider(): Stream<Arguments> =
            Stream.of(
                Arguments.of(
                    PendingPayment(
                        paymentId = UUID.randomUUID(),
                        bookingId = UUID.randomUUID(),
                    ),
                ),
                Arguments.of(
                    CompletedPayment(
                        paymentId = UUID.randomUUID(),
                        bookingId = UUID.randomUUID(),
                        reference = "ref-123",
                    ),
                ),
                Arguments.of(
                    FailedPayment(
                        paymentId = UUID.randomUUID(),
                        bookingId = UUID.randomUUID(),
                        reason = "insufficient funds",
                    ),
                ),
            )
    }
}
