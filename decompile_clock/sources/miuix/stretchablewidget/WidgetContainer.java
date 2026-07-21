package miuix.stretchablewidget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/* JADX INFO: loaded from: classes3.dex */
public class WidgetContainer extends LinearLayout {
    public WidgetContainer(Context context) {
        this(context, null);
    }

    public WidgetContainer(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public WidgetContainer(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void setWidgetHeight(int i) {
        if (i < 0) {
            return;
        }
        getLayoutParams().height = i;
        requestLayout();
    }

    public int getWidgetHeight() {
        return getHeight();
    }
}
