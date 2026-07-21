package miuix.flexible.grid.strategy;

import java.util.ArrayList;
import miuix.flexible.grid.HyperGridConfiguration;

/* JADX INFO: loaded from: classes2.dex */
public class DynamicColumnFixedSpacingFullGridStrategy {
    /* JADX WARN: Code duplicated, block: B:21:0x004c A[PHI: r1
  0x004c: PHI (r1v1 int) = (r1v0 int), (r1v3 int) binds: [B:3:0x0005, B:19:0x0049] A[DONT_GENERATE, DONT_INLINE]] */
    public static HyperGridConfiguration getConfiguration(float f, float f2, float f3, float f4, int i) {
        HyperGridConfiguration hyperGridConfigurationObtain = HyperGridConfiguration.obtain();
        int iIntValue = 1;
        if (i > 1) {
            ArrayList arrayList = new ArrayList(16);
            for (int i2 = 1; i2 <= i; i2++) {
                if (i % i2 == 0) {
                    arrayList.add(Integer.valueOf(i2));
                }
            }
            int i3 = (int) ((f + f2) / (f3 + f2));
            int i4 = 0;
            while (true) {
                if (i4 >= arrayList.size()) {
                    iIntValue = i3;
                    break;
                }
                if (((Integer) arrayList.get(i4)).intValue() > i3) {
                    if (i4 <= 0) {
                        break;
                    }
                    iIntValue = ((Integer) arrayList.get(i4 - 1)).intValue();
                    break;
                }
                i4++;
            }
            if (iIntValue <= i) {
                i = iIntValue;
            }
        } else {
            i = iIntValue;
        }
        hyperGridConfigurationObtain.cellWidth = ((f + f2) / i) - f2;
        hyperGridConfigurationObtain.cellWidth = Math.min(f4, hyperGridConfigurationObtain.cellWidth);
        hyperGridConfigurationObtain.columnCount = i;
        hyperGridConfigurationObtain.columnSpacing = f2;
        return hyperGridConfigurationObtain;
    }
}
