package com.github.janmalch.kotlin.experiments.serialization

import androidx.collection.MutableObjectIntMap
import androidx.collection.ObjectIntMap
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeCollection
import kotlinx.serialization.modules.SerializersModuleBuilder

internal class ObjectIntMapClassDesc(keyDesc: SerialDescriptor) :
    MapLikeDescriptor("androidx.collection.ObjectIntMap", keyDesc, INT_SERIALIZER.descriptor)

@OptIn(ExperimentalSerializationApi::class)
internal class ObjectIntMapSerializer<K>(private val keySerializer: KSerializer<K>) : KSerializer<ObjectIntMap<K>> {
    override val descriptor: SerialDescriptor = ObjectIntMapClassDesc(keySerializer.descriptor)

    override fun serialize(
        encoder: Encoder,
        value: ObjectIntMap<K>
    ) {
        val size = value.size
        encoder.encodeCollection(descriptor, size) {
            var index = 0
            value.forEach { k, v ->
                encodeSerializableElement(descriptor, index++, keySerializer, k)
                encodeSerializableElement(descriptor, index++, INT_SERIALIZER, v)
            }
        }
    }

    override fun deserialize(decoder: Decoder): ObjectIntMap<K> {
        val builder: MutableObjectIntMap<K>
        val compositeDecoder = decoder.beginStructure(descriptor)
        if (compositeDecoder.decodeSequentially()) {
            val size = compositeDecoder.decodeCollectionSize(descriptor)
            builder = MutableObjectIntMap(size)
            readAll(
                compositeDecoder,
                builder,
                size,
            )
        } else {
            builder = MutableObjectIntMap()
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
        builder: MutableObjectIntMap<K>,
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
        builder[key] = decoder.decodeIntElement(descriptor, vIndex)
    }

    private fun readAll(
        decoder: CompositeDecoder,
        builder: MutableObjectIntMap<K>,
        size: Int
    ) {
        require(size >= 0) { "Size must be known in advance when using READ_ALL" }
        for (index in 0 until size * 2 step 2)
            readElement(decoder, index, builder, checkIndex = false)
    }

}

fun SerializersModuleBuilder.objectIntMap() {
    contextual(ObjectIntMap::class) { args -> ObjectIntMapSerializer(args[0]) }
}
