package com.google.android.material.resources;

/* JADX INFO: loaded from: classes2.dex */
@Deprecated
public class TextAppearanceConfig {
    private static boolean shouldLoadFontSynchronously;

    public static void setShouldLoadFontSynchronously(boolean z) {
        shouldLoadFontSynchronously = z;
    }

    public static boolean shouldLoadFontSynchronously() {
        return shouldLoadFontSynchronously;
    }
}
