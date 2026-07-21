package miuix.animation.function;

/* JADX INFO: loaded from: classes2.dex */
public class Bezier implements Differentiable {
    private static final Bezier ZERO = new Bezier(null, null, null);
    private Differentiable derivative;
    private final double[] originX;
    private double[] p;
    private final double[] x;
    private final double[] y;

    private double linearInterpolate(double d, double d2, double d3) {
        return d + ((d2 - d) * d3);
    }

    public Bezier(double... dArr) {
        double[] dArr2 = new double[dArr.length / 2];
        this.x = dArr2;
        this.originX = dArr2;
        this.y = new double[dArr.length / 2];
        int i = 0;
        while (true) {
            double[] dArr3 = this.x;
            if (i >= dArr3.length) {
                return;
            }
            int i2 = i * 2;
            dArr3[i] = dArr[i2];
            this.y[i] = dArr[i2 + 1];
            i++;
        }
    }

    private Bezier(double[] dArr, double[] dArr2, double[] dArr3) {
        this.originX = dArr;
        this.x = dArr2;
        this.y = dArr3;
    }

    @Override // miuix.animation.function.Differentiable, miuix.animation.function.Function
    public double apply(double d) {
        if (this == ZERO) {
            return 0.0d;
        }
        double tForXValue = getTForXValue(d);
        if (this.originX == this.x) {
            return solveAxis(this.y, tForXValue);
        }
        double dSolveAxis = solveAxis(this.y, tForXValue) / solveAxis(this.x, tForXValue);
        if (Double.isNaN(dSolveAxis)) {
            return 0.0d;
        }
        return Math.min(1000000.0d, Math.max(dSolveAxis, -1000000.0d));
    }

    @Override // miuix.animation.function.Differentiable
    public Function derivative() {
        if (this.derivative == null) {
            this.derivative = solveDerivative();
        }
        return this.derivative;
    }

    private Bezier solveDerivative() {
        int length;
        Bezier bezier = ZERO;
        if (this == bezier || (length = this.y.length - 1) <= 1) {
            return bezier;
        }
        double[] dArr = new double[length];
        double[] dArr2 = new double[length];
        int i = 0;
        while (i < length) {
            double d = length;
            double[] dArr3 = this.x;
            int i2 = i + 1;
            dArr[i] = (dArr3[i2] - dArr3[i]) * d;
            double[] dArr4 = this.y;
            dArr2[i] = d * (dArr4[i2] - dArr4[i]);
            i = i2;
        }
        return new Bezier(this.x, dArr, dArr2);
    }

    private double solveAxis(double[] dArr, double d) {
        if (d == 0.0d) {
            return dArr[0];
        }
        if (d == 1.0d) {
            return dArr[dArr.length - 1];
        }
        double[] dArr2 = this.p;
        if (dArr2 == null || dArr2.length < dArr.length) {
            this.p = new double[dArr.length];
        }
        System.arraycopy(dArr, 0, this.p, 0, dArr.length);
        for (int length = dArr.length - 1; length > 0; length--) {
            int i = 0;
            while (i < length) {
                double[] dArr3 = this.p;
                int i2 = i + 1;
                dArr3[i] = linearInterpolate(dArr3[i], dArr3[i2], d);
                i = i2;
            }
        }
        return this.p[0];
    }

    private double getTForXValue(double d) {
        double d2;
        double d3 = 0.0d;
        double d4 = d;
        double d5 = 1.0d;
        int i = 0;
        double dSolveAxis = 0.0d;
        while (true) {
            if (i >= 8) {
                d2 = d5;
                break;
            }
            dSolveAxis = solveAxis(this.originX, d4);
            d2 = d5;
            double dSolveAxis2 = (solveAxis(this.originX, d4 + 1.0E-6d) - dSolveAxis) / 1.0E-6d;
            double d6 = dSolveAxis - d;
            if (Math.abs(d6) < 1.0E-6d) {
                return d4;
            }
            if (Math.abs(dSolveAxis2) < 1.0E-6d) {
                break;
            }
            if (dSolveAxis < d) {
                d3 = d4;
            } else {
                d2 = d4;
            }
            d4 -= d6 / dSolveAxis2;
            i++;
            d5 = d2;
        }
        double d7 = d2;
        for (int i2 = 0; Math.abs(dSolveAxis - d) > 1.0E-6d && i2 < 8; i2++) {
            if (dSolveAxis < d) {
                d3 = d4;
                d4 = (d4 + d7) / 2.0d;
            } else {
                d7 = d4;
                d4 = (d4 + d3) / 2.0d;
            }
            dSolveAxis = solveAxis(this.originX, d4);
        }
        return d4;
    }
}
