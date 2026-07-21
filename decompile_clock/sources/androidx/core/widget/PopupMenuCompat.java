package androidx.core.widget;

import android.view.View;
import android.widget.PopupMenu;

/* JADX INFO: loaded from: classes.dex */
public final class PopupMenuCompat {
    private PopupMenuCompat() {
    }

    public static View.OnTouchListener getDragToOpenListener(Object obj) {
        return ((PopupMenu) obj).getDragToOpenListener();
    }
}
