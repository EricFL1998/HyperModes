package miuix.view.animation;

import android.view.animation.Interpolator;

/* JADX INFO: loaded from: classes3.dex */
public class BounceEaseInOutInterpolator implements Interpolator {
    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float f) {
        if (f < 0.5f) {
            return new BounceEaseInInterpolator().getInterpolation(f * 2.0f) * 0.5f;
        }
        return (new BounceEaseOutInterpolator().getInterpolation((f * 2.0f) - 1.0f) * 0.5f) + 0.5f;
    }
}
