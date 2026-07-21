package miuix.animation.motion;

import miuix.animation.function.Differentiable;
import miuix.animation.function.Function;

/* JADX INFO: loaded from: classes2.dex */
public final class MotionConverter implements Motion {
    private Differentiable function;
    private final Motion mMotion;
    private final double mScale;
    private final double mZeroPoint;

    public MotionConverter(Motion motion, double d, double d2) {
        if (d2 == 0.0d) {
            throw new IllegalArgumentException("scale must not be zero");
        }
        this.mMotion = motion;
        this.mZeroPoint = d;
        this.mScale = d2;
    }

    @Override // miuix.animation.motion.Motion
    public void setInitialX(double d) {
        this.mMotion.setInitialX((d - this.mZeroPoint) / this.mScale);
    }

    @Override // miuix.animation.motion.Motion
    public void setInitialV(double d) {
        this.mMotion.setInitialV(d / this.mScale);
    }

    @Override // miuix.animation.motion.Motion
    public double getInitialX() {
        return this.mScale + this.mZeroPoint;
    }

    @Override // miuix.animation.motion.Motion
    public double getInitialV() {
        return this.mMotion.getInitialV() * this.mScale;
    }

    /* JADX INFO: renamed from: miuix.animation.motion.MotionConverter$1, reason: invalid class name */
    class AnonymousClass1 implements Differentiable {
        AnonymousClass1() {
        }

        @Override // miuix.animation.function.Differentiable, miuix.animation.function.Function
        public double apply(double d) {
            return (MotionConverter.this.mMotion.solve().apply(d) * MotionConverter.this.mScale) + MotionConverter.this.mZeroPoint;
        }

        @Override // miuix.animation.function.Differentiable
        public Function derivative() {
            return new Function() { // from class: miuix.animation.motion.MotionConverter$1$$ExternalSyntheticLambda0
                @Override // miuix.animation.function.Function
                public final double apply(double d) {
                    return this.f$0.m1793lambda$derivative$0$miuixanimationmotionMotionConverter$1(d);
                }
            };
        }

        /* JADX INFO: renamed from: lambda$derivative$0$miuix-animation-motion-MotionConverter$1, reason: not valid java name */
        /* synthetic */ double m1793lambda$derivative$0$miuixanimationmotionMotionConverter$1(double d) {
            return MotionConverter.this.mMotion.solve().derivative().apply(d) * MotionConverter.this.mScale;
        }
    }

    @Override // miuix.animation.motion.Motion
    public Differentiable solve() {
        if (this.function == null) {
            this.function = new AnonymousClass1();
        }
        return this.function;
    }

    @Override // miuix.animation.motion.Motion
    public double finishTime() {
        return this.mMotion.finishTime();
    }

    @Override // miuix.animation.motion.Motion
    public double stopPosition() {
        return (this.mMotion.stopPosition() * this.mScale) + this.mZeroPoint;
    }

    @Override // miuix.animation.motion.Motion
    public double stopSpeed() {
        return this.mMotion.stopSpeed() * this.mScale;
    }
}
