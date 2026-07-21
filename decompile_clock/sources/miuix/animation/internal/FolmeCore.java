package miuix.animation.internal;

import android.animation.FloatEvaluator;
import android.animation.IntEvaluator;
import android.animation.TimeInterpolator;
import android.animation.TypeEvaluator;
import android.util.Log;
import miuix.animation.FolmeFactory;
import miuix.animation.IAnimTarget;
import miuix.animation.function.Differentiable;
import miuix.animation.motion.Motion;
import miuix.animation.physics.AccelerateOperator;
import miuix.animation.physics.EquilibriumChecker;
import miuix.animation.physics.FrictionOperator;
import miuix.animation.physics.PhysicsOperator;
import miuix.animation.physics.SpringOperator;
import miuix.animation.property.FloatProperty;
import miuix.animation.property.IIntValueProperty;
import miuix.animation.utils.CommonUtils;
import miuix.animation.utils.EaseManager;
import miuix.animation.utils.LogUtils;

/* JADX INFO: loaded from: classes2.dex */
public class FolmeCore {
    private static final long LONGEST_DURATION_NANOS = 10000000000L;
    public static final long NANOS_TO_MS = 1000000;
    public static final long NANOS_TO_S = 1000000000;
    static final SpringOperator sSpring = new SpringOperator();
    static final AccelerateOperator sAccelerate = new AccelerateOperator();
    static final FrictionOperator sFriction = new FrictionOperator();
    static IntEvaluator sIntEvaluator = new IntEvaluator();
    static FloatEvaluator sFloatEvaluator = new FloatEvaluator();
    static final ThreadLocal<EquilibriumChecker> mCheckerLocal = new ThreadLocal<>();

    private static float regulateProgress(float f) {
        if (f > 1.0f) {
            return 1.0f;
        }
        if (f < 0.0f) {
            return 0.0f;
        }
        return f;
    }

    public static void doAnimationFrame(IAnimTarget iAnimTarget, boolean z, AnimData animData, long j, double d, int i) {
        long j2 = j - animData.startTime;
        if (EaseManager.isPhysicsStyle(animData.ease.style)) {
            updatePhysicsAnim(iAnimTarget, animData, z, j2, d, i);
            return;
        }
        if (EaseManager.isPhysicsMotionStyle(animData.ease.style)) {
            updatePhysicsMotionAnim(animData, j2);
        } else if (animData.ease instanceof EaseManager.InterpolateEaseStyle) {
            updateInterpolatorAnim(animData, z, j2 / NANOS_TO_MS);
        } else if (EaseManager.isDurationMotionStyle(animData.ease.style)) {
            updateDurationMotionAnim(animData, z, j2);
        }
    }

    @Deprecated
    private static void updateInterpolatorAnim(AnimData animData, boolean z, long j) {
        EaseManager.InterpolateEaseStyle interpolateEaseStyle = (EaseManager.InterpolateEaseStyle) animData.ease;
        TimeInterpolator interpolator = EaseManager.getInterpolator(interpolateEaseStyle);
        if (j < interpolateEaseStyle.duration) {
            animData.progress = interpolator.getInterpolation(j / interpolateEaseStyle.duration);
        } else {
            animData.setOp((byte) 3);
            animData.progress = 1.0d;
        }
        if (z) {
            animData.value = ((Integer) CommonUtils.sArgbEvaluator.evaluate((float) animData.progress, Integer.valueOf((int) animData.startValue), Integer.valueOf((int) animData.targetValue))).doubleValue();
        } else {
            animData.value = evaluateValue(animData, (float) animData.progress);
        }
        if (Double.isInfinite(animData.value)) {
            Log.e(CommonUtils.TAG, "updateInterpolatorAnim data.value isInfinite!  data.ease " + interpolateEaseStyle + " totalTime_ms " + j + " interpolator " + interpolator + " data.progress " + animData.progress);
        }
    }

    private static double evaluateValue(AnimData animData, float f) {
        TypeEvaluator evaluator = getEvaluator(animData.property);
        if (evaluator instanceof IntEvaluator) {
            return ((IntEvaluator) evaluator).evaluate(f, Integer.valueOf((int) animData.startValue), Integer.valueOf((int) animData.targetValue)).doubleValue();
        }
        return ((FloatEvaluator) evaluator).evaluate(f, (Number) Float.valueOf((float) animData.startValue), (Number) Float.valueOf((float) animData.targetValue)).doubleValue();
    }

    private static TypeEvaluator getEvaluator(FloatProperty floatProperty) {
        if (floatProperty instanceof IIntValueProperty) {
            return sIntEvaluator;
        }
        return sFloatEvaluator;
    }

    private static void updatePhysicsAnim(IAnimTarget iAnimTarget, AnimData animData, boolean z, long j, double d, int i) {
        EquilibriumChecker equilibriumChecker = (EquilibriumChecker) CommonUtils.getLocal(FolmeFactory.getEngine().getObjPool(), mCheckerLocal, EquilibriumChecker.class);
        if (animData.property.getMinVisibleChange() == -1.0f) {
            animData.property.setMinVisibleChange(iAnimTarget.getMinVisibleChange(animData.property.getName()));
        }
        equilibriumChecker.init(animData.property, animData.targetValue);
        for (int i2 = 0; i2 < i; i2++) {
            if (z) {
                doArgbPhysicsCalculation(animData, d);
            } else {
                doPhysicsCalculation(animData, d);
            }
            if (!isAnimRunning(equilibriumChecker, animData.property, animData.ease.style, animData.value, animData.velocity, j)) {
                animData.setOp((byte) 3);
                if (LogUtils.isLogMoreEnable()) {
                    LogUtils.debug("----- updatePhysicsAnim data.setOp(AnimTask.OP_END)", new Object[0]);
                }
                setFinishValue(animData);
                return;
            }
        }
    }

    private static void updateDurationMotionAnim(AnimData animData, boolean z, long j) {
        EaseManager.DurationMotionEaseStyle durationMotionEaseStyle = (EaseManager.DurationMotionEaseStyle) animData.ease;
        double d = j / (durationMotionEaseStyle.factors[0] * 1000000.0d);
        Motion durationMotion = EaseManager.getDurationMotion(durationMotionEaseStyle);
        if (d > 1.0d) {
            animData.progress = durationMotion.stopPosition();
            animData.value = animData.targetValue;
            animData.velocity = durationMotion.stopSpeed();
            animData.setOp((byte) 3);
        } else {
            Differentiable differentiableSolve = durationMotion.solve();
            animData.progress = differentiableSolve.apply(d);
            if (z) {
                animData.value = ((Integer) CommonUtils.sArgbEvaluator.evaluate((float) animData.progress, Integer.valueOf((int) animData.startValue), Integer.valueOf((int) animData.targetValue))).doubleValue();
            } else {
                animData.value = animData.startValue + ((animData.targetValue - animData.startValue) * animData.progress);
            }
            animData.velocity = differentiableSolve.derivative().apply(d);
        }
        if (Double.isInfinite(animData.value)) {
            Log.e(CommonUtils.TAG, "updateDurationMotionAnim data.value isInfinite!  data.ease " + durationMotionEaseStyle + " totalTimeNanos " + j + " data.progress " + animData.progress);
        }
    }

    private static void updatePhysicsMotionAnim(AnimData animData, long j) {
        Motion motion = ((EaseManager.PhysicsMotionEaseStyle) animData.ease).motion;
        double dFinishTime = motion.finishTime();
        double d = animData.duration;
        if (Double.isFinite(dFinishTime) && d >= dFinishTime) {
            animData.value = ((animData.targetValue - animData.startValue) * motion.stopPosition()) + animData.startValue;
            animData.velocity = motion.stopSpeed();
            animData.setOp((byte) 3);
        } else {
            Differentiable differentiableSolve = motion.solve();
            animData.value = ((animData.targetValue - animData.startValue) * differentiableSolve.apply(d)) + animData.startValue;
            animData.velocity = differentiableSolve.derivative().apply(d);
        }
    }

    private static void setFinishValue(AnimData animData) {
        if (isUsingSpringPhy(animData)) {
            animData.value = animData.targetValue;
        }
    }

    private static void doPhysicsCalculation(AnimData animData, double d) {
        boolean z;
        double d2 = animData.velocity;
        PhysicsOperator phyOperator = getPhyOperator(animData.ease.style);
        if (phyOperator == null || (((z = phyOperator instanceof SpringOperator)) && AnimValueUtils.isInvalid(animData.targetValue))) {
            animData.value = animData.targetValue;
            animData.velocity = 0.0d;
            return;
        }
        if (z) {
            if (animData.frameCount == 1) {
                double d3 = animData.value;
                SpringOperator.updateValues(animData, animData.ease.factors[0], animData.ease.parameters[1], animData.ease.parameters[2], d, false);
                animData.value = d3;
            }
            SpringOperator.updateValues(animData, animData.ease.factors[0], animData.ease.parameters[1], animData.ease.parameters[2], d, false);
            if (Double.isInfinite(animData.value)) {
                Log.e(CommonUtils.TAG, "doPhysicsCalculation data.value isInfinite! startVelocity " + d2 + " data.ease.parameters " + animData.ease.parameters + " delta " + d + " data.targetValue " + animData.targetValue + " data.velocity " + animData.velocity);
                return;
            }
            return;
        }
        double dUpdateVelocity = phyOperator.updateVelocity(d2, animData.ease.parameters[0], animData.ease.parameters[1], d, animData.targetValue, animData.value);
        animData.value += (animData.velocity + dUpdateVelocity) * 0.5d * d;
        if (Double.isInfinite(animData.value)) {
            Log.e(CommonUtils.TAG, "doPhysicsCalculation data.value isInfinite! startVelocity " + d2 + " velocity " + dUpdateVelocity + " data.ease.parameters " + animData.ease.parameters + " delta " + d + " data.targetValue " + animData.targetValue + " data.velocity " + animData.velocity);
        }
        animData.velocity = dUpdateVelocity;
    }

    private static void doArgbPhysicsCalculation(AnimData animData, double d) {
        boolean z;
        double d2 = animData.velocity;
        PhysicsOperator phyOperator = getPhyOperator(animData.ease.style);
        if (phyOperator == null || (((z = phyOperator instanceof SpringOperator)) && AnimValueUtils.isInvalid(animData.targetValue))) {
            animData.value = 1.0d;
            animData.velocity = 0.0d;
        } else if (z) {
            if (animData.frameCount == 1) {
                double d3 = animData.progress;
                SpringOperator.updateValues(animData, animData.ease.factors[0], animData.ease.parameters[1], animData.ease.parameters[2], d, true);
                animData.progress = d3;
            }
            SpringOperator.updateValues(animData, animData.ease.factors[0], animData.ease.parameters[1], animData.ease.parameters[2], d, true);
        } else {
            double dUpdateVelocity = phyOperator.updateVelocity(d2, animData.ease.parameters[0], animData.ease.parameters[1], d, 1.0d, animData.progress);
            animData.progress += (animData.velocity + dUpdateVelocity) * 0.5d * d;
            if (animData.progress > 1.0d) {
                animData.progress = 1.0d;
            } else if (animData.progress < 0.0d) {
                animData.progress = 0.0d;
            }
            animData.velocity = dUpdateVelocity;
        }
        Integer num = (Integer) CommonUtils.sArgbEvaluator.evaluate((float) animData.progress, Integer.valueOf((int) animData.startValue), Integer.valueOf((int) animData.targetValue));
        if (LogUtils.isLogFrameEnable() || LogUtils.isLogDetailEnable()) {
            LogUtils.debug("doArgbPhysics p='" + animData.property.getName() + "' color=" + Integer.toHexString(num.intValue()) + " fraction=" + animData.progress, new Object[0]);
        }
        animData.value = num.doubleValue();
    }

    static boolean isAnimRunning(EquilibriumChecker equilibriumChecker, FloatProperty floatProperty, int i, double d, double d2, long j) {
        boolean z = !equilibriumChecker.isAtEquilibrium(i, d, d2);
        if (!z || j <= LONGEST_DURATION_NANOS) {
            return z;
        }
        if (LogUtils.isLogMainEnabled()) {
            LogUtils.debug("animation for " + floatProperty.getName() + " stopped for running time too long, totalTime_nanos = " + j, new Object[0]);
        }
        return false;
    }

    public static PhysicsOperator getPhyOperator(int i) {
        if (i == -4) {
            return sFriction;
        }
        if (i == -3) {
            return sAccelerate;
        }
        if (i != -2) {
            return null;
        }
        return sSpring;
    }

    @Deprecated
    public static float getVelocityThreshold() {
        EquilibriumChecker equilibriumChecker = (EquilibriumChecker) CommonUtils.getLocal(FolmeFactory.getEngine().getObjPool(), mCheckerLocal, EquilibriumChecker.class);
        if (equilibriumChecker != null) {
            return equilibriumChecker.getVelocityThreshold();
        }
        return 0.0f;
    }

    private static boolean isUsingSpringPhy(AnimData animData) {
        return animData.ease.style == -2;
    }
}
