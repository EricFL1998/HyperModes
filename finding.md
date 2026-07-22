# HyperOS DeskClock Bedtime API Reference

Complete reference for controlling HyperOS DeskClock's bedtime mode programmatically via LSPosed hooks.

---

## Architecture Overview

**Sleep Alarm Storage:**
- Sleep time → SharedPreferences (`BedtimeAlarm`, keys: `KEY_SLEEP_ALARM_HOUR`, `KEY_SLEEP_ALARM_MIN`)
- Wake alarm → ContentProvider database (`content://com.android.deskclock/sleep_alarms`, id = `Integer.MIN_VALUE`)

**Official Bedtime Start Sequence** (when sleep time arrives):
1. `AlarmHelper.setSleepNotification(context)` - Schedule next sleep notification
2. `AlarmHelper.setZenMode(context)` - Evaluate time and enter DND if in sleep window
3. `MiHomeHelper.notifyBedtimeChanged()` - Sync with Mi Home IoT devices

**Official Bedtime Stop Sequence** (when wake time arrives):
1. `SleepModeUtil.exitSleepMode(context)` - Exit powerkeeper sleep mode
2. `AlarmHelper.setSleepNotification(context)` - Reschedule for next day
3. `AlarmHelper.setZenMode(context)` - Exit DND (checks time)

---

## Core Classes

### `com.android.deskclock.alarm.bedtime.BedtimeUtil`

#### `getWakeAlarm(Context context): Alarm`
Fetches the wake alarm object from the `sleep_alarms` table.

**Parameters:**
- `context` - DeskClock application context

**Returns:** `com.android.deskclock.Alarm` with id = `Integer.MIN_VALUE`, or `null` if bedtime not initialized

**Usage:**
```kotlin
val wakeAlarm = Reflect.callStatic(bedtimeUtil, "getWakeAlarm", context)
```

#### `saveSleepAlarm(Context context, Alarm alarm)`
Saves sleep time to SharedPreferences.

**Parameters:**
- `context` - Application context
- `alarm` - Alarm object with `hour` and `minutes` fields set

**Usage:**
```kotlin
val alarm = Reflect.newInstance(alarmClass)
Reflect.setIntField(alarm, "hour", 22)
Reflect.setIntField(alarm, "minutes", 30)
Reflect.callStatic(bedtimeUtil, "saveSleepAlarm", context, alarm)
```

#### `getDisturbanceState(Context context): Boolean`
Checks if DND integration is enabled in bedtime settings.

**Returns:** `true` if user enabled DND integration

---

### `com.android.deskclock.util.AlarmHelper`

#### `setWakeAlarm(Context context, Alarm alarm): long`
Saves wake alarm to the `sleep_alarms` ContentProvider table.

**Parameters:**
- `context` - Application context
- `alarm` - Wake alarm object with mutated time/repeat settings

**Returns:** Calculated next alarm time in milliseconds

**Usage:**
```kotlin
// Mutate wake alarm fields
Reflect.setIntField(wakeAlarm, "hour", 7)
Reflect.setIntField(wakeAlarm, "minutes", 30)
Reflect.callStatic(alarmHelper, "setWakeAlarm", context, wakeAlarm)
```

**Important:** Use `setWakeAlarm()` not `setAlarm()` - wake alarms use a different table!

#### `setSleepNotification(Context context)`
Schedules/reschedules the bedtime reminder notification (shown 15 minutes before sleep time by default).

**Usage:**
```kotlin
Reflect.callStatic(alarmHelper, "setSleepNotification", context)
```

#### `setZenMode(Context context)`
Intelligent DND manager that:
- Checks if DND integration is enabled
- Checks if current time is in sleep window
- Enters DND if in sleep window
- Exits DND if outside sleep window
- Schedules future DND activation if before sleep time

**Important:** Calling this during the day will EXIT DND and schedule it for later. For immediate manual activation, use `ZenModeUtil.enterZenMode()` instead.

**Usage:**
```kotlin
Reflect.callStatic(alarmHelper, "setZenMode", context)
```

---

### `com.android.deskclock.alarm.bedtime.ZenModeUtil`

#### `enterZenMode(Context context)`
Immediately activates DND mode using MIUI-specific APIs.

**Implementation:**
- Android R+ (API 30+): Uses `MiuiSettings.SoundMode.setZenModeOn()` via reflection
- Older Android: Creates/updates `AutomaticZenRule` via `NotificationManager`

**Usage:**
```kotlin
Reflect.callStatic(zenModeUtil, "enterZenMode", context)
```

#### `exitZenMode(Context context)`
Exits DND mode.

**Usage:**
```kotlin
Reflect.callStatic(zenModeUtil, "exitZenMode", context)
```

---

### `com.android.deskclock.alarm.bedtime.HealthDataUtil`

#### `updateSleepSchedule(Context context, int hour, int minutes): int`
Syncs sleep time to Mi Health provider (`content://com.mi.health.provider.main/sleep/schedule`).

**Parameters:**
- `hour` - 24-hour format (0-23)
- `minutes` - 0-59

**Returns:** Number of rows updated

**Usage:**
```kotlin
Reflect.callStatic(healthDataUtil, "updateSleepSchedule", context, 22, 30)
```

#### `updateWakeSchedule(Context context, int hour, int minutes): int`
Syncs wake time to Mi Health provider.

**Usage:**
```kotlin
Reflect.callStatic(healthDataUtil, "updateWakeSchedule", context, 7, 30)
```

---

### `com.android.deskclock.alarm.bedtime.MiHomeHelper`

#### `notifyBedtimeChanged()`
Notifies Mi Home IoT ecosystem of schedule changes.

**Usage:**
```kotlin
val miHomeHelper = Reflect.newInstance(miHomeHelperClass, context)
Reflect.callMethod(miHomeHelper, "notifyBedtimeChanged")
```

---

### `com.android.deskclock.util.NotificationUtil`

#### `showSleepNotification(Context context)`
Immediately displays the bedtime reminder notification (normally shown 15 min before sleep time).

**Usage:**
```kotlin
val notificationUtil = Reflect.findClass("com.android.deskclock.util.NotificationUtil", classLoader)
Reflect.callStatic(notificationUtil, "showSleepNotification", context)
```

---

### `com.android.deskclock.util.SleepModeUtil`

#### `exitSleepMode(Context context)`
Sends broadcast to exit powerkeeper sleep mode.

**Equivalent to:**
```kotlin
val intent = Intent("com.miui.powerkeeper_request_wake")
intent.setPackage("com.miui.powerkeeper")
intent.putExtra("reason", 1)
context.sendBroadcast(intent)
```

#### `inSleepMode(Context context): Boolean`
Queries current powerkeeper sleep mode state from `content://com.miui.powerkeeper.configure`.

**Returns:** `true` if currently in sleep mode

---

## Complete Implementation Examples

### Apply Schedule (Update Sleep/Wake Times)
```kotlin
fun applySchedule(
    sleepHour: Int, sleepMin: Int,
    wakeHour: Int, wakeMin: Int,
    repeatDays: Int
): List<StepResult> {
    val results = mutableListOf<StepResult>()

    // 1. Save sleep time to SharedPreferences
    val sleepAlarm = Reflect.newInstance(alarmClass)
    Reflect.setIntField(sleepAlarm, "hour", sleepHour)
    Reflect.setIntField(sleepAlarm, "minutes", sleepMin)
    Reflect.callStatic(bedtimeUtil, "saveSleepAlarm", context, sleepAlarm)

    // 2. Sync to Mi Health
    Reflect.callStatic(healthDataUtil, "updateSleepSchedule", context, sleepHour, sleepMin)

    // 3. Fetch and mutate wake alarm
    val wakeAlarm = Reflect.callStatic(bedtimeUtil, "getWakeAlarm", context)
    Reflect.setIntField(wakeAlarm, "hour", wakeHour)
    Reflect.setIntField(wakeAlarm, "minutes", wakeMin)
    // Set repeat days via DaysOfWeek wrapper
    val daysOfWeek = Reflect.getField(wakeAlarm, "daysOfWeek")
    Reflect.callMethod(daysOfWeek, "setCoded", repeatDays)

    // 4. Save wake alarm
    Reflect.callStatic(alarmHelper, "setWakeAlarm", context, wakeAlarm)

    // 5. Sync wake to Mi Health
    Reflect.callStatic(healthDataUtil, "updateWakeSchedule", context, wakeHour, wakeMin)

    // 6. Reschedule notification
    Reflect.callStatic(alarmHelper, "setSleepNotification", context)

    // 7. Notify Mi Home
    val miHomeHelper = Reflect.newInstance(miHomeHelperClass, context)
    Reflect.callMethod(miHomeHelper, "notifyBedtimeChanged")

    return results
}
```

### Start Bedtime (Manual Entry)
```kotlin
fun startBedtime(): List<StepResult> {
    val results = mutableListOf<StepResult>()

    // 1. Schedule sleep notification
    Reflect.callStatic(alarmHelper, "setSleepNotification", context)

    // 2. Force DND on immediately (bypass time checks)
    Reflect.callStatic(zenModeUtil, "enterZenMode", context)

    // 3. Notify Mi Home
    val miHomeHelper = Reflect.newInstance(miHomeHelperClass, context)
    Reflect.callMethod(miHomeHelper, "notifyBedtimeChanged")

    return results
}
```

### Stop Bedtime (Manual Exit)
```kotlin
fun stopBedtime(): List<StepResult> {
    val results = mutableListOf<StepResult>()

    // 1. Exit sleep mode
    val intent = Intent("com.miui.powerkeeper_request_wake")
    intent.setPackage("com.miui.powerkeeper")
    intent.putExtra("reason", 1)
    context.sendBroadcast(intent)

    // 2. Reschedule notification for next day
    Reflect.callStatic(alarmHelper, "setSleepNotification", context)

    // 3. Exit DND (evaluates time)
    Reflect.callStatic(alarmHelper, "setZenMode", context)

    // 4. Notify Mi Home
    val miHomeHelper = Reflect.newInstance(miHomeHelperClass, context)
    Reflect.callMethod(miHomeHelper, "notifyBedtimeChanged")

    return results
}
```

---

## Key Findings

1. **Two storage locations:** Sleep time in SharedPreferences, wake alarm in ContentProvider database
2. **Use correct save method:** `setWakeAlarm()` for wake alarms (not `setAlarm()`)
3. **setZenMode() is time-aware:** Will exit DND if called outside sleep window
4. **Manual control needs direct calls:** Use `ZenModeUtil.enterZenMode()` for immediate activation
5. **Complete sync required:** Must call both HealthDataUtil methods and MiHomeHelper for full ecosystem sync
6. **Notification scheduling:** `setSleepNotification()` is part of both start and stop sequences

---

## Alarm Object Structure

**Class:** `com.android.deskclock.Alarm`

**Key fields:**
- `id: int` - Database ID (wake alarm uses `Integer.MIN_VALUE`)
- `hour: int` - 24-hour format (0-23)
- `minutes: int` - 0-59
- `enabled: boolean`
- `daysOfWeek: Alarm.DaysOfWeek` - Repeat schedule wrapper
- `vibrate: boolean`
- `alert: Uri` - Ringtone
- `skipTime: long` - Timestamp for skipped alarm
- `time: long` - Next fire time (calculated, not persisted for repeating alarms)

**DaysOfWeek encoding:**
- Bit 0 = Monday, Bit 6 = Sunday
- `127` (0b1111111) = every day
- Access via `getCoded()` / `setCoded(int)` methods

### `Alarm.DaysOfWeek` Coded Values (complete)

From `com.android.deskclock.Alarm$DaysOfWeek`:

| Value | Constant | Meaning |
|-------|----------|---------|
| `0`   | `NO_DAY` | Never / once only |
| `31`  | `MONDAY_TO_FRIDAY` | 周一至周五 (bits 0-4) |
| `96`  | `WEEKENDS` | Sat+Sun (bits 5-6) |
| `127` | `EVERY_DAY` | 每天 |
| `128` | `LEGAL_WORK_DAY` | 法定工作日（智能跳过节假日）— bit 7 |
| `256` | `LEGAL_OFF_DAY` | 法定节假日（智能跳过工作日）— bit 8 |
| `512` | `SHIFT_DAY` | 倒班闹钟 |

- `DAY_MAP = {2,3,4,5,6,7,1}` maps bit index → `Calendar.DAY_OF_WEEK` (bit 0 = Monday = Calendar.MONDAY(2)).
- `getAlarmType()`: 0=once, 1=every day, 2=legal workday, 3=legal off day, 4=Mon-Fri, 5=custom, 6=shift.
- `getNextAlarm()` for 128/256 consults `HolidayHelper.isHoliday(context, calendar)` ( holiday data from `com.android.deskclock.addition.holiday.HolidayInstance`); if holiday data is invalid the label falls back to `legal_workday_invalidate`.

---

## Wake Alarm Toggle Sequences (起床响铃 switch)

From `BedtimeManageActivity.onPreferenceChange()` + `showRepeatAlarmTurnOffDialog()`:

**Toggle ON:**
```kotlin
AlarmHelper.enableAlarm(context, Integer.MIN_VALUE, true)
```

**Toggle OFF → ActionSheet with 3 options:**
- 仅关闭一次: `AlarmHelper.skipAlarmForOnce(context, Integer.MIN_VALUE)` + `AlarmHelper.registerWakeAlarm(context)` — alarm stays enabled, only the next occurrence is skipped
- 永久关闭: `AlarmHelper.enableAlarm(context, Integer.MIN_VALUE, false)` + `AlarmHelper.registerWakeAlarm(context)`
- 取消: re-check the toggle, no change

### `AlarmHelper` additional methods

#### `enableAlarm(Context context, int id, boolean enabled)`
Enables/disables an alarm in the `sleep_alarms` table (use id = `Integer.MIN_VALUE` for the wake alarm).

#### `skipAlarmForOnce(Context context, int id)`
Marks the next occurrence of the alarm as skipped (sets `skipTime`).

#### `registerWakeAlarm(Context context)`
Re-registers the wake alarm with the system AlarmManager after skip/disable.

---

## Bedtime Setup State

`BedtimeUtil.bedTimeAlarmCompleted(context)` reads `KEY_BEDTIME_ALARM_COMPLETED` from the `BedtimeAlarm` SharedPreferences — `false` means the user never finished bedtime setup (show intro/guide page).

`BedtimeUtil.isBedtimeOpen(context)` reads `KEY_OPEN_BEDTIME` — master bedtime switch.

---

## Exported Activities (manifest)

| Component | exported | Purpose |
|-----------|----------|---------|
| `com.android.deskclock.alarm.bedtime.BedtimeManageActivity` | yes (label `@string/bedtime`) | Main bedtime manage page (作息) |
| `com.android.deskclock.alarm.bedtime.BedtimeGuideActivity` | yes | First-time setup guide |
| `com.android.deskclock.alarm.bedtime.BedtimeSettingsActivity` | yes (label `@string/bedtime_settings`) | Bedtime settings |

Launch first-time setup with `BedtimeGuideActivity`, fall back to `BedtimeManageActivity`.

## Provider

`com.android.deskclock.alarm.bedtime.BedtimeProvider` — authority `com.android.deskclock.bedtimeProvider`.
