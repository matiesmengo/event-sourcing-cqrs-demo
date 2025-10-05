package com.mengo.e2e

import com.mengo.booking.model.CreateBookingRequest
import com.mengo.e2e.clients.BookingFeignClient
import com.mengo.e2e.infrastructure.AbstractServicesE2ETest
import com.mengo.e2e.infrastructure.KafkaTestConsumer
import com.mengo.payment.events.PaymentCompletedEvent
import feign.Feign
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import org.awaitility.Awaitility.await
import org.junit.Before
import org.springframework.cloud.openfeign.support.SpringMvcContract
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals

class BookingE2ETest : AbstractServicesE2ETest() {
    @Before
    fun startContainers() {
        bookingService.start()
        paymentService.start()
    }

    @Test
    fun `booking to payment flow E2E`() {
        // given
        val bookingClient = createBookingClient()

        val createRequest =
            CreateBookingRequest()
                .userId(UUID.randomUUID())
                .resourceId(UUID.randomUUID())

        // when
        val response = bookingClient.bookingsPost(createRequest)

        // Then
        await().atMost(15L, TimeUnit.SECONDS).until {
            getBookingStatus(response.bookingId)
        }

        await().atMost(15L, TimeUnit.SECONDS).until {
            println(paymentPostgres.logs)
            getPaymentStatus(response.bookingId)
        }

        await().atMost(15L, TimeUnit.SECONDS).until {
            getBookingStatus(response.bookingId)
        }

        KafkaTestConsumer(kafka.bootstrapServers).consumeAndAssert("booking.created") { msg ->
            assertEquals(response.bookingId.toString(), msg.key())
        }

        // TODO: WireMock external payment service - payment.completed or payment.failed
        KafkaTestConsumer(kafka.bootstrapServers).consumeAndAssert("payment.completed") { msg ->
            assertEquals(response.bookingId.toString(), (msg.value() as PaymentCompletedEvent).bookingId)
        }
    }

    private fun createBookingClient(): BookingFeignClient {
        val url = "http://${bookingService.host}:${bookingService.getMappedPort(8080)}"
        return Feign
            .builder()
            .encoder(JacksonEncoder())
            .decoder(JacksonDecoder())
            .contract(SpringMvcContract())
            .target(BookingFeignClient::class.java, url)
    }

    private fun getBookingStatus(bookingId: UUID): Boolean =
        queryStatus(
            jdbcUrl = bookingPostgres.jdbcUrl,
            schema = "booking",
            username = bookingPostgres.username,
            password = bookingPostgres.password,
            query = "SELECT 1 FROM bookings WHERE booking_id = ? LIMIT 1",
            parameters = listOf(bookingId),
        )

    private fun getPaymentStatus(bookingId: UUID): Boolean =
        queryStatus(
            jdbcUrl = paymentPostgres.jdbcUrl,
            schema = "payment",
            username = paymentPostgres.username,
            password = paymentPostgres.password,
            query = "SELECT 1 FROM payments WHERE booking_id = ? LIMIT 1",
            parameters = listOf(bookingId),
        )
}
