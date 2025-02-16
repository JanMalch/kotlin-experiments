package com.github.janmalch.kotlin.experiments.serialization

import androidx.collection.MutableScatterMap
import androidx.collection.ScatterMap
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeCollection
import kotlinx.serialization.modules.SerializersModuleBuilder

internal class ScatterMapClassDesc(keyDesc: SerialDescriptor, valueDesc: SerialDescriptor) :
    MapLikeDescriptor("androidx.collection.ScatterMap", keyDesc, valueDesc)

@OptIn(ExperimentalSerializationApi::class)
internal class ScatterMapSerializer<K, V>(
    private val keySerializer: KSerializer<K>,
    private val valueSerializer: KSerializer<V>,
) : KSerializer<ScatterMap<K, V>> {
    override val descriptor: SerialDescriptor =
        ScatterMapClassDesc(keySerializer.descriptor, valueSerializer.descriptor)

    override fun serialize(
        encoder: Encoder,
        value: ScatterMap<K, V>
    ) {
        val size = value.size
        encoder.encodeCollection(descriptor, size) {
            var index = 0
            value.forEach { k, v ->
                encodeSerializableElement(descriptor, index++, keySerializer, k)
                encodeSerializableElement(descriptor, index++, valueSerializer, v)
            }
        }
    }

    override fun deserialize(decoder: Decoder): ScatterMap<K, V> {
        val builder: MutableScatterMap<K, V>
        val compositeDecoder = decoder.beginStructure(descriptor)
        if (compositeDecoder.decodeSequentially()) {
            val size = compositeDecoder.decodeCollectionSize(descriptor)
            builder = MutableScatterMap(size)
            readAll(
                compositeDecoder,
                builder,
                size,
            )
        } else {
            builder = MutableScatterMap()
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
        builder: MutableScatterMap<K, V>,
        checkIndex: Boolean = true
    ) {
        val key = decoder.decodeSerializableElement(descriptor, index, keySerializer)
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
        builder: MutableScatterMap<K, V>,
        size: Int
    ) {
        require(size >= 0) { "Size must be known in advance when using READ_ALL" }
        for (index in 0 until size * 2 step 2)
            readElement(decoder, index, builder, checkIndex = false)
    }

}

fun SerializersModuleBuilder.scatterMap() {
    contextual(ScatterMap::class) { args -> ScatterMapSerializer(args[0], args[1]) }
}
