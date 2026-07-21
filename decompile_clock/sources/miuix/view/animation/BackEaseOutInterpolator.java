package miuix.view.animation;

import android.view.animation.Interpolator;

/* JADX INFO: loaded from: classes3.dex */
public class BackEaseOutInterpolator implements Interpolator {
    private final float mOvershot;

    public BackEaseOutInterpolator() {
        this(0.0f);
    }

    public BackEaseOutInterpolator(float f) {
        this.mOvershot = f;
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float f) {
        float f2 = this.mOvershot;
        if (f2 == 0.0f) {
            f2 = 1.70158f;
        }
        float f3 = f - 1.0f;
        return (f3 * f3 * (((f2 + 1.0f) * f3) + f2)) + 1.0f;
    }
}
