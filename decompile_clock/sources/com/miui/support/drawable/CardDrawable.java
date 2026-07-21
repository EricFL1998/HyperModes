package com.miui.support.drawable;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import androidx.core.view.ViewCompat;
import com.miui.support.cardview.R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes2.dex */
public class CardDrawable extends CardStateDrawable {
    private int mCardColor;
    private int mCardPaddingBottom;
    private int mCardPaddingLeft;
    private int mCardPaddingRight;
    private int mCardPaddingTop;
    private int mCardRadius;
    private CardState mCardState;
    private boolean mIsSupportOutline;
    private final Rect mPadding;
    private final Paint mPaint;

    @Override // com.miui.support.drawable.CardStateDrawable, android.graphics.drawable.Drawable
    public int getOpacity() {
        return 0;
    }

    public CardDrawable() {
        this.mPaint = new Paint();
        this.mPadding = new Rect();
        this.mIsSupportOutline = true;
        this.mCardState = new CardState();
    }

    public CardDrawable(CardState cardState, Resources resources) {
        super(cardState, resources);
        this.mPaint = new Paint();
        this.mPadding = new Rect();
        this.mIsSupportOutline = true;
        this.mCardState = new CardState(cardState);
        initCardDrawable(cardState);
        cacheCardDrawable();
    }

    private void updateParentState() {
        this.mCardState.mTintColor = this.mTintColor;
        this.mCardState.mRadius = this.mRadius;
        this.mCardState.mNormalAlpha = this.mNormalAlpha;
        this.mCardState.mPressedAlpha = this.mPressedAlpha;
        this.mCardState.mHoveredAlpha = this.mHoveredAlpha;
        this.mCardState.mFocusedAlpha = this.mFocusedAlpha;
        this.mCardState.mActivatedAlpha = this.mActivatedAlpha;
        this.mCardState.mCheckedAlpha = this.mCheckedAlpha;
        this.mCardState.mHoveredCheckedAlpha = this.mHoveredCheckedAlpha;
    }

    private void cacheCardDrawable() {
        this.mCardState.color = this.mCardColor;
        this.mCardState.radius = this.mCardRadius;
        this.mCardState.paddingLeft = this.mCardPaddingLeft;
        this.mCardState.paddingTop = this.mCardPaddingTop;
        this.mCardState.paddingRight = this.mCardPaddingRight;
        this.mCardState.paddingBottom = this.mCardPaddingBottom;
        this.mCardState.radiusMode = this.mCardRadiusMode;
        this.mCardState.isSupportOutline = this.mIsSupportOutline;
        updateParentState();
    }

    @Override // com.miui.support.drawable.CardStateDrawable, android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        if (isVisible()) {
            this.mPath.reset();
            this.mPath.addRoundRect(this.mRect, this.mAllRadii, Path.Direction.CW);
            canvas.drawPath(this.mPath, this.mPaint);
        }
        super.draw(canvas);
    }

    @Override // android.graphics.drawable.Drawable
    public boolean getPadding(Rect rect) {
        rect.set(this.mPadding);
        return true;
    }

    @Override // com.miui.support.drawable.CardStateDrawable, android.graphics.drawable.Drawable
    public void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, Resources.Theme theme) throws XmlPullParserException, IOException {
        TypedArray typedArrayObtainAttributes;
        super.inflate(resources, xmlPullParser, attributeSet, theme);
        if (theme != null) {
            typedArrayObtainAttributes = theme.obtainStyledAttributes(attributeSet, R.styleable.CardDrawable, 0, 0);
        } else {
            typedArrayObtainAttributes = resources.obtainAttributes(attributeSet, R.styleable.CardDrawable);
        }
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mCardColor = typedArrayObtainAttributes.getColor(R.styleable.CardDrawable_backgroundColor, ViewCompat.MEASURED_STATE_MASK);
        this.mCardPaddingLeft = typedArrayObtainAttributes.getDimensionPixelSize(R.styleable.CardDrawable_paddingLeft, 0);
        this.mCardPaddingRight = typedArrayObtainAttributes.getDimensionPixelSize(R.styleable.CardDrawable_paddingRight, 0);
        this.mCardPaddingTop = typedArrayObtainAttributes.getDimensionPixelSize(R.styleable.CardDrawable_paddingTop, 0);
        this.mCardPaddingBottom = typedArrayObtainAttributes.getDimensionPixelSize(R.styleable.CardDrawable_paddingBottom, 0);
        this.mCardRadius = typedArrayObtainAttributes.getDimensionPixelSize(R.styleable.CardDrawable_cardRadius, 0);
        this.mCardRadiusMode = typedArrayObtainAttributes.getInteger(R.styleable.CardDrawable_radiusMode, 0);
        this.mIsSupportOutline = typedArrayObtainAttributes.getBoolean(R.styleable.CardDrawable_supportOutline, true);
        this.mPadding.set(this.mCardPaddingLeft, this.mCardPaddingTop, this.mCardPaddingRight, this.mCardPaddingBottom);
        this.mPaint.setColor(this.mCardColor);
        setRadiusMode(this.mCardRadius, this.mCardRadiusMode);
        cacheCardDrawable();
        typedArrayObtainAttributes.recycle();
    }

    private void initCardDrawable(CardState cardState) {
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mCardColor = cardState.color;
        this.mCardPaddingLeft = cardState.paddingLeft;
        this.mCardPaddingRight = cardState.paddingRight;
        this.mCardPaddingTop = cardState.paddingTop;
        this.mCardPaddingBottom = cardState.paddingBottom;
        this.mCardRadius = cardState.radius;
        this.mCardRadiusMode = cardState.radiusMode;
        this.mIsSupportOutline = cardState.isSupportOutline;
        this.mPadding.set(this.mCardPaddingLeft, this.mCardPaddingTop, this.mCardPaddingRight, this.mCardPaddingBottom);
        this.mPaint.setColor(this.mCardColor);
        setRadiusMode(this.mCardRadius, this.mCardRadiusMode);
    }

    public void setRadiusAndRoundMode(int i, int i2) {
        this.mCardRadius = i;
        this.mCardRadiusMode = i2;
        setRadiusMode(i, i2);
        invalidateSelf();
    }

    public void setCardBackgroundColor(int i) {
        this.mCardColor = i;
        this.mPaint.setColor(i);
        invalidateSelf();
    }

    public void setPadding(Rect rect) {
        if (rect != null) {
            this.mCardPaddingLeft = rect.left;
            this.mCardPaddingTop = rect.top;
            this.mCardPaddingRight = rect.right;
            this.mCardPaddingBottom = rect.bottom;
            this.mPadding.set(rect);
            invalidateSelf();
        }
    }

    public void setIsSupportOutline(boolean z) {
        this.mIsSupportOutline = z;
    }

    @Override // com.miui.support.drawable.CardStateDrawable, android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        return this.mCardState;
    }

    @Override // android.graphics.drawable.Drawable
    public void getOutline(Outline outline) {
        if (!this.mIsSupportOutline) {
            super.getOutline(outline);
        } else if (Build.VERSION.SDK_INT >= 30) {
            outline.setPath(this.mPath);
        } else {
            outline.setRoundRect(getBounds(), this.mCardRadius);
        }
    }

    public static final class CardState extends CardStateDrawable.AlphaBlendingState {
        int color;
        boolean isSupportOutline;
        int paddingBottom;
        int paddingLeft;
        int paddingRight;
        int paddingTop;
        int radius;
        int radiusMode;

        @Override // com.miui.support.drawable.CardStateDrawable.AlphaBlendingState, android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            return 0;
        }

        public CardState() {
            this.isSupportOutline = true;
        }

        CardState(CardState cardState) {
            super(cardState);
            this.isSupportOutline = true;
            this.color = cardState.color;
            this.paddingLeft = cardState.paddingLeft;
            this.paddingRight = cardState.paddingRight;
            this.paddingTop = cardState.paddingTop;
            this.paddingBottom = cardState.paddingBottom;
            this.radius = cardState.radius;
            this.radiusMode = cardState.radiusMode;
            this.isSupportOutline = cardState.isSupportOutline;
        }

        @Override // com.miui.support.drawable.CardStateDrawable.AlphaBlendingState, android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new CardDrawable();
        }

        @Override // com.miui.support.drawable.CardStateDrawable.AlphaBlendingState, android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources resources) {
            return new CardDrawable(new CardState(this), resources);
        }
    }

    public static class Builder {
        private float mActivatedAlpha;
        private int mBackgroundColor;
        private int mCardRadius;
        private float mCheckedAlpha;
        private float mFocusedAlpha;
        private float mHoveredAlpha;
        private float mHoveredCheckedAlpha;
        private boolean mIsSupportOutline = true;
        private float mNormalAlpha;
        private Rect mPadding;
        private float mPressedAlpha;
        private int mRadiusRoundMode;
        private int mTintColor;

        public Builder setTintColor(int i) {
            this.mTintColor = i;
            return this;
        }

        public Builder setNormalAlpha(float f) {
            this.mNormalAlpha = f;
            return this;
        }

        public Builder setPressedAlpha(float f) {
            this.mPressedAlpha = f;
            return this;
        }

        public Builder setHoveredAlpha(float f) {
            this.mHoveredAlpha = f;
            return this;
        }

        public Builder setActivatedAlpha(float f) {
            this.mActivatedAlpha = f;
            return this;
        }

        public Builder setCheckedAlpha(float f) {
            this.mCheckedAlpha = f;
            return this;
        }

        public Builder setHoveredCheckedAlpha(float f) {
            this.mHoveredCheckedAlpha = f;
            return this;
        }

        public Builder setFocusedAlpha(float f) {
            this.mFocusedAlpha = f;
            return this;
        }

        public Builder setRadiusAndRoundMode(int i, int i2) {
            this.mCardRadius = i;
            this.mRadiusRoundMode = i2;
            return this;
        }

        public Builder setBackgroundColor(int i) {
            this.mBackgroundColor = i;
            return this;
        }

        public Builder setIsSupportOutline(boolean z) {
            this.mIsSupportOutline = z;
            return this;
        }

        public Builder setPadding(Rect rect) {
            this.mPadding = rect;
            return this;
        }

        public CardDrawable build() {
            CardDrawable cardDrawable = new CardDrawable();
            cardDrawable.setTintColor(this.mTintColor);
            cardDrawable.setNormalAlpha(this.mNormalAlpha);
            cardDrawable.setPressedAlpha(this.mPressedAlpha);
            cardDrawable.setHoveredAlpha(this.mHoveredAlpha);
            cardDrawable.setActivatedAlpha(this.mActivatedAlpha);
            cardDrawable.setCheckedAlpha(this.mCheckedAlpha);
            cardDrawable.setHoveredCheckedAlpha(this.mHoveredCheckedAlpha);
            cardDrawable.setFocusedAlpha(this.mFocusedAlpha);
            cardDrawable.setCardBackgroundColor(this.mBackgroundColor);
            cardDrawable.setRadiusAndRoundMode(this.mCardRadius, this.mRadiusRoundMode);
            cardDrawable.setIsSupportOutline(this.mIsSupportOutline);
            cardDrawable.setPadding(this.mPadding);
            cardDrawable.initState();
            return cardDrawable;
        }
    }
}
