package miuix.view.animation;

import android.graphics.Path;
import android.view.animation.Interpolator;

/* JADX INFO: loaded from: classes3.dex */
public class PathInterpolator implements Interpolator {
    private static final float PRECISION = 0.002f;
    private float[] mX;
    private float[] mY;

    public PathInterpolator(Path path) {
        initPath(path);
    }

    public PathInterpolator(float f, float f2) {
        initQuad(f, f2);
    }

    public PathInterpolator(float f, float f2, float f3, float f4) {
        initCubic(f, f2, f3, f4);
    }

    private void initQuad(float f, float f2) {
        Path path = new Path();
        path.moveTo(0.0f, 0.0f);
        path.quadTo(f, f2, 1.0f, 1.0f);
        initPath(path);
    }

    private void initCubic(float f, float f2, float f3, float f4) {
        Path path = new Path();
        path.moveTo(0.0f, 0.0f);
        path.cubicTo(f, f2, f3, f4, 1.0f, 1.0f);
        initPath(path);
    }

    private void initPath(Path path) {
        float[] fArrApproximate = path.approximate(0.002f);
        int length = fArrApproximate.length / 3;
        float f = 0.0f;
        if (fArrApproximate[1] != 0.0f || fArrApproximate[2] != 0.0f || fArrApproximate[fArrApproximate.length - 2] != 1.0f || fArrApproximate[fArrApproximate.length - 1] != 1.0f) {
            throw new IllegalArgumentException("The Path must start at (0,0) and end at (1,1)");
        }
        this.mX = new float[length];
        this.mY = new float[length];
        int i = 0;
        int i2 = 0;
        float f2 = 0.0f;
        while (i < length) {
            float f3 = fArrApproximate[i2];
            int i3 = i2 + 2;
            float f4 = fArrApproximate[i2 + 1];
            i2 += 3;
            float f5 = fArrApproximate[i3];
            if (f3 == f && f4 != f2) {
                throw new IllegalArgumentException("The Path cannot have discontinuity in the X axis.");
            }
            if (f4 < f2) {
                throw new IllegalArgumentException("The Path cannot loop back on itself.");
            }
            this.mX[i] = f4;
            this.mY[i] = f5;
            i++;
            f2 = f4;
            f = f3;
        }
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float f) {
        if (f <= 0.0f) {
            return 0.0f;
        }
        if (f >= 1.0f) {
            return 1.0f;
        }
        int length = this.mX.length - 1;
        int i = 0;
        while (length - i > 1) {
            int i2 = (i + length) / 2;
            if (f < this.mX[i2]) {
                length = i2;
            } else {
                i = i2;
            }
        }
        float[] fArr = this.mX;
        float f2 = fArr[length];
        float f3 = fArr[i];
        float f4 = f2 - f3;
        if (f4 == 0.0f) {
            return this.mY[i];
        }
        float f5 = (f - f3) / f4;
        float[] fArr2 = this.mY;
        float f6 = fArr2[i];
        return f6 + (f5 * (fArr2[length] - f6));
    }
}
