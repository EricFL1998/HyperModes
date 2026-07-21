package miuix.responsive.wrapper;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

/* JADX INFO: loaded from: classes3.dex */
public abstract class Factory2Wrapper implements LayoutInflater.Factory2 {
    private LayoutInflater.Factory2 mOriginFactory2;

    public void setOriginFactory2(LayoutInflater.Factory2 factory2) {
        this.mOriginFactory2 = factory2;
    }

    @Override // android.view.LayoutInflater.Factory2
    public View onCreateView(View view, String str, Context context, AttributeSet attributeSet) {
        LayoutInflater.Factory2 factory2 = this.mOriginFactory2;
        if (factory2 != null) {
            return factory2.onCreateView(view, str, context, attributeSet);
        }
        return null;
    }

    @Override // android.view.LayoutInflater.Factory
    public View onCreateView(String str, Context context, AttributeSet attributeSet) {
        LayoutInflater.Factory2 factory2 = this.mOriginFactory2;
        if (factory2 != null) {
            return factory2.onCreateView(str, context, attributeSet);
        }
        return null;
    }

    public void onDestroy() {
        this.mOriginFactory2 = null;
    }
}
