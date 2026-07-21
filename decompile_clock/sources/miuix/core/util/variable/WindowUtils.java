package miuix.core.util.variable;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

/* JADX INFO: loaded from: classes2.dex */
public class WindowUtils {
    private WindowUtils() {
    }

    public static void setTranslucentStatus(Window window, int i) {
        WindowWrapper.setTranslucentStatus(window, i);
    }

    public static void changeWindowBackground(Activity activity, float f) {
        WindowManager.LayoutParams attributes = activity.getWindow().getAttributes();
        attributes.alpha = f;
        activity.getWindow().setAttributes(attributes);
    }
}
