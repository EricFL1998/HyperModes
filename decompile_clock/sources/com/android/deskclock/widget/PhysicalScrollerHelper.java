package com.android.deskclock.widget;

import android.util.Log;
import android.view.animation.AnimationUtils;

/* JADX INFO: loaded from: classes.dex */
public class PhysicalScrollerHelper {
    private static final double DAMPING = 1.0d;
    private static final double DEFAULT_FRICTION = 0.3333333333333333d;
    private static final int FLING_STATE = 1;
    public static final double INVALIDATE_FRICTION = -1.0d;
    private static final double MASS = 1.0d;
    private static final double MAX_DELTA_TIME = 33.0d;
    private static final double MEDIAN_DAMPING = 41.88790038338829d;
    private static final double MIN_VISIBLE_VELOCITY = 70.0d;
    private static final double RESPONSE = 0.30000001192092896d;
    private static final int SPRING_STATE = 2;
    private static final String TAG = "PhysicalScroller";
    private static final double TENSION = Math.pow(20.943950191694146d, 2.0d) * 1.0d;
    private static final double USE_PHY_MIN_VELOCITY = 1000.0d;
    private double mCurrentPosition;
    private double mCurrentVelocity;
    private double mEndPosition;
    private double mFriction;
    private boolean mIsFinish = true;
    private long mLastTime;
    private double mMaxEndPosition;
    private double mMinEndPosition;
    private int mState;

    private double getDistance(double d, double d2, double d3) {
        return (((d + d2) / 2.0d) * d3) / USE_PHY_MIN_VELOCITY;
    }

    private static double getMinVisibleVelocity() {
        return MIN_VISIBLE_VELOCITY;
    }

    public void startAnim(double d, double d2, double d3, double d4, double d5) {
        this.mMaxEndPosition = d5;
        this.mMinEndPosition = d4;
        this.mFriction = getFriction(d2, d, d3);
        this.mIsFinish = false;
        this.mLastTime = AnimationUtils.currentAnimationTimeMillis();
        this.mCurrentPosition = d;
        this.mCurrentVelocity = d2;
        this.mEndPosition = d3;
        if (this.mFriction == -1.0d) {
            this.mState = 2;
        } else {
            this.mState = 1;
        }
        Log.d(TAG, "startAnim, startPos=" + d + ", endPos=" + d3 + ", vel=" + d2 + ", state=" + this.mState);
    }

    public void startAnim(double d, double d2, String str) {
        this.mMaxEndPosition = d2;
        this.mMinEndPosition = d2;
        this.mFriction = getFriction(getMinVisibleVelocity(), d, d2);
        this.mIsFinish = false;
        this.mLastTime = AnimationUtils.currentAnimationTimeMillis();
        this.mCurrentPosition = d;
        this.mCurrentVelocity = getMinVisibleVelocity();
        this.mEndPosition = d2;
        if (this.mFriction == -1.0d) {
            this.mState = 2;
        } else {
            this.mState = 1;
        }
        Log.d(TAG, "startAnim, startPos=" + d + ", endPos=" + d2 + ", vel=" + this.mCurrentVelocity + ", state=" + this.mState);
    }

    public boolean isFinish() {
        return this.mIsFinish;
    }

    public double getPosition() {
        return this.mCurrentPosition;
    }

    public void stopScroll() {
        this.mCurrentPosition = this.mEndPosition;
        this.mIsFinish = true;
    }

    public static double getEndPosition(double d, double d2) {
        return getEndPosition(d, d2, getMinVisibleVelocity(), DEFAULT_FRICTION);
    }

    private static double getEndVelocity(double d, double d2, double d3) {
        return d * Math.pow(Math.pow(2.718281828459045d, d2 * (-4.2d)), d3 / USE_PHY_MIN_VELOCITY);
    }

    public boolean computeScrollOffset() {
        if (isFinish()) {
            return false;
        }
        long jCurrentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis();
        long j = jCurrentAnimationTimeMillis - this.mLastTime;
        this.mLastTime = jCurrentAnimationTimeMillis;
        if (this.mState == 1) {
            double d = this.mCurrentVelocity;
            double d2 = j;
            double endVelocity = getEndVelocity(d, this.mFriction, d2);
            this.mCurrentVelocity = endVelocity;
            if (Math.abs(endVelocity) < getMinVisibleVelocity()) {
                this.mIsFinish = true;
                return false;
            }
            double distance = this.mCurrentPosition + getDistance(d, this.mCurrentVelocity, d2);
            this.mCurrentPosition = distance;
            double d3 = this.mMaxEndPosition;
            if (distance > d3 || distance < this.mMinEndPosition) {
                this.mState = 2;
                if (distance <= d3) {
                    d3 = this.mMinEndPosition;
                }
                this.mEndPosition = d3;
            }
            return true;
        }
        double d4 = ((-this.mCurrentVelocity) * MEDIAN_DAMPING) + (TENSION * (this.mEndPosition - this.mCurrentPosition));
        double dMin = Math.min(j, MAX_DELTA_TIME) / USE_PHY_MIN_VELOCITY;
        double d5 = this.mCurrentVelocity + (d4 * dMin);
        this.mCurrentVelocity = d5;
        double d6 = this.mCurrentPosition + (d5 * dMin);
        this.mCurrentPosition = d6;
        if (Math.abs(d6 - this.mEndPosition) < 1.0d) {
            this.mCurrentPosition = this.mEndPosition;
            this.mIsFinish = true;
            return false;
        }
        boolean z = this.mCurrentPosition == this.mEndPosition;
        this.mIsFinish = z;
        return !z;
    }

    public static double getEndPosition(double d, double d2, double d3, double d4) {
        if (Math.abs(d2) < USE_PHY_MIN_VELOCITY) {
            return d;
        }
        if (d2 * d3 < 0.0d) {
            d3 = -d3;
        }
        return d + ((d2 - d3) / (d4 * 4.2d));
    }

    private static double getFriction(double d, double d2, double d3) {
        return getFriction(d2, d3, d, getMinVisibleVelocity());
    }

    private static double getFriction(double d, double d2, double d3, double d4) {
        if (Math.abs(d3) < USE_PHY_MIN_VELOCITY) {
            return -1.0d;
        }
        if (d4 * d3 < 0.0d) {
            d4 = -d4;
        }
        double d5 = d2 - d;
        if (d3 * d5 <= 0.0d) {
            return -1.0d;
        }
        return (d3 - d4) / (d5 * 4.2d);
    }
}
