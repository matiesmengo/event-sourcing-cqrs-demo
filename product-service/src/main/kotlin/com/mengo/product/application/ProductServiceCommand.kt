package com.mengo.product.application

import com.mengo.product.domain.model.command.BookingCommand
import com.mengo.product.domain.model.command.SagaCommand
import com.mengo.product.domain.service.ProductEventPublisher
import com.mengo.product.domain.service.ProductEventStoreRepository
import com.mengo.product.domain.service.ProductService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class ProductServiceCommand(
    private val eventStoreRepository: ProductEventStoreRepository,
    private val eventPublisher: ProductEventPublisher,
) : ProductService {
    // TODO: create projection Mongo DB after every eventStore change
    // TODO: create snapshot or Event pruning

    @Transactional
    override fun onReserveProduct(command: SagaCommand.ReserveProduct) {
        val aggregate = eventStoreRepository.load(command.productId) ?: error("This product doesn't exist")

        if (aggregate.availableStock < command.quantity) {
            eventPublisher.publishProductReservedFailed(
                BookingCommand.ReservedFailed(
                    bookingId = command.productId,
                    productId = command.bookingId,
                ),
            )
        } else {
            eventStoreRepository.append(
                aggregate.reserveProduct(
                    command.productId,
                    command.bookingId,
                    command.quantity,
                ),
            )

            eventPublisher.publishProductReserved(
                BookingCommand.Reserved(
                    productId = command.productId,
                    bookingId = command.bookingId,
                    quantity = command.quantity,
                    price = aggregate.price,
                ),
            )
        }
    }

    @Transactional
    override fun onReleaseProduct(command: SagaCommand.ReleaseProduct) {
        val aggregate = eventStoreRepository.load(command.productId) ?: error("This product doesn't exist")

        eventStoreRepository.append(
            aggregate.releaseProduct(
                command.productId,
                command.bookingId,
                command.quantity,
            ),
        )
    }
}
