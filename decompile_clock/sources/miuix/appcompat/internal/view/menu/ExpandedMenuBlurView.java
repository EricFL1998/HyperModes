package miuix.appcompat.internal.view.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/* JADX INFO: loaded from: classes2.dex */
@Deprecated
public class ExpandedMenuBlurView extends FrameLayout {
    public boolean isSearchStubSupportBlur() {
        return false;
    }

    public ExpandedMenuBlurView(Context context) {
        this(context, null);
    }

    public ExpandedMenuBlurView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }
}
