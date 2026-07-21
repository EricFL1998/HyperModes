package miuix.navigator.navigation;

import android.R;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.util.Pools;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import miuix.animation.Folme;
import miuix.animation.base.AnimConfig;
import miuix.animation.base.AnimSpecialConfig;
import miuix.animation.controller.AnimState;
import miuix.animation.property.ViewProperty;
import miuix.appcompat.view.menu.MenuBuilder;
import miuix.appcompat.view.menu.MenuItemImpl;
import miuix.appcompat.view.menu.MenuView;

/* JADX INFO: loaded from: classes3.dex */
public abstract class NavigationBarMenuView extends ViewGroup implements MenuView {
    private static final int[] CHECKED_STATE_SET = {R.attr.state_checked};
    private static final int[] DISABLED_STATE_SET = {-16842910};
    private static final int ITEM_POOL_SIZE = 5;
    private static final int NO_PADDING = -1;
    private NavigationBarItemView[] buttons;
    private ColorStateList itemActiveIndicatorColor;
    private boolean itemActiveIndicatorEnabled;
    private int itemActiveIndicatorHeight;
    private int itemActiveIndicatorMarginHorizontal;
    private boolean itemActiveIndicatorResizeable;
    private int itemActiveIndicatorWidth;
    private Drawable itemBackground;
    private int itemBackgroundRes;
    private int itemIconSize;
    private ColorStateList itemIconTint;
    private int itemPaddingBottom;
    private int itemPaddingTop;
    private final Pools.Pool<NavigationBarItemView> itemPool;
    private int itemTextAppearance;
    private final ColorStateList itemTextColorDefault;
    private ColorStateList itemTextColorFromUser;
    private int itemTextMaxLine;
    private ColorStateList itemTouchColor;
    private int labelVisibilityMode;
    private int layoutStyle;
    private MenuBuilder menu;
    private final View.OnClickListener onClickListener;
    private final SparseArray<View.OnTouchListener> onTouchListeners;
    private NavigationBarPresenter presenter;
    private int selectedItemId;
    private int selectedItemPosition;

    private Drawable createItemActiveIndicatorDrawable() {
        return null;
    }

    private boolean isValidId(int i) {
        return i != -1;
    }

    protected abstract NavigationBarItemView createNavigationBarItemView(Context context);

    @Override // miuix.appcompat.view.menu.MenuView
    public int getWindowAnimations() {
        return 0;
    }

    protected boolean isShifting(int i, int i2) {
        if (i == -1) {
            if (i2 > 3) {
                return true;
            }
        } else if (i == 0) {
            return true;
        }
        return false;
    }

    public NavigationBarMenuView(Context context) {
        super(context);
        this.itemPool = new Pools.SynchronizedPool(5);
        this.onTouchListeners = new SparseArray<>(5);
        this.selectedItemId = 0;
        this.selectedItemPosition = 0;
        this.itemTextMaxLine = 1;
        this.itemPaddingTop = -1;
        this.itemPaddingBottom = -1;
        this.itemActiveIndicatorResizeable = false;
        this.itemTextColorDefault = createDefaultColorStateList(R.attr.textColorSecondary);
        this.onClickListener = new View.OnClickListener() { // from class: miuix.navigator.navigation.NavigationBarMenuView.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MenuItemImpl itemData = ((NavigationBarItemView) view).getItemData();
                if (NavigationBarMenuView.this.menu.performItemAction(itemData, NavigationBarMenuView.this.presenter, 0)) {
                    return;
                }
                itemData.setChecked(true);
            }
        };
        ViewCompat.setImportantForAccessibility(this, 1);
    }

    @Override // miuix.appcompat.view.menu.MenuView
    public void initialize(MenuBuilder menuBuilder) {
        this.menu = menuBuilder;
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        AccessibilityNodeInfoCompat.wrap(accessibilityNodeInfo).setCollectionInfo(AccessibilityNodeInfoCompat.CollectionInfoCompat.obtain(1, this.menu.getVisibleItems().size(), false, 1));
    }

    private void initializeAccessibility(NavigationBarItemView navigationBarItemView) {
        ViewCompat.setAccessibilityDelegate(navigationBarItemView, new AccessibilityDelegateCompat() { // from class: miuix.navigator.navigation.NavigationBarMenuView.2
            @Override // androidx.core.view.AccessibilityDelegateCompat
            public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
                MenuItemImpl itemData = view instanceof NavigationBarItemView ? ((NavigationBarItemView) view).getItemData() : null;
                if (itemData == null || itemData.isChecked()) {
                    return;
                }
                accessibilityNodeInfoCompat.setStateDescription(NavigationBarMenuView.this.getResources().getString(miuix.navigator.R.string.accessibility_tab_state_description_unselect));
                if (TextUtils.isEmpty(itemData.getTitle())) {
                    return;
                }
                accessibilityNodeInfoCompat.setContentDescription(itemData.getTitle());
            }
        });
    }

    public void setIconTintList(ColorStateList colorStateList) {
        this.itemIconTint = colorStateList;
        NavigationBarItemView[] navigationBarItemViewArr = this.buttons;
        if (navigationBarItemViewArr != null) {
            for (NavigationBarItemView navigationBarItemView : navigationBarItemViewArr) {
                navigationBarItemView.setIconTintList(colorStateList);
            }
        }
    }

    public ColorStateList getIconTintList() {
        return this.itemIconTint;
    }

    public void setItemIconSize(int i) {
        this.itemIconSize = i;
        NavigationBarItemView[] navigationBarItemViewArr = this.buttons;
        if (navigationBarItemViewArr != null) {
            for (NavigationBarItemView navigationBarItemView : navigationBarItemViewArr) {
                navigationBarItemView.setIconSize(i);
            }
        }
    }

    public int getItemIconSize() {
        return this.itemIconSize;
    }

    public void setItemTextColor(ColorStateList colorStateList) {
        this.itemTextColorFromUser = colorStateList;
        NavigationBarItemView[] navigationBarItemViewArr = this.buttons;
        if (navigationBarItemViewArr != null) {
            for (NavigationBarItemView navigationBarItemView : navigationBarItemViewArr) {
                navigationBarItemView.setTextColor(colorStateList);
            }
        }
    }

    public ColorStateList getItemTextColor() {
        return this.itemTextColorFromUser;
    }

    public void setItemTextAppearance(int i) {
        this.itemTextAppearance = i;
        NavigationBarItemView[] navigationBarItemViewArr = this.buttons;
        if (navigationBarItemViewArr != null) {
            for (NavigationBarItemView navigationBarItemView : navigationBarItemViewArr) {
                navigationBarItemView.setTextAppearance(i);
                ColorStateList colorStateList = this.itemTextColorFromUser;
                if (colorStateList != null) {
                    navigationBarItemView.setTextColor(colorStateList);
                }
            }
        }
    }

    public int getItemTextAppearance() {
        return this.itemTextAppearance;
    }

    public void setItemTextMaxLine(int i) {
        this.itemTextMaxLine = i;
        NavigationBarItemView[] navigationBarItemViewArr = this.buttons;
        if (navigationBarItemViewArr != null) {
            for (NavigationBarItemView navigationBarItemView : navigationBarItemViewArr) {
                navigationBarItemView.setLabelMaxLine(i);
            }
        }
    }

    public void setItemBackgroundRes(int i) {
        this.itemBackgroundRes = i;
        NavigationBarItemView[] navigationBarItemViewArr = this.buttons;
        if (navigationBarItemViewArr != null) {
            for (NavigationBarItemView navigationBarItemView : navigationBarItemViewArr) {
                navigationBarItemView.setItemBackground(i);
            }
        }
    }

    public int getItemPaddingTop() {
        return this.itemPaddingTop;
    }

    public void setItemPaddingTop(int i) {
        this.itemPaddingTop = i;
        NavigationBarItemView[] navigationBarItemViewArr = this.buttons;
        if (navigationBarItemViewArr != null) {
            for (NavigationBarItemView navigationBarItemView : navigationBarItemViewArr) {
                navigationBarItemView.setItemPaddingTop(i);
            }
        }
    }

    public int getItemPaddingBottom() {
        return this.itemPaddingBottom;
    }

    public void setItemPaddingBottom(int i) {
        this.itemPaddingBottom = i;
        NavigationBarItemView[] navigationBarItemViewArr = this.buttons;
        if (navigationBarItemViewArr != null) {
            for (NavigationBarItemView navigationBarItemView : navigationBarItemViewArr) {
                navigationBarItemView.setItemPaddingBottom(i);
            }
        }
    }

    public boolean getItemActiveIndicatorEnabled() {
        return this.itemActiveIndicatorEnabled;
    }

    public void setItemActiveIndicatorEnabled(boolean z) {
        this.itemActiveIndicatorEnabled = z;
        NavigationBarItemView[] navigationBarItemViewArr = this.buttons;
        if (navigationBarItemViewArr != null) {
            for (NavigationBarItemView navigationBarItemView : navigationBarItemViewArr) {
                navigationBarItemView.setActiveIndicatorEnabled(z);
            }
        }
    }

    public int getItemActiveIndicatorWidth() {
        return this.itemActiveIndicatorWidth;
    }

    public void setItemActiveIndicatorWidth(int i) {
        this.itemActiveIndicatorWidth = i;
        NavigationBarItemView[] navigationBarItemViewArr = this.buttons;
        if (navigationBarItemViewArr != null) {
            for (NavigationBarItemView navigationBarItemView : navigationBarItemViewArr) {
                navigationBarItemView.setActiveIndicatorWidth(i);
            }
        }
    }

    public int getItemActiveIndicatorHeight() {
        return this.itemActiveIndicatorHeight;
    }

    public void setItemActiveIndicatorHeight(int i) {
        this.itemActiveIndicatorHeight = i;
        NavigationBarItemView[] navigationBarItemViewArr = this.buttons;
        if (navigationBarItemViewArr != null) {
            for (NavigationBarItemView navigationBarItemView : navigationBarItemViewArr) {
                navigationBarItemView.setActiveIndicatorHeight(i);
            }
        }
    }

    public int getItemActiveIndicatorMarginHorizontal() {
        return this.itemActiveIndicatorMarginHorizontal;
    }

    public void setItemActiveIndicatorMarginHorizontal(int i) {
        this.itemActiveIndicatorMarginHorizontal = i;
        NavigationBarItemView[] navigationBarItemViewArr = this.buttons;
        if (navigationBarItemViewArr != null) {
            for (NavigationBarItemView navigationBarItemView : navigationBarItemViewArr) {
                navigationBarItemView.setActiveIndicatorMarginHorizontal(i);
            }
        }
    }

    protected boolean isItemActiveIndicatorResizeable() {
        return this.itemActiveIndicatorResizeable;
    }

    protected void setItemActiveIndicatorResizeable(boolean z) {
        this.itemActiveIndicatorResizeable = z;
        NavigationBarItemView[] navigationBarItemViewArr = this.buttons;
        if (navigationBarItemViewArr != null) {
            for (NavigationBarItemView navigationBarItemView : navigationBarItemViewArr) {
                navigationBarItemView.setActiveIndicatorResizeable(z);
            }
        }
    }

    public ColorStateList getItemActiveIndicatorColor() {
        return this.itemActiveIndicatorColor;
    }

    public void setItemActiveIndicatorColor(ColorStateList colorStateList) {
        this.itemActiveIndicatorColor = colorStateList;
        NavigationBarItemView[] navigationBarItemViewArr = this.buttons;
        if (navigationBarItemViewArr != null) {
            for (NavigationBarItemView navigationBarItemView : navigationBarItemViewArr) {
                navigationBarItemView.setActiveIndicatorDrawable(createItemActiveIndicatorDrawable());
            }
        }
    }

    @Deprecated
    public int getItemBackgroundRes() {
        return this.itemBackgroundRes;
    }

    public void setItemBackground(Drawable drawable) {
        this.itemBackground = drawable;
        NavigationBarItemView[] navigationBarItemViewArr = this.buttons;
        if (navigationBarItemViewArr != null) {
            for (NavigationBarItemView navigationBarItemView : navigationBarItemViewArr) {
                navigationBarItemView.setItemBackground(drawable);
            }
        }
    }

    public void setItemTouchColor(ColorStateList colorStateList) {
        this.itemTouchColor = colorStateList;
        NavigationBarItemView[] navigationBarItemViewArr = this.buttons;
        if (navigationBarItemViewArr != null) {
            for (NavigationBarItemView navigationBarItemView : navigationBarItemViewArr) {
                navigationBarItemView.setItemTouchColor(colorStateList);
            }
        }
    }

    public Drawable getItemBackground() {
        NavigationBarItemView[] navigationBarItemViewArr = this.buttons;
        if (navigationBarItemViewArr != null && navigationBarItemViewArr.length > 0) {
            return navigationBarItemViewArr[0].getBackground();
        }
        return this.itemBackground;
    }

    public void setLabelVisibilityMode(int i) {
        this.labelVisibilityMode = i;
    }

    public int getLabelVisibilityMode() {
        return this.labelVisibilityMode;
    }

    public void setLayoutStyle(int i) {
        this.layoutStyle = i;
        post(new Runnable() { // from class: miuix.navigator.navigation.NavigationBarMenuView$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.requestLayout();
            }
        });
    }

    public int getLayoutStyle() {
        return this.layoutStyle;
    }

    public boolean isLayoutInWideStyle() {
        return (this.layoutStyle & 1) > 0;
    }

    public void setItemOnTouchListener(int i, View.OnTouchListener onTouchListener) {
        if (onTouchListener == null) {
            this.onTouchListeners.remove(i);
        } else {
            this.onTouchListeners.put(i, onTouchListener);
        }
        NavigationBarItemView[] navigationBarItemViewArr = this.buttons;
        if (navigationBarItemViewArr != null) {
            for (NavigationBarItemView navigationBarItemView : navigationBarItemViewArr) {
                if (navigationBarItemView.getItemData().getItemId() == i) {
                    navigationBarItemView.setOnTouchListener(onTouchListener);
                }
            }
        }
    }

    public ColorStateList createDefaultColorStateList(int i) {
        TypedValue typedValue = new TypedValue();
        if (!getContext().getTheme().resolveAttribute(i, typedValue, true)) {
            return null;
        }
        ColorStateList colorStateList = AppCompatResources.getColorStateList(getContext(), typedValue.resourceId);
        if (!getContext().getTheme().resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true)) {
            return null;
        }
        int i2 = typedValue.data;
        int defaultColor = colorStateList.getDefaultColor();
        int[] iArr = DISABLED_STATE_SET;
        return new ColorStateList(new int[][]{iArr, CHECKED_STATE_SET, EMPTY_STATE_SET}, new int[]{colorStateList.getColorForState(iArr, defaultColor), i2, defaultColor});
    }

    public void setPresenter(NavigationBarPresenter navigationBarPresenter) {
        this.presenter = navigationBarPresenter;
    }

    public void buildMenuView() {
        removeAllViews();
        NavigationBarItemView[] navigationBarItemViewArr = this.buttons;
        if (navigationBarItemViewArr != null) {
            for (NavigationBarItemView navigationBarItemView : navigationBarItemViewArr) {
                if (navigationBarItemView != null) {
                    this.itemPool.release(navigationBarItemView);
                    navigationBarItemView.clear();
                }
            }
        }
        if (this.menu.size() == 0) {
            this.selectedItemId = 0;
            this.selectedItemPosition = 0;
            this.buttons = null;
            return;
        }
        this.buttons = new NavigationBarItemView[this.menu.size()];
        boolean zIsShifting = isShifting(this.labelVisibilityMode, this.menu.getVisibleItems().size());
        for (int i = 0; i < this.menu.size(); i++) {
            this.presenter.setUpdateSuspended(true);
            this.menu.getItem(i).setCheckable(true);
            this.presenter.setUpdateSuspended(false);
            NavigationBarItemView newItem = getNewItem();
            this.buttons[i] = newItem;
            newItem.setIconTintList(this.itemIconTint);
            newItem.setIconSize(this.itemIconSize);
            newItem.setTextColor(this.itemTextColorDefault);
            newItem.setTextAppearance(this.itemTextAppearance);
            newItem.setLabelMaxLine(this.itemTextMaxLine);
            newItem.setTextColor(this.itemTextColorFromUser);
            int i2 = this.itemPaddingTop;
            if (i2 != -1) {
                newItem.setItemPaddingTop(i2);
            }
            int i3 = this.itemPaddingBottom;
            if (i3 != -1) {
                newItem.setItemPaddingBottom(i3);
            }
            newItem.setActiveIndicatorWidth(this.itemActiveIndicatorWidth);
            newItem.setActiveIndicatorHeight(this.itemActiveIndicatorHeight);
            newItem.setActiveIndicatorMarginHorizontal(this.itemActiveIndicatorMarginHorizontal);
            newItem.setActiveIndicatorDrawable(createItemActiveIndicatorDrawable());
            newItem.setActiveIndicatorResizeable(this.itemActiveIndicatorResizeable);
            newItem.setActiveIndicatorEnabled(this.itemActiveIndicatorEnabled);
            Drawable drawable = this.itemBackground;
            if (drawable != null) {
                newItem.setItemBackground(drawable);
            } else {
                newItem.setItemBackground(this.itemBackgroundRes);
            }
            newItem.setItemTouchColor(this.itemTouchColor);
            newItem.setShifting(zIsShifting);
            newItem.setLabelVisibilityMode(this.labelVisibilityMode);
            MenuItemImpl menuItemImpl = (MenuItemImpl) this.menu.getItem(i);
            newItem.initialize(menuItemImpl, 0);
            newItem.setItemPosition(i);
            int itemId = menuItemImpl.getItemId();
            newItem.setOnTouchListener(this.onTouchListeners.get(itemId));
            newItem.setOnClickListener(this.onClickListener);
            int i4 = this.selectedItemId;
            if (i4 != 0 && itemId == i4) {
                this.selectedItemPosition = i;
            }
            initializeAccessibility(newItem);
            addView(newItem);
        }
        int iMin = Math.min(this.menu.size() - 1, this.selectedItemPosition);
        this.selectedItemPosition = iMin;
        this.menu.getItem(iMin).setChecked(true);
    }

    public void updateMenuView() {
        MenuBuilder menuBuilder = this.menu;
        if (menuBuilder == null || this.buttons == null) {
            return;
        }
        int size = menuBuilder.size();
        if (size != this.buttons.length) {
            buildMenuView();
            return;
        }
        for (int i = 0; i < size; i++) {
            MenuItem item = this.menu.getItem(i);
            if (item.isChecked()) {
                this.selectedItemId = item.getItemId();
                this.selectedItemPosition = i;
            }
        }
        boolean zIsShifting = isShifting(this.labelVisibilityMode, this.menu.getVisibleItems().size());
        for (int i2 = 0; i2 < size; i2++) {
            this.presenter.setUpdateSuspended(true);
            if (this.buttons[i2].getLayoutStyle() != getLayoutStyle()) {
                this.buttons[i2].setLayoutStyle(getLayoutStyle());
                this.buttons[i2].updateLayout();
            }
            this.buttons[i2].setLabelVisibilityMode(this.labelVisibilityMode);
            this.buttons[i2].setShifting(zIsShifting);
            this.buttons[i2].initialize((MenuItemImpl) this.menu.getItem(i2), 0);
            this.presenter.setUpdateSuspended(false);
        }
    }

    private NavigationBarItemView getNewItem() {
        NavigationBarItemView navigationBarItemViewAcquire = this.itemPool.acquire();
        if (navigationBarItemViewAcquire == null) {
            return createNavigationBarItemView(getContext());
        }
        if (navigationBarItemViewAcquire.getLayoutStyle() == getLayoutStyle()) {
            return navigationBarItemViewAcquire;
        }
        navigationBarItemViewAcquire.setLayoutStyle(getLayoutStyle());
        navigationBarItemViewAcquire.updateLayout();
        return navigationBarItemViewAcquire;
    }

    public int getSelectedItemId() {
        return this.selectedItemId;
    }

    void tryRestoreSelectedItemId(int i) {
        int size = this.menu.size();
        for (int i2 = 0; i2 < size; i2++) {
            MenuItem item = this.menu.getItem(i2);
            if (i == item.getItemId()) {
                this.selectedItemId = i;
                this.selectedItemPosition = i2;
                item.setChecked(true);
                return;
            }
        }
    }

    public NavigationBarItemView findItemView(int i) {
        validateMenuItemId(i);
        NavigationBarItemView[] navigationBarItemViewArr = this.buttons;
        if (navigationBarItemViewArr == null) {
            return null;
        }
        for (NavigationBarItemView navigationBarItemView : navigationBarItemViewArr) {
            if (navigationBarItemView.getId() == i) {
                return navigationBarItemView;
            }
        }
        return null;
    }

    protected int getSelectedItemPosition() {
        return this.selectedItemPosition;
    }

    void showItem() {
        showItem(true);
    }

    void showItem(boolean z) {
        if (z) {
            AnimState animState = new AnimState("navigationBarItemShow");
            AnimConfig animConfig = new AnimConfig();
            animState.add(ViewProperty.SCALE_X, 1.0d);
            animState.add(ViewProperty.SCALE_Y, 1.0d);
            AnimSpecialConfig animSpecialConfig = new AnimSpecialConfig();
            animSpecialConfig.setEase(-2, 0.85f, 0.3f);
            animSpecialConfig.setDelay(50L);
            animConfig.setSpecial(ViewProperty.SCALE_X, animSpecialConfig);
            animConfig.setSpecial(ViewProperty.SCALE_Y, animSpecialConfig);
            NavigationBarItemView[] navigationBarItemViewArr = this.buttons;
            if (navigationBarItemViewArr != null) {
                for (NavigationBarItemView navigationBarItemView : navigationBarItemViewArr) {
                    Folme.useAt(navigationBarItemView).state().to(animState, animConfig);
                }
                return;
            }
            return;
        }
        NavigationBarItemView[] navigationBarItemViewArr2 = this.buttons;
        if (navigationBarItemViewArr2 != null) {
            for (NavigationBarItemView navigationBarItemView2 : navigationBarItemViewArr2) {
                navigationBarItemView2.setScaleX(1.0f);
                navigationBarItemView2.setScaleY(1.0f);
            }
        }
    }

    void hideItem() {
        hideItem(true);
    }

    void hideItem(boolean z) {
        if (z) {
            AnimState animState = new AnimState("navigationBarItemHide");
            AnimConfig animConfig = new AnimConfig();
            animState.add(ViewProperty.SCALE_X, 0.949999988079071d);
            animState.add(ViewProperty.SCALE_Y, 0.949999988079071d);
            AnimSpecialConfig animSpecialConfig = new AnimSpecialConfig();
            animSpecialConfig.setEase(-2, 0.95f, 0.15f);
            animConfig.setSpecial(ViewProperty.SCALE_X, animSpecialConfig);
            animConfig.setSpecial(ViewProperty.SCALE_Y, animSpecialConfig);
            NavigationBarItemView[] navigationBarItemViewArr = this.buttons;
            if (navigationBarItemViewArr != null) {
                for (NavigationBarItemView navigationBarItemView : navigationBarItemViewArr) {
                    Folme.useAt(navigationBarItemView).state().to(animState, animConfig);
                }
                return;
            }
            return;
        }
        NavigationBarItemView[] navigationBarItemViewArr2 = this.buttons;
        if (navigationBarItemViewArr2 != null) {
            for (NavigationBarItemView navigationBarItemView2 : navigationBarItemViewArr2) {
                navigationBarItemView2.setScaleX(0.95f);
                navigationBarItemView2.setScaleY(0.95f);
            }
        }
    }

    protected MenuBuilder getMenu() {
        return this.menu;
    }

    private void validateMenuItemId(int i) {
        if (!isValidId(i)) {
            throw new IllegalArgumentException(i + " is not a valid view id");
        }
    }
}
