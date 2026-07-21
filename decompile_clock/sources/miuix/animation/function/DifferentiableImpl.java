package miuix.animation.function;

/* JADX INFO: loaded from: classes2.dex */
public class DifferentiableImpl implements Differentiable {
    private final Function derivative;
    private final Function function;

    public DifferentiableImpl(Function function, Function function2) {
        this.function = function;
        this.derivative = function2;
    }

    @Override // miuix.animation.function.Differentiable, miuix.animation.function.Function
    public double apply(double d) {
        return this.function.apply(d);
    }

    @Override // miuix.animation.function.Differentiable
    public Function derivative() {
        return this.derivative;
    }
}
