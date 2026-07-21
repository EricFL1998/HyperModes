package miuix.animation.physics;

import miuix.animation.property.FloatProperty;

/* JADX INFO: loaded from: classes2.dex */
public class EquilibriumChecker {
    public static final float MIN_VISIBLE_CHANGE_ALPHA = 0.00390625f;
    public static final float MIN_VISIBLE_CHANGE_PIXELS = 1.0f;
    public static final float MIN_VISIBLE_CHANGE_ROTATION_DEGREES = 0.1f;
    public static final float MIN_VISIBLE_CHANGE_SCALE = 0.004f;
    public static final float THRESHOLD_MULTIPLIER = 0.75f;
    public static final float VELOCITY_THRESHOLD_MULTIPLIER = 16.666666f;
    public static final float VELOCITY_THRESHOLD_MULTIPLIER_HIGH_FPS = 8.333333f;
    private double mTargetValue = Double.MAX_VALUE;
    private float mValueThreshold;
    private float mVelocityThreshold;

    public void init(FloatProperty floatProperty, double d) {
        float minVisibleChange = floatProperty.getMinVisibleChange() * 0.75f;
        this.mValueThreshold = minVisibleChange;
        this.mVelocityThreshold = minVisibleChange * 16.666666f;
        this.mTargetValue = d;
    }

    public boolean isAtEquilibrium(int i, double d, double d2) {
        return (i != -2 || isAt(d, this.mTargetValue)) && i != -3 && Math.abs(d2) < ((double) this.mVelocityThreshold);
    }

    public float getVelocityThreshold() {
        return this.mVelocityThreshold;
    }

    private boolean isAt(double d, double d2) {
        return Math.abs(this.mTargetValue) == 3.4028234663852886E38d || Math.abs(d - d2) < ((double) this.mValueThreshold);
    }
}
