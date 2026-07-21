package miuix.animation.motion;

import miuix.animation.function.Differentiable;

/* JADX INFO: loaded from: classes2.dex */
public interface Motion {
    default double finishTime() {
        return Double.POSITIVE_INFINITY;
    }

    double getInitialV();

    double getInitialX();

    void setInitialV(double d);

    void setInitialX(double d);

    Differentiable solve();

    default double stopPosition() {
        return Double.NaN;
    }

    default double stopSpeed() {
        return Double.NaN;
    }
}
