package com.mengo.product

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("integration-web")
class PaymentServiceApplicationTest {
    @Test
    fun contextLoads() {
        // Spring context should start without errors
    }
}
