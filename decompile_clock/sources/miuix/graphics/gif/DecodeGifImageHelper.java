package miuix.graphics.gif;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import miuix.io.ResettableInputStream;

/* JADX INFO: loaded from: classes2.dex */
public class DecodeGifImageHelper {
    public static final int MESSAGE_WHAT_DECODE_FRAMES = 1;
    public Handler mDecodeFrameHandler;
    private DecodeGifFrames mDecodeGifFrames;
    public boolean mDecodedAllFrames;
    public ResettableInputStream mGifSource;
    private int mMaxFrames;
    public int mRealFrameCount;
    public long mMaxDecodeSize = 1048576;
    public List<GifFrame> mFrames = new ArrayList();

    public static class GifDecodeResult {
        public GifDecoder mGifDecoder;
        public boolean mIsDecodeOk;
    }

    public static class GifFrame {
        public int mDuration;
        public Bitmap mImage;
        public int mIndex;

        public GifFrame(Bitmap bitmap, int i, int i2) {
            this.mImage = bitmap;
            this.mDuration = i;
            this.mIndex = i2;
        }
    }

    private int getLastFrameIndex() {
        List<GifFrame> list = this.mFrames;
        return list.get(list.size() - 1).mIndex;
    }

    private int calcFrameIndex(int i) {
        int i2 = this.mRealFrameCount;
        return i2 == 0 ? i : i % i2;
    }

    public void decodeNextFrames() {
        int size = this.mFrames.size();
        int i = this.mMaxFrames;
        if (i <= 3) {
            if (size > 2) {
                return;
            }
        } else if (size > i / 2) {
            return;
        }
        this.mDecodeGifFrames.decode(calcFrameIndex(getLastFrameIndex() + 1));
    }

    public boolean handleDecodeFramesResult(GifDecodeResult gifDecodeResult) {
        if (!gifDecodeResult.mIsDecodeOk || gifDecodeResult.mGifDecoder == null) {
            return false;
        }
        GifDecoder gifDecoder = gifDecodeResult.mGifDecoder;
        Log.d("dumpFrameIndex", String.format("Thread#%d: decoded %d frames [%s] [%d]", Long.valueOf(Thread.currentThread().getId()), Integer.valueOf(gifDecodeResult.mGifDecoder.getFrameCount()), Boolean.valueOf(gifDecodeResult.mIsDecodeOk), Integer.valueOf(this.mRealFrameCount)));
        if (gifDecoder.isDecodeToTheEnd()) {
            this.mRealFrameCount = gifDecoder.getRealFrameCount();
        }
        int frameCount = gifDecoder.getFrameCount();
        if (frameCount > 0) {
            int lastFrameIndex = getLastFrameIndex();
            for (int i = 0; i < frameCount; i++) {
                this.mFrames.add(new GifFrame(gifDecoder.getFrame(i), gifDecoder.getDelay(i), calcFrameIndex(lastFrameIndex + 1 + i)));
            }
        }
        return true;
    }

    public GifDecodeResult decodeFrom(int i) {
        return decode(this.mGifSource, this.mMaxDecodeSize, i);
    }

    public static GifDecodeResult decode(ResettableInputStream resettableInputStream, long j, int i) {
        GifDecodeResult gifDecodeResult = new GifDecodeResult();
        gifDecodeResult.mGifDecoder = null;
        gifDecodeResult.mIsDecodeOk = false;
        try {
            resettableInputStream.reset();
            gifDecodeResult.mGifDecoder = new GifDecoder();
            GifDecoder gifDecoder = gifDecodeResult.mGifDecoder;
            gifDecoder.setStartFrame(i);
            gifDecoder.setMaxDecodeSize(j);
            gifDecodeResult.mIsDecodeOk = gifDecoder.read(resettableInputStream) == 0;
            resettableInputStream.close();
        } catch (IOException unused) {
        }
        return gifDecodeResult;
    }

    public void firstDecodeNextFrames() {
        Handler handler = new Handler(Looper.getMainLooper()) { // from class: miuix.graphics.gif.DecodeGifImageHelper.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                if (message.what != 1) {
                    return;
                }
                DecodeGifImageHelper decodeGifImageHelper = DecodeGifImageHelper.this;
                if (decodeGifImageHelper.handleDecodeFramesResult(decodeGifImageHelper.mDecodeGifFrames.getAndClearDecodeResult())) {
                    DecodeGifImageHelper.this.decodeNextFrames();
                }
            }
        };
        this.mDecodeFrameHandler = handler;
        this.mDecodeGifFrames = DecodeGifFrames.createInstance(this.mGifSource, this.mMaxDecodeSize, handler);
        this.mMaxFrames = this.mFrames.size();
        decodeNextFrames();
    }

    public void destroy() {
        DecodeGifFrames decodeGifFrames = this.mDecodeGifFrames;
        if (decodeGifFrames != null) {
            decodeGifFrames.destroy();
        }
    }
}
