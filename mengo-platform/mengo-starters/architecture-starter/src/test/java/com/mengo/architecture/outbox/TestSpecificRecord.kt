package com.mengo.architecture.outbox

import com.fasterxml.jackson.annotation.JsonProperty
import org.apache.avro.Schema
import org.apache.avro.SchemaBuilder
import org.apache.avro.specific.SpecificRecord

data class TestSpecificRecord(
    @JsonProperty("id") val id: String,
) : SpecificRecord {
    override fun toString(): String = """{"id": "$id"}"""

    override fun getSchema(): Schema = SCHEMA

    companion object {
        private val SCHEMA: Schema =
            SchemaBuilder
                .record("TestSpecificRecord")
                .namespace("com.mengo.architecture.outbox")
                .fields()
                .requiredString("id")
                .endRecord()
    }

    override fun get(i: Int): Any? =
        when (i) {
            0 -> id
            else -> error("Not needed for Outbox test")
        }

    override fun put(
        i: Int,
        v: Any?,
    ) {
        error("Not needed for Outbox test")
    }
}
