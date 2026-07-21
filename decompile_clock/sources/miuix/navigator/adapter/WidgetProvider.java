package miuix.navigator.adapter;

import android.view.ViewGroup;

/* JADX INFO: loaded from: classes3.dex */
public interface WidgetProvider<T> {
    void onPrepareWidget(ViewGroup viewGroup);

    void onSetupWidget(ViewGroup viewGroup, T t, boolean z);
}
