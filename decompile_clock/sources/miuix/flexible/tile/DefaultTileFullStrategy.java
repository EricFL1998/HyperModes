package miuix.flexible.tile;

import android.util.SparseIntArray;

/* JADX INFO: loaded from: classes2.dex */
public class DefaultTileFullStrategy implements ITileFullStrategy {
    protected int mColumnCount;
    protected int mResizePosition;
    protected final SparseIntArray mSizeData = new SparseIntArray();

    @Override // miuix.flexible.tile.ITileFullStrategy
    public boolean afterUpdateTileCache(int i, int i2) {
        return false;
    }

    protected static int makeSize(int i, int i2) {
        return (int) TileCache.makeItemSpec(0, 0, i, i2);
    }

    @Override // miuix.flexible.tile.ITileFullStrategy
    public void beforeUpdateTileCache(int i, int i2) {
        this.mColumnCount = i;
        this.mSizeData.clear();
        if (i2 <= i) {
            this.mResizePosition = 0;
            return;
        }
        int i3 = i * 2;
        int i4 = i3 - 3;
        int i5 = i2 % i4;
        if (i5 == 0) {
            this.mResizePosition = i2;
            return;
        }
        this.mResizePosition = i2 - i5;
        int i6 = i5 % i;
        if (i6 == 0) {
            return;
        }
        if (i6 < Math.ceil(i / 2.0f)) {
            this.mResizePosition -= i4;
            i5 += i4;
        }
        if (this.mResizePosition < 0) {
            this.mResizePosition = 1;
            i5 -= i3 - 2;
            i -= 2;
        }
        int i7 = i5 % i;
        if (i7 == 0) {
            return;
        }
        int i8 = i - i7;
        int i9 = i2 - 1;
        int i10 = 0;
        while (i8 > 0) {
            if ((i10 + 1) % i == 0) {
                i9--;
                i10 = 0;
            }
            this.mSizeData.put(i9, makeSize(2, 1));
            i8--;
            i9--;
            i10 += 2;
        }
    }

    @Override // miuix.flexible.tile.ITileFullStrategy
    public boolean isResized(int i) {
        return this.mColumnCount > 3 && i >= this.mResizePosition;
    }

    @Override // miuix.flexible.tile.ITileFullStrategy
    public int[] getTileSize(int i) {
        int[] iArr = new int[2];
        int i2 = this.mSizeData.get(i, -1);
        if (i2 != -1) {
            long j = i2;
            iArr[0] = TileCache.getWidthFromSpec(j);
            iArr[1] = TileCache.getHeightFromSpec(j);
        } else {
            iArr[0] = 1;
            iArr[1] = 1;
        }
        return iArr;
    }
}
