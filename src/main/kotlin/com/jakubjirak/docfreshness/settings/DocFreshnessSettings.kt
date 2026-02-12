package com.jakubjirak.docfreshness.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.RoamingType
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import com.jakubjirak.docfreshness.model.FreshnessLevel

/**
 * Persistent settings for Docstring Freshness Monitor.
 */
@State(
    name = "DocFreshnessSettings",
    storages = [Storage(value = "docFreshnessSettings.xml", roamingType = RoamingType.DEFAULT)],
)
class DocFreshnessSettings : PersistentStateComponent<DocFreshnessSettings> {

    var agingThreshold: Int = FreshnessLevel.DEFAULT_AGING_THRESHOLD
    var staleThreshold: Int = FreshnessLevel.DEFAULT_STALE_THRESHOLD
    var rottenThreshold: Int = FreshnessLevel.DEFAULT_ROTTEN_THRESHOLD

    var enableJavaKotlin: Boolean = true
    var enablePython: Boolean = true
    var enableJavaScript: Boolean = true

    var ignoredPatterns: String = ""

    override fun getState(): DocFreshnessSettings = this

    override fun loadState(state: DocFreshnessSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }

    fun getIgnoredPatternList(): List<String> {
        return ignoredPatterns.lines().map { it.trim() }.filter { it.isNotBlank() }
    }

    companion object {
        fun getInstance(): DocFreshnessSettings =
            ApplicationManager.getApplication().getService(DocFreshnessSettings::class.java)
    }
}
