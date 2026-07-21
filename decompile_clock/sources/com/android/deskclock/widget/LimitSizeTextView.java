package com.android.deskclock.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;
import com.android.deskclock.R;
import com.android.deskclock.util.PadAdapterUtil;
import com.android.deskclock.util.Util;

/* JADX INFO: loaded from: classes.dex */
public class LimitSizeTextView extends TextView {
    private float mLimitFontScale;
    private float mRealTextSize;
    private float mScale;

    public LimitSizeTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mRealTextSize = 0.0f;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.LimitSizeTextView);
        float f = typedArrayObtainStyledAttributes.getFloat(0, 0.0f);
        this.mScale = f;
        this.mLimitFontScale = f;
        typedArrayObtainStyledAttributes.recycle();
        if (this.mLimitFontScale > 0.0f) {
            float f2 = getResources().getConfiguration().fontScale;
            if (f2 >= this.mLimitFontScale) {
                if (Util.isDeviceCetus() || Util.isDeviceZizhan() || PadAdapterUtil.IS_PAD || Util.isRtl()) {
                    this.mLimitFontScale = 1.0f;
                }
                if (this.mRealTextSize == 0.0f) {
                    this.mRealTextSize = getTextSize() * (this.mLimitFontScale / f2);
                }
                setTextSize(0, this.mRealTextSize);
            }
        }
    }

    public void setLimitFontScale(float f) {
        this.mLimitFontScale = f;
    }

    @Override // android.widget.TextView
    public void setText(CharSequence charSequence, TextView.BufferType bufferType) {
        if (this.mLimitFontScale > 0.0f && !TextUtils.equals(charSequence, getText())) {
            super.setText(charSequence, bufferType);
            float f = getResources().getConfiguration().fontScale;
            if (f >= this.mLimitFontScale) {
                if (this.mRealTextSize == 0.0f) {
                    this.mRealTextSize = getTextSize() * (this.mLimitFontScale / f);
                }
                setTextSize(0, this.mRealTextSize);
                return;
            }
            return;
        }
        super.setText(charSequence, bufferType);
    }

    public void setTextSize2(int i, float f, boolean z) {
        if (z) {
            this.mLimitFontScale = 0.0f;
            setTextSize(i, f);
            return;
        }
        this.mLimitFontScale = this.mScale;
        float f2 = this.mRealTextSize;
        if (f > f2 && f2 > 0.0f) {
            setTextSize(i, f2);
        } else {
            setTextSize(i, f);
        }
    }
}
