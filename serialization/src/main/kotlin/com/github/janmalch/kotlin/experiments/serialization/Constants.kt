@file:OptIn(InternalSerializationApi::class)

package com.github.janmalch.kotlin.experiments.serialization

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer


internal val INT_SERIALIZER: KSerializer<Int> = Int::class.serializer()