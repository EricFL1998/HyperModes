package miuix.appcompat.internal.view;

import android.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.Layout;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;

/* JADX INFO: loaded from: classes2.dex */
public class CollapseTitleTextView extends AppCompatTextView {
    private float mOriginalTextSize;
    private final float mSmallTextSize;
    private final boolean mSmallTextSizeEnabled;

    public CollapseTitleTextView(Context context) {
        this(context, null);
    }

    public CollapseTitleTextView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.textViewStyle);
    }

    public CollapseTitleTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mOriginalTextSize = getTextSize();
        TypedArray typedArrayObtainStyledAttributes = context.getTheme().obtainStyledAttributes(attributeSet, miuix.appcompat.R.styleable.CollapseTitleView, i, 0);
        this.mSmallTextSizeEnabled = typedArrayObtainStyledAttributes.getBoolean(miuix.appcompat.R.styleable.CollapseTitleView_smallTextSizeEnabled, true);
        this.mSmallTextSize = typedArrayObtainStyledAttributes.getDimensionPixelSize(miuix.appcompat.R.styleable.CollapseTitleView_smallTextSize, context.getResources().getDimensionPixelSize(miuix.appcompat.R.dimen.miuix_font_size_headline1));
        typedArrayObtainStyledAttributes.recycle();
    }

    @Override // android.widget.TextView
    public void setTextSize(float f) {
        super.setTextSize(f);
        this.mOriginalTextSize = getTextSize();
    }

    @Override // androidx.appcompat.widget.AppCompatTextView, android.widget.TextView
    public void setTextAppearance(Context context, int i) {
        super.setTextAppearance(context, i);
        this.mOriginalTextSize = getTextSize();
    }

    @Override // androidx.appcompat.widget.AppCompatTextView, android.widget.TextView, android.view.View
    protected void onMeasure(int i, int i2) {
        if (this.mSmallTextSizeEnabled) {
            float f = this.mOriginalTextSize;
            if (f > this.mSmallTextSize) {
                setTextSize(0, f);
                super.onMeasure(i, i2);
                if (isTextEllipsis()) {
                    setTextSize(0, this.mSmallTextSize);
                    super.onMeasure(i, i2);
                    return;
                }
                return;
            }
        }
        super.onMeasure(i, i2);
    }

    private boolean isTextEllipsis() {
        Layout layout = getLayout();
        int lineCount = layout.getLineCount();
        if (getMaxLines() > 0 && lineCount > getMaxLines()) {
            return true;
        }
        for (int i = 0; i < lineCount; i++) {
            if (layout.getEllipsisCount(i) > 0) {
                return true;
            }
        }
        return false;
    }
}
