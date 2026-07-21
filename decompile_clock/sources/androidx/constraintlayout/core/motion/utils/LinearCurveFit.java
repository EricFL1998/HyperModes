package androidx.constraintlayout.core.motion.utils;

/* JADX INFO: loaded from: classes.dex */
public class LinearCurveFit extends CurveFit {
    private static final String TAG = "LinearCurveFit";
    private boolean mExtrapolate = true;
    double[] mSlopeTemp;
    private double[] mT;
    private double mTotalLength;
    private double[][] mY;

    public LinearCurveFit(double[] dArr, double[][] dArr2) {
        this.mTotalLength = Double.NaN;
        int length = dArr.length;
        int length2 = dArr2[0].length;
        this.mSlopeTemp = new double[length2];
        this.mT = dArr;
        this.mY = dArr2;
        if (length2 <= 2) {
            return;
        }
        int i = 0;
        double d = 0.0d;
        while (true) {
            double d2 = d;
            if (i < dArr.length) {
                double d3 = dArr2[i][0];
                if (i > 0) {
                    Math.hypot(d3 - d, d3 - d2);
                }
                i++;
                d = d3;
            } else {
                this.mTotalLength = 0.0d;
                return;
            }
        }
    }

    private double getLength2D(double d) {
        if (Double.isNaN(this.mTotalLength)) {
            return 0.0d;
        }
        double[] dArr = this.mT;
        int length = dArr.length;
        if (d <= dArr[0]) {
            return 0.0d;
        }
        int i = length - 1;
        if (d >= dArr[i]) {
            return this.mTotalLength;
        }
        double dHypot = 0.0d;
        double d2 = 0.0d;
        double d3 = 0.0d;
        int i2 = 0;
        while (i2 < i) {
            double[] dArr2 = this.mY[i2];
            double d4 = dArr2[0];
            double d5 = dArr2[1];
            if (i2 > 0) {
                dHypot += Math.hypot(d4 - d2, d5 - d3);
            }
            double[] dArr3 = this.mT;
            double d6 = dArr3[i2];
            if (d == d6) {
                return dHypot;
            }
            int i3 = i2 + 1;
            double d7 = dArr3[i3];
            if (d < d7) {
                double d8 = (d - d6) / (d7 - d6);
                double[][] dArr4 = this.mY;
                double[] dArr5 = dArr4[i2];
                double d9 = dArr5[0];
                double[] dArr6 = dArr4[i3];
                double d10 = 1.0d - d8;
                return dHypot + Math.hypot(d5 - ((dArr5[1] * d10) + (dArr6[1] * d8)), d4 - ((d9 * d10) + (dArr6[0] * d8)));
            }
            i2 = i3;
            d2 = d4;
            d3 = d5;
        }
        return 0.0d;
    }

    @Override // androidx.constraintlayout.core.motion.utils.CurveFit
    public void getPos(double d, double[] dArr) {
        double[] dArr2 = this.mT;
        int length = dArr2.length;
        int i = 0;
        int length2 = this.mY[0].length;
        if (this.mExtrapolate) {
            double d2 = dArr2[0];
            if (d <= d2) {
                getSlope(d2, this.mSlopeTemp);
                for (int i2 = 0; i2 < length2; i2++) {
                    dArr[i2] = this.mY[0][i2] + ((d - this.mT[0]) * this.mSlopeTemp[i2]);
                }
                return;
            }
            int i3 = length - 1;
            double d3 = dArr2[i3];
            if (d >= d3) {
                getSlope(d3, this.mSlopeTemp);
                while (i < length2) {
                    dArr[i] = this.mY[i3][i] + ((d - this.mT[i3]) * this.mSlopeTemp[i]);
                    i++;
                }
                return;
            }
        } else {
            if (d <= dArr2[0]) {
                for (int i4 = 0; i4 < length2; i4++) {
                    dArr[i4] = this.mY[0][i4];
                }
                return;
            }
            int i5 = length - 1;
            if (d >= dArr2[i5]) {
                while (i < length2) {
                    dArr[i] = this.mY[i5][i];
                    i++;
                }
                return;
            }
        }
        int i6 = 0;
        while (i6 < length - 1) {
            if (d == this.mT[i6]) {
                for (int i7 = 0; i7 < length2; i7++) {
                    dArr[i7] = this.mY[i6][i7];
                }
            }
            double[] dArr3 = this.mT;
            int i8 = i6 + 1;
            double d4 = dArr3[i8];
            if (d < d4) {
                double d5 = dArr3[i6];
                double d6 = (d - d5) / (d4 - d5);
                while (i < length2) {
                    double[][] dArr4 = this.mY;
                    dArr[i] = (dArr4[i6][i] * (1.0d - d6)) + (dArr4[i8][i] * d6);
                    i++;
                }
                return;
            }
            i6 = i8;
        }
    }

    @Override // androidx.constraintlayout.core.motion.utils.CurveFit
    public void getPos(double d, float[] fArr) {
        double[] dArr = this.mT;
        int length = dArr.length;
        int i = 0;
        int length2 = this.mY[0].length;
        if (this.mExtrapolate) {
            double d2 = dArr[0];
            if (d <= d2) {
                getSlope(d2, this.mSlopeTemp);
                for (int i2 = 0; i2 < length2; i2++) {
                    fArr[i2] = (float) (this.mY[0][i2] + ((d - this.mT[0]) * this.mSlopeTemp[i2]));
                }
                return;
            }
            int i3 = length - 1;
            double d3 = dArr[i3];
            if (d >= d3) {
                getSlope(d3, this.mSlopeTemp);
                while (i < length2) {
                    fArr[i] = (float) (this.mY[i3][i] + ((d - this.mT[i3]) * this.mSlopeTemp[i]));
                    i++;
                }
                return;
            }
        } else {
            if (d <= dArr[0]) {
                for (int i4 = 0; i4 < length2; i4++) {
                    fArr[i4] = (float) this.mY[0][i4];
                }
                return;
            }
            int i5 = length - 1;
            if (d >= dArr[i5]) {
                while (i < length2) {
                    fArr[i] = (float) this.mY[i5][i];
                    i++;
                }
                return;
            }
        }
        int i6 = 0;
        while (i6 < length - 1) {
            if (d == this.mT[i6]) {
                for (int i7 = 0; i7 < length2; i7++) {
                    fArr[i7] = (float) this.mY[i6][i7];
                }
            }
            double[] dArr2 = this.mT;
            int i8 = i6 + 1;
            double d4 = dArr2[i8];
            if (d < d4) {
                double d5 = dArr2[i6];
                double d6 = (d - d5) / (d4 - d5);
                while (i < length2) {
                    double[][] dArr3 = this.mY;
                    fArr[i] = (float) ((dArr3[i6][i] * (1.0d - d6)) + (dArr3[i8][i] * d6));
                    i++;
                }
                return;
            }
            i6 = i8;
        }
    }

    @Override // androidx.constraintlayout.core.motion.utils.CurveFit
    public double getPos(double d, int i) {
        double d2;
        double d3;
        double slope;
        double[] dArr = this.mT;
        int length = dArr.length;
        int i2 = 0;
        if (this.mExtrapolate) {
            double d4 = dArr[0];
            if (d <= d4) {
                d2 = this.mY[0][i];
                d3 = d - d4;
                slope = getSlope(d4, i);
            } else {
                int i3 = length - 1;
                double d5 = dArr[i3];
                if (d >= d5) {
                    d2 = this.mY[i3][i];
                    d3 = d - d5;
                    slope = getSlope(d5, i);
                }
            }
            return d2 + (d3 * slope);
        }
        if (d <= dArr[0]) {
            return this.mY[0][i];
        }
        int i4 = length - 1;
        if (d >= dArr[i4]) {
            return this.mY[i4][i];
        }
        while (i2 < length - 1) {
            double[] dArr2 = this.mT;
            double d6 = dArr2[i2];
            if (d == d6) {
                return this.mY[i2][i];
            }
            int i5 = i2 + 1;
            double d7 = dArr2[i5];
            if (d < d7) {
                double d8 = (d - d6) / (d7 - d6);
                double[][] dArr3 = this.mY;
                return (dArr3[i2][i] * (1.0d - d8)) + (dArr3[i5][i] * d8);
            }
            i2 = i5;
        }
        return 0.0d;
    }

    /* JADX WARN: Code duplicated, block: B:4:0x000f A[PHI: r4
  0x000f: PHI (r4v5 double) = (r4v0 double), (r4v2 double) binds: [B:3:0x000d, B:6:0x0017] A[DONT_GENERATE, DONT_INLINE]] */
    @Override // androidx.constraintlayout.core.motion.utils.CurveFit
    public void getSlope(double d, double[] dArr) {
        double[] dArr2 = this.mT;
        int length = dArr2.length;
        int length2 = this.mY[0].length;
        double d2 = dArr2[0];
        if (d <= d2) {
            d = d2;
        } else {
            d2 = dArr2[length - 1];
            if (d >= d2) {
                d = d2;
            }
        }
        int i = 0;
        while (i < length - 1) {
            double[] dArr3 = this.mT;
            int i2 = i + 1;
            double d3 = dArr3[i2];
            if (d <= d3) {
                double d4 = d3 - dArr3[i];
                for (int i3 = 0; i3 < length2; i3++) {
                    double[][] dArr4 = this.mY;
                    dArr[i3] = (dArr4[i2][i3] - dArr4[i][i3]) / d4;
                }
                return;
            }
            i = i2;
        }
    }

    /* JADX WARN: Code duplicated, block: B:4:0x000a A[PHI: r3
  0x000a: PHI (r3v4 double) = (r3v0 double), (r3v2 double) binds: [B:3:0x0008, B:6:0x0012] A[DONT_GENERATE, DONT_INLINE]] */
    @Override // androidx.constraintlayout.core.motion.utils.CurveFit
    public double getSlope(double d, int i) {
        double[] dArr = this.mT;
        int length = dArr.length;
        int i2 = 0;
        double d2 = dArr[0];
        if (d < d2) {
            d = d2;
        } else {
            d2 = dArr[length - 1];
            if (d >= d2) {
                d = d2;
            }
        }
        while (i2 < length - 1) {
            double[] dArr2 = this.mT;
            int i3 = i2 + 1;
            double d3 = dArr2[i3];
            if (d <= d3) {
                double d4 = d3 - dArr2[i2];
                double[][] dArr3 = this.mY;
                return (dArr3[i3][i] - dArr3[i2][i]) / d4;
            }
            i2 = i3;
        }
        return 0.0d;
    }

    @Override // androidx.constraintlayout.core.motion.utils.CurveFit
    public double[] getTimePoints() {
        return this.mT;
    }
}
