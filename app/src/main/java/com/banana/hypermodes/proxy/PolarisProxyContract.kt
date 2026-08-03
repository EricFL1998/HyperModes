package com.banana.hypermodes.proxy

/**
 * Contract for Polaris Proxy ContentProvider.
 * Defines methods and parameters for cross-process communication.
 */
object PolarisProxyContract {
    const val AUTHORITY = "com.banana.hypermodes.provider.polaris"

    // Methods
    const val METHOD_INIT = "init"
    const val METHOD_ADD_GEOFENCE = "add_geofence"
    const val METHOD_REMOVE_GEOFENCE = "remove_geofence"
    const val METHOD_CLEAR_ALL = "clear_all"
    const val METHOD_IS_CONNECTED = "is_connected"
    const val METHOD_SEND_DEBUG_EVENT = "send_debug_event"

    // Parameters for add_geofence
    const val PARAM_FENCE_ID = "fence_id"
    const val PARAM_MODE_ID = "mode_id"
    const val PARAM_TRIGGER_ID = "trigger_id"
    const val PARAM_LATITUDE = "latitude"
    const val PARAM_LONGITUDE = "longitude"
    const val PARAM_RADIUS = "radius"
    const val PARAM_TRANSITION_TYPE = "transition_type"
    const val PARAM_CONFIDENCE = "confidence"

    // Parameters for remove_geofence
    // PARAM_FENCE_ID (reused)

    // Parameters for send_debug_event
    // PARAM_FENCE_ID (reused)
    const val PARAM_EVENT_TYPE = "event_type"
    // PARAM_LATITUDE (reused)
    // PARAM_LONGITUDE (reused)

    // Result keys
    const val RESULT_SUCCESS = "success"
    const val RESULT_ERROR_MSG = "error_msg"
    const val RESULT_IS_CONNECTED = "is_connected"
}
