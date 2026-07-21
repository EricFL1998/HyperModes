package miuix.responsive.wrapper;

import android.view.View;
import android.view.ViewGroup;

/* JADX INFO: loaded from: classes3.dex */
public class OnHierarchyChangeListenerWrapper implements ViewGroup.OnHierarchyChangeListener {
    private ViewGroup.OnHierarchyChangeListener mOriginOnHierarchyChangeListener;

    @Override // android.view.ViewGroup.OnHierarchyChangeListener
    public void onChildViewAdded(View view, View view2) {
        ViewGroup.OnHierarchyChangeListener onHierarchyChangeListener = this.mOriginOnHierarchyChangeListener;
        if (onHierarchyChangeListener != null) {
            onHierarchyChangeListener.onChildViewAdded(view, view2);
        }
    }

    @Override // android.view.ViewGroup.OnHierarchyChangeListener
    public void onChildViewRemoved(View view, View view2) {
        ViewGroup.OnHierarchyChangeListener onHierarchyChangeListener = this.mOriginOnHierarchyChangeListener;
        if (onHierarchyChangeListener != null) {
            onHierarchyChangeListener.onChildViewRemoved(view, view2);
        }
    }

    public void setOnHierarchyChangeListener(ViewGroup.OnHierarchyChangeListener onHierarchyChangeListener) {
        this.mOriginOnHierarchyChangeListener = onHierarchyChangeListener;
    }
}
