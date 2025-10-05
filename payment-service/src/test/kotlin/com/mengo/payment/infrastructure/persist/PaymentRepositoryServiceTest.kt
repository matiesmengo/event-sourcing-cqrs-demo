package com.mengo.payment.infrastructure.persist

import com.mengo.payment.domain.model.CompletedPayment
import com.mengo.payment.domain.model.FailedPayment
import com.mengo.payment.domain.model.Payment
import com.mengo.payment.domain.model.PendingPayment
import com.mengo.payment.infrastructure.persist.mappers.toEntity
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.Optional
import java.util.UUID
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PaymentRepositoryServiceTest {
    private val jpaRepository: PaymentJpaRepository = mock()
    private val repository = PaymentRepositoryService(jpaRepository)

    @ParameterizedTest
    @MethodSource("paymentsProvider")
    fun `findById should return domain when entity exists`(payment: Payment) {
        // given
        val entity = payment.toEntity()
        whenever(jpaRepository.findById(payment.paymentId)).thenReturn(Optional.of(entity))

        // when
        val result = repository.findById(payment.paymentId)

        // then
        verify(jpaRepository).findById(payment.paymentId)
        assertEquals(payment.paymentId, result?.paymentId)
        assertEquals(payment.bookingId, result?.bookingId)
    }

    @ParameterizedTest
    @MethodSource("paymentsProvider")
    fun `findById should return null when entity does not exist`(payment: Payment) {
        // given
        whenever(jpaRepository.findById(payment.paymentId)).thenReturn(Optional.empty())

        // when
        val result = repository.findById(payment.paymentId)

        // then
        verify(jpaRepository).findById(payment.paymentId)
        assertNull(result)
    }

    @Test
    fun `save should persist and return domain`() {
        // given
        val input =
            PendingPayment(
                paymentId = UUID.randomUUID(),
                bookingId = UUID.randomUUID(),
            )
        whenever(jpaRepository.save(any())).thenAnswer { it.arguments[0] as PaymentEntity }

        // when
        val result = repository.save(input)

        // then
        verify(jpaRepository).save(any())
        assertEquals(input.paymentId, result.paymentId)
        assertEquals(input.bookingId, result.bookingId)
    }

    @Test
    fun `update CompletedPayment should persist`() {
        // given
        val input =
            CompletedPayment(
                paymentId = UUID.randomUUID(),
                bookingId = UUID.randomUUID(),
                reference = "ref-123",
            )

        // when
        repository.update(input)

        // then
        verify(jpaRepository).updateCompletedPayment(any())
    }

    @Test
    fun `update FailedPayment should persist`() {
        // given
        val input =
            FailedPayment(
                paymentId = UUID.randomUUID(),
                bookingId = UUID.randomUUID(),
                reason = "insufficient funds",
            )

        // when
        repository.update(input)

        // then
        verify(jpaRepository).updateFailedPayment(any())
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
