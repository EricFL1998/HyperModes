package miuix.bottomsheet;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import com.miui.miwallpaper.MiuiWallpaperManager;
import miuix.core.util.HyperBloomStrokeUtils;
import miuix.core.util.HyperMaterialUtils;
import miuix.core.util.MaterialConfig;
import miuix.core.util.MaterialDayNightConfig;
import miuix.core.util.MiuixUIUtils;
import miuix.internal.util.AttributeResolver;
import miuix.theme.token.BloomStrokeToken;
import miuix.theme.token.ColorBlendToken;
import miuix.theme.token.MaterialDayNightToken;
import miuix.theme.token.MaterialToken;
import miuix.view.BlurableWidget;
import miuix.view.DynamicThemeWidget;
import miuix.view.Fence;
import miuix.view.MiuiBlurUiHelper;

/* JADX INFO: loaded from: classes2.dex */
public class BottomSheetView extends FrameLayout implements BlurableWidget, DynamicThemeWidget, Fence {
    public static final MaterialToken Default_BottomSheet_Dark;
    public static final MaterialToken Default_BottomSheet_Light;
    public static final MaterialDayNightToken Default_BottomSheet_Material;
    private AttributeSet mAttrs;
    private Drawable mBackground;
    private Drawable mBackgroundInBlur;
    private boolean mBlurEnable;
    private MiuiBlurUiHelper mBlurUiHelper;
    private float[] mBottomModeRadii;
    private int mBottomModeRadius;
    private BottomModeOutlineProvider mBottomOutlineProvider;
    private boolean mClipByOutline;
    private Path mClipPath;
    private FrameLayout mContainerView;
    private MaterialConfig mCurrentMaterial;
    private int mCurrentMode;
    private float mDensityDpi;
    private View mDragHandleContainerView;
    private boolean mDragHandleViewEnabled;
    private View mExtraHeightView;
    private boolean mFenceEnabled;
    private FloatingModeOutlineProvider mFloatingModeOutlineProvider;
    private float[] mFloatingModeRadii;
    private int mFloatingModeRadius;
    private MaterialDayNightConfig mMaterial;
    private int mUserThemeType;

    static {
        MaterialToken materialTokenBuild = new MaterialToken.Builder(10, "bottomsheet-default", "light").setColorBlend(ColorBlendToken.Pured_Thick_Light).setElementBlur(100).setBloomStroke(BloomStrokeToken.Glass_Stroke_Big_Light).build();
        Default_BottomSheet_Light = materialTokenBuild;
        MaterialToken materialTokenBuild2 = new MaterialToken.Builder(10, "bottomsheet-default", MiuiWallpaperManager.MI_WALLPAPER_TYPE_DARK).setColorBlend(ColorBlendToken.Pured_Thick_Dark).setElementBlur(100).setBloomStroke(BloomStrokeToken.Glass_Stroke_Big_Dark).build();
        Default_BottomSheet_Dark = materialTokenBuild2;
        Default_BottomSheet_Material = new MaterialDayNightToken(materialTokenBuild, materialTokenBuild2);
    }

    public BottomSheetView(Context context) {
        super(context);
        this.mCurrentMode = -1;
        this.mDragHandleViewEnabled = true;
        this.mBlurEnable = false;
        this.mFenceEnabled = true;
        init(context, null);
    }

    public BottomSheetView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mCurrentMode = -1;
        this.mDragHandleViewEnabled = true;
        this.mBlurEnable = false;
        this.mFenceEnabled = true;
        init(context, attributeSet);
    }

    public BottomSheetView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mCurrentMode = -1;
        this.mDragHandleViewEnabled = true;
        this.mBlurEnable = false;
        this.mFenceEnabled = true;
        init(context, attributeSet);
    }

    public BottomSheetView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mCurrentMode = -1;
        this.mDragHandleViewEnabled = true;
        this.mBlurEnable = false;
        this.mFenceEnabled = true;
        init(context, attributeSet);
    }

    private void init(Context context, AttributeSet attributeSet) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.BottomSheetView, R.attr.bottomSheetStyle, 0);
        boolean z = typedArrayObtainStyledAttributes.getBoolean(R.styleable.BottomSheetView_contentHeightMatchParent, false);
        typedArrayObtainStyledAttributes.recycle();
        View viewInflate = LayoutInflater.from(context).inflate(R.layout.miuix_bottom_sheet_view, (ViewGroup) this, false);
        if (z) {
            addView(viewInflate, new FrameLayout.LayoutParams(-1, -1));
        } else {
            addView(viewInflate, new FrameLayout.LayoutParams(-1, -2));
        }
        setClipToOutline(true);
        this.mAttrs = attributeSet;
        this.mClipByOutline = Build.VERSION.SDK_INT >= 33;
        this.mDensityDpi = context.getResources().getDisplayMetrics().densityDpi;
        refreshSizes(attributeSet);
        if (HyperMaterialUtils.isEnable()) {
            this.mBackground = getBackground();
            boolean zResolveBoolean = AttributeResolver.resolveBoolean(getContext(), R.attr.isLightTheme, true);
            MaterialDayNightConfig materialDayNightConfigCreate = MaterialDayNightConfig.create(Default_BottomSheet_Material);
            this.mMaterial = materialDayNightConfigCreate;
            if (materialDayNightConfigCreate != null) {
                this.mCurrentMaterial = materialDayNightConfigCreate.get(zResolveBoolean);
            }
            this.mBlurUiHelper = new MiuiBlurUiHelper(getContext(), this, false, false, false, new MiuiBlurUiHelper.BlurStateCallback() { // from class: miuix.bottomsheet.BottomSheetView.1
                final boolean isDarkThemeOverlay;

                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public void onBlurEnableStateChanged(boolean z2) {
                }

                {
                    this.isDarkThemeOverlay = MiuixUIUtils.isDarkThemeOverlay(BottomSheetView.this.getContext(), R.color.miuix_default_color_on_surface_light);
                }

                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public boolean isLightTheme() {
                    if (BottomSheetView.this.hasThemeType()) {
                        return BottomSheetView.this.mUserThemeType == 1;
                    }
                    return !this.isDarkThemeOverlay && AttributeResolver.resolveBoolean(BottomSheetView.this.getContext(), R.attr.isLightTheme, true);
                }

                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public int getBackgroundColor() {
                    if (this.isDarkThemeOverlay) {
                        return AttributeResolver.resolveColor(BottomSheetView.this.getContext(), miuix.theme.R.attr.colorSurface, 0);
                    }
                    return 0;
                }

                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public MaterialConfig.BlurConfig getBlurConfig(boolean z2) {
                    MaterialDayNightConfig materialDayNightConfig = BottomSheetView.this.mMaterial;
                    if (materialDayNightConfig != null) {
                        return materialDayNightConfig.getBlurConfig(z2);
                    }
                    return null;
                }

                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public void onBlurApplyStateChanged(boolean z2) {
                    if (z2) {
                        BottomSheetView bottomSheetView = BottomSheetView.this;
                        bottomSheetView.setBackground(bottomSheetView.mBackgroundInBlur);
                    } else {
                        BottomSheetView bottomSheetView2 = BottomSheetView.this;
                        bottomSheetView2.setBackground(bottomSheetView2.mBackground);
                    }
                    BottomSheetView.this.invalidate();
                }
            });
            setSupportBlur(true);
            setEnableBlur(HyperMaterialUtils.isFeatureEnable(getContext()));
            this.mBlurUiHelper.applyBlur(this.mBlurEnable);
            MaterialConfig materialConfig = this.mCurrentMaterial;
            if (materialConfig == null || !this.mBlurEnable) {
                return;
            }
            MaterialConfig.BloomStrokeConfig bloomStrokeConfig = materialConfig.getBloomStrokeConfig();
            if (bloomStrokeConfig != null) {
                HyperBloomStrokeUtils.setBloomStrokeConfig(this, bloomStrokeConfig);
                return;
            } else {
                HyperBloomStrokeUtils.clearBloomStroke(this);
                return;
            }
        }
        this.mBlurUiHelper = null;
    }

    private void refreshSizes(AttributeSet attributeSet) {
        Resources resources = getResources();
        this.mBottomModeRadius = resources.getDimensionPixelSize(R.dimen.miuix_bottom_sheet_radius);
        this.mFloatingModeRadius = resources.getDimensionPixelSize(R.dimen.miuix_bottom_sheet_floating_radius);
        if (attributeSet != null) {
            TypedArray typedArrayObtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R.styleable.BottomSheetView, R.attr.bottomSheetStyle, 0);
            this.mBottomModeRadius = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.BottomSheetView_bottomModeRadius, this.mBottomModeRadius);
            this.mFloatingModeRadius = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.BottomSheetView_floatingModeRadius, this.mFloatingModeRadius);
            this.mBlurEnable = typedArrayObtainStyledAttributes.getBoolean(R.styleable.BottomSheetView_blurEnabled, false);
            typedArrayObtainStyledAttributes.recycle();
        }
        int i = this.mBottomModeRadius;
        this.mBottomModeRadii = new float[]{i, i, i, i, 0.0f, 0.0f, 0.0f, 0.0f};
        int i2 = this.mFloatingModeRadius;
        this.mFloatingModeRadii = new float[]{i2, i2, i2, i2, i2, i2, i2, i2};
    }

    @Override // miuix.view.DynamicThemeWidget
    public void setThemeType(int i) {
        if (this.mUserThemeType != i) {
            this.mUserThemeType = i;
            updateMaterialEffect();
        }
    }

    @Override // miuix.view.DynamicThemeWidget
    public int getThemeType() {
        return this.mUserThemeType;
    }

    @Override // miuix.view.DynamicThemeWidget
    public boolean hasThemeType() {
        return this.mUserThemeType > 0;
    }

    public void setMaterial(MaterialToken materialToken) {
        setMaterial(MaterialDayNightConfig.create(new MaterialDayNightToken(materialToken)));
    }

    public void setMaterial(MaterialDayNightToken materialDayNightToken) {
        setMaterial(MaterialDayNightConfig.create(materialDayNightToken));
    }

    @Override // miuix.view.HyperMaterialWidget
    public void setMaterial(MaterialConfig materialConfig) {
        setMaterial(new MaterialDayNightConfig(materialConfig));
    }

    @Override // miuix.view.HyperMaterialWidget
    public void setMaterial(MaterialDayNightConfig materialDayNightConfig) {
        this.mMaterial = materialDayNightConfig;
        if (isSupportBlur()) {
            if (materialDayNightConfig == null) {
                this.mMaterial = null;
                MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurUiHelper;
                if (miuiBlurUiHelper != null) {
                    miuiBlurUiHelper.applyBlur(false);
                }
                HyperBloomStrokeUtils.clearBloomStroke(this);
                return;
            }
            this.mMaterial = materialDayNightConfig;
            updateMaterialEffect();
        }
    }

    @Override // miuix.view.HyperMaterialWidget
    public MaterialDayNightConfig getMaterial() {
        return this.mMaterial;
    }

    @Override // miuix.view.HyperMaterialWidget
    public MaterialConfig getCurrentMaterial() {
        return this.mCurrentMaterial;
    }

    @Override // miuix.view.HyperMaterialWidget
    public void updateMaterialEffect() {
        if (this.mMaterial == null) {
            return;
        }
        boolean zResolveBoolean = AttributeResolver.resolveBoolean(getContext(), R.attr.isLightTheme, true);
        if (hasThemeType()) {
            zResolveBoolean = this.mUserThemeType == 1;
        }
        MaterialConfig materialConfig = this.mMaterial.get(zResolveBoolean);
        this.mCurrentMaterial = materialConfig;
        if (materialConfig != null && HyperMaterialUtils.isFeatureEnable(getContext())) {
            setEnableBlur(true);
            if (this.mBlurUiHelper != null && this.mCurrentMaterial.getBlurConfig() != null) {
                if (!isApplyBlur() && this.mBlurEnable) {
                    this.mBlurUiHelper.onConfigChanged();
                    applyBlur(this.mBlurEnable);
                } else {
                    this.mBlurUiHelper.onConfigChanged();
                    this.mBlurUiHelper.refreshBlur();
                }
            }
            if (this.mBlurEnable) {
                MaterialConfig.BloomStrokeConfig bloomStrokeConfig = this.mCurrentMaterial.getBloomStrokeConfig();
                if (bloomStrokeConfig != null) {
                    HyperBloomStrokeUtils.setBloomStrokeConfig(this, bloomStrokeConfig);
                    return;
                } else {
                    HyperBloomStrokeUtils.clearBloomStroke(this);
                    return;
                }
            }
            return;
        }
        MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurUiHelper;
        if (miuiBlurUiHelper != null) {
            miuiBlurUiHelper.applyBlur(false);
        }
        setEnableBlur(false);
        HyperBloomStrokeUtils.clearBloomStroke(this);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateMaterialEffect();
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mContainerView = (FrameLayout) findViewById(R.id.miuix_bottom_sheet_container_view);
        this.mDragHandleContainerView = findViewById(R.id.drag_handle_container_view);
        this.mExtraHeightView = findViewById(R.id.extra_space_height_view);
        if (this.mDragHandleViewEnabled) {
            return;
        }
        hideDragHandleView();
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (this.mClipByOutline) {
            return;
        }
        if (this.mClipPath == null) {
            this.mClipPath = new Path();
        }
        int i5 = this.mCurrentMode;
        if (i5 == 0) {
            this.mClipPath.reset();
            this.mClipPath.addRoundRect(new RectF(0.0f, 0.0f, i, i2), this.mBottomModeRadii, Path.Direction.CW);
        } else {
            if (i5 == 1) {
                this.mClipPath.reset();
                this.mClipPath.addRoundRect(new RectF(0.0f, 0.0f, i, i2), this.mFloatingModeRadii, Path.Direction.CW);
                return;
            }
            throw new IllegalArgumentException("Unexpected bottom sheet mode: " + this.mCurrentMode);
        }
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        if (this.mClipByOutline) {
            super.draw(canvas);
            return;
        }
        if (this.mClipPath == null) {
            super.draw(canvas);
            return;
        }
        canvas.save();
        canvas.clipPath(this.mClipPath);
        super.draw(canvas);
        canvas.restore();
    }

    public void removeAllContentViews() {
        FrameLayout frameLayout = this.mContainerView;
        if (frameLayout != null) {
            frameLayout.removeAllViews();
        }
    }

    public void addContentChildView(View view) {
        FrameLayout frameLayout = this.mContainerView;
        if (frameLayout != null) {
            frameLayout.addView(view);
        }
    }

    public void addContentChildView(View view, ViewGroup.LayoutParams layoutParams) {
        FrameLayout frameLayout = this.mContainerView;
        if (frameLayout != null) {
            frameLayout.addView(view, layoutParams);
        }
    }

    public void setDragHandleViewEnabled(boolean z) {
        this.mDragHandleViewEnabled = z;
        if (z) {
            return;
        }
        hideDragHandleView();
    }

    public boolean isDragHandleViewEnabled() {
        return this.mDragHandleViewEnabled;
    }

    public void showDragHandleView() {
        View view;
        if (!this.mDragHandleViewEnabled || (view = this.mDragHandleContainerView) == null) {
            return;
        }
        view.setVisibility(0);
    }

    public void hideDragHandleView() {
        View view = this.mDragHandleContainerView;
        if (view != null) {
            view.setVisibility(8);
        }
    }

    void setBottomSheetMode(int i) {
        if (this.mCurrentMode != i) {
            this.mCurrentMode = i;
            if (!this.mClipByOutline) {
                invalidate();
                return;
            }
            if (i == 0) {
                if (this.mBottomOutlineProvider == null) {
                    this.mBottomOutlineProvider = new BottomModeOutlineProvider(this.mBottomModeRadius);
                }
                setOutlineProvider(this.mBottomOutlineProvider);
            } else {
                if (i == 1) {
                    if (this.mFloatingModeOutlineProvider == null) {
                        this.mFloatingModeOutlineProvider = new FloatingModeOutlineProvider(this.mFloatingModeRadius);
                    }
                    setOutlineProvider(this.mFloatingModeOutlineProvider);
                    return;
                }
                throw new IllegalArgumentException("Unexpected bottom sheet mode: " + i);
            }
        }
    }

    void setExtraHeightEnabled(boolean z) {
        View view = this.mExtraHeightView;
        if (view != null) {
            if (z) {
                view.setVisibility(0);
            } else {
                view.setVisibility(8);
            }
        }
    }

    int getExtraHeight() {
        View view = this.mExtraHeightView;
        if (view == null || view.getVisibility() == 8) {
            return 0;
        }
        return this.mExtraHeightView.getMeasuredHeight();
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        float f = configuration.densityDpi;
        if (f != this.mDensityDpi) {
            this.mDensityDpi = f;
            refreshSizes(this.mAttrs);
            if (this.mBottomOutlineProvider != null) {
                this.mBottomOutlineProvider = new BottomModeOutlineProvider(this.mBottomModeRadius);
            }
            if (this.mFloatingModeOutlineProvider != null) {
                this.mFloatingModeOutlineProvider = new FloatingModeOutlineProvider(this.mFloatingModeRadius);
            }
        }
        updateMaterialEffect();
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
        MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurUiHelper;
        if (miuiBlurUiHelper == null) {
            return false;
        }
        return miuiBlurUiHelper.isSupportBlur();
    }

    @Override // miuix.view.BlurableWidget
    public void setEnableBlur(boolean z) {
        MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurUiHelper;
        if (miuiBlurUiHelper == null) {
            return;
        }
        miuiBlurUiHelper.setEnableBlur(z);
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
        this.mBlurEnable = z;
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

    @Override // miuix.view.Fence
    public void setFenceEnabled(boolean z) {
        this.mFenceEnabled = z;
    }

    @Override // miuix.view.Fence
    public boolean isFenceEnabled() {
        return this.mFenceEnabled;
    }

    public static class BottomModeOutlineProvider extends ViewOutlineProvider {
        private final float mRadius;

        public BottomModeOutlineProvider(float f) {
            this.mRadius = f;
        }

        @Override // android.view.ViewOutlineProvider
        public void getOutline(View view, Outline outline) {
            outline.setConvexPath(getConvexPath(view));
        }

        private Path getConvexPath(View view) {
            Path path = new Path();
            int width = view.getWidth();
            int height = view.getHeight();
            float f = this.mRadius;
            float f2 = 0;
            path.addRoundRect(new RectF(f2, f2, width, height), new float[]{f, f, f, f, 0.0f, 0.0f, 0.0f, 0.0f}, Path.Direction.CW);
            return path;
        }
    }

    public static class FloatingModeOutlineProvider extends ViewOutlineProvider {
        private final float mRadius;

        public FloatingModeOutlineProvider(float f) {
            this.mRadius = f;
        }

        @Override // android.view.ViewOutlineProvider
        public void getOutline(View view, Outline outline) {
            outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), this.mRadius);
        }
    }
}
