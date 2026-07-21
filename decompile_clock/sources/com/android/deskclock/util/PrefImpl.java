package com.android.deskclock.util;

import android.content.Context;
import android.content.SharedPreferences;

/* JADX INFO: loaded from: classes.dex */
public class PrefImpl {
    private static SharedPreferences getDefaultPreferences(Context context) {
        return FBEUtil.getDefaultSharedPreferences(context);
    }

    protected static void putString(Context context, String str, String str2) {
        SharedPreferences.Editor editorEdit = getDefaultPreferences(context).edit();
        editorEdit.putString(str, str2);
        editorEdit.apply();
    }

    protected static String getString(Context context, String str) {
        return getString(context, str, "");
    }

    protected static String getString(Context context, String str, String str2) {
        return getDefaultPreferences(context).getString(str, str2);
    }

    protected static void putInt(Context context, String str, int i) {
        SharedPreferences.Editor editorEdit = getDefaultPreferences(context).edit();
        editorEdit.putInt(str, i);
        editorEdit.apply();
    }

    protected static int getInt(Context context, String str) {
        return getInt(context, str, -1);
    }

    protected static int getInt(Context context, String str, int i) {
        return getDefaultPreferences(context).getInt(str, i);
    }

    protected static void putLong(Context context, String str, long j) {
        SharedPreferences.Editor editorEdit = getDefaultPreferences(context).edit();
        editorEdit.putLong(str, j);
        editorEdit.apply();
    }

    protected static long getLong(Context context, String str) {
        return getLong(context, str, -1L);
    }

    protected static long getLong(Context context, String str, long j) {
        return getDefaultPreferences(context).getLong(str, j);
    }

    protected static boolean putFloat(Context context, String str, float f) {
        SharedPreferences.Editor editorEdit = getDefaultPreferences(context).edit();
        editorEdit.putFloat(str, f);
        return editorEdit.commit();
    }

    protected static float getFloat(Context context, String str) {
        return getFloat(context, str, -1.0f);
    }

    protected static float getFloat(Context context, String str, float f) {
        return getDefaultPreferences(context).getFloat(str, f);
    }

    protected static void putBoolean(Context context, String str, boolean z) {
        SharedPreferences.Editor editorEdit = getDefaultPreferences(context).edit();
        editorEdit.putBoolean(str, z);
        editorEdit.apply();
    }

    protected static boolean getBoolean(Context context, String str) {
        return getBoolean(context, str, false);
    }

    protected static boolean getBoolean(Context context, String str, boolean z) {
        return getDefaultPreferences(context).getBoolean(str, z);
    }
}
