package miuix.graphics.shadow;

import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import miuix.core.util.MaterialConfig;

/* JADX INFO: loaded from: classes2.dex */
public class DropShadowConfig {
    float blurRadiusDp;
    BlurMaskFilter.Blur blurStyle;
    boolean clipShadowEnable;
    float dispersion;
    float offsetXDp;
    float offsetYDp;
    int shadowColor;
    int shadowDarkColor;

    DropShadowConfig(float f) {
        this(f, BlurMaskFilter.Blur.NORMAL);
    }

    DropShadowConfig(float f, BlurMaskFilter.Blur blur) {
        this(Color.parseColor("#0D000000"), Color.parseColor("#0DFFFFFF"), 0.0f, 0.0f, f, 1.0f, blur);
    }

    DropShadowConfig(int i, int i2, float f, float f2, float f3, float f4, BlurMaskFilter.Blur blur) {
        this.clipShadowEnable = true;
        this.shadowColor = i;
        this.shadowDarkColor = i2;
        this.offsetXDp = f;
        this.offsetYDp = f2;
        this.blurRadiusDp = f3;
        this.dispersion = f4;
        this.blurStyle = blur;
    }

    public static class Builder {
        private final DropShadowConfig dropShadowConfig;

        public Builder(float f) {
            this.dropShadowConfig = new DropShadowConfig(f);
        }

        public Builder(MaterialConfig.ShadowConfig shadowConfig) {
            this.dropShadowConfig = new DropShadowConfig(shadowConfig.shadowColor, shadowConfig.shadowColor, shadowConfig.shadowOffsetX, shadowConfig.shadowOffsetY, shadowConfig.shadowRadius, shadowConfig.shadowDispersion, BlurMaskFilter.Blur.NORMAL);
        }

        public Builder setBlurRadius(float f) {
            this.dropShadowConfig.blurRadiusDp = f;
            return this;
        }

        public Builder setColor(int i, int i2) {
            this.dropShadowConfig.shadowColor = i;
            this.dropShadowConfig.shadowDarkColor = i2;
            return this;
        }

        public Builder setStyle(BlurMaskFilter.Blur blur) {
            this.dropShadowConfig.blurStyle = blur;
            return this;
        }

        public Builder setOffsetXDp(int i) {
            this.dropShadowConfig.offsetXDp = i;
            return this;
        }

        public Builder setOffsetYDp(int i) {
            this.dropShadowConfig.offsetYDp = i;
            return this;
        }

        public Builder setDispersion(float f) {
            this.dropShadowConfig.dispersion = f;
            return this;
        }

        public Builder setClipShadowEnable(boolean z) {
            this.dropShadowConfig.clipShadowEnable = z;
            return this;
        }

        public DropShadowConfig create() {
            return this.dropShadowConfig;
        }
    }
}
