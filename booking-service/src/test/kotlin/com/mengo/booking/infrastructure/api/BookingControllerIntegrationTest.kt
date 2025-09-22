package com.mengo.booking.infrastructure.api

import com.example.booking.events.BookingCreated
import com.fasterxml.jackson.databind.ObjectMapper
import com.mengo.booking.domain.service.BookingRepository
import com.mengo.booking.fixtures.BookingConstants.RESOURCE_ID
import com.mengo.booking.fixtures.BookingConstants.USER_ID
import com.mengo.booking.fixtures.minimalBookingApiRequestJson
import com.mengo.booking.model.BookingResponse
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Duration

@SpringBootTest
@AutoConfigureMockMvc
@EmbeddedKafka(partitions = 1, topics = ["booking.created"])
@ActiveProfiles("test")
class BookingIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var embeddedKafkaBroker: EmbeddedKafkaBroker

    @Autowired
    private lateinit var bookingRepository: BookingRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var consumer: Consumer<String, BookingCreated>

    @BeforeEach
    fun setup() {
        val props =
            mapOf(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to embeddedKafkaBroker.brokersAsString,
                ConsumerConfig.GROUP_ID_CONFIG to "test-consumer",
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to KafkaAvroDeserializer::class.java,
                "schema.registry.url" to "mock://test",
                "specific.avro.reader" to true,
            )

        consumer = KafkaConsumer(props)
        consumer.subscribe(listOf("booking.created"))
    }

    @AfterEach
    fun cleanup() {
        consumer.close()
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

        val records = consumer.poll(Duration.ofSeconds(5))
        assertEquals(1, records.count())

        val received = records.iterator().next().value()
        assertEquals(USER_ID.toString(), received.userId)
        assertEquals(RESOURCE_ID.toString(), received.productId)
    }
}
