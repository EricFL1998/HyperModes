package miuix.miuixbasewidget.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import miuix.animation.Folme;
import miuix.animation.IHoverStyle;
import miuix.animation.ITouchStyle;
import miuix.animation.base.AnimConfig;
import miuix.core.util.HyperBloomStrokeUtils;
import miuix.core.util.HyperMaterialUtils;
import miuix.core.util.MaterialConfig;
import miuix.core.util.MaterialDayNightConfig;
import miuix.core.util.MiShadowUtils;
import miuix.core.util.RomUtils;
import miuix.internal.util.AttributeResolver;
import miuix.miuixbasewidget.R;
import miuix.theme.token.MaterialDayNightToken;
import miuix.theme.token.MaterialToken;
import miuix.view.BlurableWidget;
import miuix.view.DynamicThemeWidget;
import miuix.view.HapticCompat;
import miuix.view.HapticFeedbackConstants;
import miuix.view.MiuiBlurUiHelper;

/* JADX INFO: loaded from: classes2.dex */
public class OvalImageButton extends AppCompatImageView implements BlurableWidget, DynamicThemeWidget {
    private OvalDrawable mBackground;
    private final MiuiBlurUiHelper mBlurUiHelper;
    private MaterialConfig mCurrentMaterial;
    private Drawable mDefaultBackground;
    private final EmptyHolder mEmptyHolder;
    private final ITouchStyle mFolmeTouch;
    private boolean mHasOibColor;
    private int mImageAlpha;
    private boolean mIsLightStyle;
    private final boolean mIsShadowEnabled;
    private MaterialDayNightConfig mMaterial;
    private ColorStateList mOibColor;
    private BaseWidgetDropShadowHelper mShadowHelper;
    private int mStrokeColor;
    private float[] mStrokeGradientColorPositions;
    private int[] mStrokeGradientColors;
    private int mStrokeWidth;
    private boolean mUseCompatShadow;
    protected int mUserThemeType;

    public OvalImageButton(Context context) {
        this(context, null);
    }

    public OvalImageButton(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public OvalImageButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mImageAlpha = 255;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.OvalImageButton, i, R.style.Widget_OvalImageButton);
        this.mIsLightStyle = typedArrayObtainStyledAttributes.getBoolean(R.styleable.OvalImageButton_isLightTheme, true);
        this.mIsShadowEnabled = typedArrayObtainStyledAttributes.getBoolean(R.styleable.OvalImageButton_oibShadowEnabled, true);
        ColorStateList colorStateList = typedArrayObtainStyledAttributes.getColorStateList(R.styleable.OvalImageButton_oibColor);
        this.mOibColor = colorStateList;
        this.mHasOibColor = colorStateList != null;
        boolean zHasValue = typedArrayObtainStyledAttributes.hasValue(R.styleable.OvalImageButton_oibTouchColor);
        int color = typedArrayObtainStyledAttributes.getColor(R.styleable.OvalImageButton_oibTouchColor, 0);
        boolean z = typedArrayObtainStyledAttributes.getBoolean(R.styleable.OvalImageButton_oibTouchScalable, true);
        typedArrayObtainStyledAttributes.recycle();
        this.mEmptyHolder = new EmptyHolder(getContext().getResources().getDrawable(R.drawable.miuix_appcompat_fab_empty_holder));
        initBackground();
        if (HyperMaterialUtils.isEnable()) {
            MiuiBlurUiHelper miuiBlurUiHelper = new MiuiBlurUiHelper(context, this, false, false, false, new MiuiBlurUiHelper.BlurStateCallback() { // from class: miuix.miuixbasewidget.widget.OvalImageButton.1
                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public Drawable getBackground() {
                    return null;
                }

                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public void onBlurEnableStateChanged(boolean z2) {
                }

                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public boolean isLightTheme() {
                    if (OvalImageButton.this.hasThemeType()) {
                        return OvalImageButton.this.mUserThemeType == 1;
                    }
                    return OvalImageButton.this.mIsLightStyle;
                }

                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public MaterialConfig.BlurConfig getBlurConfig(boolean z2) {
                    if (OvalImageButton.this.mMaterial == null) {
                        return null;
                    }
                    MaterialConfig.BlurConfig blurConfig = OvalImageButton.this.mMaterial.getBlurConfig(z2);
                    MaterialConfig.ColorBlendConfig colorBlendConfig = OvalImageButton.this.mMaterial.getColorBlendConfig(z2);
                    return (blurConfig != null || colorBlendConfig == null) ? blurConfig : new MaterialConfig.BlurConfig(colorBlendConfig);
                }

                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public void onBlurApplyStateChanged(boolean z2) {
                    Drawable background = OvalImageButton.this.getBackground();
                    if (background != null) {
                        background.setAlpha(z2 ? 0 : 255);
                    }
                }
            });
            this.mBlurUiHelper = miuiBlurUiHelper;
            miuiBlurUiHelper.setSupportBlur(true);
            setEnableBlur(true);
        } else {
            this.mBlurUiHelper = null;
        }
        ITouchStyle iTouchStyle = Folme.use((View) this).touch();
        this.mFolmeTouch = iTouchStyle;
        if (zHasValue) {
            iTouchStyle.setTint(color);
        }
        iTouchStyle.setNoScale(!z);
        iTouchStyle.setTintMode(3).handleTouchOf((View) this, true, new AnimConfig[0]);
        Folme.use((View) this).hover().setEffect(IHoverStyle.HoverEffect.FLOATED_WRAPPED).handleHoverOf(this, new AnimConfig[0]);
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.mFolmeTouch.setTouchRadius(getMeasuredWidth() / 2.0f);
    }

    @Override // android.widget.ImageView, android.view.View
    public CharSequence getAccessibilityClassName() {
        return ImageButton.class.getName();
    }

    public void show() {
        Folme.use((View) this).visible().show(new AnimConfig[0]);
    }

    public void hide() {
        Folme.use((View) this).visible().hide(new AnimConfig[0]);
    }

    public void setTouchScalable(boolean z) {
        this.mFolmeTouch.setNoScale(!z);
    }

    public void setTouchColor(int i) {
        this.mFolmeTouch.setTint(i);
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
        if (isSupportBlur()) {
            if (materialDayNightConfig == null) {
                this.mMaterial = null;
                applyBlur(false);
                HyperBloomStrokeUtils.clearBloomStroke(this);
            } else {
                this.mMaterial = materialDayNightConfig;
                updateMaterialEffect();
            }
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
        boolean z = this.mIsLightStyle;
        if (hasThemeType()) {
            z = this.mUserThemeType == 1;
        }
        MaterialConfig materialConfig = this.mMaterial.get(z);
        this.mCurrentMaterial = materialConfig;
        if (materialConfig != null && HyperMaterialUtils.isFeatureEnable(getContext())) {
            setEnableBlur(true);
            if (this.mBlurUiHelper != null && this.mCurrentMaterial.getBlurConfig() != null) {
                if (!isApplyBlur()) {
                    this.mBlurUiHelper.onConfigChanged();
                    applyBlur(true);
                } else {
                    this.mBlurUiHelper.onConfigChanged();
                    this.mBlurUiHelper.refreshBlur();
                }
            }
            MaterialConfig.BloomStrokeConfig bloomStrokeConfig = this.mCurrentMaterial.getBloomStrokeConfig();
            if (bloomStrokeConfig != null) {
                HyperBloomStrokeUtils.setBloomStrokeConfig(this, bloomStrokeConfig);
            } else {
                HyperBloomStrokeUtils.clearBloomStroke(this);
            }
            updateShadow(z);
            return;
        }
        applyBlur(false);
        setEnableBlur(false);
        HyperBloomStrokeUtils.clearBloomStroke(this);
    }

    @Override // miuix.view.BlurableWidget
    public void setSupportBlur(boolean z) {
        MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurUiHelper;
        if (miuiBlurUiHelper != null) {
            miuiBlurUiHelper.setSupportBlur(z);
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
        if (miuiBlurUiHelper != null) {
            miuiBlurUiHelper.setEnableBlur(z);
        }
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
        if (miuiBlurUiHelper != null) {
            miuiBlurUiHelper.applyBlur(z);
        }
    }

    @Override // miuix.view.BlurableWidget
    public boolean isApplyBlur() {
        MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurUiHelper;
        if (miuiBlurUiHelper == null) {
            return false;
        }
        return miuiBlurUiHelper.isApplyBlur();
    }

    private void updateShadow(boolean z) {
        MaterialConfig materialConfig = this.mCurrentMaterial;
        if (materialConfig == null) {
            return;
        }
        if (materialConfig.getShadowConfig() != null) {
            BaseWidgetDropShadowHelper baseWidgetDropShadowHelper = this.mShadowHelper;
            if (baseWidgetDropShadowHelper == null) {
                this.mShadowHelper = new BaseWidgetDropShadowHelper(getContext(), new DropShadowConfig.Builder(this.mCurrentMaterial.getShadowConfig()).create(), z);
                return;
            } else {
                baseWidgetDropShadowHelper.updateDropShadowConfig(this.mCurrentMaterial.getShadowConfig());
                this.mShadowHelper.updateViewShadow(this, 2);
                return;
            }
        }
        BaseWidgetDropShadowHelper baseWidgetDropShadowHelper2 = this.mShadowHelper;
        if (baseWidgetDropShadowHelper2 != null) {
            baseWidgetDropShadowHelper2.enableViewShadow(this, false, 2);
        }
    }

    private void initBackground() {
        if (getBackground() == null) {
            if (this.mHasOibColor) {
                super.setBackground(createOibBackground());
            } else {
                super.setBackground(getDefaultBackground());
            }
            Drawable background = getBackground();
            if (background != null) {
                background.setAlpha(this.mImageAlpha);
                return;
            }
            return;
        }
        this.mHasOibColor = false;
    }

    private void initEmptyHolder() {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int width = (((getWidth() - paddingLeft) - getPaddingRight()) / 2) * 2;
        this.mEmptyHolder.setBounds(paddingLeft, paddingTop, paddingLeft + width, width + paddingTop);
    }

    @Override // android.widget.ImageView
    public void setAlpha(int i) {
        int iMax = Math.max(0, Math.min(i, 255));
        boolean z = this.mImageAlpha != iMax;
        this.mImageAlpha = iMax;
        Drawable background = getBackground();
        if (background != null) {
            background.setAlpha(iMax);
        }
        Drawable drawable = getDrawable();
        if (drawable != null && z) {
            drawable.mutate().setAlpha(iMax);
        }
        BaseWidgetDropShadowHelper baseWidgetDropShadowHelper = this.mShadowHelper;
        if (baseWidgetDropShadowHelper != null && baseWidgetDropShadowHelper.mEnableMiShadow) {
            this.mShadowHelper.invalidateShadow(this, this.mImageAlpha / 255.0f);
        }
        invalidate();
    }

    @Override // android.view.View
    public void setAlpha(float f) {
        float f2 = 255.0f * f;
        boolean z = ((float) this.mImageAlpha) != f2;
        int i = (int) f2;
        this.mImageAlpha = i;
        Drawable background = getBackground();
        if (background != null) {
            background.setAlpha(i);
        }
        Drawable drawable = getDrawable();
        if (drawable != null && z) {
            drawable.mutate().setAlpha(i);
        }
        BaseWidgetDropShadowHelper baseWidgetDropShadowHelper = this.mShadowHelper;
        if (baseWidgetDropShadowHelper != null && baseWidgetDropShadowHelper.mEnableMiShadow) {
            this.mShadowHelper.invalidateShadow(this, f);
        }
        invalidate();
    }

    @Override // android.view.View
    public float getAlpha() {
        return (float) (((double) this.mImageAlpha) / 255.0d);
    }

    @Override // android.widget.ImageView
    public void setImageAlpha(int i) {
        boolean z = this.mImageAlpha != i;
        this.mImageAlpha = i;
        Drawable background = getBackground();
        if (background != null) {
            background.setAlpha(i);
        }
        Drawable drawable = getDrawable();
        if (drawable != null && z) {
            drawable.mutate().setAlpha(i);
        }
        BaseWidgetDropShadowHelper baseWidgetDropShadowHelper = this.mShadowHelper;
        if (baseWidgetDropShadowHelper != null && baseWidgetDropShadowHelper.mEnableMiShadow) {
            this.mShadowHelper.invalidateShadow(this, this.mImageAlpha / 255.0f);
        }
        invalidate();
    }

    @Override // android.widget.ImageView
    public int getImageAlpha() {
        return this.mImageAlpha;
    }

    @Override // android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        BaseWidgetDropShadowHelper baseWidgetDropShadowHelper = this.mShadowHelper;
        if (baseWidgetDropShadowHelper != null) {
            baseWidgetDropShadowHelper.enableViewShadow(this, this.mIsShadowEnabled, 2);
        }
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDraw(Canvas canvas) {
        initEmptyHolder();
        super.onDraw(canvas);
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        updateMaterialEffect();
    }

    private Drawable getDefaultBackground() {
        if (this.mDefaultBackground == null) {
            this.mOibColor = ContextCompat.getColorStateList(getContext(), R.color.miuix_color_black_level6);
            this.mHasOibColor = true;
            this.mDefaultBackground = createOibBackground();
        }
        return this.mDefaultBackground;
    }

    private Drawable createOibBackground() {
        OvalDrawable ovalDrawable = new OvalDrawable(this.mOibColor);
        if (this.mIsShadowEnabled) {
            boolean zResolveBoolean = AttributeResolver.resolveBoolean(getContext(), R.attr.isLightTheme, true);
            BaseWidgetDropShadowHelper baseWidgetDropShadowHelper = this.mShadowHelper;
            if (baseWidgetDropShadowHelper == null) {
                updateShadow(zResolveBoolean);
            } else {
                baseWidgetDropShadowHelper.onConfigChanged(this, getResources().getConfiguration(), zResolveBoolean);
            }
        } else {
            BaseWidgetDropShadowHelper baseWidgetDropShadowHelper2 = this.mShadowHelper;
            if (baseWidgetDropShadowHelper2 != null) {
                baseWidgetDropShadowHelper2.enableViewShadow(this, false, 2);
            }
            this.mShadowHelper = null;
        }
        this.mBackground = ovalDrawable;
        return ovalDrawable;
    }

    private OvalDrawable getOvalBackground() {
        return this.mBackground;
    }

    public void showStroke() {
        OvalDrawable ovalBackground = getOvalBackground();
        if (ovalBackground != null) {
            ovalBackground.enableDrawStroke(true);
        }
        invalidate();
    }

    public void hideStroke() {
        OvalDrawable ovalBackground = getOvalBackground();
        if (ovalBackground != null) {
            ovalBackground.enableDrawStroke(false);
        }
        invalidate();
    }

    public void setStrokeWidth(int i) {
        if (this.mStrokeWidth != i) {
            this.mStrokeWidth = i;
            OvalDrawable ovalBackground = getOvalBackground();
            if (ovalBackground != null) {
                ovalBackground.setStrokeWidth(i);
            }
        }
    }

    public int getStrokeWidth() {
        return this.mStrokeWidth;
    }

    public void setStrokeColor(int i) {
        if (this.mStrokeColor != i) {
            this.mStrokeColor = i;
            OvalDrawable ovalBackground = getOvalBackground();
            if (ovalBackground != null) {
                ovalBackground.setStrokeColor(this.mStrokeColor);
            }
        }
    }

    public int getStrokeColor() {
        return this.mStrokeColor;
    }

    public void setStrokeGradientColors(int i, int i2) {
        this.mStrokeGradientColors = new int[]{i, i2};
        this.mStrokeGradientColorPositions = new float[]{0.0f, 1.0f};
        OvalDrawable ovalBackground = getOvalBackground();
        if (ovalBackground != null) {
            ovalBackground.setStrokeGradientColors(this.mStrokeGradientColors);
            ovalBackground.setStrokeColorGradientPositions(this.mStrokeGradientColorPositions);
        }
    }

    public void setStrokeGradientColors(int[] iArr, float[] fArr) {
        this.mStrokeGradientColors = iArr;
        this.mStrokeGradientColorPositions = fArr;
        OvalDrawable ovalBackground = getOvalBackground();
        if (ovalBackground != null) {
            ovalBackground.setStrokeGradientColors(this.mStrokeGradientColors);
            ovalBackground.setStrokeColorGradientPositions(this.mStrokeGradientColorPositions);
        }
    }

    public void enableUseCompatShadow(boolean z) {
        BaseWidgetDropShadowHelper baseWidgetDropShadowHelper;
        if (this.mUseCompatShadow == z || (baseWidgetDropShadowHelper = this.mShadowHelper) == null) {
            return;
        }
        if (z) {
            baseWidgetDropShadowHelper.setEnableMiShadow(false);
        } else {
            baseWidgetDropShadowHelper.setEnableMiShadow(RomUtils.getHyperOsVersion() >= 2 && MiShadowUtils.SUPPORT_MI_SHADOW);
        }
        this.mShadowHelper.enableViewShadow(this, false, 2);
        this.mUseCompatShadow = z;
        float f = this.mShadowHelper.mBlurRadiusPx;
        if (f > 0.0f) {
            setElevation(f);
        }
        if (Build.VERSION.SDK_INT >= 28) {
            setOutlineSpotShadowColor(this.mShadowHelper.mShadowColor);
        }
        requestLayout();
    }

    @Override // android.view.View
    public void setBackground(Drawable drawable) {
        this.mHasOibColor = false;
        if (drawable == null) {
            drawable = getDefaultBackground();
        }
        super.setBackground(drawable);
    }

    @Override // androidx.appcompat.widget.AppCompatImageView, android.view.View
    public void setBackgroundResource(int i) {
        this.mHasOibColor = false;
        if (i == 0) {
            super.setBackground(getDefaultBackground());
        } else {
            super.setBackgroundResource(i);
        }
    }

    @Override // android.view.View
    public void setBackgroundColor(int i) {
        this.mOibColor = ColorStateList.valueOf(i);
        super.setBackground(createOibBackground());
        this.mHasOibColor = true;
    }

    @Override // android.view.View
    public boolean performClick() {
        HapticCompat.performHapticFeedback(this, HapticFeedbackConstants.MIUI_BUTTON_MIDDLE, HapticFeedbackConstants.MIUI_TAP_LIGHT);
        return super.performClick();
    }

    class EmptyHolder extends Drawable {
        private Drawable mDrawable;
        private Paint mPaint = new Paint();

        EmptyHolder(Drawable drawable) {
            this.mDrawable = drawable;
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            int width = OvalImageButton.this.getWidth();
            int paddingLeft = OvalImageButton.this.getPaddingLeft();
            int paddingTop = OvalImageButton.this.getPaddingTop();
            int paddingRight = (((width - paddingLeft) - OvalImageButton.this.getPaddingRight()) / 2) * 2;
            this.mDrawable.setBounds(paddingLeft, paddingTop, paddingLeft + paddingRight, paddingRight + paddingTop);
            this.mDrawable.draw(canvas);
        }

        @Override // android.graphics.drawable.Drawable
        public Drawable.ConstantState getConstantState() {
            return this.mDrawable.getConstantState();
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int i) {
            this.mDrawable.setAlpha(i);
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
            this.mDrawable.setColorFilter(colorFilter);
        }

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return this.mDrawable.getOpacity();
        }
    }
}
