package com.github.janmalch.kotlin.experiments.serialization

import androidx.collection.IntIntPair
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.modules.SerializersModuleBuilder

@OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
internal class IntIntPairSerializer(asObject: Boolean) : KSerializer<IntIntPair> {
    override val descriptor: SerialDescriptor = buildSerialDescriptor(
        "androidx.collection.IntIntPair",
        if (asObject) StructureKind.OBJECT else StructureKind.LIST
    ) {
        element("first", INT_SERIALIZER.descriptor)
        element("second", INT_SERIALIZER.descriptor)
    }

    override fun serialize(encoder: Encoder, value: IntIntPair) {
        val structuredEncoder = encoder.beginStructure(descriptor)
        structuredEncoder.encodeIntElement(descriptor, 0, value.first)
        structuredEncoder.encodeIntElement(descriptor, 1, value.second)
        structuredEncoder.endStructure(descriptor)
    }

    override fun deserialize(decoder: Decoder): IntIntPair = decoder.decodeStructure(descriptor) {
        if (decodeSequentially()) {
            val first = decodeIntElement(descriptor, 0)
            val second = decodeIntElement(descriptor, 1)
            return@decodeStructure IntIntPair(first, second)
        }

        var first: Int? = null
        var second: Int? = null
        mainLoop@ while (true) {
            when (val idx = decodeElementIndex(descriptor)) {
                CompositeDecoder.DECODE_DONE -> {
                    break@mainLoop
                }

                0 -> {
                    first = decodeIntElement(descriptor, 0)
                }

                1 -> {
                    second = decodeIntElement(descriptor, 1)
                }

                else -> throw SerializationException("Invalid index: $idx")
            }
        }
        if (first == null) throw SerializationException("Element 'first' is missing")
        if (second == null) throw SerializationException("Element 'second' is missing")
        return@decodeStructure IntIntPair(first, second)
    }
}

/**
 * Registers a [contextual][SerializersModuleBuilder.contextual] serializer for [IntIntPair].
 *
 * The [asObject] parameter determines the underlying kind of structure.
 * - When `true`, any [IntIntPair] will be treated as an object: `{ "first": 0, "second": 1 }`
 * - When `false`, any [IntIntPair] will be treated as a list: `[0, 1]`
 *
 * @param asObject determines the underlying kind of structure
 */
fun SerializersModuleBuilder.intIntPair(asObject: Boolean) {
    contextual(IntIntPair::class) { _ -> IntIntPairSerializer(asObject) }
}
