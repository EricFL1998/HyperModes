package miuix.animation.internal;

import android.animation.TimeInterpolator;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import miuix.animation.IAnimTarget;
import miuix.animation.base.AnimConfig;
import miuix.animation.base.AnimConfigLink;
import miuix.animation.controller.AnimState;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.physics.PhysicsOperator;
import miuix.animation.property.FloatProperty;
import miuix.animation.utils.CommonUtils;
import miuix.animation.utils.EaseManager;

/* JADX INFO: loaded from: classes2.dex */
public class PredictTask {
    static final TransitionInfo.IUpdateInfoCreator sCreator = new TransitionInfo.IUpdateInfoCreator() { // from class: miuix.animation.internal.PredictTask.1
        @Override // miuix.animation.internal.TransitionInfo.IUpdateInfoCreator
        public UpdateInfo getUpdateInfo(FloatProperty floatProperty) {
            return new UpdateInfo(floatProperty);
        }
    };

    public static long predictDuration(IAnimTarget iAnimTarget, AnimState animState, AnimState animState2, AnimConfigLink animConfigLink) {
        TransitionInfo transitionInfo = new TransitionInfo(iAnimTarget, animState, animState2, animConfigLink);
        transitionInfo.initUpdateList(sCreator);
        if (transitionInfo.animTasks.isEmpty()) {
            Log.w(CommonUtils.TAG, "warning!! predictDuration failed! info " + transitionInfo + " trace:" + Log.getStackTraceString(new Throwable()));
            return 0L;
        }
        double d = FolmeEngine.MAX_DELTA / 1.0E9d;
        long j = 16666666;
        while (true) {
            Iterator<AnimTask> it = transitionInfo.animTasks.iterator();
            while (it.hasNext()) {
                AnimTaskStackRunner.doAnimationFrame(it.next(), j, FolmeEngine.MAX_DELTA, 1, d, false);
                j = j;
            }
            long j2 = j;
            if (!transitionInfo.getInfoAnimStats().isRunning()) {
                AnimTaskStackRunner.animDataLocal.remove();
                return (long) (j2 / 1000000.0d);
            }
            j = j2 + FolmeEngine.MAX_DELTA;
        }
    }

    public static double predictNextValue(IAnimTarget iAnimTarget, AnimState animState, FloatProperty floatProperty, AnimConfig animConfig) {
        EaseManager.EaseStyle ease = AnimConfigUtils.getEase(animState.getConfig(), animConfig.getSpecialConfig(floatProperty));
        float value = iAnimTarget.getValue(floatProperty);
        double velocity = iAnimTarget.getVelocity(floatProperty);
        ArrayList<TransitionInfo> arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        iAnimTarget.animManager.addToTransitionInfoList(arrayList);
        UpdateInfo updateInfoFindBy = null;
        for (TransitionInfo transitionInfo : arrayList) {
            arrayList2.clear();
            arrayList2.addAll(transitionInfo.updateList);
            if (transitionInfo.containsPropertyInUpdateList(floatProperty)) {
                updateInfoFindBy = UpdateInfo.findBy(transitionInfo.updateList, floatProperty);
            }
        }
        if (updateInfoFindBy == null) {
            return Double.MAX_VALUE;
        }
        double averageDeltaNanos = AndroidEngine.getInst().getAverageDeltaNanos() / 1.0E9d;
        if (EaseManager.isPhysicsStyle(ease.style) && ease.parameters != null) {
            PhysicsOperator phyOperator = FolmeCore.getPhyOperator(ease.style);
            if (AnimValueUtils.isInvalid(updateInfoFindBy.animInfo.targetValue)) {
                return Double.MAX_VALUE;
            }
            double d = value;
            return d + ((velocity + phyOperator.updateVelocity(velocity, ease.parameters[0], ease.parameters[1], averageDeltaNanos, updateInfoFindBy.animInfo.targetValue, d)) * 0.5d * averageDeltaNanos);
        }
        EaseManager.InterpolateEaseStyle interpolateEaseStyle = (EaseManager.InterpolateEaseStyle) ease;
        TimeInterpolator interpolator = EaseManager.getInterpolator(interpolateEaseStyle);
        long jCurrentTimeMillis = (System.currentTimeMillis() - updateInfoFindBy.animInfo.startTime) + ((long) averageDeltaNanos);
        if (interpolator != null && jCurrentTimeMillis < interpolateEaseStyle.duration) {
            return interpolator.getInterpolation(jCurrentTimeMillis / interpolateEaseStyle.duration);
        }
        return updateInfoFindBy.animInfo.targetValue;
    }

    public static double predictNextVelocity(IAnimTarget iAnimTarget, AnimState animState, FloatProperty floatProperty, AnimConfig animConfig) {
        EaseManager.EaseStyle ease = AnimConfigUtils.getEase(animState.getConfig(), animConfig.getSpecialConfig(floatProperty));
        float value = iAnimTarget.getValue(floatProperty);
        double velocity = iAnimTarget.getVelocity(floatProperty);
        ArrayList<TransitionInfo> arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        iAnimTarget.animManager.addToTransitionInfoList(arrayList);
        UpdateInfo updateInfoFindBy = null;
        for (TransitionInfo transitionInfo : arrayList) {
            arrayList2.clear();
            arrayList2.addAll(transitionInfo.updateList);
            if (transitionInfo.containsPropertyInUpdateList(floatProperty)) {
                updateInfoFindBy = UpdateInfo.findBy(arrayList2, floatProperty);
            }
        }
        if (updateInfoFindBy == null || !EaseManager.isPhysicsStyle(ease.style) || ease.parameters == null) {
            return Double.MAX_VALUE;
        }
        double averageDeltaNanos = AndroidEngine.getInst().getAverageDeltaNanos() / 1.0E9d;
        PhysicsOperator phyOperator = FolmeCore.getPhyOperator(ease.style);
        if (AnimValueUtils.isInvalid(updateInfoFindBy.animInfo.targetValue)) {
            return Double.MAX_VALUE;
        }
        return phyOperator.updateVelocity(velocity, ease.parameters[0], ease.parameters[1], averageDeltaNanos, updateInfoFindBy.animInfo.targetValue, value);
    }
}
