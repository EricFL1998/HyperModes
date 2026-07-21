package com.android.deskclock.addition.monitor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.addition.monitor.data.AlarmBackupHelper;
import com.android.deskclock.addition.monitor.data.AlarmModify;
import com.android.deskclock.addition.monitor.data.AlarmModifyHelper;
import com.android.deskclock.base.BaseActivity;

/* JADX INFO: loaded from: classes.dex */
public class MonitorTestActivity extends BaseActivity implements View.OnClickListener {
    private TextView mInfoTv;
    private Button reportTv;
    private Button resetTv;
    private Button showAlarmsTv;
    private Button showModifyTv;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.deskclock.base.BaseActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_monitor);
        Button button = (Button) findViewById(R.id.reset);
        this.resetTv = button;
        button.setOnClickListener(this);
        Button button2 = (Button) findViewById(R.id.report);
        this.reportTv = button2;
        button2.setOnClickListener(this);
        Button button3 = (Button) findViewById(R.id.show_alarms);
        this.showAlarmsTv = button3;
        button3.setOnClickListener(this);
        Button button4 = (Button) findViewById(R.id.show_modify);
        this.showModifyTv = button4;
        button4.setOnClickListener(this);
        this.mInfoTv = (TextView) findViewById(R.id.info);
    }

    public static void startMonitorTest(Context context) {
        context.startActivity(new Intent(context, (Class<?>) MonitorTestActivity.class));
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.report /* 2131362652 */:
                try {
                    AlarmModify alarmModify = new AlarmModify();
                    alarmModify.setType(4);
                    alarmModify.setTime(System.currentTimeMillis());
                    AlarmModifyHelper.insertAlarmModify(DeskClockApp.getAppDEContext(), alarmModify);
                    this.mInfoTv.setText(MonitorImpl.testReport(DeskClockApp.getAppDEContext()));
                } catch (Exception unused) {
                    return;
                }
                break;
            case R.id.reset /* 2131362655 */:
                MonitorHelper.reset();
                break;
            case R.id.show_alarms /* 2131362744 */:
                AlarmBackupHelper.getAlarmBackup(DeskClockApp.getAppDEContext());
                break;
            case R.id.show_modify /* 2131362745 */:
                AlarmModifyHelper.getAlarmModify(DeskClockApp.getAppDEContext());
                break;
        }
    }
}
