package miuix.flexible.grid.strategy;

import miuix.flexible.grid.HyperGridConfiguration;

/* JADX INFO: loaded from: classes2.dex */
public class DynamicColumnFixedSpacingGridStrategy {
    public static HyperGridConfiguration getConfiguration(float f, float f2, float f3, float f4, int i) {
        HyperGridConfiguration hyperGridConfigurationObtain = HyperGridConfiguration.obtain();
        float f5 = f + f2;
        int i2 = (int) (f5 / (f3 + f2));
        hyperGridConfigurationObtain.cellWidth = (f5 / i2) - f2;
        hyperGridConfigurationObtain.cellWidth = Math.min(f4, hyperGridConfigurationObtain.cellWidth);
        hyperGridConfigurationObtain.columnCount = i2;
        hyperGridConfigurationObtain.columnSpacing = f2;
        return hyperGridConfigurationObtain;
    }
}
