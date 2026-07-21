package miuix.appcompat.internal.view.menu.action;

import android.R;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Iterator;
import miuix.appcompat.internal.app.widget.ActionBarOverlayLayout;
import miuix.appcompat.internal.view.ActionBarPolicy;
import miuix.appcompat.internal.view.menu.BaseMenuPresenter;
import miuix.appcompat.internal.view.menu.ListMenuPresenter;
import miuix.appcompat.internal.view.menu.MenuBuilder;
import miuix.appcompat.internal.view.menu.MenuDialogHelper;
import miuix.appcompat.internal.view.menu.MenuItemImpl;
import miuix.appcompat.internal.view.menu.MenuPopupHelper;
import miuix.appcompat.internal.view.menu.MenuPresenter;
import miuix.appcompat.internal.view.menu.MenuView;
import miuix.appcompat.internal.view.menu.SubMenuBuilder;
import miuix.internal.util.AttributeResolver;

/* JADX INFO: loaded from: classes2.dex */
public class ActionMenuPresenter extends BaseMenuPresenter {
    private boolean isMaxItemCountSet;
    private final SparseBooleanArray mActionButtonGroups;
    private ActionButtonSubMenu mActionButtonPopup;
    private View mBottomMenuCustomView;
    protected ActionBarOverlayLayout mDecorView;
    private boolean mExpandedActionViewsExclusive;
    private int mListItemLayoutRes;
    private int mListLayoutRes;
    private OverflowMenu mListOverflowMenu;
    private int mMaxItems;
    int mOpenSubMenuId;
    protected View mOverflowButton;
    protected OverflowMenu mOverflowMenu;
    private int mOverflowMenuAttrs;
    private MenuItemImpl mOverflowMenuItem;
    final PopupPresenterCallback mPopupPresenterCallback;
    private OpenOverflowRunnable mPostedOpenRunnable;
    private boolean mReserveOverflow;
    private boolean mReserveOverflowSet;
    private View mScrapActionButtonView;
    private boolean mStrictWidthLimit;
    private boolean mWidthLimitSet;

    protected interface OverflowMenu {
        void dismiss(boolean z);

        boolean isShowing();

        boolean tryShow();

        void update(MenuBuilder menuBuilder);
    }

    protected int getOverflowMenuAnimationGravity(View view) {
        return -1;
    }

    public ActionMenuPresenter(Context context, ActionBarOverlayLayout actionBarOverlayLayout, int i, int i2) {
        this(context, actionBarOverlayLayout, i, i2, 0, 0);
    }

    public ActionMenuPresenter(Context context, ActionBarOverlayLayout actionBarOverlayLayout, int i, int i2, int i3, int i4) {
        super(context, i, i2);
        this.mOverflowMenuAttrs = R.attr.actionOverflowButtonStyle;
        this.mActionButtonGroups = new SparseBooleanArray();
        this.mPopupPresenterCallback = new PopupPresenterCallback();
        this.mListLayoutRes = i3;
        this.mListItemLayoutRes = i4;
        this.mDecorView = actionBarOverlayLayout;
    }

    @Override // miuix.appcompat.internal.view.menu.BaseMenuPresenter, miuix.appcompat.internal.view.menu.MenuPresenter
    public void initForMenu(Context context, MenuBuilder menuBuilder) {
        super.initForMenu(context, menuBuilder);
        context.getResources();
        ActionBarPolicy actionBarPolicy = ActionBarPolicy.get(context);
        if (!this.mReserveOverflowSet) {
            this.mReserveOverflow = actionBarPolicy.showsOverflowMenuButton();
        }
        if (!this.isMaxItemCountSet) {
            this.mMaxItems = getDefaultMaxItemCount();
        }
        if (this.mReserveOverflow) {
            if (this.mOverflowButton == null) {
                this.mOverflowButton = createOverflowMenuButton(this.mSystemContext);
            }
        } else {
            this.mOverflowButton = null;
        }
        this.mScrapActionButtonView = null;
    }

    public void onConfigurationChanged(Configuration configuration) {
        if (!this.isMaxItemCountSet && this.mContext != null) {
            this.mMaxItems = getDefaultMaxItemCount();
        }
        if (this.mMenu != null) {
            notifyItemsChanged(this.mMenu, true);
        }
    }

    protected int getDefaultMaxItemCount() {
        if (this.mContext != null) {
            return AttributeResolver.resolveInt(this.mContext, miuix.appcompat.R.attr.actionMenuItemLimit, 5);
        }
        return 5;
    }

    public void setWidthLimit(int i, boolean z) {
        this.mStrictWidthLimit = z;
        this.mWidthLimitSet = true;
    }

    public void setReserveOverflow(boolean z) {
        this.mReserveOverflow = z;
        this.mReserveOverflowSet = true;
    }

    public void setItemLimit(int i) {
        this.isMaxItemCountSet = true;
        int i2 = this.mMaxItems;
        this.mMaxItems = i;
        if (this.mMenu == null || i2 == i) {
            return;
        }
        this.mMenu.updateVisibleItemCountLimit();
    }

    public void setExpandedActionViewsExclusive(boolean z) {
        this.mExpandedActionViewsExclusive = z;
    }

    public void setActionEditMode(boolean z) {
        if (z) {
            this.mOverflowMenuAttrs = miuix.appcompat.R.attr.actionModeOverflowButtonStyle;
        }
    }

    public void addBadgeOnItemView(int i) {
        addBadgeOnItemView(i, 2);
    }

    public void addBadgeOnItemView(int i, int i2) {
        addBadgeOnItemView(this.mMenu.findItem(i), i2);
    }

    public void addBadgeOnItemView(MenuItem menuItem) {
        addBadgeOnItemView(menuItem, 2);
    }

    public void addBadgeOnItemView(MenuItem menuItem, int i) {
        if (menuItem instanceof MenuItemImpl) {
            MenuItemImpl menuItemImpl = (MenuItemImpl) menuItem;
            menuItemImpl.setBadgeNeeded(true, i);
            updateBadgeOnItemView(menuItemImpl);
        }
    }

    public void addNumberBadgeOnItemView(int i, int i2, int i3) {
        MenuItem menuItemFindItem = this.mMenu.findItem(i);
        if (menuItemFindItem instanceof MenuItemImpl) {
            MenuItemImpl menuItemImpl = (MenuItemImpl) menuItemFindItem;
            menuItemImpl.setBadgeNeeded(true, i3);
            updateBadgeOnItemView(menuItemImpl, i2);
        }
    }

    public void clearBadgeOnItemView(int i) {
        clearBadgeOnItemView(this.mMenu.findItem(i));
    }

    public void clearBadgeOnItemView(MenuItem menuItem) {
        if (menuItem instanceof MenuItemImpl) {
            MenuItemImpl menuItemImpl = (MenuItemImpl) menuItem;
            menuItemImpl.setBadgeNeeded(false);
            updateBadgeOnItemView(menuItemImpl);
        }
    }

    public void updateBadgeOnItemView(MenuItemImpl menuItemImpl) {
        if (menuItemImpl.isVisible()) {
            menuItemImpl.updateBadgeDrawable();
        }
    }

    public void updateBadgeOnItemView(MenuItemImpl menuItemImpl, int i) {
        if (menuItemImpl.isVisible()) {
            menuItemImpl.updateBadgeDrawable(i);
        }
    }

    public void updateBadgeOnItemViews() {
        Iterator<MenuItemImpl> it = this.mMenu.getVisibleItems().iterator();
        while (it.hasNext()) {
            it.next().updateBadgeDrawable();
        }
    }

    @Override // miuix.appcompat.internal.view.menu.BaseMenuPresenter, miuix.appcompat.internal.view.menu.MenuPresenter
    public MenuView getMenuView(ViewGroup viewGroup) {
        MenuView menuView = super.getMenuView(viewGroup);
        ((ActionMenuView) menuView).setPresenter(this);
        View view = this.mBottomMenuCustomView;
        if (view != null && view.getParent() == null && (menuView instanceof ResponsiveActionMenuView)) {
            ((ResponsiveActionMenuView) menuView).addCustomView(this.mBottomMenuCustomView);
        }
        return menuView;
    }

    protected boolean isConvertViewTypeAllowed(View view) {
        return view instanceof ActionMenuItemView;
    }

    @Override // miuix.appcompat.internal.view.menu.BaseMenuPresenter
    public View getItemView(MenuItemImpl menuItemImpl, View view, ViewGroup viewGroup) {
        View actionView = menuItemImpl.getActionView();
        if (actionView == null || menuItemImpl.hasCollapsibleActionView()) {
            if (!isConvertViewTypeAllowed(view)) {
                view = null;
            }
            actionView = super.getItemView(menuItemImpl, view, viewGroup);
        }
        actionView.setVisibility(menuItemImpl.isActionViewExpanded() ? 8 : 0);
        ActionMenuView actionMenuView = (ActionMenuView) viewGroup;
        ViewGroup.LayoutParams layoutParams = actionView.getLayoutParams();
        if (!actionMenuView.checkLayoutParams(layoutParams)) {
            actionView.setLayoutParams(actionMenuView.generateLayoutParams(layoutParams));
        }
        return actionView;
    }

    @Override // miuix.appcompat.internal.view.menu.BaseMenuPresenter
    public void bindItemView(MenuItemImpl menuItemImpl, MenuView.ItemView itemView) {
        itemView.initialize(menuItemImpl, 0);
        itemView.setItemInvoker((MenuBuilder.ItemInvoker) this.mMenuView);
    }

    @Override // miuix.appcompat.internal.view.menu.BaseMenuPresenter
    public boolean shouldIncludeItem(int i, MenuItemImpl menuItemImpl) {
        return menuItemImpl.isActionButton();
    }

    @Override // miuix.appcompat.internal.view.menu.BaseMenuPresenter, miuix.appcompat.internal.view.menu.MenuPresenter
    public void updateMenuView(boolean z) {
        super.updateMenuView(z);
        if (this.mMenuView == null) {
            return;
        }
        ArrayList<MenuItemImpl> nonActionItems = this.mMenu != null ? this.mMenu.getNonActionItems() : null;
        boolean z2 = false;
        if (this.mReserveOverflow && nonActionItems != null) {
            int size = nonActionItems.size();
            if (size == 1) {
                z2 = !nonActionItems.get(0).isActionViewExpanded();
            } else if (size > 0) {
                z2 = true;
            }
        }
        if (z2) {
            View view = this.mOverflowButton;
            if (view == null) {
                this.mOverflowButton = createOverflowMenuButton(this.mSystemContext);
            } else {
                view.setTranslationY(0.0f);
            }
            ViewGroup viewGroup = (ViewGroup) this.mOverflowButton.getParent();
            if (viewGroup != this.mMenuView) {
                if (viewGroup != null) {
                    viewGroup.removeView(this.mOverflowButton);
                }
                ActionMenuView actionMenuView = (ActionMenuView) this.mMenuView;
                View view2 = this.mOverflowButton;
                actionMenuView.addView(view2, actionMenuView.generateOverflowButtonLayoutParams(view2));
            }
        } else {
            View view3 = this.mOverflowButton;
            if (view3 != null && view3.getParent() == this.mMenuView) {
                ((ViewGroup) this.mMenuView).removeView(this.mOverflowButton);
            }
        }
        ((ActionMenuView) this.mMenuView).setOverflowReserved(this.mReserveOverflow);
        if (shouldShowPopupOverflow()) {
            return;
        }
        getOverflowMenu().update(this.mMenu);
    }

    public void setBottomMenuCustomView(View view) {
        ViewGroup viewGroup;
        View view2 = this.mBottomMenuCustomView;
        if (view2 != null && view2 != view && (viewGroup = (ViewGroup) view2.getParent()) != null) {
            viewGroup.removeView(this.mBottomMenuCustomView);
        }
        this.mBottomMenuCustomView = view;
        if (view.getParent() == null && (this.mMenuView instanceof ResponsiveActionMenuView)) {
            ((ResponsiveActionMenuView) this.mMenuView).addCustomView(view);
        }
    }

    public void removeBottomMenuCustomView() {
        if (this.mBottomMenuCustomView != null) {
            if (this.mMenuView instanceof ResponsiveActionMenuView) {
                ((ResponsiveActionMenuView) this.mMenuView).removeCustomView();
            }
            this.mBottomMenuCustomView = null;
        }
    }

    @Override // miuix.appcompat.internal.view.menu.BaseMenuPresenter, miuix.appcompat.internal.view.menu.MenuPresenter
    public boolean onSubMenuSelected(SubMenuBuilder subMenuBuilder) {
        if (!subMenuBuilder.hasVisibleItems()) {
            return false;
        }
        SubMenuBuilder subMenuBuilder2 = subMenuBuilder;
        while (subMenuBuilder2.getParentMenu() != this.mMenu) {
            subMenuBuilder2 = (SubMenuBuilder) subMenuBuilder2.getParentMenu();
        }
        if (findViewForItem(subMenuBuilder2.getItem()) == null && this.mOverflowButton == null) {
            return false;
        }
        this.mOpenSubMenuId = subMenuBuilder.getItem().getItemId();
        ActionButtonSubMenu actionButtonSubMenu = new ActionButtonSubMenu(subMenuBuilder);
        this.mActionButtonPopup = actionButtonSubMenu;
        actionButtonSubMenu.show(null);
        super.onSubMenuSelected(subMenuBuilder);
        return true;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private View findViewForItem(MenuItem menuItem) {
        ViewGroup viewGroup = (ViewGroup) this.mMenuView;
        if (viewGroup == null) {
            return null;
        }
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            if ((childAt instanceof MenuView.ItemView) && ((MenuView.ItemView) childAt).getItemData() == menuItem) {
                return childAt;
            }
        }
        return null;
    }

    public boolean showOverflowMenu() {
        if (!this.mReserveOverflow || isOverflowMenuShowing() || this.mMenu == null || this.mMenuView == null || this.mPostedOpenRunnable != null || this.mOverflowButton == null) {
            return false;
        }
        this.mPostedOpenRunnable = new OpenOverflowRunnable(getOverflowMenu());
        ((View) this.mMenuView).post(this.mPostedOpenRunnable);
        super.onSubMenuSelected(null);
        this.mOverflowButton.setSelected(true);
        return true;
    }

    protected boolean shouldShowPopupOverflow() {
        View view = this.mOverflowButton;
        return (view == null || view.getParent() == null) ? false : true;
    }

    protected OverflowMenu getOverflowMenu() {
        if (shouldShowPopupOverflow()) {
            return new PopupOverflowMenu(this.mContext, this.mMenu, this.mOverflowButton, this.mDecorView, true);
        }
        if (this.mListOverflowMenu == null) {
            this.mListOverflowMenu = new ListOverflowMenu();
        }
        return this.mListOverflowMenu;
    }

    protected MenuItemImpl getOverflowMenuItem() {
        if (this.mOverflowMenuItem == null) {
            this.mOverflowMenuItem = createMenuItemImpl(this.mMenu, 0, miuix.appcompat.R.id.more, 0, 0, this.mContext.getString(miuix.appcompat.R.string.more), 0);
        }
        return this.mOverflowMenuItem;
    }

    public boolean hideOverflowMenu(boolean z) {
        if (this.mPostedOpenRunnable != null && this.mMenuView != null) {
            this.mOverflowButton.setSelected(false);
            ((View) this.mMenuView).removeCallbacks(this.mPostedOpenRunnable);
            this.mPostedOpenRunnable = null;
            return true;
        }
        OverflowMenu overflowMenu = this.mOverflowMenu;
        if (overflowMenu == null) {
            return false;
        }
        boolean zIsShowing = overflowMenu.isShowing();
        if (zIsShowing) {
            this.mOverflowButton.setSelected(false);
        }
        this.mOverflowMenu.dismiss(z);
        return zIsShowing;
    }

    public boolean dismissPopupMenus(boolean z) {
        return hideOverflowMenu(z);
    }

    public boolean hideSubMenus() {
        ActionButtonSubMenu actionButtonSubMenu = this.mActionButtonPopup;
        if (actionButtonSubMenu == null) {
            return false;
        }
        actionButtonSubMenu.dismiss();
        return true;
    }

    public boolean isOverflowMenuShowing() {
        OverflowMenu overflowMenu = this.mOverflowMenu;
        return overflowMenu != null && overflowMenu.isShowing();
    }

    public boolean isOverflowReserved() {
        return this.mReserveOverflow;
    }

    @Override // miuix.appcompat.internal.view.menu.BaseMenuPresenter, miuix.appcompat.internal.view.menu.MenuPresenter
    public boolean flagActionItems() {
        ArrayList<MenuItemImpl> visibleItems = this.mMenu.getVisibleItems();
        int size = visibleItems.size();
        int i = this.mMaxItems;
        if (i < size) {
            i--;
        }
        int i2 = 0;
        while (true) {
            boolean z = true;
            if (i2 >= size || i <= 0) {
                break;
            }
            MenuItemImpl menuItemImpl = visibleItems.get(i2);
            if (!menuItemImpl.requestsActionButton() && !menuItemImpl.requiresActionButton()) {
                z = false;
            }
            menuItemImpl.setIsActionButton(z);
            if (z) {
                i--;
            }
            i2++;
        }
        while (i2 < size) {
            visibleItems.get(i2).setIsActionButton(false);
            i2++;
        }
        return true;
    }

    @Override // miuix.appcompat.internal.view.menu.BaseMenuPresenter, miuix.appcompat.internal.view.menu.MenuPresenter
    public void onCloseMenu(MenuBuilder menuBuilder, boolean z) {
        dismissPopupMenus(true);
        super.onCloseMenu(menuBuilder, z);
    }

    @Override // miuix.appcompat.internal.view.menu.MenuPresenter
    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState();
        savedState.openSubMenuId = this.mOpenSubMenuId;
        return savedState;
    }

    @Override // miuix.appcompat.internal.view.menu.MenuPresenter
    public void onRestoreInstanceState(Parcelable parcelable) {
        MenuItem menuItemFindItem;
        SavedState savedState = (SavedState) parcelable;
        if (savedState.openSubMenuId <= 0 || (menuItemFindItem = this.mMenu.findItem(savedState.openSubMenuId)) == null) {
            return;
        }
        onSubMenuSelected((SubMenuBuilder) menuItemFindItem.getSubMenu());
    }

    public void onSubUiVisibilityChanged(boolean z) {
        if (z) {
            super.onSubMenuSelected(null);
        } else {
            close(this.mMenu, false);
        }
    }

    private static class SavedState implements Parcelable {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: miuix.appcompat.internal.view.menu.action.ActionMenuPresenter.SavedState.1
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        public int openSubMenuId;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        SavedState() {
        }

        SavedState(Parcel parcel) {
            this.openSubMenuId = parcel.readInt();
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.openSubMenuId);
        }
    }

    private class ListOverflowMenu implements OverflowMenu {
        private ListMenuPresenter mListMenuPresenter;

        private ListOverflowMenu() {
        }

        private ListMenuPresenter getListMenuPresenter(MenuBuilder menuBuilder) {
            if (this.mListMenuPresenter == null) {
                this.mListMenuPresenter = new ListMenuPresenter(ActionMenuPresenter.this.mContext, ActionMenuPresenter.this.mListLayoutRes, ActionMenuPresenter.this.mListItemLayoutRes);
            }
            menuBuilder.addMenuPresenter(this.mListMenuPresenter);
            return this.mListMenuPresenter;
        }

        public View getOverflowMenuView(MenuBuilder menuBuilder) {
            if (menuBuilder == null || menuBuilder.getNonActionItems().size() <= 0) {
                return null;
            }
            return (View) getListMenuPresenter(menuBuilder).getMenuView((ViewGroup) ActionMenuPresenter.this.mMenuView);
        }

        @Override // miuix.appcompat.internal.view.menu.action.ActionMenuPresenter.OverflowMenu
        public boolean tryShow() {
            if (ActionMenuPresenter.this.mMenuView instanceof PhoneActionMenuView) {
                return ((PhoneActionMenuView) ActionMenuPresenter.this.mMenuView).showOverflowMenu(ActionMenuPresenter.this.mDecorView);
            }
            return false;
        }

        @Override // miuix.appcompat.internal.view.menu.action.ActionMenuPresenter.OverflowMenu
        public boolean isShowing() {
            if (ActionMenuPresenter.this.mMenuView instanceof PhoneActionMenuView) {
                return ((PhoneActionMenuView) ActionMenuPresenter.this.mMenuView).isOverflowMenuShowing();
            }
            return false;
        }

        @Override // miuix.appcompat.internal.view.menu.action.ActionMenuPresenter.OverflowMenu
        public void dismiss(boolean z) {
            if (ActionMenuPresenter.this.mMenuView instanceof PhoneActionMenuView) {
                ((PhoneActionMenuView) ActionMenuPresenter.this.mMenuView).hideOverflowMenu(ActionMenuPresenter.this.mDecorView);
            }
        }

        @Override // miuix.appcompat.internal.view.menu.action.ActionMenuPresenter.OverflowMenu
        public void update(MenuBuilder menuBuilder) {
            if (ActionMenuPresenter.this.mMenuView instanceof PhoneActionMenuView) {
                ((PhoneActionMenuView) ActionMenuPresenter.this.mMenuView).setOverflowMenuView(getOverflowMenuView(menuBuilder));
            }
        }
    }

    private class PopupOverflowMenu extends MenuPopupHelper implements OverflowMenu {
        @Override // miuix.appcompat.internal.view.menu.action.ActionMenuPresenter.OverflowMenu
        public void update(MenuBuilder menuBuilder) {
        }

        public PopupOverflowMenu(Context context, MenuBuilder menuBuilder, View view, View view2, boolean z) {
            int iComplexToDimensionPixelSize;
            super(context, menuBuilder, view, view2, z);
            TypedValue typedValueResolveTypedValue = AttributeResolver.resolveTypedValue(context, miuix.appcompat.R.attr.overflowMenuMaxHeight);
            if (typedValueResolveTypedValue == null || typedValueResolveTypedValue.type != 5) {
                iComplexToDimensionPixelSize = 0;
            } else if (typedValueResolveTypedValue.resourceId > 0) {
                iComplexToDimensionPixelSize = context.getResources().getDimensionPixelSize(typedValueResolveTypedValue.resourceId);
            } else {
                iComplexToDimensionPixelSize = TypedValue.complexToDimensionPixelSize(typedValueResolveTypedValue.data, context.getResources().getDisplayMetrics());
            }
            if (iComplexToDimensionPixelSize > 0) {
                setPopupMaxHeight(iComplexToDimensionPixelSize);
            }
            setCallback(ActionMenuPresenter.this.mPopupPresenterCallback);
            setMenuItemLayout(miuix.appcompat.R.layout.miuix_appcompat_overflow_popup_menu_item_layout);
            int overflowMenuAnimationGravity = ActionMenuPresenter.this.getOverflowMenuAnimationGravity(view);
            if (overflowMenuAnimationGravity != -1) {
                setAnimationGravity(overflowMenuAnimationGravity);
            }
        }

        @Override // miuix.appcompat.internal.view.menu.MenuPopupHelper, android.widget.PopupWindow.OnDismissListener
        public void onDismiss() {
            super.onDismiss();
            ActionMenuPresenter.this.mMenu.close();
            ActionMenuPresenter.this.mOverflowMenu = null;
        }

        @Override // miuix.appcompat.internal.view.menu.MenuPopupHelper, miuix.appcompat.internal.view.menu.action.ActionMenuPresenter.OverflowMenu
        public void dismiss(boolean z) {
            super.dismiss(z);
            if (ActionMenuPresenter.this.mOverflowButton != null) {
                ActionMenuPresenter.this.mOverflowButton.setSelected(false);
            }
        }
    }

    private class ActionButtonSubMenu extends MenuDialogHelper {
        public ActionButtonSubMenu(SubMenuBuilder subMenuBuilder) {
            super(subMenuBuilder);
            ActionMenuPresenter.this.setCallback(ActionMenuPresenter.this.mPopupPresenterCallback);
        }

        @Override // miuix.appcompat.internal.view.menu.MenuDialogHelper, android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialogInterface) {
            super.onDismiss(dialogInterface);
            ActionMenuPresenter.this.mActionButtonPopup = null;
            ActionMenuPresenter.this.mOpenSubMenuId = 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    class PopupPresenterCallback implements MenuPresenter.Callback {
        private PopupPresenterCallback() {
        }

        @Override // miuix.appcompat.internal.view.menu.MenuPresenter.Callback
        public boolean onOpenSubMenu(MenuBuilder menuBuilder) {
            if (menuBuilder == null) {
                return false;
            }
            ActionMenuPresenter.this.mOpenSubMenuId = ((SubMenuBuilder) menuBuilder).getItem().getItemId();
            return false;
        }

        @Override // miuix.appcompat.internal.view.menu.MenuPresenter.Callback
        public void onCloseMenu(MenuBuilder menuBuilder, boolean z) {
            if (menuBuilder instanceof SubMenuBuilder) {
                ActionMenuPresenter.close(menuBuilder.getRootMenu(), false);
            }
        }
    }

    private class OpenOverflowRunnable implements Runnable {
        private OverflowMenu mPopup;

        public OpenOverflowRunnable(OverflowMenu overflowMenu) {
            this.mPopup = overflowMenu;
        }

        @Override // java.lang.Runnable
        public void run() {
            View view = (View) ActionMenuPresenter.this.mMenuView;
            if (view != null && view.getWindowToken() != null && this.mPopup.tryShow()) {
                ActionMenuPresenter.this.mOverflowMenu = this.mPopup;
            }
            ActionMenuPresenter.this.mPostedOpenRunnable = null;
        }
    }

    protected View createOverflowMenuButton(Context context) {
        OverflowMenuButton overflowMenuButton = new OverflowMenuButton(context, null, this.mOverflowMenuAttrs);
        overflowMenuButton.setOnOverflowMenuButtonClickListener(new OverflowMenuButton.OnOverflowMenuButtonClickListener() { // from class: miuix.appcompat.internal.view.menu.action.ActionMenuPresenter$$ExternalSyntheticLambda0
            @Override // miuix.appcompat.internal.view.menu.action.OverflowMenuButton.OnOverflowMenuButtonClickListener
            public final void onOverflowMenuButtonClick() {
                this.f$0.m1838x1b5dd6b7();
            }
        });
        return overflowMenuButton;
    }

    /* JADX INFO: renamed from: lambda$createOverflowMenuButton$0$miuix-appcompat-internal-view-menu-action-ActionMenuPresenter, reason: not valid java name */
    /* synthetic */ void m1838x1b5dd6b7() {
        if (this.mMenu != null) {
            dispatchMenuItemSelected(this.mMenu, this.mMenu.getRootMenu(), getOverflowMenuItem());
        }
        if (this.mOverflowButton.isSelected()) {
            hideOverflowMenu(true);
        } else {
            showOverflowMenu();
        }
    }
}
