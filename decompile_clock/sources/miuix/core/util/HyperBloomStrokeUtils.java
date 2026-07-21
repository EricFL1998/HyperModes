package miuix.core.util;

import android.view.View;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes2.dex */
public class HyperBloomStrokeUtils {
    public static float[] EMPTY = {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
    public static Method METHOD_SET_BLOOM_STROKE;

    public static boolean clearBloomStroke(View view) {
        if (view == null) {
            return false;
        }
        return setBloomStroke(view, EMPTY);
    }

    public static boolean setBloomStrokeConfig(View view, MaterialConfig.BloomStrokeConfig bloomStrokeConfig) {
        if (bloomStrokeConfig == null) {
            return clearBloomStroke(view);
        }
        return setBloomStrokeWithDp(view, bloomStrokeConfig.bloomStrokeWidth, bloomStrokeConfig.bloomStrokeGradientDegree, bloomStrokeConfig.bloomStrokeColorR, bloomStrokeConfig.bloomStrokeColorG, bloomStrokeConfig.bloomStrokeColorB, bloomStrokeConfig.bloomStrokeColorA, bloomStrokeConfig.normalWidth, bloomStrokeConfig.source1X, bloomStrokeConfig.source1Y, bloomStrokeConfig.source1Z, bloomStrokeConfig.source1R, bloomStrokeConfig.source1G, bloomStrokeConfig.source1B, bloomStrokeConfig.source1A, bloomStrokeConfig.source2X, bloomStrokeConfig.source2Y, bloomStrokeConfig.source2Z, bloomStrokeConfig.source2R, bloomStrokeConfig.source2G, bloomStrokeConfig.source2B, bloomStrokeConfig.source2A);
    }

    public static boolean setBloomStrokeWithDp(View view, float[] fArr) {
        if (view == null || !HyperMaterialUtils.isEnable()) {
            return false;
        }
        float[] fArr2 = new float[fArr.length];
        System.arraycopy(fArr, 0, fArr2, 0, fArr.length);
        float f = view.getResources().getDisplayMetrics().density;
        fArr2[0] = (int) ((fArr[0] * f) + 0.5f);
        fArr2[6] = (int) ((fArr[6] * f) + 0.5f);
        return setBloomStroke(view, fArr2);
    }

    public static boolean setBloomStrokeWithDp(View view, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, float f10, float f11, float f12, float f13, float f14, float f15, float f16, float f17, float f18, float f19, float f20, float f21) {
        if (view == null || !HyperMaterialUtils.isEnable()) {
            return false;
        }
        float f22 = view.getResources().getDisplayMetrics().density;
        return setBloomStroke(view, new float[]{(f * f22) + 0.5f, f2, f3, f4, f5, f6, (f22 * f7) + 0.5f, f8, f9, f10, f11, f12, f13, f14, f15, f16, f17, f18, f19, f20, f21});
    }

    public static boolean setBloomStroke(View view, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, float f10, float f11, float f12, float f13, float f14, float f15, float f16, float f17, float f18, float f19, float f20, float f21) {
        return setBloomStroke(view, new float[]{Math.max(0.0f, f), Math.max(0.0f, Math.min(360.0f, f2)), f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15, f16, f17, f18, f19, f20, f21});
    }

    public static boolean setBloomStroke(View view, float[] fArr) {
        if (view == null || !HyperMaterialUtils.isEnable()) {
            return false;
        }
        try {
            if (METHOD_SET_BLOOM_STROKE == null) {
                METHOD_SET_BLOOM_STROKE = View.class.getMethod("setMiBloomStroke", float[].class);
            }
            METHOD_SET_BLOOM_STROKE.invoke(view, fArr);
            return true;
        } catch (Exception unused) {
            METHOD_SET_BLOOM_STROKE = null;
            return false;
        }
    }
}
