package com.android.deskclock.view;

import android.content.Context;
import android.util.AttributeSet;
import com.google.android.flexbox.FlexboxLayout;

/* JADX INFO: loaded from: classes.dex */
public class CustomFlexboxLayout extends FlexboxLayout {
    private boolean isLineBreak;

    public CustomFlexboxLayout(Context context) {
        super(context);
    }

    public CustomFlexboxLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CustomFlexboxLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // com.google.android.flexbox.FlexboxLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (z) {
            this.isLineBreak = false;
            int childCount = getChildCount();
            int width = 0;
            for (int i5 = 0; i5 < childCount; i5++) {
                width += getChildAt(i5).getWidth();
                if (width > getWidth()) {
                    this.isLineBreak = true;
                    return;
                }
            }
        }
    }

    public boolean isLineBreak() {
        return this.isLineBreak;
    }
}
