import com.mengo.orchestrator.domain.model.Product
import java.util.UUID

sealed class OrchestratorEvent {
    abstract val bookingId: UUID
    abstract val aggregateVersion: Int

    data class Created(
        override val bookingId: UUID,
        val expectedProducts: Set<Product>,
        override val aggregateVersion: Int,
    ) : OrchestratorEvent()

    data class ProductReserved(
        override val bookingId: UUID,
        val product: Product,
        override val aggregateVersion: Int,
    ) : OrchestratorEvent()

    data class ProductReservationFailed(
        override val bookingId: UUID,
        val productId: UUID,
        override val aggregateVersion: Int,
    ) : OrchestratorEvent()

    data class CompensatedProduct(
        override val bookingId: UUID,
        val product: Product,
        override val aggregateVersion: Int,
    ) : OrchestratorEvent()

    data class PaymentCompleted(
        override val bookingId: UUID,
        override val aggregateVersion: Int,
    ) : OrchestratorEvent()

    data class PaymentFailed(
        override val bookingId: UUID,
        override val aggregateVersion: Int,
    ) : OrchestratorEvent()
}
