package miuix.navigator.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

/* JADX INFO: loaded from: classes3.dex */
public abstract class LayoutWidgetProvider<T> implements WidgetProvider<T> {
    private final int mWidgetFrameRes;

    @Override // miuix.navigator.adapter.WidgetProvider
    public abstract void onSetupWidget(ViewGroup viewGroup, T t, boolean z);

    public LayoutWidgetProvider(int i) {
        this.mWidgetFrameRes = i;
    }

    public int getWidgetFrameRes() {
        return this.mWidgetFrameRes;
    }

    @Override // miuix.navigator.adapter.WidgetProvider
    public void onPrepareWidget(ViewGroup viewGroup) {
        if (this.mWidgetFrameRes == 0) {
            return;
        }
        viewGroup.addView(LayoutInflater.from(viewGroup.getContext()).inflate(this.mWidgetFrameRes, viewGroup, false));
    }
}
