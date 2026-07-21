package miuix.androidbasewidget.internal.view;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

/* JADX INFO: loaded from: classes2.dex */
public class SeekBarGradientDrawable extends GradientDrawable {
    protected static final float HIGH_STIFFNESS = 986.96f;
    private int mHeight;
    protected SeekBarGradientState mSeekBarGradientState;
    private int mWidth;

    @Override // android.graphics.drawable.GradientDrawable, android.graphics.drawable.Drawable
    public boolean isStateful() {
        return true;
    }

    protected void startPressedAnim() {
    }

    protected void startUnPressedAnim() {
    }

    public SeekBarGradientDrawable() {
        this.mWidth = -1;
        this.mHeight = -1;
        SeekBarGradientState seekBarGradientStateNewSeekBarGradientState = newSeekBarGradientState();
        this.mSeekBarGradientState = seekBarGradientStateNewSeekBarGradientState;
        seekBarGradientStateNewSeekBarGradientState.setConstantState(super.getConstantState());
    }

    public SeekBarGradientDrawable(Resources resources, Resources.Theme theme, SeekBarGradientState seekBarGradientState) {
        Drawable drawableNewDrawable;
        this.mWidth = -1;
        this.mHeight = -1;
        if (resources == null) {
            drawableNewDrawable = seekBarGradientState.mParent.newDrawable();
        } else if (theme == null) {
            drawableNewDrawable = seekBarGradientState.mParent.newDrawable(resources);
        } else {
            drawableNewDrawable = seekBarGradientState.mParent.newDrawable(resources, theme);
        }
        seekBarGradientState.mParent = drawableNewDrawable.getConstantState();
        SeekBarGradientState seekBarGradientStateNewSeekBarGradientState = newSeekBarGradientState();
        this.mSeekBarGradientState = seekBarGradientStateNewSeekBarGradientState;
        seekBarGradientStateNewSeekBarGradientState.setConstantState(seekBarGradientState.mParent);
        this.mWidth = drawableNewDrawable.getIntrinsicWidth();
        this.mHeight = drawableNewDrawable.getIntrinsicHeight();
        if (drawableNewDrawable instanceof GradientDrawable) {
            GradientDrawable gradientDrawable = (GradientDrawable) drawableNewDrawable;
            setCornerRadius(gradientDrawable.getCornerRadius());
            setShape(gradientDrawable.getShape());
            setColor(gradientDrawable.getColor());
        }
    }

    protected SeekBarGradientState newSeekBarGradientState() {
        return new SeekBarGradientState();
    }

    @Override // android.graphics.drawable.GradientDrawable, android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] iArr) {
        boolean zOnStateChange = super.onStateChange(iArr);
        boolean z = false;
        for (int i : iArr) {
            if (i == 16842919) {
                z = true;
            }
        }
        if (z) {
            startPressedAnim();
        }
        if (!z) {
            startUnPressedAnim();
        }
        return zOnStateChange;
    }

    @Override // android.graphics.drawable.GradientDrawable, android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        return this.mSeekBarGradientState;
    }

    protected static class SeekBarGradientState extends Drawable.ConstantState {
        Drawable.ConstantState mParent;

        protected SeekBarGradientState() {
        }

        public void setConstantState(Drawable.ConstantState constantState) {
            this.mParent = constantState;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            if (this.mParent == null) {
                return null;
            }
            return newSeekBarGradientDrawable(null, null, this);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources resources) {
            if (this.mParent == null) {
                return null;
            }
            return newSeekBarGradientDrawable(resources, null, this);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources resources, Resources.Theme theme) {
            if (this.mParent == null) {
                return null;
            }
            return newSeekBarGradientDrawable(resources, theme, this);
        }

        protected Drawable newSeekBarGradientDrawable(Resources resources, Resources.Theme theme, SeekBarGradientState seekBarGradientState) {
            return new SeekBarGradientDrawable(resources, theme, seekBarGradientState);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            return this.mParent.getChangingConfigurations();
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public boolean canApplyTheme() {
            return this.mParent.canApplyTheme();
        }
    }

    @Override // android.graphics.drawable.GradientDrawable, android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.mWidth;
    }

    @Override // android.graphics.drawable.GradientDrawable, android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.mHeight;
    }
}
