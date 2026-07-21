package miuix.animation.function;

/* JADX INFO: loaded from: classes2.dex */
public class Polynomial implements Differentiable {
    private final double[] a;
    private Function derivative;

    public Polynomial(int i, double... dArr) {
        int i2 = 1;
        int i3 = i + 1;
        if (dArr.length != i3) {
            throw new IllegalArgumentException("params must have a length of " + i3);
        }
        if (dArr[0] != 0.0d) {
            this.a = dArr;
            return;
        }
        while (i2 < dArr.length && dArr[i2] == 0.0d) {
            i2++;
        }
        i2 = i2 == dArr.length ? i2 - 1 : i2;
        double[] dArr2 = new double[dArr.length - i2];
        this.a = dArr2;
        System.arraycopy(dArr, i2, dArr2, 0, dArr2.length);
    }

    @Override // miuix.animation.function.Differentiable, miuix.animation.function.Function
    public double apply(double d) {
        double d2 = this.a[0];
        int i = 1;
        while (true) {
            double[] dArr = this.a;
            if (i >= dArr.length) {
                return d2;
            }
            d2 = (d2 * d) + dArr[i];
            i++;
        }
    }

    @Override // miuix.animation.function.Differentiable
    public Function derivative() {
        if (this.derivative == null) {
            double[] dArr = this.a;
            if (dArr.length == 1) {
                this.derivative = Constant.ZERO;
            } else {
                int length = dArr.length;
                int i = length - 1;
                double[] dArr2 = new double[i];
                for (int i2 = 0; i2 < i; i2++) {
                    dArr2[i2] = ((double) (i - i2)) * this.a[i2];
                }
                this.derivative = new Polynomial(length - 2, dArr2);
            }
        }
        return this.derivative;
    }
}
