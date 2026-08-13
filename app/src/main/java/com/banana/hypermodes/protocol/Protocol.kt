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
    /** App -> system_server: 开关个人热点（system_server 内用系统 API flip switch）。 */
    const val ACTION_SET_HOTSPOT_ENABLED = "com.banana.hypermodes.SET_HOTSPOT_ENABLED"
    /** App -> system_server: 通用特权操作（写 Settings / 飞行模式 / 手电筒 / SIM 等）。
     *  所有自动化里需要系统级权限的操作统一走这个 action，不再用 root shell 兜底。 */
    const val ACTION_SYSTEM_OP = "com.banana.hypermodes.SYSTEM_OP"
    const val EXTRA_OP = "op"

    // ACTION_SYSTEM_OP 的操作码
    /** 写 Settings 值：EXTRA_NAMESPACE + EXTRA_KEY + EXTRA_VALUE（字符串）。 */
    const val OP_WRITE_SETTING = "write_setting"
    const val OP_SET_AIRPLANE_ENABLED = "set_airplane_enabled"
    const val OP_SET_MOBILE_DATA_ENABLED = "set_mobile_data_enabled"
    const val OP_SET_FLASHLIGHT_ENABLED = "set_flashlight_enabled"
    const val OP_SET_PREFERRED_SIM_SLOT = "set_preferred_sim_slot"
    const val OP_SET_MOTION_SICKNESS_RELIEF = "set_motion_sickness_relief"
    const val OP_SET_WIFI_ENABLED = "set_wifi_enabled"
    const val OP_SET_BLUETOOTH_ENABLED = "set_bluetooth_enabled"
    const val EXTRA_NAMESPACE = "namespace"
    const val EXTRA_KEY = "key"
    const val EXTRA_VALUE = "value"
    const val EXTRA_SLOT = "slot"

    /** Ask system_server for the saved (configured) WiFi SSID list; the result
     *  comes back on the ResultReceiver in EXTRA_RESULT_RECEIVER. Apps lost
     *  WifiManager.getConfiguredNetworks() in Android 10 — system_server hasn't. */
    const val ACTION_GET_CONFIGURED_WIFI = "com.banana.hypermodes.GET_CONFIGURED_WIFI"
    /** Probe Xiaomi Polaris geofencing capability. Returns structured result via
     *  ResultReceiver indicating whether Polaris service is available and allows
     *  non-SecurityCenter callers. Fail-closed gate before location trigger UI. */
    const val ACTION_PROBE_POLARIS = "com.banana.hypermodes.PROBE_POLARIS"
    /** App -> system_server: capture the current lock-screen style JSON + wallpaper
     *  files into a snapshot dir readable by the App, and return paths/values via
     *  the ResultReceiver in EXTRA_RESULT_RECEIVER. Used after the user edits
     *  wallpaper in the official ThemeManager UI so HyperModes can store the set. */
    const val ACTION_CAPTURE_WALLPAPER_SNAPSHOT =
        "com.banana.hypermodes.CAPTURE_WALLPAPER_SNAPSHOT"
    /** App -> system_server: 把模式已保存的单个壁纸子项（锁屏/桌面）写入系统，
     *  让官方编辑器从保存的样式开始编辑（而不是每次从当前系统样式开始）。 */
    const val ACTION_PREPARE_WALLPAPER_EDIT =
        "com.banana.hypermodes.PREPARE_WALLPAPER_EDIT"

    // System_server -> Engine: Polaris geofence event (internal authenticated broadcast)
    const val ACTION_POLARIS_GEOFENCE_EVENT =
        "com.banana.hypermodes.POLARIS_GEOFENCE_EVENT"
    const val EXTRA_POLARIS_FENCE_ID = "polarisFenceId"
    const val EXTRA_POLARIS_EVENT = "polarisEvent"

    // Engine extras
    const val EXTRA_MODE_ID = "modeId"
    const val EXTRA_TRIGGER = "trigger" // "start" | "end"
    const val EXTRA_PACKAGES = "packages"
    const val EXTRA_SUSPENDED = "suspended"
    const val EXTRA_ENABLED = "enabled"
    const val EXTRA_BYPASS = "bypass"
    const val EXTRA_RESULT_RECEIVER = "resultReceiver"
    const val EXTRA_SSIDS = "ssids"
    /** Wallpaper snapshot result extras (ACTION_CAPTURE_WALLPAPER_SNAPSHOT). */
    const val EXTRA_LOCKSCREEN_JSON = "lockscreenJson"
    const val EXTRA_TEMPLATE_EDITOR_JSON = "templateEditorJson"
    const val EXTRA_DEFAULT_LOCKSCREEN_JSON = "defaultLockscreenJson"
    const val EXTRA_LOCKSCREEN_VERSION = "lockscreenVersion"
    const val EXTRA_LOCK_IMAGE_PATH = "lockImagePath"
    const val EXTRA_DESKTOP_IMAGE_PATH = "desktopImagePath"
    /** JPEG-compressed wallpaper bytes returned by system_server (scoped storage
     *  blocks writing into the app's external dir from another process, so the
     *  app writes these bytes to its own files dir instead). */
    const val EXTRA_LOCK_IMAGE_BYTES = "lockImageBytes"
    const val EXTRA_DESKTOP_IMAGE_BYTES = "desktopImageBytes"
    /** system_server 落盘的壁纸路径（system 可读，模式应用时从此复制）。 */
    const val EXTRA_LOCK_SYS_IMAGE_PATH = "lockSysImagePath"
    const val EXTRA_DESKTOP_SYS_IMAGE_PATH = "desktopSysImagePath"
    /** system_server 落盘的锁屏主体蒙版路径（system 可读）。 */
    const val EXTRA_SUBJECT_MASK_SYS_PATH = "subjectMaskSysPath"
    /** 锁屏壁纸主体蒙版（subject_mask）JPEG 字节，用于预览景深效果。 */
    const val EXTRA_SUBJECT_MASK_BYTES = "subjectMaskBytes"
    const val EXTRA_DESKTOP_SCROLL_ENABLED = "desktopScrollEnabled"
    const val EXTRA_WALLPAPER_EFFECT_TYPE = "wallpaperEffectType"
    /** 锁屏景深/壁纸特效类型（wallpaper_effect_type_2）。 */
    const val EXTRA_LOCK_WALLPAPER_EFFECT_TYPE = "lockWallpaperEffectType"
    const val EXTRA_WALLPAPER_CHANGED = "wallpaperChanged"
    /** Capture the current system wallpaper into a shared preview dir instead of
     *  a mode-specific one, so entering the detail page never overwrites a saved
     *  mode's wallpaper files. */
    const val EXTRA_PREVIEW_ONLY = "previewOnly"
    /** 预置编辑时指定子项：1 桌面 / 2 锁屏（对应 WallpaperItem.which）。 */
    const val EXTRA_WHICH = "which"
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
