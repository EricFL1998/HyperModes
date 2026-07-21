package com.android.deskclock.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Scroller;
import java.lang.reflect.Field;
import miuix.viewpager.widget.ViewPager;

/* JADX INFO: loaded from: classes.dex */
public class DraggableViewPager extends ViewPager {
    private boolean mCanDrag;

    public DraggableViewPager(Context context) {
        super(context);
        this.mCanDrag = true;
    }

    public DraggableViewPager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mCanDrag = true;
    }

    private void init(Context context) {
        try {
            Field declaredField = ViewPager.class.getDeclaredField("mScroller");
            declaredField.setAccessible(true);
            ViewPagerScroller viewPagerScroller = new ViewPagerScroller(context, new DecelerateInterpolator());
            viewPagerScroller.setDuration(350);
            declaredField.set(this, viewPagerScroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // miuix.viewpager.widget.ViewPager
    public void setDraggable(boolean z) {
        this.mCanDrag = z;
    }

    @Override // miuix.viewpager.widget.ViewPager, androidx.viewpager.widget.OriginalViewPager, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        try {
            return this.mCanDrag && super.onInterceptTouchEvent(motionEvent);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override // miuix.viewpager.widget.ViewPager, androidx.viewpager.widget.OriginalViewPager, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        try {
            return this.mCanDrag && super.onTouchEvent(motionEvent);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static class ViewPagerScroller extends Scroller {
        private int mDuration;

        public ViewPagerScroller(Context context) {
            super(context);
            this.mDuration = 1000;
        }

        public ViewPagerScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
            this.mDuration = 1000;
        }

        public ViewPagerScroller(Context context, Interpolator interpolator, boolean z) {
            super(context, interpolator, z);
            this.mDuration = 1000;
        }

        @Override // android.widget.Scroller
        public void startScroll(int i, int i2, int i3, int i4, int i5) {
            super.startScroll(i, i2, i3, i4, this.mDuration);
        }

        @Override // android.widget.Scroller
        public void startScroll(int i, int i2, int i3, int i4) {
            super.startScroll(i, i2, i3, i4, this.mDuration);
        }

        public void setDuration(int i) {
            this.mDuration = i;
        }
    }
}
