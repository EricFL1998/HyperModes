package com.android.deskclock.compat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.StatusBarManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.UserHandle;
import android.provider.MiuiSettings;
import miui.app.backup.BackupMeta;
import miui.security.SecurityManager;
import miui.securityspace.CrossUserUtils;
import miui.util.AppConstants;

/* JADX INFO: loaded from: classes.dex */
public class ClockCompat {
    public static final int BackupManager_ERR_NONE = 0;
    public static final String ExtraContacts_LunarBirthday_CONTENT_ITEM_TYPE = "vnd.com.miui.cursor.item/lunarBirthday";
    public static final String MiuiIntent_ACTION_SMART_COVER = "miui.intent.action.SMART_COVER";
    public static final String MiuiIntent_IS_SMART_COVER_OPEN = "is_smart_cover_open";
    public static final String MiuiNotification_EXTRA_SHOW_ACTION = "miui.showAction";
    public static final String MiuiSettings_DEFAULT_ALARM_ALERT = "default_alarm_alert";
    public static final String MiuiSettings_KEY_IN_SMALL_WINDOW_MODE = "is_small_window";
    public static final String MiuiSettings_SCREEN_BUTTONS_STATE = "screen_buttons_state";
    private static final String SHUTDOWN_ALARM_SERVICE_NAME = "com.android.deskclock.util.ShutdownAlarm";
    public static final UserHandle UserHandle_ALL = UserHandle.ALL;
    public static final UserHandle UserHandle_CURRENT = UserHandle.CURRENT;
    public static final int UserHandle_USER_OWNER = 0;

    public static void enableStatusBar(Context context, boolean z) {
        ((StatusBarManager) context.getSystemService("statusbar")).disable(!z ? 23134208 : 0);
    }

    public static void cancelNotification(NotificationManager notificationManager, String str, int i, UserHandle userHandle) {
        notificationManager.cancelAsUser(str, i, userHandle);
    }

    public static void notifyNotification(NotificationManager notificationManager, String str, int i, Notification notification, UserHandle userHandle) {
        notificationManager.notifyAsUser(str, i, notification, userHandle);
    }

    public static PendingIntent getBroadcastPendingIntent(Context context, int i, Intent intent, int i2, UserHandle userHandle) {
        return PendingIntent.getBroadcastAsUser(context, i, intent, i2, userHandle);
    }

    public static PendingIntent getActivityPendingIntent(Context context, int i, Intent intent, int i2, Bundle bundle, UserHandle userHandle) {
        return PendingIntent.getActivityAsUser(context, i, intent, i2, bundle, userHandle);
    }

    public static void setSystemWakeUpTime(long j) {
        ((SecurityManager) AppConstants.getCurrentApplication().getSystemService("security")).setWakeUpTime(SHUTDOWN_ALARM_SERVICE_NAME, j);
    }

    public static Uri addUserIdForUri(Uri uri, int i) {
        return CrossUserUtils.addUserIdForUri(uri, i);
    }

    public static boolean isInSmallWindowMode(Context context) {
        return MiuiSettings.System.isInSmallWindowMode(context);
    }

    public static Rect getDisplayWindowSizeInSmartCover() {
        return MiuiSettings.System.getDisplayWindowSizeInSmartCover();
    }

    public static void goToSleep(Context context, long j) {
        ((PowerManager) context.getSystemService("power")).goToSleep(j);
    }

    public static int getBackupMetaVersion(BackupMeta backupMeta) {
        return backupMeta.version;
    }

    public static boolean isInSecureSpace(Context context) {
        return MiuiSettings.Secure.isSecureSpace(context.getContentResolver());
    }
}
