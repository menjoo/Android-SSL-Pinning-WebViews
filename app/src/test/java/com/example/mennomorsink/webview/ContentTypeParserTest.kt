package com.example.mennomorsink.webview

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ContentTypeParserTest {

    @Test
    fun testGetMimeType() {
        val input = "text/html"
        val result = ContentTypeParser.getMimeType(input)
        assertThat(result).isEqualTo("text/html")
    }

    @Test
    fun testGetMimeTypeMultipleParts() {
        val input = "text/html; charset=utf-8"
        val result = ContentTypeParser.getMimeType(input)
        assertThat(result).isEqualTo("text/html")
    }

    @Test
    fun testGetCharset() {
        val input = "text/html; charset=utf-8"
        val result = ContentTypeParser.getCharset(input)
        assertThat(result).isEqualTo("UTF-8")
    }

    @Test
    fun testGetCharsetNotMentioned() {
        val input = "text/html"
        val result = ContentTypeParser.getCharset(input)
        assertThat(result).isEqualTo("UTF-8")
    }
}
