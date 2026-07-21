package miuix.view.animation;

import android.view.animation.Interpolator;

/* JADX INFO: loaded from: classes3.dex */
public class SineEaseInOutInterpolator implements Interpolator {
    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float f) {
        return ((float) (Math.cos(((double) f) * 3.141592653589793d) - 1.0d)) * (-0.5f);
    }
}
