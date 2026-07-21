package androidx.core.widget;

import android.widget.ListView;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public final class ListViewCompat {
    @Deprecated
    public static void scrollListBy(ListView listView, int i) {
        listView.scrollListBy(i);
    }

    @Deprecated
    public static boolean canScrollList(ListView listView, int i) {
        return listView.canScrollList(i);
    }

    private ListViewCompat() {
    }
}
