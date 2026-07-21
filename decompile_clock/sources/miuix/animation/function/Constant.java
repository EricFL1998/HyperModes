package miuix.animation.function;

/* JADX INFO: loaded from: classes2.dex */
public class Constant implements Differentiable {
    public static final Constant ZERO = new Constant(0.0d);
    private final double c;

    public Constant(double d) {
        this.c = d;
    }

    @Override // miuix.animation.function.Differentiable, miuix.animation.function.Function
    public double apply(double d) {
        return this.c;
    }

    @Override // miuix.animation.function.Differentiable
    public Function derivative() {
        return ZERO;
    }
}
