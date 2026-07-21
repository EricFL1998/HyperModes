package com.miui.support.cardview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewOutlineProvider;

/* JADX INFO: loaded from: classes2.dex */
public class BackgroundOutline extends ViewOutlineProvider {
    private float mAlpha;

    public BackgroundOutline(Context context, int i) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(i, R.styleable.BackgroundOutline);
        this.mAlpha = typedArrayObtainStyledAttributes.getFloat(R.styleable.BackgroundOutline_android_alpha, 1.0f);
        typedArrayObtainStyledAttributes.recycle();
    }

    private BackgroundOutline(float f) {
        this.mAlpha = f;
    }

    @Override // android.view.ViewOutlineProvider
    public void getOutline(View view, Outline outline) {
        Drawable background;
        if (view.getWidth() == 0 || view.getHeight() == 0) {
            return;
        }
        if (view.isAttachedToWindow() && (background = view.getBackground()) != null) {
            background.getOutline(outline);
        }
        outline.setAlpha(this.mAlpha);
    }

    public BackgroundOutline of(float f) {
        return new BackgroundOutline(f);
    }
}
