package com.mengo.product.application

import com.mengo.product.domain.model.BookingCommand
import com.mengo.product.domain.model.BookingProduct
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
    // TODO: create projection Mongo DB after every eventStore change
    // TODO: create snapshot or Event pruning
    // TODO: refactor event store class (with/without aggregateVersion)

    @Transactional
    override fun onBookingCreated(product: BookingProduct) {
        val events = productEventStoreRepository.findByProductIdOrderByAggregateVersionAsc(product.productId)
        val aggregate = ProductAggregate.rehydrate(events)

        if (aggregate.availableStock < product.quantity) {
            eventPublisher.publishProductReservedFailed(
                BookingCommand.ReservedFailed(
                    bookingId = product.productId,
                    productId = product.bookingId,
                ),
            )
        } else {
            val newEvent =
                ProductReservedEvent(
                    productId = product.productId,
                    bookingId = product.bookingId,
                    quantity = product.quantity,
                    price = aggregate.price,
                    aggregateVersion = aggregate.lastEventVersion + 1,
                )

            productEventStoreRepository.save(newEvent)
            eventPublisher.publishProductReserved(
                BookingCommand.Reserved(
                    productId = product.productId,
                    bookingId = product.bookingId,
                    quantity = product.quantity,
                    price = aggregate.price,
                ),
            )
        }
    }
}
