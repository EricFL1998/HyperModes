package miuix.view;

import android.R;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;
import miuix.core.util.HyperMaterialUtils;
import miuix.core.util.MaterialConfig;
import miuix.core.util.MiuiBlurUtils;
import miuix.core.util.MiuixUIUtils;
import miuix.internal.util.AttributeResolver;

/* JADX INFO: loaded from: classes3.dex */
public class MiuiBlurUiHelper implements BlurableWidget {
    private boolean mApplyBlur;
    private int[] mBlurBlendColorModes;
    private int[] mBlurBlendColors;
    private int mBlurEffect;
    private final BlurStateCallback mCallback;
    private final Context mContext;
    private final boolean mCreateParamsByCallback;
    private boolean mEnableBlurSelfAsBackground;
    private boolean mIsEnableBlur;
    private final boolean mIsSpecialShape;
    private boolean mIsSupportBlur;
    private boolean mNeedApplyBlur;
    private boolean mNeedEnableBlur;
    private final View mViewApplyBlur;
    private int mViewBlurMode;

    public interface BlurStateCallback {
        default Drawable getBackground() {
            return null;
        }

        default int getBackgroundColor() {
            return 0;
        }

        default MaterialConfig.BlurConfig getBlurConfig(boolean z) {
            return null;
        }

        default boolean isLightTheme() {
            return true;
        }

        void onBlurApplyStateChanged(boolean z);

        void onBlurEnableStateChanged(boolean z);

        default void onCreateBlurParams(MiuiBlurUiHelper miuiBlurUiHelper) {
        }
    }

    public MiuiBlurUiHelper(Context context, View view, boolean z, BlurStateCallback blurStateCallback) {
        this(context, view, z, true, blurStateCallback);
    }

    public MiuiBlurUiHelper(Context context, View view, boolean z, boolean z2, BlurStateCallback blurStateCallback) {
        this(context, view, z, z2, true, blurStateCallback);
    }

    public MiuiBlurUiHelper(Context context, View view, boolean z, boolean z2, boolean z3, BlurStateCallback blurStateCallback) {
        this.mIsSupportBlur = false;
        this.mNeedEnableBlur = false;
        this.mIsEnableBlur = false;
        this.mNeedApplyBlur = false;
        this.mApplyBlur = false;
        this.mBlurBlendColors = null;
        this.mBlurBlendColorModes = null;
        this.mBlurEffect = 0;
        this.mContext = context;
        this.mCreateParamsByCallback = z2;
        this.mViewApplyBlur = view;
        this.mIsSpecialShape = z;
        this.mCallback = blurStateCallback;
        this.mEnableBlurSelfAsBackground = z3;
        if (z) {
            this.mViewBlurMode = 2;
        } else {
            this.mViewBlurMode = 1;
        }
    }

    public void setViewBlurMode(int i) {
        this.mViewBlurMode = i;
        resetBlurParams();
    }

    public void enableBlurSelfAsBackground(boolean z) {
        this.mEnableBlurSelfAsBackground = z;
        resetBlurParams();
    }

    @Override // miuix.view.BlurableWidget
    public void setSupportBlur(boolean z) {
        this.mIsSupportBlur = z;
    }

    @Override // miuix.view.BlurableWidget
    public boolean isSupportBlur() {
        return this.mIsSupportBlur;
    }

    @Override // miuix.view.BlurableWidget
    public void setEnableBlur(boolean z) {
        if (this.mIsSupportBlur) {
            this.mNeedEnableBlur = z;
            if (HyperMaterialUtils.isFeatureEnable(this.mContext)) {
                setEnableBlurInternal(this.mNeedEnableBlur);
            }
        }
    }

    @Override // miuix.view.BlurableWidget
    public boolean isEnableBlur() {
        return this.mNeedEnableBlur;
    }

    private void setEnableBlurInternal(boolean z) {
        if (this.mIsEnableBlur != z) {
            if (!z) {
                this.mNeedApplyBlur = isApplyBlur();
                applyBlurInternal(false);
            }
            this.mIsEnableBlur = z;
            this.mCallback.onBlurEnableStateChanged(z);
            if (z && this.mNeedApplyBlur) {
                applyBlurInternal(true);
            }
        }
    }

    @Override // miuix.view.BlurableWidget
    public void applyBlur(boolean z) {
        this.mNeedApplyBlur = z;
        applyBlurInternal(z);
    }

    private void applyBlurInternal(boolean z) {
        if (this.mIsSupportBlur && this.mIsEnableBlur && this.mApplyBlur != z) {
            this.mApplyBlur = z;
            if (z) {
                refreshBlur();
                return;
            }
            MiuiBlurUtils.clearBackgroundBlur(this.mViewApplyBlur);
            MiuiBlurUtils.clearBackgroundBlendConfig(this.mViewApplyBlur);
            this.mCallback.onBlurApplyStateChanged(false);
        }
    }

    private void createBlurParamsInternal() {
        int[] finalBlendColorForViewByBackgroundColor;
        int[] iArr;
        BlurStateCallback blurStateCallback = this.mCallback;
        MaterialConfig.BlurConfig blurConfig = blurStateCallback.getBlurConfig(blurStateCallback.isLightTheme());
        Drawable background = this.mCallback.getBackground();
        int backgroundColor = this.mCallback.getBackgroundColor();
        if (blurConfig != null) {
            int i = blurConfig.blurRadius;
            if (blurConfig.colorBlendConfig != null) {
                MaterialConfig.ColorBlendConfig colorBlendConfig = blurConfig.colorBlendConfig;
                int[] iArr2 = colorBlendConfig.blendColors;
                if (backgroundColor != 0) {
                    finalBlendColorForViewByBackgroundColor = getFinalBlendColorForViewByBackgroundColor(this.mContext, backgroundColor, iArr2, colorBlendConfig.blendModes);
                } else {
                    finalBlendColorForViewByBackgroundColor = getFinalBlendColorForViewByBackgroundColor(this.mContext, background, iArr2, colorBlendConfig.blendModes);
                }
                if (finalBlendColorForViewByBackgroundColor.length > colorBlendConfig.blendModes.length) {
                    int length = finalBlendColorForViewByBackgroundColor.length;
                    iArr = new int[length];
                    System.arraycopy(colorBlendConfig.blendModes, 0, iArr, 0, colorBlendConfig.blendModes.length);
                    iArr[length - 1] = 3;
                } else {
                    iArr = colorBlendConfig.blendModes;
                }
                setBlurParams(finalBlendColorForViewByBackgroundColor, iArr, i);
                return;
            }
            this.mBlurEffect = blurConfig.blurRadius;
        }
    }

    @Override // miuix.view.BlurableWidget
    public boolean isApplyBlur() {
        return this.mNeedApplyBlur;
    }

    public void setBlurParams(int[] iArr, int[] iArr2, int i) {
        this.mBlurBlendColors = iArr;
        this.mBlurBlendColorModes = iArr2;
        this.mBlurEffect = i;
    }

    public View getViewApplyBlur() {
        return this.mViewApplyBlur;
    }

    public void onConfigChanged() {
        resetBlurParams();
        if (!HyperMaterialUtils.isFeatureEnable(this.mContext)) {
            setEnableBlurInternal(false);
        } else if (HyperMaterialUtils.isEnable() && HyperMaterialUtils.isFeatureEnable(this.mContext) && isEnableBlur()) {
            setEnableBlurInternal(true);
        }
    }

    public void resetBlurParams() {
        this.mBlurBlendColors = null;
        this.mBlurBlendColorModes = null;
        this.mBlurEffect = 0;
    }

    public void refreshBlur() {
        float f;
        int[] iArr;
        if (this.mApplyBlur) {
            if (this.mBlurBlendColors == null) {
                if (this.mEnableBlurSelfAsBackground) {
                    MiuiBlurUtils.clearBackgroundBlur(this.mViewApplyBlur);
                } else {
                    MiuiBlurUtils.setViewBlurMode(this.mViewApplyBlur, 0);
                }
                MiuiBlurUtils.clearBackgroundBlendConfig(this.mViewApplyBlur);
                if (this.mCreateParamsByCallback) {
                    this.mCallback.onCreateBlurParams(this);
                } else {
                    createBlurParamsInternal();
                }
            }
            try {
                f = this.mViewApplyBlur.getContext().getResources().getDisplayMetrics().density;
            } catch (Exception unused) {
                f = 2.75f;
            }
            this.mCallback.onBlurApplyStateChanged(true);
            if (this.mEnableBlurSelfAsBackground) {
                int i = this.mBlurEffect;
                if (i > 0) {
                    MiuiBlurUtils.setBackgroundBlur(this.mViewApplyBlur, (int) ((i * f) + 0.5f), this.mViewBlurMode);
                } else {
                    MiuiBlurUtils.clearBackgroundBlur(this.mViewApplyBlur);
                }
            } else {
                MiuiBlurUtils.setViewBlurMode(this.mViewApplyBlur, this.mViewBlurMode);
                int i2 = this.mBlurEffect;
                if (i2 >= 0) {
                    MiuiBlurUtils.setBackgroundBlurRadius(this.mViewApplyBlur, (int) ((i2 * f) + 0.5f));
                }
            }
            int[] iArr2 = this.mBlurBlendColors;
            if (iArr2 == null || (iArr = this.mBlurBlendColorModes) == null) {
                return;
            }
            MiuiBlurUtils.setBackgroundBlendConfig(this.mViewApplyBlur, iArr2, iArr);
        }
    }

    public static int[] getFinalBlendColorForViewByBackgroundColor(Context context, Drawable drawable, int[] iArr, int[] iArr2) {
        Integer colorFromDrawable;
        return (drawable == null || (colorFromDrawable = MiuixUIUtils.getColorFromDrawable(drawable)) == null) ? iArr : getFinalBlendColorForViewByBackgroundColor(context, colorFromDrawable.intValue(), iArr, iArr2);
    }

    public static int[] getFinalBlendColorForViewByBackgroundColor(Context context, int i, int[] iArr, int[] iArr2) {
        System.arraycopy(iArr, 0, iArr, 0, iArr.length);
        if (i == 0) {
            Drawable drawableResolveDrawable = AttributeResolver.resolveDrawable(context, R.attr.windowBackground);
            if (drawableResolveDrawable instanceof ColorDrawable) {
                i = ((ColorDrawable) drawableResolveDrawable).getColor();
            }
        }
        if (i == 0 || i == iArr[iArr.length - 1]) {
            return iArr;
        }
        int i2 = (i >> 16) & 255;
        int i3 = i & 255;
        if (i2 == ((i >> 8) & 255) && i2 == i3) {
            int length = iArr.length - 1;
            iArr[length] = (i & 16777215) | ((-16777216) & iArr[length]);
            return iArr;
        }
        int length2 = iArr.length;
        int[] iArr3 = new int[length2 + 1];
        System.arraycopy(iArr, 0, iArr3, 0, iArr.length);
        iArr3[length2] = (i & 16777215) | 805306368;
        return iArr3;
    }

    public static void enableOnlyTextBlur(TextView textView, boolean z, int i, int[] iArr, int[] iArr2) {
        if (textView == null) {
            return;
        }
        Object parent = textView.getParent();
        if (parent != null) {
            if (z) {
                View view = (View) parent;
                MiuiBlurUtils.setBackgroundBlur(view, i);
                MiuiBlurUtils.setViewBlurMode(view, 0);
            } else {
                MiuiBlurUtils.clearBackgroundBlur((View) parent);
            }
        }
        enableTextBlur(textView, z, iArr, iArr2);
    }

    @Deprecated
    public static void enableTextBlur(TextView textView, boolean z, int[] iArr, int[] iArr2) {
        if (textView == null) {
            return;
        }
        if (z) {
            MiuiBlurUtils.setViewBlurMode(textView, 3);
            MiuiBlurUtils.setBackgroundBlendConfig(textView, iArr, iArr2);
        } else {
            MiuiBlurUtils.setViewBlurMode(textView, 0);
            MiuiBlurUtils.clearBackgroundBlendConfig(textView);
        }
    }
}
