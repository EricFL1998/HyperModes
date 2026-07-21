package miuix.internal.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

/* JADX INFO: loaded from: classes2.dex */
@Deprecated
public final class DisplayHelper {
    private static final String TAG = "DisplayHelper";
    private float mDensity;
    private int mDensityDpi;
    private DisplayMetrics mDisplayMetrics;
    private int mHeightDps;
    private int mHeightPixels;
    private int mWidthDps;
    private int mWidthPixels;

    public DisplayHelper(Context context) {
        getAndroidScreenProperty(context);
    }

    public DisplayMetrics getDm() {
        return this.mDisplayMetrics;
    }

    private void getAndroidScreenProperty(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService("window");
        this.mDisplayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(this.mDisplayMetrics);
        this.mWidthPixels = this.mDisplayMetrics.widthPixels;
        this.mHeightPixels = this.mDisplayMetrics.heightPixels;
        this.mDensity = this.mDisplayMetrics.density;
        this.mDensityDpi = this.mDisplayMetrics.densityDpi;
        float f = this.mWidthPixels;
        float f2 = this.mDensity;
        this.mWidthDps = (int) (f / f2);
        this.mHeightDps = (int) (this.mHeightPixels / f2);
    }

    public int getWidthPixels() {
        return this.mWidthPixels;
    }

    public int getHeightPixels() {
        return this.mHeightPixels;
    }

    public float getDensity() {
        return this.mDensity;
    }

    public int getDensityDpi() {
        return this.mDensityDpi;
    }

    public int getWidthDps() {
        return this.mWidthDps;
    }

    public int getHeightDps() {
        return this.mHeightDps;
    }

    public void info() {
        Log.d(TAG, "屏幕宽度（像素）：" + this.mWidthPixels);
        Log.d(TAG, "屏幕高度（像素）：" + this.mHeightPixels);
        Log.d(TAG, "屏幕密度：" + this.mDensity);
        Log.d(TAG, "屏幕密度（dpi）：" + this.mDensityDpi);
        Log.d(TAG, "屏幕宽度（dp）：" + this.mWidthDps);
        Log.d(TAG, "屏幕高度（dp）：" + this.mHeightDps);
    }
}
