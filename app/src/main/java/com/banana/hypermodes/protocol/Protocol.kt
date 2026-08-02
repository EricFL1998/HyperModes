package com.banana.hypermodes.protocol

/**
 * Wire protocol shared by the manager UI (our process) and the LSPosed hook
 * (running inside com.android.deskclock). Pure constants + day-bitmask math;
 * no Android imports so it stays unit-testable on the JVM.
 */
object Protocol {
    const val MODULE_PACKAGE = "com.banana.hypermodes"
    const val TARGET_PACKAGE = "com.android.deskclock"
    const val DESKCLOCK_PACKAGE = "com.android.deskclock"
    const val SETTINGS_PACKAGE = "com.android.settings"
    /** system_server scope (LSPosed "系统框架"). */
    const val FRAMEWORK_PACKAGE = "android"

    /** Signature-level permission guarding the command receiver in DeskClock. */
    const val PERMISSION_CONTROL = "com.banana.hypermodes.permission.CONTROL"

    // App -> hook
    const val ACTION_APPLY_SCHEDULE = "com.banana.hypermodes.APPLY_SCHEDULE"
    const val ACTION_START_BEDTIME = "com.banana.hypermodes.START_BEDTIME"
    const val ACTION_STOP_BEDTIME = "com.banana.hypermodes.STOP_BEDTIME"
    const val ACTION_QUERY_STATE = "com.banana.hypermodes.QUERY_STATE"
    const val ACTION_QUERY_SCHEDULE = "com.banana.hypermodes.QUERY_SCHEDULE"
    const val ACTION_SHOW_SLEEP_NOTIFICATION = "com.banana.hypermodes.SHOW_SLEEP_NOTIFICATION"
    const val ACTION_ENABLE_WAKE_ALARM = "com.banana.hypermodes.ENABLE_WAKE_ALARM"
    const val ACTION_DISABLE_WAKE_ALARM = "com.banana.hypermodes.DISABLE_WAKE_ALARM"
    const val ACTION_SKIP_WAKE_ALARM_ONCE = "com.banana.hypermodes.SKIP_WAKE_ALARM_ONCE"
    const val ACTION_SET_SLEEP_REMINDER = "com.banana.hypermodes.SET_SLEEP_REMINDER"
    /** Exit the live bedtime session WITHOUT touching the wake alarm schedule
     * (manual turn-off outside the sleep window). */
    const val ACTION_EXIT_BEDTIME = "com.banana.hypermodes.EXIT_BEDTIME"
    const val ACTION_DISABLE_BEDTIME = "com.banana.hypermodes.DISABLE_BEDTIME"

    // Engine (own process, manifest receivers)
    const val ACTION_RESCHEDULE = "com.banana.hypermodes.RESCHEDULE"
    const val ACTION_ALARM_TRIGGER = "com.banana.hypermodes.ALARM_TRIGGER"
    /** Engine -> UI: a mode was activated/deactivated by the engine. */
    const val ACTION_MODE_STATE = "com.banana.hypermodes.MODE_STATE"

    // App -> system_server hook (SystemModeHook bridge)
    const val ACTION_SET_PACKAGES_SUSPENDED = "com.banana.hypermodes.SET_PACKAGES_SUSPENDED"
    const val ACTION_SET_CHANNELS_BYPASS_DND = "com.banana.hypermodes.SET_CHANNELS_BYPASS_DND"
    /** Ask system_server for the saved (configured) WiFi SSID list; the result
     *  comes back on the ResultReceiver in EXTRA_RESULT_RECEIVER. Apps lost
     *  WifiManager.getConfiguredNetworks() in Android 10 — system_server hasn't. */
    const val ACTION_GET_CONFIGURED_WIFI = "com.banana.hypermodes.GET_CONFIGURED_WIFI"
    /** Probe Xiaomi Polaris geofencing capability. Returns structured result via
     *  ResultReceiver indicating whether Polaris service is available and allows
     *  non-SecurityCenter callers. Fail-closed gate before location trigger UI. */
    const val ACTION_PROBE_POLARIS = "com.banana.hypermodes.PROBE_POLARIS"

    // Engine extras
    const val EXTRA_MODE_ID = "modeId"
    const val EXTRA_TRIGGER = "trigger" // "start" | "end"
    const val EXTRA_PACKAGES = "packages"
    const val EXTRA_SUSPENDED = "suspended"
    const val EXTRA_BYPASS = "bypass"
    const val EXTRA_RESULT_RECEIVER = "resultReceiver"
    const val EXTRA_SSIDS = "ssids"
    const val ACTION_RESULT = "com.banana.hypermodes.RESULT"
    /** Unsolicited push: official bedtime activated/deactivated inside DeskClock
     * (scheduled sleep/wake alarms or the Clock app's own toggle). */
    const val ACTION_BEDTIME_ACTIVE = "com.banana.hypermodes.BEDTIME_ACTIVE"

    // Extras
    const val EXTRA_SLEEP_HOUR = "sleepHour"
    const val EXTRA_SLEEP_MIN = "sleepMin"
    const val EXTRA_WAKE_HOUR = "wakeHour"
    const val EXTRA_WAKE_MIN = "wakeMin"
    const val EXTRA_REPEAT_DAYS = "repeatDays"
    const val EXTRA_STEPS = "steps"
    const val EXTRA_IN_SLEEP_MODE = "inSleepMode"
    const val EXTRA_WAKE_ENABLED = "wakeEnabled"
    const val EXTRA_BEDTIME_CONFIGURED = "bedtimeConfigured"
    const val EXTRA_REMINDER_MINUTES = "reminderMinutes"
    const val EXTRA_IS_SKIPPED = "isSkipped"
    /** Extra on ACTION_BEDTIME_ACTIVE: why the state changed
     *  (BedtimeReconciler.Reason name; absent on older hook builds). */
    const val EXTRA_BEDTIME_REASON = "bedtimeReason"
    /** Extra on ACTION_BEDTIME_ACTIVE: sender-side event wall time
     *  (informational only — receivers stamp their own receipt time). */
    const val EXTRA_EVENT_TIME = "eventTime"

    /** Bit 0 = Monday ... bit 6 = Sunday. 127 = every day. */
    const val EVERY_DAY = 0b1111111

    fun daysToBitmask(days: Set<Int>): Int {
        require(days.all { it in 0..6 }) { "day index out of range: $days" }
        return days.fold(0) { acc, day -> acc or (1 shl day) }
    }

    fun bitmaskToDays(bitmask: Int): Set<Int> =
        (0..6).filter { bitmask and (1 shl it) != 0 }.toSet()
}
