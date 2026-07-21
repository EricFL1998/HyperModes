package miuix.animation.function;

/* JADX INFO: loaded from: classes2.dex */
public class Accelerate implements Differentiable {
    private final double mDoubleFactor;
    private final double mFactor;

    public Accelerate() {
        this.mFactor = 1.0d;
        this.mDoubleFactor = 2.0d;
    }

    public Accelerate(double d) {
        this.mFactor = d;
        this.mDoubleFactor = d * 2.0d;
    }

    @Override // miuix.animation.function.Differentiable, miuix.animation.function.Function
    public double apply(double d) {
        return this.mFactor == 1.0d ? d * d : Math.pow(d, this.mDoubleFactor);
    }

    @Override // miuix.animation.function.Differentiable
    public Function derivative() {
        return Constant.ZERO;
    }
}
