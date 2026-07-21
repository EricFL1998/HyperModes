package miuix.core.compat;

import android.content.ContentResolver;
import android.content.Context;
import android.os.UserHandle;

/* JADX INFO: loaded from: classes2.dex */
public class ContextCompat {
    public static ContentResolver getContentResolverForUser(Context context, UserHandle userHandle) {
        return context.getContentResolverForUser(userHandle);
    }
}
