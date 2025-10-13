package com.mengo.product.infrastructure.events

import com.mengo.booking.payload.BookingCreatedPayload
import com.mengo.product.domain.service.ProductService
import com.mengo.product.infrastructure.events.mappers.toDomain
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class BookingKafkaListener(
    private val productService: ProductService,
) {
    @KafkaListener(topics = [KafkaTopics.KAFKA_BOOKING_CREATED], groupId = "product-service-group")
    fun consumeBookingCreatedEvent(payload: BookingCreatedPayload) {
        val bookingDomain = payload.toDomain()
        productService.onBookingCreated(bookingDomain)
    }
}
