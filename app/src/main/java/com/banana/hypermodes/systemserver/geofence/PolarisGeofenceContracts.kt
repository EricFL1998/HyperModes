package com.banana.hypermodes.systemserver.geofence

/**
 * Pure Polaris contract constants and types for geofence integration.
 * All values match the device Polaris service contract.
 */
internal object PolarisContract {
    const val SERVICE_PACKAGE = "com.xiaomi.gnss.polaris"
    const val SERVICE_CLASS = "com.xiaomi.gnss.polaris.PolarisService"
    const val CLIENT_PACKAGE = "android"
    const val CALLBACK_PACKAGE = "com.banana.hypermodes"
    const val FENCE_PREFIX = "hypermodes_"
    const val EXTRA_FENCE_ID = "context-data"
    const val EXTRA_EVENT = "transition-event"
    const val EVENT_ENTER = 11
    const val EVENT_EXIT = 12
    const val STATUS_UNKNOWN = 0
    const val STATUS_IN = 1
    const val STATUS_OUT = 2
    const val TRANSITION_BOTH = 3
    const val CONFIDENCE_HIGH = 3

    /**
     * Parse and validate a Polaris callback payload.
     * Returns null if the payload doesn't match the expected format.
     */
    fun parseCallback(fenceId: String?, event: Int): PolarisCallback? {
        val id = fenceId?.takeIf { it.isNotBlank() && it.startsWith(FENCE_PREFIX) } ?: return null
        if (event != EVENT_ENTER && event != EVENT_EXIT) return null
        return PolarisCallback(id, event)
    }

    /**
     * Verify that a broadcast sender is the Polaris service.
     * Both the sender package and the UID's package list must contain the Polaris package.
     */
    fun senderIsPolaris(sentFromPackage: String?, packagesForUid: Array<String>?): Boolean =
        sentFromPackage == SERVICE_PACKAGE && packagesForUid?.contains(SERVICE_PACKAGE) == true
}

/**
 * Validated Polaris callback payload.
 */
internal data class PolarisCallback(val fenceId: String, val event: Int)

/**
 * Complete specification for a geofence to register with Polaris.
 */
internal data class PolarisFenceSpec(
    val fenceId: String,
    val modeId: String,
    val triggerId: String,
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Int,
    val transitionType: Int = PolarisContract.TRANSITION_BOTH,
    val confidence: Int = PolarisContract.CONFIDENCE_HIGH
)

/**
 * Remote geofence state as returned by Polaris.
 * Used for reconciliation and verification.
 */
internal data class PolarisRemoteFence(
    val fenceId: String,
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Int,
    val transitionType: Int,
    val confidence: Int,
    val packageName: String?
)
