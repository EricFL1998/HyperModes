package com.android.deskclock.view.drawable;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import com.android.deskclock.R;
import com.android.deskclock.util.Util;

/* JADX INFO: loaded from: classes.dex */
public class SegmentDialProgressDrawable {
    private static final int DEFAULT_SEGMENT_COLOR = -1;
    private static final int DEFAULT_SEGMENT_COUNT = 120;
    private static final float DEFAULT_SEGMENT_DEGREE = 360.0f;
    public static final int STATE_PAUSE = 1;
    public static final int STATE_RUNNING = 0;
    private int mArcCount;
    private Paint mArcPaint;
    protected Path mArcPath;
    private int mBackgroundColor;
    private Paint mBackgroundPaint;
    protected float mDegreePerSegment;
    protected int mIntrinsicHeight;
    protected int mIntrinsicWidth;
    private float mLastCount;
    private Paint mMiddlePaint;
    protected Path mMiddlePath;
    protected Path mPath;
    private Matrix mPathMatrix;
    private int mPauseColor;
    private Paint mPausePaint;
    private boolean mResetCircle;
    private int mSegmentColor;
    protected int mSegmentLength;
    protected int mSegmentWidth;
    private int mSegmentsCount;
    private int mState;

    /* JADX WARN: Illegal instructions before constructor call */
    public SegmentDialProgressDrawable(Context context) {
        Resources resources;
        int i;
        if (Util.isTinyScreen(context)) {
            resources = context.getResources();
            i = R.dimen.segments_dial_length_stopwatch_tiny;
        } else {
            resources = context.getResources();
            i = R.dimen.segments_dial_length_stopwatch;
        }
        this(resources.getDimension(i), context.getResources().getDimension(R.dimen.segments_dial_width));
    }

    public SegmentDialProgressDrawable(float f, float f2) {
        this.mSegmentColor = -1;
        this.mSegmentsCount = 120;
        this.mPathMatrix = new Matrix();
        this.mState = 0;
        this.mSegmentLength = Math.round(f);
        this.mSegmentWidth = Math.round(f2);
        this.mPath = new Path();
        this.mArcPath = new Path();
        this.mMiddlePath = new Path();
        this.mDegreePerSegment = 360.0f / this.mSegmentsCount;
        Paint paint = new Paint();
        this.mPausePaint = paint;
        paint.setAntiAlias(true);
        this.mPausePaint.setColor(this.mSegmentColor);
        Paint paint2 = new Paint();
        this.mArcPaint = paint2;
        paint2.setAntiAlias(true);
        this.mArcPaint.setColor(this.mSegmentColor);
        Paint paint3 = new Paint();
        this.mMiddlePaint = paint3;
        paint3.setAntiAlias(true);
        this.mMiddlePaint.setColor(this.mSegmentColor);
        Paint paint4 = new Paint();
        this.mBackgroundPaint = paint4;
        paint4.setAntiAlias(true);
        this.mBackgroundPaint.setColor(this.mSegmentColor);
    }

    public void setProgressDegree(float f) {
        int i = (int) (f / this.mDegreePerSegment);
        if (this.mArcCount != i) {
            this.mArcCount = i;
        }
    }

    public void setSegmentColor(int i, int i2, int i3) {
        this.mSegmentColor = i;
        this.mBackgroundColor = i2;
        this.mPauseColor = i3;
        this.mArcPaint.setColor(i2);
        this.mMiddlePaint.setColor(this.mSegmentColor);
        this.mBackgroundPaint.setColor(this.mSegmentColor);
        this.mPausePaint.setColor(this.mPauseColor);
    }

    protected void addRect1(Path path, float f) {
        if (f != this.mLastCount || this.mResetCircle) {
            path.reset();
            this.mPathMatrix.reset();
            for (int i = 1; i <= f; i++) {
                this.mPathMatrix.setRotate(this.mDegreePerSegment, this.mIntrinsicWidth / 2, this.mIntrinsicHeight / 2);
                path.transform(this.mPathMatrix);
                int i2 = this.mIntrinsicWidth;
                int i3 = this.mSegmentWidth;
                path.addRect((i2 - i3) / 2, 0.0f, (i2 + i3) / 2, this.mSegmentLength, Path.Direction.CCW);
            }
            this.mLastCount = f;
            path.close();
        }
    }

    protected void addRect2(Path path, float f) {
        path.reset();
        this.mPathMatrix.reset();
        for (int i = 1; i <= f; i++) {
            this.mPathMatrix.setRotate(-this.mDegreePerSegment, this.mIntrinsicWidth / 2, this.mIntrinsicHeight / 2);
            path.transform(this.mPathMatrix);
            int i2 = this.mIntrinsicWidth;
            int i3 = this.mSegmentWidth;
            path.addRect((i2 - i3) / 2, 0.0f, (i2 + i3) / 2, this.mSegmentLength, Path.Direction.CCW);
        }
        this.mPathMatrix.setRotate(-this.mDegreePerSegment, this.mIntrinsicWidth / 2, this.mIntrinsicHeight / 2);
        path.transform(this.mPathMatrix);
        path.close();
    }

    protected void addRect3(Path path, float f) {
        path.reset();
        this.mPathMatrix.reset();
        int i = this.mIntrinsicWidth;
        int i2 = this.mSegmentWidth;
        path.addRect((i - i2) / 2, 0.0f, (i + i2) / 2, this.mSegmentLength, Path.Direction.CCW);
        this.mPathMatrix.setRotate((-this.mDegreePerSegment) * f, this.mIntrinsicWidth / 2, this.mIntrinsicHeight / 2);
        path.transform(this.mPathMatrix);
        path.close();
    }

    public void invalidate() {
        float f = this.mSegmentsCount - this.mArcCount;
        addRect2(this.mArcPath, f);
        if (this.mState == 0) {
            addRect3(this.mMiddlePath, f);
            addRect1(this.mPath, this.mArcCount);
        } else {
            addRect1(this.mPath, this.mArcCount);
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawPath(this.mArcPath, this.mArcPaint);
        if (this.mState == 0) {
            canvas.drawPath(this.mMiddlePath, this.mMiddlePaint);
            canvas.drawPath(this.mPath, this.mBackgroundPaint);
        } else {
            canvas.drawPath(this.mPath, this.mPausePaint);
        }
    }

    public void setIntrinsicSize(float f, float f2) {
        this.mIntrinsicWidth = Math.round(f);
        this.mIntrinsicHeight = Math.round(f2);
        invalidate();
    }

    public void reSetIntrinsicSize(float f, float f2) {
        int i = this.mIntrinsicWidth;
        int i2 = this.mIntrinsicHeight;
        this.mIntrinsicWidth = Math.round(f);
        int iRound = Math.round(f2);
        this.mIntrinsicHeight = iRound;
        if (i != this.mIntrinsicWidth || i2 != iRound) {
            this.mResetCircle = true;
            invalidate();
        }
        this.mResetCircle = false;
    }

    public int getIntrinsicHeight() {
        return this.mIntrinsicHeight;
    }

    public int getIntrinsicWidth() {
        return this.mIntrinsicWidth;
    }

    public void setState(int i) {
        this.mState = i;
    }
}
