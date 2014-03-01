package com.philiprehberger.pipe

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class PipeTest {

    @Test
    fun `pipe operator chains transformations left to right`() {
        val result = 5 pipe { it * 2 } pipe { it + 1 } pipe { it.toString() }
        assertEquals("11", result)
    }

    @Test
    fun `pipe operator works with single transformation`() {
        val result = "hello" pipe { it.uppercase() }
        assertEquals("HELLO", result)
    }

    @Test
    fun `pipeline with multiple stages transforms input sequentially`() {
        val p = pipeline<String, Int> {
            stage<String, String>("trim") { it.trim() }
            stage<String, Int>("parse") { it.toInt() }
            stage<Int, Int>("double") { it * 2 }
        }

        val result = p.execute("  21  ")
        assertIs<PipelineResult.Success<Int>>(result)
        assertEquals(42, result.value)
    }

    @Test
    fun `stageIf skips stage when condition is false`() {
        val p = pipeline<Int, Int> {
            stage<Int, Int>("add ten") { it + 10 }
            stageIf<Int>(false, "add five") { it + 5 }
            stage<Int, Int>("double") { it * 2 }
        }

        val result = p.execute(1)
        assertIs<PipelineResult.Success<Int>>(result)
        assertEquals(22, result.value) // (1 + 10) * 2
    }

    @Test
    fun `stageIf executes stage when condition is true`() {
        val p = pipeline<Int, Int> {
            stage<Int, Int>("add ten") { it + 10 }
            stageIf<Int>(true, "add five") { it + 5 }
            stage<Int, Int>("double") { it * 2 }
        }

        val result = p.execute(1)
        assertIs<PipelineResult.Success<Int>>(result)
        assertEquals(32, result.value) // (1 + 10 + 5) * 2
    }

    @Test
    fun `pipeline error stops at failing stage and returns Failure`() {
        val p = pipeline<String, Int> {
            stage<String, String>("trim") { it.trim() }
            stage<String, Int>("parse") { it.toInt() }
            stage<Int, Int>("double") { it * 2 }
        }

        val result = p.execute("  not-a-number  ")
        assertIs<PipelineResult.Failure>(result)
        assertEquals("parse", result.stageName)
        assertIs<NumberFormatException>(result.error)
    }

    @Test
    fun `onError callback is invoked on failure`() {
        var capturedStage = ""
        var capturedException: Throwable? = null

        val p = pipeline<String, Int> {
            stage<String, Int>("parse") { it.toInt() }
            onError { stage, error ->
                capturedStage = stage
                capturedException = error
            }
        }

        p.execute("bad")
        assertEquals("parse", capturedStage)
        assertIs<NumberFormatException>(capturedException)
    }

    @Test
    fun `pipeline composition with then executes both pipelines`() {
        val first = pipeline<String, Int> {
            stage<String, String>("trim") { it.trim() }
            stage<String, Int>("parse") { it.toInt() }
        }

        val second = pipeline<Int, String> {
            stage<Int, String>("classify") { if (it >= 18) "adult" else "minor" }
        }

        val combined = first then second
        val result = combined.execute("  25  ")
        assertIs<PipelineResult.Success<String>>(result)
        assertEquals("adult", result.value)
    }

    @Test
    fun `pipeline composition propagates failure from first pipeline`() {
        val first = pipeline<String, Int> {
            stage<String, Int>("parse") { it.toInt() }
        }

        val second = pipeline<Int, String> {
            stage<Int, String>("format") { "Value: $it" }
        }

        val combined = first then second
        val result = combined.execute("bad")
        assertIs<PipelineResult.Failure>(result)
        assertEquals("parse", result.stageName)
    }

    @Test
    fun `pipeline with no stages returns input as output`() {
        val p = pipeline<String, String> {}
        val result = p.execute("hello")
        assertIs<PipelineResult.Success<String>>(result)
        assertEquals("hello", result.value)
    }
}
