package com.android.deskclock.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.deskclock.DeskClockTabActivity;
import com.android.deskclock.R;
import com.android.deskclock.TabNavigatorContentFragment;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.stat.StatHelper;
import java.lang.ref.WeakReference;
import miuix.appcompat.app.Fragment;

/* JADX INFO: loaded from: classes.dex */
public abstract class BaseClockFragment extends Fragment implements TabNavigatorContentFragment.IClockViews, TabNavigatorContentFragment.IFragmentChange, TabNavigatorContentFragment.IFabClick {
    protected DeskClockTabActivity mActivity;
    protected InitRunnable mInitRunnable;
    protected ViewGroup mRootView;
    protected boolean mStopped;
    protected boolean mInitialized = false;
    private long mDelayTime = 0;

    public void destroyActionMode() {
    }

    protected abstract String getTab();

    protected void initView() {
    }

    @Override // com.android.deskclock.TabNavigatorContentFragment.IFabClick
    public void onCenterClick(View view) {
    }

    public void onDataChanged() {
    }

    @Override // com.android.deskclock.TabNavigatorContentFragment.IFabClick
    public void onEndClick(View view) {
    }

    public void onEndClick2(View view) {
    }

    @Override // com.android.deskclock.TabNavigatorContentFragment.IFabClick
    public void onStartClick(View view) {
    }

    public void onTimeChanged() {
    }

    public void onTimeFormatChanged() {
    }

    public void onTimeTick() {
    }

    public void onTimezoneChanged() {
    }

    @Override // com.android.deskclock.TabNavigatorContentFragment.IClockViews
    public boolean shouldKeepScreenOn() {
        return false;
    }

    public void setTime(long j) {
        this.mDelayTime = j;
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        DeskClockTabActivity deskClockTabActivity = (DeskClockTabActivity) getActivity();
        this.mActivity = deskClockTabActivity;
        if (Util.isTinyScreen(deskClockTabActivity)) {
            setThemeRes(R.style.PageFragmentThemeTiny);
        } else {
            setThemeRes(R.style.PageFragmentTheme);
        }
    }

    @Override // miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return super.onInflateView(layoutInflater, viewGroup, bundle);
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        InitRunnable initRunnable = new InitRunnable(this);
        this.mInitRunnable = initRunnable;
        ViewGroup viewGroup = this.mRootView;
        if (viewGroup != null) {
            long j = this.mDelayTime;
            if (j == 0) {
                initView();
            } else {
                viewGroup.postDelayed(initRunnable, j);
            }
        }
    }

    public void onEnter() {
        if (!this.mInitialized) {
            initView();
        }
        StatHelper.recordPageStart(this);
    }

    public void onLeave() {
        StatHelper.recordPageEnd(this);
    }

    @Override // androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        this.mStopped = false;
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        this.mStopped = true;
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        ViewGroup viewGroup = this.mRootView;
        if (viewGroup != null) {
            viewGroup.removeCallbacks(this.mInitRunnable);
        }
    }

    public static class InitRunnable implements Runnable {
        private WeakReference<BaseClockFragment> mWeakReference;

        public InitRunnable(BaseClockFragment baseClockFragment) {
            this.mWeakReference = new WeakReference<>(baseClockFragment);
        }

        @Override // java.lang.Runnable
        public void run() {
            BaseClockFragment baseClockFragment = this.mWeakReference.get();
            if (baseClockFragment != null) {
                baseClockFragment.initView();
            }
        }
    }

    protected void setRootViewPadding(int i, int i2) {
        this.mRootView.setPadding(i, i2, i, 0);
    }
}
