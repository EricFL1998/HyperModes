package com.android.deskclock.settings;

import android.R;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.base.BaseActivity;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.Util;

/* JADX INFO: loaded from: classes.dex */
public class AlarmSettingsActivity extends BaseActivity {
    public static final String DEFAULT_SNOOZE = "10";
    public static final String DEFAULT_SNOOZE_REPEAT_COUNT = "3";
    public static final String DEFAULT_VOLUME_BEHAVIOR = "1";
    public static final String KEY_ALARM_APACE_SNOOZE = "alarm_space_snooze_duration";
    public static final String KEY_ALARM_ARRIVING_NOTIFICATION = "alarm_arriving_notification";
    public static final String KEY_ALARM_ASCENDING = "alarm_ascending_mode";
    public static final String KEY_ALARM_SNOOZE = "snooze_duration";
    public static final String KEY_LOCKED_SHOW_ALARM_ARRIVING = "locked_show_alarm_arriving_state";
    public static final String KEY_SHUTDOWN_ALARM = "shutdown_alarm";
    public static final String KEY_SNOOZE_REPEAT_COUNT = "snooze_repeat_count";
    public static final String KEY_SNOOZE_REPEAT_COUNT_REMAINDER = "snooze_repeat_count_remainder";
    public static final String KEY_VOLUME_BEHAVIOR = "volume_button_setting";

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.deskclock.base.BaseActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (Util.isTinyScreen(this)) {
            finish();
        }
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        if (supportFragmentManager.findFragmentByTag(AlarmSettingsFragment.TAG) == null) {
            FragmentTransaction fragmentTransactionBeginTransaction = supportFragmentManager.beginTransaction();
            fragmentTransactionBeginTransaction.add(R.id.content, new AlarmSettingsFragment(), AlarmSettingsFragment.TAG);
            fragmentTransactionBeginTransaction.commit();
        }
    }

    public static void popSnoozeSetToast(String str) {
        DeskClockApp.getAppDEContext();
        if (TextUtils.isEmpty(str)) {
            return;
        }
        Toast.makeText(DeskClockApp.getAppDEContext(), str, 1).show();
    }

    private static String getDefaultSnoozeRepeatCount() {
        return FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppDEContext()).getString("snooze_repeat_count", "3");
    }

    public static String getRemindSnoozeRepeatCount() {
        return FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppDEContext()).getString("snooze_repeat_count_remainder", "3");
    }

    public static void resetSnoozeRepeatCountRemind() {
        updateSnoozeRepeatCountRemind(getDefaultSnoozeRepeatCount());
    }

    public static void updateSnoozeRepeatCountRemind(String str) {
        FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppDEContext()).edit().putString("snooze_repeat_count_remainder", str).apply();
    }
}
