package com.jakubjirak.docfreshness.inspection

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiFile
import com.jakubjirak.docfreshness.analyzer.FreshnessAnalyzer
import com.jakubjirak.docfreshness.git.GitHistoryAnalyzer
import com.jakubjirak.docfreshness.model.FreshnessLevel
import com.jakubjirak.docfreshness.scanner.DocBlockScanner

/**
 * Inspection that highlights stale documentation blocks.
 * Uses git history to determine if code around a docstring has changed
 * more recently than the docstring itself.
 */
class StaleDocInspection : LocalInspectionTool() {

    override fun getShortName(): String = "StaleDocInspection"

    override fun getDisplayName(): String = "Stale documentation"

    override fun getGroupDisplayName(): String = "Documentation freshness"

    override fun isEnabledByDefault(): Boolean = true

    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor>? {
        val virtualFile = file.virtualFile ?: return null
        val content = file.text
        val docBlocks = scanDocBlocks(content, virtualFile.path) ?: return null
        if (docBlocks.isEmpty()) return null

        val gitAnalyzer = resolveGitAnalyzer(file.project)
        val ctx = InspectionContext(virtualFile, gitAnalyzer, content, file, manager, isOnTheFly)
        val problems = docBlocks.mapNotNull { analyzeDocBlock(it, ctx) }
        return problems.toTypedArray().ifEmpty { null }
    }

    private fun scanDocBlocks(content: String, filePath: String) = try {
        DocBlockScanner.scanContent(content, filePath)
    } catch (e: Exception) {
        LOG.warn("Failed to scan doc blocks in $filePath", e)
        null
    }

    private fun analyzeDocBlock(
        docBlock: com.jakubjirak.docfreshness.model.DocBlock,
        ctx: InspectionContext,
    ): ProblemDescriptor? {
        val result = evaluate(docBlock, ctx.virtualFile, ctx.gitAnalyzer)
        if (result.level != FreshnessLevel.STALE && result.level != FreshnessLevel.ROTTEN) return null

        val offset = findOffsetForLine(ctx.content, docBlock.lineStart)
        val element = ctx.file.findElementAt(offset) ?: return null

        val highlightType = when (result.level) {
            FreshnessLevel.ROTTEN -> ProblemHighlightType.GENERIC_ERROR
            else -> ProblemHighlightType.WARNING
        }

        val description = "Stale documentation (score: ${result.freshnessScore}/100, " +
            "${result.codeChangesSinceDocChange} code change(s) since last doc update)"

        return ctx.manager.createProblemDescriptor(element, description, ctx.isOnTheFly, emptyArray(), highlightType)
    }

    private fun findOffsetForLine(content: String, line: Int): Int {
        var currentLine = 1
        for ((index, char) in content.withIndex()) {
            if (currentLine == line) return index
            if (char == '\n') currentLine++
        }
        return content.length
    }

    companion object {
        private val LOG = Logger.getInstance(StaleDocInspection::class.java)
        private const val CODE_CONTEXT_LINES = 20

        /**
         * Scores [docBlock] from git history: when the doc lines last changed, when the following
         * [CODE_CONTEXT_LINES] lines of code last changed, and how many commits touched that code since.
         * Without git ([gitAnalyzer] `null`) every block scores as fresh. Runs git, so call it off the EDT.
         */
        fun evaluate(
            docBlock: com.jakubjirak.docfreshness.model.DocBlock,
            virtualFile: com.intellij.openapi.vfs.VirtualFile,
            gitAnalyzer: GitHistoryAnalyzer?,
        ): com.jakubjirak.docfreshness.model.FreshnessResult {
            val codeLineStart = docBlock.lineEnd + 1
            val codeLineEnd = codeLineStart + CODE_CONTEXT_LINES
            val lastDocChange = gitAnalyzer?.getLastModification(virtualFile, docBlock.lineStart, docBlock.lineEnd)
            val lastCodeChange = gitAnalyzer?.getLastModification(virtualFile, codeLineStart, codeLineEnd)
            val codeChanges = lastDocChange
                ?.let { gitAnalyzer?.countChangesSince(virtualFile, codeLineStart, codeLineEnd, it) }
                ?: 0
            return FreshnessAnalyzer.analyze(docBlock, codeChanges, lastCodeChange, lastDocChange)
        }

        fun resolveGitAnalyzer(project: com.intellij.openapi.project.Project): GitHistoryAnalyzer? = try {
            GitHistoryAnalyzer.getInstance(project)
        } catch (_: Exception) {
            LOG.debug("Git4Idea not available, skipping git-based analysis")
            null
        } catch (_: NoClassDefFoundError) {
            LOG.debug("Git4Idea not installed, skipping git-based analysis")
            null
        }
    }

    private class InspectionContext(
        val virtualFile: com.intellij.openapi.vfs.VirtualFile,
        val gitAnalyzer: GitHistoryAnalyzer?,
        val content: String,
        val file: PsiFile,
        val manager: InspectionManager,
        val isOnTheFly: Boolean,
    )
}
