package com.github.janmalch.kotlin.experiments.serialization

import androidx.collection.MutableObjectIntMap
import androidx.collection.ObjectIntMap
import androidx.collection.objectIntMapOf
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

class ObjectIntMapSerializerTest {

    private val data = mapOf(
        "John" to 1,
        "Maria" to 2,
    )
    private val encodedData = Json.encodeToString(data)

    private val oimData: ObjectIntMap<String> = MutableObjectIntMap<String>().apply {
        data.forEach { (key, value) -> set(key, value) }
    }

    private val oimJson = Json {
        serializersModule = SerializersModule {
            objectIntMap()
        }
    }

    @Ignore("Order isn't the same, but that doesn't matter for JSON.")
    @Test
    fun `should have the same encoding results as Map`() {
        assertEquals(Json.encodeToString(data), oimJson.encodeToString(oimData))
    }

    @Test
    fun `should have the same decoding results as Map`() {
        assertEquals(data, Json.decodeFromString(encodedData))

        val decoded = oimJson.decodeFromString<ObjectIntMap<String>>(encodedData)
        val decodedAsMap = mutableMapOf<String, Int>()
        decoded.forEach { key, value -> decodedAsMap[key] = value }
        assertEquals(data, decodedAsMap)
    }

    @Test
    fun `deserialize like Map`() {
        val input = """{"a":1,"b":2}"""
        val regular = oimJson.decodeFromString<Map<String, Int>>(input)
        assertEquals(mapOf("a" to 1, "b" to 2), regular)
        val actual = oimJson.decodeFromString<ObjectIntMap<String>>(input)
        assertEquals(objectIntMapOf("a", 1, "b", 2), actual)
    }
}
