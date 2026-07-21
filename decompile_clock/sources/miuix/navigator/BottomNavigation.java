package miuix.navigator;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import miuix.navigator.navigatorinfo.NavigatorInfo;

/* JADX INFO: loaded from: classes3.dex */
public interface BottomNavigation {
    public static final int LAYOUT_STYLE_COMPACT = 0;
    public static final int LAYOUT_STYLE_MASK_WIDE = 1;
    public static final int LAYOUT_STYLE_WIDE_LAND = 3;
    public static final int LAYOUT_STYLE_WIDE_PORT = 1;

    public interface OnItemSelectedListener {
        boolean onNavigationItemSelected(MenuItem menuItem, NavigatorInfo navigatorInfo);
    }

    Menu getMenu();

    View getView();

    void inflateMenu(int i);

    void setLayoutStyle(int i);
}
