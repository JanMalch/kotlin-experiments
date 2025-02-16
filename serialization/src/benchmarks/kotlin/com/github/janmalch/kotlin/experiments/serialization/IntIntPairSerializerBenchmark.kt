package com.github.janmalch.kotlin.experiments.serialization

import androidx.collection.IntIntPair
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Warmup
import java.util.concurrent.TimeUnit

/*

Benchmark                                      Mode  Cnt        Score         Error  Units


 */

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 1)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
class IntIntPairSerializerBenchmark {

    private val data = 10 to 20
    private val encodedData = Json.encodeToString(data)
    private val encodedIip = "[10,20]"

    private val iipJson = Json {
        serializersModule = SerializersModule {
            intIntPair(asObject = false)
        }
    }

    // Container are necessary because JMH struggles with inline class IntIntPair as return value

    data class PairContainer(
        val entry: Pair<Int, Int>
    )

    data class IntIntPairContainer(
        val entry: IntIntPair
    )

    @Benchmark
    fun mapEntry(): PairContainer = PairContainer(iipJson.decodeFromString(encodedData))

    @Benchmark
    fun intIntPair(): IntIntPairContainer = IntIntPairContainer(iipJson.decodeFromString(encodedIip))

}
