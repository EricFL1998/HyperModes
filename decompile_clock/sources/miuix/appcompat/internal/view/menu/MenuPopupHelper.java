package miuix.appcompat.internal.view.menu;

import android.content.Context;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.PopupWindow;
import java.util.ArrayList;
import miuix.appcompat.R;
import miuix.internal.util.AnimHelper;
import miuix.internal.util.TaggingDrawableUtil;

/* JADX INFO: loaded from: classes2.dex */
public class MenuPopupHelper implements AdapterView.OnItemClickListener, View.OnKeyListener, PopupWindow.OnDismissListener, MenuPresenter {
    private static final int ITEM_LAYOUT = R.layout.miuix_appcompat_popup_menu_item_layout;
    private MenuAdapter mAdapter;
    private View mAnchorView;
    private Context mContext;
    private View mFenceDecor;
    boolean mForceShowIcon;
    private LayoutInflater mInflater;
    private MenuBuilder mMenu;
    private int mMenuItemLayout;
    private boolean mOverflowOnly;
    private miuix.popupwidget.widget.PopupWindow mPopup;
    private int mPopupAnimationGravity;
    private int mPopupHorizontalOffset;
    private int mPopupMaxHeight;
    private int mPopupVerticalOffset;
    private MenuPresenter.Callback mPresenterCallback;

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

    @Override // miuix.appcompat.internal.view.menu.MenuPresenter
    public int getId() {
        return 0;
    }

    @Override // miuix.appcompat.internal.view.menu.MenuPresenter
    public void initForMenu(Context context, MenuBuilder menuBuilder) {
    }

    @Override // miuix.appcompat.internal.view.menu.MenuPresenter
    public void onRestoreInstanceState(Parcelable parcelable) {
    }

    @Override // miuix.appcompat.internal.view.menu.MenuPresenter
    public Parcelable onSaveInstanceState() {
        return null;
    }

    public MenuPopupHelper(Context context, MenuBuilder menuBuilder) {
        this(context, menuBuilder, null, false);
    }

    public MenuPopupHelper(Context context, MenuBuilder menuBuilder, View view) {
        this(context, menuBuilder, view, false);
    }

    public MenuPopupHelper(Context context, MenuBuilder menuBuilder, View view, boolean z) {
        this(context, menuBuilder, view, null, z);
    }

    public MenuPopupHelper(Context context, MenuBuilder menuBuilder, View view, View view2, boolean z) {
        this.mMenuItemLayout = ITEM_LAYOUT;
        this.mPopupHorizontalOffset = 0;
        this.mPopupAnimationGravity = -1;
        this.mPopupMaxHeight = 0;
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mMenu = menuBuilder;
        this.mOverflowOnly = z;
        this.mAnchorView = view;
        this.mFenceDecor = view2;
        menuBuilder.addMenuPresenter(this);
    }

    public void setAnimationGravity(int i) {
        this.mPopupAnimationGravity = i;
    }

    public void setAnchorView(View view) {
        this.mAnchorView = view;
    }

    public void setFenceDecor(View view) {
        this.mFenceDecor = view;
    }

    public void setForceShowIcon(boolean z) {
        this.mForceShowIcon = z;
    }

    public void setVerticalOffset(int i) {
        this.mPopupVerticalOffset = i;
    }

    public void setPopupMaxHeight(int i) {
        this.mPopupMaxHeight = i;
    }

    public void setMenuItemLayout(int i) {
        this.mMenuItemLayout = i;
    }

    public void show() {
        if (!tryShow()) {
            throw new IllegalStateException("MenuPopupHelper cannot be used without an anchor");
        }
    }

    public boolean tryShow() {
        miuix.popupwidget.widget.PopupWindow popupWindow = new miuix.popupwidget.widget.PopupWindow(this.mContext, this.mFenceDecor);
        this.mPopup = popupWindow;
        popupWindow.setDropDownGravity(81);
        this.mPopup.setOnDismissListener(this);
        this.mPopup.setOnItemClickListener(this);
        MenuAdapter menuAdapter = new MenuAdapter(this.mMenu);
        this.mAdapter = menuAdapter;
        this.mPopup.setAdapter(menuAdapter);
        this.mPopup.setHorizontalOffset(this.mPopupHorizontalOffset);
        this.mPopup.setVerticalOffset(this.mPopupVerticalOffset);
        int i = this.mPopupMaxHeight;
        if (i > 0) {
            this.mPopup.setMaxAllowedHeight(i);
        }
        if (!this.mPopup.prepareShow(this.mAnchorView)) {
            return true;
        }
        this.mPopup.showAsDropDown(this.mAnchorView, 81);
        this.mPopup.getListView().setOnKeyListener(this);
        return true;
    }

    public void dismiss(boolean z) {
        if (isShowing()) {
            this.mPopup.dismiss();
        }
    }

    @Override // android.widget.PopupWindow.OnDismissListener
    public void onDismiss() {
        this.mPopup = null;
        this.mMenu.close();
    }

    public boolean isShowing() {
        miuix.popupwidget.widget.PopupWindow popupWindow = this.mPopup;
        return (popupWindow == null || !popupWindow.isShowing() || this.mPopup.isInDismissAnimation()) ? false : true;
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        MenuAdapter menuAdapter = this.mAdapter;
        menuAdapter.mAdapterMenu.performItemAction(menuAdapter.getItem(i), 0);
    }

    @Override // android.view.View.OnKeyListener
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (keyEvent.getAction() != 1 || i != 82) {
            return false;
        }
        dismiss(false);
        return true;
    }

    @Override // miuix.appcompat.internal.view.menu.MenuPresenter
    public MenuView getMenuView(ViewGroup viewGroup) {
        throw new UnsupportedOperationException("MenuPopupHelpers manage their own views");
    }

    @Override // miuix.appcompat.internal.view.menu.MenuPresenter
    public void updateMenuView(boolean z) {
        MenuAdapter menuAdapter = this.mAdapter;
        if (menuAdapter != null) {
            menuAdapter.notifyDataSetChanged();
        }
    }

    @Override // miuix.appcompat.internal.view.menu.MenuPresenter
    public void setCallback(MenuPresenter.Callback callback) {
        this.mPresenterCallback = callback;
    }

    @Override // miuix.appcompat.internal.view.menu.MenuPresenter
    public boolean onSubMenuSelected(SubMenuBuilder subMenuBuilder) {
        boolean z;
        if (subMenuBuilder.hasVisibleItems()) {
            MenuPopupHelper menuPopupHelper = new MenuPopupHelper(this.mContext, subMenuBuilder, this.mAnchorView, this.mFenceDecor, false);
            menuPopupHelper.setCallback(this.mPresenterCallback);
            int size = subMenuBuilder.size();
            int i = 0;
            while (true) {
                if (i >= size) {
                    z = false;
                    break;
                }
                MenuItem item = subMenuBuilder.getItem(i);
                if (item.isVisible() && item.getIcon() != null) {
                    z = true;
                    break;
                }
                i++;
            }
            menuPopupHelper.setForceShowIcon(z);
            if (menuPopupHelper.tryShow()) {
                MenuPresenter.Callback callback = this.mPresenterCallback;
                if (callback != null) {
                    callback.onOpenSubMenu(subMenuBuilder);
                }
                return true;
            }
        }
        return false;
    }

    @Override // miuix.appcompat.internal.view.menu.MenuPresenter
    public void onCloseMenu(MenuBuilder menuBuilder, boolean z) {
        if (menuBuilder != this.mMenu) {
            return;
        }
        dismiss(true);
        MenuPresenter.Callback callback = this.mPresenterCallback;
        if (callback != null) {
            callback.onCloseMenu(menuBuilder, z);
        }
    }

    private class MenuAdapter extends BaseAdapter {
        private MenuBuilder mAdapterMenu;
        private int mExpandedIndex = -1;

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return i;
        }

        public MenuAdapter(MenuBuilder menuBuilder) {
            this.mAdapterMenu = menuBuilder;
            findExpandedIndex();
        }

        @Override // android.widget.Adapter
        public int getCount() {
            ArrayList<MenuItemImpl> nonActionItems = MenuPopupHelper.this.mOverflowOnly ? this.mAdapterMenu.getNonActionItems() : this.mAdapterMenu.getVisibleItems();
            if (this.mExpandedIndex < 0) {
                return nonActionItems.size();
            }
            return nonActionItems.size() - 1;
        }

        @Override // android.widget.Adapter
        public MenuItemImpl getItem(int i) {
            ArrayList<MenuItemImpl> nonActionItems = MenuPopupHelper.this.mOverflowOnly ? this.mAdapterMenu.getNonActionItems() : this.mAdapterMenu.getVisibleItems();
            int i2 = this.mExpandedIndex;
            if (i2 >= 0 && i >= i2) {
                i++;
            }
            return nonActionItems.get(i);
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = MenuPopupHelper.this.mInflater.inflate(MenuPopupHelper.this.mMenuItemLayout, viewGroup, false);
                AnimHelper.addItemPressEffect(view);
            }
            TaggingDrawableUtil.updateItemPadding(view, i, getCount());
            MenuView.ItemView itemView = (MenuView.ItemView) view;
            if (MenuPopupHelper.this.mForceShowIcon) {
                ((ListMenuItemView) view).setForceShowIcon(true);
            }
            itemView.initialize(getItem(i), 0);
            return view;
        }

        void findExpandedIndex() {
            MenuItemImpl expandedItem = MenuPopupHelper.this.mMenu.getExpandedItem();
            if (expandedItem != null) {
                ArrayList<MenuItemImpl> nonActionItems = MenuPopupHelper.this.mMenu.getNonActionItems();
                int size = nonActionItems.size();
                for (int i = 0; i < size; i++) {
                    if (nonActionItems.get(i) == expandedItem) {
                        this.mExpandedIndex = i;
                        return;
                    }
                }
            }
            this.mExpandedIndex = -1;
        }

        @Override // android.widget.BaseAdapter
        public void notifyDataSetChanged() {
            findExpandedIndex();
            super.notifyDataSetChanged();
        }
    }
}
