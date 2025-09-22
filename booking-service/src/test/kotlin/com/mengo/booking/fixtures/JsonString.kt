package com.mengo.booking.fixtures

import org.intellij.lang.annotations.Language

class JsonString(
    @Language("JSON") private val json: String,
) {
    fun asString(): String = json
}

fun @receiver:Language("JSON") String.asJson(): JsonString = JsonString(this)
