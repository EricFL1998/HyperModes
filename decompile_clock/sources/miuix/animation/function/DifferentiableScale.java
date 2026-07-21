package miuix.animation.function;

/* JADX INFO: loaded from: classes2.dex */
public class DifferentiableScale extends Scale implements Differentiable {
    private final Differentiable base;
    private Function derivative;

    public DifferentiableScale(Differentiable differentiable, double d, double d2, double d3, double d4) {
        super(differentiable, d, d2, d3, d4);
        this.base = differentiable;
    }

    @Override // miuix.animation.function.Differentiable
    public Function derivative() {
        if (this.derivative == null) {
            Function functionDerivative = this.base.derivative();
            if (functionDerivative instanceof Differentiable) {
                this.derivative = new DifferentiableScale((Differentiable) functionDerivative, getScaleX(), getScaleY(), 0.0d, 0.0d);
            } else {
                this.derivative = new Scale(functionDerivative, getScaleX(), getScaleY(), 0.0d, 0.0d);
            }
        }
        return this.derivative;
    }
}
