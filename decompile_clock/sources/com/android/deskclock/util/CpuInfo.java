package com.android.deskclock.util;

import android.content.Context;
import android.text.TextUtils;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import miuix.appcompat.app.floatingactivity.multiapp.MethodCodeHelper;

/* JADX INFO: loaded from: classes.dex */
public class CpuInfo {
    public static final int CPU_MTK_HSeries = 6;
    public static final int CPU_MTK_LSeries = 8;
    public static final int CPU_MTK_MSeries = 7;
    public static final int CPU_Qualcomm_Series_4 = 5;
    public static final int CPU_Qualcomm_Series_5 = 4;
    public static final int CPU_Qualcomm_Series_6 = 3;
    public static final int CPU_Qualcomm_Series_7 = 2;
    public static final int CPU_Qualcomm_Series_8 = 1;
    public static final int CPU_Unknow = 0;
    private static final String TAG = "MiPlayQuickCpuInfo";
    static String[] cpuinfo_arr_Qualcomm_8Series = {"SM8350", "SM8250", "SM8150", "SDM845", "MSM8998", "MSM8996pro", "MSM8996"};
    static String[] cpuinfo_arr_Qualcomm_7Series = {"SM7350", "SM7250", "SM7150", "SDM712", "SDM710", "LAGOON", "SM7225"};
    static String[] cpuinfo_arr_Qualcomm_5Series = new String[0];
    static String[] cpuinfo_arr_Qualcomm_6Series = {"TRINKET", "SM6150", "SDM660", "SDM632", "SDM636", "MSM8953", "SM6125", "BENGAL", "SM6350"};
    static String[] cpuinfo_arr_Qualcomm_4Series = {"SDM439"};
    static String[] cpuinfo_arr_MTK_Series = new String[0];
    static String[] cpuinfo_arr_MTK_HSeries = {"MT6889"};
    static String[] cpuinfo_arr_MTK_MSeries = new String[0];
    static String[] cpuinfo_arr_MTK_LSeries = new String[0];

    public static String getCpuName() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("/proc/cpuinfo"));
            while (true) {
                String line = bufferedReader.readLine();
                if (line != null) {
                    if (line.contains("Hardware")) {
                        String str = line.split(MethodCodeHelper.IDENTITY_INFO_SEPARATOR)[1];
                        if (str.contains("Qualcomm Technologies, Inc ")) {
                            String str2 = str.split("Inc ")[1];
                            android.util.Log.i(TAG, "cpu is " + str2);
                            return str2;
                        }
                        if (str.contains("Qualcomm Technologies, Inc. ")) {
                            String str3 = str.split("Inc. ")[1];
                            android.util.Log.i(TAG, "... cpu is " + str3);
                            return str3;
                        }
                        if (str.contains("MT")) {
                            String str4 = str.split(" ")[1];
                            android.util.Log.i(TAG, "mtk cpu is " + str4);
                            return str4;
                        }
                    }
                } else {
                    bufferedReader.close();
                    return null;
                }
            }
        } catch (IOException unused) {
            return null;
        }
    }

    public static boolean getCpuIsSupport(String[] strArr, String str) {
        if (strArr.length > 0 && str != null) {
            for (int i = 0; i < strArr.length; i++) {
                if (TextUtils.equals(strArr[i], str)) {
                    return true;
                }
                if (strArr[i].length() >= str.length()) {
                    if (TextUtils.regionMatches(strArr[i], 0, str, 0, str.length())) {
                        return true;
                    }
                } else {
                    String str2 = strArr[i];
                    if (TextUtils.regionMatches(str2, 0, str, 0, str2.length())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static String get(Context context, String str) throws IllegalArgumentException {
        try {
            Class<?> clsLoadClass = context.getClassLoader().loadClass("android.os.SystemProperties");
            return (String) clsLoadClass.getMethod("get", String.class).invoke(clsLoadClass, new String(str));
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception unused) {
            return "";
        }
    }

    public static int GetCpuSeriesFlag(Context context) {
        String cpuName = getCpuName();
        android.util.Log.i(TAG, "cpuinfo " + cpuName);
        if (cpuName != null) {
            if (getCpuIsSupport(cpuinfo_arr_Qualcomm_8Series, cpuName)) {
                return 1;
            }
            if (getCpuIsSupport(cpuinfo_arr_Qualcomm_7Series, cpuName)) {
                return 2;
            }
            if (getCpuIsSupport(cpuinfo_arr_Qualcomm_6Series, cpuName)) {
                return 3;
            }
            if (getCpuIsSupport(cpuinfo_arr_Qualcomm_5Series, cpuName)) {
                return 4;
            }
            if (getCpuIsSupport(cpuinfo_arr_Qualcomm_4Series, cpuName)) {
                return 5;
            }
            if (getCpuIsSupport(cpuinfo_arr_MTK_HSeries, cpuName)) {
                return 6;
            }
            if (getCpuIsSupport(cpuinfo_arr_MTK_MSeries, cpuName)) {
                return 7;
            }
            if (getCpuIsSupport(cpuinfo_arr_MTK_LSeries, cpuName)) {
                return 8;
            }
        }
        String str = context != null ? get(context, "ro.build.product") : null;
        return (str == null || !str.equals("venus")) ? 0 : 1;
    }
}
