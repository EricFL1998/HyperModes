package miuix.util;

import android.content.Context;
import android.os.DropBoxManager;
import android.os.Process;
import java.text.SimpleDateFormat;
import java.util.Date;
import miuix.core.util.SoftReferenceSingleton;

/* JADX INFO: loaded from: classes3.dex */
public class DropBoxLog {
    private static final String CLOUD_DROP_BOX_LOG_TAG = "micloud";
    private static final SoftReferenceSingleton<SimpleDateFormat> DATE_FORMAT = new SoftReferenceSingleton<SimpleDateFormat>() { // from class: miuix.util.DropBoxLog.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // miuix.core.util.SoftReferenceSingleton
        public SimpleDateFormat createInstance() {
            return new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
        }
    };
    private StringBuilder mStringBuilder = new StringBuilder();

    public DropBoxLog v(String str, String str2) {
        android.util.Log.v(str, str2);
        appendLog("V", str, str2, null);
        return this;
    }

    public DropBoxLog v(String str, String str2, Throwable th) {
        android.util.Log.v(str, str2, th);
        appendLog("V", str, str2, th);
        return this;
    }

    public DropBoxLog d(String str, String str2) {
        android.util.Log.d(str, str2);
        appendLog("D", str, str2, null);
        return this;
    }

    public DropBoxLog d(String str, String str2, Throwable th) {
        android.util.Log.d(str, str2, th);
        appendLog("D", str, str2, th);
        return this;
    }

    public DropBoxLog i(String str, String str2) {
        android.util.Log.i(str, str2);
        appendLog("I", str, str2, null);
        return this;
    }

    public DropBoxLog i(String str, String str2, Throwable th) {
        android.util.Log.i(str, str2, th);
        appendLog("I", str, str2, th);
        return this;
    }

    public DropBoxLog w(String str, String str2) {
        android.util.Log.w(str, str2);
        appendLog("W", str, str2, null);
        return this;
    }

    public DropBoxLog w(String str, String str2, Throwable th) {
        android.util.Log.w(str, str2, th);
        appendLog("W", str, str2, th);
        return this;
    }

    public DropBoxLog e(String str, String str2) {
        android.util.Log.e(str, str2);
        appendLog("E", str, str2, null);
        return this;
    }

    public DropBoxLog e(String str, String str2, Throwable th) {
        android.util.Log.e(str, str2, th);
        appendLog("E", str, str2, th);
        return this;
    }

    public String getMessage() {
        return this.mStringBuilder.toString();
    }

    public void commit(Context context, String str) {
        if (str == null) {
            str = context.getPackageName();
        }
        writeLog(context, this.mStringBuilder, "micloud_" + str);
        this.mStringBuilder.setLength(0);
    }

    private void appendLog(String str, String str2, String str3, Throwable th) {
        this.mStringBuilder.append(DATE_FORMAT.get().format(new Date()));
        this.mStringBuilder.append(' ');
        this.mStringBuilder.append(str);
        this.mStringBuilder.append('/');
        this.mStringBuilder.append(str2);
        this.mStringBuilder.append('(');
        this.mStringBuilder.append(Process.myPid());
        this.mStringBuilder.append(')');
        this.mStringBuilder.append(':');
        this.mStringBuilder.append(str3);
        if (th != null) {
            this.mStringBuilder.append(':');
            this.mStringBuilder.append(android.util.Log.getStackTraceString(th));
        }
        this.mStringBuilder.append('\r');
    }

    public static void writeLog(Context context, CharSequence charSequence, String str) {
        DropBoxManager dropBoxManager = (DropBoxManager) context.getSystemService("dropbox");
        if (charSequence == null || charSequence.length() <= 0 || dropBoxManager == null || !dropBoxManager.isTagEnabled(str)) {
            return;
        }
        dropBoxManager.addText(str, charSequence.toString());
    }
}
