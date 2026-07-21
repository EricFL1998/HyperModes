package miuix.animation.function;

/* JADX INFO: loaded from: classes2.dex */
public class BounceOut implements Differentiable {
    @Override // miuix.animation.function.Differentiable, miuix.animation.function.Function
    public double apply(double d) {
        if (d < 0.36363636363636365d) {
            return 7.5625d * d * d;
        }
        if (d < 0.7272727272727273d) {
            double d2 = d - 0.5454545454545454d;
            return (7.5625d * d2 * d2) + 0.75d;
        }
        if (d < 0.9090909090909091d) {
            double d3 = d - 0.8181818181818182d;
            return (7.5625d * d3 * d3) + 0.9375d;
        }
        double d4 = d - 0.9545454545454546d;
        return (7.5625d * d4 * d4) + 0.984375d;
    }

    @Override // miuix.animation.function.Differentiable
    public Function derivative() {
        return Constant.ZERO;
    }
}
