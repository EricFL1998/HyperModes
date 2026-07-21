package com.android.deskclock.alarm.lifepost.model;

import android.graphics.Bitmap;

/* JADX INFO: loaded from: classes.dex */
public class RecommendViewModel {
    private Gallery gallery;
    private Bitmap picBitmap;
    private int type;

    public int getType() {
        return this.type;
    }

    public void setType(int i) {
        this.type = i;
    }

    public Gallery getGallery() {
        return this.gallery;
    }

    public void setGallery(Gallery gallery) {
        this.gallery = gallery;
    }

    public Bitmap getPicBitmap() {
        return this.picBitmap;
    }

    public void setPicBitmap(Bitmap bitmap) {
        this.picBitmap = bitmap;
    }
}
