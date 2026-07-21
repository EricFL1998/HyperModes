package miuix.graphics.gif;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import miuix.io.ResettableInputStream;

/* JADX INFO: compiled from: DecodeGifImageHelper.java */
/* JADX INFO: loaded from: classes2.dex */
class DecodeGifFrames extends Handler {
    private static final int MESSAGE_WHAT_START = 1;
    protected static final String TAG = "DecodeGifFrames";
    private Handler mCallerHandler;
    DecodeGifImageHelper.GifDecodeResult mDecodeResult;
    private ResettableInputStream mGifSource;
    HandlerThread mHandlerThread;
    private long mMaxDecodeSize;

    public static DecodeGifFrames createInstance(ResettableInputStream resettableInputStream, long j, Handler handler) {
        HandlerThread handlerThread = new HandlerThread("handler thread to decode GIF frames");
        handlerThread.start();
        return new DecodeGifFrames(handlerThread, resettableInputStream, j, handler);
    }

    public DecodeGifFrames(HandlerThread handlerThread, ResettableInputStream resettableInputStream, long j, Handler handler) {
        super(handlerThread.getLooper());
        this.mHandlerThread = handlerThread;
        this.mMaxDecodeSize = j;
        this.mGifSource = resettableInputStream;
        this.mCallerHandler = handler;
    }

    public void destroy() {
        this.mHandlerThread.quit();
    }

    protected void finalize() throws Throwable {
        this.mHandlerThread.quit();
        super.finalize();
    }

    public void decode(int i) {
        if (this.mDecodeResult != null) {
            return;
        }
        this.mDecodeResult = new DecodeGifImageHelper.GifDecodeResult();
        sendMessage(obtainMessage(1, i, 0));
    }

    public DecodeGifImageHelper.GifDecodeResult getAndClearDecodeResult() {
        DecodeGifImageHelper.GifDecodeResult gifDecodeResult = this.mDecodeResult;
        this.mDecodeResult = null;
        return gifDecodeResult;
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        if (message.what == 1) {
            DecodeGifImageHelper.GifDecodeResult gifDecodeResultDecode = DecodeGifImageHelper.decode(this.mGifSource, this.mMaxDecodeSize, message.arg1);
            this.mDecodeResult.mGifDecoder = gifDecodeResultDecode.mGifDecoder;
            this.mDecodeResult.mIsDecodeOk = gifDecodeResultDecode.mIsDecodeOk;
            this.mCallerHandler.sendEmptyMessage(1);
        }
    }
}
