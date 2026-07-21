package com.android.deskclock.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import com.android.deskclock.view.drawable.SegmentDialProgressDrawable;

/* JADX INFO: loaded from: classes.dex */
public class DragCircleView extends View {
    public static int CIRCLE_STATE_PAUSE = 1;
    public static int CIRCLE_STATE_RUN = 0;
    public static int CIRCLE_STATE_STOP = 2;
    private SegmentDialProgressDrawable mCircleBackground;
    private int mCurrentDegree;
    private int mDragCircleState;
    private int mDrawnDegree;
    private long mEndTime;
    private int mHeight;
    private long mRemainedValue;
    private long mTotalValue;
    private int mWidth;

    public DragCircleView(Context context) {
        this(context, null);
    }

    public DragCircleView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mCurrentDegree = 360;
        this.mDragCircleState = CIRCLE_STATE_STOP;
        this.mDrawnDegree = -1;
        setLayerType(2, null);
    }

    public void initDrawableRes(SegmentDialProgressDrawable segmentDialProgressDrawable) {
        this.mCircleBackground = segmentDialProgressDrawable;
        this.mWidth = segmentDialProgressDrawable.getIntrinsicWidth();
        this.mHeight = this.mCircleBackground.getIntrinsicHeight();
        invalidateView();
    }

    public void setTotalValue(long j) {
        this.mTotalValue = j;
    }

    public void setRemainedValue(long j) {
        this.mRemainedValue = j;
    }

    public void setEndTime(long j) {
        if (j > 0) {
            this.mEndTime = j;
        }
        invalidateView();
    }

    public int getState() {
        return this.mDragCircleState;
    }

    public void setState(int i) {
        this.mDragCircleState = i;
        if (i == CIRCLE_STATE_STOP) {
            this.mCurrentDegree = 360;
            this.mTotalValue = 0L;
        }
        SegmentDialProgressDrawable segmentDialProgressDrawable = this.mCircleBackground;
        if (segmentDialProgressDrawable != null) {
            segmentDialProgressDrawable.setState(i == CIRCLE_STATE_RUN ? 0 : 1);
        }
        this.mDrawnDegree = -1;
        invalidateView();
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        int i3;
        int i4;
        int mode = View.MeasureSpec.getMode(i);
        int size = View.MeasureSpec.getSize(i);
        int mode2 = View.MeasureSpec.getMode(i2);
        int size2 = View.MeasureSpec.getSize(i2);
        float f = 1.0f;
        float f2 = (mode == 0 || size >= (i4 = this.mWidth)) ? 1.0f : size / i4;
        if (mode2 != 0 && size2 < (i3 = this.mHeight)) {
            f = size2 / i3;
        }
        float fMin = Math.min(f2, f);
        setMeasuredDimension(resolveSizeAndState((int) (this.mWidth * fMin), i, 0), resolveSizeAndState((int) (this.mHeight * fMin), i2, 0));
        SegmentDialProgressDrawable segmentDialProgressDrawable = this.mCircleBackground;
        if (segmentDialProgressDrawable != null) {
            segmentDialProgressDrawable.reSetIntrinsicSize(getMeasuredWidth(), getMeasuredWidth());
        }
    }

    /* JADX WARN: Code duplicated, block: B:12:0x0028  */
    /* JADX WARN: Code duplicated, block: B:22:0x003d  */
    public void invalidateView() {
        int i;
        float fCurrentTimeMillis;
        long j;
        int i2;
        SegmentDialProgressDrawable segmentDialProgressDrawable;
        int i3;
        int i4 = this.mDragCircleState;
        if (i4 == CIRCLE_STATE_RUN) {
            fCurrentTimeMillis = this.mEndTime - System.currentTimeMillis();
            j = this.mTotalValue;
        } else {
            if (i4 == CIRCLE_STATE_PAUSE) {
                fCurrentTimeMillis = this.mRemainedValue;
                j = this.mTotalValue;
            } else {
                i = 360;
            }
            this.mCurrentDegree = i;
            if (i < 0) {
                this.mCurrentDegree = 0;
            }
            i2 = this.mDrawnDegree;
            if (i2 != -1 || (i3 = this.mCurrentDegree) == 360 || i2 >= i3 + 3) {
                segmentDialProgressDrawable = this.mCircleBackground;
                if (segmentDialProgressDrawable != null) {
                    segmentDialProgressDrawable.setProgressDegree(this.mCurrentDegree);
                    this.mCircleBackground.invalidate();
                }
                invalidate();
            }
            return;
        }
        i = (int) ((fCurrentTimeMillis / j) * 360.0f);
        this.mCurrentDegree = i;
        if (i < 0) {
            this.mCurrentDegree = 0;
        }
        i2 = this.mDrawnDegree;
        if (i2 != -1) {
        }
        segmentDialProgressDrawable = this.mCircleBackground;
        if (segmentDialProgressDrawable != null) {
            segmentDialProgressDrawable.setProgressDegree(this.mCurrentDegree);
            this.mCircleBackground.invalidate();
        }
        invalidate();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        SegmentDialProgressDrawable segmentDialProgressDrawable = this.mCircleBackground;
        if (segmentDialProgressDrawable != null) {
            segmentDialProgressDrawable.draw(canvas);
            this.mDrawnDegree = this.mCurrentDegree;
        }
    }
}
