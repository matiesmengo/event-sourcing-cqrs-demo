package com.mengo.booking.infrastructure.persist

import com.mengo.booking.domain.model.BookingCreatedEvent
import com.mengo.booking.domain.model.BookingPaymentConfirmedEvent
import com.mengo.booking.domain.model.BookingPaymentFailedEvent
import com.mengo.booking.fixtures.BookingTestData.buildBookingCreatedEvent
import com.mengo.booking.fixtures.BookingTestData.buildBookingPaymentConfirmedEvent
import com.mengo.booking.fixtures.BookingTestData.buildBookingPaymentFailedEvent
import com.mengo.postgres.test.PostgresTestContainerBase
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BookingEventStoreRepositoryServiceIntegrationTest : PostgresTestContainerBase() {
    @Autowired
    private lateinit var bookingEventStoreRepositoryService: BookingEventStoreRepositoryService

    @Test
    fun `should persist and retrieve BookingCreatedEvent`() {
        // given
        val bookingId = UUID.randomUUID()
        val booking = buildBookingCreatedEvent(bookingId = bookingId)

        // when
        bookingEventStoreRepositoryService.save(booking)
        val fetched = bookingEventStoreRepositoryService.findById(booking.bookingId)

        // then
        assertNotNull(fetched)
        assertTrue(fetched is BookingCreatedEvent)
        assertEquals(booking.bookingId, fetched.bookingId)
        assertEquals(booking.aggregateVersion, fetched.aggregateVersion)
    }

    @Test
    fun `should persist and retrieve BookingPaymentConfirmedEvent`() {
        // given
        val bookingId = UUID.randomUUID()
        val booking = buildBookingPaymentConfirmedEvent(bookingId = bookingId)

        // when
        bookingEventStoreRepositoryService.save(booking)
        val fetched = bookingEventStoreRepositoryService.findById(booking.bookingId)

        // then
        assertNotNull(fetched)
        assertTrue(fetched is BookingPaymentConfirmedEvent)
        assertEquals(booking.bookingId, fetched.bookingId)
        assertEquals(booking.aggregateVersion, fetched.aggregateVersion)
    }

    @Test
    fun `should persist and retrieve BookingPaymentFailedEvent`() {
        // given
        val bookingId = UUID.randomUUID()
        val booking = buildBookingPaymentFailedEvent(bookingId = bookingId)

        // when
        bookingEventStoreRepositoryService.save(booking)
        val fetched = bookingEventStoreRepositoryService.findById(booking.bookingId)

        // then
        assertNotNull(fetched)
        assertTrue(fetched is BookingPaymentFailedEvent)
        assertEquals(booking.bookingId, fetched.bookingId)
        assertEquals(booking.aggregateVersion, fetched.aggregateVersion)
    }
}
