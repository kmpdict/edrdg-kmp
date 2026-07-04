package com.boswelja.edrdg.core

import com.squareup.zstd.okio.zstdDecompress
import nl.adaptivity.xmlutil.serialization.XML
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