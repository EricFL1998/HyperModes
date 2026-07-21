package miuix.theme.symbol;

import android.R;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.StateSet;
import androidx.core.graphics.drawable.DrawableCompat;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes3.dex */
public class SymbolDrawable extends Drawable {
    private static final int[] STATE_DISABLED = {-16842910};
    private static final int[] STATE_PRESSED = {R.attr.state_enabled, R.attr.state_pressed};
    private boolean autoMirroredCompat;
    private SymbolPaint<Paint> backgroundBrush;
    private SymbolPaint<Paint> backgroundContourBrush;
    private int backgroundContourWidthPx;
    private int compatAlpha;
    private SymbolPaint<Paint> contourBrush;
    private int contourWidthPx;
    private float disabledAlpha;
    private boolean drawBackgroundContour;
    private boolean drawContour;
    private int drawIconColor;
    private int effectiveIconColor;
    private int effectiveShadowColor;
    private String fontPath;
    private SymbolPaint<TextPaint> iconBrush;
    private ColorFilter iconColorFilter;
    private int iconHeight;
    private int iconOffsetXPx;
    private int iconOffsetYPx;
    private int iconSize;
    private String iconText;
    private int iconWidth;
    private boolean invalidateShadowEnabled;
    private boolean invalidationEnabled;
    private float normalAlpha;
    private Rect paddingBounds;
    private int paddingPx;
    private float pressedAlpha;
    private Resources res;
    private boolean respectFontBounds;
    private float roundedCornerRxPx;
    private float roundedCornerRyPx;
    private ColorStateList shadowColor;
    private float shadowDx;
    private float shadowDy;
    private float shadowRadius;
    private int sizeXPx;
    private int sizeYPx;
    private Rect textBound;
    private Point textBoundOffset;
    private Resources.Theme theme;
    private ColorStateList tint;
    private ColorFilter tintFilter;
    private PorterDuff.Mode tintPorterMode;
    private Typeface typeface;

    @Override // android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        return null;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        return true;
    }

    public SymbolDrawable() {
        this.iconBrush = new SymbolPaint<>(new TextPaint(1));
        this.backgroundContourBrush = new SymbolPaint<>(new Paint(1));
        this.backgroundBrush = new SymbolPaint<>(new Paint(1));
        this.contourBrush = new SymbolPaint<>(new Paint(1));
        this.paddingBounds = new Rect();
        this.textBound = new Rect();
        this.textBoundOffset = new Point();
        this.tintPorterMode = PorterDuff.Mode.SRC_IN;
        this.effectiveIconColor = 0;
        this.drawIconColor = 0;
        this.compatAlpha = 255;
        this.autoMirroredCompat = false;
        this.invalidationEnabled = true;
        this.invalidateShadowEnabled = true;
        this.sizeXPx = -1;
        this.sizeYPx = -1;
        this.respectFontBounds = false;
        this.drawContour = false;
        this.drawBackgroundContour = false;
        this.roundedCornerRxPx = -1.0f;
        this.roundedCornerRyPx = -1.0f;
        this.paddingPx = 0;
        this.contourWidthPx = 0;
        this.backgroundContourWidthPx = 0;
        this.iconOffsetXPx = 0;
        this.iconOffsetYPx = 0;
        this.shadowRadius = 0.0f;
        this.shadowDx = 0.0f;
        this.shadowDy = 0.0f;
        this.normalAlpha = 0.8f;
        this.pressedAlpha = 0.5f;
        this.disabledAlpha = 0.3f;
        this.typeface = null;
    }

    public SymbolDrawable(Context context, String str, Typeface typeface, int i, int i2, ColorStateList colorStateList) {
        this(context.getResources(), context.getTheme(), str, typeface, i, i, i2, colorStateList);
    }

    public SymbolDrawable(Resources resources, Resources.Theme theme, String str, Typeface typeface, int i, int i2, int i3, ColorStateList colorStateList) {
        this.iconBrush = new SymbolPaint<>(new TextPaint(1));
        this.backgroundContourBrush = new SymbolPaint<>(new Paint(1));
        this.backgroundBrush = new SymbolPaint<>(new Paint(1));
        this.contourBrush = new SymbolPaint<>(new Paint(1));
        this.paddingBounds = new Rect();
        this.textBound = new Rect();
        this.textBoundOffset = new Point();
        this.tintPorterMode = PorterDuff.Mode.SRC_IN;
        this.effectiveIconColor = 0;
        this.drawIconColor = 0;
        this.compatAlpha = 255;
        this.autoMirroredCompat = false;
        this.invalidationEnabled = true;
        this.invalidateShadowEnabled = true;
        this.sizeXPx = -1;
        this.sizeYPx = -1;
        this.respectFontBounds = false;
        this.drawContour = false;
        this.drawBackgroundContour = false;
        this.roundedCornerRxPx = -1.0f;
        this.roundedCornerRyPx = -1.0f;
        this.paddingPx = 0;
        this.contourWidthPx = 0;
        this.backgroundContourWidthPx = 0;
        this.iconOffsetXPx = 0;
        this.iconOffsetYPx = 0;
        this.shadowRadius = 0.0f;
        this.shadowDx = 0.0f;
        this.shadowDy = 0.0f;
        this.normalAlpha = 0.8f;
        this.pressedAlpha = 0.5f;
        this.disabledAlpha = 0.3f;
        this.res = resources;
        this.theme = theme;
        this.iconText = str;
        this.typeface = typeface;
        if (i > 0) {
            this.iconWidth = i;
        }
        if (i2 > 0) {
            this.iconHeight = i2;
        }
        if (i3 > 0) {
            this.iconSize = i3;
        }
        this.tint = colorStateList;
        initBrushes(typeface);
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, Resources.Theme theme) throws XmlPullParserException, IOException {
        init(resources, attributeSet, theme);
        super.inflate(resources, xmlPullParser, attributeSet, theme);
    }

    private void init(Resources resources, AttributeSet attributeSet, Resources.Theme theme) {
        TypedArray typedArrayObtainAttributes;
        this.res = resources;
        this.theme = theme;
        if (theme != null) {
            typedArrayObtainAttributes = theme.obtainStyledAttributes(attributeSet, miuix.theme.R.styleable.SymbolDrawable, 0, 0);
        } else {
            typedArrayObtainAttributes = resources.obtainAttributes(attributeSet, miuix.theme.R.styleable.SymbolDrawable);
        }
        this.tint = typedArrayObtainAttributes.getColorStateList(miuix.theme.R.styleable.SymbolDrawable_symbolTintColor);
        this.fontPath = typedArrayObtainAttributes.getString(miuix.theme.R.styleable.SymbolDrawable_android_fontFamily);
        this.shadowColor = typedArrayObtainAttributes.getColorStateList(miuix.theme.R.styleable.SymbolDrawable_symbolShadowColor);
        this.shadowDx = typedArrayObtainAttributes.getDimension(miuix.theme.R.styleable.SymbolDrawable_symbolShadowDx, 0.0f);
        this.shadowDy = typedArrayObtainAttributes.getDimension(miuix.theme.R.styleable.SymbolDrawable_symbolShadowDy, 0.0f);
        this.shadowRadius = typedArrayObtainAttributes.getDimension(miuix.theme.R.styleable.SymbolDrawable_symbolShadowRadius, 0.0f);
        this.normalAlpha = typedArrayObtainAttributes.getFloat(miuix.theme.R.styleable.SymbolDrawable_symbolNormalAlpha, 1.0f);
        this.pressedAlpha = typedArrayObtainAttributes.getFloat(miuix.theme.R.styleable.SymbolDrawable_symbolPressedAlpha, 0.0f);
        this.disabledAlpha = typedArrayObtainAttributes.getFloat(miuix.theme.R.styleable.SymbolDrawable_symbolDisabledAlpha, 0.0f);
        this.iconWidth = typedArrayObtainAttributes.getDimensionPixelSize(miuix.theme.R.styleable.SymbolDrawable_symbolIconWidth, 0);
        this.iconHeight = typedArrayObtainAttributes.getDimensionPixelSize(miuix.theme.R.styleable.SymbolDrawable_symbolIconHeight, 0);
        this.iconSize = typedArrayObtainAttributes.getDimensionPixelSize(miuix.theme.R.styleable.SymbolDrawable_symbolIconSize, 0);
        this.autoMirroredCompat = typedArrayObtainAttributes.getBoolean(miuix.theme.R.styleable.SymbolDrawable_symbolAutoMirroredCompat, false);
        if (typedArrayObtainAttributes.hasValue(miuix.theme.R.styleable.SymbolDrawable_symbolText)) {
            this.iconText = typedArrayObtainAttributes.getString(miuix.theme.R.styleable.SymbolDrawable_symbolText);
        }
        String str = "'wght' " + HyperSymbolFont.getWeightByConfig(resources.getConfiguration());
        try {
            AssetManager assets = resources.getAssets();
            String str2 = this.fontPath;
            if (str2 == null) {
                str2 = "fonts/misymbol_vf.ttf";
            }
            this.typeface = new Typeface.Builder(assets, str2).setFontVariationSettings(str).build();
        } catch (Exception e) {
            Log.w("MiuixSymbol", "Warning!! fontPath=" + this.fontPath + " build typeface failed: " + e);
            this.typeface = new Typeface.Builder("fonts/misymbol_vf.ttf").setFontVariationSettings(str).build();
        }
        typedArrayObtainAttributes.recycle();
        int i = this.iconHeight;
        if (i > 0 || this.iconWidth > 0) {
            this.paddingPx = (i - this.iconSize) / 2;
        }
        initBrushes(this.typeface);
        if (this.tint != null) {
            updateTintColor();
        }
        setAlphaF(this.normalAlpha);
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        int i = this.iconWidth;
        return i == 0 ? super.getIntrinsicWidth() : i;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        int i = this.iconHeight;
        return i == 0 ? super.getIntrinsicHeight() : i;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean setState(int[] iArr) {
        if (iArr != null) {
            return super.setState(iArr);
        }
        return false;
    }

    private void initBrushes(Typeface typeface) {
        ((TextPaint) this.iconBrush.getPaint()).setTypeface(typeface);
        ((TextPaint) this.iconBrush.getPaint()).setStyle(Paint.Style.FILL);
        ((TextPaint) this.iconBrush.getPaint()).setTextAlign(Paint.Align.LEFT);
        ((TextPaint) this.iconBrush.getPaint()).setUnderlineText(false);
        updateShadow();
        this.contourBrush.getPaint().setStyle(Paint.Style.STROKE);
        this.backgroundContourBrush.getPaint().setStyle(Paint.Style.STROKE);
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        if (this.iconText == null) {
            return;
        }
        Rect bounds = getBounds();
        updatePaddingBounds(bounds);
        updateTextBounds();
        offsetIcon(bounds);
        if (needMirroring()) {
            canvas.translate(bounds.right - bounds.left, bounds.top);
            canvas.scale(-1.0f, 1.0f);
        } else {
            canvas.translate(bounds.left, bounds.top);
        }
        if (this.roundedCornerRyPx > -1.0f && this.roundedCornerRxPx > -1.0f) {
            if (this.drawBackgroundContour) {
                float f = this.backgroundContourWidthPx / 2.0f;
                RectF rectF = new RectF(f, f, bounds.width() - f, bounds.height() - f);
                canvas.drawRoundRect(rectF, this.roundedCornerRxPx, this.roundedCornerRyPx, this.backgroundBrush.getPaint());
                canvas.drawRoundRect(rectF, this.roundedCornerRxPx, this.roundedCornerRyPx, this.backgroundContourBrush.getPaint());
            } else {
                canvas.drawRoundRect(new RectF(0.0f, 0.0f, bounds.width(), bounds.height()), this.roundedCornerRxPx, this.roundedCornerRyPx, this.backgroundBrush.getPaint());
            }
        }
        if (this.iconColorFilter != null) {
            ((TextPaint) this.iconBrush.getPaint()).setColorFilter(this.iconColorFilter);
        } else {
            ((TextPaint) this.iconBrush.getPaint()).setColorFilter(null);
            if (this.effectiveIconColor != 0) {
                ((TextPaint) this.iconBrush.getPaint()).setColor(updateColorWithAlpha(this.effectiveIconColor, this.compatAlpha));
            }
        }
        if (this.respectFontBounds) {
            String str = this.iconText;
            canvas.drawText(str, 0, str.length(), this.textBoundOffset.x, (-this.textBound.top) + this.textBoundOffset.y, this.iconBrush.getPaint());
        } else {
            String str2 = this.iconText;
            canvas.drawText(str2, 0, str2.length(), this.textBoundOffset.x, this.textBound.height() + this.textBoundOffset.y, this.iconBrush.getPaint());
        }
        if (needMirroring()) {
            canvas.translate(-(bounds.right - bounds.left), -bounds.top);
            canvas.scale(1.0f, -1.0f);
        } else {
            canvas.translate(-bounds.left, -bounds.top);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        this.iconBrush.setAlpha(i);
        this.contourBrush.setAlpha(i);
        this.backgroundBrush.setAlpha(i);
        this.backgroundContourBrush.setAlpha(i);
        this.compatAlpha = i;
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        return this.compatAlpha;
    }

    @Override // android.graphics.drawable.Drawable
    public void setTintList(ColorStateList colorStateList) {
        if (this.tint != colorStateList) {
            this.tint = colorStateList;
            updateTintColor();
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.iconColorFilter = colorFilter;
    }

    @Override // android.graphics.drawable.Drawable
    public void clearColorFilter() {
        this.iconColorFilter = null;
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        if (this.tintFilter == null && this.iconColorFilter == null) {
            int i = this.compatAlpha;
            if (i == 255) {
                return -1;
            }
            if (i == 0) {
                return -2;
            }
        }
        return -3;
    }

    public void setShadowColor(int i) {
        this.shadowColor = ColorStateList.valueOf(i);
        updateShadow();
        invalidateSelf();
    }

    public void setShadowColor(ColorStateList colorStateList) {
        if (this.shadowColor != colorStateList) {
            this.shadowColor = colorStateList;
            updateShadow();
            invalidateSelf();
        }
    }

    private int updateColorWithAlpha(int i, int i2) {
        return (i & 16777215) | ((((i >>> 24) * i2) / 255) << 24);
    }

    public void setShadowDx(float f) {
        if (this.shadowDx != f) {
            this.shadowDx = f;
            updateShadow();
            invalidateSelf();
        }
    }

    public void setShadowDy(float f) {
        if (this.shadowDy != f) {
            this.shadowDy = f;
            updateShadow();
            invalidateSelf();
        }
    }

    public void setShadowRadius(float f) {
        if (this.shadowRadius != f) {
            this.shadowRadius = f;
            updateShadow();
            invalidateSelf();
        }
    }

    public void setShadow(int i, float f, float f2, float f3) {
        this.shadowColor = ColorStateList.valueOf(i);
        this.shadowDx = f;
        this.shadowDy = f2;
        this.shadowRadius = f3;
        updateShadow();
        invalidateSelf();
    }

    public void setShadow(ColorStateList colorStateList, float f, float f2, float f3) {
        this.shadowColor = colorStateList;
        this.shadowDx = f;
        this.shadowDy = f2;
        this.shadowRadius = f3;
        updateShadow();
        invalidateSelf();
    }

    private void updatePaddingBounds(Rect rect) {
        int i = this.paddingPx;
        if (i < 0 || i * 2 > rect.width() || this.paddingPx * 2 > rect.height()) {
            return;
        }
        this.paddingBounds.set(rect.left + this.paddingPx, rect.top + this.paddingPx, rect.right - this.paddingPx, rect.bottom - this.paddingPx);
    }

    private void updateTextBounds() {
        String str = this.iconText;
        int iHeight = this.iconSize;
        if (iHeight <= 0) {
            iHeight = this.paddingBounds.height();
        }
        ((TextPaint) this.iconBrush.getPaint()).setTextSize(iHeight);
        ((TextPaint) this.iconBrush.getPaint()).getTextBounds(str, 0, str.length(), this.textBound);
    }

    private void offsetIcon(Rect rect) {
        if (this.respectFontBounds) {
            this.textBoundOffset.set(this.iconOffsetXPx, this.iconOffsetYPx);
            return;
        }
        this.textBoundOffset.set((((rect.width() - this.textBound.width()) / 2) + this.iconOffsetXPx) - this.textBound.left, (((rect.height() - this.textBound.height()) / 2) + this.iconOffsetYPx) - this.textBound.bottom);
    }

    private boolean needMirroring() {
        return this.autoMirroredCompat && DrawableCompat.getLayoutDirection(this) == 1;
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] iArr) {
        boolean z = true;
        boolean z2 = this.backgroundContourBrush.applyState(iArr) || (this.backgroundBrush.applyState(iArr) || (this.contourBrush.applyState(iArr) || this.iconBrush.applyState(iArr)));
        if (this.tint != null) {
            updateTintColor();
        } else {
            z = z2;
        }
        updateShadow();
        if (StateSet.stateSetMatches(STATE_DISABLED, iArr)) {
            toDisabledState();
            return z;
        }
        if (StateSet.stateSetMatches(STATE_PRESSED, iArr)) {
            toPressedState();
            return z;
        }
        toNormalState();
        return z;
    }

    private boolean toDisabledState() {
        setAlphaF(this.disabledAlpha);
        return true;
    }

    private boolean toPressedState() {
        setAlphaF(this.pressedAlpha);
        return true;
    }

    private boolean toNormalState() {
        setAlphaF(this.normalAlpha);
        return true;
    }

    private void setAlphaF(float f) {
        setAlpha((int) (f * 255.0f));
    }

    private void updateTintColor() {
        ColorStateList colorStateList = this.tint;
        if (colorStateList == null) {
            this.tintFilter = null;
        } else {
            this.effectiveIconColor = colorStateList.getColorForState(getState(), 0);
        }
    }

    private void updateShadow() {
        ColorStateList colorStateList = this.shadowColor;
        if (colorStateList == null) {
            ((TextPaint) this.iconBrush.getPaint()).clearShadowLayer();
            return;
        }
        int colorForState = colorStateList.getColorForState(getState(), this.shadowColor.getDefaultColor());
        this.effectiveShadowColor = colorForState;
        if (colorForState == 0) {
            ((TextPaint) this.iconBrush.getPaint()).clearShadowLayer();
        } else {
            ((TextPaint) this.iconBrush.getPaint()).setShadowLayer(this.shadowRadius, this.shadowDx, this.shadowDy, this.effectiveShadowColor);
        }
    }

    public SymbolDrawable copy(SymbolDrawable symbolDrawable, Resources resources, Resources.Theme theme, Typeface typeface, int i, int i2, int i3, int i4, String str, boolean z, int i5, int i6, boolean z2, boolean z3, boolean z4, float f, float f2, int i7, int i8, int i9, int i10, int i11, float f3, float f4, float f5, ColorStateList colorStateList, ColorStateList colorStateList2, PorterDuff.Mode mode, ColorFilter colorFilter) {
        SymbolDrawable symbolDrawable2 = symbolDrawable != null ? symbolDrawable : new SymbolDrawable(resources, theme, str, typeface, i, i2, i3, colorStateList2);
        symbolDrawable2.compatAlpha = i4 != 0 ? i4 : this.compatAlpha;
        symbolDrawable2.iconText = str != null ? str : this.iconText;
        symbolDrawable2.autoMirroredCompat = z;
        int i12 = i5;
        if (i12 == -1) {
            i12 = this.sizeXPx;
        }
        symbolDrawable2.sizeXPx = i12;
        int i13 = i6;
        if (i13 == -1) {
            i13 = this.sizeYPx;
        }
        symbolDrawable2.sizeYPx = i13;
        symbolDrawable2.respectFontBounds = z2;
        symbolDrawable2.drawContour = z3;
        symbolDrawable2.drawBackgroundContour = z4;
        symbolDrawable2.roundedCornerRxPx = f != -1.0f ? f : this.roundedCornerRxPx;
        symbolDrawable2.roundedCornerRyPx = f2 != -1.0f ? f2 : this.roundedCornerRyPx;
        symbolDrawable2.paddingPx = i7 != 0 ? i7 : this.paddingPx;
        symbolDrawable2.contourWidthPx = i8 != 0 ? i8 : this.contourWidthPx;
        symbolDrawable2.backgroundContourWidthPx = i9 != 0 ? i9 : this.backgroundContourWidthPx;
        symbolDrawable2.iconOffsetXPx = i10 != 0 ? i10 : this.iconOffsetXPx;
        symbolDrawable2.iconOffsetYPx = i11 != 0 ? i11 : this.iconOffsetYPx;
        symbolDrawable2.shadowRadius = f3 != 0.0f ? f3 : this.shadowRadius;
        symbolDrawable2.shadowDx = f4 != 0.0f ? f4 : this.shadowDx;
        symbolDrawable2.shadowDy = f5 != 0.0f ? f5 : this.shadowDy;
        symbolDrawable2.shadowColor = colorStateList != null ? colorStateList : this.shadowColor;
        symbolDrawable2.tint = colorStateList2 != null ? colorStateList2 : this.tint;
        symbolDrawable2.tintPorterMode = mode != null ? mode : this.tintPorterMode;
        symbolDrawable2.iconColorFilter = colorFilter != null ? colorFilter : this.iconColorFilter;
        return symbolDrawable2;
    }
}
