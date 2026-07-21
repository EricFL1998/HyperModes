package com.android.deskclock.util;

import android.graphics.Typeface;
import com.android.deskclock.DeskClockApp;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class TypefaceFactory {
    public static final String CUSTOM_MONO_DEMIBOLD = "fonts/MitypeMono-DemiBold.otf";
    public static final String MI_TYPE_2019_40 = "fonts/Mitype2019-40.ttf";
    public static final String MI_TYPE_2019_50 = "fonts/Mitype2019-50.ttf";
    public static final String MI_TYPE_2019_60 = "fonts/Mitype2019-60.ttf";
    public static final String MI_TYPE_2019_70 = "fonts/Mitype2019-70.ttf";
    public static final String MI_TYPE_MiSANS_RCFVF = "fonts/MiSansRCFVF.ttf";
    public static Map<String, Typeface> sTypeface = new HashMap();

    public static Typeface get(String str) {
        Typeface typefaceCreateFromAsset = sTypeface.get(str);
        if (typefaceCreateFromAsset == null) {
            synchronized (TypefaceFactory.class) {
                typefaceCreateFromAsset = sTypeface.get(str);
                if (typefaceCreateFromAsset == null) {
                    typefaceCreateFromAsset = Typeface.createFromAsset(DeskClockApp.getAppDEContext().getAssets(), str);
                    sTypeface.put(str, typefaceCreateFromAsset);
                }
            }
        }
        return typefaceCreateFromAsset;
    }
}
