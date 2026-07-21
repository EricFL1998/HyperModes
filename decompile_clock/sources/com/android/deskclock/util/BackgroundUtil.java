package com.android.deskclock.util;

import android.app.UiModeManager;
import android.content.Context;
import com.android.deskclock.DeskClockApp;

/* JADX INFO: loaded from: classes.dex */
public class BackgroundUtil {
    public static final int VALUE_BACKGROUND_COLOR_BLACK = 2;
    public static final int VALUE_BACKGROUND_COLOR_WHITE = 1;

    public static boolean isAppNightMode() {
        return getSystemBackgroundColor(DeskClockApp.getAppContext()) == 2;
    }

    public static int getSystemBackgroundColor(Context context) {
        UiModeManager uiModeManager = (UiModeManager) context.getSystemService("uimode");
        return (uiModeManager == null || uiModeManager.getNightMode() != 2) ? 1 : 2;
    }
}
