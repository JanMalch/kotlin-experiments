package com.github.janmalch.kotlin.experiments.serialization

import androidx.collection.IntObjectMap
import androidx.collection.MutableIntObjectMap
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlin.test.Test
import kotlin.test.assertEquals

class IntObjectMapSerializerTest {

    @Serializable
    data class User(
        val id: Int,
        val name: String,
    )

    private val data = listOf(
        User(0, "John"),
        User(1, "Maria"),
    ).associateBy { it.id }
    private val encodedData = Json.encodeToString(data)

    private val iomData: IntObjectMap<User> = MutableIntObjectMap<User>().apply {
        data.forEach { (key, value) -> set(key, value) }
    }

    private val iomJson = Json {
        serializersModule = SerializersModule {
            intObjectMap()
        }
    }

    @Test
    fun `should have the same encoding results as Map`() {
        assertEquals(Json.encodeToString(data), iomJson.encodeToString(iomData))
    }

    @Test
    fun `should have the same decoding results as Map`() {
        assertEquals(data, Json.decodeFromString(encodedData))

        val decoded = iomJson.decodeFromString<IntObjectMap<User>>(encodedData)
        val decodedAsMap = mutableMapOf<Int, User>()
        decoded.forEach { key, value -> decodedAsMap[key] = value }
        assertEquals(data, decodedAsMap)
    }
}
