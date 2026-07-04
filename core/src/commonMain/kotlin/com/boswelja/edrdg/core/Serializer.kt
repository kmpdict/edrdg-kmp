package com.boswelja.edrdg.core

import com.squareup.zstd.okio.zstdDecompress
import nl.adaptivity.xmlutil.serialization.XML
import nl.adaptivity.xmlutil.xmlStreaming
import okio.Source
import okio.buffer

/**
 * The default serializer to use for deserializing dictionary files.
 */
public val Serializer: XML = XML.v1 {
    defaultToGenericParser = true
}

public expect suspend fun readCompressedBytes(filename: String): Source

public suspend fun streamDict(filename: String): Sequence<String> {
    val compressedSource = readCompressedBytes(filename)
    return compressedSource
        .zstdDecompress()
        .buffer()
        .readLines()
}

/**
 * Decodes some XML String [target] into [T], with entity expansion enabled.
 */
public inline fun <reified T> XML.decodeFromStringExpandEntities(
    target: String
): T {
    val xr = when {
        config.defaultToGenericParser -> xmlStreaming.newGenericReader(target, true)
        else -> xmlStreaming.newReader(target, true)
    }
    return decodeFromReader(xr)
}
