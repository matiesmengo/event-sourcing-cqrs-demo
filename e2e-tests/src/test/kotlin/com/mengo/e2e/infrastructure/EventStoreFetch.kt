package com.mengo.e2e.infrastructure

import org.testcontainers.containers.PostgreSQLContainer
import java.sql.DriverManager
import java.util.UUID

data class EventRow(
    val type: String,
    val payload: String,
    val aggregateId: UUID,
    val aggregateVersion: Long,
)

object EventStoreFetch {
    fun fetchEventsFromPostgres(
        container: PostgreSQLContainer<*>,
        schema: String,
        table: String,
        aggregateId: UUID? = null,
    ): List<EventRow> {
        val urlWithSchema =
            if (container.jdbcUrl.contains("?")) {
                "${container.jdbcUrl}&currentSchema=$schema"
            } else {
                "${container.jdbcUrl}?currentSchema=$schema"
            }

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
}
