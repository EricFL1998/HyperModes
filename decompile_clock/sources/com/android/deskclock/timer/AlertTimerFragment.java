package com.android.deskclock.timer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.deskclock.Alarm;
import com.android.deskclock.R;
import com.android.deskclock.alarm.alert.AlertBaseFragment;

/* JADX INFO: loaded from: classes.dex */
public class AlertTimerFragment extends AlertBaseFragment {
    @Override // com.android.deskclock.alarm.alert.AlertBaseFragment
    public void initView() {
        super.initView();
        showTimerAlertScreen(this.mAlarm);
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        if (this.mAlertController != null) {
            this.mAlertController.release();
        }
    }

    private void showTimerAlertScreen(Alarm alarm) {
        if (this.mAlertController != null) {
            this.mAlertController.release();
            this.mViewRoot.removeView(this.mAlertController.getRootView());
        }
        View viewInflate = LayoutInflater.from(getActivity()).inflate(R.layout.timer_alert_screen, this.mViewRoot, false);
        this.mAlertController = new TimerAlertScreenController(getActivity(), viewInflate, alarm);
        this.mAlertController.setAlertScreenListener(this.mAlertScreenListener);
        this.mAlertController.init();
        this.mViewRoot.addView(viewInflate, new ViewGroup.LayoutParams(-1, -1));
    }
}
