package miuix.navigator.navigation;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.MenuItem;
import android.view.SubMenu;
import miuix.appcompat.view.menu.MenuBuilder;
import miuix.appcompat.view.menu.MenuItemImpl;
import miuix.navigator.BottomTab;

/* JADX INFO: loaded from: classes3.dex */
public final class NavigationBarMenu extends MenuBuilder {
    public static final String EXTRA_BOTTOM_TAB_ID = "miuix.miracle:bottomTabId";
    private final int maxItemCount;
    private final Class<?> viewClass;

    public NavigationBarMenu(Context context, Class<?> cls, int i) {
        super(context);
        this.viewClass = cls;
        this.maxItemCount = i;
    }

    public int getMaxItemCount() {
        return this.maxItemCount;
    }

    @Override // miuix.appcompat.view.menu.MenuBuilder, android.view.Menu
    public SubMenu addSubMenu(int i, int i2, int i3, CharSequence charSequence) {
        throw new UnsupportedOperationException(this.viewClass.getSimpleName() + " does not support submenus");
    }

    @Override // miuix.appcompat.view.menu.MenuBuilder
    protected MenuItem addInternal(int i, int i2, int i3, CharSequence charSequence) {
        if (size() + 1 > this.maxItemCount) {
            String simpleName = this.viewClass.getSimpleName();
            throw new IllegalArgumentException("Maximum number of items supported by " + simpleName + " is " + this.maxItemCount + ". Limit can be checked with " + simpleName + "#getMaxItemCount()");
        }
        stopDispatchingItemsChanged();
        MenuItem menuItemAddInternal = super.addInternal(i, i2, i3, charSequence);
        if (menuItemAddInternal instanceof MenuItemImpl) {
            ((MenuItemImpl) menuItemAddInternal).setExclusiveCheckable(true);
        }
        startDispatchingItemsChanged();
        return menuItemAddInternal;
    }

    public MenuItem add(BottomTab bottomTab) {
        CharSequence title;
        if (size() + 1 > this.maxItemCount) {
            String simpleName = this.viewClass.getSimpleName();
            throw new IllegalArgumentException("Maximum number of items supported by " + simpleName + " is " + this.maxItemCount + ". Limit can be checked with " + simpleName + "#getMaxItemCount()");
        }
        stopDispatchingItemsChanged();
        int titleResId = bottomTab.getTitleResId();
        if (titleResId != 0) {
            title = getResources().getString(titleResId);
        } else {
            title = bottomTab.getTitle();
        }
        MenuItem menuItemAddInternal = super.addInternal(bottomTab.groupId, bottomTab.id, bottomTab.categoryOrder, title);
        int iconResId = bottomTab.getIconResId();
        Drawable iconDrawable = bottomTab.getIconDrawable();
        if (bottomTab.getIconResId() != 0) {
            menuItemAddInternal.setIcon(iconResId);
        } else if (iconDrawable != null) {
            menuItemAddInternal.setIcon(iconDrawable);
        }
        if (bottomTab.getIconTintMode() != null) {
            menuItemAddInternal.setIconTintMode(bottomTab.getIconTintMode());
        }
        if (bottomTab.getIconTintList() != null) {
            menuItemAddInternal.setIconTintList(bottomTab.getIconTintList());
        }
        if (bottomTab.getContentDescription() != null) {
            menuItemAddInternal.setContentDescription(bottomTab.getContentDescription());
        }
        if (bottomTab.getNavigatorInfo() != null) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_BOTTOM_TAB_ID, bottomTab.getNavigatorInfo().getNavigationId());
            menuItemAddInternal.setIntent(intent);
        }
        startDispatchingItemsChanged();
        return menuItemAddInternal;
    }
}
