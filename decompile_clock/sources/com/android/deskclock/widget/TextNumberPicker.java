package com.android.deskclock.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;

/* JADX INFO: loaded from: classes.dex */
public class TextNumberPicker extends NumberPicker {
    public TextNumberPicker(Context context) {
        super(context);
    }

    public TextNumberPicker(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public TextNumberPicker(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // com.android.deskclock.widget.NumberPicker, android.view.View
    public void onPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onPopulateAccessibilityEvent(accessibilityEvent);
        String[] displayedValues = super.getDisplayedValues();
        int value = super.getValue();
        if (displayedValues == null || value < 0 || value >= displayedValues.length) {
            return;
        }
        accessibilityEvent.getText().add(displayedValues[value]);
    }
}
