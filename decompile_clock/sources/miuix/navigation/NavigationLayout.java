package miuix.navigation;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import miuix.appcompat.app.floatingactivity.multiapp.MethodCodeHelper;
import miuix.internal.util.DeviceHelper;
import miuix.internal.util.ViewDragHelper;
import miuix.internal.util.ViewUtils;

/* JADX INFO: loaded from: classes.dex */
public class NavigationLayout extends ViewGroup {
    public static final int ABSOLUTE = 0;
    private static final int DEFAULT_SCRIM_COLOR = -856295433;
    public static final int DRAWER_ENABLED_ALL = 3;
    public static final int DRAWER_ENABLED_LANDSCAPE = 2;
    public static final int DRAWER_ENABLED_NONE = 0;
    public static final int DRAWER_ENABLED_PORTRAIT = 1;
    public static final int DRAWER_MODE_CONTENT_SQUEEZED = 2;
    public static final int DRAWER_MODE_OVERLAY = 0;
    public static final int DRAWER_MODE_PUSHED_AWAY = 1;
    private static final float HALF_OFFSET = 0.5f;
    public static final int LOCK_MODE_LOCKED_CLOSED = 1;
    public static final int LOCK_MODE_LOCKED_OPEN = 2;
    public static final int LOCK_MODE_UNLOCKED = 0;
    private static final int MIN_FLING_VELOCITY = 400;
    private static final int PEEK_DELAY = 150;
    public static final int RELATIVE_TO_PARENT = 1;
    public static final int STATE_DRAGGING = 1;
    public static final int STATE_IDLE = 0;
    public static final int STATE_SETTLING = 2;
    private boolean mChildrenCanceledTouch;
    private View mContent;
    private float mContentPreviewRatio;
    private Drawable mDivider;
    private int mDividerWidth;
    private final ViewDragHelper mDragger;
    private boolean mDrawerEnabled;
    private int mDrawerEnabledOrientation;
    private int mDrawerMode;
    private boolean mFirstMeasure;
    private float mInitialMotionX;
    private float mInitialMotionY;
    private WidthDescription mLandscapeWidthDescription;
    private boolean mLayoutRtl;
    private NavigationListener mListener;
    private int mLockMode;
    private View mNavigation;
    private Runnable mPeekRunnable;
    private WidthDescription mPortraitWidthDescription;
    private View mPreview;
    private View mScrimAnimationView;
    private ValueAnimator mScrimAnimator;
    private ValueAnimator.AnimatorUpdateListener mScrimAnimatorListener;
    private int mScrimColor;
    private float mScrimOpacity;
    private float mScrimOpacityAnimatior;
    private Paint mScrimPaint;
    private Drawable mShadow;
    private Rect mTmpRect;

    public interface NavigationListener {
        void onDrawerClosed();

        void onDrawerDragStateChanged(int i);

        void onDrawerEnableStateChange(boolean z);

        void onDrawerOpened();

        void onDrawerSlide(float f);
    }

    public NavigationLayout(Context context) {
        this(context, null);
    }

    public NavigationLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.navigationLayoutStyle);
    }

    public NavigationLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mTmpRect = new Rect();
        this.mScrimColor = DEFAULT_SCRIM_COLOR;
        this.mScrimPaint = new Paint();
        this.mFirstMeasure = true;
        this.mLockMode = 0;
        this.mContentPreviewRatio = 1.0f;
        this.mPeekRunnable = new Runnable() { // from class: miuix.navigation.NavigationLayout.1
            @Override // java.lang.Runnable
            public void run() {
                int width;
                View view = NavigationLayout.this.mNavigation;
                int edgeSize = NavigationLayout.this.mDragger.getEdgeSize();
                if (NavigationLayout.this.mLayoutRtl) {
                    width = (view != null ? NavigationLayout.this.getWidth() : 0) - edgeSize;
                } else {
                    width = (view != null ? -view.getWidth() : 0) + edgeSize;
                }
                if (view == null || view.getLeft() >= width || NavigationLayout.this.getDrawerLockMode() != 0) {
                    return;
                }
                LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
                NavigationLayout.this.mDragger.smoothSlideViewTo(view, width, view.getTop());
                layoutParams.isPeeking = true;
                NavigationLayout.this.invalidate();
                NavigationLayout.this.cancelChildViewTouch();
            }
        };
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.NavigationLayout, i, R.style.Widget_NavigationLayout);
        Drawable drawable = typedArrayObtainStyledAttributes.getDrawable(R.styleable.NavigationLayout_navigationDivider);
        if (drawable != null) {
            setDivider(drawable);
        }
        Drawable drawable2 = typedArrayObtainStyledAttributes.getDrawable(R.styleable.NavigationLayout_navigationShadow);
        if (drawable2 != null) {
            setNavigationShadow(drawable2);
        }
        int dimensionPixelSize = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.NavigationLayout_navigationDividerWidth, 0);
        if (dimensionPixelSize != 0) {
            setDividerWidth(dimensionPixelSize);
        }
        this.mScrimColor = typedArrayObtainStyledAttributes.getColor(R.styleable.NavigationLayout_navigationScrimColor, DEFAULT_SCRIM_COLOR);
        this.mDrawerEnabledOrientation = typedArrayObtainStyledAttributes.getInt(R.styleable.NavigationLayout_drawerEnabledOrientation, 0);
        this.mPortraitWidthDescription = WidthDescription.parseValue(typedArrayObtainStyledAttributes.peekValue(R.styleable.NavigationLayout_portraitNavigationWidth), getResources());
        this.mLandscapeWidthDescription = WidthDescription.parseValue(typedArrayObtainStyledAttributes.peekValue(R.styleable.NavigationLayout_landscapeNavigationWidth), getResources());
        this.mDrawerMode = typedArrayObtainStyledAttributes.getInt(R.styleable.NavigationLayout_drawerMode, 0);
        String string = typedArrayObtainStyledAttributes.getString(R.styleable.NavigationLayout_contentPreviewRatio);
        if (!TextUtils.isEmpty(string)) {
            int length = string.length();
            int iIndexOf = string.indexOf(MethodCodeHelper.IDENTITY_INFO_SEPARATOR);
            try {
                if (iIndexOf >= 0 && iIndexOf < length - 1) {
                    String strSubstring = string.substring(0, iIndexOf);
                    String strSubstring2 = string.substring(iIndexOf + 1);
                    if (strSubstring.length() > 0 && strSubstring2.length() > 0) {
                        float f = Float.parseFloat(strSubstring);
                        float f2 = Float.parseFloat(strSubstring2);
                        if (f > 0.0f && f2 > 0.0f) {
                            this.mContentPreviewRatio = Math.abs(f / f2);
                        }
                    }
                } else {
                    this.mContentPreviewRatio = Math.abs(Float.parseFloat(string));
                }
            } catch (NumberFormatException unused) {
            }
        }
        typedArrayObtainStyledAttributes.recycle();
        ViewDragHelper viewDragHelperCreate = ViewDragHelper.create(this, 0.5f, new ViewDragCallback());
        this.mDragger = viewDragHelperCreate;
        viewDragHelperCreate.setMinVelocity(getResources().getDisplayMetrics().density * 400.0f);
        setFocusableInTouchMode(true);
    }

    @Override // android.view.View
    public void onRtlPropertiesChanged(int i) {
        boolean z = i == 1;
        this.mLayoutRtl = z;
        this.mDragger.setEdgeTrackingEnabled(z ? 2 : 1);
    }

    public void setNavigationListener(NavigationListener navigationListener) {
        this.mListener = navigationListener;
    }

    public int getDrawerEnabledOrientation() {
        return this.mDrawerEnabledOrientation;
    }

    public void setDrawerEnabledOrientation(int i) {
        this.mDrawerEnabledOrientation = i;
        requestLayout();
    }

    public void setNavigationShadow(Drawable drawable) {
        this.mShadow = drawable;
        invalidate();
    }

    public void setNavigationShadow(int i) {
        setNavigationShadow(getResources().getDrawable(i));
    }

    public void setDivider(Drawable drawable) {
        if (drawable != null) {
            this.mDividerWidth = drawable.getIntrinsicHeight();
        } else {
            this.mDividerWidth = 0;
        }
        this.mDivider = drawable;
        requestLayout();
        invalidate();
    }

    public int getDividerWidth() {
        return this.mDividerWidth;
    }

    public void setDividerWidth(int i) {
        this.mDividerWidth = i;
        requestLayout();
        invalidate();
    }

    private void setChildViewEnabled(View view, boolean z) {
        if (view == null || view.isEnabled() == z) {
            return;
        }
        View view2 = this.mScrimAnimationView;
        if (view2 == null || view2 == view || view2.isEnabled() || z) {
            view.setEnabled(z);
            ValueAnimator valueAnimator = this.mScrimAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.mScrimAnimator.setFloatValues(z ? 1.0f : 0.0f, z ? 0.0f : 1.0f);
            } else {
                this.mScrimAnimator = ValueAnimator.ofFloat(z ? 1.0f : 0.0f, z ? 0.0f : 1.0f);
            }
            this.mScrimAnimationView = view;
            this.mScrimAnimator.setDuration(DeviceHelper.isFeatureWholeAnim() ? 500L : 0L);
            this.mScrimAnimator.addUpdateListener(getScrimAnimatorListener());
            this.mScrimOpacityAnimatior = z ? 1.0f : 0.0f;
            this.mScrimAnimator.start();
        }
    }

    public void setNavigationEanbled(boolean z) {
        setChildViewEnabled(this.mNavigation, z);
    }

    public void setContentEnabled(boolean z) {
        setChildViewEnabled(this.mContent, z);
        setChildViewEnabled(this.mPreview, z);
    }

    public void setDrawerMode(int i) {
        this.mDrawerMode = i;
        requestLayout();
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        View view = this.mScrimAnimationView;
        if (view != null && !view.isEnabled()) {
            int x = (int) motionEvent.getX();
            int y = (int) motionEvent.getY();
            int left = this.mScrimAnimationView.getLeft();
            int right = this.mScrimAnimationView.getRight();
            int top = this.mScrimAnimationView.getTop();
            int bottom = this.mScrimAnimationView.getBottom();
            if (left < x && x < right && top < y && y < bottom) {
                return true;
            }
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    private ValueAnimator.AnimatorUpdateListener getScrimAnimatorListener() {
        if (this.mScrimAnimatorListener == null) {
            this.mScrimAnimatorListener = new ValueAnimator.AnimatorUpdateListener() { // from class: miuix.navigation.NavigationLayout.2
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    if (NavigationLayout.this.mScrimAnimationView != null) {
                        NavigationLayout.this.mScrimOpacityAnimatior = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                        NavigationLayout navigationLayout = NavigationLayout.this;
                        navigationLayout.postInvalidateOnAnimation(navigationLayout.mScrimAnimationView.getLeft(), NavigationLayout.this.mScrimAnimationView.getTop(), NavigationLayout.this.mScrimAnimationView.getRight(), NavigationLayout.this.mScrimAnimationView.getBottom());
                    }
                }
            };
        }
        return this.mScrimAnimatorListener;
    }

    private void pullChildren() {
        if (this.mContent == null) {
            this.mContent = findViewById(R.id.content);
        }
        if (this.mNavigation == null) {
            this.mNavigation = findViewById(R.id.navigation);
        }
        if (this.mPreview == null) {
            this.mPreview = findViewById(R.id.preview);
        }
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        pullChildren();
    }

    /* JADX WARN: Code duplicated, block: B:18:0x004c  */
    /* JADX WARN: Code duplicated, block: B:20:0x005c  */
    /* JADX WARN: Code duplicated, block: B:21:0x0063  */
    /* JADX WARN: Code duplicated, block: B:24:0x0077  */
    /* JADX WARN: Code duplicated, block: B:25:0x007d  */
    /* JADX WARN: Code duplicated, block: B:28:0x0083  */
    /* JADX WARN: Code duplicated, block: B:29:0x0087  */
    /* JADX WARN: Code duplicated, block: B:33:0x0091  */
    /* JADX WARN: Code duplicated, block: B:35:0x0094 A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:41:0x00ab  */
    /* JADX WARN: Code duplicated, block: B:43:0x00b1  */
    /* JADX WARN: Code duplicated, block: B:45:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:46:? A[RETURN, SYNTHETIC] */
    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        float f;
        int i3;
        int measuredWidth;
        int i4;
        NavigationListener navigationListener;
        LayoutParams layoutParams;
        float fAbs;
        boolean z = false;
        this.mFirstMeasure = false;
        pullChildren();
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        setMeasuredDimension(size, size2);
        boolean z2 = getResources().getConfiguration().orientation == 2;
        WidthDescription widthDescription = z2 ? this.mLandscapeWidthDescription : this.mPortraitWidthDescription;
        int i5 = widthDescription.type;
        if (i5 == 0) {
            f = widthDescription.value;
        } else {
            if (i5 != 1) {
                i3 = 0;
            } else {
                f = widthDescription.value * size;
            }
            measureChild(this.mNavigation, View.MeasureSpec.makeMeasureSpec(i3, BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(size2, BasicMeasure.EXACTLY));
            if (this.mDrawerMode == 2) {
                layoutParams = (LayoutParams) this.mNavigation.getLayoutParams();
                if (layoutParams.offset > 0.5f) {
                    measuredWidth = this.mNavigation.getMeasuredWidth();
                } else {
                    measuredWidth = 0;
                }
                fAbs = Math.abs(layoutParams.offset - 0.5f) / 0.5f;
                this.mContent.setAlpha(fAbs);
                if (hasPreview()) {
                    this.mPreview.setAlpha(fAbs);
                }
            } else {
                measuredWidth = 0;
            }
            i4 = this.mDrawerEnabledOrientation;
            if (i4 == 3) {
                measureContentPreviewWithMargins(i, measuredWidth, i2, size);
            } else if ((i4 & 2) == 0 && z2) {
                measureContentPreviewWithMargins(i, measuredWidth, i2, size);
            } else {
                if ((i4 & 1) == 0 && !z2) {
                    measureContentPreviewWithMargins(i, measuredWidth, i2, size);
                } else {
                    measureContentPreviewWithMargins(i, this.mNavigation.getMeasuredWidth() + this.mDividerWidth, i2, size);
                }
                if (this.mDrawerEnabled != z) {
                    this.mDrawerEnabled = z;
                    navigationListener = this.mListener;
                    if (navigationListener != null) {
                        navigationListener.onDrawerEnableStateChange(z);
                    }
                }
            }
            z = true;
            if (this.mDrawerEnabled != z) {
                this.mDrawerEnabled = z;
                navigationListener = this.mListener;
                if (navigationListener != null) {
                    navigationListener.onDrawerEnableStateChange(z);
                }
            }
        }
        i3 = (int) f;
        measureChild(this.mNavigation, View.MeasureSpec.makeMeasureSpec(i3, BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(size2, BasicMeasure.EXACTLY));
        if (this.mDrawerMode == 2) {
            layoutParams = (LayoutParams) this.mNavigation.getLayoutParams();
            if (layoutParams.offset > 0.5f) {
                measuredWidth = this.mNavigation.getMeasuredWidth();
            } else {
                measuredWidth = 0;
            }
            fAbs = Math.abs(layoutParams.offset - 0.5f) / 0.5f;
            this.mContent.setAlpha(fAbs);
            if (hasPreview()) {
                this.mPreview.setAlpha(fAbs);
            }
        } else {
            measuredWidth = 0;
        }
        i4 = this.mDrawerEnabledOrientation;
        if (i4 == 3) {
            measureContentPreviewWithMargins(i, measuredWidth, i2, size);
        } else {
            if ((i4 & 2) == 0) {
                if ((i4 & 1) == 0) {
                }
                measureContentPreviewWithMargins(i, this.mNavigation.getMeasuredWidth() + this.mDividerWidth, i2, size);
            } else {
                if ((i4 & 1) == 0) {
                }
                measureContentPreviewWithMargins(i, this.mNavigation.getMeasuredWidth() + this.mDividerWidth, i2, size);
            }
            if (this.mDrawerEnabled != z) {
                this.mDrawerEnabled = z;
                navigationListener = this.mListener;
                if (navigationListener != null) {
                    navigationListener.onDrawerEnableStateChange(z);
                }
            }
        }
        z = true;
        if (this.mDrawerEnabled != z) {
            this.mDrawerEnabled = z;
            navigationListener = this.mListener;
            if (navigationListener != null) {
                navigationListener.onDrawerEnableStateChange(z);
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:26:0x0085  */
    /* JADX WARN: Code duplicated, block: B:27:0x00a2  */
    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        int measuredWidth;
        int i6;
        int i7;
        if (this.mDrawerEnabled) {
            int measuredWidth2 = this.mNavigation.getMeasuredWidth();
            LayoutParams layoutParams = (LayoutParams) this.mNavigation.getLayoutParams();
            float f = -measuredWidth2;
            float f2 = measuredWidth2;
            int i8 = (int) ((layoutParams.offset * f2) + f);
            View view = this.mNavigation;
            ViewUtils.layoutChildView(this, view, i8, i2, i8 + measuredWidth2, i2 + view.getMeasuredHeight());
            int measuredWidth3 = this.mContent.getMeasuredWidth() + i;
            int measuredWidth4 = hasPreview() ? this.mPreview.getMeasuredWidth() + measuredWidth3 : 0;
            int i9 = this.mDrawerMode;
            if (i9 == 1) {
                int i10 = (int) (f * layoutParams.offset);
                if (this.mLayoutRtl) {
                    i10 = 0 - i10;
                }
                setContentPreviewScrollX(i10);
            } else if (i9 == 0) {
                setContentPreviewScrollX(0);
            } else {
                if (i9 == 2) {
                    i5 = (int) (i + (f2 * layoutParams.offset));
                    if (hasPreview()) {
                        i7 = i3;
                        measuredWidth = this.mContent.getMeasuredWidth() + i5;
                    } else {
                        measuredWidth = i3;
                        i6 = measuredWidth3;
                        i7 = measuredWidth4;
                    }
                    if (hasPreview()) {
                        View view2 = this.mContent;
                        ViewUtils.layoutChildView(this, view2, i5, i2, measuredWidth, i2 + view2.getMeasuredHeight());
                        View view3 = this.mPreview;
                        ViewUtils.layoutChildView(this, view3, i6, i2, i7, i2 + view3.getMeasuredHeight());
                        return;
                    }
                    View view4 = this.mContent;
                    ViewUtils.layoutChildView(this, view4, i5, i2, measuredWidth, i2 + view4.getMeasuredHeight());
                    return;
                }
                i6 = measuredWidth;
                if (hasPreview()) {
                    View view5 = this.mContent;
                    ViewUtils.layoutChildView(this, view5, i5, i2, measuredWidth, i2 + view5.getMeasuredHeight());
                    View view6 = this.mPreview;
                    ViewUtils.layoutChildView(this, view6, i6, i2, i7, i2 + view6.getMeasuredHeight());
                    return;
                }
                View view7 = this.mContent;
                ViewUtils.layoutChildView(this, view7, i5, i2, measuredWidth, i2 + view7.getMeasuredHeight());
                return;
            }
            measuredWidth = measuredWidth3;
            i7 = measuredWidth4;
            i5 = i;
            i6 = measuredWidth;
            if (hasPreview()) {
                View view8 = this.mContent;
                ViewUtils.layoutChildView(this, view8, i5, i2, measuredWidth, i2 + view8.getMeasuredHeight());
                View view9 = this.mPreview;
                ViewUtils.layoutChildView(this, view9, i6, i2, i7, i2 + view9.getMeasuredHeight());
                return;
            }
            View view10 = this.mContent;
            ViewUtils.layoutChildView(this, view10, i5, i2, measuredWidth, i2 + view10.getMeasuredHeight());
            return;
        }
        View view11 = this.mNavigation;
        ViewUtils.layoutChildView(this, view11, i, i2, i + view11.getMeasuredWidth(), i2 + this.mNavigation.getMeasuredHeight());
        int measuredWidth5 = this.mNavigation.getMeasuredWidth() + i + this.mDividerWidth;
        int measuredWidth6 = measuredWidth5 + this.mContent.getMeasuredWidth();
        if (hasPreview()) {
            View view12 = this.mContent;
            ViewUtils.layoutChildView(this, view12, measuredWidth5, i2, measuredWidth6, i2 + view12.getMeasuredHeight());
            View view13 = this.mPreview;
            ViewUtils.layoutChildView(this, view13, measuredWidth6, i2, i3, i2 + view13.getMeasuredHeight());
        } else {
            View view14 = this.mContent;
            ViewUtils.layoutChildView(this, view14, measuredWidth5, i2, measuredWidth6, i2 + view14.getMeasuredHeight());
        }
        setContentPreviewScrollX(0);
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-1, -1);
    }

    @Override // android.view.ViewGroup
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return new LayoutParams(layoutParams);
    }

    @Override // android.view.ViewGroup
    protected boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.mDrawerEnabled) {
            drawScrim(canvas);
            drawShadow(canvas);
        } else {
            drawDivider(canvas);
            drawNavigationDisableScrim(canvas);
        }
    }

    private void measureContentPreviewWithMargins(int i, int i2, int i3, int i4) {
        int i5 = i4 - i2;
        if (hasPreview()) {
            View view = this.mContent;
            float f = i5;
            float f2 = this.mContentPreviewRatio;
            measureChildWithMarginsPadding(view, i, (int) ((f * f2) / (f2 + 1.0f)), i3);
            View view2 = this.mPreview;
            float f3 = this.mContentPreviewRatio;
            measureChildWithMarginsPadding(view2, i, (int) (f * (1.0f - (f3 / (f3 + 1.0f)))), i3);
            return;
        }
        measureChildWithMarginsPadding(this.mContent, i, i5, i3);
    }

    private void setContentPreviewScrollX(int i) {
        float f = -i;
        this.mContent.setTranslationX(f);
        if (hasPreview()) {
            this.mPreview.setTranslationX(f);
        }
    }

    protected void measureChildWithMarginsPadding(View view, int i, int i2, int i3) {
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        view.measure(getChildMeasureSpec(i, getPaddingLeft() + getPaddingRight() + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin, i2), getChildMeasureSpec(i3, getPaddingTop() + getPaddingBottom() + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin, marginLayoutParams.height));
    }

    public void setScrimColor(int i) {
        this.mScrimColor = i;
    }

    private void drawScrim(Canvas canvas) {
        float f = this.mScrimOpacity;
        if (f > 0.0f) {
            int i = this.mScrimColor;
            this.mScrimPaint.setColor((((int) ((((-16777216) & i) >>> 24) * f)) << 24) | (i & 16777215));
            canvas.drawRect(this.mLayoutRtl ? 0 : this.mNavigation.getRight(), 0.0f, this.mLayoutRtl ? this.mNavigation.getLeft() : getWidth(), getHeight(), this.mScrimPaint);
        }
    }

    private void drawNavigationDisableScrim(Canvas canvas) {
        float f = this.mScrimOpacityAnimatior;
        if (f <= 0.0f || this.mScrimAnimationView == null) {
            return;
        }
        int i = this.mScrimColor;
        this.mScrimPaint.setColor((((int) ((((-16777216) & i) >>> 24) * f)) << 24) | (i & 16777215));
        canvas.drawRect(this.mScrimAnimationView.getLeft(), this.mScrimAnimationView.getTop(), this.mScrimAnimationView.getRight(), this.mScrimAnimationView.getBottom(), this.mScrimPaint);
    }

    private void drawShadow(Canvas canvas) {
        Drawable drawable = this.mShadow;
        if (drawable == null) {
            return;
        }
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int left = this.mLayoutRtl ? this.mNavigation.getLeft() - intrinsicWidth : this.mNavigation.getRight();
        this.mShadow.setBounds(left, this.mNavigation.getTop(), intrinsicWidth + left, this.mNavigation.getBottom());
        this.mShadow.draw(canvas);
    }

    private void drawDivider(Canvas canvas) {
        Rect rect = this.mTmpRect;
        int measuredWidth = this.mNavigation.getMeasuredWidth();
        if (this.mLayoutRtl) {
            measuredWidth = (getWidth() - measuredWidth) - this.mDividerWidth;
        }
        rect.set(measuredWidth, getPaddingTop(), this.mDividerWidth + measuredWidth, getBottom() - getPaddingBottom());
        Drawable drawable = this.mDivider;
        drawable.setBounds(rect);
        drawable.draw(canvas);
    }

    /* JADX WARN: Code duplicated, block: B:17:0x002f  */
    /* JADX WARN: Code duplicated, block: B:26:0x0068  */
    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        boolean z;
        boolean zShouldInterceptTouchEvent = this.mDragger.shouldInterceptTouchEvent(motionEvent);
        if (!this.mDrawerEnabled || this.mLockMode != 0) {
            return super.onInterceptTouchEvent(motionEvent);
        }
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked != 0) {
            if (actionMasked == 1) {
                removeCallbacks(this.mPeekRunnable);
                closePeekingDrawer();
                this.mChildrenCanceledTouch = false;
            } else if (actionMasked != 2) {
                if (actionMasked == 3) {
                    removeCallbacks(this.mPeekRunnable);
                    closePeekingDrawer();
                    this.mChildrenCanceledTouch = false;
                }
            } else if (this.mDragger.checkTouchSlop(3)) {
                removeCallbacks(this.mPeekRunnable);
            }
            z = false;
        } else {
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            this.mInitialMotionX = x;
            this.mInitialMotionY = y;
            if (getNavigationOffset() > 0.0f) {
                int i = (int) x;
                int i2 = (int) y;
                if (this.mDragger.findTopChildUnder(i, i2) == this.mContent || this.mDragger.findTopChildUnder(i, i2) == this.mPreview) {
                    z = true;
                } else {
                    z = false;
                }
            } else {
                z = false;
            }
            this.mChildrenCanceledTouch = false;
        }
        return zShouldInterceptTouchEvent || z || isDrawerPeeking() || this.mChildrenCanceledTouch;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        this.mDragger.processTouchEvent(motionEvent);
        if (!this.mDrawerEnabled || this.mLockMode != 0) {
            return super.onTouchEvent(motionEvent);
        }
        int actionMasked = motionEvent.getActionMasked();
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        if (actionMasked == 0) {
            this.mInitialMotionX = x;
            this.mInitialMotionY = y;
            this.mChildrenCanceledTouch = false;
        } else if (actionMasked == 1) {
            float f = x - this.mInitialMotionX;
            float f2 = y - this.mInitialMotionY;
            int touchSlop = this.mDragger.getTouchSlop();
            View viewFindTopChildUnder = this.mDragger.findTopChildUnder((int) x, (int) y);
            boolean z = viewFindTopChildUnder == null || !((viewFindTopChildUnder == this.mContent || viewFindTopChildUnder == this.mPreview) && (f * f) + (f2 * f2) < ((float) (touchSlop * touchSlop)) && isNavigationDrawerOpen());
            removeCallbacks(this.mPeekRunnable);
            if (z) {
                closePeekingDrawer();
            } else if (this.mLockMode == 0) {
                closeNavigationDrawer(true);
            }
        } else if (actionMasked == 3) {
            closePeekingDrawer();
            this.mChildrenCanceledTouch = false;
        }
        return true;
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void requestDisallowInterceptTouchEvent(boolean z) {
        if (!this.mDragger.isEdgeTouched(this.mLayoutRtl ? 2 : 1)) {
            super.requestDisallowInterceptTouchEvent(z);
        }
        if (z) {
            closePeekingDrawer();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelChildViewTouch() {
        if (this.mChildrenCanceledTouch) {
            return;
        }
        long jUptimeMillis = SystemClock.uptimeMillis();
        MotionEvent motionEventObtain = MotionEvent.obtain(jUptimeMillis, jUptimeMillis, 3, 0.0f, 0.0f, 0);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).dispatchTouchEvent(motionEventObtain);
        }
        motionEventObtain.recycle();
        this.mChildrenCanceledTouch = true;
    }

    private boolean isDrawerPeeking() {
        return ((LayoutParams) this.mNavigation.getLayoutParams()).isPeeking;
    }

    private boolean hasPreview() {
        return this.mPreview != null;
    }

    @Override // android.view.View
    public void computeScroll() {
        super.computeScroll();
        if (this.mDrawerMode == 2) {
            this.mScrimOpacity = 0.0f;
        } else {
            this.mScrimOpacity = getNavigationOffset();
        }
        if (this.mDragger.continueSettling(true)) {
            postInvalidateOnAnimation();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float getNavigationOffset() {
        return ((LayoutParams) this.mNavigation.getLayoutParams()).offset;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setNavigationOffset(float f) {
        LayoutParams layoutParams = (LayoutParams) this.mNavigation.getLayoutParams();
        if (f == layoutParams.offset) {
            return;
        }
        layoutParams.offset = f;
        NavigationListener navigationListener = this.mListener;
        if (navigationListener != null) {
            navigationListener.onDrawerSlide(f);
        }
        int i = this.mDrawerMode;
        if (i == 0) {
            setContentPreviewScrollX(0);
            return;
        }
        if (i == 1) {
            int width = this.mNavigation.getWidth();
            if (!this.mLayoutRtl) {
                width = -width;
            }
            setContentPreviewScrollX((int) (width * layoutParams.offset));
            return;
        }
        requestLayout();
    }

    public void setDrawerLockMode(int i) {
        if (this.mLockMode == i) {
            return;
        }
        this.mLockMode = i;
        if (i != 0) {
            this.mDragger.cancel();
        }
        if (i == 1) {
            closeNavigationDrawer(false);
        } else {
            if (i != 2) {
                return;
            }
            openNavigationDrawer(false);
        }
    }

    public int getDrawerLockMode() {
        return this.mLockMode;
    }

    public void openNavigationDrawer(boolean z) {
        if (this.mFirstMeasure) {
            z = false;
        }
        if (z) {
            if (!this.mDrawerEnabled) {
                return;
            }
            int width = this.mLayoutRtl ? getWidth() - this.mNavigation.getWidth() : 0;
            ViewDragHelper viewDragHelper = this.mDragger;
            View view = this.mNavigation;
            viewDragHelper.smoothSlideViewTo(view, width, view.getTop());
        } else {
            setNavigationOffset(1.0f);
            NavigationListener navigationListener = this.mListener;
            if (navigationListener != null) {
                navigationListener.onDrawerOpened();
            }
        }
        invalidate();
    }

    private void closePeekingDrawer() {
        LayoutParams layoutParams = (LayoutParams) this.mNavigation.getLayoutParams();
        if (layoutParams.isPeeking) {
            layoutParams.isPeeking = false;
            closeNavigationDrawer(true);
        }
    }

    public void closeNavigationDrawer(boolean z) {
        if (this.mFirstMeasure) {
            z = false;
        }
        if (z) {
            if (!this.mDrawerEnabled) {
                return;
            }
            int width = this.mLayoutRtl ? getWidth() : -this.mNavigation.getWidth();
            ViewDragHelper viewDragHelper = this.mDragger;
            View view = this.mNavigation;
            viewDragHelper.smoothSlideViewTo(view, width, view.getTop());
        } else {
            setNavigationOffset(0.0f);
            NavigationListener navigationListener = this.mListener;
            if (navigationListener != null) {
                navigationListener.onDrawerClosed();
            }
        }
        invalidate();
        removeCallbacks(this.mPeekRunnable);
    }

    public boolean isNavigationDrawerOpen() {
        return ((LayoutParams) this.mNavigation.getLayoutParams()).offset == 1.0f;
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (super.dispatchKeyEvent(keyEvent)) {
            return true;
        }
        if (getDrawerLockMode() != 0 || !this.mDrawerEnabled || keyEvent.getKeyCode() != 4 || keyEvent.getAction() != 1) {
            return false;
        }
        boolean zIsNavigationDrawerOpen = isNavigationDrawerOpen();
        closeNavigationDrawer(true);
        return zIsNavigationDrawerOpen;
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.offset = getNavigationOffset();
        return savedState;
    }

    @Override // android.view.View
    protected void onRestoreInstanceState(Parcelable parcelable) {
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        setNavigationOffset(savedState.offset);
        if (savedState.offset >= 0.5f) {
            openNavigationDrawer(false);
        } else {
            closeNavigationDrawer(false);
        }
    }

    private static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: miuix.navigation.NavigationLayout.SavedState.1
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        float offset;

        public SavedState(Parcel parcel) {
            super(parcel);
            this.offset = parcel.readFloat();
        }

        private SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeFloat(this.offset);
        }
    }

    protected static class LayoutParams extends ViewGroup.MarginLayoutParams {
        boolean isPeeking;
        float offset;

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        public LayoutParams(int i, int i2) {
            super(i, i2);
        }

        public LayoutParams(ViewGroup.MarginLayoutParams marginLayoutParams) {
            super(marginLayoutParams);
        }

        public LayoutParams(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
        }
    }

    private static class WidthDescription {
        public int type;
        public float value;

        private WidthDescription() {
        }

        static WidthDescription parseValue(TypedValue typedValue, Resources resources) {
            WidthDescription widthDescription = new WidthDescription();
            if (typedValue == null) {
                widthDescription.type = 1;
                widthDescription.value = 0.3f;
            } else {
                if (typedValue.type == 6) {
                    widthDescription.type = 1;
                    widthDescription.value = TypedValue.complexToFloat(typedValue.data);
                    return widthDescription;
                }
                if (typedValue.type == 4) {
                    widthDescription.type = 1;
                    widthDescription.value = typedValue.getFloat();
                    return widthDescription;
                }
                if (typedValue.type == 5) {
                    widthDescription.type = 0;
                    widthDescription.value = TypedValue.complexToDimensionPixelSize(typedValue.data, resources.getDisplayMetrics());
                    return widthDescription;
                }
            }
            widthDescription.type = 1;
            widthDescription.value = 0.3f;
            return widthDescription;
        }
    }

    private class ViewDragCallback extends ViewDragHelper.Callback {
        private ViewDragCallback() {
        }

        @Override // miuix.internal.util.ViewDragHelper.Callback
        public boolean tryCaptureView(View view, int i) {
            return view == NavigationLayout.this.mNavigation && NavigationLayout.this.getDrawerLockMode() == 0;
        }

        @Override // miuix.internal.util.ViewDragHelper.Callback
        public int getViewHorizontalDragRange(View view) {
            if (view == NavigationLayout.this.mNavigation) {
                return NavigationLayout.this.mNavigation.getWidth();
            }
            return 0;
        }

        @Override // miuix.internal.util.ViewDragHelper.Callback
        public void onViewPositionChanged(View view, int i, int i2, int i3, int i4) {
            if (view == NavigationLayout.this.mNavigation) {
                int width = NavigationLayout.this.mNavigation.getWidth();
                NavigationLayout.this.setNavigationOffset((NavigationLayout.this.mLayoutRtl ? NavigationLayout.this.getWidth() - i : i + width) / width);
                NavigationLayout.this.invalidate();
            }
        }

        @Override // miuix.internal.util.ViewDragHelper.Callback
        public void onViewCaptured(View view, int i) {
            ((LayoutParams) view.getLayoutParams()).isPeeking = false;
        }

        @Override // miuix.internal.util.ViewDragHelper.Callback
        public void onViewReleased(View view, float f, float f2) {
            float navigationOffset = NavigationLayout.this.getNavigationOffset();
            int width = view.getWidth();
            int width2 = NavigationLayout.this.mLayoutRtl ? NavigationLayout.this.getWidth() - width : 0;
            int width3 = NavigationLayout.this.mLayoutRtl ? NavigationLayout.this.getWidth() : -width;
            if (!NavigationLayout.this.mLayoutRtl ? f <= 0.0f : f >= 0.0f) {
                if (f != 0.0f || navigationOffset <= 0.5f) {
                    width2 = width3;
                }
            }
            NavigationLayout.this.mDragger.settleCapturedViewAt(width2, view.getTop());
            NavigationLayout.this.invalidate();
        }

        @Override // miuix.internal.util.ViewDragHelper.Callback
        public int clampViewPositionHorizontal(View view, int i, int i2) {
            int width = NavigationLayout.this.mLayoutRtl ? NavigationLayout.this.getWidth() - view.getWidth() : -view.getWidth();
            return Math.max(width, Math.min(i, view.getWidth() + width));
        }

        @Override // miuix.internal.util.ViewDragHelper.Callback
        public void onEdgeTouched(int i, int i2) {
            NavigationLayout navigationLayout = NavigationLayout.this;
            navigationLayout.postDelayed(navigationLayout.mPeekRunnable, 150L);
        }

        @Override // miuix.internal.util.ViewDragHelper.Callback
        public void onEdgeDragStarted(int i, int i2) {
            if (NavigationLayout.this.getDrawerLockMode() == 0) {
                NavigationLayout.this.mDragger.captureChildView(NavigationLayout.this.mNavigation, i2);
                NavigationLayout navigationLayout = NavigationLayout.this;
                navigationLayout.removeCallbacks(navigationLayout.mPeekRunnable);
            }
        }

        @Override // miuix.internal.util.ViewDragHelper.Callback
        public void onViewDragStateChanged(int i) {
            if (NavigationLayout.this.mListener != null) {
                if (i == 0) {
                    if (NavigationLayout.this.isNavigationDrawerOpen()) {
                        NavigationLayout.this.mListener.onDrawerOpened();
                    } else {
                        NavigationLayout.this.mListener.onDrawerClosed();
                    }
                }
                NavigationLayout.this.mListener.onDrawerDragStateChanged(i);
            }
        }
    }
}
