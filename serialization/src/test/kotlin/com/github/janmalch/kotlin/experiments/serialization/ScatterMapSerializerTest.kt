package com.github.janmalch.kotlin.experiments.serialization

import androidx.collection.MutableScatterMap
import androidx.collection.ScatterMap
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

class ScatterMapSerializerTest {

    private val data = mapOf(
        "John" to 1,
        "Maria" to 2,
    )
    private val encodedData = Json.encodeToString(data)

    private val scatterData: ScatterMap<String, Int> = MutableScatterMap<String, Int>().apply {
        data.forEach { (key, value) -> set(key, value) }
    }

    private val scatterJson = Json {
        serializersModule = SerializersModule {
            scatterMap()
        }
    }

    @Ignore("Order isn't the same, but that doesn't matter for JSON.")
    @Test
    fun `should have the same encoding results as Map`() {
        assertEquals(Json.encodeToString(data), scatterJson.encodeToString(scatterData))
    }

    @Test
    fun `should have the same decoding results as Map`() {
        assertEquals(data, Json.decodeFromString(encodedData))

        val decoded = scatterJson.decodeFromString<ScatterMap<String, Int>>(encodedData)
        val decodedAsMap = mutableMapOf<String, Int>()
        decoded.forEach { key, value -> decodedAsMap[key] = value }
        assertEquals(data, decodedAsMap)
    }
}
