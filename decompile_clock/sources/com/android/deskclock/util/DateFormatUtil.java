package com.android.deskclock.util;

import java.text.DateFormatSymbols;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class DateFormatUtil {
    private static String mLanguage = Locale.getDefault().getLanguage();
    private static String[] amPmStrings = new DateFormatSymbols().getAmPmStrings();

    public static void reset() {
        if (mLanguage.equals(Locale.getDefault().getLanguage())) {
            return;
        }
        amPmStrings = new DateFormatSymbols().getAmPmStrings();
        mLanguage = Locale.getDefault().getLanguage();
    }

    public static String[] getAmPmStrings() {
        return amPmStrings;
    }
}
