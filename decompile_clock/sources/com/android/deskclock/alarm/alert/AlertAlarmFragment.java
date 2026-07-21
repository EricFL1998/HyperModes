package com.android.deskclock.alarm.alert;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.android.deskclock.Alarm;
import com.android.deskclock.R;
import com.android.deskclock.alarm.lifepost.AlarmLifePostScreenController;
import com.android.deskclock.alarm.lifepost.LifePostDragHelperLayout;
import com.android.deskclock.alarm.lifepost.LifePostUtils;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.Util;
import com.android.deskclock.view.ViewDragHelperLayout;

/* JADX INFO: loaded from: classes.dex */
public class AlertAlarmFragment extends AlertBaseFragment {
    private static final String TAG = "AlertAlarmFragment";
    private int mSnoozeMinutes;

    @Override // com.android.deskclock.alarm.alert.AlertBaseFragment
    public void initView() {
        super.initView();
        setupAlarmScreen(this.mAlarm);
    }

    public void resetLifePostLayout() {
        if (this.mAlertController == null || !(this.mAlertController instanceof AlarmLifePostScreenController)) {
            return;
        }
        ((AlarmLifePostScreenController) this.mAlertController).resetLifePostLayout();
    }

    private void setupAlarmScreen(Alarm alarm) {
        this.mViewRoot.removeAllViews();
        if (this.mAlertController != null) {
            this.mAlertController.release();
        }
        if (Util.atLeastU()) {
            this.mSnoozeMinutes = Util.getAlarmSpaceSnoozeMinutes(getAppCompatActivity()) == Integer.MIN_VALUE ? Util.getSnoozeMinutes(getAppCompatActivity()) : Util.getAlarmSpaceSnoozeMinutes(getAppCompatActivity());
        } else {
            this.mSnoozeMinutes = Util.getSnoozeMinutes(getAppCompatActivity());
        }
        addDefaultAlarmScreen(alarm);
    }

    public void showLifePost() {
        if (this.mAlertController instanceof AlarmLifePostScreenController) {
            ((AlarmLifePostScreenController) this.mAlertController).showLifePost();
        }
    }

    private void addDefaultAlarmScreen(Alarm alarm) {
        boolean zIsShowLifePost = LifePostUtils.isShowLifePost(getAppCompatActivity(), alarm);
        Log.d(TAG, "showLifePost:" + zIsShowLifePost);
        if (zIsShowLifePost) {
            this.mAlertController = new AlarmLifePostScreenController(getAppCompatActivity(), (LifePostDragHelperLayout) LayoutInflater.from(getAppCompatActivity()).inflate(R.layout.alarm_alert_life_post_screen, this.mViewRoot, false), alarm, this.mSnoozeMinutes, this);
        } else {
            this.mAlertController = new DefaultAlarmAlertScreenController(getAppCompatActivity(), (ViewDragHelperLayout) LayoutInflater.from(getAppCompatActivity()).inflate(R.layout.alarm_alert_screen_default, this.mViewRoot, false), alarm, this.mSnoozeMinutes);
        }
        this.mAlertController.setAlertScreenListener(this.mAlertScreenListener);
        this.mAlertController.init();
        this.mViewRoot.addView(this.mAlertController.getRootView(), new ViewGroup.LayoutParams(-1, -1));
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        if (this.mAlertController != null) {
            this.mAlertController.onResume();
        }
        if (this.mAlertController instanceof AlarmLifePostScreenController) {
            ((AlarmLifePostScreenController) this.mAlertController).startNewsRoundPlay();
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        if (this.mAlertController != null) {
            this.mAlertController.onDestroy();
        }
    }
}
