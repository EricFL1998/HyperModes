package miuix.animation.function;

/* JADX INFO: loaded from: classes2.dex */
public class Logarithmic implements Differentiable {
    private final double a;
    private Function derivative;
    private final boolean isLn;
    private final double k;

    public Logarithmic(double d) {
        this(1.0d, d);
    }

    public Logarithmic(double d, double d2) {
        this.k = d;
        this.a = d2;
        this.isLn = d2 == 2.718281828459045d;
    }

    @Override // miuix.animation.function.Differentiable, miuix.animation.function.Function
    public double apply(double d) {
        return this.isLn ? this.k * Math.log(d) : (this.k * Math.log(d)) / Math.log(this.a);
    }

    @Override // miuix.animation.function.Differentiable
    public Function derivative() {
        if (this.derivative == null) {
            this.derivative = new InverseProportional(this.isLn ? this.k : this.k / Math.log(this.a));
        }
        return this.derivative;
    }
}
