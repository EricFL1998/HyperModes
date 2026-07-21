package com.xiaomi.onetrack.util;

import android.text.TextUtils;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* JADX INFO: loaded from: classes2.dex */
public class z {
    public static final String a = "\\.";
    public static final String b = ",";
    private static final String c = "com.xiaomi.onetrack.util.z";

    public static boolean a(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        for (int i = 0; i < length; i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean b(String str) {
        return (str == null || str.length() == 0 || str.equals("") || str.equals("null")) ? false : true;
    }

    public static List<String> a(String str, String str2) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            String[] strArrSplit = str.split(str2);
            if (strArrSplit == null || strArrSplit.length <= 0) {
                return null;
            }
            return Arrays.asList(strArrSplit);
        } catch (Exception e) {
            p.b(c, "StringToList error: " + e.getMessage());
            return null;
        }
    }

    public static Set<String> a(String str, String str2, String str3) {
        HashSet hashSet = new HashSet();
        try {
            String[] strArrSplit = null;
            String[] strArrSplit2 = TextUtils.isEmpty(str) ? null : str.split(str3);
            if (strArrSplit2 != null && strArrSplit2.length > 0) {
                hashSet.addAll(Arrays.asList(strArrSplit2));
            }
            if (!TextUtils.isEmpty(str2)) {
                strArrSplit = str2.split(str3);
            }
            if (strArrSplit != null && strArrSplit.length > 0) {
                hashSet.addAll(Arrays.asList(strArrSplit));
            }
        } catch (Exception e) {
            p.b(c, "mergeParams error: " + e.getMessage());
        }
        return hashSet;
    }
}
