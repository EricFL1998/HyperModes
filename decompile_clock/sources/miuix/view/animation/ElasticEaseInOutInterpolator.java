package miuix.view.animation;

import android.view.animation.Interpolator;

/* JADX INFO: loaded from: classes3.dex */
public class ElasticEaseInOutInterpolator implements Interpolator {
    private final float mAmplitude;
    private final float mPeriod;

    public ElasticEaseInOutInterpolator() {
        this(0.0f, 0.0f);
    }

    public ElasticEaseInOutInterpolator(float f, float f2) {
        this.mAmplitude = f;
        this.mPeriod = f2;
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float f) {
        float fAsin;
        float f2 = this.mPeriod;
        float f3 = this.mAmplitude;
        if (f == 0.0f) {
            return 0.0f;
        }
        float f4 = f / 0.5f;
        if (f4 == 2.0f) {
            return 1.0f;
        }
        if (f2 == 0.0f) {
            f2 = 0.45000002f;
        }
        if (f3 == 0.0f || f3 < 1.0f) {
            fAsin = f2 / 4.0f;
            f3 = 1.0f;
        } else {
            fAsin = (float) ((((double) f2) / 6.283185307179586d) * Math.asin(1.0f / f3));
        }
        if (f4 < 1.0f) {
            float f5 = f4 - 1.0f;
            return ((float) (((double) f3) * Math.pow(2.0d, 10.0f * f5) * Math.sin((((double) (f5 - fAsin)) * 6.283185307179586d) / ((double) f2)))) * (-0.5f);
        }
        float f6 = f4 - 1.0f;
        return (float) ((((double) f3) * Math.pow(2.0d, (-10.0f) * f6) * Math.sin((((double) (f6 - fAsin)) * 6.283185307179586d) / ((double) f2)) * 0.5d) + 1.0d);
    }
}
