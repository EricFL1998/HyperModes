package com.android.deskclock;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import com.android.deskclock.base.BaseActivity;
import com.android.deskclock.util.Util;

/* JADX INFO: loaded from: classes.dex */
public class ShortcutTrampolineActivity extends BaseActivity {
    public static final String EXTRA_SHORTCUT = "shortcut";
    public static final String SHORTCUT_NEW_ALARM = "com.android.deskclock.shortcut.NEW_ALARM";
    public static final String SHORTCUT_START_TIMER = "com.android.deskclock.shortcut.START_TIMER";
    public static final String SHORTCUT_STOP_WATCH = "com.android.deskclock.shortcut.STOP_WATCH";

    @Override // com.android.deskclock.base.BaseActivity
    protected void checkCtaStatement() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.deskclock.base.BaseActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (Util.isTinyScreen(this)) {
            finish();
        }
        Intent intent = getIntent();
        if (intent == null) {
            gotoDeskClockTabActivity();
            return;
        }
        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            gotoDeskClockTabActivity();
            return;
        }
        if (SHORTCUT_NEW_ALARM.equals(action)) {
            newAlarm();
        } else if (SHORTCUT_STOP_WATCH.equals(action)) {
            startStopWatch();
        } else if (SHORTCUT_START_TIMER.equals(action)) {
            startTimer();
        }
        finish();
    }

    public void gotoDeskClockTabActivity() {
        startActivity(new Intent(this, (Class<?>) DeskClockTabActivity.class));
        finish();
    }

    public void newAlarm() {
        Intent intent = new Intent(this, (Class<?>) DeskClockTabActivity.class);
        intent.putExtra(Util.NAVIGATION_TAB, 0);
        intent.putExtra(EXTRA_SHORTCUT, SHORTCUT_NEW_ALARM);
        startActivity(intent);
        finish();
    }

    public void startStopWatch() {
        Intent intent = new Intent(this, (Class<?>) DeskClockTabActivity.class);
        intent.putExtra(Util.NAVIGATION_TAB, 2);
        intent.putExtra(EXTRA_SHORTCUT, SHORTCUT_STOP_WATCH);
        startActivity(intent);
        finish();
    }

    public void startTimer() {
        Intent intent = new Intent(this, (Class<?>) DeskClockTabActivity.class);
        intent.putExtra(Util.NAVIGATION_TAB, 3);
        intent.putExtra(EXTRA_SHORTCUT, SHORTCUT_START_TIMER);
        startActivity(intent);
        finish();
    }
}
