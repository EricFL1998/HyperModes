package com.android.deskclock.util;

import java.text.DecimalFormatSymbols;
import java.util.Locale;
import miuix.core.util.Pools;

/* JADX INFO: loaded from: classes.dex */
public class SimpleNumberFormatter {
    private static Locale sLocale = Locale.getDefault();
    private static char sZeroDigit = new DecimalFormatSymbols(sLocale).getZeroDigit();

    public static String format(int i) {
        return format(-1, i);
    }

    public static String format(int i, int i2) {
        char zeroDigit;
        try {
            zeroDigit = getZeroDigit(Locale.getDefault());
        } catch (NullPointerException e) {
            Log.e("locale == null", e);
            zeroDigit = '0';
        }
        String strConvertInt = convertInt(i, i2);
        return zeroDigit != '0' ? localizeDigits(zeroDigit, strConvertInt) : strConvertInt;
    }

    private static String convertInt(int i, int i2) {
        int i3;
        StringBuilder sbAcquire = Pools.getStringBuilderPool().acquire();
        if (i2 < 0) {
            i2 = -i2;
            i--;
            sbAcquire.append('-');
        }
        if (i2 >= 10000) {
            String string = Integer.toString(i2);
            for (int length = string.length(); length < i; length++) {
                sbAcquire.append('0');
            }
            sbAcquire.append(string);
        } else {
            if (i2 >= 1000) {
                i3 = 4;
            } else if (i2 >= 100) {
                i3 = 3;
            } else {
                i3 = i2 >= 10 ? 2 : 1;
            }
            while (i3 < i) {
                sbAcquire.append('0');
                i3++;
            }
            sbAcquire.append(i2);
        }
        String string2 = sbAcquire.toString();
        Pools.getStringBuilderPool().release(sbAcquire);
        return string2;
    }

    private static String localizeDigits(char c, String str) {
        int length = str.length();
        int i = c - '0';
        StringBuilder sbAcquire = Pools.getStringBuilderPool().acquire();
        for (int i2 = 0; i2 < length; i2++) {
            char cCharAt = str.charAt(i2);
            if (cCharAt >= '0' && cCharAt <= '9') {
                cCharAt = (char) (cCharAt + i);
            }
            sbAcquire.append(cCharAt);
        }
        String string = sbAcquire.toString();
        Pools.getStringBuilderPool().release(sbAcquire);
        return string;
    }

    private static char getZeroDigit(Locale locale) {
        if (locale == null) {
            throw new NullPointerException("locale == null");
        }
        if (!locale.equals(sLocale)) {
            sZeroDigit = new DecimalFormatSymbols(locale).getZeroDigit();
            sLocale = locale;
        }
        return sZeroDigit;
    }
}
