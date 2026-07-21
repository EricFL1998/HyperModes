package miuix.navigator.bottomnavigation;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import miuix.animation.Folme;
import miuix.animation.base.AnimConfig;
import miuix.core.util.EnvStateManager;
import miuix.core.util.HyperMaterialUtils;
import miuix.core.util.MaterialConfig;
import miuix.core.util.MaterialDayNightConfig;
import miuix.core.util.MiuixUIUtils;
import miuix.core.util.RomUtils;
import miuix.internal.util.AttributeResolver;
import miuix.internal.util.ViewUtils;
import miuix.navigator.R;
import miuix.navigator.navigation.NavigationBarMenuView;
import miuix.navigator.navigation.NavigationBarView;
import miuix.theme.token.hypermaterial.Blur;
import miuix.theme.token.hypermaterial.Mask;
import miuix.view.BlurableWidget;
import miuix.view.MiuiBlurUiHelper;
import miuix.view.WindowInsetsController;
import miuix.view.WindowInsetsState;

/* JADX INFO: loaded from: classes3.dex */
public class BottomNavigationView extends NavigationBarView implements BlurableWidget, WindowInsetsState, WindowInsetsController {
    private static final int MAX_ITEM_COUNT = 5;
    private Activity mActivityContext;
    private boolean mApplyBlur;
    private Drawable mBackgroundDivider;
    private Drawable mBackgroundInBlur;
    private boolean mBackgroundIsVisible;
    private Drawable mBackgroundWithoutBlur;
    private final MiuiBlurUiHelper mBlurUiHelper;
    private boolean mIgnoreLeftInsets;
    private boolean mIgnoreRightInsets;
    private WindowInsetsController.InsetsConfig mInsetsConfig;
    private boolean mInsetsIgnoringVisibility;
    private boolean mLargeFontAdaptationEnabled;
    private MaterialDayNightConfig mMaterial;
    private boolean mNeedApplyBlurBeforeDetach;

    @Deprecated
    public interface OnNavigationItemReselectedListener extends NavigationBarView.OnItemReselectedListener {
    }

    @Deprecated
    public interface OnNavigationItemSelectedListener extends NavigationBarView.OnItemSelectedListener {
    }

    @Override // miuix.navigator.navigation.NavigationBarView
    public int getMaxItemCount() {
        return 5;
    }

    public BottomNavigationView(Context context) {
        this(context, null);
    }

    public BottomNavigationView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.miuixBottomNavigationStyle);
    }

    public BottomNavigationView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, R.style.Widget_MiuixDesign_BottomNavigationView);
    }

    public BottomNavigationView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mBackgroundInBlur = null;
        this.mApplyBlur = false;
        this.mNeedApplyBlurBeforeDetach = false;
        this.mInsetsIgnoringVisibility = false;
        this.mLargeFontAdaptationEnabled = false;
        this.mIgnoreLeftInsets = false;
        this.mIgnoreRightInsets = false;
        Context context2 = getContext();
        TypedArray typedArrayObtainStyledAttributes = context2.obtainStyledAttributes(attributeSet, R.styleable.MiuixBottomNavigationView, i, i2);
        setItemHorizontalTranslationEnabled(typedArrayObtainStyledAttributes.getBoolean(R.styleable.MiuixBottomNavigationView_miuixItemHorizontalTranslationEnabled, true));
        boolean z = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MiuixBottomNavigationView_largeFontAdaptationEnabled, true) && MiuixUIUtils.getFontLevel(context2) == 2;
        this.mLargeFontAdaptationEnabled = z;
        if (z) {
            if (typedArrayObtainStyledAttributes.hasValue(R.styleable.MiuixBottomNavigationView_minHeightInLargeFont)) {
                setMinHeightDp(MiuixUIUtils.getDefDimen(context2, typedArrayObtainStyledAttributes.getResourceId(R.styleable.MiuixBottomNavigationView_minHeightInLargeFont, 0)));
            }
        } else if (typedArrayObtainStyledAttributes.hasValue(R.styleable.MiuixBottomNavigationView_android_minHeight)) {
            setMinHeightDp(MiuixUIUtils.getDefDimen(context2, typedArrayObtainStyledAttributes.getResourceId(R.styleable.MiuixBottomNavigationView_android_minHeight, 0)));
        }
        if (this.mLargeFontAdaptationEnabled) {
            if (typedArrayObtainStyledAttributes.hasValue(R.styleable.MiuixBottomNavigationView_miuixMinHeightInWideStyleInLargeFont)) {
                setMinHeightDpInWideStyle(MiuixUIUtils.getDefDimen(context2, typedArrayObtainStyledAttributes.getResourceId(R.styleable.MiuixBottomNavigationView_miuixMinHeightInWideStyleInLargeFont, 0)));
            }
        } else if (typedArrayObtainStyledAttributes.hasValue(R.styleable.MiuixBottomNavigationView_miuixMinHeightInWideStyle)) {
            setMinHeightDpInWideStyle(MiuixUIUtils.getDefDimen(context2, typedArrayObtainStyledAttributes.getResourceId(R.styleable.MiuixBottomNavigationView_miuixMinHeightInWideStyle, 0)));
        }
        typedArrayObtainStyledAttributes.recycle();
        applyWindowInsets();
        setClickable(true);
        setImportantForAccessibility(2);
        this.mBackgroundIsVisible = true;
        this.mBackgroundDivider = context2.getDrawable(R.drawable.bottom_navigation_background_divider);
        this.mBackgroundWithoutBlur = getBackground();
        final int iResolveColor = AttributeResolver.resolveColor(context2, R.attr.miuixColorBottomSurface);
        if (HyperMaterialUtils.isEnable()) {
            this.mMaterial = MaterialDayNightConfig.create(RomUtils.getHyperOsVersion() > 2 ? Mask.Pured_Regular : Blur.ExtraHeavy);
            this.mBlurUiHelper = new MiuiBlurUiHelper(context2, this, false, false, new MiuiBlurUiHelper.BlurStateCallback() { // from class: miuix.navigator.bottomnavigation.BottomNavigationView.1
                final boolean isDarkThemeOverlay;

                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public void onBlurEnableStateChanged(boolean z2) {
                }

                {
                    this.isDarkThemeOverlay = MiuixUIUtils.isDarkThemeOverlay(BottomNavigationView.this.getContext(), miuix.appcompat.R.color.miuix_default_color_on_surface_light);
                }

                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public boolean isLightTheme() {
                    int i3 = iResolveColor;
                    if (i3 != 0) {
                        return MiuixUIUtils.isLightColor(i3) && !this.isDarkThemeOverlay;
                    }
                    return !this.isDarkThemeOverlay && AttributeResolver.resolveBoolean(BottomNavigationView.this.getContext(), miuix.appcompat.R.attr.isLightTheme, true);
                }

                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public Drawable getBackground() {
                    return new ColorDrawable(iResolveColor);
                }

                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public int getBackgroundColor() {
                    if (this.isDarkThemeOverlay) {
                        return AttributeResolver.resolveColor(BottomNavigationView.this.getContext(), miuix.theme.R.attr.colorSurface, 0);
                    }
                    return 0;
                }

                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public MaterialConfig.BlurConfig getBlurConfig(boolean z2) {
                    MaterialDayNightConfig materialDayNightConfig = BottomNavigationView.this.mMaterial;
                    if (materialDayNightConfig != null) {
                        return materialDayNightConfig.getBlurConfig(z2);
                    }
                    return null;
                }

                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public void onBlurApplyStateChanged(boolean z2) {
                    BottomNavigationView.this.mApplyBlur = z2;
                    BottomNavigationView.this.updateBackground();
                }
            });
        } else {
            this.mBlurUiHelper = null;
        }
    }

    @Override // miuix.view.HyperMaterialWidget
    public void setMaterial(MaterialDayNightConfig materialDayNightConfig) {
        boolean z = this.mMaterial == null && materialDayNightConfig != null;
        if (materialDayNightConfig == null) {
            this.mMaterial = null;
            applyBlur(false);
            return;
        }
        this.mMaterial = materialDayNightConfig;
        if (this.mBlurUiHelper != null) {
            if (!isApplyBlur() && z) {
                applyBlur(true);
            }
            this.mBlurUiHelper.onConfigChanged();
        }
    }

    @Override // miuix.view.HyperMaterialWidget
    public MaterialDayNightConfig getMaterial() {
        return this.mMaterial;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        View childAt = ((ViewGroup) getRootView()).getChildAt(0);
        if (childAt.getContext() instanceof Activity) {
            this.mActivityContext = (Activity) childAt.getContext();
        }
        MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurUiHelper;
        if (miuiBlurUiHelper != null) {
            miuiBlurUiHelper.onConfigChanged();
            applyBlur(this.mNeedApplyBlurBeforeDetach);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        this.mNeedApplyBlurBeforeDetach = isApplyBlur();
        applyBlur(false);
        super.onDetachedFromWindow();
    }

    @Override // miuix.navigator.navigation.NavigationBarView, android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        setMinHeightDp(getMinHeightDp());
        setMinHeightDpInWideStyle(getMinHeightDpInWideStyle());
        MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurUiHelper;
        if (miuiBlurUiHelper != null) {
            miuiBlurUiHelper.onConfigChanged();
        }
    }

    @Override // miuix.navigator.navigation.NavigationBarView, miuix.navigator.BottomNavigation
    public void setLayoutStyle(int i) {
        if (!this.mLargeFontAdaptationEnabled || i == 3) {
            setItemTextMaxLine(1);
        } else {
            setItemTextMaxLine(2);
        }
        super.setLayoutStyle(i);
    }

    private void applyWindowInsets() {
        ViewUtils.doOnApplyWindowInsets(this, new ViewUtils.OnApplyWindowInsetsListener() { // from class: miuix.navigator.bottomnavigation.BottomNavigationView$$ExternalSyntheticLambda0
            @Override // miuix.internal.util.ViewUtils.OnApplyWindowInsetsListener
            public final WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat, ViewUtils.RelativePadding relativePadding) {
                return this.f$0.m1910x8ac49f6(view, windowInsetsCompat, relativePadding);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$applyWindowInsets$0$miuix-navigator-bottomnavigation-BottomNavigationView, reason: not valid java name */
    /* synthetic */ WindowInsetsCompat m1910x8ac49f6(View view, WindowInsetsCompat windowInsetsCompat, ViewUtils.RelativePadding relativePadding) {
        int i;
        WindowInsetsController.InsetsConfig insetsConfig;
        boolean zIsLayoutHideNavigation = MiuixUIUtils.isLayoutHideNavigation(view);
        WindowInsetsCompat rootWindowInsets = ViewCompat.getRootWindowInsets(view);
        if (rootWindowInsets != null) {
            int i2 = 0;
            if (!zIsLayoutHideNavigation && ((insetsConfig = this.mInsetsConfig) == null || !insetsConfig.renderUnderBottomDecorations)) {
                i = 0;
            } else if (this.mInsetsIgnoringVisibility) {
                i = rootWindowInsets.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.systemBars()).bottom;
            } else {
                i = rootWindowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
            }
            boolean z = ViewCompat.getLayoutDirection(view) == 1;
            Insets insets = rootWindowInsets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            int i3 = insets.left;
            int i4 = insets.right;
            Insets insets2 = rootWindowInsets.getInsets(WindowInsetsCompat.Type.displayCutout());
            Insets insets3 = rootWindowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            if (this.mIgnoreLeftInsets || i3 == insets2.left || (!zIsLayoutHideNavigation && i3 == insets3.left)) {
                i3 = 0;
            }
            if (this.mIgnoreRightInsets || i4 == insets2.right || (!zIsLayoutHideNavigation && i4 == insets3.right)) {
                i4 = 0;
            }
            WindowInsetsController.InsetsConfig insetsConfig2 = this.mInsetsConfig;
            if (insetsConfig2 == null) {
                i2 = i;
            } else if (insetsConfig2.isFloatingMode) {
                i4 = 0;
                i3 = 0;
            } else {
                if (this.mInsetsConfig.ignoreBottomInset) {
                    i = 0;
                }
                if (this.mInsetsConfig.ignoreLeftInset) {
                    i3 = 0;
                }
                if (this.mInsetsConfig.ignoreRightInset) {
                    i4 = 0;
                }
                i2 = i;
            }
            relativePadding.bottom += i2;
            relativePadding.start += z ? i4 : i3;
            int i5 = relativePadding.end;
            if (!z) {
                i3 = i4;
            }
            relativePadding.end = i5 + i3;
            relativePadding.applyToView(view);
        }
        return windowInsetsCompat;
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, makeMinHeightSpec(i2));
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        WindowInsetsCompat rootWindowInsets;
        Insets insets;
        super.onLayout(z, i, i2, i3, i4);
        int i5 = EnvStateManager.getScreenSize(getContext()).x;
        if (i5 == -1 || (rootWindowInsets = ViewCompat.getRootWindowInsets(this)) == null) {
            return;
        }
        if (this.mInsetsIgnoringVisibility) {
            insets = rootWindowInsets.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
        } else {
            insets = rootWindowInsets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
        }
        int[] iArr = new int[2];
        getLocationOnScreen(iArr);
        boolean z2 = false;
        int i6 = iArr[0];
        int width = i5 - (getWidth() + i6);
        boolean z3 = true;
        boolean z4 = i6 >= insets.left;
        boolean z5 = width >= insets.right;
        if (this.mIgnoreLeftInsets != z4) {
            this.mIgnoreLeftInsets = z4;
            z2 = true;
        }
        if (this.mIgnoreRightInsets != z5) {
            this.mIgnoreRightInsets = z5;
        } else {
            z3 = z2;
        }
        if (z3) {
            requestApplyInsets();
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mBackgroundIsVisible && this.mApplyBlur) {
            this.mBackgroundDivider.setBounds(0, 0, getMeasuredWidth(), this.mBackgroundDivider.getIntrinsicHeight());
            this.mBackgroundDivider.draw(canvas);
        }
    }

    @Override // miuix.navigator.navigation.NavigationBarView
    public void hide(boolean z, boolean z2) {
        super.hide(z, z2);
        applyBlur(false);
        this.mNeedApplyBlurBeforeDetach = false;
        if (isEnableBlur()) {
            if (z2) {
                AnimConfig animConfig = new AnimConfig();
                animConfig.setEase(-2, 1.0f, 0.4f);
                Folme.useValue(this.mBackgroundWithoutBlur).to("alpha", 0, animConfig);
            } else {
                Folme.clean(this.mBackgroundWithoutBlur);
                this.mBackgroundWithoutBlur.setAlpha(0);
            }
        }
    }

    @Override // miuix.navigator.navigation.NavigationBarView
    public void show(boolean z) {
        super.show(z);
        if (isEnableBlur()) {
            if (z) {
                Folme.useValue(this.mBackgroundWithoutBlur).setTo("alpha", 255);
            } else {
                Folme.clean(this.mBackgroundWithoutBlur);
                this.mBackgroundWithoutBlur.setAlpha(255);
            }
            updateBackground();
        }
        applyBlur(true);
        this.mNeedApplyBlurBeforeDetach = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateBackground() {
        if (this.mApplyBlur) {
            setBackground(this.mBackgroundIsVisible ? this.mBackgroundInBlur : null);
        } else {
            setBackground(this.mBackgroundIsVisible ? this.mBackgroundWithoutBlur : null);
        }
    }

    private int makeMinHeightSpec(int i) {
        int suggestedMinimumHeight;
        if (getLayoutStyle() == 3) {
            suggestedMinimumHeight = getMinHeightInWideStyle();
        } else {
            suggestedMinimumHeight = getSuggestedMinimumHeight();
        }
        if (View.MeasureSpec.getMode(i) == 1073741824 || suggestedMinimumHeight <= 0) {
            return i;
        }
        return View.MeasureSpec.makeMeasureSpec(Math.min(View.MeasureSpec.getSize(i), suggestedMinimumHeight + getPaddingTop() + getPaddingBottom()), BasicMeasure.EXACTLY);
    }

    public void setItemHorizontalTranslationEnabled(boolean z) {
        BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) getMenuView();
        if (bottomNavigationMenuView.isItemHorizontalTranslationEnabled() != z) {
            bottomNavigationMenuView.setItemHorizontalTranslationEnabled(z);
            getPresenter().updateMenuView(false);
        }
    }

    public boolean isItemHorizontalTranslationEnabled() {
        return ((BottomNavigationMenuView) getMenuView()).isItemHorizontalTranslationEnabled();
    }

    @Override // miuix.navigator.navigation.NavigationBarView
    protected NavigationBarMenuView createNavigationBarMenuView(Context context) {
        return new BottomNavigationMenuView(context);
    }

    @Deprecated
    public void setOnNavigationItemSelectedListener(OnNavigationItemSelectedListener onNavigationItemSelectedListener) {
        setOnItemSelectedListener(onNavigationItemSelectedListener);
    }

    @Deprecated
    public void setOnNavigationItemReselectedListener(OnNavigationItemReselectedListener onNavigationItemReselectedListener) {
        setOnItemReselectedListener(onNavigationItemReselectedListener);
    }

    @Override // miuix.view.WindowInsetsState
    public void setInsetsIgnoringVisibility(boolean z) {
        this.mInsetsIgnoringVisibility = z;
    }

    @Override // miuix.view.WindowInsetsController
    public void applyWindowInsets(boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6) {
        if (this.mInsetsConfig == null) {
            this.mInsetsConfig = new WindowInsetsController.InsetsConfig();
        }
        if (this.mInsetsConfig.update(z, z2, z3, z4, z5, z6)) {
            ViewCompat.requestApplyInsets(this);
        }
    }

    @Override // miuix.view.BlurableWidget
    public void setSupportBlur(boolean z) {
        MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurUiHelper;
        if (miuiBlurUiHelper == null) {
            return;
        }
        miuiBlurUiHelper.setSupportBlur(z);
        if (z) {
            this.mBackgroundInBlur = new ColorDrawable(0);
        }
    }

    @Override // miuix.view.BlurableWidget
    public boolean isSupportBlur() {
        return this.mBlurUiHelper.isSupportBlur();
    }

    @Override // miuix.view.BlurableWidget
    public void setEnableBlur(boolean z) {
        MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurUiHelper;
        if (miuiBlurUiHelper == null) {
            return;
        }
        miuiBlurUiHelper.setEnableBlur(z);
        this.mNeedApplyBlurBeforeDetach = true;
        applyBlur(true);
    }

    @Override // miuix.view.BlurableWidget
    public boolean isEnableBlur() {
        MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurUiHelper;
        if (miuiBlurUiHelper == null) {
            return false;
        }
        return miuiBlurUiHelper.isEnableBlur();
    }

    @Override // miuix.view.BlurableWidget
    public void applyBlur(boolean z) {
        MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurUiHelper;
        if (miuiBlurUiHelper == null) {
            return;
        }
        miuiBlurUiHelper.applyBlur(z);
    }

    @Override // miuix.view.BlurableWidget
    public boolean isApplyBlur() {
        MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurUiHelper;
        if (miuiBlurUiHelper == null) {
            return false;
        }
        return miuiBlurUiHelper.isApplyBlur();
    }

    public void setBackgroundVisible(boolean z) {
        this.mBackgroundIsVisible = z;
        if (z) {
            setBackground(this.mApplyBlur ? this.mBackgroundInBlur : this.mBackgroundWithoutBlur);
        } else {
            setBackground(this.mApplyBlur ? this.mBackgroundInBlur : null);
        }
    }
}
