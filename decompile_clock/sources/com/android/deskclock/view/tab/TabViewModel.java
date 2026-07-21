package com.android.deskclock.view.tab;

import com.android.deskclock.DeskClockApp;
import com.android.deskclock.util.Util;

/* JADX INFO: loaded from: classes.dex */
public class TabViewModel {
    public static final String TAB_ALARM = "ALARM";
    public static final String TAB_CLOCK = "CLOCK";
    public static final String TAB_STOPWATCH = "STOPWATCH";
    public static final String TAB_TIMER = "TIMER";
    public static final String[] TABS = {TAB_ALARM, TAB_CLOCK, TAB_STOPWATCH, TAB_TIMER};
    public static final String[] TINY_SCREEN_TABS = {TAB_ALARM, TAB_TIMER};
    public static final String[] RTL_TABS = {TAB_TIMER, TAB_STOPWATCH, TAB_CLOCK, TAB_ALARM};

    public static int getTabCount() {
        if (Util.isTinyScreen(DeskClockApp.getAppContext())) {
            return TINY_SCREEN_TABS.length;
        }
        return TABS.length;
    }

    public static String getTab(int i) {
        if (i < 0) {
            return null;
        }
        String[] strArr = TABS;
        if (i >= strArr.length) {
            return null;
        }
        if (Util.isTinyScreen(DeskClockApp.getAppContext())) {
            if (i == 3) {
                i = 1;
            } else if (i == 2) {
                i = 0;
            }
            return TINY_SCREEN_TABS[i];
        }
        return strArr[i];
    }

    public static String getTabAt(int i, boolean z) {
        if (i < 0) {
            return null;
        }
        String[] strArr = TABS;
        if (i >= strArr.length) {
            return null;
        }
        if (z) {
            if (i == 3) {
                i = 1;
            } else if (i == 2) {
                i = 0;
            }
            return TINY_SCREEN_TABS[i];
        }
        return strArr[i];
    }

    public static int checkPosition(int i) {
        return Util.isRtl() ? (getTabCount() - i) - 1 : i;
    }

    public static int getTabPosition(String str, boolean z) {
        for (int i = 0; i < TABS.length; i++) {
            if (getTabAt(i, z).equals(str)) {
                return i;
            }
        }
        return 0;
    }
}
