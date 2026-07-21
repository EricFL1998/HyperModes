package miuix.flexible.grid.strategy;

import miuix.flexible.grid.HyperGridConfiguration;

/* JADX INFO: loaded from: classes2.dex */
public class FixedColumnFixedSpacingGridStrategy {
    public static HyperGridConfiguration getConfiguration(float f, int i, float f2) {
        HyperGridConfiguration hyperGridConfigurationObtain = HyperGridConfiguration.obtain();
        if (i < 1) {
            throw new IllegalArgumentException("Column count must be greater than 0!");
        }
        if (i == 1) {
            hyperGridConfigurationObtain.cellWidth = f;
        } else {
            hyperGridConfigurationObtain.cellWidth = ((f + f2) / i) - f2;
        }
        hyperGridConfigurationObtain.columnCount = i;
        hyperGridConfigurationObtain.columnSpacing = f2;
        return hyperGridConfigurationObtain;
    }
}
