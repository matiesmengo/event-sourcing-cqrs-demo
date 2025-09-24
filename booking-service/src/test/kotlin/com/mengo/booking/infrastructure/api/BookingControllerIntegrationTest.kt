package com.mengo.booking.infrastructure.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.mengo.booking.domain.service.BookingRepository
import com.mengo.booking.events.BookingCreatedEvent
import com.mengo.booking.fixtures.BookingConstants.RESOURCE_ID
import com.mengo.booking.fixtures.BookingConstants.USER_ID
import com.mengo.booking.fixtures.minimalBookingApiRequestJson
import com.mengo.booking.infrastructure.events.KafkaTopics.KAFKA_BOOKING_CREATED
import com.mengo.booking.model.BookingResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@EmbeddedKafka(partitions = 1, topics = ["booking.created"])
@ActiveProfiles("test")
class BookingIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var bookingRepository: BookingRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val receivedEvents = mutableListOf<BookingCreatedEvent>()

    @KafkaListener(topics = [KAFKA_BOOKING_CREATED], groupId = "test-consumer")
    fun listen(event: BookingCreatedEvent) {
        receivedEvents.add(event)
    }

    @Test
    fun `bookingsPost should return a 200 OK with the created booking and publish Kafka message`() {
        val mvcResult =
            mockMvc
                .perform(
                    post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(minimalBookingApiRequestJson.asString()),
                ).andExpect(status().isOk)
                .andReturn()

        val responseBody = mvcResult.response.contentAsString
        val response = objectMapper.readValue(responseBody, BookingResponse::class.java)

        val saved = bookingRepository.findById(response.bookingId)
        assertEquals(saved.bookingId, response.bookingId)

        Thread.sleep(1000)
        assertEquals(1, receivedEvents.size)
        val received = receivedEvents.first()
        assertEquals(USER_ID.toString(), received.userId)
        assertEquals(RESOURCE_ID.toString(), received.resourceId)
    }
}
