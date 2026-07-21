package com.android.deskclock.worldclock;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import miuix.core.widget.NestedScrollView;

/* JADX INFO: loaded from: classes.dex */
public class ClockNestedScrollView extends NestedScrollView {
    WorldClockEditActivity mActivity;

    public ClockNestedScrollView(Context context) {
        super(context);
    }

    public ClockNestedScrollView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public ClockNestedScrollView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void setActivity(WorldClockEditActivity worldClockEditActivity) {
        this.mActivity = worldClockEditActivity;
    }

    @Override // miuix.core.widget.NestedScrollView, android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override // miuix.core.widget.NestedScrollView, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (this.mActivity.isInActionMode()) {
            return false;
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    @Override // miuix.core.widget.NestedScrollView, android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (getChildCount() <= 0 || !this.mActivity.isInActionMode()) {
            return;
        }
        View childAt = getChildAt(0);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) childAt.getLayoutParams();
        childAt.measure(getChildMeasureSpec(i, getPaddingLeft() + getPaddingRight() + layoutParams.leftMargin + layoutParams.rightMargin, layoutParams.width), View.MeasureSpec.makeMeasureSpec((((getMeasuredHeight() - getPaddingTop()) - getPaddingBottom()) - layoutParams.topMargin) - layoutParams.bottomMargin, BasicMeasure.EXACTLY));
    }
}
