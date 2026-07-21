package com.android.deskclock.alarm.shiftalarm;

import android.os.Bundle;
import android.util.Log;
import com.android.deskclock.base.BaseActivity;

/* JADX INFO: loaded from: classes.dex */
public class ShiftAlarmEditEmptyActivity extends BaseActivity {
    private static final String TAG = "DC:ShiftAlarmEditEmptyActivity";

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.deskclock.base.BaseActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.d(TAG, "onCreate: ");
        finish();
    }
}
