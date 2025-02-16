package com.github.janmalch.kotlin.experiments

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.test.assertEquals

class RepeatALotTest {
    @ParameterizedTest
    @MethodSource("provideInputs")
    fun `should have the same results as repeat`(data: String, n: Int) {
        assertEquals(data.repeat(n), data.repeatALot(n))
    }

    companion object {
        @JvmStatic
        fun provideInputs() = List(10) {
            Arguments.of(getRandomString(10..20), Random.nextInt(5..1000))
        }.stream()
    }
}

fun getRandomString(length: IntRange): String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return length
        .map { allowedChars.random() }
        .joinToString("")
}
