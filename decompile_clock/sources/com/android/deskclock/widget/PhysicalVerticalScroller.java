package com.android.deskclock.widget;

import android.content.Context;
import android.widget.OverScroller;

/* JADX INFO: loaded from: classes.dex */
public class PhysicalVerticalScroller extends OverScroller {
    private static final double MIN_VISIBLE_VELOCITY = 45.0d;
    private static final String TAG = "PhysicalScroller";
    private int endPos;
    private PhysicalScrollerHelper mHelper;
    private int mSelectorElementHeight;
    private int startPos;

    public PhysicalVerticalScroller(Context context) {
        super(context);
        this.mHelper = new PhysicalScrollerHelper();
    }

    public PhysicalVerticalScroller(Context context, PhysicalScrollerHelper physicalScrollerHelper) {
        super(context);
        this.mHelper = physicalScrollerHelper;
    }

    public void setElementHeight(int i) {
        this.mSelectorElementHeight = i;
    }

    @Override // android.widget.OverScroller
    public void startScroll(int i, int i2, int i3, int i4) {
        this.startPos = i2;
        int i5 = i4 + i2;
        this.endPos = i5;
        this.mHelper.startAnim(i2, MIN_VISIBLE_VELOCITY, i5, i5, i5);
    }

    @Override // android.widget.OverScroller
    public void startScroll(int i, int i2, int i3, int i4, int i5) {
        this.startPos = i2;
        int i6 = i2 + i4;
        this.endPos = i6;
        this.mHelper.startAnim(i2, MIN_VISIBLE_VELOCITY, i6, i6, i6);
    }

    @Override // android.widget.OverScroller
    public void fling(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        this.startPos = i2;
        double d = i2;
        double d2 = i4;
        int iAdjustEndScrollY = adjustEndScrollY(0, (int) PhysicalScrollerHelper.getEndPosition(d, d2));
        this.endPos = iAdjustEndScrollY;
        this.mHelper.startAnim(d, d2, iAdjustEndScrollY, i7, i8);
    }

    public void fling(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9) {
        this.startPos = i3;
        double d = i3;
        double d2 = i5;
        int iAdjustEndScrollY = adjustEndScrollY(i, (int) PhysicalScrollerHelper.getEndPosition(d, d2));
        this.endPos = iAdjustEndScrollY;
        this.mHelper.startAnim(d, d2, iAdjustEndScrollY, i8, i9);
    }

    public void startScroll(int i, int i2, String str) {
        this.startPos = i;
        int i3 = i2 + i;
        this.endPos = i3;
        this.mHelper.startAnim(i, i3, str);
    }

    @Override // android.widget.OverScroller
    public boolean computeScrollOffset() {
        return this.mHelper.computeScrollOffset();
    }

    public boolean isScrollFinished() {
        return this.mHelper.isFinish();
    }

    public void forceScrollFinished(boolean z) {
        this.mHelper.stopScroll();
    }

    public int getStartYPosition() {
        return this.startPos;
    }

    public int getFinalYPosition() {
        return this.endPos;
    }

    public int getCurrentYPosition() {
        return (int) this.mHelper.getPosition();
    }

    private int adjustEndScrollY(int i, int i2) {
        int i3 = this.mSelectorElementHeight;
        if (i3 == 0) {
            return 0;
        }
        return (i3 * (i2 / i3)) + i;
    }
}
