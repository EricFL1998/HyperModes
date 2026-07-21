package miuix.internal.log.message;

import android.util.Log;

/* JADX INFO: loaded from: classes2.dex */
public abstract class AbstractMessage implements Message {
    private static final String TAG = "AbstractMessage";
    private boolean mRecycled;

    @Override // miuix.internal.log.message.Message
    public abstract void format(Appendable appendable);

    @Override // miuix.internal.log.message.Message
    public abstract Throwable getThrowable();

    protected abstract void onRecycle();

    @Override // miuix.internal.log.message.Message
    public void recycle() {
        if (this.mRecycled) {
            Log.w(TAG, "Recycle message twice");
            return;
        }
        onRecycle();
        this.mRecycled = true;
        MessageFactory.recycle(this);
    }

    @Override // miuix.internal.log.message.Message
    public boolean isRecycled() {
        return this.mRecycled;
    }

    @Override // miuix.internal.log.message.Message
    public void prepareForReuse() {
        this.mRecycled = false;
    }
}
