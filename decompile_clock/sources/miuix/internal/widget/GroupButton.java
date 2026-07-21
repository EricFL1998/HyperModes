package miuix.internal.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.appcompat.widget.ViewUtils;
import miuix.appcompat.R;
import miuix.appcompat.widget.Button;

/* JADX INFO: loaded from: classes2.dex */
public class GroupButton extends Button {
    private AttributeSet mAttrsCache;
    private boolean mPrimary;
    private static final int[] STATE_FIRST_V = {R.attr.state_first_v};
    private static final int[] STATE_MIDDLE_V = {R.attr.state_middle_v};
    private static final int[] STATE_LAST_V = {R.attr.state_last_v};
    private static final int[] STATE_FIRST_H = {R.attr.state_first_h};
    private static final int[] STATE_MIDDLE_H = {R.attr.state_middle_h};
    private static final int[] STATE_LAST_H = {R.attr.state_last_h};
    private static final int[] STATE_SINGLE_H = {R.attr.state_single_h};

    public GroupButton(Context context) {
        super(context);
    }

    public GroupButton(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public GroupButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initAttr(context, attributeSet, i);
    }

    private void initAttr(Context context, AttributeSet attributeSet, int i) {
        this.mAttrsCache = attributeSet;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.GroupButton, i, 0);
        try {
            if (typedArrayObtainStyledAttributes.hasValue(R.styleable.GroupButton_primaryButton)) {
                this.mPrimary = typedArrayObtainStyledAttributes.getBoolean(R.styleable.GroupButton_primaryButton, false);
            }
        } finally {
            typedArrayObtainStyledAttributes.recycle();
        }
    }

    @Override // android.widget.TextView, android.view.View
    protected int[] onCreateDrawableState(int i) {
        ViewGroup viewGroup = (ViewGroup) getParent();
        if (viewGroup == null) {
            return super.onCreateDrawableState(i);
        }
        if (viewGroup instanceof LinearLayout) {
            int orientation = ((LinearLayout) viewGroup).getOrientation();
            int iIndexOfChild = viewGroup.indexOfChild(this);
            int i2 = 0;
            boolean z = true;
            boolean z2 = true;
            for (int i3 = 0; i3 < viewGroup.getChildCount(); i3++) {
                if (viewGroup.getChildAt(i3).getVisibility() == 0) {
                    i2++;
                    if (i3 < iIndexOfChild) {
                        z = false;
                    }
                    if (i3 > iIndexOfChild) {
                        z2 = false;
                    }
                }
            }
            boolean z3 = i2 == 1;
            if (orientation == 1) {
                int[] iArrOnCreateDrawableState = super.onCreateDrawableState(i + 2);
                mergeDrawableStates(iArrOnCreateDrawableState, STATE_SINGLE_H);
                if (!z3) {
                    if (z) {
                        mergeDrawableStates(iArrOnCreateDrawableState, STATE_FIRST_V);
                    } else if (z2) {
                        mergeDrawableStates(iArrOnCreateDrawableState, STATE_LAST_V);
                    } else {
                        mergeDrawableStates(iArrOnCreateDrawableState, STATE_MIDDLE_V);
                    }
                }
                return iArrOnCreateDrawableState;
            }
            boolean zIsLayoutRtl = ViewUtils.isLayoutRtl(this);
            int[] iArrOnCreateDrawableState2 = super.onCreateDrawableState(i + 1);
            if (z3) {
                mergeDrawableStates(iArrOnCreateDrawableState2, STATE_SINGLE_H);
            } else if (z) {
                mergeDrawableStates(iArrOnCreateDrawableState2, zIsLayoutRtl ? STATE_LAST_H : STATE_FIRST_H);
            } else if (z2) {
                mergeDrawableStates(iArrOnCreateDrawableState2, zIsLayoutRtl ? STATE_FIRST_H : STATE_LAST_H);
            } else {
                mergeDrawableStates(iArrOnCreateDrawableState2, STATE_MIDDLE_H);
            }
            return iArrOnCreateDrawableState2;
        }
        return super.onCreateDrawableState(i);
    }

    public boolean isPrimary() {
        return this.mPrimary;
    }
}
