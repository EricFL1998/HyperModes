package com.android.deskclock;

import android.content.Intent;
import android.os.Bundle;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.alarm.bedtime.BedtimeGuideActivity;
import com.android.deskclock.alarm.bedtime.BedtimeManageActivity;
import com.android.deskclock.alarm.bedtime.BedtimeUtil;
import com.android.deskclock.base.BaseActivity;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.Util;

/* JADX INFO: loaded from: classes.dex */
public class HandleActivityIntent extends BaseActivity {
    private static final String ACTION_BEDTIME_MANAGE = "com.android.deskclock.BEDTIME_MANAGE";

    @Override // com.android.deskclock.base.BaseActivity
    protected void checkCtaStatement() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.deskclock.base.BaseActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        try {
            try {
                super.onCreate(bundle);
                if (Util.isTinyScreen(this)) {
                    finish();
                }
                Intent intent = getIntent();
                if (intent != null) {
                    Log.d("HandleActivityIntent, intent.getAction() = " + intent.getAction());
                }
                if (ACTION_BEDTIME_MANAGE.equals(intent.getAction())) {
                    handleBedtimeManage();
                }
            } catch (Exception e) {
                Log.e("HandleActivityIntent onCreate error, error is ", e);
            }
        } finally {
            finish();
        }
    }

    private void handleBedtimeManage() {
        if (MiuiSdk.isSupportSleep()) {
            if (BedtimeUtil.bedTimeAlarmCompleted(this)) {
                startActivity(new Intent(this, (Class<?>) BedtimeManageActivity.class));
            } else {
                startActivity(new Intent(this, (Class<?>) BedtimeGuideActivity.class));
            }
        }
    }
}
