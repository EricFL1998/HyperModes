package com.android.deskclock.alarm.lifepost;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import androidx.customview.widget.ViewDragHelper;
import com.android.deskclock.R;

/* JADX INFO: loaded from: classes.dex */
public class LifePostDragHelperLayout extends FrameLayout {
    private View mAlarmViewContainer;
    private boolean mIsMovable;
    private boolean mIsMoving;
    private View mLifePostBgContainer;
    private View mLifePostViewContainer;
    private ViewDragHelper mViewDragHelper;

    public LifePostDragHelperLayout(Context context) {
        this(context, null);
    }

    public LifePostDragHelperLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public LifePostDragHelperLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mIsMovable = true;
        this.mIsMoving = false;
    }

    public void setViewDragHelper(ViewDragHelper viewDragHelper) {
        this.mViewDragHelper = viewDragHelper;
    }

    public void setMovable(boolean z) {
        this.mIsMovable = z;
    }

    public boolean getMovable() {
        return this.mIsMovable;
    }

    public void setMoving(boolean z) {
        this.mIsMoving = z;
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (!this.mIsMoving || z) {
            super.onLayout(z, i, i2, i3, i4);
            int childCount = getChildCount();
            for (int i5 = 0; i5 < childCount; i5++) {
                View childAt = getChildAt(i5);
                if (childAt.getVisibility() != 8) {
                    int measuredWidth = childAt.getMeasuredWidth();
                    int measuredHeight = childAt.getMeasuredHeight();
                    if (childAt == this.mLifePostBgContainer) {
                        childAt.layout(i, i2, i3, i4);
                    } else if (this.mIsMovable) {
                        int i6 = measuredHeight + i2;
                        childAt.layout(i, i2, measuredWidth + i, i6);
                        i2 = i6;
                    } else {
                        if (childAt == this.mLifePostViewContainer) {
                            i2 = this.mAlarmViewContainer.getHeight() - this.mLifePostViewContainer.getHeight();
                        }
                        childAt.layout(i, i2, measuredWidth + i, measuredHeight + i2);
                    }
                }
            }
        }
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mLifePostBgContainer = findViewById(R.id.life_post_background);
        this.mAlarmViewContainer = findViewById(R.id.alert_content);
        this.mLifePostViewContainer = findViewById(R.id.life_post_container);
    }

    @Override // android.view.View
    public void computeScroll() {
        ViewDragHelper viewDragHelper;
        super.computeScroll();
        if (this.mIsMovable && (viewDragHelper = this.mViewDragHelper) != null && viewDragHelper.continueSettling(true)) {
            postInvalidateOnAnimation();
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        ViewDragHelper viewDragHelper = this.mViewDragHelper;
        if (viewDragHelper != null && this.mIsMovable) {
            viewDragHelper.processTouchEvent(motionEvent);
            return true;
        }
        return super.onTouchEvent(motionEvent);
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        ViewDragHelper viewDragHelper = this.mViewDragHelper;
        if (viewDragHelper != null && this.mIsMovable) {
            return viewDragHelper.shouldInterceptTouchEvent(motionEvent);
        }
        return super.onInterceptTouchEvent(motionEvent);
    }
}
