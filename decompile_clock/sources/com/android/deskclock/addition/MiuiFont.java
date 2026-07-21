package com.android.deskclock.addition;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.widget.TextView;
import com.android.deskclock.util.Log;
import miuix.animation.utils.EaseManager;

/* JADX INFO: loaded from: classes.dex */
public class MiuiFont {
    private static final String ERROR_MSG = "folme font error: ";
    public static int FONT_TYPE_MITYPE_MONO = 2;
    public static int FONT_TYPE_MIUI = 0;
    public static Typeface MI_PRO_DEMIBOLD = null;
    public static Typeface MI_PRO_LIGHT = null;
    public static Typeface MI_PRO_MEDIUM = null;
    public static Typeface MI_PRO_NORMAL = null;
    public static Typeface MI_PRO_REGULAR = null;
    public static Typeface MI_TYPE_MONO_BOLD = null;
    public static Typeface MI_TYPE_MONO_DEMIBOLD = null;
    public static Typeface MI_TYPE_MONO_HEAVY = null;
    public static Typeface MI_TYPE_MONO_MEDIUM = null;
    public static Typeface MI_TYPE_MONO_NORMAL = null;
    public static Typeface MI_TYPE_MONO_REGULAR = null;
    public static Typeface MI_TYPE_MONO_SEMIBOLD = null;
    private static Object STYLE_ALARM_FONT = null;
    private static Object STYLE_STOPWATCH_MAX = null;
    private static Object STYLE_STOPWATCH_NORMAL = null;
    private static final String TAG = "DC:MiuiFont";

    static {
        try {
            STYLE_ALARM_FONT = EaseManager.getStyle(0, 350.0f, 0.875f, 0.9f);
            STYLE_STOPWATCH_MAX = EaseManager.getStyle(0, 300.0f, 0.75f, 0.99f);
            STYLE_STOPWATCH_NORMAL = EaseManager.getStyle(0, 1500.0f, 0.75f, 0.99f);
        } catch (Exception e) {
            Log.e(TAG, ERROR_MSG + e.getMessage());
        }
        try {
            if (MiuiSdk.isSupportMiUiFont()) {
                MI_PRO_NORMAL = Typeface.create("mipro-normal", 0);
                MI_PRO_MEDIUM = Typeface.create("mipro-medium", 0);
                MI_PRO_REGULAR = Typeface.create("mipro-regular", 0);
                MI_PRO_LIGHT = Typeface.create("mipro-light", 0);
                MI_PRO_DEMIBOLD = Typeface.create("mipro-demibold", 0);
                MI_TYPE_MONO_NORMAL = Typeface.create("mitype-mono-normal", 0);
                MI_TYPE_MONO_MEDIUM = Typeface.create("mitype-mono-medium", 0);
                MI_TYPE_MONO_BOLD = Typeface.create("mitype-mono-bold", 0);
                MI_TYPE_MONO_HEAVY = Typeface.create("mitype-mono-heavy", 0);
                MI_TYPE_MONO_DEMIBOLD = Typeface.create("mitype-mono-demibold", 0);
                MI_TYPE_MONO_SEMIBOLD = Typeface.create("mitype-mono-semibold", 0);
                MI_TYPE_MONO_REGULAR = Typeface.create("mitype-mono-regular", 0);
            }
        } catch (Exception e2) {
            Log.e(TAG, ERROR_MSG + e2.getMessage());
        }
    }

    public static void setFont(TextView textView, Typeface typeface) {
        if (typeface != null) {
            try {
                textView.setTypeface(typeface);
                textView.setAlpha(1.0f);
            } catch (Exception e) {
                Log.e(TAG, ERROR_MSG + e.getMessage());
            }
        }
    }

    public static void setPaintFont(Paint paint, Typeface typeface) {
        if (typeface != null) {
            try {
                paint.setTypeface(typeface);
            } catch (Exception e) {
                Log.e(TAG, ERROR_MSG + e.getMessage());
            }
        }
    }
}
