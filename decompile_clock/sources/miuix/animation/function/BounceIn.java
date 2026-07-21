package miuix.animation.function;

/* JADX INFO: loaded from: classes2.dex */
public class BounceIn implements Differentiable {
    @Override // miuix.animation.function.Differentiable, miuix.animation.function.Function
    public double apply(double d) {
        return 1.0d - new BounceOut().apply(1.0d - d);
    }

    @Override // miuix.animation.function.Differentiable
    public Function derivative() {
        return Constant.ZERO;
    }
}
