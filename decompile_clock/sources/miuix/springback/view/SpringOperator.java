package miuix.springback.view;

/* JADX INFO: loaded from: classes3.dex */
public class SpringOperator {
    private final double damping;
    private final double tension;

    public SpringOperator(float f, float f2) {
        double d = f2;
        this.tension = Math.pow(6.283185307179586d / d, 2.0d);
        this.damping = (((double) f) * 12.566370614359172d) / d;
    }

    public double updateVelocity(double d, double d2, double d3, double d4) {
        return (d * (1.0d - (this.damping * d2))) + ((double) ((float) (this.tension * (d3 - d4) * d2)));
    }
}
