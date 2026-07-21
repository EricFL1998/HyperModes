package com.android.deskclock.view.list;

/* JADX INFO: loaded from: classes.dex */
public interface ViewHolderEditableCallback {
    boolean hasAnimationStarted();

    void onAnimationStart(boolean z);

    void onAnimationStop(boolean z);

    void onAnimationUpdate(boolean z, float f);

    void onUpdateEditable(boolean z, boolean z2);
}
