package com.android.deskclock.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/* JADX INFO: loaded from: classes.dex */
public class DispatchRelativeLayout extends RelativeLayout {
    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return true;
    }

    public DispatchRelativeLayout(Context context) {
        super(context);
    }

    public DispatchRelativeLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public DispatchRelativeLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public DispatchRelativeLayout(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }
}
