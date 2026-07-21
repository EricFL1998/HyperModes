package miuix.internal.view;

import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import miuix.appcompat.R;

/* JADX INFO: loaded from: classes2.dex */
public class RadioButtonAnimatedStateListDrawable extends CheckBoxAnimatedStateListDrawable {
    private int mDrawPadding;

    @Override // miuix.internal.view.CheckBoxAnimatedStateListDrawable
    protected boolean isSingleSelectionWidget() {
        return true;
    }

    public RadioButtonAnimatedStateListDrawable() {
        this.mDrawPadding = 19;
    }

    public RadioButtonAnimatedStateListDrawable(Resources resources, Resources.Theme theme, CheckWidgetAnimatedStateListDrawable.CheckWidgetConstantState checkWidgetConstantState) {
        super(resources, theme, checkWidgetConstantState);
        this.mDrawPadding = 19;
        if (resources != null) {
            this.mDrawPadding = resources.getDimensionPixelSize(R.dimen.miuix_appcompat_radio_button_drawable_padding);
        }
    }

    @Override // miuix.internal.view.CheckBoxAnimatedStateListDrawable
    protected int getCheckWidgetDrawableStyle() {
        return R.style.CheckWidgetDrawable_RadioButton;
    }

    @Override // miuix.internal.view.CheckBoxAnimatedStateListDrawable, miuix.internal.view.CheckWidgetAnimatedStateListDrawable
    protected CheckWidgetAnimatedStateListDrawable.CheckWidgetConstantState newCheckWidgetConstantState() {
        return new RadioButtonConstantState();
    }

    protected static class RadioButtonConstantState extends CheckWidgetAnimatedStateListDrawable.CheckWidgetConstantState {
        protected RadioButtonConstantState() {
        }

        @Override // miuix.internal.view.CheckWidgetAnimatedStateListDrawable.CheckWidgetConstantState
        protected Drawable newAnimatedStateListDrawable(Resources resources, Resources.Theme theme, CheckWidgetAnimatedStateListDrawable.CheckWidgetConstantState checkWidgetConstantState) {
            return new RadioButtonAnimatedStateListDrawable(resources, theme, checkWidgetConstantState);
        }
    }

    @Override // miuix.internal.view.CheckBoxAnimatedStateListDrawable
    protected void setCheckWidgetDrawableBounds(int i, int i2, int i3, int i4) {
        int i5 = this.mDrawPadding;
        super.setCheckWidgetDrawableBounds(i + i5, i2 + i5, i3 - i5, i4 - i5);
    }

    @Override // miuix.internal.view.CheckBoxAnimatedStateListDrawable
    protected void setCheckWidgetDrawableBounds(Rect rect) {
        int i = this.mDrawPadding;
        rect.inset(i, i);
        super.setCheckWidgetDrawableBounds(rect);
    }
}
