package com.boswelja.edrdg.integrationtests

import com.boswelja.jmdict.streamJmDict
import com.boswelja.jmnedict.streamJmmeDict
import com.boswelja.kanjidict.streamKanjiDict
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class BackToBackDeserializationTest {

    @Test
    fun testBackToBackDeserialization() = runTest {
        streamJmDict().forEach { _ -> }
        streamJmmeDict().forEach { _ -> }
        streamKanjiDict().forEach { _ -> }
    }
}
