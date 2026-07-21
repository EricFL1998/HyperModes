package miuix.cardview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewOutlineProvider;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import com.miui.support.cardview.R;
import com.miui.support.drawable.CardStateDrawable;
import java.lang.reflect.InvocationTargetException;
import miuix.core.util.HyperBloomStrokeUtils;
import miuix.core.util.HyperMaterialUtils;
import miuix.core.util.MaterialConfig;
import miuix.core.util.MaterialDayNightConfig;
import miuix.core.util.MiShadowUtils;
import miuix.core.util.MiuiBlurUtils;
import miuix.core.util.RomUtils;
import miuix.device.DeviceUtils;
import miuix.graphics.shadow.DropShadowConfig;
import miuix.graphics.shadow.DropShadowHelper;
import miuix.internal.util.AttributeResolver;
import miuix.reflect.ReflectionHelper;
import miuix.theme.token.MaterialDayNightToken;
import miuix.theme.token.MaterialToken;
import miuix.view.BlurableWidget;
import miuix.view.DynamicThemeWidget;
import miuix.view.MiuiBlurUiHelper;

/* JADX INFO: loaded from: classes2.dex */
public class HyperCardView extends CardView implements BlurableWidget, DynamicThemeWidget {
    private static final String TAG = "MiuiX.HyperCardView";
    private boolean mApplyBlur;
    private Drawable mBackgroundWithoutBlur;
    private int[] mBlendColorModes;
    private int[] mBlendColors;
    private int mBlurRadius;
    private int mBlurRadiusDp;
    private final MiuiBlurUiHelper mBlurUiHelper;
    private MaterialConfig mCurrentMaterial;
    private boolean mDrawStrokeOverlay;
    private final DropShadowHelper mDropShadowHelper;
    private float mIsBlendColorAnimProgress;
    private boolean mIsBlendColorAnimRunning;
    private int[] mLastBlendColorModes;
    private int[] mLastBlendColors;
    private MaterialDayNightConfig mMaterial;
    private ValueAnimator mRunningBgAnimator;
    private int mShadowColor;
    private boolean mShadowConfigDirty;
    private float mShadowDispersion;
    private float mShadowDxDp;
    private float mShadowDyDp;
    private float mShadowRadiusDp;
    private int mStrokeColor;
    private float[] mStrokeGradientColorPositions;
    private int[] mStrokeGradientColors;
    private float mStrokeWidth;
    private int[] mTmpBlendColorModes;
    private boolean mUseCompatShadow;
    private boolean mUseSmooth;
    private float mUserAlpha;
    private int mUserThemeType;

    public interface BlendColorTransitionListener {
        void onCancel();

        void onEnd();

        void onStart();

        void onUpdate(float f);
    }

    public HyperCardView(Context context) {
        this(context, null);
    }

    public HyperCardView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.cardViewStyle);
    }

    public HyperCardView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mApplyBlur = false;
        this.mBackgroundWithoutBlur = null;
        this.mRunningBgAnimator = null;
        this.mLastBlendColors = null;
        this.mLastBlendColorModes = null;
        this.mBlendColors = null;
        this.mBlendColorModes = null;
        this.mTmpBlendColorModes = null;
        this.mIsBlendColorAnimRunning = false;
        this.mIsBlendColorAnimProgress = 0.0f;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.CardView, i, 0);
        Resources resources = context.getResources();
        float f = resources.getDisplayMetrics().density;
        int resourceId = typedArrayObtainStyledAttributes.getResourceId(R.styleable.CardView_outlineStyle, -1);
        if (resourceId != -1) {
            TypedArray typedArrayObtainStyledAttributes2 = context.obtainStyledAttributes(resourceId, R.styleable.OutlineProvider);
            String string = typedArrayObtainStyledAttributes2.getString(R.styleable.OutlineProvider_android_name);
            if (!TextUtils.isEmpty(string)) {
                setOutlineProviderFromAttribute(context, string, resourceId);
            }
            typedArrayObtainStyledAttributes2.recycle();
        }
        this.mUseCompatShadow = typedArrayObtainStyledAttributes.getBoolean(R.styleable.CardView_miuix_useCompatShadow, false);
        this.mShadowDispersion = typedArrayObtainStyledAttributes.getFloat(R.styleable.CardView_miuix_shadowDispersion, 0.0f);
        int dimensionPixelSize = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.CardView_android_shadowRadius, 0);
        this.mShadowRadiusDp = dimensionPixelSize == 0 ? 0.0f : (dimensionPixelSize / f) + 0.5f;
        int dimensionPixelSize2 = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.CardView_android_shadowDx, 0);
        this.mShadowDxDp = dimensionPixelSize2 == 0 ? 0.0f : (dimensionPixelSize2 / f) + 0.5f;
        int dimensionPixelSize3 = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.CardView_android_shadowDy, 0);
        this.mShadowDyDp = dimensionPixelSize3 == 0 ? 0.0f : (dimensionPixelSize3 / f) + 0.5f;
        this.mShadowColor = typedArrayObtainStyledAttributes.getColor(R.styleable.CardView_android_shadowColor, 0);
        this.mUseSmooth = typedArrayObtainStyledAttributes.getBoolean(R.styleable.CardView_miuix_useSmooth, true);
        int dimensionPixelSize4 = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.CardView_miuix_blurRadius, 0);
        this.mBlurRadius = dimensionPixelSize4;
        this.mBlurRadiusDp = dimensionPixelSize4 == 0 ? 0 : (int) ((dimensionPixelSize4 / f) + 0.5f);
        boolean z = typedArrayObtainStyledAttributes.getBoolean(R.styleable.CardView_miuix_blurSelfBackground, false);
        if (checkNeedSmooth()) {
            setSmoothCornerEnable(true);
        }
        this.mDrawStrokeOverlay = typedArrayObtainStyledAttributes.getBoolean(R.styleable.CardView_miuix_drawStrokeOverlay, false);
        this.mStrokeWidth = typedArrayObtainStyledAttributes.getDimension(R.styleable.CardView_miuix_strokeWidth, 0.0f);
        this.mStrokeColor = typedArrayObtainStyledAttributes.getColor(R.styleable.CardView_miuix_strokeColor, 0);
        int resourceId2 = typedArrayObtainStyledAttributes.getResourceId(R.styleable.CardView_miuix_strokeGradientColors, 0);
        if (resourceId2 > 0) {
            this.mStrokeGradientColors = resources.getIntArray(resourceId2);
        }
        int resourceId3 = typedArrayObtainStyledAttributes.getResourceId(R.styleable.CardView_miuix_strokeGradientPositions, 0);
        if (resourceId3 > 0) {
            TypedArray typedArrayObtainTypedArray = resources.obtainTypedArray(resourceId3);
            this.mStrokeGradientColorPositions = new float[typedArrayObtainTypedArray.length()];
            for (int i2 = 0; i2 < typedArrayObtainTypedArray.length(); i2++) {
                this.mStrokeGradientColorPositions[i2] = resources.getFraction(typedArrayObtainTypedArray.getResourceId(i2, 0), 1, 1);
            }
            typedArrayObtainTypedArray.recycle();
        }
        int resourceId4 = typedArrayObtainStyledAttributes.getResourceId(R.styleable.CardView_cardBlendColors, 0);
        if (resourceId4 > 0) {
            this.mBlendColors = resources.getIntArray(resourceId4);
        }
        int resourceId5 = typedArrayObtainStyledAttributes.getResourceId(R.styleable.CardView_cardBlendColorModes, 0);
        if (resourceId5 > 0) {
            this.mBlendColorModes = resources.getIntArray(resourceId5);
        }
        typedArrayObtainStyledAttributes.recycle();
        this.mMaterial = updateMaterial();
        boolean zResolveBoolean = AttributeResolver.resolveBoolean(getContext(), R.attr.isLightTheme, true);
        this.mCurrentMaterial = this.mMaterial.get(zResolveBoolean);
        replaceHyperBackground();
        this.mBackgroundWithoutBlur = getBackground();
        if (HyperMaterialUtils.isEnable()) {
            MiuiBlurUiHelper miuiBlurUiHelper = new MiuiBlurUiHelper(context, this, false, true, z, new MiuiBlurUiHelper.BlurStateCallback() { // from class: miuix.cardview.HyperCardView.1
                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public void onBlurEnableStateChanged(boolean z2) {
                }

                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public boolean isLightTheme() {
                    return AttributeResolver.resolveBoolean(HyperCardView.this.getContext(), R.attr.isLightTheme, true);
                }

                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public void onCreateBlurParams(MiuiBlurUiHelper miuiBlurUiHelper2) {
                    if (HyperCardView.this.mBlendColors == null || HyperCardView.this.mBlendColorModes == null) {
                        return;
                    }
                    if (!HyperCardView.this.mIsBlendColorAnimRunning || HyperCardView.this.mLastBlendColors == null) {
                        miuiBlurUiHelper2.setBlurParams(HyperCardView.this.mBlendColors, HyperCardView.this.mBlendColorModes, HyperCardView.this.mBlurRadiusDp);
                        return;
                    }
                    float f2 = 1.0f - HyperCardView.this.mIsBlendColorAnimProgress;
                    float f3 = HyperCardView.this.mIsBlendColorAnimProgress;
                    int[] iArr = new int[HyperCardView.this.mLastBlendColors.length + HyperCardView.this.mBlendColors.length];
                    int length = HyperCardView.this.mLastBlendColors.length;
                    for (int i3 = 0; i3 < HyperCardView.this.mLastBlendColors.length; i3++) {
                        iArr[i3] = (((int) ((HyperCardView.this.mLastBlendColors[i3] >>> 24) * f2)) << 24) | (16777215 & HyperCardView.this.mLastBlendColors[i3]);
                    }
                    for (int i4 = 0; i4 < HyperCardView.this.mBlendColors.length; i4++) {
                        iArr[length + i4] = (((int) ((HyperCardView.this.mBlendColors[i4] >>> 24) * f3)) << 24) | (HyperCardView.this.mBlendColors[i4] & 16777215);
                    }
                    miuiBlurUiHelper2.setBlurParams(iArr, HyperCardView.this.mTmpBlendColorModes, HyperCardView.this.mBlurRadiusDp);
                }

                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public void onBlurApplyStateChanged(boolean z2) {
                    HyperCardView.this.mApplyBlur = z2;
                    if (HyperCardView.this.mBackgroundWithoutBlur != null) {
                        HyperCardView.this.mBackgroundWithoutBlur.setAlpha(HyperCardView.this.mApplyBlur ? 0 : 255);
                    }
                }
            });
            this.mBlurUiHelper = miuiBlurUiHelper;
            setSupportBlur(true);
            setEnableBlur(true);
            miuiBlurUiHelper.applyBlur(HyperMaterialUtils.isFeatureEnable(context) && this.mBlurRadius > 0);
        } else {
            this.mBlurUiHelper = null;
        }
        this.mShadowConfigDirty = false;
        DropShadowHelper dropShadowHelper = new DropShadowHelper(context, updateShadowConfig(), zResolveBoolean);
        this.mDropShadowHelper = dropShadowHelper;
        if (this.mUseCompatShadow) {
            dropShadowHelper.setEnableMiShadow(false);
        }
        if (!dropShadowHelper.isEnableMiShadow()) {
            setCardElevation(dimensionPixelSize);
            if (Build.VERSION.SDK_INT >= 28) {
                setOutlineAmbientShadowColor(ViewCompat.MEASURED_STATE_MASK);
            }
            setOutlineSpotShadowColor(this.mShadowColor);
        }
        if (getAlpha() == 1.0f) {
            setAlpha(1.0f);
        }
    }

    private boolean checkNeedSmooth() {
        return !isCommonLiteStrategy() && this.mUseSmooth;
    }

    private boolean isCommonLiteStrategy() {
        return DeviceUtils.isMiuiLiteV2() || DeviceUtils.isLiteV1StockPlus();
    }

    private void setSmoothCornerEnable(boolean z) {
        try {
            ReflectionHelper.invoke(View.class, this, "setSmoothCornerEnabled", new Class[]{Boolean.TYPE}, Boolean.valueOf(z));
        } catch (Exception e) {
            Log.e(TAG, "setSmoothCornerEnabled failed:" + e.getMessage());
        }
    }

    private RoundRectDrawable getHyperBackground() {
        Drawable drawable = this.mBackgroundWithoutBlur;
        if (drawable instanceof RoundRectDrawable) {
            return (RoundRectDrawable) drawable;
        }
        return null;
    }

    private void replaceHyperBackground() {
        RoundRectDrawable roundRectDrawable = new RoundRectDrawable(getCardBackgroundColor(), getRadius(), getStrokeWidth(), getStrokeColor());
        roundRectDrawable.setStrokeGradientColors(this.mStrokeGradientColors);
        roundRectDrawable.setStrokeColorGradientPositions(this.mStrokeGradientColorPositions);
        setBackground(roundRectDrawable);
        Drawable foreground = getForeground();
        if (foreground instanceof CardStateDrawable) {
            ((CardStateDrawable) foreground).setRadius((int) getRadius());
        }
    }

    private MaterialDayNightConfig updateMaterial() {
        int[] iArr;
        MaterialToken.Builder shadow = new MaterialToken.Builder(10).setBlur(0, 1, 0, this.mBlurRadiusDp).setShadow(this.mShadowColor, this.mShadowDxDp, this.mShadowDyDp, this.mShadowRadiusDp, this.mShadowDispersion);
        int[] iArr2 = this.mBlendColors;
        if (iArr2 != null && (iArr = this.mBlendColorModes) != null) {
            shadow.setColorBlend(MiuiBlurUtils.wrapBlendConfig(iArr2, iArr));
        }
        return MaterialDayNightConfig.create(new MaterialDayNightToken(shadow.build()));
    }

    @Override // androidx.cardview.widget.CardView
    public void setRadius(float f) {
        RoundRectDrawable hyperBackground = getHyperBackground();
        if (hyperBackground != null) {
            hyperBackground.setRadius(f);
        } else {
            super.setRadius(f);
        }
        Drawable foreground = getForeground();
        if (foreground instanceof CardStateDrawable) {
            ((CardStateDrawable) foreground).setRadius((int) f);
        }
    }

    @Override // android.view.View
    public void setAlpha(float f) {
        this.mUserAlpha = f;
        if (isNeedRestrictAlpha() && this.mUserAlpha > 0.99f) {
            super.setAlpha(0.99f);
        } else {
            super.setAlpha(f);
        }
    }

    private boolean isNeedRestrictAlpha() {
        DropShadowHelper dropShadowHelper = this.mDropShadowHelper;
        if (dropShadowHelper == null) {
            return false;
        }
        return ((!dropShadowHelper.isEnableMiShadow() && (getCardElevation() > 0.0f ? 1 : (getCardElevation() == 0.0f ? 0 : -1)) > 0) && (getCardBackgroundColor().isOpaque() ^ true)) || (isEnableBlur() && isApplyBlur());
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

    public void setDrawStrokeOverlay(boolean z) {
        if (this.mDrawStrokeOverlay != z) {
            this.mDrawStrokeOverlay = z;
            RoundRectDrawable hyperBackground = getHyperBackground();
            if (hyperBackground != null) {
                hyperBackground.setDrawStrokeOverlay(this.mDrawStrokeOverlay);
                invalidate();
            }
        }
    }

    public boolean isDrawStrokeOverlay() {
        return this.mDrawStrokeOverlay;
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
        boolean zResolveBoolean = AttributeResolver.resolveBoolean(getContext(), R.attr.isLightTheme, true);
        if (hasThemeType()) {
            zResolveBoolean = this.mUserThemeType == 1;
        }
        this.mCurrentMaterial = this.mMaterial.get(zResolveBoolean);
        if (HyperMaterialUtils.isFeatureEnable(getContext()) && this.mCurrentMaterial != null) {
            setEnableBlur(true);
            if (this.mBlurUiHelper != null && this.mCurrentMaterial.getBlurConfig() != null && this.mCurrentMaterial.getBlurConfig().colorBlendConfig != null) {
                MaterialConfig.BlurConfig blurConfig = this.mCurrentMaterial.getBlurConfig();
                MaterialConfig.ColorBlendConfig colorBlendConfig = this.mCurrentMaterial.getBlurConfig().colorBlendConfig;
                this.mBlendColors = colorBlendConfig.blendColors;
                this.mBlendColorModes = colorBlendConfig.blendModes;
                this.mBlurRadiusDp = blurConfig.blurRadius;
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
            if (this.mCurrentMaterial.getShadowConfig() != null) {
                this.mDropShadowHelper.updateDropShadowConfig(this.mCurrentMaterial.getShadowConfig());
                this.mDropShadowHelper.updateViewShadow(this, 2);
                invalidate();
                return;
            }
            return;
        }
        applyBlur(false);
        setEnableBlur(false);
        HyperBloomStrokeUtils.clearBloomStroke(this);
    }

    @Override // androidx.cardview.widget.CardView
    public void setCardElevation(float f) {
        DropShadowHelper dropShadowHelper = this.mDropShadowHelper;
        if (dropShadowHelper != null && dropShadowHelper.isEnableMiShadow()) {
            setShadowRadius(f);
            super.setCardElevation(0.0f);
        } else {
            if (isNeedRestrictAlpha()) {
                super.setAlpha(0.99f);
            } else {
                super.setAlpha(this.mUserAlpha);
            }
            super.setCardElevation(f);
        }
    }

    @Override // androidx.cardview.widget.CardView
    public void setCardBackgroundColor(int i) {
        setCardBackgroundColor(ColorStateList.valueOf(i));
    }

    @Override // androidx.cardview.widget.CardView
    public void setCardBackgroundColor(ColorStateList colorStateList) {
        RoundRectDrawable hyperBackground = getHyperBackground();
        if (hyperBackground != null) {
            hyperBackground.setColor(colorStateList);
        } else {
            super.setCardBackgroundColor(colorStateList);
        }
        this.mShadowConfigDirty = true;
        MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurUiHelper;
        if (miuiBlurUiHelper != null) {
            miuiBlurUiHelper.resetBlurParams();
            this.mBlurUiHelper.refreshBlur();
        }
    }

    @Override // androidx.cardview.widget.CardView
    public ColorStateList getCardBackgroundColor() {
        Drawable drawable = this.mBackgroundWithoutBlur;
        if (drawable instanceof RoundRectDrawable) {
            return ((RoundRectDrawable) drawable).getColor();
        }
        return super.getCardBackgroundColor();
    }

    @Override // android.view.View
    public void setOutlineSpotShadowColor(int i) {
        if (this.mDropShadowHelper.isEnableMiShadow()) {
            setShadowColor(i);
            if (Build.VERSION.SDK_INT >= 28) {
                super.setOutlineSpotShadowColor(0);
                return;
            }
            return;
        }
        if (Build.VERSION.SDK_INT >= 28) {
            super.setOutlineSpotShadowColor(i);
        }
    }

    public void setBlurRadius(int i) {
        if (this.mBlurRadius != i) {
            this.mBlurRadius = i;
            this.mBlurRadiusDp = i == 0 ? 0 : (int) ((i / getContext().getResources().getDisplayMetrics().density) + 0.5f);
            if (this.mBlurUiHelper != null) {
                MaterialDayNightConfig materialDayNightConfigUpdateMaterial = updateMaterial();
                this.mMaterial = materialDayNightConfigUpdateMaterial;
                if (materialDayNightConfigUpdateMaterial != null) {
                    this.mCurrentMaterial = this.mMaterial.get(AttributeResolver.resolveBoolean(getContext(), R.attr.isLightTheme, true));
                }
                this.mBlurUiHelper.resetBlurParams();
                if (isApplyBlur() && i == 0) {
                    this.mBlurUiHelper.applyBlur(false);
                } else {
                    this.mBlurUiHelper.refreshBlur();
                }
            }
        }
    }

    public void setBlendColorParams(int[] iArr, int[] iArr2) {
        setBlendColorParams(iArr, iArr2, this.mBlendColors != null, null);
    }

    public void setBlendColorParams(int[] iArr, int[] iArr2, BlendColorTransitionController blendColorTransitionController) {
        setBlendColorParams(iArr, iArr2, this.mBlendColors != null, blendColorTransitionController);
    }

    public void setBlendColorParamsWithoutAnim(int[] iArr, int[] iArr2) {
        setBlendColorParams(iArr, iArr2, false, null);
    }

    public void setBlendColorParams(int[] iArr, int[] iArr2, boolean z, BlendColorTransitionController blendColorTransitionController) {
        MaterialDayNightConfig materialDayNightConfig = this.mMaterial;
        this.mBlendColors = iArr;
        this.mBlendColorModes = iArr2;
        MaterialDayNightConfig materialDayNightConfigUpdateMaterial = updateMaterial();
        this.mMaterial = materialDayNightConfigUpdateMaterial;
        if (materialDayNightConfigUpdateMaterial != null) {
            this.mCurrentMaterial = this.mMaterial.get(AttributeResolver.resolveBoolean(getContext(), R.attr.isLightTheme, true));
        }
        updateBlendParams(z, materialDayNightConfig);
        if (!z) {
            MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurUiHelper;
            if (miuiBlurUiHelper != null) {
                miuiBlurUiHelper.resetBlurParams();
                this.mBlurUiHelper.refreshBlur();
                return;
            }
            return;
        }
        if (this.mLastBlendColors == null && this.mBlendColors == null) {
            return;
        }
        startBlurChangedAnimation(blendColorTransitionController);
    }

    private void updateBlendParams(boolean z, MaterialDayNightConfig materialDayNightConfig) {
        if (z && materialDayNightConfig != null && materialDayNightConfig.getDefaultBlurConfig() != null && materialDayNightConfig.getDefaultBlurConfig().colorBlendConfig != null) {
            MaterialConfig.BlurConfig defaultBlurConfig = materialDayNightConfig.getDefaultBlurConfig();
            this.mLastBlendColors = defaultBlurConfig.colorBlendConfig.blendColors;
            this.mLastBlendColorModes = defaultBlurConfig.colorBlendConfig.blendModes;
        } else {
            this.mLastBlendColors = null;
            this.mLastBlendColorModes = null;
        }
        MaterialConfig materialConfig = this.mCurrentMaterial;
        if (materialConfig != null && materialConfig.getBlurConfig() != null && this.mCurrentMaterial.getBlurConfig().colorBlendConfig != null) {
            MaterialConfig.BlurConfig blurConfig = this.mCurrentMaterial.getBlurConfig();
            this.mBlendColors = blurConfig.colorBlendConfig.blendColors;
            this.mBlendColorModes = blurConfig.colorBlendConfig.blendModes;
        } else {
            this.mBlendColors = null;
            this.mBlendColorModes = null;
        }
    }

    private void startBlurChangedAnimation(BlendColorTransitionController blendColorTransitionController) {
        final BlendColorTransitionListener blendColorTransitionListener = new BlendColorTransitionListener() { // from class: miuix.cardview.HyperCardView.2
            @Override // miuix.cardview.HyperCardView.BlendColorTransitionListener
            public void onStart() {
                HyperCardView.this.mIsBlendColorAnimRunning = true;
            }

            @Override // miuix.cardview.HyperCardView.BlendColorTransitionListener
            public void onUpdate(float f) {
                HyperCardView.this.mIsBlendColorAnimProgress = f;
                if (HyperCardView.this.mBlurUiHelper != null) {
                    HyperCardView.this.mBlurUiHelper.resetBlurParams();
                    HyperCardView.this.mBlurUiHelper.refreshBlur();
                }
            }

            @Override // miuix.cardview.HyperCardView.BlendColorTransitionListener
            public void onEnd() {
                HyperCardView.this.mIsBlendColorAnimRunning = false;
                HyperCardView.this.mIsBlendColorAnimProgress = 0.0f;
                HyperCardView.this.mTmpBlendColorModes = null;
                HyperCardView.this.mLastBlendColors = null;
                HyperCardView.this.mLastBlendColorModes = null;
            }

            @Override // miuix.cardview.HyperCardView.BlendColorTransitionListener
            public void onCancel() {
                HyperCardView.this.mIsBlendColorAnimRunning = false;
                HyperCardView.this.mIsBlendColorAnimProgress = 0.0f;
                HyperCardView.this.mTmpBlendColorModes = null;
                HyperCardView.this.mLastBlendColors = null;
                HyperCardView.this.mLastBlendColorModes = null;
            }
        };
        int[] iArr = this.mLastBlendColorModes;
        int i = 0;
        int length = iArr != null ? iArr.length : 0;
        int[] iArr2 = this.mBlendColorModes;
        this.mTmpBlendColorModes = new int[(iArr2 != null ? iArr2.length : 0) + length];
        if (iArr != null && iArr.length > 0) {
            int i2 = 0;
            while (true) {
                int[] iArr3 = this.mLastBlendColorModes;
                if (i2 >= iArr3.length) {
                    break;
                }
                this.mTmpBlendColorModes[i2] = iArr3[i2];
                i2++;
            }
        }
        int[] iArr4 = this.mBlendColorModes;
        if (iArr4 != null && iArr4.length > 0 && iArr4.length + length <= this.mTmpBlendColorModes.length) {
            while (true) {
                int[] iArr5 = this.mBlendColorModes;
                if (i >= iArr5.length) {
                    break;
                }
                this.mTmpBlendColorModes[length + i] = iArr5[i];
                i++;
            }
        }
        if (blendColorTransitionController != null) {
            blendColorTransitionController.setListener(blendColorTransitionListener);
            return;
        }
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        valueAnimatorOfFloat.setDuration(150L);
        valueAnimatorOfFloat.addListener(new Animator.AnimatorListener() { // from class: miuix.cardview.HyperCardView.3
            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationRepeat(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
                blendColorTransitionListener.onStart();
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                blendColorTransitionListener.onEnd();
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                blendColorTransitionListener.onCancel();
            }
        });
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: miuix.cardview.HyperCardView.4
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                blendColorTransitionListener.onUpdate(((Float) valueAnimator.getAnimatedValue()).floatValue());
            }
        });
        valueAnimatorOfFloat.start();
    }

    public void setStrokeWidth(float f) {
        if (this.mStrokeWidth != f) {
            this.mStrokeWidth = f;
            RoundRectDrawable hyperBackground = getHyperBackground();
            if (hyperBackground != null) {
                hyperBackground.setStrokeWidth(f);
            }
        }
    }

    public float getStrokeWidth() {
        return this.mStrokeWidth;
    }

    public void setStrokeColor(int i) {
        if (this.mStrokeColor != i) {
            this.mStrokeColor = i;
            RoundRectDrawable hyperBackground = getHyperBackground();
            if (hyperBackground != null) {
                hyperBackground.setStrokeColor(i);
            }
        }
    }

    public int getStrokeColor() {
        return this.mStrokeColor;
    }

    public void setStrokeGradientColors(int i, int i2) {
        this.mStrokeGradientColors = new int[]{i, i2};
        this.mStrokeGradientColorPositions = new float[]{0.0f, 1.0f};
        RoundRectDrawable hyperBackground = getHyperBackground();
        if (hyperBackground != null) {
            hyperBackground.setStrokeGradientColors(this.mStrokeGradientColors);
            hyperBackground.setStrokeColorGradientPositions(this.mStrokeGradientColorPositions);
        }
    }

    public void setStrokeGradientColors(int[] iArr, float[] fArr) {
        this.mStrokeGradientColors = iArr;
        this.mStrokeGradientColorPositions = fArr;
        RoundRectDrawable hyperBackground = getHyperBackground();
        if (hyperBackground != null) {
            hyperBackground.setStrokeGradientColors(this.mStrokeGradientColors);
            hyperBackground.setStrokeColorGradientPositions(this.mStrokeGradientColorPositions);
        }
    }

    public void enableUseCompatShadow(boolean z) {
        if (this.mUseCompatShadow != z) {
            if (z) {
                this.mDropShadowHelper.setEnableMiShadow(false);
            } else {
                this.mDropShadowHelper.setEnableMiShadow(RomUtils.getHyperOsVersion() >= 2 && MiShadowUtils.SUPPORT_MI_SHADOW);
            }
            this.mDropShadowHelper.enableViewShadow(this, false, 2);
            this.mUseCompatShadow = z;
            float f = (this.mShadowRadiusDp * getResources().getDisplayMetrics().density) + 0.5f;
            setShadowRadius(f);
            setCardElevation(f);
            setOutlineSpotShadowColor(this.mShadowColor);
            requestLayout();
        }
    }

    public void setShadowRadius(float f) {
        setShadowRadiusDp(f == 0.0f ? 0.0f : (f / getResources().getDisplayMetrics().density) + 0.5f);
        if (!this.mDropShadowHelper.isEnableMiShadow()) {
            super.setCardElevation(f);
        } else {
            super.setCardElevation(0.0f);
        }
    }

    public void setShadowRadiusDp(float f) {
        if (this.mShadowRadiusDp != f) {
            this.mShadowRadiusDp = f;
            this.mShadowConfigDirty = true;
            invalidate();
        }
    }

    public void setShadowDx(float f) {
        if (this.mShadowDxDp != f) {
            this.mShadowDxDp = f;
            this.mShadowConfigDirty = true;
            invalidate();
        }
    }

    public void setShadowDy(float f) {
        if (this.mShadowDyDp != f) {
            this.mShadowDyDp = f;
            this.mShadowConfigDirty = true;
            invalidate();
        }
    }

    public void setShadowColor(int i) {
        if (this.mShadowColor != i) {
            this.mShadowColor = i;
            this.mShadowConfigDirty = true;
            if (!this.mDropShadowHelper.isEnableMiShadow()) {
                setOutlineSpotShadowColor(i);
            } else if (Build.VERSION.SDK_INT >= 28) {
                super.setOutlineSpotShadowColor(0);
            }
            invalidate();
        }
    }

    public int getShadowColor() {
        return this.mShadowColor;
    }

    private DropShadowConfig updateShadowConfig() {
        int defaultColor = getCardBackgroundColor().getDefaultColor();
        DropShadowConfig.Builder offsetYDp = new DropShadowConfig.Builder(this.mShadowRadiusDp).setOffsetXDp((int) this.mShadowDxDp).setOffsetYDp((int) this.mShadowDyDp);
        int i = this.mShadowColor;
        return offsetYDp.setColor(i, i).setDispersion(this.mShadowDispersion).setClipShadowEnable(Color.alpha(defaultColor) != 255 || isApplyBlur()).create();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.mShadowConfigDirty) {
            this.mShadowConfigDirty = false;
            DropShadowHelper dropShadowHelper = this.mDropShadowHelper;
            if (dropShadowHelper != null) {
                dropShadowHelper.updateDropShadowConfig(updateShadowConfig());
                this.mDropShadowHelper.updateViewShadow(this, 2);
            }
        }
        super.onDraw(canvas);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        DropShadowHelper dropShadowHelper = this.mDropShadowHelper;
        if (dropShadowHelper != null) {
            dropShadowHelper.updateShadowRect(i, i2, i3, i4);
            if (this.mShadowRadiusDp > 0.0f) {
                this.mDropShadowHelper.enableViewShadow(this, true, 2);
            } else {
                this.mDropShadowHelper.enableViewShadow(this, false, 2);
            }
        }
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        RoundRectDrawable hyperBackground;
        super.draw(canvas);
        if (!this.mDrawStrokeOverlay || (hyperBackground = getHyperBackground()) == null) {
            return;
        }
        hyperBackground.drawStroke(canvas);
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        updateMaterialEffect();
    }

    private void setOutlineProviderFromAttribute(Context context, String str, int i) {
        try {
            Class<? extends U> clsAsSubclass = Class.forName(str).asSubclass(ViewOutlineProvider.class);
            try {
                try {
                    setOutlineProvider((ViewOutlineProvider) clsAsSubclass.getConstructor(Context.class, Integer.TYPE).newInstance(context, Integer.valueOf(i)));
                } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            } catch (IllegalAccessException | NoSuchMethodException unused) {
                setOutlineProvider((ViewOutlineProvider) clsAsSubclass.getConstructor(new Class[0]).newInstance(new Object[0]));
            } catch (InstantiationException e2) {
                e = e2;
                throw new RuntimeException(e);
            } catch (InvocationTargetException e3) {
                e = e3;
                throw new RuntimeException(e);
            }
        } catch (ClassNotFoundException unused2) {
            throw new NoClassDefFoundError(str);
        }
    }

    @Override // miuix.view.BlurableWidget
    public void setSupportBlur(boolean z) {
        MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurUiHelper;
        if (miuiBlurUiHelper == null) {
            return;
        }
        miuiBlurUiHelper.setSupportBlur(z);
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

    public void applyBlurSmooth(boolean z) {
        if (this.mBlurUiHelper == null || !isEnableBlur() || isApplyBlur() == z || this.mBackgroundWithoutBlur == null) {
            return;
        }
        ValueAnimator valueAnimator = this.mRunningBgAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        if (z) {
            ValueAnimator valueAnimatorOfInt = ValueAnimator.ofInt(255, 0);
            this.mRunningBgAnimator = valueAnimatorOfInt;
            this.mBlurUiHelper.applyBlur(true);
            valueAnimatorOfInt.setDuration(100L);
            valueAnimatorOfInt.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: miuix.cardview.HyperCardView.5
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    Integer num = (Integer) valueAnimator2.getAnimatedValue();
                    if (num != null) {
                        HyperCardView.this.mBackgroundWithoutBlur.setAlpha(num.intValue());
                        HyperCardView.this.invalidate();
                        if (num.intValue() == 0) {
                            HyperCardView.this.mRunningBgAnimator = null;
                        }
                    }
                }
            });
            valueAnimatorOfInt.start();
            return;
        }
        ValueAnimator valueAnimatorOfInt2 = ValueAnimator.ofInt(128, 255);
        this.mRunningBgAnimator = valueAnimatorOfInt2;
        valueAnimatorOfInt2.setDuration(50L);
        valueAnimatorOfInt2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: miuix.cardview.HyperCardView.6
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                Integer num = (Integer) valueAnimator2.getAnimatedValue();
                if (num != null) {
                    HyperCardView.this.mBackgroundWithoutBlur.setAlpha(num.intValue());
                    HyperCardView.this.invalidate();
                    if (num.intValue() == 255) {
                        HyperCardView.this.mBlurUiHelper.applyBlur(false);
                        HyperCardView.this.mRunningBgAnimator = null;
                    }
                }
            }
        });
        valueAnimatorOfInt2.start();
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

    public static class BlendColorTransitionController {
        private BlendColorTransitionListener mListener;

        public void setListener(BlendColorTransitionListener blendColorTransitionListener) {
            this.mListener = blendColorTransitionListener;
        }

        public void start() {
            BlendColorTransitionListener blendColorTransitionListener = this.mListener;
            if (blendColorTransitionListener != null) {
                blendColorTransitionListener.onStart();
            }
        }

        public void update(float f) {
            BlendColorTransitionListener blendColorTransitionListener = this.mListener;
            if (blendColorTransitionListener != null) {
                blendColorTransitionListener.onUpdate(f);
            }
        }

        public void end() {
            BlendColorTransitionListener blendColorTransitionListener = this.mListener;
            if (blendColorTransitionListener != null) {
                blendColorTransitionListener.onEnd();
            }
        }

        public void cancel() {
            BlendColorTransitionListener blendColorTransitionListener = this.mListener;
            if (blendColorTransitionListener != null) {
                blendColorTransitionListener.onCancel();
            }
        }
    }
}
