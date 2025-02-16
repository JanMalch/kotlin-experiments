package com.github.janmalch.kotlin.experiments.serialization

import androidx.collection.ObjectIntMap
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
ObjectIntMapSerializerBenchmark.map           thrpt    3  3439058,602 ± 3992062,598  ops/s
ObjectIntMapSerializerBenchmark.objectIntMap  thrpt    3  3455931,100 ±  159265,337  ops/s

 */

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 1)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
class ObjectIntMapSerializerBenchmark {

    private val data = mapOf(
        "John" to 0,
        "Maria" to 1,
    )

    private val encodedData = Json.encodeToString(data)

    private val oimJson = Json {
        serializersModule = SerializersModule {
            objectIntMap()
        }
    }

    @Benchmark
    fun map(): Map<String, Int> = oimJson.decodeFromString(encodedData)

    @Benchmark
    fun objectIntMap(): ObjectIntMap<String> = oimJson.decodeFromString(encodedData)

}
