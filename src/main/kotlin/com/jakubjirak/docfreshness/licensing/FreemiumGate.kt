package com.jakubjirak.docfreshness.licensing

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger

/**
 * Central gate for freemium feature access.
 * Free tier: single file analysis, gutter icons, one language.
 * Paid tier: project-wide dashboard, CI export, batch scan, all languages.
 */
@Service(Service.Level.APP)
class FreemiumGate {

    enum class LicenseStatus {
        FREE, TRIAL, PAID, EXPIRED
    }

    /**
     * Check if a paid feature is available for the current license.
     */
    @Suppress("UNUSED_PARAMETER")
    fun isPaidFeatureAvailable(featureId: String): Boolean {
        val status = getLicenseStatus()
        return status == LicenseStatus.PAID || status == LicenseStatus.TRIAL
    }

    fun getLicenseStatus(): LicenseStatus {
        // TODO: Integrate with JetBrains Marketplace Licensing API
        // For now, return FREE — all paid features gated behind this check
        LOG.debug("License check: returning FREE (Marketplace API not yet integrated)")
        return LicenseStatus.FREE
    }

    companion object {
        private val LOG = Logger.getInstance(FreemiumGate::class.java)

        // Feature IDs
        const val FEATURE_PROJECT_DASHBOARD = "project_dashboard"
        const val FEATURE_CI_EXPORT = "ci_export"
        const val FEATURE_BATCH_SCAN = "batch_scan"
        const val FEATURE_ALL_LANGUAGES = "all_languages"
        const val FEATURE_EXPORT_REPORTS = "export_reports"

        fun getInstance(): FreemiumGate =
            ApplicationManager.getApplication().getService(FreemiumGate::class.java)
    }
}
