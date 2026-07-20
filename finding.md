To fully understand how to invoke and utilize these internal methods reliably in your LSPosed module, we need to break down each native Java method discovered in DeskClock's decompiled source code.

Below is a detailed, parameter-by-parameter analysis of the internal methods, what they expect, what they return, and how to call them correctly via reflection.

1. BedtimeUtil.getSleepAlarm(Context context)
Purpose: Fetches the active sleep schedule configuration object (com.android.deskclock.Alarm) from the system's internal storage.

Parameters: context (usually the application or activity context).

Return Type: com.android.deskclock.Alarm (or null if no bedtime has ever been initialized).

Why you need it: Before you can update a time, you need the underlying Alarm instance so you don't overwrite its unique database ID or custom flags.

Reflection Invocation:

Kotlin
val bedtimeUtilClass = XposedHelpers.findClass("com.android.deskclock.alarm.bedtime.BedtimeUtil", classLoader)
val sleepAlarm = XposedHelpers.callStaticMethod(bedtimeUtilClass, "getSleepAlarm", context)
2. BedtimeUtil.saveSleepAlarm(Context context, Alarm alarm)
Purpose: Persists the modified Alarm object back into DeskClock's persistent storage layer.

Parameters:

context (Context)

alarm (com.android.deskclock.Alarm)

Return Type: void (or internal status flags, but treated as a procedure).

Why you need it: Without calling this, your changes will only exist in temporary runtime memory and will revert the moment the clock process restarts.

Reflection Invocation:

Kotlin
XposedHelpers.callStaticMethod(
    bedtimeUtilClass, 
    "saveSleepAlarm", 
    context, 
    sleepAlarm // The modified Alarm object
)
3. HealthDataUtil.updateSleepSchedule(Context context, int hour, int minutes)
Purpose: Synchronizes the new sleep time with the system-level Mi Health provider (content://com.mi.health.provider.main/sleep/schedule) using the proper snake_case column mapping (sleep_hour).

Parameters:

context (Context)

hour (int - 24-hour format, e.g., 22)

minutes (int - e.g., 46)

Return Type: int (Returns the number of rows updated, or an error code parsed from IllegalStateException).

Why you need it: This satisfies the ecosystem requirement so that Mi Health and system widgets recognize the new schedule.

Reflection Invocation:

Kotlin
val healthUtilClass = XposedHelpers.findClass("com.android.deskclock.alarm.bedtime.HealthDataUtil", classLoader)
XposedHelpers.callStaticMethod(
    healthUtilClass, 
    "updateSleepSchedule", 
    context, 
    22, // hour
    46  // minutes
)
4. HealthDataUtil.updateWakeSchedule(Context context, int hour, int minutes)
Purpose: Identical to the sleep schedule updater, but targets the wake-up schema (wake_hour / wake_min) in Mi Health.

Parameters: Context, int (hour), int (minutes).

Return Type: int.

Reflection Invocation:

Kotlin
XposedHelpers.callStaticMethod(
    healthUtilClass, 
    "updateWakeSchedule", 
    context, 
    7,  // hour
    30  // minutes
)
5. MiHomeHelper.notifyBedtimeChanged()
Purpose: Triggers Xiaomi's smart home ecosystem handler to pass the new schedule down to connected IoT devices (e.g., turning off smart lights at bedtime).

Parameters: Instantiated with a Context.

Return Type: void.

Why you need it: Essential for achieving true feature parity with official HyperOS bedtime automations.

Reflection Invocation:

Kotlin
val miHomeHelperClass = XposedHelpers.findClass("com.android.deskclock.alarm.bedtime.MiHomeHelper", classLoader)
val miHomeHelperInstance = XposedHelpers.newInstance(miHomeHelperClass, context)
XposedHelpers.callMethod(miHomeHelperInstance, "notifyBedtimeChanged")
6. AlarmHelper.setSleepNotification(Context context)
Purpose: Instructs the Android AlarmManager to schedule the preemptive bedtime notification/reminder alerts.

Parameters: context (Context).

Return Type: Varies by internal implementation.

Reflection Invocation:

Kotlin
val alarmHelperClass = XposedHelpers.findClass("com.android.deskclock.util.AlarmHelper", classLoader)
XposedHelpers.callStaticMethod(alarmHelperClass, "setSleepNotification", context)
7. BedtimeUtil.getDisturbanceState(Context context) & AlarmHelper.setZenMode(Context context)
Purpose:

getDisturbanceState checks if the user has enabled the DND/Zen Mode integration toggle within the Bedtime settings.

setZenMode forces the system to enter Do Not Disturb matching the sleep duration parameters.

Reflection Invocation:

Kotlin
val isDndEnabled = XposedHelpers.callStaticMethod(bedtimeUtilClass, "getDisturbanceState", context) as Boolean
if (isDndEnabled) {
    XposedHelpers.callStaticMethod(alarmHelperClass, "setZenMode", context)
}