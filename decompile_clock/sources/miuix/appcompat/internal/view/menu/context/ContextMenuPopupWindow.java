package miuix.appcompat.internal.view.menu.context;

import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

/* JADX INFO: loaded from: classes2.dex */
public interface ContextMenuPopupWindow {
    void dismiss();

    void show(View view, ViewGroup viewGroup, float f, float f2);

    void update(Menu menu);

    void updatePopupWindow(View view, ViewGroup viewGroup, float f, float f2);
}
