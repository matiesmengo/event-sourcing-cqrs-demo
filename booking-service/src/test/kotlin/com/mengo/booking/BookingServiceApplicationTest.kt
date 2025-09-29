package com.mengo.booking

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("integration-web")
class BookingServiceApplicationTest {
    @Test
    fun contextLoads() {
        // Spring context should start without errors
    }
}
