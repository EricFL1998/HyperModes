package miuix.view;

import miuix.core.util.MaterialConfig;
import miuix.core.util.MaterialDayNightConfig;

/* JADX INFO: loaded from: classes3.dex */
public interface HyperMaterialWidget {
    default MaterialConfig getCurrentMaterial() {
        return null;
    }

    default MaterialDayNightConfig getMaterial() {
        return null;
    }

    default void setMaterial(MaterialConfig materialConfig) {
    }

    default void setMaterial(MaterialDayNightConfig materialDayNightConfig) {
    }

    default void updateMaterialEffect() {
    }
}
