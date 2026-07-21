package miuix.view.animation;

import android.view.animation.Interpolator;

/* JADX INFO: loaded from: classes3.dex */
public class ExponentialEaseInOutInterpolator implements Interpolator {
    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float f) {
        double dPow;
        if (f == 0.0f) {
            return 0.0f;
        }
        if (f == 1.0f) {
            return 1.0f;
        }
        float f2 = f * 2.0f;
        if (f2 < 1.0f) {
            dPow = Math.pow(2.0d, (f2 - 1.0f) * 10.0f);
        } else {
            dPow = (-Math.pow(2.0d, (f2 - 1.0f) * (-10.0f))) + 2.0d;
        }
        return ((float) dPow) * 0.5f;
    }
}
