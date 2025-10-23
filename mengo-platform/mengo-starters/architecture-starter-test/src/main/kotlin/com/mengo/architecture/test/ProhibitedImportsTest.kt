package com.mengo.architecture.test

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

class ProhibitedImportsTest {
    private val prohibitedImports = listOf("org.mockito.Mockito", "java.io.PrintStream")
    private val excludedFiles = listOf("ProhibitedImportsTest.kt")

    @Test
    fun `detect prohibited imports`() {
        val testDir = File("src/test/kotlin")
        assertTrue(testDir.exists(), "Directory of tests doesn't exist")

        val violations =
            testDir
                .walkTopDown()
                .filter { it.isFile && it.extension == "kt" && it.name !in excludedFiles }
                .flatMap { file ->
                    prohibitedImports
                        .filter { file.readText().contains(it) }
                        .map { prohibited -> "${file.relativeTo(testDir)} -> $prohibited" }
                }.toList()

        if (violations.isNotEmpty()) {
            println("Imports not allowed:")
            violations.forEach { println(it) }
        }

        assertTrue(violations.isEmpty(), "Architecture test found a prohibited import")
    }
}
