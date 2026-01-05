package com.mengo.booking.infrastructure.api

import com.mengo.booking.application.BookingServiceCommand
import com.mengo.booking.fixtures.minimalBookingApiRequestJson
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class BookingCommandControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var bookingServiceCommand: BookingServiceCommand

    @Test
    fun `should return 200 OK and call service`() {
        mockMvc
            .perform(
                post("/bookings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(minimalBookingApiRequestJson.asString()),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.bookingId").exists())
            .andExpect(jsonPath("$.status").value("CREATED"))

        verify(bookingServiceCommand).onCreateBooking(any())
    }
}
