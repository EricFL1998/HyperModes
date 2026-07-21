package kotlin.io;

import com.android.deskclock.settings.SearchContract;
import com.android.deskclock.util.SleepModeUtil;
import java.io.File;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: Exceptions.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000\u0010\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a$\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\b\u0010\u0004\u001a\u0004\u0018\u00010\u00032\b\u0010\u0005\u001a\u0004\u0018\u00010\u0001H\u0002¨\u0006\u0006"}, d2 = {"constructMessage", "", "file", "Ljava/io/File;", SearchContract.SearchResultColumn.COLUMN_OTHER, SleepModeUtil.EXTRA_REASON, "kotlin-stdlib"}, k = 2, mv = {2, 1, 0}, xi = 48)
public final class ExceptionsKt {
    /* JADX INFO: Access modifiers changed from: private */
    public static final String constructMessage(File file, File file2, String str) {
        StringBuilder sb = new StringBuilder(file.toString());
        if (file2 != null) {
            sb.append(" -> " + file2);
        }
        if (str != null) {
            sb.append(": " + str);
        }
        String string = sb.toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        return string;
    }
}
