package com.android.deskclock.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;

/* JADX INFO: loaded from: classes.dex */
public class FontLinearLayout extends LinearLayout {
    private boolean mIsDragging;
    private OnFontLinearLayoutClickListener mOnFontLinearLayoutClickListener;
    private float mScale;
    private int mScaledTouchSlop;
    private float mTouchDownX;
    private float mTouchUpX;

    public interface OnFontLinearLayoutClickListener {
        void onStopTrackingTouch(FontLinearLayout fontLinearLayout, float f);
    }

    public FontLinearLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        OnFontLinearLayoutClickListener onFontLinearLayoutClickListener;
        int action = motionEvent.getAction();
        if (action == 0) {
            this.mTouchDownX = motionEvent.getX();
            onStartTrackingTouch();
        } else if (action == 1 && this.mIsDragging) {
            this.mTouchUpX = motionEvent.getX();
            onStopTrackingTouch();
            if (trackTouchEvent(motionEvent) && (onFontLinearLayoutClickListener = this.mOnFontLinearLayoutClickListener) != null) {
                onFontLinearLayoutClickListener.onStopTrackingTouch(this, this.mScale);
            }
        }
        return true;
    }

    private boolean trackTouchEvent(MotionEvent motionEvent) {
        if (Math.abs(this.mTouchUpX - this.mTouchDownX) >= this.mScaledTouchSlop) {
            return false;
        }
        int width = getWidth();
        int paddingLeft = (width - getPaddingLeft()) - getPaddingRight();
        int x = (int) motionEvent.getX();
        if (x < getPaddingLeft()) {
            this.mScale = 0.0f;
            return true;
        }
        if (x > width - getPaddingRight()) {
            this.mScale = 1.0f;
            return true;
        }
        this.mScale = (x - getPaddingLeft()) / paddingLeft;
        return true;
    }

    void onStartTrackingTouch() {
        this.mIsDragging = true;
    }

    void onStopTrackingTouch() {
        this.mIsDragging = false;
    }

    public void setOnFontLinearLayoutClickListener(OnFontLinearLayoutClickListener onFontLinearLayoutClickListener) {
        this.mOnFontLinearLayoutClickListener = onFontLinearLayoutClickListener;
    }
}
