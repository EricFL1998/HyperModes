package miuix.appcompat.internal.view.menu.action;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import miuix.appcompat.R;
import miuix.appcompat.internal.app.widget.ActionBarOverlayLayout;
import miuix.appcompat.internal.view.menu.MenuBuilder;
import miuix.appcompat.internal.view.menu.MenuItemImpl;
import miuix.appcompat.internal.view.menu.MenuPopupHelper;
import miuix.appcompat.internal.view.menu.SubMenuBuilder;
import miuix.internal.util.ViewUtils;

/* JADX INFO: loaded from: classes2.dex */
public class EndActionMenuPresenter extends ActionMenuPresenter {
    private MenuItemImpl mMoreButtonItem;

    public EndActionMenuPresenter(Context context, ActionBarOverlayLayout actionBarOverlayLayout, int i, int i2) {
        this(context, actionBarOverlayLayout, i, i2, 0, 0);
    }

    public EndActionMenuPresenter(Context context, ActionBarOverlayLayout actionBarOverlayLayout, int i, int i2, int i3, int i4) {
        super(context, actionBarOverlayLayout, i, i2, i3, i4);
    }

    @Override // miuix.appcompat.internal.view.menu.action.ActionMenuPresenter
    protected int getDefaultMaxItemCount() {
        if (this.mContext != null) {
            return this.mContext.getResources().getInteger(R.integer.action_bar_end_menu_max_item_count);
        }
        return 5;
    }

    public void addBadgeOnMoreButton() {
        addBadgeOnMoreButton(2);
    }

    public void addBadgeOnMoreButton(int i) {
        MenuItemImpl menuItemImpl = this.mMoreButtonItem;
        if (menuItemImpl == null) {
            return;
        }
        menuItemImpl.setBadgeNeeded(true, i);
        updateBadgeOnMoreButton();
    }

    public void addNumberBadgeOnMoreButton(int i, int i2) {
        MenuItemImpl menuItemImpl = this.mMoreButtonItem;
        if (menuItemImpl == null) {
            return;
        }
        menuItemImpl.setBadgeNeeded(true, i2);
        updateBadgeOnMoreButton(i);
    }

    public void clearBadgeOnMoreButton() {
        MenuItemImpl menuItemImpl = this.mMoreButtonItem;
        if (menuItemImpl == null) {
            return;
        }
        menuItemImpl.setBadgeNeeded(false);
        updateBadgeOnMoreButton();
    }

    public void updateBadgeOnMoreButton() {
        MenuItemImpl menuItemImpl = this.mMoreButtonItem;
        if (menuItemImpl == null) {
            return;
        }
        menuItemImpl.setHandleExtraOffset(true);
        this.mMoreButtonItem.updateBadgeDrawable();
    }

    public void updateBadgeOnMoreButton(int i) {
        MenuItemImpl menuItemImpl = this.mMoreButtonItem;
        if (menuItemImpl == null) {
            return;
        }
        menuItemImpl.setHandleExtraOffset(true);
        this.mMoreButtonItem.updateBadgeDrawable(i);
    }

    @Override // miuix.appcompat.internal.view.menu.action.ActionMenuPresenter
    protected boolean isConvertViewTypeAllowed(View view) {
        if (view == null) {
            return false;
        }
        MenuItemImpl menuItemImpl = this.mMoreButtonItem;
        return (view instanceof EndActionMenuItemView) && !(menuItemImpl != null && menuItemImpl.getView() == view);
    }

    @Override // miuix.appcompat.internal.view.menu.action.ActionMenuPresenter
    protected View createOverflowMenuButton(Context context) {
        ViewGroup viewGroup = (ViewGroup) this.mMenuView;
        if (viewGroup == null) {
            return null;
        }
        MenuItemImpl menuItemImplCreateMenuItemImpl = createMenuItemImpl(this.mMenu, 0, R.id.more, 0, 0, context.getString(R.string.more), 2);
        this.mMenu.stopDispatchingItemsChanged();
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(new int[]{R.attr.endActionMoreButtonIcon});
        Drawable drawable = typedArrayObtainStyledAttributes.getDrawable(0);
        typedArrayObtainStyledAttributes.recycle();
        menuItemImplCreateMenuItemImpl.setIcon(drawable);
        menuItemImplCreateMenuItemImpl.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() { // from class: miuix.appcompat.internal.view.menu.action.EndActionMenuPresenter$$ExternalSyntheticLambda0
            @Override // android.view.MenuItem.OnMenuItemClickListener
            public final boolean onMenuItemClick(MenuItem menuItem) {
                return this.f$0.m1839x21a58d4(menuItem);
            }
        });
        this.mMenu.setPreventDispatchingItemsChanged(false);
        View itemView = getItemView(menuItemImplCreateMenuItemImpl, null, viewGroup);
        itemView.setId(R.id.more);
        this.mMoreButtonItem = menuItemImplCreateMenuItemImpl;
        menuItemImplCreateMenuItemImpl.setView(itemView);
        return itemView;
    }

    /* JADX INFO: renamed from: lambda$createOverflowMenuButton$0$miuix-appcompat-internal-view-menu-action-EndActionMenuPresenter, reason: not valid java name */
    /* synthetic */ boolean m1839x21a58d4(MenuItem menuItem) {
        if (this.mMenu != null) {
            dispatchMenuItemSelected(this.mMenu, this.mMenu.getRootMenu(), getOverflowMenuItem());
        }
        if (this.mOverflowButton.isSelected()) {
            hideOverflowMenu(true);
        } else {
            showOverflowMenu();
        }
        return true;
    }

    @Override // miuix.appcompat.internal.view.menu.action.ActionMenuPresenter
    protected int getOverflowMenuAnimationGravity(View view) {
        return ViewUtils.isLayoutRtl(view) ? 51 : 53;
    }

    @Override // miuix.appcompat.internal.view.menu.action.ActionMenuPresenter, miuix.appcompat.internal.view.menu.BaseMenuPresenter, miuix.appcompat.internal.view.menu.MenuPresenter
    public boolean onSubMenuSelected(SubMenuBuilder subMenuBuilder) {
        if (!subMenuBuilder.hasVisibleItems()) {
            return false;
        }
        new PopupSubMenu(this.mContext, subMenuBuilder, this.mOverflowButton, this.mDecorView, true).tryShow();
        return true;
    }

    private class PopupSubMenu extends MenuPopupHelper {
        public PopupSubMenu(Context context, MenuBuilder menuBuilder, View view, View view2, boolean z) {
            super(context, menuBuilder, view, view2, z);
            setCallback(EndActionMenuPresenter.this.mPopupPresenterCallback);
            setMenuItemLayout(R.layout.miuix_appcompat_overflow_popup_menu_item_layout);
        }

        @Override // miuix.appcompat.internal.view.menu.MenuPopupHelper, android.widget.PopupWindow.OnDismissListener
        public void onDismiss() {
            super.onDismiss();
            EndActionMenuPresenter.this.mMenu.close();
        }

        @Override // miuix.appcompat.internal.view.menu.MenuPopupHelper, miuix.appcompat.internal.view.menu.action.ActionMenuPresenter.OverflowMenu
        public void dismiss(boolean z) {
            super.dismiss(z);
            if (EndActionMenuPresenter.this.mOverflowButton != null) {
                EndActionMenuPresenter.this.mOverflowButton.setSelected(false);
            }
        }
    }
}
