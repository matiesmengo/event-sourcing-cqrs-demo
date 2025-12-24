package com.mengo.booking.infrastructure.persist

import com.mengo.architecture.test.infrastructure.AbstractIntegrationTest
import com.mengo.booking.domain.model.BookingCommand
import com.mengo.booking.domain.model.BookingItem
import com.mengo.booking.domain.model.BookingStatus
import com.mengo.booking.fixtures.BookingConstants.BOOKING_ID
import com.mengo.booking.fixtures.BookingConstants.PAYMENT_REASON
import com.mengo.booking.fixtures.BookingConstants.PAYMENT_REFERENCE
import com.mengo.booking.fixtures.BookingConstants.PRODUCT_ID
import com.mengo.booking.fixtures.BookingConstants.PRODUCT_PRICE
import com.mengo.booking.fixtures.BookingConstants.USER_ID
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class BookingProjectionRepositoryServiceIntegrationTest : AbstractIntegrationTest() {
    @Autowired
    private lateinit var repositoryService: BookingProjectionRepositoryService

    @Autowired
    private lateinit var mongoRepository: BookingProjectionMongoRepository

    @BeforeEach
    fun cleanDatabase() {
        mongoRepository.deleteAll()
    }

    @Test
    fun `should persist a complete booking when save is called`() {
        // given
        val now = Instant.now()
        val command =
            BookingCommand.Create(
                bookingId = BOOKING_ID,
                userId = USER_ID,
                items = listOf(BookingItem(PRODUCT_ID, 2)),
                timestamp = now,
            )

        // when
        repositoryService.save(command)

        // then
        val found = repositoryService.findById(BOOKING_ID)
        assertNotNull(found)
        assertEquals(BookingStatus.CREATED, found.status)
    }

    @Test
    fun `should maintain newest status when create event arrives late`() {
        // given
        val t10h10 = Instant.parse("2025-12-24T10:10:00Z")
        repositoryService.updateStatus(BookingCommand.Status(BOOKING_ID, BookingStatus.PAID, null, t10h10))

        // when
        val t10h00 = Instant.parse("2025-12-24T10:00:00Z")
        val createCommand =
            BookingCommand.Create(
                bookingId = BOOKING_ID,
                userId = USER_ID,
                items = listOf(BookingItem(PRODUCT_ID, 1)),
                timestamp = t10h00,
            )
        repositoryService.save(createCommand)

        // then
        val result = repositoryService.findById(BOOKING_ID)
        assertNotNull(result)
        assertEquals(BookingStatus.PAID, result.status)
        assertEquals(USER_ID, result.userId)
        assertEquals(1, result.items.size)
    }

    @Test
    fun `should ignore price update when older timestamp is received`() {
        // given
        val t09h00 = Instant.parse("2025-12-24T10:00:00Z")
        val t10h05 = Instant.parse("2025-12-24T10:05:00Z")
        repositoryService.save(
            BookingCommand.Create(
                bookingId = BOOKING_ID,
                userId = USER_ID,
                items = listOf(BookingItem(productId = PRODUCT_ID, quantity = 1)),
                timestamp = t09h00,
            ),
        )
        repositoryService.updateProductPrice(
            BookingCommand.Price(BOOKING_ID, PRODUCT_ID, BigDecimal("50.00"), t10h05),
        )

        // when
        val t10h00 = Instant.parse("2025-12-24T10:00:00Z")
        repositoryService.updateProductPrice(
            BookingCommand.Price(BOOKING_ID, PRODUCT_ID, BigDecimal("10.00"), t10h00),
        )

        // then
        val result = repositoryService.findById(BOOKING_ID)
        val item = result?.items?.find { it.productId == PRODUCT_ID }
        assertNotNull(item)
        assertEquals(0, BigDecimal("50.00").compareTo(item.price))
    }

    @Test
    fun `should upsert and persist price data when update arrives without previous create`() {
        // given
        val eventTimestamp = Instant.now()
        val priceCommand = BookingCommand.Price(BOOKING_ID, PRODUCT_ID, PRODUCT_PRICE, eventTimestamp)
        val priceCommand2 = BookingCommand.Price(BOOKING_ID, UUID.randomUUID(), PRODUCT_PRICE, eventTimestamp)

        // when
        repositoryService.updateProductPrice(priceCommand)
        repositoryService.updateProductPrice(priceCommand2)

        // then
        val found = repositoryService.findById(BOOKING_ID)
        assertNotNull(found)
        val item = found.items.find { it.productId == PRODUCT_ID }
        assertNotNull(item)
        assertEquals(0, PRODUCT_PRICE.compareTo(item.price))
        assertEquals(2, found.items.size)
    }

    @Test
    fun `should upsert and persist status data when update arrives without previous create`() {
        // given when
        repositoryService.updateStatus(
            BookingCommand.Status(
                BOOKING_ID,
                BookingStatus.CANCELLED,
                PAYMENT_REASON,
                Instant.now(),
            ),
        )

        // then
        val found = repositoryService.findById(BOOKING_ID)
        assertNotNull(found)
        assertEquals(BookingStatus.CANCELLED, found.status)
        assertEquals(PAYMENT_REASON, found.cancelReason)
    }

    @Test
    fun `should update payment data only when timestamp is newer`() {
        // given
        val t10h00 = Instant.parse("2025-12-24T10:00:00Z")
        repositoryService.updateStatus(BookingCommand.Status(BOOKING_ID, BookingStatus.CREATED, null, t10h00))

        // when
        val t10h10 = Instant.parse("2025-12-24T10:10:00Z")
        repositoryService.updatePayment(
            BookingCommand.Payment(
                BOOKING_ID,
                PAYMENT_REFERENCE,
                BookingStatus.CONFIRMED,
                t10h10,
            ),
        )

        // then
        val entity = mongoRepository.findById(BOOKING_ID.toString()).get()
        assertEquals(BookingStatus.CONFIRMED.toString(), entity.status)
        assertEquals(PAYMENT_REFERENCE, entity.paymentReference)

        // and when
        val t10h05 = Instant.parse("2025-12-24T10:05:00Z")
        repositoryService.updatePayment(
            BookingCommand.Payment(
                BOOKING_ID,
                PAYMENT_REFERENCE,
                BookingStatus.PAYMENT_FAILED,
                t10h05,
            ),
        )

        // and then
        val entityAfterOld = mongoRepository.findById(BOOKING_ID.toString()).get()
        assertEquals(BookingStatus.CONFIRMED.toString(), entityAfterOld.status)
    }
}
