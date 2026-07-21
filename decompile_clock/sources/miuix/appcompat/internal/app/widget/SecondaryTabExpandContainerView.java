package miuix.appcompat.internal.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import miuix.appcompat.R;

/* JADX INFO: loaded from: classes2.dex */
public class SecondaryTabExpandContainerView extends SecondaryTabContainerView {
    public SecondaryTabExpandContainerView(Context context) {
        super(context);
    }

    public SecondaryTabExpandContainerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public SecondaryTabExpandContainerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // miuix.appcompat.internal.app.widget.SecondaryTabContainerView
    protected int getDefaultTabTextStyle() {
        return R.attr.actionBarTabTextSecondaryExpandStyle;
    }

    @Override // miuix.appcompat.internal.app.widget.SecondaryTabContainerView
    protected int getTabActivatedTextStyle() {
        return R.attr.actionBarTabActivatedTextSecondaryExpandStyle;
    }
}
