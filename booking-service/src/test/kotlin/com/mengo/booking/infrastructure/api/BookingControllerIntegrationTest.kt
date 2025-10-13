package com.mengo.booking.infrastructure.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.mengo.booking.application.BookingServiceCommand
import com.mengo.booking.fixtures.minimalBookingApiRequestJson
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration-web")
class BookingControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var bookingServiceCommand: BookingServiceCommand

    @Test
    fun `bookingsPost should return 200 OK and call service`() {
        // given

        // when
        val result =
            mockMvc
                .perform(
                    post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(minimalBookingApiRequestJson.asString()),
                ).andExpect(status().isOk)
                .andReturn()

        // then
    }
}
