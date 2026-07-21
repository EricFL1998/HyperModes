package com.android.deskclock.util.log.message;

/* JADX INFO: loaded from: classes.dex */
public interface Message {
    void format(Appendable appendable);

    Throwable getThrowable();

    boolean isRecycled();

    void prepareForReuse();

    void recycle();
}
