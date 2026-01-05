package com.mengo.architecture

import org.intellij.lang.annotations.Language

class JsonString(
    @Language("JSON") private val json: String,
) {
    fun asString(): String = json
}

fun String.asJson(): JsonString = JsonString(this)
