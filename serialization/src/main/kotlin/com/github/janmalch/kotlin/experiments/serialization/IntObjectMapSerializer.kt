package com.github.janmalch.kotlin.experiments.serialization

import androidx.collection.IntObjectMap
import androidx.collection.MutableIntObjectMap
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeCollection
import kotlinx.serialization.modules.SerializersModuleBuilder


internal class IntObjectMapClassDesc(valueDesc: SerialDescriptor) :
    MapLikeDescriptor("androidx.collection.IntObjectMap", INT_SERIALIZER.descriptor, valueDesc)

@OptIn(ExperimentalSerializationApi::class)
internal class IntObjectMapSerializer<V>(private val valueSerializer: KSerializer<V>) : KSerializer<IntObjectMap<V>> {
    override val descriptor: SerialDescriptor = IntObjectMapClassDesc(valueSerializer.descriptor)

    override fun serialize(
        encoder: Encoder,
        value: IntObjectMap<V>
    ) {
        val size = value.size
        encoder.encodeCollection(descriptor, size) {
            var index = 0
            value.forEach { k, v ->
                encodeSerializableElement(descriptor, index++, INT_SERIALIZER, k)
                encodeSerializableElement(descriptor, index++, valueSerializer, v)
            }
        }
    }

    override fun deserialize(decoder: Decoder): IntObjectMap<V> {
        val builder: MutableIntObjectMap<V>
        val compositeDecoder = decoder.beginStructure(descriptor)
        if (compositeDecoder.decodeSequentially()) {
            val size = compositeDecoder.decodeCollectionSize(descriptor)
            builder = MutableIntObjectMap(size)
            readAll(
                compositeDecoder,
                builder,
                size,
            )
        } else {
            builder = MutableIntObjectMap()
            while (true) {
                val index = compositeDecoder.decodeElementIndex(descriptor)
                if (index == CompositeDecoder.DECODE_DONE) break
                readElement(compositeDecoder, index, builder)
            }
        }
        compositeDecoder.endStructure(descriptor)
        return builder.also { it.trim() }
    }

    private fun readElement(
        decoder: CompositeDecoder,
        index: Int,
        builder: MutableIntObjectMap<V>,
        checkIndex: Boolean = true
    ) {
        val key = decoder.decodeSerializableElement(descriptor, index, INT_SERIALIZER)
        val vIndex = if (checkIndex) {
            decoder.decodeElementIndex(descriptor).also {
                require(it == index + 1) { "Value must follow key in a map, index for key: $index, returned index for value: $it" }
            }
        } else {
            index + 1
        }
        val value: V = if (builder.containsKey(key) && valueSerializer.descriptor.kind !is PrimitiveKind) {
            decoder.decodeSerializableElement(descriptor, vIndex, valueSerializer, builder[key])
        } else {
            decoder.decodeSerializableElement(descriptor, vIndex, valueSerializer)
        }
        builder[key] = value
    }

    private fun readAll(
        decoder: CompositeDecoder,
        builder: MutableIntObjectMap<V>,
        size: Int
    ) {
        require(size >= 0) { "Size must be known in advance when using READ_ALL" }
        for (index in 0 until size * 2 step 2)
            readElement(decoder, index, builder, checkIndex = false)
    }
}

fun SerializersModuleBuilder.intObjectMap() {
    contextual(IntObjectMap::class) { args -> IntObjectMapSerializer(args[0]) }
}
