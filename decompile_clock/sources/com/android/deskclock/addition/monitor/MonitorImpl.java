package com.android.deskclock.addition.monitor;

import android.content.ContentValues;
import android.content.Context;
import com.android.deskclock.Alarm;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.addition.monitor.data.AlarmAlert;
import com.android.deskclock.addition.monitor.data.AlarmAlertHelper;
import com.android.deskclock.addition.monitor.data.AlarmBackupHelper;
import com.android.deskclock.addition.monitor.data.AlarmModify;
import com.android.deskclock.addition.monitor.data.AlarmModifyHelper;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.ParcelableUtil;
import com.android.deskclock.util.PrefUtil;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import com.android.deskclock.util.stat.StatHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class MonitorImpl {
    private static boolean SHOW_LOG = false;
    private static final long SHUTDOWN_ALARM_RANGE = 300000;
    private static final String TAG = "DC:MonitorImpl";
    public static final int VERSION = 1;
    public static final int VERSION_ALERT = 0;
    public static final int VERSION_SHOW = 1;
    public static StringBuilder sLogInfo = new StringBuilder();

    public static boolean isSupport() {
        return OneTrackStatHelper.IS_ENABLED;
    }

    private static void log(String str) {
        if (SHOW_LOG) {
            Log.i(TAG, str);
            sLogInfo.append(str).append("\n");
        }
    }

    public static void reset() {
        Context appDEContext = DeskClockApp.getAppDEContext();
        log("monitor reset");
        try {
            AlarmBackupHelper.deleteAlarmBackup(appDEContext);
            AlarmModifyHelper.deleteAlarmModify(appDEContext);
            AlarmAlertHelper.deleteAlarmAlert(appDEContext);
            AlarmBackupHelper.backupAlarm(DeskClockApp.getAppDEContext());
            AlarmModify alarmModify = new AlarmModify();
            alarmModify.setType(1);
            alarmModify.setTime(System.currentTimeMillis());
            AlarmModifyHelper.insertAlarmModify(appDEContext, alarmModify);
            monitorShutDownAlarmState(System.currentTimeMillis());
            PrefUtil.setMonitorStatus(1);
        } catch (Exception e) {
            PrefUtil.setMonitorStatus(0);
            log("monitor reset error: " + e.getMessage());
        }
    }

    public static void monitorBoot(long j, long j2) {
        log("monitor boot");
        if (isBootTimeValid(j)) {
            try {
                Context appDEContext = DeskClockApp.getAppDEContext();
                AlarmModify alarmModify = new AlarmModify();
                alarmModify.setType(2);
                alarmModify.setTime(j);
                AlarmModifyHelper.insertAlarmModify(appDEContext, alarmModify);
            } catch (Exception e) {
                PrefUtil.setMonitorStatus(2);
                log("monitor boot error: " + e.getMessage());
            }
        } else {
            log("monitor boot error for shutdown time miss");
            reset();
        }
        PrefUtil.setRelativeTimeSet(j);
        PrefUtil.setAbsoluteTimeSet(j2);
    }

    public static void monitorShutdown(long j) {
        Context appDEContext = DeskClockApp.getAppDEContext();
        log("monitor shutdown");
        try {
            AlarmModify alarmModify = new AlarmModify();
            alarmModify.setType(3);
            alarmModify.setTime(j);
            AlarmModifyHelper.insertAlarmModify(appDEContext, alarmModify);
        } catch (Exception e) {
            PrefUtil.setMonitorStatus(2);
            log("monitor shutdown error: " + e.getMessage());
        }
    }

    public static void monitorShutDownAlarmState(long j) {
        Context appDEContext = DeskClockApp.getAppDEContext();
        try {
            AlarmModify alarmModify = new AlarmModify();
            alarmModify.setType(10);
            alarmModify.setTime(j);
            alarmModify.setContent(String.valueOf(FBEUtil.getDefaultSharedPreferences(appDEContext).getBoolean("shutdown_alarm", true)));
            AlarmModifyHelper.insertAlarmModify(appDEContext, alarmModify);
        } catch (Exception e) {
            PrefUtil.setMonitorStatus(2);
            log("monitor modify error: " + e.getMessage());
        }
    }

    public static void monitorTimeSet(long j, long j2) {
        Context appDEContext = DeskClockApp.getAppDEContext();
        log("monitor time set");
        try {
            long relativeTimeSet = PrefUtil.getRelativeTimeSet();
            long absoluteTimeSet = PrefUtil.getAbsoluteTimeSet();
            if (relativeTimeSet > 0 && absoluteTimeSet > 0) {
                long j3 = (relativeTimeSet + j2) - absoluteTimeSet;
                AlarmModify alarmModify = new AlarmModify();
                alarmModify.setType(8);
                alarmModify.setTime(j3);
                AlarmModifyHelper.insertAlarmModify(appDEContext, alarmModify);
                AlarmModify alarmModify2 = new AlarmModify();
                alarmModify2.setType(9);
                alarmModify2.setTime(j);
                AlarmModifyHelper.insertAlarmModify(appDEContext, alarmModify2);
            } else {
                log("monitor time set error: no anchor");
                reset();
            }
        } catch (Exception e) {
            PrefUtil.setMonitorStatus(2);
            log("monitor time set error: " + e.getMessage());
        }
        PrefUtil.setRelativeTimeSet(j);
        PrefUtil.setAbsoluteTimeSet(j2);
    }

    public static void monitorModify(int i, long j, int i2, ContentValues contentValues) {
        Context appDEContext = DeskClockApp.getAppDEContext();
        if (i == 5) {
            log("monitor insert:" + i2 + ", " + contentValues.toString());
        } else if (i == 7) {
            log("monitor update: " + i2 + ", " + contentValues.toString());
        } else if (i == 6) {
            log("monitor delete： " + i2);
        }
        if (i == 6 && i2 == -1) {
            PrefUtil.setMonitorStatus(2);
            return;
        }
        try {
            AlarmModify alarmModify = new AlarmModify();
            alarmModify.setType(i);
            alarmModify.setTime(j);
            alarmModify.setAlarmId(i2);
            alarmModify.setContent(getStringFromContentValues(contentValues));
            AlarmModifyHelper.insertAlarmModify(appDEContext, alarmModify);
        } catch (Exception e) {
            PrefUtil.setMonitorStatus(2);
            log("monitor modify error: " + e.getMessage());
        }
    }

    public static void monitorAlert(int i, long j, long j2, boolean z) {
        log("monitor alert");
        try {
            Context appDEContext = DeskClockApp.getAppDEContext();
            AlarmAlert alarmAlert = new AlarmAlert();
            alarmAlert.setAlarmId(i);
            alarmAlert.setAlarmTime(j);
            alarmAlert.setPlayTime(j2);
            alarmAlert.setScreenLocked(z);
            AlarmAlertHelper.insertOrUpdateAlarmAlert(appDEContext, alarmAlert);
        } catch (Exception e) {
            PrefUtil.setMonitorStatus(2);
            log("monitor alert error: " + e.getMessage());
        }
    }

    public static void monitorShow(int i, long j, long j2) {
        log("monitor show");
        try {
            Context appDEContext = DeskClockApp.getAppDEContext();
            AlarmAlert alarmAlert = new AlarmAlert();
            alarmAlert.setAlarmId(i);
            alarmAlert.setAlarmTime(j);
            alarmAlert.setShowTime(j2);
            AlarmAlertHelper.insertOrUpdateAlarmShow(appDEContext, alarmAlert);
        } catch (Exception e) {
            PrefUtil.setMonitorStatus(2);
            log("monitor notify error: " + e.getMessage());
        }
    }

    public static void trickMonitorReport() {
        long jCurrentTimeMillis = System.currentTimeMillis();
        Context appDEContext = DeskClockApp.getAppDEContext();
        StringBuilder sb = sLogInfo;
        boolean zCalculatorMonitorResult = false;
        sb.delete(0, sb.length());
        log("monitor start: " + Util.formatTimeForLog(System.currentTimeMillis()));
        try {
            AlarmModify alarmModify = new AlarmModify();
            alarmModify.setType(4);
            alarmModify.setTime(System.currentTimeMillis());
            AlarmModifyHelper.insertAlarmModify(appDEContext, alarmModify);
            zCalculatorMonitorResult = calculatorMonitorResult(appDEContext);
        } catch (Exception e) {
            log("monitor error: " + e.getMessage());
        }
        reset();
        long jCurrentTimeMillis2 = System.currentTimeMillis() - jCurrentTimeMillis;
        log("monitor finish use： " + jCurrentTimeMillis2 + "ms");
        if (zCalculatorMonitorResult) {
            int i = ((int) (jCurrentTimeMillis2 / 1000)) + 1;
            StatHelper.recordNumericPropertyEvent(StatHelper.CATEGORY_ALARM_COMMON, StatHelper.KEY_MONITOR_ELAPSED_TIME, i);
            OneTrackStatHelper.trackNumEvent(i, OneTrackStatHelper.ALERT_MONITOR_ELAPSED_TIME);
        }
    }

    private static boolean isBootTimeValid(long j) {
        int size;
        Context appDEContext = DeskClockApp.getAppDEContext();
        try {
            List<AlarmModify> alarmModify = AlarmModifyHelper.getAlarmModify(appDEContext);
            if (alarmModify == null || (size = alarmModify.size()) <= 0) {
                return false;
            }
            long time = alarmModify.get(size - 1).getTime();
            if (j < time) {
                return false;
            }
            List<AlarmAlert> alarmAlert = AlarmAlertHelper.getAlarmAlert(appDEContext);
            int size2 = alarmAlert.size();
            if (size2 > 0) {
                long alarmTime = alarmAlert.get(size2 - 1).getAlarmTime();
                if (alarmTime > time && alarmTime < j) {
                    time = alarmTime;
                }
            }
            AlarmModify alarmModify2 = new AlarmModify();
            alarmModify2.setType(3);
            alarmModify2.setTime(time + 100);
            AlarmModifyHelper.insertAlarmModify(appDEContext, alarmModify2);
            return true;
        } catch (Exception e) {
            log("checkBootTimeValid error: " + e.getMessage());
            return false;
        }
    }

    private static String getStringFromContentValues(ContentValues contentValues) {
        String strSerialize;
        if (contentValues == null) {
            return "";
        }
        try {
            strSerialize = ParcelableUtil.serialize(contentValues);
        } catch (Exception e) {
            log("getStringFromContentValues error: " + e.getMessage());
            strSerialize = "";
        }
        return strSerialize == null ? "" : strSerialize;
    }

    public static ContentValues getContentValuesFromString(String str) {
        try {
            ContentValues contentValues = (ContentValues) ParcelableUtil.deserialize(str, ContentValues.CREATOR);
            log("getContentValuesFromString: " + contentValues.toString());
            return contentValues;
        } catch (Exception e) {
            log("getContentValuesFromString error: " + e.getMessage());
            return null;
        }
    }

    private static Alarm createAlarmFromContentValue(ContentValues contentValues) {
        try {
            Alarm alarm = new Alarm();
            alarm.hour = contentValues.getAsInteger("hour").intValue();
            alarm.minutes = contentValues.getAsInteger("minutes").intValue();
            alarm.daysOfWeek = new Alarm.DaysOfWeek(contentValues.getAsInteger("daysofweek").intValue());
            alarm.time = contentValues.getAsLong("alarmtime").longValue();
            boolean z = true;
            if (contentValues.getAsInteger("enabled").intValue() != 1) {
                z = false;
            }
            alarm.enabled = z;
            alarm.vibrate = contentValues.getAsBoolean("vibrate").booleanValue();
            alarm.label = contentValues.getAsString("message");
            alarm.skipTime = contentValues.getAsLong("skiptime").longValue();
            alarm.seconds = 0;
            return alarm;
        } catch (Exception e) {
            log("createAlarmFromContentValue error: " + e.getMessage());
            return null;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:36:0x0074  */
    private static void updateAlarmWithContentValue(Alarm alarm, ContentValues contentValues) {
        if (alarm == null || contentValues == null) {
            return;
        }
        try {
            Iterator<String> it = contentValues.keySet().iterator();
            while (it.hasNext()) {
                switch (it.next()) {
                    case "hour":
                        alarm.hour = contentValues.getAsInteger("hour").intValue();
                        break;
                    case "minutes":
                        alarm.minutes = contentValues.getAsInteger("minutes").intValue();
                        break;
                    case "daysofweek":
                        alarm.daysOfWeek = new Alarm.DaysOfWeek(contentValues.getAsInteger("daysofweek").intValue());
                        break;
                    case "alarmtime":
                        alarm.time = contentValues.getAsLong("alarmtime").longValue();
                        break;
                    case "enabled":
                        alarm.enabled = contentValues.getAsInteger("enabled").intValue() == 1;
                        break;
                    case "vibrate":
                        alarm.vibrate = contentValues.getAsBoolean("vibrate").booleanValue();
                        break;
                    case "message":
                        alarm.label = contentValues.getAsString("message");
                        break;
                    case "skiptime":
                        alarm.skipTime = contentValues.getAsLong("skiptime").longValue();
                        break;
                }
            }
        } catch (Exception e) {
            log("updateAlarmWithContentValue error: " + e.getMessage());
        }
    }

    public static String testReport(Context context) {
        boolean z;
        StringBuilder sb = new StringBuilder();
        try {
            List<Alarm> alarmBackup = AlarmBackupHelper.getAlarmBackup(context);
            List<AlarmModify> alarmModify = AlarmModifyHelper.getAlarmModify(context);
            List<Alarm> currAlarm = AlarmBackupHelper.getCurrAlarm(context);
            List<AlarmAlert> shouldAlertAlarmList = getShouldAlertAlarmList(context, alarmBackup, alarmModify, currAlarm);
            sb.append("isAlarmsSame: " + isAlarmsSame(alarmBackup, currAlarm)).append("\n");
            List<AlarmAlert> alarmAlert = AlarmAlertHelper.getAlarmAlert(context);
            int monitorStatus = PrefUtil.getMonitorStatus();
            if (shouldAlertAlarmList.size() == 0) {
                sb.append("shouldAlert size = 0");
            } else if (monitorStatus == 1) {
                sb.append("*****************************************************").append("\n");
                int size = shouldAlertAlarmList.size();
                sb.append("should alert：" + size).append("\n");
                for (int i = 0; i < size; i++) {
                    sb.append("shouldTime=" + shouldAlertAlarmList.get(i).getAlarmTime()).append("\n");
                }
                sb.append("*****************************************************").append("\n");
                int size2 = alarmAlert.size();
                sb.append("alert record：" + size2).append("\n");
                for (int i2 = 0; i2 < size2; i2++) {
                    AlarmAlert alarmAlert2 = alarmAlert.get(i2);
                    sb.append("alarmTime=" + alarmAlert2.getAlarmTime() + " alertTime=" + alarmAlert2.getPlayTime()).append("\n");
                }
                sb.append("*****************************************************").append("\n");
                if (size > 10) {
                    log("no need to monitor for should alert alarm count more than 10");
                    sb.append("no need to monitor for should alert alarm count more than 10").append("\n");
                }
                int size3 = shouldAlertAlarmList.size();
                int i3 = 0;
                int i4 = 0;
                int i5 = 0;
                int i6 = 0;
                int i7 = 0;
                for (int i8 = 0; i8 < size3; i8++) {
                    AlarmAlert alarmAlert3 = shouldAlertAlarmList.get(i8);
                    long alarmTime = alarmAlert3.getAlarmTime();
                    boolean zIsShutDown = alarmAlert3.isShutDown();
                    int i9 = 0;
                    while (true) {
                        if (i9 >= size2) {
                            z = false;
                            break;
                        }
                        AlarmAlert alarmAlert4 = alarmAlert.get(i9);
                        long alarmTime2 = alarmAlert4.getAlarmTime();
                        long playTime = alarmAlert4.getPlayTime();
                        if (alarmTime == alarmTime2 && playTime > 0) {
                            z = true;
                            break;
                        }
                        i9++;
                    }
                    if (z) {
                        i3++;
                    }
                    if (zIsShutDown) {
                        i6++;
                        if (z) {
                            i7++;
                        }
                    } else {
                        i4++;
                        if (z) {
                            i5++;
                        }
                    }
                }
                sb.append("all alert： " + i3 + "/" + size3).append("\n");
                sb.append("normal alert： " + i5 + "/" + i4).append("\n");
                sb.append("shutdown alert： " + i7 + "/" + i6).append("\n");
            } else if (monitorStatus == 0) {
                sb.append("monitor cancel: not initialization").append("\n");
            } else {
                sb.append("monitor cancel: error").append("\n");
            }
        } catch (Exception unused) {
        }
        return sb.toString();
    }

    private static boolean calculatorMonitorResult(Context context) throws Exception {
        List<AlarmAlert> shouldAlertAlarmList = getShouldAlertAlarmList(context, AlarmBackupHelper.getAlarmBackup(context), AlarmModifyHelper.getAlarmModify(context), AlarmBackupHelper.getCurrAlarm(context));
        List<AlarmAlert> alarmAlert = AlarmAlertHelper.getAlarmAlert(context);
        int monitorStatus = PrefUtil.getMonitorStatus();
        if (shouldAlertAlarmList.size() == 0) {
            return false;
        }
        if (monitorStatus != 1) {
            if (monitorStatus == 0) {
                log("monitor cancel: not initialization");
                return false;
            }
            log("monitor cancel: error");
            return false;
        }
        log("*****************************************************");
        int size = shouldAlertAlarmList.size();
        log("should alert：" + size);
        for (int i = 0; i < size; i++) {
            log("shouldTime=" + shouldAlertAlarmList.get(i).getAlarmTime());
        }
        log("*****************************************************");
        int size2 = alarmAlert.size();
        log("alert record：" + size2);
        for (int i2 = 0; i2 < size2; i2++) {
            AlarmAlert alarmAlert2 = alarmAlert.get(i2);
            log("alarmTime=" + alarmAlert2.getAlarmTime() + " alertTime=" + alarmAlert2.getPlayTime());
        }
        log("*****************************************************");
        if (size > 10) {
            log("no need to monitor for should alert alarm count more than 10");
            return false;
        }
        calculatorAlert(shouldAlertAlarmList, alarmAlert);
        calculatorShow(alarmAlert);
        return true;
    }

    private static void calculatorAlert(List<AlarmAlert> list, List<AlarmAlert> list2) throws Exception {
        boolean z;
        int size = list.size();
        int size2 = list2.size();
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        int i5 = 0;
        for (int i6 = 0; i6 < size; i6++) {
            AlarmAlert alarmAlert = list.get(i6);
            long alarmTime = alarmAlert.getAlarmTime();
            boolean zIsShutDown = alarmAlert.isShutDown();
            int i7 = 0;
            while (true) {
                if (i7 >= size2) {
                    z = false;
                    break;
                }
                AlarmAlert alarmAlert2 = list2.get(i7);
                long alarmTime2 = alarmAlert2.getAlarmTime();
                long playTime = alarmAlert2.getPlayTime();
                if (alarmTime == alarmTime2 && playTime > 0) {
                    z = true;
                    break;
                }
                i7++;
            }
            if (z) {
                i++;
            }
            if (zIsShutDown) {
                i5++;
                if (z) {
                    i4++;
                }
            } else {
                i3++;
                if (z) {
                    i2++;
                }
            }
        }
        log("alert:");
        log("all alert： " + i + "/" + size);
        log("normal alert： " + i2 + "/" + i3);
        log("shutdown alert： " + i4 + "/" + i5);
        HashMap map = new HashMap();
        map.put(OneTrackStatHelper.PARAM_MONITOR_ALL_ALARM, Integer.valueOf(size));
        map.put(OneTrackStatHelper.PARAM_MONITOR_ALL_ALARM_ALERT, Integer.valueOf(i));
        map.put(OneTrackStatHelper.PARAM_MONITOR_NORMAL_ALARM, Integer.valueOf(i3));
        map.put(OneTrackStatHelper.PARAM_MONITOR_NORMAL_ALARM_ALERT, Integer.valueOf(i2));
        map.put(OneTrackStatHelper.PARAM_MONITOR_SHUT_DOWN_ALARM, Integer.valueOf(i5));
        map.put(OneTrackStatHelper.PARAM_MONITOR_SHUT_DOWN_ALARM_ALERT, Integer.valueOf(i4));
        OneTrackStatHelper.trackMultiParamEvent(OneTrackStatHelper.KEY_MONITOR_ALERT, map, OneTrackStatHelper.ALERT_MONITOR_ALERT);
    }

    private static void calculatorShow(List<AlarmAlert> list) throws Exception {
        int size = list.size();
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        for (int i7 = 0; i7 < size; i7++) {
            AlarmAlert alarmAlert = list.get(i7);
            if (alarmAlert.getPlayTime() > 0) {
                boolean zIsScreenLocked = alarmAlert.isScreenLocked();
                boolean z = alarmAlert.getShowTime() > 0;
                i2++;
                if (z) {
                    i++;
                }
                if (zIsScreenLocked) {
                    i4++;
                    if (z) {
                        i3++;
                    }
                } else {
                    i6++;
                    if (z) {
                        i5++;
                    }
                }
            }
        }
        log("show:");
        log("all show： " + i + "/" + i2);
        log("locked show： " + i3 + "/" + i4);
        log("unlocked show： " + i5 + "/" + i6);
        HashMap map = new HashMap();
        map.put(StatHelper.PARAM_MONITOR_SHOW_ALL, String.valueOf(i2));
        map.put(StatHelper.PARAM_MONITOR_SHOW_ALL_SUCCESS, String.valueOf(i));
        map.put(StatHelper.PARAM_MONITOR_SHOW_LOCKED, String.valueOf(i4));
        map.put(StatHelper.PARAM_MONITOR_SHOW_LOCKED_SUCCESS, String.valueOf(i3));
        map.put(StatHelper.PARAM_MONITOR_SHOW_UNLOCKED, String.valueOf(i6));
        map.put(StatHelper.PARAM_MONITOR_SHOW_UNLOCKED_SUCCESS, String.valueOf(i5));
        StatHelper.recordCountEvent(StatHelper.KEY_MONITOR_SHOW, map);
    }

    /* JADX WARN: Code duplicated, block: B:100:0x020c A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:41:0x0201  */
    /* JADX WARN: Code duplicated, block: B:42:0x0208  */
    /* JADX WARN: Code duplicated, block: B:46:0x0216  */
    /* JADX WARN: Code duplicated, block: B:56:0x022c  */
    /* JADX WARN: Code duplicated, block: B:57:0x022e  */
    /* JADX WARN: Code duplicated, block: B:61:0x0237  */
    /* JADX WARN: Code duplicated, block: B:63:0x0279  */
    /* JADX WARN: Code duplicated, block: B:64:0x0280  */
    /* JADX WARN: Code duplicated, block: B:66:0x0296  */
    /* JADX WARN: Code duplicated, block: B:70:0x02a0 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:72:0x02a3  */
    /* JADX WARN: Code duplicated, block: B:77:0x02b4  */
    /* JADX WARN: Code duplicated, block: B:79:0x02b7  */
    /* JADX WARN: Code duplicated, block: B:85:0x02c5  */
    /* JADX WARN: Code duplicated, block: B:86:0x02c9  */
    private static List<AlarmAlert> getShouldAlertAlarmList(Context context, List<Alarm> list, List<AlarmModify> list2, List<Alarm> list3) throws Exception {
        boolean z;
        boolean z2;
        int i;
        String content;
        ContentValues contentValuesFromString;
        Alarm alarmCreateAlarmFromContentValue;
        ContentValues contentValuesFromString2;
        Alarm alarm;
        List<Long> listCalculateAlarmTimeWithin;
        List<AlarmModify> list4 = list2;
        ArrayList arrayList = new ArrayList();
        if (list != null && list4 != null) {
            int size = list2.size();
            log("modify start with count： " + size);
            if (size >= 2) {
                long j = 0;
                boolean z3 = true;
                int i2 = 0;
                while (i2 < size - 1) {
                    AlarmModify alarmModify = list4.get(i2);
                    int i3 = i2 + 1;
                    AlarmModify alarmModify2 = list4.get(i3);
                    int type = alarmModify.getType();
                    long time = alarmModify.getTime();
                    long time2 = alarmModify2.getTime();
                    int type2 = alarmModify2.getType();
                    int alarmId = alarmModify2.getAlarmId();
                    log("trigger currType");
                    int i4 = size;
                    long j2 = j;
                    if (type2 == 5) {
                        z = z3;
                        log("insert: " + alarmId + " at: " + time2 + "(" + Util.formatTimeForLog(time2) + ")");
                    } else {
                        z = z3;
                        if (type2 == 7) {
                            log("update: " + alarmId + " at: " + time2 + "(" + Util.formatTimeForLog(time2) + ")");
                        } else {
                            if (type2 == 6) {
                                log("delete: " + alarmId + " at: " + time2 + "(" + Util.formatTimeForLog(time2) + ")");
                            } else {
                                z2 = true;
                                if (type2 == 1) {
                                    log("init: " + time2 + "(" + Util.formatTimeForLog(time2) + ")");
                                } else if (type2 == 4) {
                                    log("end: " + time2 + "(" + Util.formatTimeForLog(time2) + ")");
                                } else if (type2 == 3) {
                                    log("shutdown: " + time2 + "(" + Util.formatTimeForLog(time2) + ")");
                                } else if (type2 == 2) {
                                    log("boot: " + time2 + "(" + Util.formatTimeForLog(time2) + ")");
                                } else if (type2 == 8) {
                                    log("time set pre: " + time2 + "(" + Util.formatTimeForLog(time2) + ")");
                                } else if (type2 == 9) {
                                    log("time set: " + time2 + "(" + Util.formatTimeForLog(time2) + ")");
                                }
                            }
                            log("from: " + time + "(" + Util.formatTimeForLog(time) + ")");
                            if (type == 8) {
                                j = j2;
                                z3 = z;
                            } else {
                                if (type == 10) {
                                    try {
                                        z3 = Boolean.getBoolean(alarmModify.getContent());
                                    } catch (Exception unused) {
                                        z3 = z;
                                    }
                                } else {
                                    z3 = z;
                                }
                                if (type2 == 8 && time2 <= time) {
                                    throw new Exception("time invalid");
                                }
                                if (type == 2) {
                                    j = time;
                                } else {
                                    j = j2;
                                }
                                i = 0;
                                while (i < list.size()) {
                                    alarm = list.get(i);
                                    i++;
                                    log("alarm" + i + ": id=" + alarm.id + " hour=" + alarm.hour + " minute=" + alarm.minutes);
                                    if (!alarm.enabled) {
                                        log("not enable");
                                    } else {
                                        listCalculateAlarmTimeWithin = AlarmHelper.calculateAlarmTimeWithin(context, alarm, alarmModify.getTime(), alarmModify2.getTime());
                                        if (listCalculateAlarmTimeWithin.size() == 0) {
                                            log("not in time");
                                        }
                                        if (type == 3 || z3) {
                                            if (type != 3) {
                                                z2 = false;
                                            }
                                            arrayList.addAll(getShouldAlarms(alarm, listCalculateAlarmTimeWithin, z2, j));
                                        }
                                    }
                                    z2 = true;
                                }
                                content = alarmModify2.getContent();
                                if (type2 != 5) {
                                    contentValuesFromString = getContentValuesFromString(content);
                                    if (contentValuesFromString == null && (alarmCreateAlarmFromContentValue = createAlarmFromContentValue(contentValuesFromString)) != null) {
                                        alarmCreateAlarmFromContentValue.id = alarmId;
                                        insertAlarm(list, alarmCreateAlarmFromContentValue);
                                    }
                                } else if (type2 != 6) {
                                    deleteAlarm(list, alarmId);
                                } else if (type2 == 7 && (contentValuesFromString2 = getContentValuesFromString(content)) != null) {
                                    updateAlarm(list, alarmId, contentValuesFromString2);
                                }
                            }
                            list4 = list2;
                            size = i4;
                            i2 = i3;
                        }
                    }
                    z2 = true;
                    log("from: " + time + "(" + Util.formatTimeForLog(time) + ")");
                    if (type == 8) {
                        j = j2;
                        z3 = z;
                    } else {
                        if (type == 10) {
                            z3 = Boolean.getBoolean(alarmModify.getContent());
                        } else {
                            z3 = z;
                        }
                        if (type2 == 8) {
                        }
                        if (type == 2) {
                            j = time;
                        } else {
                            j = j2;
                        }
                        i = 0;
                        while (i < list.size()) {
                            alarm = list.get(i);
                            i++;
                            log("alarm" + i + ": id=" + alarm.id + " hour=" + alarm.hour + " minute=" + alarm.minutes);
                            if (!alarm.enabled) {
                                log("not enable");
                            } else {
                                listCalculateAlarmTimeWithin = AlarmHelper.calculateAlarmTimeWithin(context, alarm, alarmModify.getTime(), alarmModify2.getTime());
                                if (listCalculateAlarmTimeWithin.size() == 0) {
                                    log("not in time");
                                }
                                if (type == 3) {
                                    if (type != 3) {
                                        z2 = false;
                                    }
                                    arrayList.addAll(getShouldAlarms(alarm, listCalculateAlarmTimeWithin, z2, j));
                                } else {
                                    if (type != 3) {
                                        z2 = false;
                                    }
                                    arrayList.addAll(getShouldAlarms(alarm, listCalculateAlarmTimeWithin, z2, j));
                                }
                            }
                            z2 = true;
                        }
                        content = alarmModify2.getContent();
                        if (type2 != 5) {
                            contentValuesFromString = getContentValuesFromString(content);
                            if (contentValuesFromString == null) {
                            }
                        } else if (type2 != 6) {
                            deleteAlarm(list, alarmId);
                        } else if (type2 == 7) {
                            updateAlarm(list, alarmId, contentValuesFromString2);
                        }
                    }
                    list4 = list2;
                    size = i4;
                    i2 = i3;
                }
                log("--------------------------------------------");
            } else {
                log("modify error for count less then 2");
            }
            if (!isAlarmsSame(list, list3)) {
                arrayList.clear();
            }
            log("modify done");
        }
        return arrayList;
    }

    private static boolean isAlarmsSame(List<Alarm> list, List<Alarm> list2) {
        int size;
        if (list == null && list2 == null) {
            return true;
        }
        if (list == null || list2 == null || list2.size() != (size = list.size())) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (!isSame(list.get(i), list2.get(i))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isSame(Alarm alarm, Alarm alarm2) {
        return alarm != null && alarm2 != null && alarm.id == alarm2.id && alarm.hour == alarm2.hour && alarm.minutes == alarm2.minutes && alarm.daysOfWeek.getCoded() == alarm2.daysOfWeek.getCoded() && alarm.time == alarm2.time && alarm.deleteAfterUse == alarm2.deleteAfterUse && alarm.skipTime == alarm2.skipTime;
    }

    private static List<AlarmAlert> getShouldAlarms(Alarm alarm, List<Long> list, boolean z, long j) throws Exception {
        ArrayList arrayList = new ArrayList();
        if (list != null) {
            int size = list.size();
            int i = alarm.id;
            for (int i2 = 0; i2 < size; i2++) {
                long jLongValue = list.get(i2).longValue();
                log("valid alarm, time=" + jLongValue + "(" + Util.formatTimeForLog(jLongValue) + ")");
                AlarmAlert alarmAlert = new AlarmAlert();
                alarmAlert.setAlarmId(i);
                alarmAlert.setAlarmTime(jLongValue);
                alarmAlert.setShutDown(z || jLongValue <= SHUTDOWN_ALARM_RANGE + j);
                arrayList.add(alarmAlert);
            }
        }
        return arrayList;
    }

    private static void insertAlarm(List<Alarm> list, Alarm alarm) throws Exception {
        list.add(alarm);
    }

    private static void updateAlarm(List<Alarm> list, int i, ContentValues contentValues) throws Exception {
        if (contentValues != null) {
            for (int i2 = 0; i2 < list.size(); i2++) {
                Alarm alarm = list.get(i2);
                if (i == alarm.id || i == -1) {
                    updateAlarmWithContentValue(alarm, contentValues);
                    return;
                }
            }
        }
    }

    private static void deleteAlarm(List<Alarm> list, int i) throws Exception {
        for (int size = list.size() - 1; size >= 0; size--) {
            if (i == list.get(size).id) {
                list.remove(size);
                return;
            }
        }
    }
}
