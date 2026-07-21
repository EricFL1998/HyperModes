package miuix.androidbasewidget.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import miuix.androidbasewidget.R;
import miuix.reflect.Reflects;

/* JADX INFO: loaded from: classes2.dex */
public class ProgressBar extends android.widget.ProgressBar {
    private Drawable mIndeterminateDrawableOrignal;

    public ProgressBar(Context context) {
        this(context, null);
    }

    public ProgressBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.progressBarStyle);
    }

    public ProgressBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        postConstruct(context, attributeSet, i);
    }

    public void postConstruct(Context context, AttributeSet attributeSet, int i) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ProgressBar, i, R.style.Widget_ProgressBar_Horizontal_DayNight);
        Drawable drawable = this.mIndeterminateDrawableOrignal;
        if (drawable != null && drawable.getClass().getName().equals("android.graphics.drawable.AnimatedRotateDrawable")) {
            int i2 = typedArrayObtainStyledAttributes.getInt(R.styleable.ProgressBar_indeterminateFramesCount, 48);
            Class<?> cls = drawable.getClass();
            Reflects.invoke(drawable, Reflects.getMethod(cls, "setFramesCount", (Class<?>[]) new Class[]{Integer.TYPE}), Integer.valueOf(i2));
            Reflects.invoke(drawable, Reflects.getMethod(cls, "setFramesDuration", (Class<?>[]) new Class[]{Integer.TYPE}), Integer.valueOf(typedArrayObtainStyledAttributes.getInt(R.styleable.ProgressBar_indeterminateFramesDuration, 25)));
        }
        typedArrayObtainStyledAttributes.recycle();
    }

    @Override // android.widget.ProgressBar
    public void setIndeterminateDrawable(Drawable drawable) {
        super.setIndeterminateDrawable(drawable);
        if (this.mIndeterminateDrawableOrignal != drawable) {
            this.mIndeterminateDrawableOrignal = drawable;
        }
    }

    @Override // android.widget.ProgressBar, android.view.View
    public CharSequence getAccessibilityClassName() {
        return View.class.getName();
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        if (accessibilityNodeInfo.getRangeInfo() == null) {
            if (Build.VERSION.SDK_INT >= 30) {
                accessibilityNodeInfo.setStateDescription(getContext().getString(R.string.miuix_progressbar_loading));
            } else {
                accessibilityNodeInfo.setContentDescription(getContext().getString(R.string.miuix_progressbar_loading));
            }
        }
    }
}
