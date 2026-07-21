package miuix.theme;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.util.AttributeSet;
import android.util.StateSet;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes3.dex */
public class ActionIconDrawable extends VectorDrawable {
    private static final int[] STATE_DISABLED = {-16842910};
    private static final int[] STATE_PRESSED = {android.R.attr.state_enabled, android.R.attr.state_pressed};
    private int mActionIconHeight;
    private int mActionIconWidth;
    private float mNormalAlpha = 0.8f;
    private float mPressedAlpha = 0.5f;
    private float mDisabledAlpha = 0.3f;

    @Override // android.graphics.drawable.VectorDrawable, android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        return null;
    }

    @Override // android.graphics.drawable.VectorDrawable, android.graphics.drawable.Drawable
    public boolean isStateful() {
        return true;
    }

    @Override // android.graphics.drawable.VectorDrawable, android.graphics.drawable.Drawable
    public void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, Resources.Theme theme) throws XmlPullParserException, IOException {
        init(resources, attributeSet, theme);
        super.inflate(resources, xmlPullParser, attributeSet, theme);
    }

    private void init(Resources resources, AttributeSet attributeSet, Resources.Theme theme) {
        TypedArray typedArrayObtainAttributes;
        if (theme != null) {
            typedArrayObtainAttributes = theme.obtainStyledAttributes(attributeSet, R.styleable.ActionIconDrawable, 0, 0);
        } else {
            typedArrayObtainAttributes = resources.obtainAttributes(attributeSet, R.styleable.ActionIconDrawable);
        }
        this.mNormalAlpha = typedArrayObtainAttributes.getFloat(R.styleable.ActionIconDrawable_actionIconNormalAlpha, 0.0f);
        this.mPressedAlpha = typedArrayObtainAttributes.getFloat(R.styleable.ActionIconDrawable_actionIconPressedAlpha, 0.0f);
        this.mDisabledAlpha = typedArrayObtainAttributes.getFloat(R.styleable.ActionIconDrawable_actionIconDisabledAlpha, 0.0f);
        this.mActionIconWidth = typedArrayObtainAttributes.getDimensionPixelSize(R.styleable.ActionIconDrawable_actionIconWidth, 0);
        this.mActionIconHeight = typedArrayObtainAttributes.getDimensionPixelSize(R.styleable.ActionIconDrawable_actionIconHeight, 0);
        typedArrayObtainAttributes.recycle();
        setAlphaF(this.mNormalAlpha);
    }

    @Override // android.graphics.drawable.VectorDrawable, android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        int i = this.mActionIconWidth;
        return i == 0 ? super.getIntrinsicWidth() : i;
    }

    @Override // android.graphics.drawable.VectorDrawable, android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        int i = this.mActionIconHeight;
        return i == 0 ? super.getIntrinsicHeight() : i;
    }

    @Override // android.graphics.drawable.VectorDrawable, android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        int i = this.mActionIconWidth;
        if (i != 0 && this.mActionIconHeight != 0) {
            canvas.translate((i - super.getIntrinsicWidth()) >> 1, (this.mActionIconHeight - super.getIntrinsicHeight()) >> 1);
            canvas.scale(super.getIntrinsicWidth() / this.mActionIconWidth, super.getIntrinsicHeight() / this.mActionIconHeight, 0.5f, 0.5f);
        }
        super.draw(canvas);
    }

    @Override // android.graphics.drawable.VectorDrawable, android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] iArr) {
        super.onStateChange(iArr);
        if (StateSet.stateSetMatches(STATE_DISABLED, iArr)) {
            return toDisabledState();
        }
        if (StateSet.stateSetMatches(STATE_PRESSED, iArr)) {
            return toPressedState();
        }
        return toNormalState();
    }

    private boolean toDisabledState() {
        setAlphaF(this.mDisabledAlpha);
        return true;
    }

    private boolean toPressedState() {
        setAlphaF(this.mPressedAlpha);
        return true;
    }

    private boolean toNormalState() {
        setAlphaF(this.mNormalAlpha);
        return true;
    }

    private void setAlphaF(float f) {
        setAlpha((int) (f * 255.0f));
    }
}
