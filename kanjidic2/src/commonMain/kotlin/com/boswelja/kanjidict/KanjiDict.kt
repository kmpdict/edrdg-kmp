package com.boswelja.kanjidict

import com.boswelja.edrdg.core.Serializer
import com.boswelja.edrdg.core.chunkedUntil
import com.boswelja.edrdg.core.streamDict
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import nl.adaptivity.xmlutil.serialization.XmlElement

suspend fun streamKanjiDict(): Sequence<Character> {
    return streamDict("kanjidict.xml")
        .asCharacterSequence()
}

internal fun Sequence<String>.asCharacterSequence(): Sequence<Character> {
    return this
        .dropWhile { !it.contains("<character>") }
        .chunkedUntil { it.contains("<character>") }
        .chunked(100)
        .flatMap { entryLines ->
            if (entryLines.isNotEmpty()) {
                val target = "<kanjidic2>${entryLines.flatten().joinToString(separator = "")}</kanjidic2>"
                try {
                    Serializer.decodeFromString<KanjiDictCharacters>(target).characters
                } catch (e: Exception) {
                    throw SerializationException("Failed deserializing:\n$target", e)
                }
            } else emptyList()
        }
}

@Serializable
@XmlElement(value = true)
@SerialName(value = "kanjidic2")
internal class KanjiDictCharacters(
    @XmlElement(value = true)
    @SerialName(value = "character")
    public val characters: List<Character>,
)