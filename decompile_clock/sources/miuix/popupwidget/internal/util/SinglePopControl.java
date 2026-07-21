package miuix.popupwidget.internal.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.SparseArray;
import android.widget.PopupWindow;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes3.dex */
public class SinglePopControl {
    private static SparseArray<WeakReference<Object>> sPops = new SparseArray<>();

    private SinglePopControl() {
    }

    public static void showPop(Context context, Object obj) {
        Object obj2;
        int hashKey = getHashKey(context);
        if (sPops.get(hashKey) != null && (obj2 = sPops.get(hashKey).get()) != null && obj2 != obj) {
            hide(obj2);
        }
        sPops.put(hashKey, new WeakReference<>(obj));
    }

    public static void hidePop(Context context, Object obj) {
        sPops.remove(getHashKey(context));
    }

    private static void hide(Object obj) {
        if (obj instanceof PopupWindow) {
            PopupWindow popupWindow = (PopupWindow) obj;
            if (popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
        }
    }

    private static int getHashKey(Context context) {
        Activity associatedActivity = getAssociatedActivity(context);
        return associatedActivity != null ? associatedActivity.hashCode() : context.hashCode();
    }

    private static Activity getAssociatedActivity(Context context) {
        Activity activity = null;
        while (activity == null && context != null) {
            if (context instanceof Activity) {
                activity = (Activity) context;
            } else {
                context = context instanceof ContextWrapper ? ((ContextWrapper) context).getBaseContext() : null;
            }
        }
        return activity;
    }
}
