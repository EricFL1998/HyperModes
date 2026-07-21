package miuix.springback.view;

import miuix.view.animation.AnimationUtils;

/* JADX INFO: loaded from: classes3.dex */
public class SpringOverScroller {
    private static final float FRICTION = 8.0f;
    private static final float MAX_DELTA_TIME = 0.016f;
    private static final float VALUE_THRESHOLD = 1.0f;
    private static final float VELOCITY_THRESHOLD = 2000.0f;
    private double mCurrX;
    private double mCurrY;
    private long mCurrentTimeNanos;
    private boolean mFinished = true;
    private int mFirstStep;
    private boolean mLastStep;
    private int mOrientation;
    private SpringOperator mSpringOperator;
    private long mStartTimeNanos;
    private double mStartX;
    private double mStartY;
    private double mVelocity;

    public void startOverScroll(float f, float f2, float f3, int i) {
        this.mFinished = false;
        this.mLastStep = false;
        this.mStartX = f;
        double d = f2;
        this.mStartY = d;
        this.mCurrY = (int) d;
        double d2 = f3;
        this.mVelocity = d2;
        if (Math.abs(d2) <= 5000.0d) {
            this.mSpringOperator = new SpringOperator(1.0f, 0.4f);
        } else {
            this.mSpringOperator = new SpringOperator(1.0f, 0.55f);
        }
        this.mOrientation = i;
        this.mStartTimeNanos = AnimationUtils.currentAnimationTimeNanos();
    }

    public boolean computeScrollOffset() {
        if (this.mSpringOperator == null || this.mFinished) {
            return false;
        }
        int i = this.mFirstStep;
        if (i != 0) {
            if (this.mOrientation == 1) {
                this.mCurrX = i;
                this.mStartX = i;
            } else {
                this.mCurrY = i;
                this.mStartY = i;
            }
            this.mFirstStep = 0;
            return true;
        }
        if (this.mLastStep) {
            this.mFinished = true;
            return true;
        }
        long jCurrentAnimationTimeNanos = AnimationUtils.currentAnimationTimeNanos();
        this.mCurrentTimeNanos = jCurrentAnimationTimeNanos;
        double dMin = Math.min((jCurrentAnimationTimeNanos - this.mStartTimeNanos) / 1.0E9d, 0.01600000075995922d);
        double d = dMin != 0.0d ? dMin : 0.01600000075995922d;
        this.mStartTimeNanos = this.mCurrentTimeNanos;
        if (this.mOrientation == 2) {
            double dComputeCurrentVelocity = computeCurrentVelocity((float) this.mVelocity, (float) d);
            this.mCurrY = this.mStartY + (d * dComputeCurrentVelocity);
            this.mVelocity = dComputeCurrentVelocity;
            if (Math.abs(dComputeCurrentVelocity) <= 2000.0d) {
                this.mLastStep = true;
            } else {
                this.mStartY = this.mCurrY;
            }
        } else {
            double dComputeCurrentVelocity2 = computeCurrentVelocity((float) this.mVelocity, (float) d);
            this.mCurrX = this.mStartX + (d * dComputeCurrentVelocity2);
            this.mVelocity = dComputeCurrentVelocity2;
            if (Math.abs(dComputeCurrentVelocity2) <= 2000.0d) {
                this.mLastStep = true;
            } else {
                this.mStartX = this.mCurrX;
            }
        }
        return true;
    }

    private float computeCurrentVelocity(float f, float f2) {
        int i = f > 0.0f ? 1 : -1;
        float fAbs = Math.abs(f);
        float fMax = Math.max(0.0f, 1.0f - (f2 * FRICTION)) * fAbs;
        if (Math.abs(fMax) > 100.0f) {
            fMax = fAbs * (1.0f - ((((fAbs / 5000.0f) + 1.0f) * FRICTION) * f2));
        }
        return i * Math.max(0.0f, fMax);
    }

    public final int getCurrX() {
        return (int) this.mCurrX;
    }

    public final int getCurrY() {
        return (int) this.mCurrY;
    }

    public final double getVelocity() {
        return this.mVelocity;
    }

    public final int getScrollOrientation() {
        return this.mOrientation;
    }

    public final boolean isFinished() {
        return this.mFinished;
    }

    public final void forceStop() {
        this.mFinished = true;
        this.mFirstStep = 0;
    }

    public void setFirstStep(int i) {
        this.mFirstStep = i;
    }
}
