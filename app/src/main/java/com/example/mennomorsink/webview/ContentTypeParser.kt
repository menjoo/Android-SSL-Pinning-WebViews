package com.example.mennomorsink.webview

object ContentTypeParser {

    private const val PARTS_DELIMITER = ";"
    private const val VALUE_DELIMITER = "="
    private const val UTF8 = "UTF-8"
    private const val CHARSET = "charset"

    fun getMimeType(contentType: String):String {
        if (contentType.contains(PARTS_DELIMITER)) {
            val contentTypeParts = contentType.split(PARTS_DELIMITER.toRegex())
            return contentTypeParts[0].trim()
        }
        return contentType
    }

    fun getCharset(contentType: String):String {
        if (contentType.contains(PARTS_DELIMITER)) {
            val contentTypeParts = contentType.split(PARTS_DELIMITER.toRegex())
            val charsetParts = contentTypeParts[1].split(VALUE_DELIMITER.toRegex())
            if(charsetParts[0].trim().startsWith(CHARSET)) {
                return charsetParts[1].trim().toUpperCase()
            }
        }
        return UTF8
    }
}
