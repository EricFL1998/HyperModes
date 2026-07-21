package miuix.animation.internal;

import miuix.animation.base.AnimConfig;
import miuix.animation.base.AnimSpecialConfig;
import miuix.animation.utils.EaseManager;

/* JADX INFO: loaded from: classes2.dex */
public class AnimConfigUtils {
    private AnimConfigUtils() {
    }

    static EaseManager.EaseStyle getEase(AnimConfig animConfig, AnimSpecialConfig animSpecialConfig) {
        EaseManager.EaseStyle easeStyle;
        if (animSpecialConfig != null && animSpecialConfig.ease != null && animSpecialConfig.ease != AnimConfig.sDefEase) {
            easeStyle = animSpecialConfig.ease;
        } else {
            easeStyle = animConfig.ease;
        }
        return easeStyle == null ? AnimConfig.sDefEase : easeStyle;
    }

    static long getDelay(AnimConfig animConfig, AnimSpecialConfig animSpecialConfig) {
        if (animSpecialConfig != null) {
            return Math.max(0L, animSpecialConfig.delay);
        }
        return 0L;
    }

    static int getTintMode(AnimConfig animConfig, AnimSpecialConfig animSpecialConfig) {
        return Math.max(animConfig.tintMode, animSpecialConfig != null ? animSpecialConfig.tintMode : -1);
    }

    static float getFromSpeed(AnimConfig animConfig, AnimSpecialConfig animSpecialConfig) {
        if (animSpecialConfig != null && AnimValueUtils.isValid(animSpecialConfig.fromSpeed)) {
            return animSpecialConfig.fromSpeed;
        }
        return animConfig.fromSpeed;
    }

    public static float chooseSpeed(float f, float f2) {
        if (AnimValueUtils.isInvalid(f)) {
            return f2;
        }
        return AnimValueUtils.isInvalid((double) f2) ? f : Math.max(f, f2);
    }
}
