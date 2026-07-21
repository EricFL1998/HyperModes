package miuix.navigator.navigation;

import android.R;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.view.SupportMenuInflater;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.customview.view.AbsSavedState;
import miuix.appcompat.view.menu.MenuBuilder;
import miuix.appcompat.view.menu.MenuItemImpl;
import miuix.navigator.navigation.internal.NavigationMenu;
import miuix.navigator.navigation.internal.NavigationMenuPresenter;

/* JADX INFO: loaded from: classes3.dex */
public class NavigationView extends FrameLayout {
    private static final int PRESENTER_NAVIGATION_VIEW_ID = 1;
    private boolean bottomInsetScrimEnabled;
    private int layoutGravity;
    OnNavigationItemSelectedListener listener;
    private final int maxWidth;
    private final NavigationMenu menu;
    private MenuInflater menuInflater;
    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;
    private final NavigationMenuPresenter presenter;
    private final int[] tmpLocation;
    private boolean topInsetScrimEnabled;
    private static final int[] CHECKED_STATE_SET = {R.attr.state_checked};
    private static final int[] DISABLED_STATE_SET = {-16842910};
    private static final int DEF_STYLE_RES = miuix.navigator.R.style.Widget_MiuixDesign_NavigationView;

    public interface OnNavigationItemSelectedListener {
        boolean onNavigationItemSelected(MenuItem menuItem);
    }

    public NavigationView(Context context) {
        this(context, null);
    }

    public NavigationView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, miuix.navigator.R.attr.miuixNavigationViewStyle);
    }

    public NavigationView(Context context, AttributeSet attributeSet, int i) {
        ColorStateList colorStateListCreateDefaultColorStateList;
        super(context, attributeSet, i);
        NavigationMenuPresenter navigationMenuPresenter = new NavigationMenuPresenter();
        this.presenter = navigationMenuPresenter;
        this.tmpLocation = new int[2];
        this.topInsetScrimEnabled = true;
        this.bottomInsetScrimEnabled = true;
        this.layoutGravity = 0;
        Context context2 = getContext();
        NavigationMenu navigationMenu = new NavigationMenu(context2);
        this.menu = navigationMenu;
        TypedArray typedArrayObtainStyledAttributes = context2.obtainStyledAttributes(attributeSet, miuix.navigator.R.styleable.MiuixNavigationView, i, DEF_STYLE_RES);
        if (typedArrayObtainStyledAttributes.hasValue(miuix.navigator.R.styleable.MiuixNavigationView_android_background)) {
            ViewCompat.setBackground(this, typedArrayObtainStyledAttributes.getDrawable(miuix.navigator.R.styleable.MiuixNavigationView_android_background));
        }
        this.layoutGravity = typedArrayObtainStyledAttributes.getInt(miuix.navigator.R.styleable.MiuixNavigationView_android_layout_gravity, 0);
        if (typedArrayObtainStyledAttributes.hasValue(miuix.navigator.R.styleable.MiuixNavigationView_elevation)) {
            setElevation(typedArrayObtainStyledAttributes.getDimensionPixelSize(miuix.navigator.R.styleable.MiuixNavigationView_elevation, 0));
        }
        setFitsSystemWindows(typedArrayObtainStyledAttributes.getBoolean(miuix.navigator.R.styleable.MiuixNavigationView_android_fitsSystemWindows, false));
        this.maxWidth = typedArrayObtainStyledAttributes.getDimensionPixelSize(miuix.navigator.R.styleable.MiuixNavigationView_android_maxWidth, 0);
        ColorStateList colorStateList = typedArrayObtainStyledAttributes.hasValue(miuix.navigator.R.styleable.MiuixNavigationView_miuixSubheaderColor) ? typedArrayObtainStyledAttributes.getColorStateList(miuix.navigator.R.styleable.MiuixNavigationView_miuixSubheaderColor) : null;
        int resourceId = typedArrayObtainStyledAttributes.hasValue(miuix.navigator.R.styleable.MiuixNavigationView_miuixSubheaderTextAppearance) ? typedArrayObtainStyledAttributes.getResourceId(miuix.navigator.R.styleable.MiuixNavigationView_miuixSubheaderTextAppearance, 0) : 0;
        if (resourceId == 0 && colorStateList == null) {
            colorStateList = createDefaultColorStateList(R.attr.textColorSecondary);
        }
        if (typedArrayObtainStyledAttributes.hasValue(miuix.navigator.R.styleable.MiuixNavigationView_miuixItemIconTint)) {
            colorStateListCreateDefaultColorStateList = typedArrayObtainStyledAttributes.getColorStateList(miuix.navigator.R.styleable.MiuixNavigationView_miuixItemIconTint);
        } else {
            colorStateListCreateDefaultColorStateList = createDefaultColorStateList(R.attr.textColorSecondary);
        }
        int resourceId2 = typedArrayObtainStyledAttributes.hasValue(miuix.navigator.R.styleable.MiuixNavigationView_miuixItemTextAppearance) ? typedArrayObtainStyledAttributes.getResourceId(miuix.navigator.R.styleable.MiuixNavigationView_miuixItemTextAppearance, 0) : 0;
        if (typedArrayObtainStyledAttributes.hasValue(miuix.navigator.R.styleable.MiuixNavigationView_miuixItemIconSize)) {
            setItemIconSize(typedArrayObtainStyledAttributes.getDimensionPixelSize(miuix.navigator.R.styleable.MiuixNavigationView_miuixItemIconSize, 0));
        }
        ColorStateList colorStateList2 = typedArrayObtainStyledAttributes.hasValue(miuix.navigator.R.styleable.MiuixNavigationView_miuixItemTextColor) ? typedArrayObtainStyledAttributes.getColorStateList(miuix.navigator.R.styleable.MiuixNavigationView_miuixItemTextColor) : null;
        if (resourceId2 == 0 && colorStateList2 == null) {
            colorStateList2 = createDefaultColorStateList(R.attr.textColorPrimary);
        }
        Drawable drawable = typedArrayObtainStyledAttributes.getDrawable(miuix.navigator.R.styleable.MiuixNavigationView_miuixItemBackground);
        if (typedArrayObtainStyledAttributes.hasValue(miuix.navigator.R.styleable.MiuixNavigationView_miuixItemHorizontalPadding)) {
            setItemHorizontalPadding(typedArrayObtainStyledAttributes.getDimensionPixelSize(miuix.navigator.R.styleable.MiuixNavigationView_miuixItemHorizontalPadding, 0));
        }
        if (typedArrayObtainStyledAttributes.hasValue(miuix.navigator.R.styleable.MiuixNavigationView_miuixItemVerticalPadding)) {
            setItemVerticalPadding(typedArrayObtainStyledAttributes.getDimensionPixelSize(miuix.navigator.R.styleable.MiuixNavigationView_miuixItemVerticalPadding, 0));
        }
        setDividerInsetStart(typedArrayObtainStyledAttributes.getDimensionPixelSize(miuix.navigator.R.styleable.MiuixNavigationView_miuixDividerInsetStart, 0));
        setDividerInsetEnd(typedArrayObtainStyledAttributes.getDimensionPixelSize(miuix.navigator.R.styleable.MiuixNavigationView_miuixDividerInsetEnd, 0));
        setSubheaderInsetStart(typedArrayObtainStyledAttributes.getDimensionPixelSize(miuix.navigator.R.styleable.MiuixNavigationView_miuixSubheaderInsetStart, 0));
        setSubheaderInsetEnd(typedArrayObtainStyledAttributes.getDimensionPixelSize(miuix.navigator.R.styleable.MiuixNavigationView_miuixSubheaderInsetEnd, 0));
        setTopInsetScrimEnabled(typedArrayObtainStyledAttributes.getBoolean(miuix.navigator.R.styleable.MiuixNavigationView_miuixTopInsetScrimEnabled, this.topInsetScrimEnabled));
        setBottomInsetScrimEnabled(typedArrayObtainStyledAttributes.getBoolean(miuix.navigator.R.styleable.MiuixNavigationView_miuixBottomInsetScrimEnabled, this.bottomInsetScrimEnabled));
        int dimensionPixelSize = typedArrayObtainStyledAttributes.getDimensionPixelSize(miuix.navigator.R.styleable.MiuixNavigationView_miuixItemIconPadding, 0);
        setItemMaxLines(typedArrayObtainStyledAttributes.getInt(miuix.navigator.R.styleable.MiuixNavigationView_miuixItemMaxLines, 1));
        navigationMenu.setCallback(new MenuBuilder.Callback() { // from class: miuix.navigator.navigation.NavigationView.1
            @Override // miuix.appcompat.view.menu.MenuBuilder.Callback
            public void onMenuModeChange(MenuBuilder menuBuilder) {
            }

            @Override // miuix.appcompat.view.menu.MenuBuilder.Callback
            public boolean onMenuItemSelected(MenuBuilder menuBuilder, MenuItem menuItem) {
                return NavigationView.this.listener != null && NavigationView.this.listener.onNavigationItemSelected(menuItem);
            }
        });
        navigationMenuPresenter.setId(1);
        navigationMenuPresenter.initForMenu(context2, navigationMenu);
        if (resourceId != 0) {
            navigationMenuPresenter.setSubheaderTextAppearance(resourceId);
        }
        navigationMenuPresenter.setSubheaderColor(colorStateList);
        navigationMenuPresenter.setItemIconTintList(colorStateListCreateDefaultColorStateList);
        navigationMenuPresenter.setOverScrollMode(getOverScrollMode());
        if (resourceId2 != 0) {
            navigationMenuPresenter.setItemTextAppearance(resourceId2);
        }
        navigationMenuPresenter.setItemTextColor(colorStateList2);
        navigationMenuPresenter.setItemBackground(drawable);
        navigationMenuPresenter.setItemIconPadding(dimensionPixelSize);
        navigationMenu.addMenuPresenter(navigationMenuPresenter);
        addView((View) navigationMenuPresenter.getMenuView(this));
        if (typedArrayObtainStyledAttributes.hasValue(miuix.navigator.R.styleable.MiuixNavigationView_menu)) {
            inflateMenu(typedArrayObtainStyledAttributes.getResourceId(miuix.navigator.R.styleable.MiuixNavigationView_menu, 0));
        }
        if (typedArrayObtainStyledAttributes.hasValue(miuix.navigator.R.styleable.MiuixNavigationView_miuixHeaderLayout)) {
            inflateHeaderView(typedArrayObtainStyledAttributes.getResourceId(miuix.navigator.R.styleable.MiuixNavigationView_miuixHeaderLayout, 0));
        }
        typedArrayObtainStyledAttributes.recycle();
    }

    @Override // android.view.View
    public void setOverScrollMode(int i) {
        super.setOverScrollMode(i);
        NavigationMenuPresenter navigationMenuPresenter = this.presenter;
        if (navigationMenuPresenter != null) {
            navigationMenuPresenter.setOverScrollMode(i);
        }
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.menuState = new Bundle();
        this.menu.savePresenterStates(savedState.menuState);
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
        this.menu.restorePresenterStates(savedState.menuState);
    }

    public void setNavigationItemSelectedListener(OnNavigationItemSelectedListener onNavigationItemSelectedListener) {
        this.listener = onNavigationItemSelectedListener;
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        int mode = View.MeasureSpec.getMode(i);
        if (mode == Integer.MIN_VALUE) {
            i = View.MeasureSpec.makeMeasureSpec(Math.min(View.MeasureSpec.getSize(i), this.maxWidth), BasicMeasure.EXACTLY);
        } else if (mode == 0) {
            i = View.MeasureSpec.makeMeasureSpec(this.maxWidth, BasicMeasure.EXACTLY);
        }
        super.onMeasure(i, i2);
    }

    public void inflateMenu(int i) {
        this.presenter.setUpdateSuspended(true);
        getMenuInflater().inflate(i, this.menu);
        this.presenter.setUpdateSuspended(false);
        this.presenter.updateMenuView(false);
    }

    public Menu getMenu() {
        return this.menu;
    }

    public View inflateHeaderView(int i) {
        return this.presenter.inflateHeaderView(i);
    }

    public void addHeaderView(View view) {
        this.presenter.addHeaderView(view);
    }

    public void removeHeaderView(View view) {
        this.presenter.removeHeaderView(view);
    }

    public int getHeaderCount() {
        return this.presenter.getHeaderCount();
    }

    public View getHeaderView(int i) {
        return this.presenter.getHeaderView(i);
    }

    public ColorStateList getItemIconTintList() {
        return this.presenter.getItemTintList();
    }

    public void setItemIconTintList(ColorStateList colorStateList) {
        this.presenter.setItemIconTintList(colorStateList);
    }

    public ColorStateList getItemTextColor() {
        return this.presenter.getItemTextColor();
    }

    public void setItemTextColor(ColorStateList colorStateList) {
        this.presenter.setItemTextColor(colorStateList);
    }

    public Drawable getItemBackground() {
        return this.presenter.getItemBackground();
    }

    public void setItemBackgroundResource(int i) {
        setItemBackground(ContextCompat.getDrawable(getContext(), i));
    }

    public void setItemBackground(Drawable drawable) {
        this.presenter.setItemBackground(drawable);
    }

    public int getItemHorizontalPadding() {
        return this.presenter.getItemHorizontalPadding();
    }

    public void setItemHorizontalPadding(int i) {
        this.presenter.setItemHorizontalPadding(i);
    }

    public void setItemHorizontalPaddingResource(int i) {
        this.presenter.setItemHorizontalPadding(getResources().getDimensionPixelSize(i));
    }

    public int getItemVerticalPadding() {
        return this.presenter.getItemVerticalPadding();
    }

    public void setItemVerticalPadding(int i) {
        this.presenter.setItemVerticalPadding(i);
    }

    public void setItemVerticalPaddingResource(int i) {
        this.presenter.setItemVerticalPadding(getResources().getDimensionPixelSize(i));
    }

    public int getItemIconPadding() {
        return this.presenter.getItemIconPadding();
    }

    public void setItemIconPadding(int i) {
        this.presenter.setItemIconPadding(i);
    }

    public void setItemIconPaddingResource(int i) {
        this.presenter.setItemIconPadding(getResources().getDimensionPixelSize(i));
    }

    public void setCheckedItem(int i) {
        MenuItem menuItemFindItem = this.menu.findItem(i);
        if (menuItemFindItem != null) {
            this.presenter.setCheckedItem((MenuItemImpl) menuItemFindItem);
        }
    }

    public void setCheckedItem(MenuItem menuItem) {
        MenuItem menuItemFindItem = this.menu.findItem(menuItem.getItemId());
        if (menuItemFindItem != null) {
            this.presenter.setCheckedItem((MenuItemImpl) menuItemFindItem);
            return;
        }
        throw new IllegalArgumentException("Called setCheckedItem(MenuItem) with an item that is not in the current menu.");
    }

    public MenuItem getCheckedItem() {
        return this.presenter.getCheckedItem();
    }

    public void setItemTextAppearance(int i) {
        this.presenter.setItemTextAppearance(i);
    }

    public void setItemIconSize(int i) {
        this.presenter.setItemIconSize(i);
    }

    public void setItemMaxLines(int i) {
        this.presenter.setItemMaxLines(i);
    }

    public int getItemMaxLines() {
        return this.presenter.getItemMaxLines();
    }

    public boolean isTopInsetScrimEnabled() {
        return this.topInsetScrimEnabled;
    }

    public void setTopInsetScrimEnabled(boolean z) {
        this.topInsetScrimEnabled = z;
    }

    public boolean isBottomInsetScrimEnabled() {
        return this.bottomInsetScrimEnabled;
    }

    public void setBottomInsetScrimEnabled(boolean z) {
        this.bottomInsetScrimEnabled = z;
    }

    public int getDividerInsetStart() {
        return this.presenter.getDividerInsetStart();
    }

    public void setDividerInsetStart(int i) {
        this.presenter.setDividerInsetStart(i);
    }

    public int getDividerInsetEnd() {
        return this.presenter.getDividerInsetEnd();
    }

    public void setDividerInsetEnd(int i) {
        this.presenter.setDividerInsetEnd(i);
    }

    public int getSubheaderInsetStart() {
        return this.presenter.getSubheaderInsetStart();
    }

    public void setSubheaderInsetStart(int i) {
        this.presenter.setSubheaderInsetStart(i);
    }

    public int getSubheaderInsetEnd() {
        return this.presenter.getSubheaderInsetEnd();
    }

    public void setSubheaderInsetEnd(int i) {
        this.presenter.setSubheaderInsetEnd(i);
    }

    private MenuInflater getMenuInflater() {
        if (this.menuInflater == null) {
            this.menuInflater = new SupportMenuInflater(getContext());
        }
        return this.menuInflater;
    }

    private ColorStateList createDefaultColorStateList(int i) {
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

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(this.onGlobalLayoutListener);
    }

    public static class SavedState extends AbsSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() { // from class: miuix.navigator.navigation.NavigationView.SavedState.1
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
        public Bundle menuState;

        public SavedState(Parcel parcel, ClassLoader classLoader) {
            super(parcel, classLoader);
            this.menuState = parcel.readBundle(classLoader);
        }

        public SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        @Override // androidx.customview.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeBundle(this.menuState);
        }
    }
}
