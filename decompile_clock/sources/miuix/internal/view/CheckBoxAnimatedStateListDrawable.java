package miuix.internal.view;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import miuix.appcompat.R;

/* JADX INFO: loaded from: classes2.dex */
public class CheckBoxAnimatedStateListDrawable extends CheckWidgetAnimatedStateListDrawable {
    protected static final int FULL_ALPHA = 255;
    protected static final int ONE_THIRD_ALPHA = 76;
    private static final String TAG = "MiuixCheckbox";
    private CheckWidgetDrawableAnims mCheckWidgetDrawableAnims;
    private float mContentAlpha;
    private boolean mIsEnabled;
    private boolean mPreChecked;
    private boolean mPrePressed;
    private float mScale;

    protected boolean isSingleSelectionWidget() {
        return false;
    }

    @Override // android.graphics.drawable.AnimatedStateListDrawable, android.graphics.drawable.StateListDrawable, android.graphics.drawable.DrawableContainer, android.graphics.drawable.Drawable
    public void applyTheme(Resources.Theme theme) {
        int color;
        super.applyTheme(theme);
        TypedArray typedArrayObtainStyledAttributes = theme.obtainStyledAttributes(getCheckWidgetDrawableStyle(), R.styleable.CheckWidgetDrawable);
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(android.R.attr.isLightTheme, typedValue, true);
        boolean zEquals = "true".equals(TypedValue.coerceToString(typedValue.type, typedValue.data));
        if (!zEquals) {
            color = Color.parseColor("#ffffff");
        } else {
            color = Color.parseColor("#000000");
        }
        this.mCheckWidgetConstantState.grayColor = safeGetColor(typedArrayObtainStyledAttributes, R.styleable.CheckWidgetDrawable_checkwidget_disableBackgroundColor, color);
        this.mCheckWidgetConstantState.blackColor = safeGetColor(typedArrayObtainStyledAttributes, R.styleable.CheckWidgetDrawable_checkwidget_checkOnAlphaBackgroundColor, color);
        this.mCheckWidgetConstantState.backGroundColor = safeGetColor(typedArrayObtainStyledAttributes, R.styleable.CheckWidgetDrawable_checkwidget_checkOnBackgroundColor, Color.parseColor(zEquals ? "#3482FF" : "#277AF7"));
        this.mCheckWidgetConstantState.strokeColor = safeGetColor(typedArrayObtainStyledAttributes, R.styleable.CheckWidgetDrawable_checkwidget_strokeColor, Color.parseColor("#ffffff"));
        this.mCheckWidgetConstantState.backgroundNormalAlpha = safeGetInt(typedArrayObtainStyledAttributes, R.styleable.CheckWidgetDrawable_checkwidget_backgroundNormalAlpha, zEquals ? 15 : 51);
        this.mCheckWidgetConstantState.backgroundDisableAlpha = safeGetInt(typedArrayObtainStyledAttributes, R.styleable.CheckWidgetDrawable_checkwidget_backgroundDisableAlpha, zEquals ? 15 : 51);
        this.mCheckWidgetConstantState.strokeNormalAlpha = safeGetInt(typedArrayObtainStyledAttributes, R.styleable.CheckWidgetDrawable_checkwidget_strokeNormalAlpha, zEquals ? 255 : 0);
        this.mCheckWidgetConstantState.strokeDisableAlpha = safeGetInt(typedArrayObtainStyledAttributes, R.styleable.CheckWidgetDrawable_checkwidget_strokeDisableAlpha, zEquals ? 255 : 0);
        this.mCheckWidgetConstantState.touchAnimEnable = safeGetBoolean(typedArrayObtainStyledAttributes, R.styleable.CheckWidgetDrawable_checkwidget_touchAnimEnable, false);
        this.mCheckWidgetConstantState.strokeWidth = typedArrayObtainStyledAttributes.getDimension(R.styleable.CheckWidgetDrawable_checkwidget_strokeWidth, 2.0f);
        this.mCheckWidgetConstantState.strokeStyle = typedArrayObtainStyledAttributes.getInt(R.styleable.CheckWidgetDrawable_checkwidget_strokeStyle, -1);
        typedArrayObtainStyledAttributes.recycle();
        this.mCheckWidgetDrawableAnims = new CheckWidgetDrawableAnims(this, isSingleSelectionWidget(), this.mCheckWidgetConstantState.grayColor, this.mCheckWidgetConstantState.blackColor, this.mCheckWidgetConstantState.backGroundColor, this.mCheckWidgetConstantState.backgroundNormalAlpha, this.mCheckWidgetConstantState.backgroundDisableAlpha, this.mCheckWidgetConstantState.strokeColor, this.mCheckWidgetConstantState.strokeNormalAlpha, this.mCheckWidgetConstantState.strokeDisableAlpha, this.mCheckWidgetConstantState.strokeWidth, this.mCheckWidgetConstantState.strokeStyle);
    }

    private int safeGetColor(TypedArray typedArray, int i, int i2) {
        try {
            return typedArray.getColor(i, i2);
        } catch (UnsupportedOperationException e) {
            Log.w(TAG, "try catch UnsupportedOperationException insafeGetColor", e);
            return i2;
        }
    }

    private int safeGetInt(TypedArray typedArray, int i, int i2) {
        try {
            return typedArray.getInt(i, i2);
        } catch (Exception e) {
            Log.w(TAG, "try catch Exception insafeGetInt", e);
            return i2;
        }
    }

    private boolean safeGetBoolean(TypedArray typedArray, int i, boolean z) {
        try {
            return typedArray.getBoolean(i, z);
        } catch (Exception e) {
            Log.w(TAG, "try catch Exception insafeGetBoolean", e);
            return z;
        }
    }

    protected int getCheckWidgetDrawableStyle() {
        return R.style.CheckWidgetDrawable_CheckBox;
    }

    public CheckBoxAnimatedStateListDrawable() {
        this.mScale = 1.0f;
        this.mContentAlpha = 1.0f;
        this.mPrePressed = false;
        this.mPreChecked = false;
    }

    public CheckBoxAnimatedStateListDrawable(Resources resources, Resources.Theme theme, CheckWidgetAnimatedStateListDrawable.CheckWidgetConstantState checkWidgetConstantState) {
        super(resources, theme, checkWidgetConstantState);
        this.mScale = 1.0f;
        this.mContentAlpha = 1.0f;
        this.mPrePressed = false;
        this.mPreChecked = false;
        this.mCheckWidgetDrawableAnims = new CheckWidgetDrawableAnims(this, isSingleSelectionWidget(), checkWidgetConstantState.grayColor, checkWidgetConstantState.blackColor, checkWidgetConstantState.backGroundColor, checkWidgetConstantState.backgroundNormalAlpha, checkWidgetConstantState.backgroundDisableAlpha, checkWidgetConstantState.strokeColor, checkWidgetConstantState.strokeNormalAlpha, checkWidgetConstantState.strokeDisableAlpha, checkWidgetConstantState.strokeWidth, checkWidgetConstantState.strokeStyle);
    }

    @Override // android.graphics.drawable.AnimatedStateListDrawable, android.graphics.drawable.StateListDrawable, android.graphics.drawable.DrawableContainer, android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] iArr) {
        boolean zOnStateChange = super.onStateChange(iArr);
        if (this.mCheckWidgetDrawableAnims == null) {
            return zOnStateChange;
        }
        Drawable current = getCurrent();
        if (current != null && (current instanceof BitmapDrawable)) {
            return super.onStateChange(iArr);
        }
        this.mIsEnabled = false;
        boolean z = false;
        boolean z2 = false;
        for (int i : iArr) {
            if (i == 16842919) {
                z = true;
            } else if (i == 16842912) {
                z2 = true;
            } else if (i == 16842910) {
                this.mIsEnabled = true;
            }
        }
        if (z) {
            startPressedAnim(z2);
        }
        if (!z) {
            verifyChecked(z2, this.mIsEnabled);
        }
        if (!z && (this.mPrePressed || z2 != this.mPreChecked)) {
            startUnPressedAnim(z2);
        }
        this.mPrePressed = z;
        this.mPreChecked = z2;
        return zOnStateChange;
    }

    protected void verifyChecked(boolean z, boolean z2) {
        CheckWidgetDrawableAnims checkWidgetDrawableAnims = this.mCheckWidgetDrawableAnims;
        if (checkWidgetDrawableAnims != null) {
            checkWidgetDrawableAnims.verifyChecked(z, z2);
            invalidateSelf();
        }
    }

    protected void startPressedAnim(boolean z) {
        CheckWidgetDrawableAnims checkWidgetDrawableAnims = this.mCheckWidgetDrawableAnims;
        if (checkWidgetDrawableAnims != null) {
            checkWidgetDrawableAnims.startPressedAnim(z, this.mCheckWidgetConstantState.touchAnimEnable);
        }
    }

    protected void startUnPressedAnim(boolean z) {
        CheckWidgetDrawableAnims checkWidgetDrawableAnims = this.mCheckWidgetDrawableAnims;
        if (checkWidgetDrawableAnims != null) {
            checkWidgetDrawableAnims.startUnPressedAnim(z, this.mCheckWidgetConstantState.touchAnimEnable);
        }
    }

    @Override // miuix.internal.view.CheckWidgetAnimatedStateListDrawable
    protected CheckWidgetAnimatedStateListDrawable.CheckWidgetConstantState newCheckWidgetConstantState() {
        return new CheckBoxConstantState();
    }

    protected static class CheckBoxConstantState extends CheckWidgetAnimatedStateListDrawable.CheckWidgetConstantState {
        protected CheckBoxConstantState() {
        }

        @Override // miuix.internal.view.CheckWidgetAnimatedStateListDrawable.CheckWidgetConstantState
        protected Drawable newAnimatedStateListDrawable(Resources resources, Resources.Theme theme, CheckWidgetAnimatedStateListDrawable.CheckWidgetConstantState checkWidgetConstantState) {
            return new CheckBoxAnimatedStateListDrawable(resources, theme, checkWidgetConstantState);
        }
    }

    @Override // android.graphics.drawable.DrawableContainer, android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        Drawable current = getCurrent();
        if (current != null && (current instanceof BitmapDrawable)) {
            super.draw(canvas);
            return;
        }
        if (!this.mCheckWidgetConstantState.touchAnimEnable) {
            CheckWidgetDrawableAnims checkWidgetDrawableAnims = this.mCheckWidgetDrawableAnims;
            if (checkWidgetDrawableAnims != null) {
                checkWidgetDrawableAnims.draw(canvas);
            }
            super.draw(canvas);
            return;
        }
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, 2));
        if (this.mIsEnabled) {
            CheckWidgetDrawableAnims checkWidgetDrawableAnims2 = this.mCheckWidgetDrawableAnims;
            if (checkWidgetDrawableAnims2 != null) {
                checkWidgetDrawableAnims2.draw(canvas);
            }
            setAlpha((int) (this.mContentAlpha * 255.0f));
        } else {
            CheckWidgetDrawableAnims checkWidgetDrawableAnims3 = this.mCheckWidgetDrawableAnims;
            if (checkWidgetDrawableAnims3 != null) {
                checkWidgetDrawableAnims3.draw(canvas);
            }
            setAlpha(76);
        }
        canvas.save();
        Rect bounds = getBounds();
        float f = this.mScale;
        canvas.scale(f, f, (bounds.left + bounds.right) / 2, (bounds.top + bounds.bottom) / 2);
        super.draw(canvas);
        canvas.restore();
    }

    public float getScale() {
        return this.mScale;
    }

    public void setScale(float f) {
        this.mScale = f;
    }

    public void setContentAlpha(float f) {
        this.mContentAlpha = f;
    }

    public float getContentAlpha() {
        return this.mContentAlpha;
    }

    @Override // android.graphics.drawable.Drawable
    public void setBounds(int i, int i2, int i3, int i4) {
        super.setBounds(i, i2, i3, i4);
        setCheckWidgetDrawableBounds(i, i2, i3, i4);
    }

    protected void setCheckWidgetDrawableBounds(int i, int i2, int i3, int i4) {
        CheckWidgetDrawableAnims checkWidgetDrawableAnims = this.mCheckWidgetDrawableAnims;
        if (checkWidgetDrawableAnims != null) {
            checkWidgetDrawableAnims.setBounds(i, i2, i3, i4);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setBounds(Rect rect) {
        super.setBounds(rect);
        setCheckWidgetDrawableBounds(rect);
    }

    protected void setCheckWidgetDrawableBounds(Rect rect) {
        CheckWidgetDrawableAnims checkWidgetDrawableAnims = this.mCheckWidgetDrawableAnims;
        if (checkWidgetDrawableAnims != null) {
            checkWidgetDrawableAnims.setBounds(rect);
        }
    }
}
