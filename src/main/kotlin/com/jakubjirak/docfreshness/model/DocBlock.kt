package com.jakubjirak.docfreshness.model

import java.time.Instant

/**
 * Represents a documentation block found in source code.
 */
data class DocBlock(
    val filePath: String,
    val lineStart: Int,
    val lineEnd: Int,
    val text: String,
    val lastModified: Instant? = null,
    val language: DocLanguage = DocLanguage.GENERIC,
)
