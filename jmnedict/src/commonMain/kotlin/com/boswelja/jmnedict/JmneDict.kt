package com.boswelja.jmnedict

import com.boswelja.edrdg.core.Serializer
import com.boswelja.edrdg.core.chunkedUntil
import com.boswelja.edrdg.core.streamDict
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlin.collections.emptyList

suspend fun streamJmmeDict(): Sequence<Entry> {
    return streamDict("jmnedict.xml")
        .asEntrySequence()
}

internal fun Sequence<String>.asEntrySequence(): Sequence<Entry> {
    return this
        .dropWhile { !it.contains("<entry>") }
        .chunkedUntil { it.contains("<entry>") }
        .chunked(100)
        .flatMap { entryLines ->
            if (entryLines.isNotEmpty()) {
                val target = "<JMnedict>${entryLines.flatten().joinToString(separator = "")}</JMnedict>"
                try {
                    Serializer.decodeFromString<JMnedict>(target).entries
                } catch (e: Exception) {
                    throw SerializationException("Failed deserializing:\n$target", e)
                }
            } else emptyList()
        }
}
