package miuix.internal.util;

import android.content.Context;
import android.view.accessibility.AccessibilityManager;

/* JADX INFO: loaded from: classes2.dex */
public class AccessibilityUtil {
    public static boolean isTalkBackActive(Context context) {
        AccessibilityManager accessibilityManager = (AccessibilityManager) context.getSystemService("accessibility");
        return accessibilityManager.isEnabled() && accessibilityManager.isTouchExplorationEnabled();
    }
}
