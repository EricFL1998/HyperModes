package miuix.internal.log.message;

/* JADX INFO: loaded from: classes2.dex */
public interface Message {
    void format(Appendable appendable);

    Throwable getThrowable();

    boolean isRecycled();

    void prepareForReuse();

    void recycle();
}
