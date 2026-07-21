package miuix.navigator.bottomnavigation;

import android.content.Context;
import androidx.appcompat.view.menu.MenuBuilder;
import miuix.navigator.R;
import miuix.navigator.navigation.NavigationBarItemView;

/* JADX INFO: loaded from: classes3.dex */
public class BottomNavigationItemView extends NavigationBarItemView {
    public void setItemInvoker(MenuBuilder.ItemInvoker itemInvoker) {
    }

    public BottomNavigationItemView(Context context) {
        super(context);
    }

    public BottomNavigationItemView(Context context, int i) {
        super(context, i);
    }

    @Override // miuix.navigator.navigation.NavigationBarItemView
    protected int getItemLayoutResId() {
        return R.layout.miuix_design_bottom_navigation_item;
    }

    @Override // miuix.navigator.navigation.NavigationBarItemView
    protected int getItemDefaultMarginResId() {
        return R.dimen.miuix_design_bottom_navigation_margin;
    }
}
