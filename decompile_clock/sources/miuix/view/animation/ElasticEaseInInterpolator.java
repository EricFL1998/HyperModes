package miuix.view.animation;

import android.view.animation.Interpolator;

/* JADX INFO: loaded from: classes3.dex */
public class ElasticEaseInInterpolator implements Interpolator {
    private final float mAmplitude;
    private final float mPeriod;

    public ElasticEaseInInterpolator() {
        this(0.0f, 0.0f);
    }

    public ElasticEaseInInterpolator(float f, float f2) {
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
        if (f == 1.0f) {
            return 1.0f;
        }
        if (f2 == 0.0f) {
            f2 = 0.3f;
        }
        if (f3 == 0.0f || f3 < 1.0f) {
            fAsin = f2 / 4.0f;
            f3 = 1.0f;
        } else {
            fAsin = (float) ((((double) f2) / 6.283185307179586d) * Math.asin(1.0f / f3));
        }
        float f4 = f - 1.0f;
        return -((float) (((double) f3) * Math.pow(2.0d, 10.0f * f4) * Math.sin((((double) (f4 - fAsin)) * 6.283185307179586d) / ((double) f2))));
    }
}
