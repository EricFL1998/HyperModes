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

    /** Signature-level permission guarding the command receiver in DeskClock. */
    const val PERMISSION_CONTROL = "com.banana.hypermodes.permission.CONTROL"

    // App -> hook
    const val ACTION_APPLY_SCHEDULE = "com.banana.hypermodes.APPLY_SCHEDULE"
    const val ACTION_START_BEDTIME = "com.banana.hypermodes.START_BEDTIME"
    const val ACTION_STOP_BEDTIME = "com.banana.hypermodes.STOP_BEDTIME"
    const val ACTION_QUERY_STATE = "com.banana.hypermodes.QUERY_STATE"
    const val ACTION_SHOW_SLEEP_NOTIFICATION = "com.banana.hypermodes.SHOW_SLEEP_NOTIFICATION"

    // Hook -> app
    const val ACTION_RESULT = "com.banana.hypermodes.RESULT"

    // Extras
    const val EXTRA_SLEEP_HOUR = "sleepHour"
    const val EXTRA_SLEEP_MIN = "sleepMin"
    const val EXTRA_WAKE_HOUR = "wakeHour"
    const val EXTRA_WAKE_MIN = "wakeMin"
    const val EXTRA_REPEAT_DAYS = "repeatDays"
    const val EXTRA_STEPS = "steps"
    const val EXTRA_IN_SLEEP_MODE = "inSleepMode"

    /** Bit 0 = Monday ... bit 6 = Sunday. 127 = every day. */
    const val EVERY_DAY = 0b1111111

    fun daysToBitmask(days: Set<Int>): Int {
        require(days.all { it in 0..6 }) { "day index out of range: $days" }
        return days.fold(0) { acc, day -> acc or (1 shl day) }
    }

    fun bitmaskToDays(bitmask: Int): Set<Int> =
        (0..6).filter { bitmask and (1 shl it) != 0 }.toSet()
}
