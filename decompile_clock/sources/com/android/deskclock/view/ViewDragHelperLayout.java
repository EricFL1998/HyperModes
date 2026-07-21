package com.android.deskclock.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import androidx.customview.widget.ViewDragHelper;

/* JADX INFO: loaded from: classes.dex */
public class ViewDragHelperLayout extends FrameLayout {
    private ViewDragHelper mViewDragHelper;

    public ViewDragHelperLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public ViewDragHelperLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void setViewDragHelper(ViewDragHelper viewDragHelper) {
        this.mViewDragHelper = viewDragHelper;
    }

    @Override // android.view.View
    public void computeScroll() {
        super.computeScroll();
        ViewDragHelper viewDragHelper = this.mViewDragHelper;
        if (viewDragHelper == null || !viewDragHelper.continueSettling(true)) {
            return;
        }
        postInvalidateOnAnimation();
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        try {
            ViewDragHelper viewDragHelper = this.mViewDragHelper;
            if (viewDragHelper != null) {
                viewDragHelper.processTouchEvent(motionEvent);
                return true;
            }
            return super.onTouchEvent(motionEvent);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        try {
            ViewDragHelper viewDragHelper = this.mViewDragHelper;
            if (viewDragHelper != null) {
                return viewDragHelper.shouldInterceptTouchEvent(motionEvent);
            }
            return super.onInterceptTouchEvent(motionEvent);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (z) {
            super.onLayout(z, i, i2, i3, i4);
        }
    }
}
