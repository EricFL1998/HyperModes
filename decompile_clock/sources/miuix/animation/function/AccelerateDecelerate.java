package miuix.animation.function;

/* JADX INFO: loaded from: classes2.dex */
public class AccelerateDecelerate implements Differentiable {
    @Override // miuix.animation.function.Differentiable, miuix.animation.function.Function
    public double apply(double d) {
        return ((float) (Math.cos((d + 1.0d) * 3.141592653589793d) / 2.0d)) + 0.5f;
    }

    @Override // miuix.animation.function.Differentiable
    public Function derivative() {
        return Constant.ZERO;
    }
}
