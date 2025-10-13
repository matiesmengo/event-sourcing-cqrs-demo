package com.mengo.product.application

import com.mengo.product.domain.model.Booking
import com.mengo.product.domain.model.ProductAggregate
import com.mengo.product.domain.model.ProductReservedEvent
import com.mengo.product.domain.service.ProductEventPublisher
import com.mengo.product.domain.service.ProductEventStoreRepository
import com.mengo.product.domain.service.ProductService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class ProductServiceCommand(
    private val productEventStoreRepository: ProductEventStoreRepository,
    private val eventPublisher: ProductEventPublisher,
) : ProductService {
    // TODO: create projeccion Mongo DB after every eventStore change
    // TODO: create snapshot or Event pruning

    @Transactional
    override fun onBookingCreated(booking: Booking) {
        booking.products.forEach { item ->
            val events = productEventStoreRepository.findByProductIdOrderByAggregateVersionAsc(item.productId)
            val aggregate = ProductAggregate.rehydrate(events)

            if (aggregate.availableStock < item.quantity) {
                // TODO: Publish errors (after implementation of SAGA)
                // eventPublisher.publishProductNotAvailableStock(failedItem)
                throw RuntimeException("Handle Not Available Product is not implemented yet")
            } else {
                val newEvent =
                    ProductReservedEvent(
                        productId = item.productId,
                        bookingId = booking.bookingId,
                        quantity = item.quantity,
                        aggregateVersion = aggregate.lastEventVersion + 1,
                    )

                productEventStoreRepository.save(newEvent)
                eventPublisher.publishProductReserved(newEvent)
            }
        }
    }
}
