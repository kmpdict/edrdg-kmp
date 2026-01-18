package com.boswelja.jmdict

import com.boswelja.edrdg.core.Serializer
import com.boswelja.edrdg.core.chunkedUntil
import com.boswelja.edrdg.core.streamDict
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString

suspend fun streamJmDict(): Sequence<Entry> {
    return streamDict("jmdict.xml")
        .asEntrySequence()
}

internal fun Sequence<String>.asEntrySequence(): Sequence<Entry> {
    return this
        .dropWhile { !it.contains("<entry>") }
        .chunkedUntil { it.contains("<entry>") }
        .chunked(100)
        .flatMap { entryLines ->
            if (entryLines.isNotEmpty()) {
                val target = "<JMdict>${entryLines.flatten().joinToString(separator = "")}</JMdict>"
                try {
                    Serializer.decodeFromString<JMdict>(target).entries
                } catch (e: SerializationException) {
                    throw SerializationException("Failed deserializing:\n$target", e)
                }
            } else emptyList()
        }
}
