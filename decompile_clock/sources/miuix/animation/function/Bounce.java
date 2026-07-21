package miuix.animation.function;

/* JADX INFO: loaded from: classes2.dex */
public class Bounce implements Differentiable {
    private static double bounce(double d) {
        return d * d * 8.0d;
    }

    @Override // miuix.animation.function.Differentiable, miuix.animation.function.Function
    public double apply(double d) {
        double dBounce;
        double d2;
        double d3 = d * 1.1226d;
        if (d3 < 0.3535d) {
            return bounce(d3);
        }
        if (d3 < 0.7408d) {
            dBounce = bounce(d3 - 0.54719d);
            d2 = 0.7d;
        } else if (d3 < 0.9644d) {
            dBounce = bounce(d3 - 0.8526d);
            d2 = 0.9d;
        } else {
            dBounce = bounce(d3 - 1.0435d);
            d2 = 0.95d;
        }
        return dBounce + d2;
    }

    @Override // miuix.animation.function.Differentiable
    public Function derivative() {
        return Constant.ZERO;
    }
}
