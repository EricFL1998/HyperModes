package miuix.animation.function;

/* JADX INFO: loaded from: classes2.dex */
public class BounceInOut implements Differentiable {
    @Override // miuix.animation.function.Differentiable, miuix.animation.function.Function
    public double apply(double d) {
        if (d < 0.5d) {
            return new BounceIn().apply(d * 2.0d) * 0.5d;
        }
        return (new BounceOut().apply((d * 2.0d) - 1.0d) * 0.5d) + 0.5d;
    }

    @Override // miuix.animation.function.Differentiable
    public Function derivative() {
        return Constant.ZERO;
    }
}
