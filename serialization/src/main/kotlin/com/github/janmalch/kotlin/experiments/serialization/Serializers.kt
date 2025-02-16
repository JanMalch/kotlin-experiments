package com.github.janmalch.kotlin.experiments.serialization

import kotlinx.serialization.modules.SerializersModuleBuilder


fun SerializersModuleBuilder.androidxCollections(
    pairsAsObjects: Boolean
) {
    intIntPair(asObject = pairsAsObjects)
    intObjectMap()
    objectIntMap()
    scatterMap()
}
