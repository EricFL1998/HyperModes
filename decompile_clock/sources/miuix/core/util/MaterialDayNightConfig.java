package miuix.core.util;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes2.dex */
public class MaterialDayNightConfig {
    public MaterialConfig darkConfig;
    public MaterialConfig defaultConfig;

    public MaterialDayNightConfig(Parcel parcel) {
        if (parcel == null) {
            this.defaultConfig = null;
            this.darkConfig = null;
            return;
        }
        int i = parcel.readInt();
        if (i < 1) {
            this.defaultConfig = null;
            this.darkConfig = null;
            return;
        }
        this.defaultConfig = new MaterialConfig(parcel);
        if (i > 1) {
            this.darkConfig = new MaterialConfig(parcel);
        } else {
            this.darkConfig = null;
        }
    }

    public MaterialDayNightConfig(MaterialConfig materialConfig) {
        this.defaultConfig = materialConfig;
    }

    public MaterialDayNightConfig(MaterialConfig materialConfig, MaterialConfig materialConfig2) {
        this.defaultConfig = materialConfig;
        this.darkConfig = materialConfig2;
    }

    public MaterialConfig get(boolean z) {
        MaterialConfig materialConfig = this.darkConfig;
        if (materialConfig == null) {
            return this.defaultConfig;
        }
        return z ? this.defaultConfig : materialConfig;
    }

    public MaterialConfig.ColorBlendConfig getDefaultColorBlendConfig() {
        MaterialConfig materialConfig = get(true);
        if (materialConfig == null) {
            return null;
        }
        return materialConfig.getColorBlendConfig();
    }

    public MaterialConfig.ColorBlendConfig getColorBlendConfig(boolean z) {
        MaterialConfig materialConfig = get(z);
        if (materialConfig == null) {
            return null;
        }
        return materialConfig.getColorBlendConfig();
    }

    public MaterialConfig.BlurConfig getDefaultBlurConfig() {
        MaterialConfig materialConfig = get(true);
        if (materialConfig == null) {
            return null;
        }
        return materialConfig.getBlurConfig();
    }

    public MaterialConfig.BlurConfig getBlurConfig(boolean z) {
        MaterialConfig materialConfig = get(z);
        if (materialConfig == null) {
            return null;
        }
        return materialConfig.getBlurConfig();
    }

    public MaterialConfig.ShadowConfig getDefaultShadowConfig() {
        MaterialConfig materialConfig = get(true);
        if (materialConfig == null) {
            return null;
        }
        return materialConfig.getShadowConfig();
    }

    public MaterialConfig.ShadowConfig getShadowConfig(boolean z) {
        MaterialConfig materialConfig = get(z);
        if (materialConfig == null) {
            return null;
        }
        return materialConfig.getShadowConfig();
    }

    public MaterialConfig.BloomStrokeConfig getDefaultBloomStrokeConfig() {
        MaterialConfig materialConfig = get(true);
        if (materialConfig == null) {
            return null;
        }
        return materialConfig.getBloomStrokeConfig();
    }

    public MaterialConfig.BloomStrokeConfig getBloomStrokeConfig(boolean z) {
        MaterialConfig materialConfig = get(z);
        if (materialConfig == null) {
            return null;
        }
        return materialConfig.getBloomStrokeConfig();
    }

    public static MaterialDayNightConfig create(Parcelable parcelable) {
        if (parcelable == null) {
            return null;
        }
        Parcel parcelObtain = Parcel.obtain();
        parcelObtain.setDataPosition(0);
        try {
            parcelable.writeToParcel(parcelObtain, 0);
            parcelObtain.setDataPosition(0);
            MaterialDayNightConfig materialDayNightConfig = new MaterialDayNightConfig(parcelObtain);
            parcelObtain.recycle();
            return materialDayNightConfig;
        } catch (Exception unused) {
            parcelObtain.recycle();
            return null;
        }
    }
}
