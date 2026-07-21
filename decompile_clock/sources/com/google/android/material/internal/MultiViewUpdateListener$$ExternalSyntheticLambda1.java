package com.google.android.material.internal;

import android.animation.ValueAnimator;
import android.view.View;

/* JADX INFO: compiled from: D8$$SyntheticClass */
/* JADX INFO: loaded from: classes2.dex */
public final /* synthetic */ class MultiViewUpdateListener$$ExternalSyntheticLambda1 implements MultiViewUpdateListener.Listener {
    @Override // com.google.android.material.internal.MultiViewUpdateListener.Listener
    public final void onAnimationUpdate(ValueAnimator valueAnimator, View view) {
        MultiViewUpdateListener.setScale(valueAnimator, view);
    }
}
