package com.android.deskclock.addition.monitor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.android.deskclock.R;
import com.android.deskclock.base.BaseActivity;

/* JADX INFO: loaded from: classes.dex */
public class TestActivity extends BaseActivity {
    private TextView infoTv;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.deskclock.base.BaseActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.test_layout);
    }

    public static void startMonitorTest(Context context) {
        context.startActivity(new Intent(context, (Class<?>) TestActivity.class));
    }
}
