package com.mengo.booking.application

import com.mengo.booking.domain.model.BookingCommand
import com.mengo.booking.domain.model.BookingEventItem
import com.mengo.booking.domain.model.BookingQueryEvent
import com.mengo.booking.domain.model.BookingStatus
import com.mengo.booking.domain.service.BookingProjectionRepository
import com.mengo.booking.fixtures.BookingConstants.BOOKING_ID
import com.mengo.booking.fixtures.BookingConstants.PAYMENT_REASON
import com.mengo.booking.fixtures.BookingConstants.PRODUCT_ID
import com.mengo.booking.fixtures.BookingConstants.PRODUCT_PRICE
import com.mengo.booking.fixtures.BookingConstants.USER_ID
import org.junit.jupiter.api.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import java.time.Instant
import kotlin.test.assertEquals

class BookingUpdateServiceTest {
    private val repository: BookingProjectionRepository = mock()
    private val service = BookingUpdateService(repository)

    private val now = Instant.now()

    @Test
    fun `should call save when handle created is called`() {
        // given
        val event =
            BookingQueryEvent.Created(
                bookingId = BOOKING_ID,
                userId = USER_ID,
                items = listOf(BookingEventItem(PRODUCT_ID, 2)),
                timestamp = now,
            )

        // when
        service.handleCreated(event)

        // then
        val captor = argumentCaptor<BookingCommand.Create>()
        verify(repository, times(1)).save(captor.capture())

        val command = captor.firstValue
        assertEquals(event.bookingId, command.bookingId)
        assertEquals(event.userId, command.userId)
        assertEquals(event.timestamp, command.timestamp)
    }

    @Test
    fun `should call update product price when handle product reserved is called`() {
        // given
        val event =
            BookingQueryEvent.ProductReserved(
                bookingId = BOOKING_ID,
                productId = PRODUCT_ID,
                price = PRODUCT_PRICE,
                timestamp = now,
            )

        // when
        service.handleProductReserved(event)

        // then
        val captor = argumentCaptor<BookingCommand.Price>()
        verify(repository).updateProductPrice(captor.capture())

        val command = captor.firstValue
        assertEquals(event.bookingId, command.bookingId)
        assertEquals(event.productId, command.productId)
        assertEquals(event.price, command.price)
    }

    @Test
    fun `should call update payment with paid status when handle payment completed is called`() {
        // given
        val event =
            BookingQueryEvent.PaymentProcessed(
                bookingId = BOOKING_ID,
                reference = "REF-123",
                timestamp = now,
            )

        // when
        service.handlePaymentCompleted(event)

        // then
        val captor = argumentCaptor<BookingCommand.Payment>()
        verify(repository).updatePayment(captor.capture())

        val command = captor.firstValue
        assertEquals(event.bookingId, command.bookingId)
        assertEquals(event.reference, command.reference)
        assertEquals(BookingStatus.PAID, command.status)
    }

    @Test
    fun `should call update status when handle status change is called`() {
        // given
        val event =
            BookingQueryEvent.StatusChanged(
                bookingId = BOOKING_ID,
                status = BookingStatus.CANCELLED,
                reason = PAYMENT_REASON,
                timestamp = now,
            )

        // when
        service.handleStatusChange(event)

        // then
        val captor = argumentCaptor<BookingCommand.Status>()
        verify(repository).updateStatus(captor.capture())

        val command = captor.firstValue
        assertEquals(event.bookingId, command.bookingId)
        assertEquals(event.status, command.status)
        assertEquals(event.reason, command.reason)
    }
}
