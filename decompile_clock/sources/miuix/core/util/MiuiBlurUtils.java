package miuix.core.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

/* JADX INFO: loaded from: classes2.dex */
public class MiuiBlurUtils {
    public static final int BG_MODE_ENABLE = 1;
    public static final int BG_MODE_ENABLE_NOT_DRAW = 3;
    public static final int BG_MODE_NONE = 0;
    public static final int BG_MODE_ONLY_BLUR = 2;
    public static final int CONTENT_MODE_ENABLE = 1;
    public static final int CONTENT_MODE_ENABLE_ANY_SHAPE = 2;
    public static final int CONTENT_MODE_ENABLE_SELF = 3;
    public static final int CONTENT_MODE_ENABLE_SELF_LIGHT_BLEND = 4;
    public static final int CONTENT_MODE_NONE = 0;
    public static final int GRADIENT_ORI_HORI = 2;
    public static final int GRADIENT_ORI_VER = 1;
    public static Method METHOD_ADD_BG_BLEND_COLOR = null;
    public static Method METHOD_CHOOSE_BG_BLUR_CONTAINER = null;
    public static Method METHOD_CLEAR_BG_BLEND_COLOR = null;
    public static Method METHOD_DISABLE_BG_CONTAIN_BELOW = null;
    public static Method METHOD_GET_BG_BLUR_MODE = null;
    public static Method METHOD_GET_BG_BLUR_RADIUS = null;
    public static Method METHOD_GET_MIX_EFFECT_ENABLED = null;
    public static Method METHOD_GET_PASS_WINDOW_BLUR_MODE = null;
    public static Method METHOD_GET_SELF_BLUR_TYPE = null;
    public static Method METHOD_SET_BG_BLEND_COLORS = null;
    public static Method METHOD_SET_BG_BLUR_ENHANCE_FLAG = null;
    public static Method METHOD_SET_BG_BLUR_MODE = null;
    public static Method METHOD_SET_BG_BLUR_RADIUS = null;
    public static Method METHOD_SET_BG_BLUR_SCALE_RATIO = null;
    public static Method METHOD_SET_BG_BLUR_TYPE = null;
    public static Method METHOD_SET_BG_GRADIENT_BLUR_PARAMS = null;
    public static Method METHOD_SET_BG_LIGHT_BLEND_MODE = null;
    public static Method METHOD_SET_COLOR_ADJUST = null;
    public static Method METHOD_SET_MIX_EFFECT_ENABLED = null;
    public static Method METHOD_SET_PASS_TEXTURE_SCALE = null;
    public static Method METHOD_SET_PASS_WINDOW_BLUR_MODE = null;
    public static Method METHOD_SET_SELF_BLUR = null;
    public static Method METHOD_SET_SELF_BLUR_ENHANCE_FLAG = null;
    public static Method METHOD_SET_SELF_BLUR_TYPE = null;
    public static Method METHOD_SET_SELF_GRADIENT_BLUR_PARAMS = null;
    public static Method METHOD_SET_VIEW_BLUR_MODE = null;
    public static final int TYPE_GAUSS = 0;
    public static final int TYPE_GRADIENT = 2;
    public static final int TYPE_KAWASSE = 1;

    @Deprecated
    public static boolean isEnable() {
        return HyperMaterialUtils.isEnable();
    }

    @Deprecated
    public static synchronized boolean isEffectEnable(Context context) {
        if (!HyperMaterialUtils.isEnable()) {
            return false;
        }
        return HyperMaterialUtils.isFeatureEnable(context);
    }

    @Deprecated
    public static synchronized void clearEffectEnable() {
        HyperMaterialUtils.clearFeatureEnable();
    }

    public static void setBlurConfig(View view, float f, MaterialConfig.BlurConfig blurConfig) {
        int i = 0;
        if (blurConfig != null) {
            int i2 = (int) ((blurConfig.blurRadius * f) + 0.5f);
            if (blurConfig.blurBgMode > 0) {
                setBackgroundBlur(view, i2, blurConfig.blurBgMode);
            }
            if (blurConfig.blurContentMode > 0) {
                int i3 = blurConfig.blurContentMode;
                if (i3 == 1 || i3 == 2 || i3 == 3) {
                    setViewBlurMode(view, blurConfig.blurContentMode);
                    setBackgroundBlurRadius(view, i2);
                } else if (i3 == 4 && blurConfig.colorBlendConfig != null) {
                    setBackgroundLightBlendMode(view, 1);
                }
            }
            if (blurConfig.colorBlendConfig != null) {
                MaterialConfig.ColorBlendConfig colorBlendConfig = blurConfig.colorBlendConfig;
                if (colorBlendConfig.blendColors != null && colorBlendConfig.blendModes != null) {
                    setBackgroundBlendConfig(view, wrapBlendConfig(colorBlendConfig.blendColors, colorBlendConfig.blendModes));
                    if (colorBlendConfig.blendExtraParams != null) {
                        float[] fArr = colorBlendConfig.blendExtraParams;
                        int length = fArr.length / 7;
                        ArrayList arrayList = new ArrayList();
                        while (i < length) {
                            int i4 = i + 1;
                            arrayList.add(new Pair(Integer.valueOf(i4), Arrays.copyOfRange(fArr, i, i + 6)));
                            i = i4;
                        }
                        setColorAdjust(view, arrayList);
                    }
                }
            }
            setBackgroundBlurType(view, blurConfig.blurType);
            if (blurConfig.blurType != 2 || blurConfig.blurExtraParams == null) {
                return;
            }
            setBackgroundGradientBlurParams(view, blurConfig.blurExtraParams, blurConfig.blurSubType);
            return;
        }
        setViewBlurMode(view, 0);
        setBackgroundBlurType(view, 0);
        clearBackgroundBlendConfig(view);
    }

    public static void enableContentBlur(View view, boolean z, MaterialConfig.ColorBlendConfig colorBlendConfig) {
        if (view == null) {
            return;
        }
        if (z) {
            setViewBlurMode(view, 3);
            setBackgroundBlendConfig(view, colorBlendConfig.blendColors, colorBlendConfig.blendModes);
        } else {
            setViewBlurMode(view, 0);
            clearBackgroundBlendConfig(view);
        }
    }

    public static void enableContentBlur(View view, boolean z, int[] iArr, int[] iArr2) {
        if (view == null) {
            return;
        }
        if (z) {
            setViewBlurMode(view, 3);
            setBackgroundBlendConfig(view, iArr, iArr2);
        } else {
            setViewBlurMode(view, 0);
            clearBackgroundBlendConfig(view);
        }
    }

    public static boolean setBackgroundBlur(View view, int i) {
        return setBackgroundBlur(view, i, false);
    }

    public static boolean setBackgroundBlur(View view, int i, boolean z) {
        if (z) {
            return setViewBlurMode(view, 2) | setBackgroundBlur(view, i, 1);
        }
        return setBackgroundBlur(view, i, 1);
    }

    public static boolean setBackgroundOnlyBlur(View view, int i) {
        if (view == null || !HyperMaterialUtils.isEnable() || !HyperMaterialUtils.isFeatureEnable(view.getContext())) {
            return false;
        }
        if (i > 400) {
            i = 400;
        }
        try {
            if (METHOD_SET_BG_BLUR_MODE == null) {
                METHOD_SET_BG_BLUR_MODE = View.class.getMethod("setMiBackgroundBlurMode", Integer.TYPE);
            }
            METHOD_SET_BG_BLUR_MODE.invoke(view, 1);
            if (i >= 0) {
                if (METHOD_SET_BG_BLUR_RADIUS == null) {
                    METHOD_SET_BG_BLUR_RADIUS = View.class.getMethod("setMiBackgroundBlurRadius", Integer.TYPE);
                }
                METHOD_SET_BG_BLUR_RADIUS.invoke(view, Integer.valueOf(i));
            }
            return true;
        } catch (Exception unused) {
            METHOD_SET_BG_BLUR_MODE = null;
            METHOD_SET_BG_BLUR_RADIUS = null;
            return false;
        }
    }

    public static boolean setBackgroundBlur(View view, int i, int i2) {
        if (view == null || !HyperMaterialUtils.isEnable() || !HyperMaterialUtils.isFeatureEnable(view.getContext())) {
            return false;
        }
        if (i > 400) {
            i = 400;
        }
        try {
            if (METHOD_SET_BG_BLUR_MODE == null) {
                METHOD_SET_BG_BLUR_MODE = View.class.getMethod("setMiBackgroundBlurMode", Integer.TYPE);
            }
            METHOD_SET_BG_BLUR_MODE.invoke(view, 1);
            if (i >= 0) {
                if (METHOD_SET_BG_BLUR_RADIUS == null) {
                    METHOD_SET_BG_BLUR_RADIUS = View.class.getMethod("setMiBackgroundBlurRadius", Integer.TYPE);
                }
                METHOD_SET_BG_BLUR_RADIUS.invoke(view, Integer.valueOf(i));
            }
            return setViewBlurMode(view, i2);
        } catch (Exception unused) {
            METHOD_SET_BG_BLUR_MODE = null;
            METHOD_SET_BG_BLUR_RADIUS = null;
            return false;
        }
    }

    public static boolean setBackgroundBlurRadius(View view, int i) {
        if (!HyperMaterialUtils.isEnable()) {
            return false;
        }
        if (i > 400) {
            i = 400;
        }
        if (i < 0) {
            return false;
        }
        try {
            if (METHOD_SET_BG_BLUR_RADIUS == null) {
                METHOD_SET_BG_BLUR_RADIUS = View.class.getMethod("setMiBackgroundBlurRadius", Integer.TYPE);
            }
            METHOD_SET_BG_BLUR_RADIUS.invoke(view, Integer.valueOf(i));
            return true;
        } catch (Exception unused) {
            METHOD_SET_BG_BLUR_RADIUS = null;
            return false;
        }
    }

    public static int getBackgroundBlurRadius(View view) {
        if (!HyperMaterialUtils.isEnable()) {
            return 0;
        }
        try {
            if (METHOD_GET_BG_BLUR_RADIUS == null) {
                METHOD_GET_BG_BLUR_RADIUS = View.class.getMethod("getMiBackgroundBlurRadius", new Class[0]);
            }
            return ((Integer) METHOD_GET_BG_BLUR_RADIUS.invoke(view, new Object[0])).intValue();
        } catch (Exception unused) {
            METHOD_GET_BG_BLUR_RADIUS = null;
            return 0;
        }
    }

    public static boolean setBackgroundBlurMode(View view, int i) {
        if (!HyperMaterialUtils.isEnable()) {
            return false;
        }
        try {
            if (METHOD_SET_BG_BLUR_MODE == null) {
                METHOD_SET_BG_BLUR_MODE = View.class.getMethod("setMiBackgroundBlurMode", Integer.TYPE);
            }
            METHOD_SET_BG_BLUR_MODE.invoke(view, Integer.valueOf(i));
            return true;
        } catch (Exception unused) {
            METHOD_SET_BG_BLUR_MODE = null;
            return false;
        }
    }

    public static int getBackgroundBlurMode(View view) {
        if (!HyperMaterialUtils.isEnable()) {
            return 0;
        }
        try {
            if (METHOD_GET_BG_BLUR_MODE == null) {
                METHOD_GET_BG_BLUR_MODE = View.class.getMethod("getMiBackgroundBlurMode", new Class[0]);
            }
            return ((Integer) METHOD_GET_BG_BLUR_MODE.invoke(view, new Object[0])).intValue();
        } catch (Exception unused) {
            METHOD_GET_BG_BLUR_MODE = null;
            return 0;
        }
    }

    public static boolean clearBackgroundLightBlendMode(View view) {
        if (!HyperMaterialUtils.isEnable()) {
            return false;
        }
        try {
            if (METHOD_SET_BG_LIGHT_BLEND_MODE == null) {
                METHOD_SET_BG_LIGHT_BLEND_MODE = View.class.getMethod("setMiBackgroundLightBlendMode", Integer.TYPE);
            }
            METHOD_SET_BG_LIGHT_BLEND_MODE.invoke(view, 0);
            return true;
        } catch (Exception unused) {
            METHOD_SET_BG_LIGHT_BLEND_MODE = null;
            return false;
        }
    }

    public static boolean setBackgroundLightBlendMode(View view, int i) {
        if (!HyperMaterialUtils.isEnable()) {
            return false;
        }
        try {
            if (METHOD_SET_BG_LIGHT_BLEND_MODE == null) {
                METHOD_SET_BG_LIGHT_BLEND_MODE = View.class.getMethod("setMiBackgroundLightBlendMode", Integer.TYPE);
            }
            METHOD_SET_BG_LIGHT_BLEND_MODE.invoke(view, Integer.valueOf(i));
            return true;
        } catch (Exception unused) {
            METHOD_SET_BG_LIGHT_BLEND_MODE = null;
            return false;
        }
    }

    public static boolean setBackgroundBlurType(View view, int i) {
        if (!HyperMaterialUtils.isEnable()) {
            return false;
        }
        try {
            if (METHOD_SET_BG_BLUR_TYPE == null) {
                METHOD_SET_BG_BLUR_TYPE = View.class.getMethod("setMiBackgroundBlurType", Integer.TYPE);
            }
            METHOD_SET_BG_BLUR_TYPE.invoke(view, Integer.valueOf(i));
            return true;
        } catch (Exception unused) {
            METHOD_SET_BG_BLUR_TYPE = null;
            return false;
        }
    }

    public static boolean clearBackgroundBlur(View view) {
        if (setBackgroundBlurMode(view, 0)) {
            return setViewBlurMode(view, 0);
        }
        return false;
    }

    public static boolean setViewBlurMode(View view, int i) {
        if (view == null || !HyperMaterialUtils.isEnable()) {
            return false;
        }
        try {
            if (METHOD_SET_VIEW_BLUR_MODE == null) {
                METHOD_SET_VIEW_BLUR_MODE = View.class.getMethod("setMiViewBlurMode", Integer.TYPE);
            }
            METHOD_SET_VIEW_BLUR_MODE.invoke(view, Integer.valueOf(i));
            return true;
        } catch (Exception unused) {
            METHOD_SET_VIEW_BLUR_MODE = null;
            return false;
        }
    }

    @Deprecated
    public static boolean addBackgroundBlenderColor(View view, int i, int i2) {
        if (view == null || !HyperMaterialUtils.isEnable()) {
            return false;
        }
        try {
            if (METHOD_ADD_BG_BLEND_COLOR == null) {
                METHOD_ADD_BG_BLEND_COLOR = View.class.getMethod("addMiBackgroundBlendColor", Integer.TYPE, Integer.TYPE);
            }
            METHOD_ADD_BG_BLEND_COLOR.invoke(view, Integer.valueOf(i), Integer.valueOf(i2));
            return true;
        } catch (Exception unused) {
            METHOD_ADD_BG_BLEND_COLOR = null;
            return false;
        }
    }

    public static boolean setBackgroundBlendConfig(View view, MaterialConfig.BlurConfig blurConfig) {
        if (blurConfig == null || blurConfig.colorBlendConfig == null) {
            return false;
        }
        return setBackgroundBlendConfig(view, blurConfig.colorBlendConfig);
    }

    public static boolean setBackgroundBlendConfig(View view, MaterialConfig.ColorBlendConfig colorBlendConfig) {
        if (colorBlendConfig == null) {
            return false;
        }
        return setBackgroundBlendConfig(view, colorBlendConfig.blendColors, colorBlendConfig.blendModes);
    }

    public static boolean setBackgroundBlendConfig(View view, int[] iArr, int[] iArr2) {
        if (view == null || iArr == null || iArr2 == null || !HyperMaterialUtils.isEnable()) {
            return false;
        }
        if (RomUtils.getHyperOsVersion() > 1) {
            ArrayList<Point> arrayListWrapBlendConfig = wrapBlendConfig(iArr, iArr2);
            if (arrayListWrapBlendConfig == null) {
                return false;
            }
            setBackgroundBlendConfig(view, arrayListWrapBlendConfig);
            return true;
        }
        clearBackgroundBlendConfig(view);
        int iMin = Math.min(iArr.length, iArr2.length);
        for (int i = 0; i < iMin; i++) {
            if (!addBackgroundBlenderColor(view, iArr[i], iArr2[i])) {
                clearBackgroundBlendConfig(view);
                return false;
            }
        }
        return true;
    }

    public static boolean setBackgroundBlendConfig(View view, ArrayList<Point> arrayList) {
        if (view == null || !HyperMaterialUtils.isEnable()) {
            return false;
        }
        try {
            if (METHOD_SET_BG_BLEND_COLORS == null) {
                METHOD_SET_BG_BLEND_COLORS = View.class.getMethod("setMiBackgroundBlendColors", ArrayList.class);
            }
            METHOD_SET_BG_BLEND_COLORS.invoke(view, arrayList);
            return true;
        } catch (Exception unused) {
            METHOD_SET_BG_BLEND_COLORS = null;
            return false;
        }
    }

    public static boolean setColorAdjust(View view, ArrayList<Pair<Integer, float[]>> arrayList) {
        if (view == null || !HyperMaterialUtils.isEnable()) {
            return false;
        }
        try {
            if (METHOD_SET_COLOR_ADJUST == null) {
                METHOD_SET_COLOR_ADJUST = View.class.getMethod("setMiColorAdjust", ArrayList.class);
            }
            METHOD_SET_COLOR_ADJUST.invoke(view, arrayList);
            return true;
        } catch (Exception unused) {
            METHOD_SET_COLOR_ADJUST = null;
            return false;
        }
    }

    public static boolean setBgCommonLinearGradientBlurWithDp(View view, int i, float f) {
        Resources resources;
        if (view == null || (resources = view.getResources()) == null) {
            return false;
        }
        return setBgCommonLinearGradientBlur(view, i, (f * resources.getDisplayMetrics().density) + 0.5f);
    }

    public static boolean setBgCommonLinearGradientBlurWithDp(View view, int i, float f, float f2) {
        Resources resources;
        if (view == null || (resources = view.getResources()) == null) {
            return false;
        }
        float f3 = resources.getDisplayMetrics().density;
        return setBgCommonLinearGradientBlur(view, i, (f * f3) + 0.5f, (f2 * f3) + 0.5f);
    }

    public static boolean setBgCommonLinearGradientBlur(View view, int i, float f) {
        if (view == null) {
            return false;
        }
        return setBgCommonLinearGradientBlur(view, i, f, 255.0f);
    }

    public static boolean setBgCommonLinearGradientBlur(View view, int i, float f, float f2) {
        float[] fArr;
        if (view == null) {
            return false;
        }
        if (i == 1) {
            float height = view.getHeight();
            fArr = new float[]{0.0f, Math.max(0.0f, height - f2), f, 0.0f, height, 0.0f};
        } else if (i == 2) {
            float width = view.getWidth();
            fArr = new float[]{Math.max(0.0f, width - f2), 0.0f, f, width, 0.0f, 0.0f};
        } else {
            fArr = null;
        }
        return setBackgroundGradientBlur(view, fArr, 1);
    }

    public static boolean setBackgroundLinearGradientBlur(View view, float[] fArr) {
        return setBackgroundGradientBlur(view, fArr, 1);
    }

    public static float[] wrapLinearGradientBlurConfig(float f, float f2, float f3, float f4, float f5, float f6) {
        return new float[]{f, f2, f3, f4, f5, f6};
    }

    public static boolean setBackgroundMultiRadialGradientBlur(View view, float[] fArr) {
        return setBackgroundGradientBlur(view, fArr, 2);
    }

    public static float[] wrapRadialGradientBlurConfig(float f, float f2, float f3, float f4, float f5) {
        return new float[]{f, f2, f3, f4, f5};
    }

    public static boolean setBackgroundGradientBlur(View view, float[] fArr, int i) {
        if (!HyperMaterialUtils.isEnable() || !HyperMaterialUtils.isDefaultFeatureEnable()) {
            return false;
        }
        try {
            if (METHOD_SET_BG_BLUR_TYPE == null) {
                METHOD_SET_BG_BLUR_TYPE = View.class.getMethod("setMiBackgroundBlurType", Integer.TYPE);
            }
            METHOD_SET_BG_BLUR_TYPE.invoke(view, 2);
            try {
                if (METHOD_SET_BG_GRADIENT_BLUR_PARAMS == null) {
                    METHOD_SET_BG_GRADIENT_BLUR_PARAMS = View.class.getMethod("setBackgroundGradientBlurParams", float[].class, Integer.TYPE);
                }
                METHOD_SET_BG_GRADIENT_BLUR_PARAMS.invoke(view, fArr, Integer.valueOf(i));
                return true;
            } catch (Exception unused) {
                METHOD_SET_BG_GRADIENT_BLUR_PARAMS = null;
                return false;
            }
        } catch (Exception unused2) {
            METHOD_SET_BG_BLUR_TYPE = null;
            return false;
        }
    }

    public static boolean setBackgroundGradientBlurParams(View view, float[] fArr, int i) {
        if (!HyperMaterialUtils.isEnable() || !HyperMaterialUtils.isDefaultFeatureEnable()) {
            return false;
        }
        try {
            if (METHOD_SET_BG_GRADIENT_BLUR_PARAMS == null) {
                METHOD_SET_BG_GRADIENT_BLUR_PARAMS = View.class.getMethod("setBackgroundGradientBlurParams", float[].class, Integer.TYPE);
            }
            METHOD_SET_BG_GRADIENT_BLUR_PARAMS.invoke(view, fArr, Integer.valueOf(i));
            return true;
        } catch (Exception unused) {
            METHOD_SET_BG_GRADIENT_BLUR_PARAMS = null;
            return false;
        }
    }

    public static boolean clearBackgroundGradientBlur(View view) {
        if (!HyperMaterialUtils.isEnable() || !HyperMaterialUtils.isDefaultFeatureEnable()) {
            return false;
        }
        try {
            if (METHOD_SET_BG_BLUR_TYPE == null) {
                METHOD_SET_BG_BLUR_TYPE = View.class.getMethod("setMiBackgroundBlurType", Integer.TYPE);
            }
            METHOD_SET_BG_BLUR_TYPE.invoke(view, 0);
            return true;
        } catch (Exception unused) {
            METHOD_SET_BG_BLUR_TYPE = null;
            return false;
        }
    }

    public static ArrayList<Point> wrapBlendConfig(int[] iArr, int[] iArr2) {
        if (iArr == null || iArr2 == null) {
            return null;
        }
        if (iArr.length != iArr2.length) {
            Log.w("MiuixBlur", String.format("warning!! colorInts(%s) and blendModes(%s) size not match. %s", Integer.valueOf(iArr.length), Integer.valueOf(iArr2.length), Log.getStackTraceString(new Throwable())));
        }
        int iMin = Math.min(iArr.length, iArr2.length);
        ArrayList<Point> arrayList = new ArrayList<>();
        for (int i = 0; i < iMin; i++) {
            arrayList.add(new Point(iArr[i], iArr2[i]));
        }
        return arrayList;
    }

    public static boolean clearBackgroundBlendConfig(View view) {
        if (!HyperMaterialUtils.isEnable()) {
            return false;
        }
        try {
            if (METHOD_CLEAR_BG_BLEND_COLOR == null) {
                METHOD_CLEAR_BG_BLEND_COLOR = View.class.getMethod("clearMiBackgroundBlendColor", new Class[0]);
            }
            METHOD_CLEAR_BG_BLEND_COLOR.invoke(view, new Object[0]);
            return true;
        } catch (Exception unused) {
            METHOD_CLEAR_BG_BLEND_COLOR = null;
            return false;
        }
    }

    public static boolean disableBackgroundContainBelow(View view, boolean z) {
        if (view == null || !HyperMaterialUtils.isEnable()) {
            return false;
        }
        try {
            if (METHOD_DISABLE_BG_CONTAIN_BELOW == null) {
                METHOD_DISABLE_BG_CONTAIN_BELOW = View.class.getMethod("disableMiBackgroundContainBelow", Boolean.TYPE);
            }
            METHOD_DISABLE_BG_CONTAIN_BELOW.invoke(view, Boolean.valueOf(z));
            return true;
        } catch (Exception unused) {
            METHOD_DISABLE_BG_CONTAIN_BELOW = null;
            return false;
        }
    }

    public static boolean chooseBackgroundBlurContainer(View view, View view2) {
        if (view == null || !HyperMaterialUtils.isEnable()) {
            return false;
        }
        try {
            if (METHOD_CHOOSE_BG_BLUR_CONTAINER == null) {
                METHOD_CHOOSE_BG_BLUR_CONTAINER = View.class.getMethod("chooseBackgroundBlurContainer", View.class);
            }
            METHOD_CHOOSE_BG_BLUR_CONTAINER.invoke(view, view2);
            return true;
        } catch (Exception unused) {
            METHOD_CHOOSE_BG_BLUR_CONTAINER = null;
            return false;
        }
    }

    public static boolean clearBackgroundBlurContainer(View view) {
        if (view == null || !HyperMaterialUtils.isEnable()) {
            return false;
        }
        try {
            if (METHOD_CHOOSE_BG_BLUR_CONTAINER == null) {
                METHOD_CHOOSE_BG_BLUR_CONTAINER = View.class.getMethod("chooseBackgroundBlurContainer", View.class);
            }
            METHOD_CHOOSE_BG_BLUR_CONTAINER.invoke(view, null);
            return true;
        } catch (Exception unused) {
            METHOD_CHOOSE_BG_BLUR_CONTAINER = null;
            return false;
        }
    }

    public static boolean setBackgroundBlurScaleRatio(View view, float f) {
        if (!HyperMaterialUtils.isEnable()) {
            return false;
        }
        float fMax = Math.max(0.0f, Math.min(1.0f, f));
        try {
            if (METHOD_SET_BG_BLUR_SCALE_RATIO == null) {
                METHOD_SET_BG_BLUR_SCALE_RATIO = View.class.getMethod("setMiBackgroundBlurScaleRatio", Float.TYPE);
            }
            METHOD_SET_BG_BLUR_SCALE_RATIO.invoke(view, Float.valueOf(fMax));
            return true;
        } catch (Exception unused) {
            METHOD_SET_BG_BLUR_SCALE_RATIO = null;
            return false;
        }
    }

    public static boolean setPassTextureScale(View view, float f) {
        if (!HyperMaterialUtils.isEnable()) {
            return false;
        }
        float fMax = Math.max(0.0f, Math.min(1.0f, f));
        try {
            if (METHOD_SET_PASS_TEXTURE_SCALE == null) {
                METHOD_SET_PASS_TEXTURE_SCALE = View.class.getMethod("setPassTextureScale", Float.TYPE);
            }
            METHOD_SET_PASS_TEXTURE_SCALE.invoke(view, Float.valueOf(fMax));
            return true;
        } catch (Exception unused) {
            METHOD_SET_PASS_TEXTURE_SCALE = null;
            return false;
        }
    }

    public static boolean setBackgroundBlurEnhanceFlag(View view, int i) {
        return setBackgroundBlurEnhanceFlag(view, 1, i);
    }

    public static boolean clearBackgroundBlurEnhanceFlag(View view, int i) {
        return setBackgroundBlurEnhanceFlag(view, 0, i);
    }

    public static boolean setBackgroundBlurEnhanceFlag(View view, int i, int i2) {
        if (view == null || !HyperMaterialUtils.isEnable()) {
            return false;
        }
        try {
            if (METHOD_SET_BG_BLUR_ENHANCE_FLAG == null) {
                METHOD_SET_BG_BLUR_ENHANCE_FLAG = View.class.getMethod("setMiBackgroundBlurEnhanceFlag", Integer.TYPE, Integer.TYPE);
            }
            METHOD_SET_BG_BLUR_ENHANCE_FLAG.invoke(view, Integer.valueOf(i), Integer.valueOf(i2));
            return true;
        } catch (Exception unused) {
            METHOD_SET_BG_BLUR_ENHANCE_FLAG = null;
            return false;
        }
    }

    public static boolean setMiSelfBlurEnhanceFlag(View view, int i, int i2) {
        if (view == null || !HyperMaterialUtils.isEnable()) {
            return false;
        }
        try {
            if (METHOD_SET_SELF_BLUR_ENHANCE_FLAG == null) {
                METHOD_SET_SELF_BLUR_ENHANCE_FLAG = View.class.getMethod("setMiSelfBlurEnhanceFlag", Integer.TYPE, Integer.TYPE);
            }
            METHOD_SET_SELF_BLUR_ENHANCE_FLAG.invoke(view, Integer.valueOf(i), Integer.valueOf(i2));
            return true;
        } catch (Exception unused) {
            METHOD_SET_SELF_BLUR_ENHANCE_FLAG = null;
            return false;
        }
    }

    public static boolean setMixEffectEnabled(View view, boolean z) {
        if (!HyperMaterialUtils.isEnable()) {
            return false;
        }
        try {
            if (METHOD_SET_MIX_EFFECT_ENABLED == null) {
                METHOD_SET_MIX_EFFECT_ENABLED = View.class.getMethod("setMixEffectEnabled", Boolean.TYPE);
            }
            METHOD_SET_MIX_EFFECT_ENABLED.invoke(view, Boolean.valueOf(z));
            return true;
        } catch (Exception unused) {
            METHOD_SET_MIX_EFFECT_ENABLED = null;
            return false;
        }
    }

    public static boolean getMixEffectEnabled(View view) {
        if (!HyperMaterialUtils.isEnable()) {
            return false;
        }
        try {
            if (METHOD_GET_MIX_EFFECT_ENABLED == null) {
                METHOD_GET_MIX_EFFECT_ENABLED = View.class.getMethod("getMixEffectEnabled", new Class[0]);
            }
            Object objInvoke = METHOD_GET_MIX_EFFECT_ENABLED.invoke(view, new Object[0]);
            if (objInvoke instanceof Boolean) {
                return ((Boolean) objInvoke).booleanValue();
            }
            return false;
        } catch (Exception unused) {
            METHOD_GET_MIX_EFFECT_ENABLED = null;
            return false;
        }
    }

    public static boolean setSelfBlur(View view, int i, ArrayList<Point> arrayList) {
        if (!HyperMaterialUtils.isEnable()) {
            return false;
        }
        try {
            if (METHOD_SET_SELF_BLUR == null) {
                METHOD_SET_SELF_BLUR = View.class.getMethod("setMiSelfBlur", Integer.TYPE, ArrayList.class);
            }
            METHOD_SET_SELF_BLUR.invoke(view, Integer.valueOf(i), arrayList);
            return true;
        } catch (Exception unused) {
            METHOD_SET_SELF_BLUR = null;
            return false;
        }
    }

    public static boolean setSelfGradientBlurParams(View view, float[] fArr, int i) {
        if (!HyperMaterialUtils.isEnable()) {
            return false;
        }
        try {
            if (METHOD_SET_SELF_GRADIENT_BLUR_PARAMS == null) {
                METHOD_SET_SELF_GRADIENT_BLUR_PARAMS = View.class.getMethod("setSelfGradientBlurParams", float[].class, Integer.TYPE);
            }
            METHOD_SET_SELF_GRADIENT_BLUR_PARAMS.invoke(view, fArr, Integer.valueOf(i));
            return true;
        } catch (Exception unused) {
            METHOD_SET_SELF_GRADIENT_BLUR_PARAMS = null;
            return false;
        }
    }

    public static boolean setSelfBlurType(View view, int i) {
        if (!HyperMaterialUtils.isEnable()) {
            return false;
        }
        try {
            if (METHOD_SET_SELF_BLUR_TYPE == null) {
                METHOD_SET_SELF_BLUR_TYPE = View.class.getMethod("setMiSelfBlurType", Integer.TYPE);
            }
            METHOD_SET_SELF_BLUR_TYPE.invoke(view, Integer.valueOf(i));
            return true;
        } catch (Exception unused) {
            METHOD_SET_SELF_BLUR_TYPE = null;
            return false;
        }
    }

    public static boolean getSelfBlurType(View view) {
        if (!HyperMaterialUtils.isEnable()) {
            return false;
        }
        try {
            if (METHOD_GET_SELF_BLUR_TYPE == null) {
                METHOD_GET_SELF_BLUR_TYPE = View.class.getMethod("getMiSelfBlurType", new Class[0]);
            }
            Object objInvoke = METHOD_GET_SELF_BLUR_TYPE.invoke(view, new Object[0]);
            if (objInvoke instanceof Boolean) {
                return ((Boolean) objInvoke).booleanValue();
            }
            return false;
        } catch (Exception unused) {
            METHOD_GET_SELF_BLUR_TYPE = null;
            return false;
        }
    }

    public static boolean setPassWindowBlurEnabled(View view, boolean z) {
        if (!HyperMaterialUtils.isEnable()) {
            return false;
        }
        try {
            if (METHOD_SET_PASS_WINDOW_BLUR_MODE == null) {
                METHOD_SET_PASS_WINDOW_BLUR_MODE = View.class.getMethod("setPassWindowBlurEnabled", Boolean.TYPE);
            }
            METHOD_SET_PASS_WINDOW_BLUR_MODE.invoke(view, Boolean.valueOf(z));
            return true;
        } catch (Exception unused) {
            METHOD_SET_PASS_WINDOW_BLUR_MODE = null;
            return false;
        }
    }

    public static boolean getPassWindowBlurEnabled(View view) {
        if (!HyperMaterialUtils.isEnable()) {
            return false;
        }
        try {
            if (METHOD_GET_PASS_WINDOW_BLUR_MODE == null) {
                METHOD_GET_PASS_WINDOW_BLUR_MODE = View.class.getMethod("getPassWindowBlurEnabled", new Class[0]);
            }
            Object objInvoke = METHOD_GET_PASS_WINDOW_BLUR_MODE.invoke(view, new Object[0]);
            if (objInvoke instanceof Boolean) {
                return ((Boolean) objInvoke).booleanValue();
            }
        } catch (Exception unused) {
            METHOD_GET_PASS_WINDOW_BLUR_MODE = null;
        }
        return false;
    }
}
