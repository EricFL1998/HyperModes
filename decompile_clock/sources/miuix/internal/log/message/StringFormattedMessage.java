package miuix.internal.log.message;

import android.util.Log;
import java.io.IOException;
import java.util.Locale;
import miuix.internal.log.util.AppendableFormatter;

/* JADX INFO: loaded from: classes2.dex */
public class StringFormattedMessage extends AbstractMessage {
    private static final String TAG = "StringFormattedMessage";
    private String mFormat;
    private AppendableFormatter mFormatter = new AppendableFormatter();
    private Object[] mParameters;
    private Throwable mThrowable;

    public static StringFormattedMessage obtain() {
        return (StringFormattedMessage) MessageFactory.obtain(StringFormattedMessage.class);
    }

    public String getFormat() {
        return this.mFormat;
    }

    public StringFormattedMessage setFormat(String str) {
        this.mFormat = str;
        return this;
    }

    public Object[] getParameters() {
        return this.mParameters;
    }

    public StringFormattedMessage setParameters(Object[] objArr) {
        this.mParameters = objArr;
        return this;
    }

    @Override // miuix.internal.log.message.AbstractMessage
    protected void onRecycle() {
        this.mFormat = null;
        this.mParameters = null;
        this.mThrowable = null;
        this.mFormatter.setAppendable(null);
    }

    @Override // miuix.internal.log.message.AbstractMessage, miuix.internal.log.message.Message
    public void format(Appendable appendable) {
        Object[] objArr;
        String str = this.mFormat;
        if (str == null || (objArr = this.mParameters) == null || objArr.length == 0) {
            try {
                appendable.append(str);
                return;
            } catch (IOException e) {
                Log.e(TAG, "Fail to format message", e);
                return;
            }
        }
        this.mFormatter.setAppendable(appendable);
        this.mFormatter.format(Locale.US, this.mFormat, this.mParameters);
    }

    @Override // miuix.internal.log.message.AbstractMessage, miuix.internal.log.message.Message
    public Throwable getThrowable() {
        return this.mThrowable;
    }

    public StringFormattedMessage setThrowable(Throwable th) {
        this.mThrowable = th;
        return this;
    }
}
