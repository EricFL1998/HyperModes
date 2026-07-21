package com.android.deskclock.util.log.message;

import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public abstract class AbstractMessage implements Message {
    private static final String TAG = "AbstractMessage";
    private boolean mRecycled;

    @Override // com.android.deskclock.util.log.message.Message
    public abstract void format(Appendable appendable);

    @Override // com.android.deskclock.util.log.message.Message
    public abstract Throwable getThrowable();

    protected abstract void onRecycle();

    @Override // com.android.deskclock.util.log.message.Message
    public void recycle() {
        if (this.mRecycled) {
            Log.w(TAG, "Recycle message twice");
            return;
        }
        onRecycle();
        this.mRecycled = true;
        MessageFactory.recycle(this);
    }

    @Override // com.android.deskclock.util.log.message.Message
    public boolean isRecycled() {
        return this.mRecycled;
    }

    @Override // com.android.deskclock.util.log.message.Message
    public void prepareForReuse() {
        this.mRecycled = false;
    }
}
