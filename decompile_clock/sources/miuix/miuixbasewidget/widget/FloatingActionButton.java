package miuix.miuixbasewidget.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import java.lang.ref.WeakReference;
import miuix.animation.Folme;
import miuix.animation.FolmeEase;
import miuix.animation.IHoverStyle;
import miuix.animation.base.AnimConfig;
import miuix.animation.property.ValueProperty;
import miuix.internal.util.AttributeResolver;
import miuix.miuixbasewidget.R;
import miuix.view.HapticCompat;
import miuix.view.HapticFeedbackConstants;

/* JADX INFO: loaded from: classes2.dex */
public class FloatingActionButton extends ImageView {
    private static final int SHADOW_ALPHA = 102;
    private static final ValueProperty<FloatingActionButton> SHADOW_ALPHA_PROPERTY = new ValueProperty<FloatingActionButton>("shadow_alpha") { // from class: miuix.miuixbasewidget.widget.FloatingActionButton.2
        @Override // miuix.animation.property.ValueProperty, miuix.animation.property.FloatProperty
        public float getValue(FloatingActionButton floatingActionButton) {
            return floatingActionButton.mFabShadowAlpha;
        }

        @Override // miuix.animation.property.ValueProperty, miuix.animation.property.FloatProperty
        public void setValue(FloatingActionButton floatingActionButton, float f) {
            floatingActionButton.updateShadowAlpha((int) f);
        }
    };
    private static final float SHADOW_RADIUS = 20.0f;
    private static final float X_OFFSET = 0.0f;
    private static final float Y_OFFSET = 16.0f;
    private Drawable mDefaultBackground;
    private EmptyHolder mEmptyHolder;
    private int mFabColor;
    private int mFabShadowAlpha;
    private int mFabShadowColor;
    private final Runnable mFolmeInitTask;
    private boolean mHasFabColor;
    private boolean mHasFabShadowColor;
    private int mImageAlpha;
    private final boolean mIsShadowEnabled;
    private final DropShadowConfig mShadowConfig;
    private BaseWidgetDropShadowHelper mShadowHelper;
    private AnimConfig mShadowHideConfig;
    private AnimConfig mShadowShowConfig;

    public FloatingActionButton(Context context) {
        this(context, null);
    }

    public FloatingActionButton(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public FloatingActionButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mImageAlpha = 255;
        this.mFabShadowAlpha = 255;
        this.mFolmeInitTask = new Runnable() { // from class: miuix.miuixbasewidget.widget.FloatingActionButton.1
            @Override // java.lang.Runnable
            public void run() {
                Folme.useAt(FloatingActionButton.this).hover().setEffect(IHoverStyle.HoverEffect.FLOATED_WRAPPED).handleHoverOf(FloatingActionButton.this, new AnimConfig[0]);
                Folme.useAt(FloatingActionButton.this).touch().setTintMode(3).setTouchRadius((float) Math.ceil(FloatingActionButton.this.getViewWidth() / 2.0f)).handleTouchOf(FloatingActionButton.this, new AnimConfig[0]);
            }
        };
        DropShadowConfig dropShadowConfigCreate = new DropShadowConfig.Builder(SHADOW_RADIUS).create();
        this.mShadowConfig = dropShadowConfigCreate;
        dropShadowConfigCreate.offsetXDp = 0.0f;
        dropShadowConfigCreate.offsetYDp = Y_OFFSET;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.FloatingActionButton, i, R.style.Widget_FloatingActionButton);
        this.mIsShadowEnabled = typedArrayObtainStyledAttributes.getBoolean(R.styleable.FloatingActionButton_fabShadowEnabled, true);
        this.mHasFabColor = typedArrayObtainStyledAttributes.hasValue(R.styleable.FloatingActionButton_fabColor);
        this.mFabColor = typedArrayObtainStyledAttributes.getColor(R.styleable.FloatingActionButton_fabColor, context.getResources().getColor(R.color.miuix_appcompat_fab_color));
        this.mHasFabShadowColor = typedArrayObtainStyledAttributes.hasValue(R.styleable.FloatingActionButton_fabShadowColor);
        this.mFabShadowColor = typedArrayObtainStyledAttributes.getColor(R.styleable.FloatingActionButton_fabShadowColor, this.mFabColor);
        typedArrayObtainStyledAttributes.recycle();
        this.mEmptyHolder = new EmptyHolder(getContext().getResources().getDrawable(R.drawable.miuix_appcompat_fab_empty_holder));
        initBackground();
    }

    private void initDropShadowHelper() {
        this.mShadowHelper = new BaseWidgetDropShadowHelper(getContext(), this.mShadowConfig, AttributeResolver.resolveBoolean(getContext(), R.attr.isLightTheme, true));
    }

    private void initBackground() {
        if (getBackground() == null) {
            if (this.mHasFabColor) {
                super.setBackground(createFabBackground());
            } else {
                super.setBackground(getDefaultBackground());
            }
            Drawable background = getBackground();
            if (background != null) {
                background.setAlpha(this.mImageAlpha);
                return;
            }
            return;
        }
        this.mHasFabColor = false;
    }

    private void initEmptyHolder() {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int width = (((getWidth() - paddingLeft) - getPaddingRight()) / 2) * 2;
        this.mEmptyHolder.setBounds(paddingLeft, paddingTop, paddingLeft + width, width + paddingTop);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        post(this.mFolmeInitTask);
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(this.mFolmeInitTask);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateShadowAlpha(int i) {
        int iMax = Math.max(Math.min(i, 255), 0);
        this.mFabShadowAlpha = iMax;
        BaseWidgetDropShadowHelper baseWidgetDropShadowHelper = this.mShadowHelper;
        if (baseWidgetDropShadowHelper != null) {
            float f = iMax / 255.0f;
            baseWidgetDropShadowHelper.setAlpha(f);
            this.mShadowHelper.invalidateShadow(this, f);
        }
    }

    public void showShadow() {
        if (this.mShadowShowConfig == null) {
            AnimConfig animConfig = new AnimConfig();
            this.mShadowShowConfig = animConfig;
            animConfig.setEase(FolmeEase.sinOut(880L));
        }
        Folme.use((View) this).to(SHADOW_ALPHA_PROPERTY, Float.valueOf(255.0f), this.mShadowShowConfig);
    }

    public void hideShadow() {
        if (this.mShadowHideConfig == null) {
            AnimConfig animConfig = new AnimConfig();
            this.mShadowHideConfig = animConfig;
            animConfig.setEase(FolmeEase.sinOut(200L));
        }
        Folme.use((View) this).to(SHADOW_ALPHA_PROPERTY, Float.valueOf(0.0f), this.mShadowHideConfig);
    }

    public void setShadowAlpha(int i) {
        Folme.use((View) this).setTo(SHADOW_ALPHA_PROPERTY, Integer.valueOf(i));
    }

    @Override // android.widget.ImageView
    public void setAlpha(int i) {
        boolean z = this.mImageAlpha != i;
        this.mImageAlpha = i;
        Drawable background = getBackground();
        if (background != null) {
            background.setAlpha(i);
        }
        Drawable drawable = getDrawable();
        if (drawable != null && z) {
            drawable.mutate().setAlpha(i);
        }
        BaseWidgetDropShadowHelper baseWidgetDropShadowHelper = this.mShadowHelper;
        if (baseWidgetDropShadowHelper != null && baseWidgetDropShadowHelper.mEnableMiShadow) {
            this.mShadowHelper.invalidateShadow(this, this.mImageAlpha / 255.0f);
        }
        invalidate();
    }

    @Override // android.view.View
    public void setAlpha(float f) {
        float f2 = 255.0f * f;
        boolean z = ((float) this.mImageAlpha) != f2;
        int i = (int) f2;
        this.mImageAlpha = i;
        Drawable background = getBackground();
        if (background != null) {
            background.setAlpha(i);
        }
        Drawable drawable = getDrawable();
        if (drawable != null && z) {
            drawable.mutate().setAlpha(i);
        }
        BaseWidgetDropShadowHelper baseWidgetDropShadowHelper = this.mShadowHelper;
        if (baseWidgetDropShadowHelper != null && baseWidgetDropShadowHelper.mEnableMiShadow) {
            this.mShadowHelper.invalidateShadow(this, f);
        }
        invalidate();
    }

    @Override // android.view.View
    public float getAlpha() {
        return (float) (((double) this.mImageAlpha) / 255.0d);
    }

    @Override // android.widget.ImageView
    public void setImageAlpha(int i) {
        boolean z = this.mImageAlpha != i;
        this.mImageAlpha = i;
        Drawable background = getBackground();
        if (background != null) {
            background.setAlpha(i);
        }
        Drawable drawable = getDrawable();
        if (drawable != null && z) {
            drawable.mutate().setAlpha(i);
        }
        BaseWidgetDropShadowHelper baseWidgetDropShadowHelper = this.mShadowHelper;
        if (baseWidgetDropShadowHelper != null && baseWidgetDropShadowHelper.mEnableMiShadow) {
            this.mShadowHelper.invalidateShadow(this, this.mImageAlpha / 255.0f);
        }
        invalidate();
    }

    @Override // android.widget.ImageView
    public int getImageAlpha() {
        return this.mImageAlpha;
    }

    @Override // android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        BaseWidgetDropShadowHelper baseWidgetDropShadowHelper = this.mShadowHelper;
        if (baseWidgetDropShadowHelper != null) {
            baseWidgetDropShadowHelper.updateShadowRect(i, i2, i3, i4);
            if (this.mIsShadowEnabled) {
                this.mShadowHelper.enableViewShadow(this, true, 2);
            } else {
                this.mShadowHelper.enableViewShadow(this, false, 2);
            }
        }
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        BaseWidgetDropShadowHelper baseWidgetDropShadowHelper;
        if (this.mIsShadowEnabled && (baseWidgetDropShadowHelper = this.mShadowHelper) != null) {
            baseWidgetDropShadowHelper.drawShadow(canvas, getHeight());
        }
        super.draw(canvas);
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDraw(Canvas canvas) {
        initEmptyHolder();
        super.onDraw(canvas);
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (this.mShadowHelper != null) {
            this.mShadowHelper.onConfigChanged(this, configuration, AttributeResolver.resolveBoolean(getContext(), R.attr.isLightTheme, true));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getViewWidth() {
        int i = getLayoutParams().width;
        int intrinsicWidth = getDrawable() != null ? getDrawable().getIntrinsicWidth() : 0;
        if (i == -2) {
            return intrinsicWidth;
        }
        if (i >= 0) {
            return i;
        }
        return 0;
    }

    private Drawable getDefaultBackground() {
        if (this.mDefaultBackground == null) {
            this.mFabColor = getContext().getResources().getColor(R.color.miuix_appcompat_fab_color_light);
            this.mHasFabColor = true;
            this.mDefaultBackground = createFabBackground();
        }
        return this.mDefaultBackground;
    }

    private Drawable createFabBackground() {
        ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShapeWithPadding(this));
        if (this.mIsShadowEnabled) {
            this.mShadowConfig.shadowColor = this.mHasFabShadowColor ? this.mFabShadowColor : computeShadowColor(this.mFabColor);
            if (this.mShadowHelper == null) {
                initDropShadowHelper();
            } else {
                this.mShadowHelper.onConfigChanged(this, getResources().getConfiguration(), AttributeResolver.resolveBoolean(getContext(), R.attr.isLightTheme, true));
            }
        } else {
            BaseWidgetDropShadowHelper baseWidgetDropShadowHelper = this.mShadowHelper;
            if (baseWidgetDropShadowHelper != null) {
                baseWidgetDropShadowHelper.enableViewShadow(this, false, 2);
            }
            this.mShadowHelper = null;
        }
        shapeDrawable.getPaint().setColor(this.mFabColor);
        return shapeDrawable;
    }

    private int computeShadowColor(int i) {
        return Color.argb(102, Color.red(i), Math.max(0, Color.green(i) - 30), Color.blue(i));
    }

    @Override // android.view.View
    public void setBackground(Drawable drawable) {
        this.mHasFabColor = false;
        if (drawable == null) {
            drawable = getDefaultBackground();
        }
        super.setBackground(drawable);
    }

    @Override // android.view.View
    public void setBackgroundResource(int i) {
        this.mHasFabColor = false;
        if (i == 0) {
            super.setBackground(getDefaultBackground());
        } else {
            super.setBackgroundResource(i);
        }
    }

    @Override // android.view.View
    public void setBackgroundColor(int i) {
        if (!this.mHasFabColor || this.mFabColor != i) {
            this.mFabColor = i;
            super.setBackground(createFabBackground());
        }
        this.mHasFabColor = true;
    }

    private static class OvalShapeWithPadding extends OvalShape {
        private WeakReference<View> mViewRef;

        public OvalShapeWithPadding() {
            this.mViewRef = new WeakReference<>(null);
        }

        public OvalShapeWithPadding(View view) {
            this.mViewRef = new WeakReference<>(view);
        }

        @Override // android.graphics.drawable.shapes.OvalShape, android.graphics.drawable.shapes.RectShape, android.graphics.drawable.shapes.Shape
        public void draw(Canvas canvas, Paint paint) {
            View view = this.mViewRef.get();
            if (view != null) {
                int width = view.getWidth();
                int paddingLeft = view.getPaddingLeft();
                int paddingTop = view.getPaddingTop();
                float paddingRight = ((width - paddingLeft) - view.getPaddingRight()) / 2.0f;
                canvas.drawCircle(paddingLeft + paddingRight, paddingTop + paddingRight, paddingRight, paint);
            }
        }
    }

    @Override // android.view.View
    public boolean performClick() {
        HapticCompat.performHapticFeedback(this, HapticFeedbackConstants.MIUI_BUTTON_MIDDLE, HapticFeedbackConstants.MIUI_TAP_LIGHT);
        return super.performClick();
    }

    class EmptyHolder extends Drawable {
        private Drawable mDrawable;
        private Paint mPaint = new Paint();

        EmptyHolder(Drawable drawable) {
            this.mDrawable = drawable;
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            int width = FloatingActionButton.this.getWidth();
            int paddingLeft = FloatingActionButton.this.getPaddingLeft();
            int paddingTop = FloatingActionButton.this.getPaddingTop();
            int paddingRight = (((width - paddingLeft) - FloatingActionButton.this.getPaddingRight()) / 2) * 2;
            this.mDrawable.setBounds(paddingLeft, paddingTop, paddingLeft + paddingRight, paddingRight + paddingTop);
            this.mDrawable.draw(canvas);
        }

        @Override // android.graphics.drawable.Drawable
        public Drawable.ConstantState getConstantState() {
            return this.mDrawable.getConstantState();
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int i) {
            this.mDrawable.setAlpha(i);
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
            this.mDrawable.setColorFilter(colorFilter);
        }

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return this.mDrawable.getOpacity();
        }
    }
}
