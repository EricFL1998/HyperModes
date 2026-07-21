package miuix.transition;

import miuix.animation.utils.EaseManager;

/* JADX INFO: loaded from: classes3.dex */
public abstract class LayoutAnimator {
    protected MiuixTransition mTransition;

    public abstract void prepareTransition();

    public abstract void traceChangeToTransition(Runnable runnable);

    public MiuixTransition getTransition() {
        return this.mTransition;
    }

    public void addTransitionListener(MiuixTransition.MiuixTransitionListener miuixTransitionListener) {
        MiuixTransition miuixTransition = this.mTransition;
        if (miuixTransition == null) {
            throw new RuntimeException("please complete transition preparation before adding listener");
        }
        miuixTransition.addListener(miuixTransitionListener);
    }

    protected static EaseManager.EaseStyle spring(float f, float f2) {
        return EaseManager.getStyle(-2, f, f2);
    }
}
