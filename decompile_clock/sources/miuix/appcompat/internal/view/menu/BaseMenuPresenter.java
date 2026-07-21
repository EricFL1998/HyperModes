package miuix.appcompat.internal.view.menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import miuix.internal.util.AsyncInflateLayoutManager;

/* JADX INFO: loaded from: classes2.dex */
public abstract class BaseMenuPresenter implements MenuPresenter {
    private MenuPresenter.Callback mCallback;
    protected Context mContext;
    private int mId;
    protected LayoutInflater mInflater;
    private int mItemLayoutRes;
    protected MenuBuilder mMenu;
    private int mMenuLayoutRes;
    protected MenuView mMenuView;
    protected Context mSystemContext;
    protected LayoutInflater mSystemInflater;

    public abstract void bindItemView(MenuItemImpl menuItemImpl, MenuView.ItemView itemView);

    @Override // miuix.appcompat.internal.view.menu.MenuPresenter
    public boolean collapseItemActionView(MenuBuilder menuBuilder, MenuItemImpl menuItemImpl) {
        return false;
    }

    @Override // miuix.appcompat.internal.view.menu.MenuPresenter
    public boolean expandItemActionView(MenuBuilder menuBuilder, MenuItemImpl menuItemImpl) {
        return false;
    }

    @Override // miuix.appcompat.internal.view.menu.MenuPresenter
    public boolean flagActionItems() {
        return false;
    }

    public boolean shouldIncludeItem(int i, MenuItemImpl menuItemImpl) {
        return true;
    }

    public BaseMenuPresenter(Context context, int i, int i2) {
        this.mSystemContext = context;
        this.mSystemInflater = LayoutInflater.from(context);
        this.mMenuLayoutRes = i;
        this.mItemLayoutRes = i2;
    }

    @Override // miuix.appcompat.internal.view.menu.MenuPresenter
    public void initForMenu(Context context, MenuBuilder menuBuilder) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mMenu = menuBuilder;
    }

    @Override // miuix.appcompat.internal.view.menu.MenuPresenter
    public MenuView getMenuView(ViewGroup viewGroup) {
        if (this.mMenuView == null) {
            MenuView menuView = (MenuView) this.mSystemInflater.inflate(this.mMenuLayoutRes, viewGroup, false);
            this.mMenuView = menuView;
            menuView.initialize(this.mMenu);
            updateMenuView(true);
        }
        return this.mMenuView;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v1, types: [android.view.ViewGroup] */
    /* JADX WARN: Type inference failed for: r1v2, types: [miuix.appcompat.internal.view.menu.MenuView] */
    /* JADX WARN: Type inference failed for: r3v1, types: [android.view.View] */
    /* JADX WARN: Type inference failed for: r6v0, types: [miuix.appcompat.internal.view.menu.BaseMenuPresenter] */
    /* JADX WARN: Type inference failed for: r7v1, types: [miuix.appcompat.internal.view.menu.MenuView] */
    /* JADX WARN: Type inference failed for: r7v2, types: [boolean] */
    /* JADX WARN: Type inference failed for: r7v3 */
    /* JADX WARN: Type inference failed for: r7v4, types: [int] */
    /* JADX WARN: Type inference failed for: r7v5 */
    /* JADX WARN: Type inference failed for: r7v6, types: [int] */
    /* JADX WARN: Type inference failed for: r7v7, types: [int] */
    /* JADX WARN: Type inference failed for: r7v8 */
    /* JADX WARN: Type inference failed for: r7v9, types: [int] */
    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    @Override // miuix.appcompat.internal.view.menu.MenuPresenter
    public void updateMenuView(boolean z) {
        ?? r7 = this.mMenuView;
        ?? r0 = (ViewGroup) r7;
        if (r0 == 0) {
            return;
        }
        ?? HasBackgroundView = r7.hasBackgroundView();
        MenuBuilder menuBuilder = this.mMenu;
        if (menuBuilder != null) {
            menuBuilder.flagActionItems();
            for (MenuItemImpl menuItemImpl : this.mMenu.getVisibleItems()) {
                if (menuItemImpl != null && shouldIncludeItem(HasBackgroundView, menuItemImpl)) {
                    ?? childAt = r0.getChildAt(HasBackgroundView);
                    MenuItemImpl itemData = childAt instanceof MenuView.ItemView ? ((MenuView.ItemView) childAt).getItemData() : null;
                    View itemView = getItemView(menuItemImpl, childAt, r0);
                    if (menuItemImpl != itemData) {
                        itemView.setPressed(false);
                    }
                    if (itemView != childAt) {
                        addItemView(itemView, HasBackgroundView);
                    }
                    if (menuItemImpl != null) {
                        menuItemImpl.setView(itemView);
                        menuItemImpl.updateBadgeDrawable();
                    }
                    HasBackgroundView++;
                }
            }
        }
        while (HasBackgroundView < r0.getChildCount()) {
            if (!this.mMenuView.filterLeftoverView(HasBackgroundView)) {
                HasBackgroundView++;
            }
        }
    }

    protected void addItemView(View view, int i) {
        ViewGroup viewGroup = (ViewGroup) view.getParent();
        if (viewGroup != null) {
            viewGroup.removeView(view);
        }
        ((ViewGroup) this.mMenuView).addView(view, i);
    }

    @Override // miuix.appcompat.internal.view.menu.MenuPresenter
    public void setCallback(MenuPresenter.Callback callback) {
        this.mCallback = callback;
    }

    public MenuView.ItemView createItemView(ViewGroup viewGroup) {
        return (MenuView.ItemView) AsyncInflateLayoutManager.getInstance().inflateViewById(Integer.valueOf(this.mItemLayoutRes), viewGroup, false);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public View getItemView(MenuItemImpl menuItemImpl, View view, ViewGroup viewGroup) {
        MenuView.ItemView itemViewCreateItemView;
        if (view instanceof MenuView.ItemView) {
            itemViewCreateItemView = (MenuView.ItemView) view;
        } else {
            itemViewCreateItemView = createItemView(viewGroup);
        }
        bindItemView(menuItemImpl, itemViewCreateItemView);
        return (View) itemViewCreateItemView;
    }

    @Override // miuix.appcompat.internal.view.menu.MenuPresenter
    public void onCloseMenu(MenuBuilder menuBuilder, boolean z) {
        MenuPresenter.Callback callback = this.mCallback;
        if (callback != null) {
            callback.onCloseMenu(menuBuilder, z);
        }
    }

    @Override // miuix.appcompat.internal.view.menu.MenuPresenter
    public boolean onSubMenuSelected(SubMenuBuilder subMenuBuilder) {
        MenuPresenter.Callback callback = this.mCallback;
        return callback != null && callback.onOpenSubMenu(subMenuBuilder);
    }

    @Override // miuix.appcompat.internal.view.menu.MenuPresenter
    public int getId() {
        return this.mId;
    }

    public void setId(int i) {
        this.mId = i;
    }

    protected static void notifyItemsChanged(MenuBuilder menuBuilder, boolean z) {
        menuBuilder.onItemsChanged(z);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void close(MenuBuilder menuBuilder, boolean z) {
        menuBuilder.closeInternal(z);
    }

    protected static boolean dispatchMenuItemSelected(MenuBuilder menuBuilder, MenuBuilder menuBuilder2, MenuItem menuItem) {
        return menuBuilder.dispatchMenuItemSelected(menuBuilder2, menuItem);
    }

    protected static MenuItemImpl createMenuItemImpl(MenuBuilder menuBuilder, int i, int i2, int i3, int i4, CharSequence charSequence, int i5) {
        return new MenuItemImpl(menuBuilder, i, i2, i3, i4, charSequence, i5);
    }
}
