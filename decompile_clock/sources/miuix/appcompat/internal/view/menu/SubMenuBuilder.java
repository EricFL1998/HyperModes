package miuix.appcompat.internal.view.menu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import miuix.appcompat.app.floatingactivity.multiapp.MethodCodeHelper;

/* JADX INFO: loaded from: classes2.dex */
public class SubMenuBuilder extends MenuBuilder implements SubMenu {
    private MenuItemImpl mItem;
    private MenuBuilder mParentMenu;

    @Override // miuix.appcompat.internal.view.menu.MenuBuilder, android.view.SubMenu
    public void clearHeader() {
    }

    public SubMenuBuilder(Context context, MenuBuilder menuBuilder, MenuItemImpl menuItemImpl) {
        super(context);
        this.mParentMenu = menuBuilder;
        this.mItem = menuItemImpl;
    }

    @Override // miuix.appcompat.internal.view.menu.MenuBuilder, android.view.Menu
    public void setQwertyMode(boolean z) {
        this.mParentMenu.setQwertyMode(z);
    }

    @Override // miuix.appcompat.internal.view.menu.MenuBuilder
    public boolean isQwertyMode() {
        return this.mParentMenu.isQwertyMode();
    }

    @Override // miuix.appcompat.internal.view.menu.MenuBuilder
    public void setShortcutsVisible(boolean z) {
        this.mParentMenu.setShortcutsVisible(z);
    }

    @Override // miuix.appcompat.internal.view.menu.MenuBuilder
    public boolean isShortcutsVisible() {
        return this.mParentMenu.isShortcutsVisible();
    }

    public void setParentMenu(MenuBuilder menuBuilder) {
        this.mParentMenu = menuBuilder;
    }

    public Menu getParentMenu() {
        return this.mParentMenu;
    }

    @Override // android.view.SubMenu
    public MenuItem getItem() {
        return this.mItem;
    }

    @Override // miuix.appcompat.internal.view.menu.MenuBuilder
    public void setCallback(MenuBuilder.Callback callback) {
        this.mParentMenu.setCallback(callback);
    }

    @Override // miuix.appcompat.internal.view.menu.MenuBuilder
    public MenuBuilder getRootMenu() {
        return this.mParentMenu;
    }

    @Override // miuix.appcompat.internal.view.menu.MenuBuilder
    public boolean dispatchMenuItemSelected(MenuBuilder menuBuilder, MenuItem menuItem) {
        return super.dispatchMenuItemSelected(menuBuilder, menuItem) || this.mParentMenu.dispatchMenuItemSelected(menuBuilder, menuItem);
    }

    @Override // android.view.SubMenu
    public SubMenu setIcon(Drawable drawable) {
        this.mItem.setIcon(drawable);
        return this;
    }

    @Override // android.view.SubMenu
    public SubMenu setIcon(int i) {
        this.mItem.setIcon(i);
        return this;
    }

    @Override // android.view.SubMenu
    public SubMenu setHeaderIcon(Drawable drawable) {
        super.setHeaderIconInt(drawable);
        return this;
    }

    @Override // android.view.SubMenu
    public SubMenu setHeaderIcon(int i) {
        super.setHeaderIconInt(getContext().getResources().getDrawable(i));
        return this;
    }

    @Override // android.view.SubMenu
    public SubMenu setHeaderTitle(CharSequence charSequence) {
        super.setHeaderTitleInt(charSequence);
        return this;
    }

    @Override // android.view.SubMenu
    public SubMenu setHeaderTitle(int i) {
        super.setHeaderTitleInt(getContext().getResources().getString(i));
        return this;
    }

    @Override // android.view.SubMenu
    public SubMenu setHeaderView(View view) {
        super.setHeaderViewInt(view);
        return this;
    }

    @Override // miuix.appcompat.internal.view.menu.MenuBuilder
    public boolean expandItemActionView(MenuItemImpl menuItemImpl) {
        return this.mParentMenu.expandItemActionView(menuItemImpl);
    }

    @Override // miuix.appcompat.internal.view.menu.MenuBuilder
    public boolean collapseItemActionView(MenuItemImpl menuItemImpl) {
        return this.mParentMenu.collapseItemActionView(menuItemImpl);
    }

    @Override // miuix.appcompat.internal.view.menu.MenuBuilder
    public String getActionViewStatesKey() {
        MenuItemImpl menuItemImpl = this.mItem;
        int itemId = menuItemImpl != null ? menuItemImpl.getItemId() : 0;
        if (itemId == 0) {
            return null;
        }
        return super.getActionViewStatesKey() + MethodCodeHelper.IDENTITY_INFO_SEPARATOR + itemId;
    }
}
