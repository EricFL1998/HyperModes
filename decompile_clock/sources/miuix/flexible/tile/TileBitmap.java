package miuix.flexible.tile;

/* JADX INFO: loaded from: classes2.dex */
public class TileBitmap {
    private static final int PAGE_COUNT = 16;
    private static final int PAGE_SIZE = 4096;
    private static final int UNIT_SIZE = 8;
    private byte[][] mBitmap = new byte[16][];
    private int mTotalHeight;
    private final int mWidth;

    public TileBitmap(int i) {
        this.mWidth = i;
    }

    public void set(int i, int i2, boolean z) {
        int i3 = (i2 * this.mWidth) + i;
        int i4 = i3 / 4096;
        checkPage(i4);
        int i5 = (i3 % 4096) / 8;
        byte[] bArr = this.mBitmap[i4];
        byte b = bArr[i5];
        int i6 = i3 % 8;
        bArr[i5] = (byte) (z ? ((1 << i6) & 255) | b : (~(1 << i6)) & 255 & b);
    }

    public void set(int i, int i2, int i3, int i4, boolean z) {
        for (int i5 = i; i5 < i + i3; i5++) {
            for (int i6 = i2; i6 < i2 + i4; i6++) {
                set(i5, i6, z);
            }
        }
    }

    public boolean get(int i, int i2) {
        int i3 = (i2 * this.mWidth) + i;
        int i4 = i3 / 4096;
        checkPage(i4);
        return (this.mBitmap[i4][(i3 % 4096) / 8] & ((1 << (i3 % 8)) & 255)) != 0;
    }

    public void findAvailablePlace(int[] iArr, int i, int i2, int i3, int i4) {
        while (!isAvailable(i, i2, i3, i4)) {
            if (i >= this.mWidth) {
                i2++;
                i = 0;
            } else {
                i++;
            }
        }
        iArr[0] = i;
        iArr[1] = i2;
    }

    public boolean isAvailable(int i, int i2, int i3, int i4) {
        int i5 = i + i3;
        if (i5 > this.mWidth) {
            return false;
        }
        if (i3 == 1 && i4 == 1) {
            return !get(i, i2);
        }
        while (i < i5) {
            for (int i6 = i2; i6 < i2 + i4; i6++) {
                if (get(i, i6)) {
                    return false;
                }
            }
            i++;
        }
        return true;
    }

    public void placeItem(int i, int i2, int i3, int i4) {
        if (i3 == 1 && i4 == 1) {
            set(i, i2, true);
        } else {
            set(i, i2, i3, i4, true);
        }
        this.mTotalHeight = Math.max(this.mTotalHeight, i2 + i4);
    }

    private void checkPage(int i) {
        byte[][] bArr = this.mBitmap;
        if (bArr[i] == null) {
            bArr[i] = new byte[512];
        }
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getTotalHeight() {
        return this.mTotalHeight;
    }

    public byte[][] getBitmap() {
        return this.mBitmap;
    }

    public void release() {
        if (this.mBitmap != null) {
            this.mBitmap = null;
        }
    }
}
