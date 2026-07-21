package miuix.navigator.navigation.internal;

import android.content.Context;
import miuix.appcompat.view.menu.MenuBuilder;
import miuix.appcompat.view.menu.MenuItemImpl;
import miuix.appcompat.view.menu.SubMenuBuilder;

/* JADX INFO: loaded from: classes3.dex */
public class NavigationSubMenu extends SubMenuBuilder {
    public NavigationSubMenu(Context context, NavigationMenu navigationMenu, MenuItemImpl menuItemImpl) {
        super(context, navigationMenu, menuItemImpl);
    }

    @Override // miuix.appcompat.view.menu.MenuBuilder
    public void onItemsChanged(boolean z) {
        super.onItemsChanged(z);
        ((MenuBuilder) getParentMenu()).onItemsChanged(z);
    }
}
