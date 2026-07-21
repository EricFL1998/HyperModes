package miuix.navigator;

import android.view.MenuItem;

/* JADX INFO: loaded from: classes3.dex */
public interface NavigatorFragmentListener {
    default void onBottomNavigationDestroyed() {
    }

    default void onBottomNavigationPrepared() {
    }

    default void onBottomNavigationVisibilityChanged(boolean z, int i) {
    }

    default void onContentVisibilityChanged(int i) {
    }

    default void onNavigationMenuSelected(MenuItem menuItem) {
    }

    default void onNavigationVisibilityChanged(int i) {
    }

    default void onNavigatorModeChanged(Navigator.Mode mode, Navigator.Mode mode2) {
    }

    default void onSecondaryContentVisibilityChanged(int i) {
    }

    static String visibilityToString(int i) {
        StringBuilder sb = new StringBuilder();
        int i2 = i & 3;
        if (i2 == 0) {
            sb.append("invisible");
        } else if (i2 == 1) {
            sb.append("partial visible");
        } else if (i2 == 2) {
            sb.append("fully visible");
        }
        if ((i & 4) != 0) {
            sb.append("(masked)");
        }
        return sb.toString();
    }
}
