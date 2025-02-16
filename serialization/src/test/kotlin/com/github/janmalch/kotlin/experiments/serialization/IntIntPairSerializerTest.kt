package com.github.janmalch.kotlin.experiments.serialization

import androidx.collection.IntIntPair
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlin.test.Test
import kotlin.test.assertEquals

class IntIntPairSerializerTest {

    private val data = 10 to 20
    private val encodedPairData = Json.encodeToString(data)
    private val listData = listOf(10, 20)
    private val encodedListData = Json.encodeToString(listData)

    private val iipData = IntIntPair(10, 20)

    private val iipObjectJson = Json {
        serializersModule = SerializersModule {
            intIntPair(asObject = true)
        }
    }

    private val iipListJson = Json {
        serializersModule = SerializersModule {
            intIntPair(asObject = false)
        }
    }

    @Test
    fun `should have the same encoding results as Pair`() {
        assertEquals(Json.encodeToString(data), iipObjectJson.encodeToString(iipData))
    }

    @Test
    fun `should have the same decoding results as Pair`() {
        assertEquals(data, Json.decodeFromString(encodedPairData))

        val decoded = iipObjectJson.decodeFromString<IntIntPair>(encodedPairData)
        assertEquals(data.first, decoded.first)
        assertEquals(data.second, decoded.second)
    }

    @Test
    fun `should have the same encoding results as List`() {
        assertEquals(Json.encodeToString(listData), iipListJson.encodeToString(iipData))
    }

    @Test
    fun `should have the same decoding results as List`() {
        assertEquals(listData, Json.decodeFromString(encodedListData))

        val decoded = iipListJson.decodeFromString<IntIntPair>(encodedListData)
        assertEquals(listData.first(), decoded.first)
        assertEquals(listData[1], decoded.second)
    }
}
