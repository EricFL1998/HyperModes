package com.android.deskclock.alarm;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.android.deskclock.Alarm;

/* JADX INFO: loaded from: classes.dex */
public abstract class AlarmController {
    private static final String TAG = "DC:AlarmController";
    private BackButtonClickListener mBackButtonClickListener;

    public interface BackButtonClickListener {
        void onButtonClick();
    }

    public abstract void initBundleData(Bundle bundle);

    public abstract void initData(Alarm alarm, Alarm alarm2, boolean z, boolean z2);

    public abstract void initOtherData();

    public void onDestroy() {
    }

    public void onPause() {
    }

    public void onResume() {
    }

    public void onSaveInstance(Bundle bundle) {
    }

    public AlarmController(Activity activity, View view) {
    }

    public void setBackButtonClickListener(BackButtonClickListener backButtonClickListener) {
        this.mBackButtonClickListener = backButtonClickListener;
    }

    protected void dismissDialog() {
        Log.d(TAG, "mBackButtonClickListener: " + this.mBackButtonClickListener);
        BackButtonClickListener backButtonClickListener = this.mBackButtonClickListener;
        if (backButtonClickListener != null) {
            backButtonClickListener.onButtonClick();
        }
    }
}
