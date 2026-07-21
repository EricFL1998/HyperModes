package miuix.flexible.grid.strategy;

import miuix.flexible.grid.HyperGridConfiguration;

/* JADX INFO: loaded from: classes2.dex */
public class DynamicColumnFixedCellWidthGridStrategy {
    public static HyperGridConfiguration getConfiguration(float f, float f2, float f3, float f4, int i) {
        HyperGridConfiguration hyperGridConfigurationObtain = HyperGridConfiguration.obtain();
        int i2 = (int) ((f + f2) / (f2 + f4));
        hyperGridConfigurationObtain.cellWidth = f4;
        hyperGridConfigurationObtain.columnCount = i2;
        hyperGridConfigurationObtain.columnSpacing = i2 == 1 ? 0.0f : (f - (i2 * f4)) / (i2 - 1);
        hyperGridConfigurationObtain.columnSpacing = Math.min(f3, hyperGridConfigurationObtain.columnSpacing);
        return hyperGridConfigurationObtain;
    }
}
