package miuix.navigator.navigation.internal;

import android.content.Context;
import android.view.SubMenu;
import miuix.appcompat.view.menu.MenuBuilder;
import miuix.appcompat.view.menu.MenuItemImpl;

/* JADX INFO: loaded from: classes3.dex */
public final class NavigationMenu extends MenuBuilder {
    public NavigationMenu(Context context) {
        super(context);
    }

    @Override // miuix.appcompat.view.menu.MenuBuilder, android.view.Menu
    public SubMenu addSubMenu(int i, int i2, int i3, CharSequence charSequence) {
        MenuItemImpl menuItemImpl = (MenuItemImpl) addInternal(i, i2, i3, charSequence);
        NavigationSubMenu navigationSubMenu = new NavigationSubMenu(getContext(), this, menuItemImpl);
        menuItemImpl.setSubMenu(navigationSubMenu);
        return navigationSubMenu;
    }
}
