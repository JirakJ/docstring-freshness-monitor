package com.jakubjirak.docfreshness.git

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.jakubjirak.docfreshness.model.DocBlock

/**
 * Enhanced staleness calculation using commit-level granularity.
 * Counts commits that changed code near a docstring without updating the doc.
 */
class CommitAwareStaleness {

    /**
     * Staleness data computed from commit history analysis.
     */
    data class CommitStaleness(
        val docBlock: DocBlock,
        val commitsSinceDocUpdate: Int,
        val codeChangingCommits: Int,
        val authors: List<String>,
    )

    /**
     * Change classification for a commit relative to a docstring.
     */
    enum class ChangeType {
        CODE_ONLY,
        DOC_ONLY,
        BOTH,
        REFACTOR,
    }

    /**
     * Analyzes staleness based on commit-level granularity.
     */
    @Suppress("UNUSED_PARAMETER")
    fun analyzeStaleness(
        project: Project,
        docBlock: DocBlock,
        commitHistory: List<CommitChange>,
    ): CommitStaleness {
        if (commitHistory.isEmpty()) {
            return CommitStaleness(
                docBlock = docBlock,
                commitsSinceDocUpdate = 0,
                codeChangingCommits = 0,
                authors = emptyList(),
            )
        }

        var commitsSinceDocUpdate = 0
        var codeChangingCommits = 0
        val authors = mutableSetOf<String>()

        for (commit in commitHistory) {
            val changeType = classifyCommitChange(commit)
            when (changeType) {
                ChangeType.CODE_ONLY -> {
                    commitsSinceDocUpdate++
                    codeChangingCommits++
                    authors.add(commit.author)
                }
                ChangeType.DOC_ONLY, ChangeType.BOTH -> {
                    // Doc was updated, reset counter
                    break
                }
                ChangeType.REFACTOR -> {
                    // Refactoring doesn't affect staleness
                }
            }
        }

        LOG.info(
            "Staleness analysis for ${docBlock.filePath}:${docBlock.lineStart}: " +
                "$codeChangingCommits code-only commits",
        )

        return CommitStaleness(
            docBlock = docBlock,
            commitsSinceDocUpdate = commitsSinceDocUpdate,
            codeChangingCommits = codeChangingCommits,
            authors = authors.toList(),
        )
    }

    /**
     * Computes a staleness score from commit analysis (0-100).
     */
    fun computeScore(staleness: CommitStaleness): Int {
        val codeCommitPenalty = staleness.codeChangingCommits * PENALTY_PER_CODE_COMMIT
        return codeCommitPenalty.coerceAtMost(MAX_SCORE)
    }

    /**
     * Classifies a commit change relative to doc/code areas.
     */
    internal fun classifyCommitChange(commit: CommitChange): ChangeType {
        val hasCodeChange = commit.codeChanged
        val hasDocChange = commit.docChanged
        val isRefactor = commit.isRefactor

        return when {
            isRefactor -> ChangeType.REFACTOR
            hasCodeChange && hasDocChange -> ChangeType.BOTH
            hasCodeChange -> ChangeType.CODE_ONLY
            hasDocChange -> ChangeType.DOC_ONLY
            else -> ChangeType.REFACTOR
        }
    }

    companion object {
        private val LOG = Logger.getInstance(CommitAwareStaleness::class.java)
        private const val PENALTY_PER_CODE_COMMIT = 15
        private const val MAX_SCORE = 100
    }
}

/**
 * Represents a commit's change impact on a doc block and its surrounding code.
 */
data class CommitChange(
    val commitHash: String,
    val author: String,
    val date: Long,
    val codeChanged: Boolean,
    val docChanged: Boolean,
    val isRefactor: Boolean = false,
)
