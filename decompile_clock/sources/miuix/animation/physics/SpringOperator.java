package miuix.animation.physics;

import miuix.animation.internal.AnimData;

/* JADX INFO: loaded from: classes2.dex */
public class SpringOperator implements PhysicsOperator {
    double[] params;

    @Deprecated
    public double updateVelocity(double d, float f, float... fArr) {
        if (this.params == null) {
            return d;
        }
        double[] dArr = new double[fArr.length];
        for (int i = 0; i < fArr.length; i++) {
            dArr[i] = fArr[i];
        }
        double[] dArr2 = this.params;
        return updateVelocity(d, dArr2[0], dArr2[1], f, dArr);
    }

    @Override // miuix.animation.physics.PhysicsOperator
    public void getParameters(double[] dArr, double[] dArr2) {
        double d = dArr[0];
        double d2 = 6.283185307179586d / dArr[1];
        double d3 = dArr.length >= 3 ? dArr[2] : 1.0d;
        dArr2[0] = Math.pow(d2, 2.0d) * d3;
        dArr2[1] = 2.0d * d2 * d * d3;
        if (dArr2.length >= 3) {
            if (d > 1.0d) {
                dArr2[2] = d2 * Math.sqrt((d * d) - 1.0d);
            } else {
                if (d < 0.0d || d >= 1.0d) {
                    return;
                }
                dArr2[2] = d2 * Math.sqrt(1.0d - (d * d));
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:21:0x008c  */
    /* JADX WARN: Code duplicated, block: B:22:0x0090  */
    public static void updateValues(AnimData animData, double d, double d2, double d3, double d4, boolean z) {
        double d5;
        double d6 = z ? 1.0d : animData.targetValue;
        double d7 = z ? animData.progress : animData.value;
        double d8 = animData.velocity;
        double d9 = d7 - d6;
        double dExp = Math.exp(d2 * (-0.5d) * d4);
        double d10 = 0.0d;
        if (d < 0.0d || d >= 1.0d) {
            if (d == 1.0d) {
                double d11 = d8 + (d2 * 0.5d * d9);
                d10 = (d9 + (d11 * d4)) * dExp;
                animData.velocity = ((-0.5d) * d10 * d2) + (d11 * dExp);
            } else if (d > 1.0d) {
                double d12 = (d8 + ((d2 * 0.5d) * d9)) / d3;
                double d13 = d3 * d4;
                double dCosh = Math.cosh(d13);
                double dSinh = Math.sinh(d13);
                d5 = ((d9 * dCosh) + (d12 * dSinh)) * dExp;
                animData.velocity = ((-0.5d) * d5 * d2) + (((d12 * dCosh) + (d9 * dSinh)) * d3 * dExp);
            }
            if (z) {
                animData.progress = d10 + d6;
            } else {
                animData.value = d10 + d6;
            }
        }
        double d14 = (d8 + ((d2 * 0.5d) * d9)) / d3;
        double d15 = d3 * d4;
        double dCos = Math.cos(d15);
        double dSin = Math.sin(d15);
        d5 = ((d9 * dCos) + (d14 * dSin)) * dExp;
        animData.velocity = ((-0.5d) * d5 * d2) + (((d14 * dCos) - (d9 * dSin)) * d3 * dExp);
        d10 = d5;
        if (z) {
            animData.progress = d10 + d6;
        } else {
            animData.value = d10 + d6;
        }
    }

    @Override // miuix.animation.physics.PhysicsOperator
    @Deprecated
    public double updateVelocity(double d, double d2, double d3, double d4, double... dArr) {
        return (d * (1.0d - (Math.max(d3, 60.0d) * d4))) + ((double) ((float) (d2 * (dArr[0] - dArr[1]) * d4)));
    }
}
