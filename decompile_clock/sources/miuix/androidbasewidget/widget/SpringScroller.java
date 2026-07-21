package miuix.androidbasewidget.widget;

import android.view.animation.AnimationUtils;

/* JADX INFO: loaded from: classes2.dex */
public class SpringScroller {
    private static final int HORIZONTAL = 1;
    private static final float MAX_DELTA_TIME = 0.016f;
    private static final float VALUE_THRESHOLD = 1.0f;
    private static final int VERTICAL = 2;
    private double mCurrX;
    private double mCurrY;
    private long mCurrentTime;
    private double mEndX;
    private double mEndY;
    private boolean mFinished = true;
    private boolean mLastStep;
    private int mOrientation;
    private double mOriginStartY;
    private SpringOperator mSpringOperator;
    private long mStartTime;
    private double mStartX;
    private double mStartY;
    private double mVelocity;

    public void startScroll(float f, float f2, float f3, float f4, float f5) {
        this.mFinished = false;
        this.mLastStep = false;
        this.mStartX = f;
        this.mEndX = f2;
        double d = f3;
        this.mStartY = d;
        this.mOriginStartY = d;
        this.mCurrY = (int) d;
        this.mEndY = f4;
        double d2 = f5;
        this.mVelocity = d2;
        if (Math.abs(d2) <= 5000.0d) {
            this.mSpringOperator = new SpringOperator(0.9f, 0.35f);
        } else {
            this.mSpringOperator = new SpringOperator(0.9f, 0.35f);
        }
        this.mOrientation = Math.abs(f4 - f3) > Math.abs(f2 - f) ? 2 : 1;
        this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
    }

    public boolean computeScrollOffset() {
        if (this.mSpringOperator == null || this.mFinished) {
            return false;
        }
        if (this.mLastStep) {
            this.mFinished = true;
            this.mCurrY = this.mEndY;
            this.mCurrX = this.mEndX;
            return true;
        }
        long jCurrentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis();
        this.mCurrentTime = jCurrentAnimationTimeMillis;
        float f = (jCurrentAnimationTimeMillis - this.mStartTime) / 1000.0f;
        float f2 = MAX_DELTA_TIME;
        float fMin = Math.min(f, MAX_DELTA_TIME);
        if (fMin != 0.0f) {
            f2 = fMin;
        }
        this.mStartTime = this.mCurrentTime;
        if (this.mOrientation == 2) {
            double dUpdateVelocity = this.mSpringOperator.updateVelocity(this.mVelocity, f2, this.mEndY, this.mStartY);
            double d = this.mStartY + (((double) f2) * dUpdateVelocity);
            this.mCurrY = d;
            this.mVelocity = dUpdateVelocity;
            if (isAtEquilibrium(d, this.mEndY)) {
                this.mLastStep = true;
            } else {
                this.mStartY = this.mCurrY;
            }
        } else {
            double dUpdateVelocity2 = this.mSpringOperator.updateVelocity(this.mVelocity, f2, this.mEndX, this.mStartX);
            double d2 = this.mStartX + (((double) f2) * dUpdateVelocity2);
            this.mCurrX = d2;
            this.mVelocity = dUpdateVelocity2;
            if (isAtEquilibrium(d2, this.mEndX)) {
                this.mLastStep = true;
            } else {
                this.mStartX = this.mCurrX;
            }
        }
        return true;
    }

    public boolean isAtEquilibrium(double d, double d2) {
        return Math.abs(d - d2) < 1.0d;
    }

    public final int getCurrX() {
        return (int) this.mCurrX;
    }

    public final int getStartX() {
        return (int) this.mStartX;
    }

    public final int getCurrY() {
        return (int) this.mCurrY;
    }

    public final boolean isFinished() {
        return this.mFinished;
    }

    public final void abortAnimation() {
        this.mFinished = true;
    }

    public void setFinalX(int i) {
        this.mEndX = i;
        this.mFinished = false;
    }

    public final int getFinalX() {
        return (int) this.mEndX;
    }
}
