package miuix.overscroller.widget;

import android.content.Context;
import android.util.Log;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;
import miuix.animation.internal.AnimData;
import miuix.animation.internal.FolmeCore;
import miuix.animation.physics.SpringOperator;
import miuix.view.animation.AnimationUtils;

/* JADX INFO: loaded from: classes3.dex */
public class OverScroller {
    private static final int DEFAULT_DURATION = 250;
    public static final int FLING_MODE = 1;
    private static boolean SCROLL_BOOST_SS_ENABLE = false;
    public static final int SCROLL_BY_FLING_MODE = 2;
    public static final int SCROLL_MODE = 0;
    private final boolean mFlywheel;
    private Interpolator mInterpolator;
    private int mMode;
    private final SplineOverScroller mScrollerX;
    private final SplineOverScroller mScrollerY;

    public OverScroller(Context context) {
        this(context, null);
    }

    public OverScroller(Context context, Interpolator interpolator) {
        this(context, interpolator, true);
    }

    public OverScroller(Context context, Interpolator interpolator, boolean z) {
        if (interpolator == null) {
            this.mInterpolator = new ViscousFluidInterpolator();
        } else {
            this.mInterpolator = interpolator;
        }
        this.mFlywheel = z;
        this.mScrollerX = new DynamicScroller(context);
        this.mScrollerY = new DynamicScroller(context);
    }

    public int getMode() {
        return this.mMode;
    }

    @Deprecated
    public OverScroller(Context context, Interpolator interpolator, float f, float f2) {
        this(context, interpolator, true);
    }

    @Deprecated
    public OverScroller(Context context, Interpolator interpolator, float f, float f2, boolean z) {
        this(context, interpolator, z);
    }

    void setInterpolator(Interpolator interpolator) {
        if (interpolator == null) {
            this.mInterpolator = new ViscousFluidInterpolator();
        } else {
            this.mInterpolator = interpolator;
        }
    }

    public final void setFriction(float f) {
        this.mScrollerX.setFriction(f);
        this.mScrollerY.setFriction(f);
    }

    public final boolean isFinished() {
        return this.mScrollerX.mFinished && this.mScrollerY.mFinished;
    }

    public final void forceFinished(boolean z) {
        this.mScrollerX.mFinished = this.mScrollerY.mFinished = z;
    }

    public final void resetPosition() {
        resetScrollerPosition(this.mScrollerX);
        resetScrollerPosition(this.mScrollerY);
    }

    private void resetScrollerPosition(SplineOverScroller splineOverScroller) {
        splineOverScroller.setStart(0);
        splineOverScroller.setFinal(0);
        splineOverScroller.setCurrentPosition(0);
    }

    public final int getCurrX() {
        return (int) this.mScrollerX.mCurrentPosition;
    }

    public final int getCurrY() {
        return (int) this.mScrollerY.mCurrentPosition;
    }

    public float getCurrVelocity() {
        return (float) Math.hypot(this.mScrollerX.mCurrVelocity, this.mScrollerY.mCurrVelocity);
    }

    public float getCurrVelocityX() {
        return (float) this.mScrollerX.mCurrVelocity;
    }

    public float getCurrVelocityY() {
        return (float) this.mScrollerY.mCurrVelocity;
    }

    public final int getStartX() {
        return (int) this.mScrollerX.mStart;
    }

    public final int getStartY() {
        return (int) this.mScrollerY.mStart;
    }

    public final int getFinalX() {
        return (int) this.mScrollerX.mFinal;
    }

    public final int getFinalY() {
        return (int) this.mScrollerY.mFinal;
    }

    @Deprecated
    public final int getDuration() {
        return Math.max(this.mScrollerX.mDuration, this.mScrollerY.mDuration);
    }

    @Deprecated
    public void extendDuration(int i) {
        this.mScrollerX.extendDuration(i);
        this.mScrollerY.extendDuration(i);
    }

    @Deprecated
    public void setFinalX(int i) {
        this.mScrollerX.setFinalPosition(i);
    }

    @Deprecated
    public void setFinalY(int i) {
        this.mScrollerY.setFinalPosition(i);
    }

    public boolean computeScrollOffset() {
        if (isFinished()) {
            return false;
        }
        int i = this.mMode;
        if (i == 0) {
            long jCurrentAnimationTimeNanos = AnimationUtils.currentAnimationTimeNanos() - this.mScrollerX.mStartTime;
            int i2 = this.mScrollerX.mDuration;
            if (jCurrentAnimationTimeNanos < i2) {
                float interpolation = this.mInterpolator.getInterpolation(jCurrentAnimationTimeNanos / i2);
                this.mScrollerX.updateScroll(interpolation);
                this.mScrollerY.updateScroll(interpolation);
            } else {
                abortAnimation();
            }
        } else if (i == 1) {
            if (!this.mScrollerX.mFinished && !this.mScrollerX.update() && !this.mScrollerX.continueWhenFinished()) {
                this.mScrollerX.finish();
            }
            if (!this.mScrollerY.mFinished && !this.mScrollerY.update() && !this.mScrollerY.continueWhenFinished()) {
                this.mScrollerY.finish();
            }
        } else if (i == 2) {
            return this.mScrollerY.computeScrollOffset() || this.mScrollerX.computeScrollOffset();
        }
        return true;
    }

    public void startScrollByFling(int i, int i2, int i3, int i4) {
        startScrollByFling(i, i2, i3, i4, 0, 0);
    }

    public void startScrollByFling(int i, int i2, int i3, int i4, int i5, int i6) {
        this.mMode = 2;
        this.mScrollerX.startScrollByFling(i, i3, i5);
        this.mScrollerY.startScrollByFling(i2, i4, i6);
    }

    public void startScroll(int i, int i2, int i3, int i4) {
        startScroll(i, i2, i3, i4, 250);
    }

    public void startScroll(int i, int i2, int i3, int i4, int i5) {
        this.mMode = 0;
        this.mScrollerX.startScroll(i, i3, i5);
        this.mScrollerY.startScroll(i2, i4, i5);
    }

    public boolean springBack(int i, int i2, int i3, int i4, int i5, int i6) {
        this.mMode = 1;
        return this.mScrollerX.springback(i, i3, i4) || this.mScrollerY.springback(i2, i5, i6);
    }

    public void fling(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        fling(i, i2, i3, i4, i5, i6, i7, i8, 0, 0);
    }

    public void fling(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10) {
        int i11;
        int i12;
        int i13;
        int i14;
        if (this.mFlywheel && !isFinished()) {
            float f = (float) this.mScrollerX.mCurrVelocity;
            float f2 = (float) this.mScrollerY.mCurrVelocity;
            i11 = i3;
            float f3 = i11;
            if (Math.signum(f3) == Math.signum(f)) {
                i12 = i4;
                float f4 = i12;
                if (Math.signum(f4) == Math.signum(f2)) {
                    i13 = (int) (f4 + f2);
                    i14 = (int) (f3 + f);
                }
                this.mMode = 1;
                this.mScrollerX.fling(i, i14, i5, i6, i9);
                this.mScrollerY.fling(i2, i13, i7, i8, i10);
            }
            i13 = i12;
            i14 = i11;
            this.mMode = 1;
            this.mScrollerX.fling(i, i14, i5, i6, i9);
            this.mScrollerY.fling(i2, i13, i7, i8, i10);
        }
        i11 = i3;
        i12 = i4;
        i13 = i12;
        i14 = i11;
        this.mMode = 1;
        this.mScrollerX.fling(i, i14, i5, i6, i9);
        this.mScrollerY.fling(i2, i13, i7, i8, i10);
    }

    public void notifyHorizontalEdgeReached(int i, int i2, int i3) {
        this.mScrollerX.notifyEdgeReached(i, i2, i3);
    }

    public void notifyVerticalEdgeReached(int i, int i2, int i3) {
        this.mScrollerY.notifyEdgeReached(i, i2, i3);
    }

    public boolean isOverScrolled() {
        return ((this.mScrollerX.mFinished || this.mScrollerX.mState == 0) && (this.mScrollerY.mFinished || this.mScrollerY.mState == 0)) ? false : true;
    }

    public void abortAnimation() {
        this.mScrollerX.finish();
        this.mScrollerY.finish();
    }

    public int timePassed() {
        return (int) (AnimationUtils.currentAnimationTimeNanos() - Math.min(this.mScrollerX.mStartTime, this.mScrollerY.mStartTime));
    }

    public boolean isScrollingInDirection(float f, float f2) {
        return !isFinished() && Math.signum(f) == Math.signum((float) (((int) this.mScrollerX.mFinal) - ((int) this.mScrollerX.mStart))) && Math.signum(f2) == Math.signum((float) (((int) this.mScrollerY.mFinal) - ((int) this.mScrollerY.mStart)));
    }

    static class SplineOverScroller {
        private static final int BALLISTIC = 2;
        private static final int CUBIC = 1;
        private static final float END_TENSION = 1.0f;
        private static final float GRAVITY = 2000.0f;
        private static final float INFLEXION = 0.35f;
        private static final float MAX_DELTA_TIME = 0.016f;
        private static final int NB_SAMPLES = 100;
        private static final float P1 = 0.175f;
        private static final float P2 = 0.35000002f;
        private static final int SPLINE = 0;
        private static final float START_TENSION = 0.5f;
        static final int STATE_BALLISTIC = 2;
        static final int STATE_CUBIC = 1;
        static final int STATE_SPLINE = 0;
        private static final float VALUE_THRESHOLD = 1.0f;
        private Context mContext;
        private double mCurrVelocity;
        private double mCurrentPosition;
        private float mDeceleration;
        private int mDuration;
        private double mFinal;
        private boolean mLastStep;
        private double mOriginStart;
        private int mOver;
        private float mPhysicalCoeff;
        private int mSplineDistance;
        private int mSplineDuration;
        private AnimData mSpringAnimData;
        private double[] mSpringFactor;
        private SpringOperator mSpringOperator;
        private double[] mSpringParams;
        private double mStart;
        private long mStartTime;
        private double mVelocity;
        private static float DECELERATION_RATE = (float) (Math.log(0.78d) / Math.log(0.9d));
        private static final float[] SPLINE_POSITION = new float[101];
        private static final float[] SPLINE_TIME = new float[101];
        private float mFlingFriction = ViewConfiguration.getScrollFriction();
        private int mState = 0;
        private boolean mFinished = true;

        private static float getDeceleration(int i) {
            if (i > 0) {
                return -2000.0f;
            }
            return GRAVITY;
        }

        static {
            float f;
            float f2;
            float f3;
            float f4;
            float f5;
            float f6;
            float f7;
            float f8;
            float f9;
            float f10;
            float f11 = 0.0f;
            float f12 = 0.0f;
            for (int i = 0; i < 100; i++) {
                float f13 = i / 100.0f;
                float f14 = 1.0f;
                while (true) {
                    f = 2.0f;
                    f2 = ((f14 - f11) / 2.0f) + f11;
                    f3 = 3.0f;
                    f4 = 1.0f - f2;
                    f5 = f2 * 3.0f * f4;
                    f6 = f2 * f2 * f2;
                    float f15 = (((f4 * P1) + (f2 * P2)) * f5) + f6;
                    if (Math.abs(f15 - f13) < 1.0E-5d) {
                        break;
                    } else if (f15 > f13) {
                        f14 = f2;
                    } else {
                        f11 = f2;
                    }
                }
                SPLINE_POSITION[i] = (f5 * ((f4 * 0.5f) + f2)) + f6;
                float f16 = 1.0f;
                while (true) {
                    f7 = ((f16 - f12) / f) + f12;
                    f8 = 1.0f - f7;
                    f9 = f7 * f3 * f8;
                    f10 = f7 * f7 * f7;
                    float f17 = (((f8 * 0.5f) + f7) * f9) + f10;
                    if (Math.abs(f17 - f13) < 1.0E-5d) {
                        break;
                    }
                    if (f17 > f13) {
                        f16 = f7;
                    } else {
                        f12 = f7;
                    }
                    f = 2.0f;
                    f3 = 3.0f;
                }
                SPLINE_TIME[i] = (f9 * ((f8 * P1) + (f7 * P2))) + f10;
            }
            float[] fArr = SPLINE_POSITION;
            SPLINE_TIME[100] = 1.0f;
            fArr[100] = 1.0f;
        }

        void setFriction(float f) {
            this.mFlingFriction = f;
        }

        SplineOverScroller(Context context) {
            this.mContext = context;
            this.mPhysicalCoeff = context.getResources().getDisplayMetrics().density * 160.0f * 386.0878f * 0.84f;
        }

        void updateScroll(float f) {
            double d = this.mStart;
            this.mCurrentPosition = d + Math.round(((double) f) * (this.mFinal - d));
        }

        private void adjustDuration(int i, int i2, int i3) {
            float fAbs = Math.abs((i3 - i) / (i2 - i));
            int i4 = (int) (fAbs * 100.0f);
            if (i4 < 100) {
                float f = i4 / 100.0f;
                int i5 = i4 + 1;
                float[] fArr = SPLINE_TIME;
                float f2 = fArr[i4];
                this.mDuration = (int) (this.mDuration * (f2 + (((fAbs - f) / ((i5 / 100.0f) - f)) * (fArr[i5] - f2))));
            }
        }

        void startScroll(int i, int i2, int i3) {
            this.mFinished = false;
            double d = i;
            this.mStart = d;
            this.mCurrentPosition = d;
            this.mFinal = i + i2;
            this.mStartTime = AnimationUtils.currentAnimationTimeNanos();
            this.mDuration = i3;
            this.mDeceleration = 0.0f;
            this.mVelocity = 0.0d;
        }

        void startScrollByFling(float f, int i, int i2) {
            this.mFinished = false;
            this.mLastStep = false;
            setState(0);
            double d = f;
            this.mOriginStart = d;
            this.mStart = d;
            this.mCurrentPosition = d;
            this.mFinal = f + i;
            this.mStartTime = AnimationUtils.currentAnimationTimeNanos();
            double d2 = i2;
            this.mVelocity = d2;
            this.mCurrVelocity = d2;
            this.mSpringOperator = new SpringOperator();
            this.mSpringFactor = new double[]{0.99d, 0.4d};
            AnimData animData = new AnimData();
            this.mSpringAnimData = animData;
            animData.startValue = this.mStart;
            this.mSpringAnimData.targetValue = this.mFinal;
            this.mSpringAnimData.value = this.mCurrentPosition;
            this.mSpringAnimData.velocity = this.mCurrVelocity;
            double[] dArr = new double[3];
            this.mSpringParams = dArr;
            this.mSpringOperator.getParameters(this.mSpringFactor, dArr);
        }

        boolean computeScrollOffset() {
            if (this.mSpringOperator == null || this.mFinished) {
                return false;
            }
            if (this.mLastStep) {
                this.mFinished = true;
                this.mCurrentPosition = this.mFinal;
                return true;
            }
            long jCurrentAnimationTimeNanos = AnimationUtils.currentAnimationTimeNanos();
            double dMin = Math.min((jCurrentAnimationTimeNanos - this.mStartTime) / 1.0E9d, 0.01600000075995922d);
            double d = dMin == 0.0d ? 0.01600000075995922d : dMin;
            this.mStartTime = jCurrentAnimationTimeNanos;
            this.mSpringAnimData.startValue = this.mStart;
            this.mSpringAnimData.targetValue = this.mFinal;
            this.mSpringAnimData.value = this.mCurrentPosition;
            AnimData animData = this.mSpringAnimData;
            double d2 = this.mSpringFactor[0];
            double[] dArr = this.mSpringParams;
            SpringOperator.updateValues(animData, d2, dArr[1], dArr[2], d, false);
            this.mCurrentPosition = this.mSpringAnimData.value;
            this.mCurrVelocity = this.mSpringAnimData.velocity;
            if (isAtEquilibrium(this.mCurrentPosition, this.mFinal)) {
                this.mLastStep = true;
            } else {
                this.mStart = this.mCurrentPosition;
            }
            return true;
        }

        boolean isAtEquilibrium(double d, double d2) {
            return Math.abs(d - d2) < 1.0d;
        }

        void finish() {
            this.mCurrentPosition = this.mFinal;
            this.mFinished = true;
        }

        void setFinalPosition(int i) {
            this.mFinal = i;
            this.mFinished = false;
        }

        void extendDuration(int i) {
            this.mDuration = (((int) (AnimationUtils.currentAnimationTimeNanos() - this.mStartTime)) + i) / 1000000;
            this.mFinished = false;
        }

        boolean springback(int i, int i2, int i3) {
            this.mFinished = true;
            double d = i;
            this.mFinal = d;
            this.mStart = d;
            this.mCurrentPosition = d;
            this.mVelocity = 0.0d;
            this.mStartTime = AnimationUtils.currentAnimationTimeNanos();
            this.mDuration = 0;
            if (i < i2) {
                startSpringback(i, i2, 0);
            } else if (i > i3) {
                startSpringback(i, i3, 0);
            }
            return !this.mFinished;
        }

        private void startSpringback(int i, int i2, int i3) {
            this.mFinished = false;
            this.mState = 1;
            double d = i;
            this.mStart = d;
            this.mCurrentPosition = d;
            this.mFinal = i2;
            int i4 = i - i2;
            this.mDeceleration = getDeceleration(i4);
            this.mVelocity = -i4;
            this.mOver = Math.abs(i4);
            this.mDuration = (int) (Math.sqrt((((double) i4) * (-2.0d)) / ((double) this.mDeceleration)) * 1000.0d);
        }

        void fling(int i, int i2, int i3, int i4, int i5) {
            double splineFlingDistance;
            this.mOver = i5;
            this.mFinished = false;
            double d = i2;
            this.mVelocity = d;
            this.mCurrVelocity = d;
            this.mSplineDuration = 0;
            this.mDuration = 0;
            this.mStartTime = AnimationUtils.currentAnimationTimeNanos();
            double d2 = i;
            this.mStart = d2;
            this.mCurrentPosition = d2;
            if (i > i4 || i < i3) {
                startAfterEdge(i, i3, i4, i2);
                return;
            }
            this.mState = 0;
            if (i2 != 0) {
                int splineFlingDuration = getSplineFlingDuration(i2);
                this.mSplineDuration = splineFlingDuration;
                this.mDuration = splineFlingDuration;
                splineFlingDistance = getSplineFlingDistance(i2);
            } else {
                splineFlingDistance = 0.0d;
            }
            int iSignum = (int) (splineFlingDistance * ((double) Math.signum(i2)));
            this.mSplineDistance = iSignum;
            double d3 = i + iSignum;
            this.mFinal = d3;
            double d4 = i3;
            if (d3 < d4) {
                adjustDuration((int) this.mStart, (int) d3, i3);
                this.mFinal = d4;
            }
            double d5 = this.mFinal;
            double d6 = i4;
            if (d5 > d6) {
                adjustDuration((int) this.mStart, (int) d5, i4);
                this.mFinal = d6;
            }
        }

        private double getSplineDeceleration(int i) {
            return Math.log((Math.abs(i) * 0.35f) / (this.mFlingFriction * this.mPhysicalCoeff));
        }

        private double getSplineFlingDistance(int i) {
            double splineDeceleration = getSplineDeceleration(i);
            float f = DECELERATION_RATE;
            return ((double) (this.mFlingFriction * this.mPhysicalCoeff)) * Math.exp((((double) f) / (((double) f) - 1.0d)) * splineDeceleration);
        }

        private int getSplineFlingDuration(int i) {
            return (int) (Math.exp(getSplineDeceleration(i) / (((double) DECELERATION_RATE) - 1.0d)) * 1000.0d);
        }

        private void fitOnBounceCurve(int i, int i2, int i3) {
            float f = this.mDeceleration;
            float f2 = (-i3) / f;
            float f3 = i3;
            float fSqrt = (float) Math.sqrt((((double) ((((f3 * f3) / 2.0f) / Math.abs(f)) + Math.abs(i2 - i))) * 2.0d) / ((double) Math.abs(this.mDeceleration)));
            this.mStartTime -= (long) ((int) ((fSqrt - f2) * 1.0E9f));
            double d = i2;
            this.mStart = d;
            this.mCurrentPosition = d;
            this.mVelocity = (int) ((-this.mDeceleration) * fSqrt);
        }

        private void startBounceAfterEdge(int i, int i2, int i3) {
            this.mDeceleration = getDeceleration(i3 == 0 ? i - i2 : i3);
            fitOnBounceCurve(i, i2, i3);
            onEdgeReached();
        }

        private void startAfterEdge(int i, int i2, int i3, int i4) {
            if (i > i2 && i < i3) {
                Log.e("OverScroller", "startAfterEdge called from a valid position");
                this.mFinished = true;
                return;
            }
            boolean z = i > i3;
            int i5 = z ? i3 : i2;
            int i6 = i - i5;
            if (i6 * i4 >= 0) {
                startBounceAfterEdge(i, i5, i4);
            } else if (getSplineFlingDistance(i4) > Math.abs(i6)) {
                fling(i, i4, z ? i2 : i, z ? i : i3, this.mOver);
            } else {
                startSpringback(i, i5, i4);
            }
        }

        void notifyEdgeReached(int i, int i2, int i3) {
            if (this.mState == 0) {
                this.mOver = i3;
                this.mStartTime = AnimationUtils.currentAnimationTimeNanos();
                startAfterEdge(i, i2, i2, (int) this.mCurrVelocity);
            }
        }

        private void onEdgeReached() {
            double d = this.mVelocity;
            float f = ((float) d) * ((float) d);
            float fAbs = f / (Math.abs(this.mDeceleration) * 2.0f);
            float fSignum = Math.signum((float) this.mVelocity);
            int i = this.mOver;
            if (fAbs > i) {
                this.mDeceleration = ((-fSignum) * f) / (i * 2.0f);
                fAbs = i;
            }
            this.mOver = (int) fAbs;
            this.mState = 2;
            double d2 = this.mStart;
            double d3 = this.mVelocity;
            if (d3 <= 0.0d) {
                fAbs = -fAbs;
            }
            this.mFinal = d2 + ((double) ((int) fAbs));
            this.mDuration = -((int) ((d3 * 1000.0d) / ((double) this.mDeceleration)));
        }

        boolean continueWhenFinished() {
            int i = this.mState;
            if (i != 0) {
                if (i == 1) {
                    return false;
                }
                if (i == 2) {
                    this.mStartTime += ((long) this.mDuration) * FolmeCore.NANOS_TO_MS;
                    startSpringback((int) this.mFinal, (int) this.mStart, 0);
                }
            } else {
                if (this.mDuration >= this.mSplineDuration) {
                    return false;
                }
                double d = this.mFinal;
                this.mStart = d;
                this.mCurrentPosition = d;
                double d2 = (int) this.mCurrVelocity;
                this.mVelocity = d2;
                this.mDeceleration = getDeceleration((int) d2);
                this.mStartTime += ((long) this.mDuration) * FolmeCore.NANOS_TO_MS;
                onEdgeReached();
            }
            update();
            return true;
        }

        boolean update() {
            float f;
            float f2;
            double d;
            double d2;
            long jCurrentAnimationTimeNanos = AnimationUtils.currentAnimationTimeNanos() - this.mStartTime;
            if (jCurrentAnimationTimeNanos == 0) {
                return this.mDuration > 0;
            }
            int i = this.mDuration;
            if (jCurrentAnimationTimeNanos > ((long) i) * FolmeCore.NANOS_TO_MS) {
                return false;
            }
            int i2 = this.mState;
            if (i2 == 0) {
                int i3 = this.mSplineDuration;
                float f3 = jCurrentAnimationTimeNanos / (((long) i3) * FolmeCore.NANOS_TO_MS);
                int i4 = (int) (f3 * 100.0f);
                if (i4 < 100) {
                    float f4 = i4 / 100.0f;
                    int i5 = i4 + 1;
                    float[] fArr = SPLINE_POSITION;
                    float f5 = fArr[i4];
                    f2 = (fArr[i5] - f5) / ((i5 / 100.0f) - f4);
                    f = f5 + ((f3 - f4) * f2);
                } else {
                    f = 1.0f;
                    f2 = 0.0f;
                }
                int i6 = this.mSplineDistance;
                d = f * i6;
                this.mCurrVelocity = ((f2 * i6) / i3) * 1000.0f;
            } else {
                if (i2 == 1) {
                    float f6 = jCurrentAnimationTimeNanos / i;
                    float f7 = f6 * f6;
                    float fSignum = Math.signum((float) this.mVelocity);
                    int i7 = this.mOver;
                    d = i7 * fSignum * ((3.0f * f7) - ((2.0f * f6) * f7));
                    this.mCurrVelocity = fSignum * i7 * 6.0f * ((-f6) + f7);
                } else if (i2 != 2) {
                    d2 = 0.0d;
                } else {
                    float f8 = (float) (jCurrentAnimationTimeNanos / 1.0E9d);
                    double d3 = this.mVelocity;
                    float f9 = this.mDeceleration;
                    this.mCurrVelocity = ((double) (f9 * f8)) + d3;
                    d2 = (d3 * ((double) f8)) + ((double) (((f9 * f8) * f8) / 2.0f));
                }
                this.mCurrentPosition = this.mStart + ((double) ((int) Math.round(d2)));
                return true;
            }
            d2 = d;
            this.mCurrentPosition = this.mStart + ((double) ((int) Math.round(d2)));
            return true;
        }

        final boolean isFinished() {
            return this.mFinished;
        }

        final void setFinished(boolean z) {
            this.mFinished = z;
        }

        final int getCurrentPosition() {
            return (int) this.mCurrentPosition;
        }

        final void setCurrentPosition(int i) {
            this.mCurrentPosition = i;
        }

        final float getCurrVelocity() {
            return (float) this.mCurrVelocity;
        }

        final void setCurrVelocity(float f) {
            this.mCurrVelocity = f;
        }

        final int getStart() {
            return (int) this.mStart;
        }

        final void setStart(int i) {
            this.mStart = i;
        }

        final int getFinal() {
            return (int) this.mFinal;
        }

        final void setFinal(int i) {
            this.mFinal = i;
        }

        final int getDuration() {
            return this.mDuration;
        }

        final void setDuration(int i) {
            this.mDuration = i;
        }

        final int getState() {
            return this.mState;
        }

        final void setState(int i) {
            this.mState = i;
        }

        final long getStartTime() {
            return this.mStartTime;
        }

        final void setStartTime(long j) {
            this.mStartTime = j;
        }
    }

    static class ViscousFluidInterpolator implements Interpolator {
        private static final float VISCOUS_FLUID_NORMALIZE;
        private static final float VISCOUS_FLUID_OFFSET;
        private static final float VISCOUS_FLUID_SCALE = 8.0f;

        ViscousFluidInterpolator() {
        }

        static {
            float fViscousFluid = 1.0f / viscousFluid(1.0f);
            VISCOUS_FLUID_NORMALIZE = fViscousFluid;
            VISCOUS_FLUID_OFFSET = 1.0f - (fViscousFluid * viscousFluid(1.0f));
        }

        private static float viscousFluid(float f) {
            float f2 = f * VISCOUS_FLUID_SCALE;
            if (f2 < 1.0f) {
                return f2 - (1.0f - ((float) Math.exp(-f2)));
            }
            return 0.36787945f + ((1.0f - ((float) Math.exp(1.0f - f2))) * 0.63212055f);
        }

        @Override // android.animation.TimeInterpolator
        public float getInterpolation(float f) {
            float fViscousFluid = VISCOUS_FLUID_NORMALIZE * viscousFluid(f);
            return fViscousFluid > 0.0f ? fViscousFluid + VISCOUS_FLUID_OFFSET : fViscousFluid;
        }
    }
}
