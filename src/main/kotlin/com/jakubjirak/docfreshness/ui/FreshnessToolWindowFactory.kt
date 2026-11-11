package com.jakubjirak.docfreshness.ui

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.table.JBTable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.jakubjirak.docfreshness.inspection.StaleDocInspection
import com.jakubjirak.docfreshness.model.FreshnessResult
import com.jakubjirak.docfreshness.scanner.DocBlockScanner
import java.awt.BorderLayout
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.table.DefaultTableModel

/**
 * Tool window factory for the Doc Freshness dashboard.
 * Displays freshness scores per file, sortable by score.
 */
class FreshnessToolWindowFactory : ToolWindowFactory, DumbAware {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val panel = FreshnessDashboardPanel(project)
        val content = ContentFactory.getInstance().createContent(panel, "Dashboard", false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project): Boolean = true

    companion object
}

/**
 * Dashboard panel showing freshness results across the project.
 */
internal class FreshnessDashboardPanel(private val project: Project) : JPanel(BorderLayout()) {

    private val columnNames = arrayOf<Any>("File", "Line", "Score", "Level", "Code Changes")
    private val tableModel = object : DefaultTableModel(columnNames, 0) {
        override fun isCellEditable(row: Int, column: Int): Boolean = false
    }
    private val resultsTable = JBTable(tableModel)
    private val statusLabel = JBLabel("Open a file and click 'Scan' to analyze documentation freshness.")

    init {
        val toolbar = JPanel()
        val scanButton = JButton("Scan Current File")
        scanButton.addActionListener { scanCurrentFile() }
        toolbar.add(scanButton)

        add(toolbar, BorderLayout.NORTH)
        add(JBScrollPane(resultsTable), BorderLayout.CENTER)
        add(statusLabel, BorderLayout.SOUTH)
    }

    private fun scanCurrentFile() {
        val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return
        val virtualFile = FileEditorManager.getInstance(project).selectedFiles.firstOrNull() ?: return
        val content = editor.document.text
        val docBlocks = DocBlockScanner.scanContent(content, virtualFile.path)
        tableModel.rowCount = 0
        statusLabel.text = "Analyzing ${docBlocks.size} doc block(s) in ${virtualFile.name}…"

        // Git history queries must not run on the EDT.
        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Analyzing documentation freshness") {
            override fun run(indicator: ProgressIndicator) {
                val git = StaleDocInspection.resolveGitAnalyzer(project)
                val results = docBlocks.map { StaleDocInspection.evaluate(it, virtualFile, git) }
                ApplicationManager.getApplication().invokeLater {
                    results.forEach { addResultRow(it) }
                    val note = if (git == null) " (Git unavailable — scores assume fresh docs)" else ""
                    statusLabel.text = "Found ${docBlocks.size} doc block(s) in ${virtualFile.name}$note"
                }
            }
        })
    }

    private fun addResultRow(result: FreshnessResult) {
        val fileName = result.docBlock.filePath.substringAfterLast('/')
        tableModel.addRow(
            arrayOf<Any>(
                fileName,
                result.docBlock.lineStart,
                result.freshnessScore,
                result.level.label,
                result.codeChangesSinceDocChange,
            ),
        )
    }
}
