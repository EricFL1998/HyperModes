package miuix.flexible.tile;

import android.os.SystemClock;
import android.util.Log;

/* JADX INFO: loaded from: classes2.dex */
public class TileCache {
    public static final long MASK_HEIGHT = 1023;
    public static final long MASK_WIDTH = 1023;
    public static final int MASK_WIDTH_OFFSET = 10;
    public static final long MASK_X = 4194303;
    public static final int MASK_X_OFFSET = 42;
    public static final long MASK_Y = 4194303;
    public static final int MASK_Y_OFFSET = 20;
    public static final String TAG = "TileCache";
    private long[] mItemCache;
    private final TileParamsGetter mParamsGetter;
    private TileBitmap mTileBitmap;
    private int mTotalHeight;

    public interface TileParamsGetter {
        int getColumnCount();

        int getItemCount();

        int[] getTileSize(int i);
    }

    public static int getHeightFromSpec(long j) {
        return (int) (j & 1023);
    }

    public static int getWidthFromSpec(long j) {
        return (int) ((j >> 10) & 1023);
    }

    public static int getXFromSpec(long j) {
        return (int) ((j >> 42) & 4194303);
    }

    public static int getYFromSpec(long j) {
        return (int) ((j >> 20) & 4194303);
    }

    public static long makeItemSpec(int i, int i2, int i3, int i4) {
        return ((((long) i2) & 4194303) << 20) | ((((long) i) & 4194303) << 42) | ((((long) i3) & 1023) << 10) | (((long) i4) & 1023);
    }

    public TileCache(TileParamsGetter tileParamsGetter) {
        this.mParamsGetter = tileParamsGetter;
    }

    public long[] getItemCache() {
        return this.mItemCache;
    }

    public int getX(int i) {
        if (isNullCacheOrInvalidPosition(i)) {
            return 0;
        }
        return getXFromSpec(this.mItemCache[i]);
    }

    public int getY(int i) {
        if (isNullCacheOrInvalidPosition(i)) {
            return 0;
        }
        return getYFromSpec(this.mItemCache[i]);
    }

    public int getWidth(int i) {
        if (isNullCacheOrInvalidPosition(i)) {
            return 1;
        }
        return getWidthFromSpec(this.mItemCache[i]);
    }

    public int getHeight(int i) {
        if (isNullCacheOrInvalidPosition(i)) {
            return 1;
        }
        return getHeightFromSpec(this.mItemCache[i]);
    }

    private boolean isNullCacheOrInvalidPosition(int i) {
        long[] jArr = this.mItemCache;
        return jArr == null || i < 0 || i >= jArr.length;
    }

    public int getTotalHeight() {
        return this.mTotalHeight;
    }

    private void buildTileBitmap() {
        this.mTileBitmap = new TileBitmap(this.mParamsGetter.getColumnCount());
    }

    private void releaseTileBitmap() {
        TileBitmap tileBitmap = this.mTileBitmap;
        if (tileBitmap != null) {
            tileBitmap.release();
            this.mTileBitmap = null;
        }
    }

    public void updateCache() {
        updateCacheCommon();
    }

    public void updateCacheCommon() {
        int itemCount = this.mParamsGetter.getItemCount();
        int columnCount = this.mParamsGetter.getColumnCount();
        this.mTotalHeight = 0;
        if (itemCount == 0 || columnCount == 0) {
            return;
        }
        long jElapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos();
        buildTileBitmap();
        this.mItemCache = new long[itemCount];
        int[] iArr = new int[2];
        int i = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < itemCount; i3++) {
            int[] tileSize = this.mParamsGetter.getTileSize(i3);
            int i4 = tileSize[0];
            int i5 = tileSize[1];
            this.mTileBitmap.findAvailablePlace(iArr, i, i2, i4, i5);
            this.mTileBitmap.placeItem(iArr[0], iArr[1], i4, i5);
            this.mItemCache[i3] = makeItemSpec(iArr[0], iArr[1], i4, i5);
            if (iArr[0] == i && iArr[1] == i2) {
                i += i4;
                while (true) {
                    if (i >= columnCount || this.mTileBitmap.get(i, i2)) {
                        if (i >= columnCount) {
                            i2++;
                            i = 0;
                        } else {
                            i++;
                        }
                    }
                }
            }
        }
        this.mTotalHeight = this.mTileBitmap.getTotalHeight();
        releaseTileBitmap();
        Log.i(TAG, "updateCache cost: " + String.format("%,d", Long.valueOf(SystemClock.elapsedRealtimeNanos() - jElapsedRealtimeNanos)) + "ns");
    }

    public void updateCacheNative() {
        long jElapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos();
        this.mTotalHeight = 0;
        int itemCount = this.mParamsGetter.getItemCount();
        int columnCount = this.mParamsGetter.getColumnCount();
        long[] jArr = new long[itemCount];
        for (int i = 0; i < itemCount; i++) {
            int[] tileSize = this.mParamsGetter.getTileSize(i);
            jArr[i] = (((long) i) << 20) | (((long) tileSize[0]) << 10) | ((long) tileSize[1]);
        }
        this.mTotalHeight = TileBitmapNative.getTileCache(jArr, itemCount, columnCount);
        this.mItemCache = jArr;
        Log.i(TAG, "updateCacheNative cost: " + String.format("%,d", Long.valueOf(SystemClock.elapsedRealtimeNanos() - jElapsedRealtimeNanos)) + "ns");
    }
}
