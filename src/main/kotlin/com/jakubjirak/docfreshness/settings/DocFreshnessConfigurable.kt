package com.jakubjirak.docfreshness.settings

import com.intellij.openapi.options.Configurable
import com.intellij.ui.IdeBorderFactory
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextArea
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JSpinner
import javax.swing.SpinnerNumberModel

/**
 * Settings UI for Docstring Freshness Monitor.
 * Located at Settings → Tools → Docstring Freshness Monitor.
 */
class DocFreshnessConfigurable : Configurable {

    private var agingSpinner: JSpinner? = null
    private var staleSpinner: JSpinner? = null
    private var rottenSpinner: JSpinner? = null
    private var enableJavaKotlinCheckbox: JBCheckBox? = null
    private var enablePythonCheckbox: JBCheckBox? = null
    private var enableJavaScriptCheckbox: JBCheckBox? = null
    private var ignoredPatternsArea: JBTextArea? = null
    private var mainPanel: JPanel? = null

    override fun getDisplayName(): String = "Docstring Freshness Monitor"

    override fun createComponent(): JComponent {
        val settings = DocFreshnessSettings.getInstance()

        agingSpinner = JSpinner(SpinnerNumberModel(settings.agingThreshold, 0, 100, 5))
        staleSpinner = JSpinner(SpinnerNumberModel(settings.staleThreshold, 0, 100, 5))
        rottenSpinner = JSpinner(SpinnerNumberModel(settings.rottenThreshold, 0, 100, 5))

        enableJavaKotlinCheckbox = JBCheckBox("Java / Kotlin (Javadoc, KDoc)", settings.enableJavaKotlin)
        enablePythonCheckbox = JBCheckBox("Python (docstrings)", settings.enablePython)
        enableJavaScriptCheckbox = JBCheckBox("JavaScript / TypeScript (JSDoc)", settings.enableJavaScript)

        ignoredPatternsArea = JBTextArea(settings.ignoredPatterns, 4, 40)

        val thresholdsPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Aging threshold (score below):"), agingSpinner!!)
            .addLabeledComponent(JBLabel("Stale threshold (score below):"), staleSpinner!!)
            .addLabeledComponent(JBLabel("Rotten threshold (score below):"), rottenSpinner!!)
            .panel
        thresholdsPanel.border = IdeBorderFactory.createTitledBorder("Freshness Thresholds")

        val languagesPanel = FormBuilder.createFormBuilder()
            .addComponent(enableJavaKotlinCheckbox!!)
            .addComponent(enablePythonCheckbox!!)
            .addComponent(enableJavaScriptCheckbox!!)
            .panel
        languagesPanel.border = IdeBorderFactory.createTitledBorder("Enabled Languages")

        val ignoredPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Ignored file patterns (one per line):"), ignoredPatternsArea!!)
            .panel
        ignoredPanel.border = IdeBorderFactory.createTitledBorder("Ignored Files")

        mainPanel = FormBuilder.createFormBuilder()
            .addComponent(thresholdsPanel)
            .addComponent(languagesPanel)
            .addComponentFillVertically(ignoredPanel, 10)
            .panel

        return mainPanel!!
    }

    override fun isModified(): Boolean {
        val settings = DocFreshnessSettings.getInstance()
        return (agingSpinner?.value as? Int) != settings.agingThreshold ||
            (staleSpinner?.value as? Int) != settings.staleThreshold ||
            (rottenSpinner?.value as? Int) != settings.rottenThreshold ||
            enableJavaKotlinCheckbox?.isSelected != settings.enableJavaKotlin ||
            enablePythonCheckbox?.isSelected != settings.enablePython ||
            enableJavaScriptCheckbox?.isSelected != settings.enableJavaScript ||
            ignoredPatternsArea?.text != settings.ignoredPatterns
    }

    override fun apply() {
        val settings = DocFreshnessSettings.getInstance()
        settings.agingThreshold = agingSpinner?.value as? Int ?: settings.agingThreshold
        settings.staleThreshold = staleSpinner?.value as? Int ?: settings.staleThreshold
        settings.rottenThreshold = rottenSpinner?.value as? Int ?: settings.rottenThreshold
        settings.enableJavaKotlin = enableJavaKotlinCheckbox?.isSelected ?: true
        settings.enablePython = enablePythonCheckbox?.isSelected ?: true
        settings.enableJavaScript = enableJavaScriptCheckbox?.isSelected ?: true
        settings.ignoredPatterns = ignoredPatternsArea?.text ?: ""
    }

    override fun reset() {
        val settings = DocFreshnessSettings.getInstance()
        agingSpinner?.value = settings.agingThreshold
        staleSpinner?.value = settings.staleThreshold
        rottenSpinner?.value = settings.rottenThreshold
        enableJavaKotlinCheckbox?.isSelected = settings.enableJavaKotlin
        enablePythonCheckbox?.isSelected = settings.enablePython
        enableJavaScriptCheckbox?.isSelected = settings.enableJavaScript
        ignoredPatternsArea?.text = settings.ignoredPatterns
    }

    override fun disposeUIResources() {
        agingSpinner = null
        staleSpinner = null
        rottenSpinner = null
        enableJavaKotlinCheckbox = null
        enablePythonCheckbox = null
        enableJavaScriptCheckbox = null
        ignoredPatternsArea = null
        mainPanel = null
    }
}
