package miuix.appcompat.internal.view.menu;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import miuix.appcompat.R;

/* JADX INFO: loaded from: classes2.dex */
public class MenuBuilder extends com.android.internal.view.menu.MenuBuilder {
    private static final String ACTION_VIEW_STATES_KEY = "android:menu:actionviewstates";
    public static final int CATEGORY_MASK = -65536;
    public static final int CATEGORY_SHIFT = 16;
    private static final String EXPANDED_ACTION_VIEW_ID = "android:menu:expandedactionview";
    private static final String PRESENTER_KEY = "android:menu:presenters";
    public static final int USER_MASK = 65535;
    private static final int[] sCategoryToOrder = {1, 4, 5, 3, 2, 0};
    private Callback mCallback;
    private final Context mContext;
    private ContextMenu.ContextMenuInfo mCurrentMenuInfo;
    private MenuItemImpl mExpandedItem;
    Drawable mHeaderIcon;
    CharSequence mHeaderTitle;
    View mHeaderView;
    private boolean mQwertyMode;
    private final Resources mResources;
    private boolean mShortcutsVisible;
    private int mDefaultShowAsAction = 0;
    private boolean mPreventDispatchingItemsChanged = false;
    private boolean mItemsChangedWhileDispatchPrevented = false;
    private boolean mOptionalIconsVisible = false;
    private boolean mIsClosing = false;
    private ArrayList<MenuItemImpl> mTempShortcutItemList = new ArrayList<>();
    private CopyOnWriteArrayList<WeakReference<MenuPresenter>> mPresenters = new CopyOnWriteArrayList<>();
    private final Comparator<MenuItemImpl> menuItemComparator = new Comparator<MenuItemImpl>() { // from class: miuix.appcompat.internal.view.menu.MenuBuilder.1
        @Override // java.util.Comparator
        public int compare(MenuItemImpl menuItemImpl, MenuItemImpl menuItemImpl2) {
            if (menuItemImpl.requiresActionButton() != menuItemImpl2.requiresActionButton()) {
                return menuItemImpl.requiresActionButton() ? -1 : 1;
            }
            if (menuItemImpl.requestsActionButton() != menuItemImpl2.requestsActionButton()) {
                return menuItemImpl.requestsActionButton() ? -1 : 1;
            }
            return menuItemImpl.getOrder() - menuItemImpl2.getOrder();
        }
    };
    private ArrayList<MenuItemImpl> mItems = new ArrayList<>();
    private ArrayList<MenuItemImpl> mVisibleItems = new ArrayList<>();
    private boolean mIsVisibleItemsStale = true;
    private ArrayList<MenuItemImpl> mActionItems = new ArrayList<>();
    private ArrayList<MenuItemImpl> mNonActionItems = new ArrayList<>();
    private boolean mIsActionItemsStale = true;

    public interface Callback {
        boolean onMenuItemSelected(MenuBuilder menuBuilder, MenuItem menuItem);

        void onMenuModeChange(MenuBuilder menuBuilder);
    }

    public interface ItemInvoker {
        boolean invokeItem(MenuItemImpl menuItemImpl, int i);
    }

    public MenuBuilder getRootMenu() {
        return this;
    }

    public MenuBuilder(Context context) {
        this.mContext = context;
        this.mResources = context.getResources();
        setShortcutsVisibleInner(true);
    }

    public MenuBuilder setDefaultShowAsAction(int i) {
        this.mDefaultShowAsAction = i;
        return this;
    }

    public void addMenuPresenter(MenuPresenter menuPresenter) {
        this.mPresenters.add(new WeakReference<>(menuPresenter));
        menuPresenter.initForMenu(this.mContext, this);
        this.mIsActionItemsStale = true;
    }

    public void removeMenuPresenter(MenuPresenter menuPresenter) {
        for (WeakReference<MenuPresenter> weakReference : this.mPresenters) {
            MenuPresenter menuPresenter2 = weakReference.get();
            if (menuPresenter2 == null || menuPresenter2 == menuPresenter) {
                this.mPresenters.remove(weakReference);
            }
        }
    }

    private void dispatchPresenterUpdate(boolean z) {
        if (this.mPresenters.isEmpty()) {
            return;
        }
        stopDispatchingItemsChanged();
        for (WeakReference<MenuPresenter> weakReference : this.mPresenters) {
            MenuPresenter menuPresenter = weakReference.get();
            if (menuPresenter == null) {
                this.mPresenters.remove(weakReference);
            } else {
                menuPresenter.updateMenuView(z);
            }
        }
        startDispatchingItemsChanged();
    }

    private boolean dispatchSubMenuSelected(SubMenuBuilder subMenuBuilder, MenuPresenter menuPresenter) {
        if (this.mPresenters.isEmpty()) {
            return false;
        }
        boolean zOnSubMenuSelected = menuPresenter != null ? menuPresenter.onSubMenuSelected(subMenuBuilder) : false;
        for (WeakReference<MenuPresenter> weakReference : this.mPresenters) {
            MenuPresenter menuPresenter2 = weakReference.get();
            if (menuPresenter2 == null) {
                this.mPresenters.remove(weakReference);
            } else if (!zOnSubMenuSelected) {
                zOnSubMenuSelected = menuPresenter2.onSubMenuSelected(subMenuBuilder);
            }
        }
        return zOnSubMenuSelected;
    }

    private void dispatchSaveInstanceState(Bundle bundle) {
        Parcelable parcelableOnSaveInstanceState;
        if (this.mPresenters.isEmpty()) {
            return;
        }
        SparseArray<? extends Parcelable> sparseArray = new SparseArray<>();
        for (WeakReference<MenuPresenter> weakReference : this.mPresenters) {
            MenuPresenter menuPresenter = weakReference.get();
            if (menuPresenter == null) {
                this.mPresenters.remove(weakReference);
            } else {
                int id = menuPresenter.getId();
                if (id > 0 && (parcelableOnSaveInstanceState = menuPresenter.onSaveInstanceState()) != null) {
                    sparseArray.put(id, parcelableOnSaveInstanceState);
                }
            }
        }
        bundle.putSparseParcelableArray(PRESENTER_KEY, sparseArray);
    }

    private void dispatchRestoreInstanceState(Bundle bundle) {
        Parcelable parcelable;
        SparseArray sparseParcelableArray = bundle.getSparseParcelableArray(PRESENTER_KEY);
        if (sparseParcelableArray == null || this.mPresenters.isEmpty()) {
            return;
        }
        for (WeakReference<MenuPresenter> weakReference : this.mPresenters) {
            MenuPresenter menuPresenter = weakReference.get();
            if (menuPresenter == null) {
                this.mPresenters.remove(weakReference);
            } else {
                int id = menuPresenter.getId();
                if (id > 0 && (parcelable = (Parcelable) sparseParcelableArray.get(id)) != null) {
                    menuPresenter.onRestoreInstanceState(parcelable);
                }
            }
        }
    }

    public void savePresenterStates(Bundle bundle) {
        dispatchSaveInstanceState(bundle);
    }

    public void restorePresenterStates(Bundle bundle) {
        dispatchRestoreInstanceState(bundle);
    }

    public void saveActionViewStates(Bundle bundle) {
        int size = size();
        SparseArray<? extends Parcelable> sparseArray = null;
        for (int i = 0; i < size; i++) {
            MenuItem item = getItem(i);
            View actionView = item.getActionView();
            if (actionView != null && actionView.getId() != -1) {
                if (sparseArray == null) {
                    sparseArray = new SparseArray<>();
                }
                actionView.saveHierarchyState(sparseArray);
                if (item.isActionViewExpanded()) {
                    bundle.putInt(EXPANDED_ACTION_VIEW_ID, item.getItemId());
                }
            }
            if (item.hasSubMenu()) {
                ((SubMenuBuilder) item.getSubMenu()).saveActionViewStates(bundle);
            }
        }
        if (sparseArray != null) {
            bundle.putSparseParcelableArray(getActionViewStatesKey(), sparseArray);
        }
    }

    public void restoreActionViewStates(Bundle bundle) {
        MenuItem menuItemFindItem;
        if (bundle == null) {
            return;
        }
        SparseArray<Parcelable> sparseParcelableArray = bundle.getSparseParcelableArray(getActionViewStatesKey());
        int size = size();
        for (int i = 0; i < size; i++) {
            MenuItem item = getItem(i);
            View actionView = item.getActionView();
            if (actionView != null && actionView.getId() != -1) {
                actionView.restoreHierarchyState(sparseParcelableArray);
            }
            if (item.hasSubMenu()) {
                ((SubMenuBuilder) item.getSubMenu()).restoreActionViewStates(bundle);
            }
        }
        int i2 = bundle.getInt(EXPANDED_ACTION_VIEW_ID);
        if (i2 <= 0 || (menuItemFindItem = findItem(i2)) == null) {
            return;
        }
        menuItemFindItem.expandActionView();
    }

    protected String getActionViewStatesKey() {
        return ACTION_VIEW_STATES_KEY;
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    public Callback getCallback() {
        return this.mCallback;
    }

    protected MenuItem addInternal(int i, int i2, int i3, CharSequence charSequence) {
        int ordering = getOrdering(i3);
        MenuItemImpl menuItemImpl = new MenuItemImpl(this, i, i2, i3, ordering, charSequence, this.mDefaultShowAsAction);
        ContextMenu.ContextMenuInfo contextMenuInfo = this.mCurrentMenuInfo;
        if (contextMenuInfo != null) {
            menuItemImpl.setMenuInfo(contextMenuInfo);
        }
        ArrayList<MenuItemImpl> arrayList = this.mItems;
        arrayList.add(findInsertIndex(arrayList, ordering), menuItemImpl);
        onItemsChanged(true);
        return menuItemImpl;
    }

    public MenuItem add(MenuItemImpl menuItemImpl) {
        int ordering = getOrdering(menuItemImpl.getOrder());
        ContextMenu.ContextMenuInfo contextMenuInfo = this.mCurrentMenuInfo;
        if (contextMenuInfo != null) {
            menuItemImpl.setMenuInfo(contextMenuInfo);
        }
        ArrayList<MenuItemImpl> arrayList = this.mItems;
        arrayList.add(findInsertIndex(arrayList, ordering), menuItemImpl);
        onItemsChanged(true);
        return menuItemImpl;
    }

    public MenuItem add(CharSequence charSequence) {
        return addInternal(0, 0, 0, charSequence);
    }

    public MenuItem add(int i) {
        return addInternal(0, 0, 0, this.mResources.getString(i));
    }

    public MenuItem add(int i, int i2, int i3, CharSequence charSequence) {
        return addInternal(i, i2, i3, charSequence);
    }

    public MenuItem add(int i, int i2, int i3, int i4) {
        return addInternal(i, i2, i3, this.mResources.getString(i4));
    }

    public SubMenu addSubMenu(CharSequence charSequence) {
        return addSubMenu(0, 0, 0, charSequence);
    }

    public SubMenu addSubMenu(int i) {
        return addSubMenu(0, 0, 0, this.mResources.getString(i));
    }

    public SubMenu addSubMenu(int i, int i2, int i3, CharSequence charSequence) {
        MenuItemImpl menuItemImpl = (MenuItemImpl) addInternal(i, i2, i3, charSequence);
        SubMenuBuilder subMenuBuilder = new SubMenuBuilder(this.mContext, this, menuItemImpl);
        menuItemImpl.setSubMenu(subMenuBuilder);
        return subMenuBuilder;
    }

    public SubMenu addSubMenu(int i, int i2, int i3, int i4) {
        return addSubMenu(i, i2, i3, this.mResources.getString(i4));
    }

    public int addIntentOptions(int i, int i2, int i3, ComponentName componentName, Intent[] intentArr, Intent intent, int i4, MenuItem[] menuItemArr) {
        PackageManager packageManager = this.mContext.getPackageManager();
        List<ResolveInfo> listQueryIntentActivityOptions = packageManager.queryIntentActivityOptions(componentName, intentArr, intent, 0);
        int size = listQueryIntentActivityOptions != null ? listQueryIntentActivityOptions.size() : 0;
        if ((i4 & 1) == 0) {
            removeGroup(i);
        }
        for (int i5 = 0; i5 < size; i5++) {
            ResolveInfo resolveInfo = listQueryIntentActivityOptions.get(i5);
            Intent intent2 = new Intent(resolveInfo.specificIndex < 0 ? intent : intentArr[resolveInfo.specificIndex]);
            intent2.setComponent(new ComponentName(resolveInfo.activityInfo.applicationInfo.packageName, resolveInfo.activityInfo.name));
            MenuItem intent3 = add(i, i2, i3, resolveInfo.loadLabel(packageManager)).setIcon(resolveInfo.loadIcon(packageManager)).setIntent(intent2);
            if (menuItemArr != null && resolveInfo.specificIndex >= 0) {
                menuItemArr[resolveInfo.specificIndex] = intent3;
            }
        }
        return size;
    }

    public void removeItem(int i) {
        removeItemAtInt(findItemIndex(i), true);
    }

    public void removeGroup(int i) {
        int iFindGroupIndex = findGroupIndex(i);
        if (iFindGroupIndex >= 0) {
            int size = this.mItems.size() - iFindGroupIndex;
            int i2 = 0;
            while (true) {
                int i3 = i2 + 1;
                if (i2 >= size || this.mItems.get(iFindGroupIndex).getGroupId() != i) {
                    break;
                }
                removeItemAtInt(iFindGroupIndex, false);
                i2 = i3;
            }
            onItemsChanged(true);
        }
    }

    private void removeItemAtInt(int i, boolean z) {
        if (i < 0 || i >= this.mItems.size()) {
            return;
        }
        this.mItems.remove(i);
        if (z) {
            onItemsChanged(true);
        }
    }

    public void removeItemAt(int i) {
        removeItemAtInt(i, true);
    }

    public void updateVisibleItemCountLimit() {
        onItemsChanged(true);
    }

    public void clearAll() {
        this.mPreventDispatchingItemsChanged = true;
        clear();
        clearHeader();
        this.mPreventDispatchingItemsChanged = false;
        this.mItemsChangedWhileDispatchPrevented = false;
        onItemsChanged(true);
    }

    public void clear() {
        MenuItemImpl menuItemImpl = this.mExpandedItem;
        if (menuItemImpl != null) {
            collapseItemActionView(menuItemImpl);
        }
        this.mItems.clear();
        onItemsChanged(true);
    }

    void setExclusiveItemChecked(MenuItem menuItem) {
        int groupId = menuItem.getGroupId();
        Iterator<MenuItemImpl> it = this.mItems.iterator();
        while (it.hasNext()) {
            MenuItemImpl next = it.next();
            if (next.getGroupId() == groupId && next.isExclusiveCheckable() && next.isCheckable()) {
                next.setCheckedInt(next == menuItem);
            }
        }
    }

    public void setGroupCheckable(int i, boolean z, boolean z2) {
        for (MenuItemImpl menuItemImpl : this.mItems) {
            if (menuItemImpl.getGroupId() == i) {
                menuItemImpl.setExclusiveCheckable(z2);
                menuItemImpl.setCheckable(z);
            }
        }
    }

    public void setGroupVisible(int i, boolean z) {
        boolean z2 = false;
        for (MenuItemImpl menuItemImpl : this.mItems) {
            if (menuItemImpl.getGroupId() == i && menuItemImpl.setVisibleInt(z)) {
                z2 = true;
            }
        }
        if (z2) {
            onItemsChanged(true);
        }
    }

    public void setGroupEnabled(int i, boolean z) {
        for (MenuItemImpl menuItemImpl : this.mItems) {
            if (menuItemImpl.getGroupId() == i) {
                menuItemImpl.setEnabled(z);
            }
        }
    }

    public boolean hasVisibleItems() {
        int size = size();
        for (int i = 0; i < size; i++) {
            if (this.mItems.get(i).isVisible()) {
                return true;
            }
        }
        return false;
    }

    public MenuItem findItem(int i) {
        MenuItem menuItemFindItem;
        int size = size();
        for (int i2 = 0; i2 < size; i2++) {
            MenuItemImpl menuItemImpl = this.mItems.get(i2);
            if (menuItemImpl.getItemId() == i) {
                return menuItemImpl;
            }
            if (menuItemImpl.hasSubMenu() && (menuItemFindItem = menuItemImpl.getSubMenu().findItem(i)) != null) {
                return menuItemFindItem;
            }
        }
        return null;
    }

    public int findItemIndex(int i) {
        int size = size();
        for (int i2 = 0; i2 < size; i2++) {
            if (this.mItems.get(i2).getItemId() == i) {
                return i2;
            }
        }
        return -1;
    }

    public int findGroupIndex(int i) {
        return findGroupIndex(i, 0);
    }

    public int findGroupIndex(int i, int i2) {
        int size = size();
        if (i2 < 0) {
            i2 = 0;
        }
        while (i2 < size) {
            if (this.mItems.get(i2).getGroupId() == i) {
                return i2;
            }
            i2++;
        }
        return -1;
    }

    public int size() {
        return this.mItems.size();
    }

    public MenuItem getItem(int i) {
        return this.mItems.get(i);
    }

    public boolean isShortcutKey(int i, KeyEvent keyEvent) {
        return findItemWithShortcutForKey(i, keyEvent) != null;
    }

    public void setQwertyMode(boolean z) {
        this.mQwertyMode = z;
        onItemsChanged(false);
    }

    private static int getOrdering(int i) {
        int i2 = ((-65536) & i) >> 16;
        if (i2 >= 0) {
            int[] iArr = sCategoryToOrder;
            if (i2 < iArr.length) {
                return (i & 65535) | (iArr[i2] << 16);
            }
        }
        throw new IllegalArgumentException("order does not contain a valid category.");
    }

    boolean isQwertyMode() {
        return this.mQwertyMode;
    }

    public void setShortcutsVisible(boolean z) {
        if (this.mShortcutsVisible == z) {
            return;
        }
        setShortcutsVisibleInner(z);
        onItemsChanged(false);
    }

    /* JADX WARN: Code duplicated, block: B:8:0x0018  */
    private void setShortcutsVisibleInner(boolean z) {
        boolean z2;
        if (z) {
            z2 = this.mResources.getConfiguration().keyboard != 1 && this.mResources.getBoolean(R.bool.abc_config_showMenuShortcutsWhenKeyboardPresent);
        }
        this.mShortcutsVisible = z2;
    }

    public boolean isShortcutsVisible() {
        return this.mShortcutsVisible;
    }

    Resources getResources() {
        return this.mResources;
    }

    public Context getContext() {
        return this.mContext;
    }

    boolean dispatchMenuItemSelected(MenuBuilder menuBuilder, MenuItem menuItem) {
        Callback callback = this.mCallback;
        return callback != null && callback.onMenuItemSelected(menuBuilder, menuItem);
    }

    public void changeMenuMode() {
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onMenuModeChange(this);
        }
    }

    private static int findInsertIndex(ArrayList<MenuItemImpl> arrayList, int i) {
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            if (arrayList.get(size).getOrdering() <= i) {
                return size + 1;
            }
        }
        return 0;
    }

    public boolean performShortcut(int i, KeyEvent keyEvent, int i2) {
        MenuItemImpl menuItemImplFindItemWithShortcutForKey = findItemWithShortcutForKey(i, keyEvent);
        boolean zPerformItemAction = menuItemImplFindItemWithShortcutForKey != null ? performItemAction(menuItemImplFindItemWithShortcutForKey, i2) : false;
        if ((i2 & 2) != 0) {
            closeInternal(true);
        }
        return zPerformItemAction;
    }

    void findItemsWithShortcutForKey(List<MenuItemImpl> list, int i, KeyEvent keyEvent) {
        char numericShortcut;
        boolean zIsQwertyMode = isQwertyMode();
        int metaState = keyEvent.getMetaState();
        KeyCharacterMap.KeyData keyData = new KeyCharacterMap.KeyData();
        if (keyEvent.getKeyData(keyData) || i == 67) {
            for (MenuItemImpl menuItemImpl : this.mItems) {
                if (menuItemImpl.hasSubMenu()) {
                    ((MenuBuilder) menuItemImpl.getSubMenu()).findItemsWithShortcutForKey(list, i, keyEvent);
                }
                if (zIsQwertyMode) {
                    numericShortcut = menuItemImpl.getAlphabeticShortcut();
                } else {
                    numericShortcut = menuItemImpl.getNumericShortcut();
                }
                if ((metaState & 5) == 0 && numericShortcut != 0 && (numericShortcut == keyData.meta[0] || numericShortcut == keyData.meta[2] || (zIsQwertyMode && numericShortcut == '\b' && i == 67))) {
                    if (menuItemImpl.isEnabled()) {
                        list.add(menuItemImpl);
                    }
                }
            }
        }
    }

    MenuItemImpl findItemWithShortcutForKey(int i, KeyEvent keyEvent) {
        char numericShortcut;
        ArrayList<MenuItemImpl> arrayList = this.mTempShortcutItemList;
        arrayList.clear();
        findItemsWithShortcutForKey(arrayList, i, keyEvent);
        if (arrayList.isEmpty()) {
            return null;
        }
        int metaState = keyEvent.getMetaState();
        KeyCharacterMap.KeyData keyData = new KeyCharacterMap.KeyData();
        keyEvent.getKeyData(keyData);
        if (arrayList.size() == 1) {
            return arrayList.get(0);
        }
        boolean zIsQwertyMode = isQwertyMode();
        for (MenuItemImpl menuItemImpl : arrayList) {
            if (zIsQwertyMode) {
                numericShortcut = menuItemImpl.getAlphabeticShortcut();
            } else {
                numericShortcut = menuItemImpl.getNumericShortcut();
            }
            if ((numericShortcut == keyData.meta[0] && (metaState & 2) == 0) || ((numericShortcut == keyData.meta[2] && (metaState & 2) != 0) || (zIsQwertyMode && numericShortcut == '\b' && i == 67))) {
                return menuItemImpl;
            }
        }
        return null;
    }

    public boolean performIdentifierAction(int i, int i2) {
        return performItemAction(findItem(i), i2);
    }

    public boolean performItemAction(MenuItem menuItem, int i) {
        return performItemAction(menuItem, null, i);
    }

    public boolean performItemAction(MenuItem menuItem, MenuPresenter menuPresenter, int i) {
        MenuItemImpl menuItemImpl = (MenuItemImpl) menuItem;
        if (menuItemImpl == null || !menuItemImpl.isEnabled()) {
            return false;
        }
        boolean zInvoke = menuItemImpl.invoke();
        ActionProvider supportActionProvider = menuItemImpl.getSupportActionProvider();
        boolean z = supportActionProvider != null && supportActionProvider.hasSubMenu();
        if (menuItemImpl.hasCollapsibleActionView()) {
            zInvoke |= menuItemImpl.expandActionView();
            if (zInvoke) {
                closeInternal(true);
            }
        } else if (menuItemImpl.hasSubMenu() || z) {
            if ((i & 4) == 0) {
                closeInternal(false);
            }
            if (!menuItemImpl.hasSubMenu()) {
                menuItemImpl.setSubMenu(new SubMenuBuilder(getContext(), this, menuItemImpl));
            }
            SubMenuBuilder subMenuBuilder = (SubMenuBuilder) menuItemImpl.getSubMenu();
            if (z) {
                supportActionProvider.onPrepareSubMenu(subMenuBuilder);
            }
            zInvoke |= dispatchSubMenuSelected(subMenuBuilder, menuPresenter);
            if (!zInvoke) {
                closeInternal(true);
            }
        } else if ((i & 1) == 0) {
            closeInternal(true);
        }
        return zInvoke;
    }

    final void closeInternal(boolean z) {
        if (this.mIsClosing) {
            return;
        }
        this.mIsClosing = true;
        for (WeakReference<MenuPresenter> weakReference : this.mPresenters) {
            MenuPresenter menuPresenter = weakReference.get();
            if (menuPresenter == null) {
                this.mPresenters.remove(weakReference);
            } else {
                menuPresenter.onCloseMenu(this, z);
            }
        }
        this.mIsClosing = false;
    }

    public void close() {
        closeInternal(true);
    }

    protected void onItemsChanged(boolean z) {
        if (!this.mPreventDispatchingItemsChanged) {
            if (z) {
                this.mIsVisibleItemsStale = true;
                this.mIsActionItemsStale = true;
            }
            dispatchPresenterUpdate(z);
            return;
        }
        this.mItemsChangedWhileDispatchPrevented = true;
    }

    public void stopDispatchingItemsChanged() {
        if (this.mPreventDispatchingItemsChanged) {
            return;
        }
        this.mPreventDispatchingItemsChanged = true;
        this.mItemsChangedWhileDispatchPrevented = false;
    }

    public void startDispatchingItemsChanged() {
        this.mPreventDispatchingItemsChanged = false;
        if (this.mItemsChangedWhileDispatchPrevented) {
            this.mItemsChangedWhileDispatchPrevented = false;
            onItemsChanged(true);
        }
    }

    public void setPreventDispatchingItemsChanged(boolean z) {
        this.mPreventDispatchingItemsChanged = z;
    }

    void onItemVisibleChanged(MenuItemImpl menuItemImpl) {
        this.mIsVisibleItemsStale = true;
        onItemsChanged(true);
    }

    void onItemActionRequestChanged(MenuItemImpl menuItemImpl) {
        this.mIsActionItemsStale = true;
        onItemsChanged(true);
    }

    public ArrayList<MenuItemImpl> getVisibleItems() {
        if (!this.mIsVisibleItemsStale) {
            return this.mVisibleItems;
        }
        this.mVisibleItems.clear();
        for (MenuItemImpl menuItemImpl : this.mItems) {
            if (menuItemImpl.isVisible()) {
                this.mVisibleItems.add(menuItemImpl);
            }
        }
        Collections.sort(this.mVisibleItems, this.menuItemComparator);
        this.mIsVisibleItemsStale = false;
        this.mIsActionItemsStale = true;
        return this.mVisibleItems;
    }

    public void flagActionItems() {
        if (this.mIsActionItemsStale) {
            boolean zFlagActionItems = false;
            for (WeakReference<MenuPresenter> weakReference : this.mPresenters) {
                MenuPresenter menuPresenter = weakReference.get();
                if (menuPresenter == null) {
                    this.mPresenters.remove(weakReference);
                } else {
                    zFlagActionItems |= menuPresenter.flagActionItems();
                }
            }
            this.mActionItems.clear();
            this.mNonActionItems.clear();
            if (zFlagActionItems) {
                for (MenuItemImpl menuItemImpl : getVisibleItems()) {
                    if (menuItemImpl.isActionButton()) {
                        this.mActionItems.add(menuItemImpl);
                    } else {
                        this.mNonActionItems.add(menuItemImpl);
                    }
                }
            } else {
                this.mNonActionItems.addAll(getVisibleItems());
            }
            this.mIsActionItemsStale = false;
        }
    }

    public ArrayList<MenuItemImpl> getActionItems() {
        flagActionItems();
        return this.mActionItems;
    }

    public ArrayList<MenuItemImpl> getNonActionItems() {
        flagActionItems();
        return this.mNonActionItems;
    }

    public void clearHeader() {
        this.mHeaderIcon = null;
        this.mHeaderTitle = null;
        this.mHeaderView = null;
        onItemsChanged(false);
    }

    private void setHeaderInternal(int i, CharSequence charSequence, int i2, Drawable drawable, View view) {
        Resources resources = getResources();
        if (view != null) {
            this.mHeaderView = view;
            this.mHeaderTitle = null;
            this.mHeaderIcon = null;
        } else {
            if (i > 0) {
                this.mHeaderTitle = resources.getText(i);
            } else if (charSequence != null) {
                this.mHeaderTitle = charSequence;
            }
            if (i2 > 0) {
                this.mHeaderIcon = resources.getDrawable(i2);
            } else if (drawable != null) {
                this.mHeaderIcon = drawable;
            }
            this.mHeaderView = null;
        }
        onItemsChanged(false);
    }

    protected MenuBuilder setHeaderTitleInt(CharSequence charSequence) {
        setHeaderInternal(0, charSequence, 0, null, null);
        return this;
    }

    protected MenuBuilder setHeaderTitleInt(int i) {
        setHeaderInternal(i, null, 0, null, null);
        return this;
    }

    protected MenuBuilder setHeaderIconInt(Drawable drawable) {
        setHeaderInternal(0, null, 0, drawable, null);
        return this;
    }

    protected MenuBuilder setHeaderIconInt(int i) {
        setHeaderInternal(0, null, i, null, null);
        return this;
    }

    protected MenuBuilder setHeaderViewInt(View view) {
        setHeaderInternal(0, null, 0, null, view);
        return this;
    }

    public CharSequence getHeaderTitle() {
        return this.mHeaderTitle;
    }

    public Drawable getHeaderIcon() {
        return this.mHeaderIcon;
    }

    public View getHeaderView() {
        return this.mHeaderView;
    }

    public void setCurrentMenuInfo(ContextMenu.ContextMenuInfo contextMenuInfo) {
        this.mCurrentMenuInfo = contextMenuInfo;
    }

    void setOptionalIconsVisible(boolean z) {
        this.mOptionalIconsVisible = z;
    }

    boolean getOptionalIconsVisible() {
        return this.mOptionalIconsVisible;
    }

    public void setForceShowOptionalIcon(boolean z) {
        this.mOptionalIconsVisible = z;
    }

    public boolean expandItemActionView(MenuItemImpl menuItemImpl) {
        boolean zExpandItemActionView = false;
        if (this.mPresenters.isEmpty()) {
            return false;
        }
        stopDispatchingItemsChanged();
        for (WeakReference<MenuPresenter> weakReference : this.mPresenters) {
            MenuPresenter menuPresenter = weakReference.get();
            if (menuPresenter == null) {
                this.mPresenters.remove(weakReference);
            } else {
                zExpandItemActionView = menuPresenter.expandItemActionView(this, menuItemImpl);
                if (zExpandItemActionView) {
                    break;
                }
            }
        }
        startDispatchingItemsChanged();
        if (zExpandItemActionView) {
            this.mExpandedItem = menuItemImpl;
        }
        return zExpandItemActionView;
    }

    public boolean collapseItemActionView(MenuItemImpl menuItemImpl) {
        boolean zCollapseItemActionView = false;
        if (!this.mPresenters.isEmpty() && this.mExpandedItem == menuItemImpl) {
            stopDispatchingItemsChanged();
            for (WeakReference<MenuPresenter> weakReference : this.mPresenters) {
                MenuPresenter menuPresenter = weakReference.get();
                if (menuPresenter == null) {
                    this.mPresenters.remove(weakReference);
                } else {
                    zCollapseItemActionView = menuPresenter.collapseItemActionView(this, menuItemImpl);
                    if (zCollapseItemActionView) {
                        break;
                    }
                }
            }
            startDispatchingItemsChanged();
            if (zCollapseItemActionView) {
                this.mExpandedItem = null;
            }
        }
        return zCollapseItemActionView;
    }

    public MenuItemImpl getExpandedItem() {
        return this.mExpandedItem;
    }
}
