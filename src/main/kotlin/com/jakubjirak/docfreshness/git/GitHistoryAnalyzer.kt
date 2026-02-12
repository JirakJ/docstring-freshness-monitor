package com.jakubjirak.docfreshness.git

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import git4idea.GitUtil
import git4idea.commands.Git
import git4idea.commands.GitCommand
import git4idea.commands.GitLineHandler
import java.time.Instant
import java.time.format.DateTimeParseException
import java.util.concurrent.ConcurrentHashMap

/**
 * Analyzes git history to determine when specific line ranges were last modified.
 * Uses Git4Idea API when available.
 */
@Service(Service.Level.PROJECT)
class GitHistoryAnalyzer(private val project: Project) {

    private val cache = ConcurrentHashMap<String, CachedBlameResult>()

    /**
     * Get the last modification timestamp for a range of lines in a file.
     * Returns null if git history is unavailable.
     */
    @Suppress("UNUSED_PARAMETER")
    fun getLastModification(file: VirtualFile, lineStart: Int, lineEnd: Int): Instant? {
        val cacheKey = "${file.path}:$lineStart-$lineEnd"
        val cached = cache[cacheKey]
        if (cached != null && !cached.isExpired()) {
            return cached.timestamp
        }

        return try {
            val result = queryGitLog(file, lineStart, lineEnd)
            cache[cacheKey] = CachedBlameResult(result, Instant.now())
            result
        } catch (e: Exception) {
            LOG.warn("Failed to query git history for ${file.path}:$lineStart-$lineEnd", e)
            null
        }
    }

    /**
     * Count the number of commits that modified a given line range since a reference timestamp.
     */
    fun countChangesSince(file: VirtualFile, lineStart: Int, lineEnd: Int, since: Instant): Int {
        return try {
            val repo = GitUtil.getRepositoryManager(project).getRepositoryForFile(file) ?: return 0
            val handler = GitLineHandler(project, repo.root, GitCommand.LOG)
            handler.addParameters(
                "--format=%aI",
                "--after=${since}",
                "-L", "$lineStart,$lineEnd:${getRelativePath(file, repo.root)}",
            )
            handler.endOptions()

            val result = Git.getInstance().runCommand(handler)
            if (result.success()) {
                result.output.count { it.isNotBlank() }
            } else {
                0
            }
        } catch (e: Exception) {
            LOG.debug("Failed to count changes for ${file.path}", e)
            0
        }
    }

    fun invalidateCache() {
        cache.clear()
    }

    private fun queryGitLog(file: VirtualFile, lineStart: Int, lineEnd: Int): Instant? {
        val repo = GitUtil.getRepositoryManager(project).getRepositoryForFile(file) ?: return null
        val handler = GitLineHandler(project, repo.root, GitCommand.LOG)
        handler.addParameters(
            "-1",
            "--format=%aI",
            "-L", "$lineStart,$lineEnd:${getRelativePath(file, repo.root)}",
        )
        handler.endOptions()

        val result = Git.getInstance().runCommand(handler)
        if (!result.success()) {
            LOG.debug("Git log failed for ${file.path}: ${result.errorOutputAsJoinedString}")
            return null
        }

        val dateStr = result.output.firstOrNull { it.isNotBlank() } ?: return null
        return try {
            Instant.parse(dateStr.trim())
        } catch (_: DateTimeParseException) {
            LOG.debug("Failed to parse git date: $dateStr")
            null
        }
    }

    private fun getRelativePath(file: VirtualFile, repoRoot: VirtualFile): String {
        return file.path.removePrefix(repoRoot.path).removePrefix("/")
    }

    private data class CachedBlameResult(
        val timestamp: Instant?,
        val cachedAt: Instant,
    ) {
        fun isExpired(): Boolean {
            return Instant.now().epochSecond - cachedAt.epochSecond > CACHE_TTL_SECONDS
        }
    }

    companion object {
        private val LOG = Logger.getInstance(GitHistoryAnalyzer::class.java)
        private const val CACHE_TTL_SECONDS = 300L // 5 minutes

        fun getInstance(project: Project): GitHistoryAnalyzer =
            project.getService(GitHistoryAnalyzer::class.java)
    }
}
