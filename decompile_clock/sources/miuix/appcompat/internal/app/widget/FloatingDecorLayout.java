package miuix.appcompat.internal.app.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import miuix.appcompat.R;

/* JADX INFO: loaded from: classes2.dex */
public class FloatingDecorLayout extends FrameLayout {
    public FloatingDecorLayout(Context context) {
        this(context, null);
    }

    public FloatingDecorLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public FloatingDecorLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // android.view.View
    protected boolean fitSystemWindows(Rect rect) {
        ActionBarOverlayLayout actionBarOverlayLayout = (ActionBarOverlayLayout) findViewById(R.id.action_bar_overlay_layout);
        if (actionBarOverlayLayout != null) {
            actionBarOverlayLayout.fitSystemWindows(rect);
        }
        rect.top = 0;
        rect.bottom = 0;
        return super.fitSystemWindows(rect);
    }
}
