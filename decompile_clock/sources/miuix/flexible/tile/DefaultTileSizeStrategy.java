package miuix.flexible.tile;

import android.util.SparseIntArray;

/* JADX INFO: loaded from: classes2.dex */
public class DefaultTileSizeStrategy {
    private static final SparseIntArray[] SIZE_DATA3 = new SparseIntArray[5];

    private DefaultTileSizeStrategy() {
    }

    static {
        int i = 0;
        while (true) {
            SparseIntArray[] sparseIntArrayArr = SIZE_DATA3;
            if (i < sparseIntArrayArr.length) {
                sparseIntArrayArr[i] = new SparseIntArray();
                i++;
            } else {
                sparseIntArrayArr[0].put(0, makeSize(3, 2));
                sparseIntArrayArr[1].put(0, makeSize(2, 1));
                sparseIntArrayArr[3].put(3, makeSize(3, 2));
                sparseIntArrayArr[4].put(3, makeSize(2, 1));
                return;
            }
        }
    }

    private static int makeSize(int i, int i2) {
        return (int) TileCache.makeItemSpec(0, 0, i, i2);
    }

    public static int[] getTileSize(int i, int i2, int i3) {
        int[] iArr = new int[2];
        if (i2 == 1 || i3 <= i2) {
            iArr[0] = 1;
            iArr[1] = 1;
        } else if (i2 == 2) {
            getTileSize2(iArr, i, i3);
        } else if (i2 == 3) {
            getTileSize3(iArr, i, i3);
        } else {
            getTileSizeNormal(iArr, i, i2);
        }
        return iArr;
    }

    private static void getTileSize2(int[] iArr, int i, int i2) {
        if (i2 % 2 == 1 && i == i2 - 1) {
            iArr[0] = 2;
        } else {
            iArr[0] = 1;
        }
        iArr[1] = 1;
    }

    private static void getTileSize3(int[] iArr, int i, int i2) {
        int i3 = i2 % 6;
        if (i3 != 0 && i2 - i <= i3) {
            int i4 = SIZE_DATA3[i3 - 1].get(i % 6, -1);
            if (i4 != -1) {
                long j = i4;
                iArr[0] = TileCache.getWidthFromSpec(j);
                iArr[1] = TileCache.getHeightFromSpec(j);
                return;
            } else {
                iArr[0] = 1;
                iArr[1] = 1;
                return;
            }
        }
        int i5 = i % 12;
        boolean z = i5 == 4 || i5 == 9;
        iArr[0] = z ? 2 : 1;
        iArr[1] = z ? 2 : 1;
    }

    private static void getTileSizeNormal(int[] iArr, int i, int i2) {
        int i3 = i % ((i2 * 4) - 6);
        boolean z = i3 == 0 || i3 == (i2 * 3) + (-5);
        iArr[0] = z ? 2 : 1;
        iArr[1] = z ? 2 : 1;
    }
}
