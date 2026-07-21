package com.android.deskclock.alarm.alert;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.android.deskclock.Alarm;
import miuix.appcompat.app.Fragment;

/* JADX INFO: loaded from: classes.dex */
public abstract class AlertBaseFragment extends Fragment {
    public static final String ALARM = "alarm";
    protected Alarm mAlarm;
    protected AlertScreenController mAlertController;
    protected AlertScreenController.AlertScreenListener mAlertScreenListener;
    protected ViewGroup mViewRoot;

    @Override // miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mAlertScreenListener = (AlertScreenController.AlertScreenListener) getAppCompatActivity();
        this.mViewRoot = new FrameLayout(getAppCompatActivity());
        initView();
        return this.mViewRoot;
    }

    public void initView() {
        initData();
    }

    protected void initData() {
        this.mAlarm = (Alarm) getArguments().getParcelable("alarm");
    }
}
