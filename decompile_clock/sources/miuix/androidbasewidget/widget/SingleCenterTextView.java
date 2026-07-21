package miuix.androidbasewidget.widget;

import android.content.Context;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;
import miuix.animation.Folme;
import miuix.animation.IHoverStyle;
import miuix.animation.base.AnimConfig;

/* JADX INFO: loaded from: classes2.dex */
public class SingleCenterTextView extends AppCompatTextView {
    private boolean mEnableSingleCenter;

    public SingleCenterTextView(Context context) {
        this(context, null);
    }

    public SingleCenterTextView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public SingleCenterTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        Folme.useAt(this).hover().setEffect(IHoverStyle.HoverEffect.NORMAL).handleHoverOf(this, new AnimConfig[0]);
    }

    public void setEnableSingleCenter(boolean z) {
        this.mEnableSingleCenter = z;
    }

    @Override // androidx.appcompat.widget.AppCompatTextView, android.widget.TextView, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.mEnableSingleCenter && getLayout().getLineCount() == 1 && getGravity() != 1) {
            setGravity(1);
        }
    }
}
