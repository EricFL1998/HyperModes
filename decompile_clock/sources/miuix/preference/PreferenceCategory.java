package miuix.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import androidx.core.content.res.TypedArrayUtils;

/* JADX INFO: loaded from: classes3.dex */
public class PreferenceCategory extends androidx.preference.PreferenceCategory {
    private boolean mNeedDividerLine;

    public PreferenceCategory(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, TypedArrayUtils.getAttr(context, R.attr.preferenceCategoryStyle, android.R.attr.preferenceCategoryStyle));
    }

    public PreferenceCategory(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public PreferenceCategory(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.PreferenceCategory, i, i2);
        setNeedDividerLine(typedArrayObtainStyledAttributes.getBoolean(R.styleable.PreferenceCategory_needDividerLine, true));
        typedArrayObtainStyledAttributes.recycle();
    }

    public boolean isDividerLineNeeded() {
        return this.mNeedDividerLine;
    }

    public void setNeedDividerLine(boolean z) {
        this.mNeedDividerLine = z;
    }

    public boolean hasTitle() {
        return !TextUtils.isEmpty(getTitle());
    }
}
