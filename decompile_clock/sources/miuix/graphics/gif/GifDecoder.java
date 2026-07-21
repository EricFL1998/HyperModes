package miuix.graphics.gif;

import android.graphics.Bitmap;
import androidx.core.view.ViewCompat;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Vector;

/* JADX INFO: loaded from: classes2.dex */
public class GifDecoder {
    public static final int MAX_DECODE_SIZE = 1048576;
    protected static final int MAX_STACK_SIZE = 4096;
    public static final int STATUS_DECODE_CANCEL = 3;
    public static final int STATUS_FORMAT_ERROR = 1;
    public static final int STATUS_OK = 0;
    public static final int STATUS_OPEN_ERROR = 2;
    protected int[] act;
    protected int bgColor;
    protected int bgIndex;
    private int[] dest;
    protected Vector<GifFrame> frames;
    protected int[] gct;
    protected boolean gctFlag;
    protected int gctSize;
    private int height;
    protected int ih;
    protected Bitmap image;
    protected BufferedInputStream in;
    protected boolean interlace;
    protected int iw;
    protected int ix;
    protected int iy;
    protected int lastBgColor;
    protected Bitmap lastBitmap;
    protected int[] lct;
    protected boolean lctFlag;
    protected int lctSize;
    protected int lrh;
    protected int lrw;
    protected int lrx;
    protected int lry;
    private long mDecodeBmSize;
    private boolean mDecodeToTheEnd;
    private int mDecodedFrames;
    private int mStartFrame;
    protected int pixelAspect;
    protected byte[] pixelStack;
    protected byte[] pixels;
    protected short[] prefix;
    protected int status;
    protected byte[] suffix;
    protected int transIndex;
    private int width;
    private long mMaxDecodeSize = 1048576;
    protected int loopCount = 1;
    protected byte[] block = new byte[256];
    protected int blockSize = 0;
    protected int dispose = 0;
    protected int lastDispose = 0;
    protected boolean transparency = false;
    protected int delay = 0;
    private boolean mCancel = false;
    private boolean calledOnce = false;

    private void requestCancel() {
    }

    public boolean isDecodeToTheEnd() {
        return this.mDecodeToTheEnd;
    }

    public void setStartFrame(int i) {
        this.mStartFrame = i;
    }

    public void recycle() {
        Vector<GifFrame> vector = this.frames;
        if (vector != null) {
            int size = vector.size();
            for (int i = 0; i < size; i++) {
                this.frames.elementAt(i).recycle();
            }
        }
    }

    public void setMaxDecodeSize(long j) {
        this.mMaxDecodeSize = j;
    }

    private static class GifFrame {
        public int delay;
        public Bitmap image;

        public GifFrame(Bitmap bitmap, int i) {
            this.image = bitmap;
            this.delay = i;
        }

        public void recycle() {
            Bitmap bitmap = this.image;
            if (bitmap == null || bitmap.isRecycled()) {
                return;
            }
            this.image.recycle();
        }
    }

    public int getDelay(int i) {
        this.delay = -1;
        int frameCount = getFrameCount();
        if (i >= 0 && i < frameCount) {
            this.delay = this.frames.elementAt(i).delay;
        }
        return this.delay;
    }

    public int getFrameCount() {
        Vector<GifFrame> vector = this.frames;
        if (vector == null) {
            return 0;
        }
        return vector.size();
    }

    public Bitmap getBitmap() {
        return getFrame(0);
    }

    public int getLoopCount() {
        return this.loopCount;
    }

    protected void setPixels() {
        Bitmap bitmap;
        int i;
        if (this.dest == null) {
            this.dest = new int[this.width * this.height];
        }
        int i2 = this.lastDispose;
        int i3 = 0;
        if (i2 > 0) {
            if (i2 == 3) {
                int frameCount = getFrameCount();
                if (frameCount - 2 > 0) {
                    Bitmap frame = getFrame(frameCount - 3);
                    if (!frame.equals(this.lastBitmap)) {
                        this.lastBitmap = frame;
                        int[] iArr = this.dest;
                        int i4 = this.width;
                        frame.getPixels(iArr, 0, i4, 0, 0, i4, this.height);
                    }
                } else {
                    this.lastBitmap = null;
                    this.dest = new int[this.width * this.height];
                }
            }
            if (this.lastBitmap != null && this.lastDispose == 2) {
                int i5 = !this.transparency ? this.lastBgColor : 0;
                int i6 = (this.lry * this.width) + this.lrx;
                for (int i7 = 0; i7 < this.lrh; i7++) {
                    int i8 = this.lrw + i6;
                    for (int i9 = i6; i9 < i8; i9++) {
                        this.dest[i9] = i5;
                    }
                    i6 += this.width;
                }
            }
        }
        int i10 = 1;
        int i11 = 8;
        int i12 = 0;
        while (true) {
            int i13 = this.ih;
            if (i3 >= i13) {
                break;
            }
            if (this.interlace) {
                if (i12 >= i13) {
                    i10++;
                    if (i10 == 2) {
                        i12 = 4;
                    } else if (i10 == 3) {
                        i12 = 2;
                        i11 = 4;
                    } else if (i10 == 4) {
                        i12 = 1;
                        i11 = 2;
                    }
                }
                i = i12 + i11;
            } else {
                i = i12;
                i12 = i3;
            }
            int i14 = i12 + this.iy;
            if (i14 < this.height) {
                int i15 = this.width;
                int i16 = i14 * i15;
                int i17 = this.ix + i16;
                int i18 = this.iw;
                int i19 = i17 + i18;
                if (i16 + i15 < i19) {
                    i19 = i16 + i15;
                }
                int i20 = i18 * i3;
                while (i17 < i19) {
                    int i21 = i20 + 1;
                    int i22 = this.act[this.pixels[i20] & 255];
                    if (i22 != 0) {
                        this.dest[i17] = i22;
                    }
                    i17++;
                    i20 = i21;
                }
            }
            i3++;
            i12 = i;
        }
        if (this.mDecodedFrames <= this.mStartFrame && (bitmap = this.image) != null && !bitmap.isRecycled()) {
            this.image.recycle();
        }
        this.image = Bitmap.createBitmap(this.dest, this.width, this.height, Bitmap.Config.ARGB_8888);
    }

    public Bitmap getFrame(int i) {
        int frameCount = getFrameCount();
        if (frameCount <= 0) {
            return null;
        }
        return this.frames.elementAt(i % frameCount).image;
    }

    public void requestCancelDecode() {
        this.mCancel = true;
        requestCancel();
    }

    protected static int readOneByte(InputStream inputStream) {
        try {
            return inputStream.read();
        } catch (Exception unused) {
            return -1;
        }
    }

    public static boolean isGifStream(InputStream inputStream) {
        int oneByte;
        if (inputStream == null) {
            return false;
        }
        String str = "";
        for (int i = 0; i < 6 && (oneByte = readOneByte(inputStream)) != -1; i++) {
            str = str + ((char) oneByte);
        }
        return str.startsWith("GIF");
    }

    public int read(InputStream inputStream) {
        this.mDecodeToTheEnd = false;
        if (this.calledOnce) {
            throw new IllegalStateException("decoder cannot be called more than once");
        }
        this.calledOnce = true;
        init();
        if (inputStream != null) {
            this.in = new BufferedInputStream(inputStream);
            try {
                readHeader();
                if (!this.mCancel && !err()) {
                    readContents();
                    if (getFrameCount() < 0) {
                        this.status = 1;
                    }
                }
            } catch (OutOfMemoryError unused) {
                this.status = 2;
                recycle();
            }
        } else {
            this.status = 2;
        }
        if (this.mCancel) {
            recycle();
            this.status = 3;
        }
        return this.status;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v20 */
    /* JADX WARN: Type inference failed for: r2v21 */
    /* JADX WARN: Type inference failed for: r2v22 */
    /* JADX WARN: Type inference failed for: r2v25, types: [short] */
    /* JADX WARN: Type inference failed for: r2v27 */
    protected void decodeBitmapData() {
        int i;
        int i2;
        int i3;
        short s;
        int i4 = this.iw * this.ih;
        byte[] bArr = this.pixels;
        if (bArr == null || bArr.length < i4) {
            this.pixels = new byte[i4];
        }
        if (this.prefix == null) {
            this.prefix = new short[4096];
        }
        if (this.suffix == null) {
            this.suffix = new byte[4096];
        }
        if (this.pixelStack == null) {
            this.pixelStack = new byte[4097];
        }
        int i5 = read();
        int i6 = 1 << i5;
        int i7 = i6 + 1;
        int i8 = i6 + 2;
        int i9 = i5 + 1;
        int i10 = (1 << i9) - 1;
        for (int i11 = 0; i11 < i6; i11++) {
            this.prefix[i11] = 0;
            this.suffix[i11] = (byte) i11;
        }
        int i12 = i9;
        int i13 = i10;
        int i14 = 0;
        int i15 = 0;
        int i16 = 0;
        int i17 = 0;
        int block = 0;
        int i18 = 0;
        int i19 = 0;
        int i20 = 0;
        int i21 = -1;
        int i22 = i8;
        while (i14 < i4) {
            if (i15 != 0) {
                int i23 = i19;
                i = i6;
                i2 = i23;
            } else if (i16 < i12) {
                if (block == 0) {
                    block = readBlock();
                    if (block <= 0) {
                        break;
                    } else {
                        i18 = 0;
                    }
                }
                i17 += (this.block[i18] & 255) << i16;
                i16 += 8;
                i18++;
                block--;
            } else {
                int i24 = i17 & i13;
                i17 >>= i12;
                i16 -= i12;
                if (i24 > i22 || i24 == i7) {
                    break;
                }
                if (i24 == i6) {
                    i12 = i9;
                    i22 = i8;
                    i13 = i10;
                    i21 = -1;
                } else if (i21 == -1) {
                    this.pixelStack[i15] = this.suffix[i24];
                    i21 = i24;
                    i19 = i21;
                    i15++;
                    i9 = i9;
                } else {
                    if (i24 == i22) {
                        i3 = i24;
                        this.pixelStack[i15] = (byte) i19;
                        s = i21;
                        i15++;
                    } else {
                        i3 = i24;
                        s = i3;
                    }
                    while (s > i6) {
                        this.pixelStack[i15] = this.suffix[s];
                        s = this.prefix[s];
                        i15++;
                        i6 = i6;
                    }
                    i = i6;
                    byte[] bArr2 = this.suffix;
                    i2 = bArr2[s] & 255;
                    if (i22 >= 4096) {
                        break;
                    }
                    int i25 = i15 + 1;
                    byte b = (byte) i2;
                    this.pixelStack[i15] = b;
                    this.prefix[i22] = (short) i21;
                    bArr2[i22] = b;
                    i22++;
                    if ((i22 & i13) == 0 && i22 < 4096) {
                        i12++;
                        i13 += i22;
                    }
                    i15 = i25;
                    i21 = i3;
                }
            }
            i15--;
            this.pixels[i20] = this.pixelStack[i15];
            i14++;
            i20++;
            i6 = i;
            i7 = i7;
            i19 = i2;
            i9 = i9;
        }
        for (int i26 = i20; i26 < i4; i26++) {
            this.pixels[i26] = 0;
        }
    }

    protected boolean err() {
        return this.status != 0;
    }

    protected void init() {
        this.status = 0;
        this.frames = new Vector<>();
        this.gct = null;
        this.lct = null;
    }

    protected int read() {
        try {
            return this.in.read();
        } catch (Exception unused) {
            this.status = 1;
            return 0;
        }
    }

    protected int readBlock() {
        int i;
        int i2 = read();
        this.blockSize = i2;
        int i3 = 0;
        if (i2 > 0) {
            while (true) {
                try {
                    int i4 = this.blockSize;
                    if (i3 >= i4 || (i = this.in.read(this.block, i3, i4 - i3)) == -1) {
                        break;
                    }
                    i3 += i;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (i3 < this.blockSize) {
                this.status = 1;
            }
        }
        return i3;
    }

    protected int[] readColorTable(int i) {
        int i2;
        int i3 = i * 3;
        byte[] bArr = new byte[i3];
        try {
            i2 = this.in.read(bArr, 0, i3);
        } catch (Exception e) {
            e.printStackTrace();
            i2 = 0;
        }
        if (i2 < i3) {
            this.status = 1;
            return null;
        }
        int[] iArr = new int[256];
        int i4 = 0;
        for (int i5 = 0; i5 < i; i5++) {
            int i6 = bArr[i4] & 255;
            int i7 = i4 + 2;
            int i8 = bArr[i4 + 1] & 255;
            i4 += 3;
            iArr[i5] = (i8 << 8) | (i6 << 16) | ViewCompat.MEASURED_STATE_MASK | (bArr[i7] & 255);
        }
        return iArr;
    }

    public int getRealFrameCount() {
        if (this.mDecodeToTheEnd) {
            return this.mDecodedFrames;
        }
        return 0;
    }

    protected void readContents() {
        this.mDecodedFrames = 0;
        boolean z = false;
        while (!z && !err() && !this.mCancel) {
            int i = read();
            if (i == 33) {
                int i2 = read();
                if (i2 == 1) {
                    skip();
                } else if (i2 == 249) {
                    readGraphicControlExt();
                } else if (i2 == 254) {
                    skip();
                } else if (i2 == 255) {
                    readBlock();
                    String str = "";
                    for (int i3 = 0; i3 < 11; i3++) {
                        str = str + ((char) this.block[i3]);
                    }
                    if (str.equals("NETSCAPE2.0")) {
                        readNetscapeExt();
                    } else {
                        skip();
                    }
                } else {
                    skip();
                }
            } else if (i == 44) {
                int size = this.frames.size();
                readBitmap();
                if (this.frames.size() > size) {
                    this.mDecodeBmSize += (long) (this.image.getRowBytes() * this.image.getHeight());
                }
                if (this.mDecodeBmSize > this.mMaxDecodeSize) {
                    z = true;
                }
            } else if (i == 59) {
                this.mDecodeToTheEnd = true;
                z = true;
            } else {
                this.status = 1;
            }
        }
    }

    protected void readGraphicControlExt() {
        read();
        int i = read();
        int i2 = (i & 28) >> 2;
        this.dispose = i2;
        if (i2 == 0) {
            this.dispose = 1;
        }
        this.transparency = (i & 1) != 0;
        int i3 = readShort() * 10;
        this.delay = i3;
        if (i3 <= 0) {
            this.delay = 100;
        }
        this.transIndex = read();
        read();
    }

    protected void readHeader() {
        if (this.mCancel) {
            return;
        }
        String str = "";
        for (int i = 0; i < 6; i++) {
            str = str + ((char) read());
        }
        if (!str.startsWith("GIF")) {
            this.status = 1;
            return;
        }
        readLSD();
        if (!this.gctFlag || err()) {
            return;
        }
        int[] colorTable = readColorTable(this.gctSize);
        this.gct = colorTable;
        this.bgColor = colorTable[this.bgIndex];
    }

    protected void readBitmap() {
        int[] iArr;
        int i;
        this.ix = readShort();
        this.iy = readShort();
        this.iw = readShort();
        this.ih = readShort();
        int i2 = read();
        int i3 = 0;
        boolean z = (i2 & 128) != 0;
        this.lctFlag = z;
        int i4 = 2 << (i2 & 7);
        this.lctSize = i4;
        this.interlace = (i2 & 64) != 0;
        if (z) {
            int[] colorTable = readColorTable(i4);
            this.lct = colorTable;
            this.act = colorTable;
        } else {
            this.act = this.gct;
            if (this.bgIndex == this.transIndex) {
                this.bgColor = 0;
            }
        }
        if (this.transparency && (iArr = this.act) != null && (i = this.transIndex) >= 0 && i < iArr.length) {
            int i5 = iArr[i];
            iArr[i] = 0;
            i3 = i5;
        }
        if (this.act == null) {
            this.status = 1;
        }
        if (err()) {
            return;
        }
        decodeBitmapData();
        skip();
        if (err() || this.mCancel) {
            return;
        }
        setPixels();
        if (this.mDecodedFrames >= this.mStartFrame) {
            this.frames.addElement(new GifFrame(this.image, this.delay));
        }
        this.mDecodedFrames++;
        if (this.transparency) {
            this.act[this.transIndex] = i3;
        }
        resetFrame();
    }

    protected void readLSD() {
        this.width = readShort();
        this.height = readShort();
        int i = read();
        this.gctFlag = (i & 128) != 0;
        this.gctSize = 2 << (i & 7);
        this.bgIndex = read();
        this.pixelAspect = read();
    }

    protected void readNetscapeExt() {
        do {
            readBlock();
            byte[] bArr = this.block;
            if (bArr[0] == 1) {
                this.loopCount = ((bArr[2] & 255) << 8) | (bArr[1] & 255);
            }
            if (this.blockSize <= 0) {
                return;
            }
        } while (!err());
    }

    protected int readShort() {
        return read() | (read() << 8);
    }

    protected void resetFrame() {
        this.lastDispose = this.dispose;
        this.lrx = this.ix;
        this.lry = this.iy;
        this.lrw = this.iw;
        this.lrh = this.ih;
        this.lastBitmap = this.image;
        this.lastBgColor = this.bgColor;
        this.dispose = 0;
        this.transparency = false;
        this.delay = 0;
        this.lct = null;
    }

    protected void skip() {
        do {
            readBlock();
            if (this.blockSize <= 0) {
                return;
            }
        } while (!err());
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}
