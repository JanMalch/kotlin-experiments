@file:OptIn(ExperimentalSerializationApi::class)

package com.github.janmalch.kotlin.experiments.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind


internal sealed class MapLikeDescriptor(
    override val serialName: String,
    val keyDescriptor: SerialDescriptor,
    val valueDescriptor: SerialDescriptor
) : SerialDescriptor {
    override val kind: SerialKind get() = StructureKind.MAP
    override val elementsCount: Int = 2
    override fun getElementName(index: Int): String = index.toString()
    override fun getElementIndex(name: String): Int =
        name.toIntOrNull() ?: throw IllegalArgumentException("$name is not a valid map index")

    override fun isElementOptional(index: Int): Boolean {
        require(index >= 0) { "Illegal index $index, $serialName expects only non-negative indices" }
        return false
    }

    override fun getElementAnnotations(index: Int): List<Annotation> {
        require(index >= 0) { "Illegal index $index, $serialName expects only non-negative indices" }
        return emptyList()
    }

    override fun getElementDescriptor(index: Int): SerialDescriptor {
        require(index >= 0) { "Illegal index $index, $serialName expects only non-negative indices" }
        return when (index % 2) {
            0 -> keyDescriptor
            1 -> valueDescriptor
            else -> error("Unreached")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MapLikeDescriptor) return false
        if (serialName != other.serialName) return false
        if (keyDescriptor != other.keyDescriptor) return false
        if (valueDescriptor != other.valueDescriptor) return false
        return true
    }

    override fun hashCode(): Int {
        var result = serialName.hashCode()
        result = 31 * result + keyDescriptor.hashCode()
        result = 31 * result + valueDescriptor.hashCode()
        return result
    }

    override fun toString(): String = "$serialName($keyDescriptor, $valueDescriptor)"
}

