package com.mengo.architecture.test.infrastructure

import org.testcontainers.containers.PostgreSQLContainer
import java.sql.DriverManager
import java.util.UUID

data class EventRow(
    val type: String,
    val payload: String,
    val aggregateId: UUID,
    val aggregateVersion: Long,
)

object MengoEventStoreAudit {
    fun fetchEvents(
        container: PostgreSQLContainer<*>,
        schema: String,
        table: String,
        aggregateId: UUID? = null,
    ): List<EventRow> {
        val separator = if (container.jdbcUrl.contains("?")) "&" else "?"
        val urlWithSchema = "${container.jdbcUrl}${separator}currentSchema=$schema"

        DriverManager.getConnection(urlWithSchema, container.username, container.password).use { conn ->
            val sql =
                buildString {
                    append("SELECT event_type, event_data, aggregate_id, aggregate_version FROM $table")
                    if (aggregateId != null) append(" WHERE aggregate_id = ?")
                    append(" ORDER BY aggregate_version ASC")
                }

            conn.prepareStatement(sql).use { stmt ->
                if (aggregateId != null) stmt.setObject(1, aggregateId)
                val rs = stmt.executeQuery()

                val events = mutableListOf<EventRow>()
                while (rs.next()) {
                    events.add(
                        EventRow(
                            type = rs.getString("event_type"),
                            payload = rs.getString("event_data"),
                            aggregateId = rs.getObject("aggregate_id", UUID::class.java),
                            aggregateVersion = rs.getLong("aggregate_version"),
                        ),
                    )
                }
                return events
            }
        }
    }

    fun cleanTable(
        container: PostgreSQLContainer<*>,
        schema: String,
        table: String,
    ) {
        val separator = if (container.jdbcUrl.contains("?")) "&" else "?"
        val urlWithSchema = "${container.jdbcUrl}${separator}currentSchema=$schema"

        DriverManager.getConnection(urlWithSchema, container.username, container.password).use { conn ->
            conn.createStatement().use { stmt ->
                stmt.execute("TRUNCATE TABLE $table RESTART IDENTITY")
            }
        }
    }

    fun List<EventRow>.assertHasEventType(expectedType: String) {
        if (this.none { it.type == expectedType }) {
            val foundTypes = this.map { it.type }.joinToString(", ")
            throw AssertionError("Event type [$expectedType] not found in Event Store. Types found: [$foundTypes]")
        }
    }

    fun List<EventRow>.assertVersionsAreStrictlyIncreasing() {
        val versions = this.map { it.aggregateVersion }

        for (i in 0 until versions.size - 1) {
            if (versions[i] >= versions[i + 1]) {
                val failureIndex = i + 1
                throw AssertionError(
                    "Aggregate versions are not strictly increasing. Found duplicate/regression at index $failureIndex. Sequence: $versions",
                )
            }
        }
    }
}
