package com.mengo.booking.infrastructure.persist

import com.mengo.booking.domain.model.BookingCommand
import com.mengo.booking.domain.model.BookingReadModel
import com.mengo.booking.domain.service.BookingProjectionRepository
import com.mengo.booking.infrastructure.persist.mappers.toDomain
import com.mongodb.client.result.UpdateResult
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

@Repository
class BookingProjectionRepositoryService(
    private val repository: BookingProjectionMongoRepository,
    private val mongoTemplate: MongoTemplate,
) : BookingProjectionRepository {
    override fun findById(id: UUID): BookingReadModel? = repository.findById(id.toString()).map { it.toDomain() }.orElse(null)

    override fun save(data: BookingCommand.Create) {
        val query =
            Query(
                Criteria
                    .where("_id")
                    .`is`(data.bookingId.toString())
                    .orOperator(
                        Criteria.where("lastEventTimestamp").exists(false),
                        Criteria.where("lastEventTimestamp").lt(data.timestamp),
                    ),
            )

        val update =
            Update()
                .set("userId", data.userId.toString())
                .set("items", data.items.map { ItemProjectionEntity(it.productId.toString(), it.quantity) })
                .set("updatedAt", Instant.now())
                .set("lastEventTimestamp", data.timestamp)
                .setOnInsert("status", data.status.toString())

        val result = mongoTemplate.updateFirst(query, update, BookingProjectionEntity::class.java)

        if (result.matchedCount == 0L) {
            val exists = repository.existsById(data.bookingId.toString())
            if (!exists) {
                val rootQuery = Query(Criteria.where("_id").`is`(data.bookingId.toString()))
                mongoTemplate.upsert(rootQuery, update, BookingProjectionEntity::class.java)
            } else {
                val patchUpdate =
                    Update()
                        .set("userId", data.userId.toString())
                        .set("items", data.items.map { ItemProjectionEntity(it.productId.toString(), it.quantity) })

                val queryMissingFields =
                    Query(
                        Criteria
                            .where("_id")
                            .`is`(data.bookingId.toString())
                            .orOperator(
                                Criteria.where("userId").exists(false),
                                Criteria.where("items").size(0),
                            ),
                    )
                mongoTemplate.updateFirst(queryMissingFields, patchUpdate, BookingProjectionEntity::class.java)
            }
        }
    }

    override fun updateProductPrice(data: BookingCommand.Price) {
        val updateResult = tryUpdateExistingItem(data)

        if (updateResult.matchedCount == 0L) {
            upsertBookingWithNewItem(data)
        }
    }

    private fun tryUpdateExistingItem(data: BookingCommand.Price): UpdateResult {
        val itemCriteria =
            Criteria
                .where("productId")
                .`is`(data.productId.toString())
                .orOperator(
                    Criteria.where("lastPriceUpdateTimestamp").exists(false),
                    Criteria.where("lastPriceUpdateTimestamp").lt(data.timestamp),
                )

        val query =
            Query(
                Criteria
                    .where("_id")
                    .`is`(data.bookingId.toString())
                    .and("items")
                    .elemMatch(itemCriteria),
            )

        val update =
            Update()
                .set("items.$.unitPrice", data.price)
                .set("items.$.lastPriceUpdateTimestamp", data.timestamp)
                .set("updatedAt", Instant.now())

        return mongoTemplate.updateFirst(query, update, BookingProjectionEntity::class.java)
    }

    private fun upsertBookingWithNewItem(data: BookingCommand.Price) {
        val rootQuery = Query(Criteria.where("_id").`is`(data.bookingId.toString()))

        val addNewItem =
            Update()
                .setOnInsert("status", "CREATED")
                .set("updatedAt", Instant.now())
                .set("lastEventTimestamp", data.timestamp)
                .addToSet(
                    "items",
                    ItemProjectionEntity(
                        productId = data.productId.toString(),
                        quantity = 0, // No la sabem encara
                        unitPrice = data.price,
                        lastPriceUpdateTimestamp = data.timestamp,
                    ),
                )

        mongoTemplate.upsert(rootQuery, addNewItem, BookingProjectionEntity::class.java)
    }

    override fun updatePayment(data: BookingCommand.Payment) {
        val query =
            Query(
                Criteria
                    .where("_id")
                    .`is`(data.bookingId.toString())
                    .orOperator(
                        Criteria.where("lastEventTimestamp").exists(false),
                        Criteria.where("lastEventTimestamp").lt(data.timestamp),
                    ),
            )

        val update =
            Update()
                .set("paymentReference", data.reference)
                .set("status", data.status)
                .set("lastEventTimestamp", data.timestamp)
                .set("updatedAt", Instant.now())

        val result = mongoTemplate.updateFirst(query, update, BookingProjectionEntity::class.java)

        if (result.matchedCount == 0L && !repository.existsById(data.bookingId.toString())) {
            val rootQuery = Query(Criteria.where("_id").`is`(data.bookingId.toString()))
            mongoTemplate.upsert(rootQuery, update, BookingProjectionEntity::class.java)
        }
    }

    override fun updateStatus(data: BookingCommand.Status) {
        val query =
            Query(
                Criteria
                    .where("_id")
                    .`is`(data.bookingId.toString())
                    .orOperator(
                        Criteria.where("lastEventTimestamp").exists(false),
                        Criteria.where("lastEventTimestamp").lt(data.timestamp),
                    ),
            )

        val update =
            Update()
                .set("status", data.status)
                .set("lastEventTimestamp", data.timestamp)
                .set("updatedAt", Instant.now())

        data.reason?.let { update.set("cancelReason", it) }

        val result = mongoTemplate.updateFirst(query, update, BookingProjectionEntity::class.java)

        if (result.matchedCount == 0L && !repository.existsById(data.bookingId.toString())) {
            mongoTemplate.upsert(Query(Criteria.where("_id").`is`(data.bookingId.toString())), update, BookingProjectionEntity::class.java)
        }
    }
}
