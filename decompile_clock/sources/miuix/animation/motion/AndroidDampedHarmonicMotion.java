package miuix.animation.motion;

import miuix.animation.function.Differentiable;
import miuix.animation.function.Function;

/* JADX INFO: loaded from: classes2.dex */
public class AndroidDampedHarmonicMotion extends DampedHarmonicMotion implements AndroidMotion {
    private double finishTime;
    private final double g;
    private final double p;
    private final double q;
    private double threshold;
    private final double xStar;

    public AndroidDampedHarmonicMotion(double d, double d2, double d3, double d4) {
        super(d, d2, d3, d4);
        this.p = d * 2.0d * d2;
        double d5 = d2 * d2;
        this.q = d5;
        this.xStar = ((-d4) / d5) + d3;
        this.g = d4;
    }

    @Override // miuix.animation.motion.AndroidMotion
    public void setThreshold(double d) {
        this.threshold = d;
        this.finishTime = Double.NaN;
    }

    @Override // miuix.animation.motion.DampedHarmonicMotion, miuix.animation.motion.BaseMotion
    protected void onInitialXChanged() {
        super.onInitialXChanged();
        this.finishTime = Double.NaN;
    }

    @Override // miuix.animation.motion.DampedHarmonicMotion, miuix.animation.motion.BaseMotion
    protected void onInitialVChanged() {
        super.onInitialVChanged();
        this.finishTime = Double.NaN;
    }

    @Override // miuix.animation.motion.DampedHarmonicMotion, miuix.animation.motion.Motion
    public double finishTime() {
        if (Double.isNaN(this.finishTime)) {
            this.finishTime = solveFinishTime();
        }
        return this.finishTime;
    }

    private double solveFinishTime() {
        double d;
        double dFinishTime = super.finishTime();
        if (dFinishTime == 0.0d || this.threshold == 0.0d) {
            return dFinishTime;
        }
        final Differentiable differentiableSolve = solve();
        if (this.g == 0.0d) {
            return (-Math.log(this.threshold)) / this.p;
        }
        Function function = new Function() { // from class: miuix.animation.motion.AndroidDampedHarmonicMotion$$ExternalSyntheticLambda0
            @Override // miuix.animation.function.Function
            public final double apply(double d2) {
                return this.f$0.m1792x9d3ab3de(differentiableSolve, d2);
            }
        };
        double dApply = function.apply(0.0d);
        double d2 = this.q;
        double d3 = this.xStar;
        double d4 = d2 * d3 * d3;
        double d5 = (dApply - d4) * this.threshold;
        double dApply2 = function.apply(1.0d);
        double d6 = 0.0d;
        double d7 = 1.0d;
        while (true) {
            d = d4 + d5;
            if (dApply2 <= d) {
                break;
            }
            double d8 = d7 + 1.0d;
            dApply2 = function.apply(d8);
            double d9 = d7;
            d7 = d8;
            d6 = d9;
        }
        do {
            double d10 = (d6 + d7) / 2.0d;
            if (function.apply(d10) > d) {
                d6 = d10;
            } else {
                d7 = d10;
            }
        } while (d7 - d6 >= this.threshold);
        return d7;
    }

    /* JADX INFO: renamed from: lambda$solveFinishTime$0$miuix-animation-motion-AndroidDampedHarmonicMotion, reason: not valid java name */
    /* synthetic */ double m1792x9d3ab3de(Differentiable differentiable, double d) {
        double dApply = differentiable.apply(d);
        double dApply2 = differentiable.derivative().apply(d);
        return (((this.q * dApply) * dApply) + (dApply2 * dApply2)) - ((this.g * 2.0d) * (dApply - this.xStar));
    }
}
