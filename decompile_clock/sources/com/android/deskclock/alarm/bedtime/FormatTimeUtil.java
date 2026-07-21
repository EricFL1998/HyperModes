package com.android.deskclock.alarm.bedtime;

import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.widget.TextView;
import com.android.deskclock.addition.MiuiFont;
import java.util.Calendar;

/* JADX INFO: loaded from: classes.dex */
public class FormatTimeUtil {
    private static final String M24 = "kk:mm";

    public static void updateTime(TextView textView, Calendar calendar) {
        if (textView == null) {
            return;
        }
        textView.setText(DateFormat.format("kk:mm", calendar));
    }

    public static void setTypeface(TextView textView, Typeface typeface) {
        if (textView == null) {
            return;
        }
        MiuiFont.setFont(textView, typeface);
    }
}
