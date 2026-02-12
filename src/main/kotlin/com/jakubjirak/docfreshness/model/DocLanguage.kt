package com.jakubjirak.docfreshness.model

/**
 * Supported documentation languages for doc block extraction.
 */
enum class DocLanguage(val displayName: String, val extensions: List<String>) {
    JAVA("Java/Kotlin", listOf("java", "kt", "kts")),
    PYTHON("Python", listOf("py")),
    JAVASCRIPT("JavaScript/TypeScript", listOf("js", "ts", "jsx", "tsx")),
    GENERIC("Generic", emptyList()),
    ;

    companion object {
        fun fromExtension(extension: String): DocLanguage {
            val ext = extension.lowercase()
            return entries.firstOrNull { lang ->
                lang.extensions.contains(ext)
            } ?: GENERIC
        }
    }
}
