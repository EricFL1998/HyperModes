package miuix.navigator.navigation;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import androidx.customview.view.AbsSavedState;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import miuix.animation.Folme;
import miuix.animation.base.AnimConfig;
import miuix.animation.listener.TransitionListener;
import miuix.appcompat.view.menu.MenuBuilder;
import miuix.appcompat.view.menu.MenuView;
import miuix.core.util.MiuixUIUtils;
import miuix.device.DeviceUtils;
import miuix.internal.util.MiuixResources;
import miuix.navigator.BottomNavigation;
import miuix.navigator.R;

/* JADX INFO: loaded from: classes3.dex */
public abstract class NavigationBarView extends FrameLayout implements BottomNavigation {
    public static final int LABEL_VISIBILITY_AUTO = -1;
    public static final int LABEL_VISIBILITY_LABELED = 1;
    public static final int LABEL_VISIBILITY_SELECTED = 0;
    public static final int LABEL_VISIBILITY_UNLABELED = 2;
    private static final int MENU_PRESENTER_ID = 1;
    private final AttributeSet mAttrs;
    private final int mDefStyleAttr;
    private final int mDefStyleRes;
    private int mDensityDpi;
    private int mItemTextAppearance;
    private int mMinHeightDp;
    private int mMinHeightDpInWideStyle;
    private int mMinHeightInWideStyle;
    private final NavigationBarMenu menu;
    private MenuInflater menuInflater;
    private final NavigationBarMenuView menuView;
    private final NavigationBarPresenter presenter;
    private OnItemReselectedListener reselectedListener;
    private OnItemSelectedListener selectedListener;

    @Retention(RetentionPolicy.SOURCE)
    public @interface LabelVisibility {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface LayoutStyle {
    }

    public interface OnItemReselectedListener {
        void onNavigationItemReselected(MenuItem menuItem);
    }

    public interface OnItemSelectedListener {
        boolean onNavigationItemSelected(MenuItem menuItem);
    }

    protected abstract NavigationBarMenuView createNavigationBarMenuView(Context context);

    public abstract int getMaxItemCount();

    @Override // miuix.navigator.BottomNavigation
    public View getView() {
        return this;
    }

    public NavigationBarView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        NavigationBarPresenter navigationBarPresenter = new NavigationBarPresenter();
        this.presenter = navigationBarPresenter;
        Context context2 = getContext();
        this.mDensityDpi = context2.getResources().getConfiguration().densityDpi;
        this.mAttrs = attributeSet;
        this.mDefStyleAttr = i;
        this.mDefStyleRes = i2;
        TypedArray typedArrayObtainStyledAttributes = context2.obtainStyledAttributes(attributeSet, R.styleable.MiuixNavigationBarView, i, i2);
        boolean z = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MiuixBottomNavigationView_largeFontAdaptationEnabled, true) && MiuixUIUtils.getFontLevel(context2) == 2;
        NavigationBarMenu navigationBarMenu = new NavigationBarMenu(context2, getClass(), getMaxItemCount());
        this.menu = navigationBarMenu;
        NavigationBarMenuView navigationBarMenuViewCreateNavigationBarMenuView = createNavigationBarMenuView(context2);
        this.menuView = navigationBarMenuViewCreateNavigationBarMenuView;
        navigationBarPresenter.setMenuView(navigationBarMenuViewCreateNavigationBarMenuView);
        navigationBarPresenter.setId(1);
        navigationBarMenuViewCreateNavigationBarMenuView.setPresenter(navigationBarPresenter);
        navigationBarMenu.addMenuPresenter(navigationBarPresenter);
        navigationBarPresenter.initForMenu(getContext(), navigationBarMenu);
        if (typedArrayObtainStyledAttributes.hasValue(R.styleable.MiuixNavigationBarView_miuixItemIconTint)) {
            navigationBarMenuViewCreateNavigationBarMenuView.setIconTintList(typedArrayObtainStyledAttributes.getColorStateList(R.styleable.MiuixNavigationBarView_miuixItemIconTint));
        } else {
            navigationBarMenuViewCreateNavigationBarMenuView.setIconTintList(navigationBarMenuViewCreateNavigationBarMenuView.createDefaultColorStateList(android.R.attr.textColorSecondary));
        }
        setItemIconSize(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MiuixNavigationBarView_miuixItemIconSize, getResources().getDimensionPixelSize(R.dimen.miuix_base_navigation_bar_item_default_icon_size)));
        if (z) {
            if (typedArrayObtainStyledAttributes.hasValue(R.styleable.MiuixNavigationBarView_miuixItemTextAppearanceInLargeFont)) {
                int resourceId = typedArrayObtainStyledAttributes.getResourceId(R.styleable.MiuixNavigationBarView_miuixItemTextAppearanceInLargeFont, 0);
                this.mItemTextAppearance = resourceId;
                setItemTextAppearance(resourceId);
            }
        } else if (typedArrayObtainStyledAttributes.hasValue(R.styleable.MiuixNavigationBarView_miuixItemTextAppearance)) {
            int resourceId2 = typedArrayObtainStyledAttributes.getResourceId(R.styleable.MiuixNavigationBarView_miuixItemTextAppearance, 0);
            this.mItemTextAppearance = resourceId2;
            setItemTextAppearance(resourceId2);
        }
        if (typedArrayObtainStyledAttributes.hasValue(R.styleable.MiuixNavigationBarView_miuixItemTextColor)) {
            setItemTextColor(typedArrayObtainStyledAttributes.getColorStateList(R.styleable.MiuixNavigationBarView_miuixItemTextColor));
        }
        if (typedArrayObtainStyledAttributes.hasValue(R.styleable.MiuixNavigationBarView_miuixItemPaddingTop)) {
            setItemPaddingTop(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MiuixNavigationBarView_miuixItemPaddingTop, 0));
        }
        if (typedArrayObtainStyledAttributes.hasValue(R.styleable.MiuixNavigationBarView_miuixItemPaddingBottom)) {
            setItemPaddingBottom(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MiuixNavigationBarView_miuixItemPaddingBottom, 0));
        }
        if (typedArrayObtainStyledAttributes.hasValue(R.styleable.MiuixNavigationBarView_elevation)) {
            setElevation(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MiuixNavigationBarView_elevation, 0));
        }
        setLabelVisibilityMode(typedArrayObtainStyledAttributes.getInteger(R.styleable.MiuixNavigationBarView_miuixLabelVisibilityMode, 1));
        setLayoutStyle(typedArrayObtainStyledAttributes.getInteger(R.styleable.MiuixNavigationBarView_miuixLayoutStyle, 0));
        int resourceId3 = typedArrayObtainStyledAttributes.getResourceId(R.styleable.MiuixNavigationBarView_miuixItemBackground, 0);
        if (resourceId3 != 0) {
            navigationBarMenuViewCreateNavigationBarMenuView.setItemBackgroundRes(resourceId3);
        } else {
            setItemTouchColor(MiuixResources.getColorStateList(context2, typedArrayObtainStyledAttributes, R.styleable.MiuixNavigationBarView_itemTouchColor));
        }
        if (typedArrayObtainStyledAttributes.hasValue(R.styleable.MiuixNavigationBarView_menu)) {
            inflateMenu(typedArrayObtainStyledAttributes.getResourceId(R.styleable.MiuixNavigationBarView_menu, 0));
        }
        typedArrayObtainStyledAttributes.recycle();
        addView(navigationBarMenuViewCreateNavigationBarMenuView);
        navigationBarMenu.setCallback(new MenuBuilder.Callback() { // from class: miuix.navigator.navigation.NavigationBarView.1
            @Override // miuix.appcompat.view.menu.MenuBuilder.Callback
            public void onMenuModeChange(MenuBuilder menuBuilder) {
            }

            @Override // miuix.appcompat.view.menu.MenuBuilder.Callback
            public boolean onMenuItemSelected(MenuBuilder menuBuilder, MenuItem menuItem) {
                if (NavigationBarView.this.reselectedListener == null || menuItem.getItemId() != NavigationBarView.this.getSelectedItemId()) {
                    return (NavigationBarView.this.selectedListener == null || NavigationBarView.this.selectedListener.onNavigationItemSelected(menuItem)) ? false : true;
                }
                NavigationBarView.this.reselectedListener.onNavigationItemReselected(menuItem);
                return true;
            }
        });
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        int i = configuration.densityDpi;
        if (this.mDensityDpi != i) {
            this.mDensityDpi = i;
            int i2 = this.mItemTextAppearance;
            if (i2 != 0) {
                setItemTextAppearance(i2);
            }
            if (this.mAttrs != null) {
                TypedArray typedArrayObtainStyledAttributes = getContext().obtainStyledAttributes(this.mAttrs, R.styleable.MiuixNavigationBarView, this.mDefStyleAttr, this.mDefStyleRes);
                int dimensionPixelSize = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MiuixNavigationBarView_miuixItemIconSize, getResources().getDimensionPixelSize(R.dimen.miuix_base_navigation_bar_item_default_icon_size));
                typedArrayObtainStyledAttributes.recycle();
                setItemIconSize(dimensionPixelSize);
            }
        }
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.selectedListener = onItemSelectedListener;
    }

    public void setOnItemReselectedListener(OnItemReselectedListener onItemReselectedListener) {
        this.reselectedListener = onItemReselectedListener;
    }

    public void show() {
        show(true);
    }

    public void show(boolean z) {
        if (z) {
            AnimConfig animConfig = new AnimConfig();
            animConfig.setEase(-2, 0.95f, 0.3f);
            animConfig.setDelay(50L);
            if (getAlpha() == 1.0f) {
                Folme.useAt(this.menuView).visible().show(animConfig);
            } else {
                if (this.menuView.getAlpha() != 1.0f) {
                    Folme.useAt(this.menuView).visible().show(animConfig);
                }
                Folme.useAt(this).visible().show(animConfig);
            }
            if (!DeviceUtils.isMiuiLiteRom()) {
                this.menuView.showItem(true);
            }
        } else {
            Folme.clean(this);
            setAlpha(1.0f);
            setVisibility(0);
            this.menuView.setAlpha(1.0f);
            this.menuView.showItem(false);
        }
        this.menuView.setVisibility(0);
        post(new Runnable() { // from class: miuix.navigator.navigation.NavigationBarView.2
            @Override // java.lang.Runnable
            public void run() {
                NavigationBarView.this.menuView.requestLayout();
            }
        });
    }

    public void hide(boolean z) {
        hide(z, true);
    }

    public void hide(boolean z, boolean z2) {
        if (z2) {
            AnimConfig animConfig = new AnimConfig();
            animConfig.setEase(-2, 1.0f, 0.2f);
            if (z) {
                Folme.useAt(this.menuView).visible().hide(animConfig);
            } else {
                animConfig.addListeners(new TransitionListener() { // from class: miuix.navigator.navigation.NavigationBarView.3
                    @Override // miuix.animation.listener.TransitionListener
                    public void onComplete(Object obj) {
                        super.onComplete(obj);
                        NavigationBarView.this.requestLayout();
                    }
                });
                Folme.useAt(this).visible().hide(animConfig);
            }
            if (DeviceUtils.isMiuiLiteRom()) {
                return;
            }
            this.menuView.hideItem(true);
            return;
        }
        if (z) {
            Folme.clean(this.menuView);
            this.menuView.setAlpha(0.0f);
            this.menuView.setVisibility(8);
        } else {
            Folme.clean(this);
            setAlpha(0.0f);
            setVisibility(8);
            this.menuView.hideItem(false);
        }
    }

    @Override // miuix.navigator.BottomNavigation
    public Menu getMenu() {
        return this.menu;
    }

    public MenuView getMenuView() {
        return this.menuView;
    }

    @Override // miuix.navigator.BottomNavigation
    public void inflateMenu(int i) {
        this.presenter.setUpdateSuspended(true);
        getMenuInflater().inflate(i, this.menu);
        this.presenter.setUpdateSuspended(false);
        this.presenter.updateMenuView(true);
    }

    public ColorStateList getItemIconTintList() {
        return this.menuView.getIconTintList();
    }

    public void setItemIconTintList(ColorStateList colorStateList) {
        this.menuView.setIconTintList(colorStateList);
    }

    public void setItemIconSize(int i) {
        this.menuView.setItemIconSize(i);
    }

    public void setItemIconSizeRes(int i) {
        setItemIconSize(getResources().getDimensionPixelSize(i));
    }

    public int getItemIconSize() {
        return this.menuView.getItemIconSize();
    }

    public ColorStateList getItemTextColor() {
        return this.menuView.getItemTextColor();
    }

    public void setItemTextColor(ColorStateList colorStateList) {
        this.menuView.setItemTextColor(colorStateList);
    }

    @Deprecated
    public int getItemBackgroundResource() {
        return this.menuView.getItemBackgroundRes();
    }

    public void setItemBackgroundResource(int i) {
        this.menuView.setItemBackgroundRes(i);
    }

    public Drawable getItemBackground() {
        return this.menuView.getItemBackground();
    }

    public void setItemBackground(Drawable drawable) {
        this.menuView.setItemBackground(drawable);
    }

    public void setItemTouchColor(ColorStateList colorStateList) {
        this.menuView.setItemTouchColor(colorStateList);
    }

    public int getItemPaddingTop() {
        return this.menuView.getItemPaddingTop();
    }

    public void setItemPaddingTop(int i) {
        this.menuView.setItemPaddingTop(i);
    }

    public int getItemPaddingBottom() {
        return this.menuView.getItemPaddingBottom();
    }

    public void setItemPaddingBottom(int i) {
        this.menuView.setItemPaddingBottom(i);
    }

    public boolean isItemActiveIndicatorEnabled() {
        return this.menuView.getItemActiveIndicatorEnabled();
    }

    public void setItemActiveIndicatorEnabled(boolean z) {
        this.menuView.setItemActiveIndicatorEnabled(z);
    }

    public int getItemActiveIndicatorWidth() {
        return this.menuView.getItemActiveIndicatorWidth();
    }

    public void setItemActiveIndicatorWidth(int i) {
        this.menuView.setItemActiveIndicatorWidth(i);
    }

    public int getItemActiveIndicatorHeight() {
        return this.menuView.getItemActiveIndicatorHeight();
    }

    public void setItemActiveIndicatorHeight(int i) {
        this.menuView.setItemActiveIndicatorHeight(i);
    }

    public int getItemActiveIndicatorMarginHorizontal() {
        return this.menuView.getItemActiveIndicatorMarginHorizontal();
    }

    public void setItemActiveIndicatorMarginHorizontal(int i) {
        this.menuView.setItemActiveIndicatorMarginHorizontal(i);
    }

    public ColorStateList getItemActiveIndicatorColor() {
        return this.menuView.getItemActiveIndicatorColor();
    }

    public void setItemActiveIndicatorColor(ColorStateList colorStateList) {
        this.menuView.setItemActiveIndicatorColor(colorStateList);
    }

    public int getSelectedItemId() {
        return this.menuView.getSelectedItemId();
    }

    public void setSelectedItemId(int i) {
        MenuItem menuItemFindItem = this.menu.findItem(i);
        if (menuItemFindItem == null || this.menu.performItemAction(menuItemFindItem, this.presenter, 0)) {
            return;
        }
        menuItemFindItem.setChecked(true);
    }

    public void setLabelVisibilityMode(int i) {
        if (this.menuView.getLabelVisibilityMode() != i) {
            this.menuView.setLabelVisibilityMode(i);
            this.presenter.updateMenuView(false);
        }
    }

    public int getLabelVisibilityMode() {
        return this.menuView.getLabelVisibilityMode();
    }

    public void setLayoutStyle(int i) {
        if (this.menuView.getLayoutStyle() != i) {
            this.menuView.setLayoutStyle(i);
            this.presenter.updateMenuView(false);
        }
    }

    public int getLayoutStyle() {
        return this.menuView.getLayoutStyle();
    }

    public void setItemTextAppearance(int i) {
        this.menuView.setItemTextAppearance(i);
    }

    public int getItemTextAppearance() {
        return this.menuView.getItemTextAppearance();
    }

    public void setItemTextMaxLine(int i) {
        this.menuView.setItemTextMaxLine(i);
    }

    public void setItemOnTouchListener(int i, View.OnTouchListener onTouchListener) {
        this.menuView.setItemOnTouchListener(i, onTouchListener);
    }

    public void setMinHeightDp(int i) {
        this.mMinHeightDp = i;
        setMinimumHeight(MiuixUIUtils.dp2px(getContext().getResources().getDisplayMetrics().density, this.mMinHeightDp));
    }

    public int getMinHeightDp() {
        return this.mMinHeightDp;
    }

    public void setMinHeightDpInWideStyle(int i) {
        this.mMinHeightDpInWideStyle = i;
        this.mMinHeightInWideStyle = MiuixUIUtils.dp2px(getContext().getResources().getDisplayMetrics().density, this.mMinHeightDpInWideStyle);
    }

    public int getMinHeightDpInWideStyle() {
        return this.mMinHeightDpInWideStyle;
    }

    public int getMinHeightInWideStyle() {
        return this.mMinHeightInWideStyle;
    }

    private MenuInflater getMenuInflater() {
        if (this.menuInflater == null) {
            this.menuInflater = new NavigationMenuInflater(getContext());
        }
        return this.menuInflater;
    }

    public NavigationBarPresenter getPresenter() {
        return this.presenter;
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.menuPresenterState = new Bundle();
        this.menu.savePresenterStates(savedState.menuPresenterState);
        return savedState;
    }

    @Override // android.view.View
    protected void onRestoreInstanceState(Parcelable parcelable) {
        if (!(parcelable instanceof SavedState)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.menu.restorePresenterStates(savedState.menuPresenterState);
    }

    static class SavedState extends AbsSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() { // from class: miuix.navigator.navigation.NavigationBarView.SavedState.1
            @Override // android.os.Parcelable.ClassLoaderCreator
            public SavedState createFromParcel(Parcel parcel, ClassLoader classLoader) {
                return new SavedState(parcel, classLoader);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel, null);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        Bundle menuPresenterState;

        public SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        public SavedState(Parcel parcel, ClassLoader classLoader) {
            super(parcel, classLoader);
            readFromParcel(parcel, classLoader == null ? getClass().getClassLoader() : classLoader);
        }

        @Override // androidx.customview.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeBundle(this.menuPresenterState);
        }

        private void readFromParcel(Parcel parcel, ClassLoader classLoader) {
            this.menuPresenterState = parcel.readBundle(classLoader);
        }
    }
}
