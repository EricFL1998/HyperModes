package miuix.animation.function;

/* JADX INFO: loaded from: classes2.dex */
public class Decelerate implements Differentiable {
    private double mFactor;

    public Decelerate() {
        this.mFactor = 1.0d;
    }

    public Decelerate(double d) {
        this.mFactor = d;
    }

    @Override // miuix.animation.function.Differentiable, miuix.animation.function.Function
    public double apply(double d) {
        double dPow;
        double d2 = this.mFactor;
        if (d2 == 1.0d) {
            double d3 = 1.0d - d;
            dPow = d3 * d3;
        } else {
            dPow = Math.pow(1.0d - d, d2 * 2.0d);
        }
        return (float) (1.0d - dPow);
    }

    @Override // miuix.animation.function.Differentiable
    public Function derivative() {
        return Constant.ZERO;
    }
}
