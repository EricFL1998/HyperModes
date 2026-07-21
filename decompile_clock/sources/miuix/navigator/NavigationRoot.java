package miuix.navigator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.DragEvent;
import miuix.springback.view.SpringBackLayout;

/* JADX INFO: loaded from: classes3.dex */
public class NavigationRoot extends SpringBackLayout {
    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchSetPressed(boolean z) {
    }

    public NavigationRoot(Context context) {
        this(context, null);
    }

    public NavigationRoot(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // android.view.View
    public boolean onDragEvent(DragEvent dragEvent) {
        return dragEvent.getAction() == 1;
    }
}
