package miuix.view;

import android.content.res.Configuration;
import android.util.DisplayMetrics;

/* JADX INFO: loaded from: classes3.dex */
public class DisplayConfig {
    public int defaultBitmapDensity;
    public float density;
    public int densityDpi;
    public float fontScale;
    public float scaledDensity;
    public int windowHeightDp;
    public int windowWidthDp;

    public DisplayConfig(DisplayMetrics displayMetrics) {
        this.defaultBitmapDensity = displayMetrics.densityDpi;
        this.densityDpi = displayMetrics.densityDpi;
        this.density = displayMetrics.density;
        float f = displayMetrics.scaledDensity;
        this.scaledDensity = f;
        this.fontScale = f / this.density;
        this.windowWidthDp = (int) ((displayMetrics.widthPixels / this.density) + 0.5f);
        this.windowHeightDp = (int) ((displayMetrics.heightPixels / this.density) + 0.5f);
    }

    public DisplayConfig(Configuration configuration) {
        this.windowWidthDp = configuration.screenWidthDp;
        this.windowHeightDp = configuration.screenHeightDp;
        this.defaultBitmapDensity = configuration.densityDpi;
        int i = configuration.densityDpi;
        this.densityDpi = i;
        this.density = i * 0.00625f;
        float f = configuration.fontScale;
        this.fontScale = f;
        this.scaledDensity = this.density * (f == 0.0f ? 1.0f : f);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof DisplayConfig)) {
            return false;
        }
        DisplayConfig displayConfig = (DisplayConfig) obj;
        return Float.compare(this.density, displayConfig.density) == 0 && Float.compare(this.scaledDensity, displayConfig.scaledDensity) == 0 && Float.compare(this.fontScale, displayConfig.fontScale) == 0 && this.densityDpi == displayConfig.densityDpi && this.defaultBitmapDensity == displayConfig.defaultBitmapDensity;
    }

    public int hashCode() {
        return super.hashCode();
    }

    public String toString() {
        return "{ densityDpi:" + this.densityDpi + ", density:" + this.density + ", windowWidthDp:" + this.windowWidthDp + ", windowHeightDp: " + this.windowHeightDp + ", scaledDensity:" + this.scaledDensity + ", fontScale: " + this.fontScale + ", defaultBitmapDensity:" + this.defaultBitmapDensity + "}";
    }
}
