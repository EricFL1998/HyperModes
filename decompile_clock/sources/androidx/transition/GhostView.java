package androidx.transition;

import android.view.View;
import android.view.ViewGroup;

/* JADX INFO: loaded from: classes.dex */
interface GhostView {
    void reserveEndViewTransition(ViewGroup viewGroup, View view);

    void setVisibility(int i);
}
