package com.boswelja.edrdg.core

import com.squareup.zstd.okio.zstdDecompress
import kotlinx.serialization.serializer
import nl.adaptivity.xmlutil.serialization.XML
import nl.adaptivity.xmlutil.xmlStreaming
import okio.Source
import okio.buffer

public val Serializer: XML = XML.v1 {
    defaultToGenericParser = true
}

internal expect suspend fun readCompressedBytes(filename: String): Source

public suspend fun streamDict(filename: String): Sequence<String> {
    val compressedSource = readCompressedBytes(filename)
    return compressedSource
        .zstdDecompress()
        .buffer()
        .readLines()
}

public inline fun <reified T> XML.decodeFromStringExpandEntities(
    target: String
): T {
    val xr = when {
        config.defaultToGenericParser -> xmlStreaming.newGenericReader(target, true)
        else -> xmlStreaming.newReader(target, true)
    }
    return decodeFromReader(xr)
}
