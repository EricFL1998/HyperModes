package miuix.appcompat.app;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import miuix.internal.util.ViewUtils;

/* JADX INFO: loaded from: classes2.dex */
public class TextAlignLayout extends LinearLayout {
    private boolean mDialogPanelHasCheckbox;

    public TextAlignLayout(Context context) {
        super(context);
    }

    public TextAlignLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public TextAlignLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public TextAlignLayout(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public void setDialogPanelHasCheckbox(boolean z) {
        this.mDialogPanelHasCheckbox = z;
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int childCount = getChildCount();
        boolean z = true;
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = getChildAt(i3);
            if (z && (childAt instanceof TextView)) {
                TextView textView = (TextView) childAt;
                boolean z2 = textView.getLineCount() <= 1 && !this.mDialogPanelHasCheckbox;
                if (z2) {
                    textView.setGravity(1);
                } else {
                    textView.setGravity(ViewUtils.isLayoutRtl(childAt) ? 5 : 3);
                }
                z = z2;
            }
        }
    }
}
