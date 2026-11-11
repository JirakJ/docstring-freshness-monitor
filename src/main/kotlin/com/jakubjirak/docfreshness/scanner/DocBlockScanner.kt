package com.jakubjirak.docfreshness.scanner

import com.intellij.openapi.diagnostic.Logger
import com.jakubjirak.docfreshness.model.DocBlock
import com.jakubjirak.docfreshness.model.DocLanguage

/**
 * Scans file content for documentation blocks using regex patterns.
 * Supports Javadoc, KDoc, Python docstrings, JSDoc, and generic comment blocks.
 */
object DocBlockScanner {

    private val LOG = Logger.getInstance(DocBlockScanner::class.java)

    private val JAVADOC_PATTERN = Regex(
        """/\*\*[\s\S]*?\*/""",
    )

    private val PYTHON_TRIPLE_DOUBLE_PATTERN = Regex(
        "\"{3}[\\s\\S]*?\"{3}",
    )

    private val PYTHON_TRIPLE_SINGLE_PATTERN = Regex(
        "'{3}[\\s\\S]*?'{3}",
    )

    private val GENERIC_COMMENT_BLOCK_PATTERN = Regex(
        """(?:^[ \t]*#[^\n]*\n?){2,}""",
        RegexOption.MULTILINE,
    )

    /**
     * Extract all documentation blocks from the given file content.
     */
    fun scanContent(content: String, filePath: String): List<DocBlock> {
        val language = DocLanguage.fromExtension(filePath.substringAfterLast('.', ""))
        return when (language) {
            DocLanguage.JAVA -> extractJavadocBlocks(content, filePath, language)
            DocLanguage.PYTHON -> extractPythonDocstrings(content, filePath, language)
            DocLanguage.JAVASCRIPT -> extractJavadocBlocks(content, filePath, language)
            DocLanguage.GENERIC -> extractGenericCommentBlocks(content, filePath, language)
        }
    }

    internal fun extractJavadocBlocks(content: String, filePath: String, language: DocLanguage): List<DocBlock> {
        return JAVADOC_PATTERN.findAll(content).map { match ->
            val lineStart = content.lineNumberAt(match.range.first)
            val lineEnd = content.lineNumberAt(match.range.last)
            DocBlock(
                filePath = filePath,
                lineStart = lineStart,
                lineEnd = lineEnd,
                text = match.value,
                language = language,
            )
        }.toList().also {
            LOG.debug("Found ${it.size} Javadoc/KDoc/JSDoc blocks in $filePath")
        }
    }

    internal fun extractPythonDocstrings(content: String, filePath: String, language: DocLanguage): List<DocBlock> {
        val results = mutableListOf<DocBlock>()
        for (pattern in listOf(PYTHON_TRIPLE_DOUBLE_PATTERN, PYTHON_TRIPLE_SINGLE_PATTERN)) {
            pattern.findAll(content).forEach { match ->
                val lineStart = content.lineNumberAt(match.range.first)
                val lineEnd = content.lineNumberAt(match.range.last)
                results.add(
                    DocBlock(
                        filePath = filePath,
                        lineStart = lineStart,
                        lineEnd = lineEnd,
                        text = match.value,
                        language = language,
                    ),
                )
            }
        }
        LOG.debug("Found ${results.size} Python docstrings in $filePath")
        // Two patterns are scanned one after the other; report blocks in file order.
        return results.sortedBy { it.lineStart }
    }

    internal fun extractGenericCommentBlocks(
        content: String,
        filePath: String,
        language: DocLanguage,
    ): List<DocBlock> {
        return GENERIC_COMMENT_BLOCK_PATTERN.findAll(content).map { match ->
            val lineStart = content.lineNumberAt(match.range.first)
            val lineEnd = content.lineNumberAt(match.range.last)
            DocBlock(
                filePath = filePath,
                lineStart = lineStart,
                lineEnd = lineEnd,
                text = match.value.trimEnd(),
                language = language,
            )
        }.toList().also {
            LOG.debug("Found ${it.size} generic comment blocks in $filePath")
        }
    }

    private fun String.lineNumberAt(offset: Int): Int {
        val safeOffset = offset.coerceIn(0, length)
        return substring(0, safeOffset).count { it == '\n' } + 1
    }
}
