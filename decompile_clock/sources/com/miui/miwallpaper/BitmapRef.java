package com.miui.miwallpaper;

import android.graphics.Bitmap;
import java.util.concurrent.atomic.AtomicInteger;

/* JADX INFO: loaded from: classes2.dex */
public class BitmapRef {
    private Bitmap mBitmap;
    private final AtomicInteger mCount = new AtomicInteger(0);

    public BitmapRef(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return this.mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.mCount.set(0);
        this.mBitmap = bitmap;
    }

    public void increment() {
        this.mCount.incrementAndGet();
    }

    public int decrement() {
        if (this.mCount.get() == 0) {
            return 0;
        }
        return this.mCount.decrementAndGet();
    }

    public boolean canRecycle() {
        return this.mCount.get() == 0;
    }

    public void recycle() {
        Bitmap bitmap = this.mBitmap;
        if (bitmap != null) {
            bitmap.recycle();
        }
        this.mCount.set(0);
    }

    public boolean equals(BitmapRef bitmapRef) {
        return bitmapRef != null && this.mBitmap == bitmapRef.getBitmap();
    }

    public String toString() {
        return "BitmapRef{" + this.mBitmap + ", " + this.mCount + '}';
    }
}
