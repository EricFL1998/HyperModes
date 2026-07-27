package com.banana.hypermodes.protocol

import android.content.Intent

/**
 * Pure classification logic for package lifecycle broadcasts.
 * Shared between system_server and DeskClock hooks.
 */
object PackageLifecyclePolicy {

    enum class Action {
        IGNORE,
        REPLACEMENT_STARTED,
        REPLACEMENT_FINISHED,
        REMOVE
    }

    fun classify(intent: Intent, targetPackage: String): Action {
        val action = intent.action ?: return Action.IGNORE
        val data = intent.data ?: return Action.IGNORE
        if (data.scheme != "package") return Action.IGNORE

        val packageName = data.schemeSpecificPart
        if (packageName != targetPackage) return Action.IGNORE

        val replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)

        return when (action) {
            Intent.ACTION_PACKAGE_REMOVED -> {
                if (replacing) Action.REPLACEMENT_STARTED else Action.REMOVE
            }
            Intent.ACTION_PACKAGE_FULLY_REMOVED -> {
                Action.REMOVE
            }
            Intent.ACTION_PACKAGE_ADDED -> {
                if (replacing) Action.REPLACEMENT_FINISHED else Action.IGNORE
            }
            else -> Action.IGNORE
        }
    }
}
