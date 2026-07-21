package com.android.deskclock.widget;

import android.content.Context;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;

/* JADX INFO: loaded from: classes.dex */
public class AutoSizingTextView extends AppCompatTextView {
    private TextSizeHelper mTextSizeHelper;

    public AutoSizingTextView(Context context) {
        super(context);
        this.mTextSizeHelper = new TextSizeHelper(this);
    }

    public AutoSizingTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mTextSizeHelper = new TextSizeHelper(this);
    }

    public AutoSizingTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mTextSizeHelper = new TextSizeHelper(this);
    }

    @Override // androidx.appcompat.widget.AppCompatTextView, android.widget.TextView, android.view.View
    protected void onMeasure(int i, int i2) {
        TextSizeHelper textSizeHelper = this.mTextSizeHelper;
        if (textSizeHelper != null) {
            textSizeHelper.onMeasure(i, i2);
        }
        super.onMeasure(i, i2);
    }

    @Override // androidx.appcompat.widget.AppCompatTextView, android.widget.TextView
    protected void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        super.onTextChanged(charSequence, i, i2, i3);
        TextSizeHelper textSizeHelper = this.mTextSizeHelper;
        if (textSizeHelper != null) {
            textSizeHelper.onTextChanged(i2, i3);
        }
    }

    @Override // android.view.View
    public void requestLayout() {
        TextSizeHelper textSizeHelper = this.mTextSizeHelper;
        if (textSizeHelper == null || !textSizeHelper.shouldIgnoreRequestLayout()) {
            super.requestLayout();
        }
    }

    @Override // androidx.appcompat.widget.AppCompatTextView, android.widget.TextView
    public void setTextSize(int i, float f) {
        super.setTextSize(i, f);
        TextSizeHelper textSizeHelper = this.mTextSizeHelper;
        if (textSizeHelper != null) {
            textSizeHelper.resetMaxTextSize();
        }
    }
}
