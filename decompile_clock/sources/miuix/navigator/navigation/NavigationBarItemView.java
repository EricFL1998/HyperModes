package miuix.navigator.navigation;

import android.R;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.PointerIconCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.widget.TextViewCompat;
import miuix.animation.Folme;
import miuix.animation.IFolme;
import miuix.animation.IHoverStyle;
import miuix.animation.ITouchStyle;
import miuix.animation.base.AnimConfig;
import miuix.appcompat.view.menu.MenuItemImpl;
import miuix.appcompat.view.menu.MenuView;
import miuix.appcompat.widget.BadgeDrawable;
import miuix.core.util.MiuixUIUtils;
import miuix.internal.util.MiuixResources;
import miuix.theme.Typography;

/* JADX INFO: loaded from: classes3.dex */
public abstract class NavigationBarItemView extends LinearLayout implements MenuView.ItemView {
    private static final int[] CHECKED_STATE_SET = {R.attr.state_checked};
    private static final float DARK_MODE_PRESSED_ALPHA_CHECKED = 0.53f;
    private static final float DARK_MODE_PRESSED_ALPHA_UNCHECKED = 0.8f;
    private static final int INVALID_ITEM_POSITION = -1;
    private static final int LABEL_WIDE_STYLE_MARGIN_START_DP = 6;
    private static final float LIGHT_MODE_PRESSED_ALPHA_CHECKED = 0.6f;
    private static final float LIGHT_MODE_PRESSED_ALPHA_UNCHECKED = 0.75f;
    private int activeIndicatorDesiredHeight;
    private int activeIndicatorDesiredWidth;
    private boolean activeIndicatorEnabled;
    private int activeIndicatorMarginHorizontal;
    private boolean activeIndicatorResizeable;

    @Deprecated
    private View activeIndicatorView;
    private ImageView icon;
    private FrameLayout iconContainer;
    private ColorStateList iconTint;
    private boolean initialized;
    private boolean isShifting;
    Drawable itemBackground;
    private MenuItemImpl itemData;
    private IFolme itemFolmeInvoke;
    private int itemPaddingBottom;
    private int itemPaddingTop;
    private int itemPosition;
    private ColorStateList itemTouchColor;
    private TextView label;
    private int labelVisibilityMode;
    protected int layoutStyle;
    private View mBadgeAnchorView;
    private BadgeDrawable mBadgeDrawable;
    private Drawable originalIconDrawable;

    @Deprecated
    private float scaleDownFactor;

    @Deprecated
    private float scaleUpFactor;

    @Deprecated
    private float shiftAmount;
    private Drawable wrappedIconDrawable;

    protected abstract int getItemLayoutResId();

    @Override // miuix.appcompat.view.menu.MenuView.ItemView
    public boolean prefersCondensedTitle() {
        return false;
    }

    @Override // miuix.appcompat.view.menu.MenuView.ItemView
    public void setShortcut(boolean z, char c) {
    }

    @Override // miuix.appcompat.view.menu.MenuView.ItemView
    public boolean showsIcon() {
        return true;
    }

    public NavigationBarItemView(Context context) {
        this(context, 0);
    }

    public NavigationBarItemView(Context context, int i) {
        super(context);
        this.initialized = false;
        this.itemPosition = -1;
        this.activeIndicatorEnabled = false;
        this.activeIndicatorDesiredWidth = 0;
        this.activeIndicatorDesiredHeight = 0;
        this.activeIndicatorResizeable = false;
        this.activeIndicatorMarginHorizontal = 0;
        this.layoutStyle = i;
        LayoutInflater.from(context).inflate(getItemLayoutResId(), (ViewGroup) this, true);
        this.iconContainer = (FrameLayout) findViewById(miuix.navigator.R.id.miuix_navigation_bar_item_icon_container);
        this.activeIndicatorView = findViewById(miuix.navigator.R.id.miuix_navigation_bar_item_active_indicator_view);
        this.icon = (ImageView) findViewById(miuix.navigator.R.id.miuix_navigation_bar_item_icon_view);
        this.label = (TextView) findViewById(miuix.navigator.R.id.miuix_navigation_bar_item_label_view);
        setBackgroundResource(getItemBackgroundResId());
        this.itemPaddingTop = getResources().getDimensionPixelSize(getItemDefaultMarginResId());
        this.itemPaddingBottom = this.label.getPaddingBottom();
        ViewCompat.setImportantForAccessibility(this.label, 2);
        setFocusable(true);
        updateLayout();
    }

    public void setLayoutStyle(int i) {
        this.layoutStyle = i;
    }

    public int getLayoutStyle() {
        return this.layoutStyle;
    }

    protected boolean isLayoutWideLandStyle() {
        return this.layoutStyle == 3;
    }

    public void updateLayout() {
        int i;
        int i2;
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.label.getLayoutParams();
        int iDp2px = 0;
        if (isLayoutWideLandStyle()) {
            i2 = 17;
            iDp2px = MiuixUIUtils.dp2px(getContext().getResources().getDisplayMetrics().density, 6.0f);
            i = 0;
        } else {
            i = 1;
            i2 = 49;
        }
        layoutParams.setMarginStart(iDp2px);
        this.label.setLayoutParams(layoutParams);
        setOrientation(i);
        setGravity(i2);
    }

    @Override // android.view.View
    protected int getSuggestedMinimumWidth() {
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.label.getLayoutParams();
        return Math.max(getSuggestedIconWidth(), marginLayoutParams.leftMargin + this.label.getMeasuredWidth() + marginLayoutParams.rightMargin);
    }

    @Override // android.view.View
    protected int getSuggestedMinimumHeight() {
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.label.getLayoutParams();
        return getSuggestedIconHeight() + marginLayoutParams.topMargin + this.label.getMeasuredHeight() + marginLayoutParams.bottomMargin;
    }

    @Override // miuix.appcompat.view.menu.MenuView.ItemView
    public void initialize(MenuItemImpl menuItemImpl, int i) {
        this.itemData = menuItemImpl;
        setCheckable(menuItemImpl.isCheckable());
        setChecked(menuItemImpl.isChecked());
        setEnabled(menuItemImpl.isEnabled());
        setIcon(menuItemImpl.getIcon());
        setTitle(menuItemImpl.getTitle());
        setId(menuItemImpl.getItemId());
        if (!TextUtils.isEmpty(menuItemImpl.getContentDescription())) {
            setContentDescription(menuItemImpl.getContentDescription());
        }
        setVisibility(menuItemImpl.isVisible() ? 0 : 8);
        this.initialized = true;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        this.itemFolmeInvoke.touch().onMotionEvent(motionEvent);
        return super.onTouchEvent(motionEvent);
    }

    void clear() {
        this.itemData = null;
        this.initialized = false;
    }

    private View getIconOrContainer() {
        FrameLayout frameLayout = this.iconContainer;
        return frameLayout != null ? frameLayout : this.icon;
    }

    public void setItemPosition(int i) {
        this.itemPosition = i;
    }

    public int getItemPosition() {
        return this.itemPosition;
    }

    public void setShifting(boolean z) {
        if (this.isShifting != z) {
            this.isShifting = z;
            refreshChecked();
        }
    }

    public void setLabelVisibilityMode(int i) {
        if (this.labelVisibilityMode != i) {
            this.labelVisibilityMode = i;
            updateActiveIndicatorLayoutParams(getWidth());
            refreshChecked();
        }
    }

    @Override // miuix.appcompat.view.menu.MenuView.ItemView
    public MenuItemImpl getItemData() {
        return this.itemData;
    }

    @Override // miuix.appcompat.view.menu.MenuView.ItemView
    public void setTitle(CharSequence charSequence) {
        this.label.setText(charSequence);
        MenuItemImpl menuItemImpl = this.itemData;
        if (menuItemImpl == null || TextUtils.isEmpty(menuItemImpl.getContentDescription())) {
            setContentDescription(charSequence);
        }
    }

    @Override // miuix.appcompat.view.menu.MenuView.ItemView
    public void setCheckable(boolean z) {
        refreshDrawableState();
    }

    private void refreshChecked() {
        MenuItemImpl menuItemImpl = this.itemData;
        if (menuItemImpl != null) {
            setChecked(menuItemImpl.isChecked());
        }
    }

    private void configureFolme(boolean z) {
        float f;
        Resources resources = getResources();
        if (resources == null) {
            return;
        }
        if ((resources.getConfiguration().uiMode & 48) == 32) {
            f = z ? DARK_MODE_PRESSED_ALPHA_CHECKED : DARK_MODE_PRESSED_ALPHA_UNCHECKED;
        } else {
            f = z ? LIGHT_MODE_PRESSED_ALPHA_CHECKED : 0.75f;
        }
        IFolme iFolmeUseAt = Folme.useAt(this);
        this.itemFolmeInvoke = iFolmeUseAt;
        iFolmeUseAt.touch().setScale(1.0f, ITouchStyle.TouchType.DOWN).setScale(1.0f, ITouchStyle.TouchType.UP).setAlpha(f, ITouchStyle.TouchType.DOWN).setAlpha(1.0f, ITouchStyle.TouchType.UP).handleTouchOf(this, new AnimConfig[0]);
        this.itemFolmeInvoke.hover().setAlpha(1.0f, IHoverStyle.HoverType.ENTER).setEffect(IHoverStyle.HoverEffect.FLOATED_WRAPPED).handleHoverOf(this, new AnimConfig[0]);
    }

    @Override // miuix.appcompat.view.menu.MenuView.ItemView
    public void setChecked(final boolean z) {
        configureFolme(z);
        TextView textView = this.label;
        textView.setPivotX(textView.getWidth() / 2.0f);
        TextView textView2 = this.label;
        textView2.setPivotY(textView2.getBaseline());
        if (isInLayout()) {
            post(new Runnable() { // from class: miuix.navigator.navigation.NavigationBarItemView$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1914xb50c820f(z);
                }
            });
        } else {
            m1914xb50c820f(z);
        }
        boolean z2 = this.layoutStyle == 3;
        int i = this.labelVisibilityMode;
        if (i != -1) {
            if (i != 0) {
                if (i != 1) {
                    if (i == 2) {
                        if (z2) {
                            setViewVerticalMargin(getIconOrContainer(), 0, 0);
                        } else {
                            View iconOrContainer = getIconOrContainer();
                            int i2 = this.itemPaddingTop;
                            setViewVerticalMargin(iconOrContainer, i2, i2);
                        }
                        this.label.setVisibility(8);
                    }
                } else if (z) {
                    if (z2) {
                        setViewVerticalMargin(getIconOrContainer(), 0, 0);
                    } else {
                        setViewVerticalMargin(getIconOrContainer(), (int) (this.itemPaddingTop + this.shiftAmount), 0);
                    }
                    this.label.setSelected(true);
                } else {
                    if (z2) {
                        setViewVerticalMargin(getIconOrContainer(), 0, 0);
                    } else {
                        setViewVerticalMargin(getIconOrContainer(), this.itemPaddingTop, 0);
                    }
                    this.label.setSelected(false);
                }
            } else if (z) {
                if (z2) {
                    setViewVerticalMargin(getIconOrContainer(), 0, 0);
                    this.label.setSelected(true);
                } else {
                    setViewVerticalMargin(getIconOrContainer(), this.itemPaddingTop, 0);
                    this.label.setSelected(false);
                }
                this.label.setVisibility(0);
            } else {
                if (z2) {
                    setViewVerticalMargin(getIconOrContainer(), 0, 0);
                } else {
                    View iconOrContainer2 = getIconOrContainer();
                    int i3 = this.itemPaddingTop;
                    setViewVerticalMargin(iconOrContainer2, i3, i3);
                }
                this.label.setVisibility(4);
            }
        } else if (this.isShifting) {
            if (z) {
                if (z2) {
                    setViewVerticalMargin(getIconOrContainer(), 0, 0);
                } else {
                    setViewVerticalMargin(getIconOrContainer(), this.itemPaddingTop, 0);
                }
                this.label.setSelected(true);
                this.label.setVisibility(0);
            } else {
                if (z2) {
                    setViewVerticalMargin(getIconOrContainer(), 0, 0);
                } else {
                    View iconOrContainer3 = getIconOrContainer();
                    int i4 = this.itemPaddingTop;
                    setViewVerticalMargin(iconOrContainer3, i4, i4);
                }
                this.label.setSelected(false);
                this.label.setVisibility(4);
            }
        } else if (z) {
            if (z2) {
                setViewVerticalMargin(getIconOrContainer(), 0, 0);
            } else {
                setViewVerticalMargin(getIconOrContainer(), (int) (this.itemPaddingTop + this.shiftAmount), 0);
            }
            this.label.setSelected(true);
        } else {
            if (z2) {
                setViewVerticalMargin(getIconOrContainer(), 0, 0);
            } else {
                setViewVerticalMargin(getIconOrContainer(), this.itemPaddingTop, 0);
            }
            this.label.setSelected(false);
        }
        refreshDrawableState();
        setSelected(z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: setTextViewTypeFace, reason: merged with bridge method [inline-methods] */
    public void m1914xb50c820f(boolean z) {
        if (z) {
            Typography.applyMiSansDemibold(this.label);
        } else {
            Typography.applyMiSans(this.label);
        }
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        AccessibilityNodeInfoCompat accessibilityNodeInfoCompatWrap = AccessibilityNodeInfoCompat.wrap(accessibilityNodeInfo);
        accessibilityNodeInfoCompatWrap.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(0, 1, getItemVisiblePosition(), 1, false, isSelected()));
        if (isSelected()) {
            accessibilityNodeInfoCompatWrap.setClickable(false);
            accessibilityNodeInfoCompatWrap.removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK);
        }
        accessibilityNodeInfoCompatWrap.setRoleDescription(getResources().getString(miuix.navigator.R.string.miuix_item_view_role_description));
        if (hasBadge()) {
            StringBuilder sb = new StringBuilder();
            if (this.label.getText() != null) {
                sb.append(this.label.getText());
            }
            sb.append(getResources().getString(miuix.appcompat.R.string.miuix_appcompat_accessibility_new_message));
            accessibilityNodeInfoCompatWrap.setContentDescription(sb);
        }
    }

    private int getItemVisiblePosition() {
        ViewGroup viewGroup = (ViewGroup) getParent();
        int iIndexOfChild = viewGroup.indexOfChild(this);
        int i = 0;
        for (int i2 = 0; i2 < iIndexOfChild; i2++) {
            View childAt = viewGroup.getChildAt(i2);
            if ((childAt instanceof NavigationBarItemView) && childAt.getVisibility() == 0) {
                i++;
            }
        }
        return i;
    }

    private static void setViewVerticalMargin(View view, int i, int i2) {
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        marginLayoutParams.topMargin = i;
        marginLayoutParams.bottomMargin = i2;
        view.setLayoutParams(marginLayoutParams);
    }

    private static void setViewTopMarginAndGravity(View view, int i, int i2) {
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        marginLayoutParams.topMargin = i;
        marginLayoutParams.bottomMargin = i;
        if (marginLayoutParams instanceof FrameLayout.LayoutParams) {
            ((FrameLayout.LayoutParams) marginLayoutParams).gravity = i2;
        }
        view.setLayoutParams(marginLayoutParams);
    }

    private static void setViewScaleValues(View view, float f, float f2, int i) {
        view.setScaleX(f);
        view.setScaleY(f2);
        view.setVisibility(i);
    }

    private static void updateViewPaddingBottom(View view, int i) {
        view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), i);
    }

    @Override // android.view.View, miuix.appcompat.view.menu.MenuView.ItemView
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        this.label.setEnabled(z);
        this.icon.setEnabled(z);
        if (z) {
            ViewCompat.setPointerIcon(this, PointerIconCompat.getSystemIcon(getContext(), 1002));
        } else {
            ViewCompat.setPointerIcon(this, null);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public int[] onCreateDrawableState(int i) {
        int[] iArrOnCreateDrawableState = super.onCreateDrawableState(i + 1);
        MenuItemImpl menuItemImpl = this.itemData;
        if (menuItemImpl != null && menuItemImpl.isCheckable() && this.itemData.isChecked()) {
            mergeDrawableStates(iArrOnCreateDrawableState, CHECKED_STATE_SET);
        }
        return iArrOnCreateDrawableState;
    }

    @Override // miuix.appcompat.view.menu.MenuView.ItemView
    public void setIcon(Drawable drawable) {
        if (drawable == this.originalIconDrawable) {
            return;
        }
        this.originalIconDrawable = drawable;
        if (drawable != null) {
            Drawable.ConstantState constantState = drawable.getConstantState();
            if (constantState != null) {
                drawable = constantState.newDrawable();
            }
            drawable = DrawableCompat.wrap(drawable).mutate();
            this.wrappedIconDrawable = drawable;
            ColorStateList colorStateList = this.iconTint;
            if (colorStateList != null) {
                DrawableCompat.setTintList(drawable, colorStateList);
            }
        }
        this.icon.setImageDrawable(drawable);
    }

    public void setIconTintList(ColorStateList colorStateList) {
        Drawable drawable;
        this.iconTint = colorStateList;
        if (this.itemData == null || (drawable = this.wrappedIconDrawable) == null) {
            return;
        }
        DrawableCompat.setTintList(drawable, colorStateList);
        this.wrappedIconDrawable.invalidateSelf();
    }

    public void setIconSize(int i) {
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.icon.getLayoutParams();
        marginLayoutParams.width = i;
        marginLayoutParams.height = i;
        this.icon.setLayoutParams(marginLayoutParams);
    }

    public void setLabelSize(int i) {
        this.label.setTextSize(0, i);
    }

    public void setLabelMaxLine(int i) {
        this.label.setMaxLines(i);
    }

    public void setTextAppearance(int i) {
        setTextAppearanceWithoutFontScaling(this.label, i);
    }

    private static void setTextAppearanceWithoutFontScaling(TextView textView, int i) {
        TextViewCompat.setTextAppearance(textView, i);
        int unscaledTextSize = MiuixResources.getUnscaledTextSize(textView.getContext(), i, 0);
        if (unscaledTextSize != 0) {
            textView.setTextSize(0, unscaledTextSize);
        }
    }

    public void setTextColor(ColorStateList colorStateList) {
        if (colorStateList != null) {
            this.label.setTextColor(colorStateList);
        }
    }

    @Deprecated
    private void calculateTextScaleFactors(float f, float f2) {
        this.shiftAmount = f - f2;
        this.scaleUpFactor = (f2 * 1.0f) / f;
        this.scaleDownFactor = (f * 1.0f) / f2;
    }

    public void setItemBackground(int i) {
        setItemBackground(i == 0 ? null : ContextCompat.getDrawable(getContext(), i));
    }

    public void setItemBackground(Drawable drawable) {
        if (drawable != null && drawable.getConstantState() != null) {
            drawable = drawable.getConstantState().newDrawable().mutate();
        }
        this.itemBackground = drawable;
        refreshItemBackground();
    }

    public void setItemTouchColor(ColorStateList colorStateList) {
        this.itemTouchColor = colorStateList;
        if (colorStateList != null) {
            this.itemFolmeInvoke.touch().setTint(this.itemTouchColor.getDefaultColor());
        }
        refreshItemBackground();
    }

    private void refreshItemBackground() {
        Drawable drawable = this.itemBackground;
        FrameLayout frameLayout = this.iconContainer;
        if (frameLayout != null) {
            ViewCompat.setBackground(frameLayout, null);
        }
        ViewCompat.setBackground(this, drawable);
        setDefaultFocusHighlightEnabled(true);
    }

    public void setItemPaddingTop(int i) {
        if (this.itemPaddingTop != i) {
            this.itemPaddingTop = i;
            refreshChecked();
        }
    }

    public void setItemPaddingBottom(int i) {
        if (this.itemPaddingBottom != i) {
            this.itemPaddingBottom = i;
            refreshChecked();
        }
    }

    public void setActiveIndicatorEnabled(boolean z) {
        this.activeIndicatorEnabled = z;
        refreshItemBackground();
        View view = this.activeIndicatorView;
        if (view != null) {
            view.setVisibility(z ? 0 : 8);
            requestLayout();
        }
    }

    public void setActiveIndicatorWidth(int i) {
        this.activeIndicatorDesiredWidth = i;
        updateActiveIndicatorLayoutParams(getWidth());
    }

    private void updateActiveIndicatorLayoutParams(int i) {
        if (this.activeIndicatorView == null) {
            return;
        }
        int iMin = Math.min(this.activeIndicatorDesiredWidth, i - (this.activeIndicatorMarginHorizontal * 2));
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.activeIndicatorView.getLayoutParams();
        marginLayoutParams.height = isActiveIndicatorResizeableAndUnlabeled() ? iMin : this.activeIndicatorDesiredHeight;
        marginLayoutParams.width = iMin;
        this.activeIndicatorView.setLayoutParams(marginLayoutParams);
    }

    private boolean isActiveIndicatorResizeableAndUnlabeled() {
        return this.activeIndicatorResizeable && this.labelVisibilityMode == 2;
    }

    public void setActiveIndicatorHeight(int i) {
        this.activeIndicatorDesiredHeight = i;
        updateActiveIndicatorLayoutParams(getWidth());
    }

    public void setActiveIndicatorMarginHorizontal(int i) {
        this.activeIndicatorMarginHorizontal = i;
        updateActiveIndicatorLayoutParams(getWidth());
    }

    public Drawable getActiveIndicatorDrawable() {
        View view = this.activeIndicatorView;
        if (view == null) {
            return null;
        }
        return view.getBackground();
    }

    public void setActiveIndicatorDrawable(Drawable drawable) {
        View view = this.activeIndicatorView;
        if (view == null) {
            return;
        }
        view.setBackgroundDrawable(drawable);
        refreshItemBackground();
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        FrameLayout frameLayout = this.iconContainer;
        if (frameLayout != null && this.activeIndicatorEnabled) {
            frameLayout.dispatchTouchEvent(motionEvent);
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    public void setActiveIndicatorResizeable(boolean z) {
        this.activeIndicatorResizeable = z;
    }

    private int getSuggestedIconWidth() {
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) getIconOrContainer().getLayoutParams();
        return Math.max(0, marginLayoutParams.leftMargin) + this.icon.getMeasuredWidth() + Math.max(0, marginLayoutParams.rightMargin);
    }

    private int getSuggestedIconHeight() {
        return Math.max(0, ((ViewGroup.MarginLayoutParams) getIconOrContainer().getLayoutParams()).topMargin) + this.icon.getMeasuredWidth();
    }

    protected int getItemBackgroundResId() {
        return miuix.navigator.R.drawable.miuix_base_navigation_bar_item_background;
    }

    protected int getItemDefaultMarginResId() {
        return miuix.navigator.R.dimen.miuix_base_navigation_bar_item_default_margin;
    }

    public void setBadge(BadgeDrawable badgeDrawable) {
        TextView textView;
        if (this.mBadgeDrawable == badgeDrawable) {
            return;
        }
        setClipToPadding(false);
        setClipChildren(false);
        if (isLayoutWideLandStyle() && (textView = this.label) != null && textView.getVisibility() == 0) {
            badgeDrawable.attachBadgeDrawable(this.label, 2);
            this.mBadgeAnchorView = this.label;
        } else {
            ImageView imageView = this.icon;
            if (imageView != null) {
                badgeDrawable.attachBadgeDrawable(imageView, 2);
                this.mBadgeAnchorView = this.icon;
            }
        }
        this.mBadgeDrawable = badgeDrawable;
    }

    public void removeBadge(BadgeDrawable badgeDrawable) {
        if (hasBadge()) {
            setClipChildren(true);
            setClipToPadding(true);
            badgeDrawable.detachBadgeDrawable(this.mBadgeAnchorView);
            this.mBadgeDrawable = null;
            this.mBadgeAnchorView = null;
        }
    }

    private boolean hasBadge() {
        return this.mBadgeDrawable != null;
    }
}
