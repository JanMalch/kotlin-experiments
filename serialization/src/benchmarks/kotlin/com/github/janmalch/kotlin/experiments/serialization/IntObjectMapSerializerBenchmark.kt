package com.github.janmalch.kotlin.experiments.serialization

import androidx.collection.IntObjectMap
import kotlinx.serialization.Serializable
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

Benchmark                                      Mode  Cnt       Score        Error  Units
IntObjectMapSerializerBenchmark.intObjectMap  thrpt    3    38109,461 ±   7732,745  ops/s
IntObjectMapSerializerBenchmark.map           thrpt    3    35733,152 ±   2856,215  ops/s

 */

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 1)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
class IntObjectMapSerializerBenchmark {

    @Serializable
    data class User(
        val id: Int,
        val name: String,
    )

    private val data = List(100) {
        User(it, "Name")
    }.associateBy { it.id }

    private val encodedData = Json.encodeToString(data)

    private val iomJson = Json {
        serializersModule = SerializersModule {
            intObjectMap()
        }
    }

    @Benchmark
    fun map(): Map<Int, User> = iomJson.decodeFromString(encodedData)

    @Benchmark
    fun intObjectMap(): IntObjectMap<User> = iomJson.decodeFromString(encodedData)

}
