package com.boswelja.jmnedict

import com.boswelja.edrdg.core.Serializer
import com.boswelja.edrdg.core.chunkedUntil
import com.boswelja.edrdg.core.decodeFromStringExpandEntities
import com.boswelja.edrdg.core.readCompressedBytes
import com.boswelja.edrdg.core.streamDict
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import okio.buffer
import kotlin.collections.emptyList

suspend fun streamJmmeDict(): Sequence<Entry> {
    val dtd = readCompressedBytes("jmnedict_dtd.xml").buffer().readUtf8()
    return streamDict("jmnedict.xml")
        .asEntrySequence(dtd)
}

internal fun Sequence<String>.asEntrySequence(dtd: String): Sequence<Entry> {
    return this
        .dropWhile { !it.contains("<entry>") }
        .chunkedUntil { it.contains("<entry>") }
        .chunked(100)
        .flatMap { entryLines ->
            if (entryLines.isNotEmpty()) {
                val target = "$dtd\n<JMnedict>${entryLines.flatten().joinToString(separator = "")}</JMnedict>"
                try {
                    Serializer.decodeFromStringExpandEntities<JMnedict>(target).entries
                } catch (e: SerializationException) {
                    throw SerializationException("Failed deserializing:\n$target", e)
                }
            } else emptyList()
        }
}
