package com.mengo.booking.architecture

import java.io.File
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

// TODO: move to common libs/profiles
class ProhibitedImportsTest {
    private val prohibitedImports =
        listOf(
            "org.mockito.Mockito.",
        )

    private val excludedFiles =
        listOf(
            "ProhibitedImportsTest.kt",
        )

    @Test
    fun `detect prohibited imports`() {
        val testDir = File("src/test/kotlin")
        assertTrue(testDir.exists(), "Directory of tests doesn't exist")

        val allTestFiles =
            testDir
                .walkTopDown()
                .filter { it.isFile && it.extension == "kt" && it.name !in excludedFiles }
                .toList()

        val violations = mutableListOf<String>()

        allTestFiles.forEach { file ->
            val content = file.readText()
            prohibitedImports.forEach { prohibited ->
                if (content.contains(prohibited)) {
                    violations.add("${file.relativeTo(testDir)} -> $prohibited")
                }
            }
        }

        if (violations.isNotEmpty()) {
            println("Imports not allowed for test: ")
            violations.forEach { println(it) }
        }

        assertTrue(violations.isEmpty(), "Architecture test found a prohibited import test.")
    }
}
