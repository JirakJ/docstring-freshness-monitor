package com.jakubjirak.docfreshness.git

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.jakubjirak.docfreshness.model.DocBlock
import com.jakubjirak.docfreshness.model.FreshnessLevel

/**
 * Identifies original doc author via git blame and creates notifications
 * when their docstring becomes stale.
 */
class DocAuthorNotifier {

    /**
     * Information about a documentation author.
     */
    data class DocAuthor(
        val name: String,
        val email: String,
        val lastDocCommit: String,
        val stalenessLevel: FreshnessLevel,
    )

    /**
     * Notification about a stale docstring for an author.
     */
    data class StaleDocNotification(
        val docBlock: DocBlock,
        val author: DocAuthor,
        val codeChangingCommits: Int,
        val message: String,
    )

    /**
     * Resolves the original author of a doc block.
     */
    @Suppress("UNUSED_PARAMETER")
    fun resolveDocAuthor(
        project: Project,
        docBlock: DocBlock,
        blameData: BlameData?,
    ): DocAuthor? {
        if (blameData == null) {
            LOG.debug("No blame data available for ${docBlock.filePath}:${docBlock.lineStart}")
            return null
        }

        return DocAuthor(
            name = blameData.authorName,
            email = blameData.authorEmail,
            lastDocCommit = blameData.commitHash,
            stalenessLevel = FreshnessLevel.FRESH,
        )
    }

    /**
     * Creates a notification if the current user is the doc author and the doc is stale.
     */
    @Suppress("UNUSED_PARAMETER")
    fun createNotificationIfNeeded(
        project: Project,
        docBlock: DocBlock,
        author: DocAuthor,
        currentUserEmail: String,
        codeChangingCommits: Int,
    ): StaleDocNotification? {
        if (author.email != currentUserEmail) {
            return null
        }

        if (codeChangingCommits < MIN_COMMITS_FOR_NOTIFICATION) {
            return null
        }

        val message = buildNotificationMessage(docBlock, codeChangingCommits)
        LOG.info("Creating stale doc notification for ${docBlock.filePath}:${docBlock.lineStart}")

        return StaleDocNotification(
            docBlock = docBlock,
            author = author,
            codeChangingCommits = codeChangingCommits,
            message = message,
        )
    }

    /**
     * Builds a user-friendly notification message.
     */
    internal fun buildNotificationMessage(docBlock: DocBlock, codeChangingCommits: Int): String {
        return "Documentation at ${docBlock.filePath}:${docBlock.lineStart} may be outdated. " +
            "$codeChangingCommits commit(s) changed nearby code without updating the documentation."
    }

    companion object {
        private val LOG = Logger.getInstance(DocAuthorNotifier::class.java)
        private const val MIN_COMMITS_FOR_NOTIFICATION = 2
    }
}

/**
 * Git blame data for a line range.
 */
data class BlameData(
    val authorName: String,
    val authorEmail: String,
    val commitHash: String,
    val date: Long,
)
