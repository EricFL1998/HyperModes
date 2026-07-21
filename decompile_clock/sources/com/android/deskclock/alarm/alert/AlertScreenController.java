package com.android.deskclock.alarm.alert;

import android.content.Context;
import android.view.View;
import miuix.core.util.HyperMaterialUtils;

/* JADX INFO: loaded from: classes.dex */
public abstract class AlertScreenController {
    private AlertScreenListener mAlertScreenListener;
    protected Context mContext;
    protected String mLabelString;
    protected View mRootView;

    public interface AlertScreenListener {
        void onDismiss(boolean z);

        void onSnooze();
    }

    public abstract void init();

    public void onDestroy() {
    }

    public void onPause() {
    }

    public void onResume() {
    }

    public abstract void release();

    public AlertScreenController(Context context, View view, String str) {
        this.mContext = context;
        this.mRootView = view;
        this.mLabelString = str;
    }

    public void setAlertScreenListener(AlertScreenListener alertScreenListener) {
        this.mAlertScreenListener = alertScreenListener;
    }

    protected void dismiss(boolean z) {
        AlertScreenListener alertScreenListener = this.mAlertScreenListener;
        if (alertScreenListener != null) {
            alertScreenListener.onDismiss(z);
        }
    }

    protected void dismiss() {
        dismiss(false);
    }

    protected void snooze() {
        AlertScreenListener alertScreenListener = this.mAlertScreenListener;
        if (alertScreenListener != null) {
            alertScreenListener.onSnooze();
        }
    }

    public View getRootView() {
        return this.mRootView;
    }

    protected boolean isSupportHyperMaterial() {
        return HyperMaterialUtils.isEnable() && HyperMaterialUtils.isFeatureEnable(this.mContext);
    }
}
