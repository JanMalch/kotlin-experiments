package com.github.janmalch.kotlin.experiments.serialization

import androidx.collection.ScatterMap
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
ScatterMapSerializerBenchmark.map             thrpt    3  3562604,433 ±  142311,164  ops/s
ScatterMapSerializerBenchmark.scatterMap      thrpt    3  2434908,932 ±  337909,630  ops/s

 */

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 1)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
class ScatterMapSerializerBenchmark {

    private val data = mapOf(
        "John" to 0,
        "Maria" to 1,
    )

    private val encodedData = Json.encodeToString(data)

    private val scatterJson = Json {
        serializersModule = SerializersModule {
            scatterMap()
        }
    }

    @Benchmark
    fun map(): Map<String, Int> = scatterJson.decodeFromString(encodedData)

    @Benchmark
    fun scatterMap(): ScatterMap<String, Int> = scatterJson.decodeFromString(encodedData)

}
