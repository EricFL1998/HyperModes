package com.android.deskclock.alarm.bedtime;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import miuix.viewpager.widget.ViewPager;

/* JADX INFO: loaded from: classes.dex */
public class SettingGuideViewPager extends ViewPager {
    @Override // miuix.viewpager.widget.ViewPager, androidx.viewpager.widget.OriginalViewPager, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    @Override // miuix.viewpager.widget.ViewPager, androidx.viewpager.widget.OriginalViewPager, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    public SettingGuideViewPager(Context context) {
        super(context);
    }

    public SettingGuideViewPager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }
}
