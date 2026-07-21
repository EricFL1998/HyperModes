package miuix.view.animation;

import android.view.animation.Interpolator;

/* JADX INFO: loaded from: classes3.dex */
public class CirclularEaseInOutInterpolator implements Interpolator {
    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float f) {
        float fSqrt;
        float f2;
        float f3 = f * 2.0f;
        if (f3 < 1.0f) {
            fSqrt = (float) (Math.sqrt(1.0f - (f3 * f3)) - 1.0d);
            f2 = -0.5f;
        } else {
            float f4 = f3 - 2.0f;
            fSqrt = (float) (Math.sqrt(1.0f - (f4 * f4)) + 1.0d);
            f2 = 0.5f;
        }
        return fSqrt * f2;
    }
}
