package com.boswelja.jmdict

import com.boswelja.edrdg.core.Serializer
import com.boswelja.edrdg.core.chunkedUntil
import com.boswelja.edrdg.core.decodeFromStringExpandEntities
import com.boswelja.edrdg.core.readCompressedBytes
import com.boswelja.edrdg.core.streamDict
import kotlinx.serialization.SerializationException
import okio.buffer

suspend fun streamJmDict(): Sequence<Entry> {
    val dtd = readCompressedBytes("jmdict_dtd.xml").buffer().readUtf8()
    return streamDict("jmdict.xml")
        .asEntrySequence(dtd)
}

internal fun Sequence<String>.asEntrySequence(dtd: String): Sequence<Entry> {
    return this
        .dropWhile { !it.contains("<entry>") }
        .chunkedUntil { it.contains("<entry>") }
        .chunked(100)
        .flatMap { entryLines ->
            if (entryLines.isNotEmpty()) {
                val target = "$dtd\n<JMdict>${entryLines.flatten().joinToString(separator = "")}</JMdict>"
                try {
                    Serializer.decodeFromStringExpandEntities<JMdict>(target).entries
                } catch (e: SerializationException) {
                    throw SerializationException("Failed deserializing:\n$target", e)
                }
            } else emptyList()
        }
}
