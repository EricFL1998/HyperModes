package miuix.view.animation;

import android.view.animation.Interpolator;

/* JADX INFO: loaded from: classes3.dex */
public class BackEaseInOutInterpolator implements Interpolator {
    private final float mOvershot;

    public BackEaseInOutInterpolator() {
        this(0.0f);
    }

    public BackEaseInOutInterpolator(float f) {
        this.mOvershot = f;
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float f) {
        float f2 = this.mOvershot;
        if (f2 == 0.0f) {
            f2 = 1.70158f;
        }
        float f3 = f * 2.0f;
        if (f3 < 1.0f) {
            float f4 = (float) (((double) f2) * 1.525d);
            return f3 * f3 * (((1.0f + f4) * f3) - f4) * 0.5f;
        }
        float f5 = f3 - 2.0f;
        float f6 = (float) (((double) f2) * 1.525d);
        return ((f5 * f5 * (((1.0f + f6) * f5) + f6)) + 2.0f) * 0.5f;
    }
}
