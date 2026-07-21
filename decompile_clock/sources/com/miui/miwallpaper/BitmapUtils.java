package com.miui.miwallpaper;

import android.graphics.Bitmap;

/* JADX INFO: loaded from: classes2.dex */
public class BitmapUtils {
    public static void recycleBitmap(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }
        bitmap.recycle();
    }
}
