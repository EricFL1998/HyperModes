package miuix.popupwidget.internal.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Property;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import miuix.internal.util.DeviceHelper;
import miuix.internal.util.ViewUtils;
import miuix.popupwidget.R;
import miuix.popupwidget.widget.ArrowPopupWindow;
import miuix.view.CompatViewMethod;

/* JADX INFO: loaded from: classes3.dex */
public class ArrowPopupView extends FrameLayout implements View.OnTouchListener {
    private static final int ANIMATION_REPEAT_COUNT = 8;
    public static final byte ARROW_BOTTOM_LEFT_MODE = 18;
    public static final byte ARROW_BOTTOM_MODE = 16;
    public static final byte ARROW_BOTTOM_RIGHT_MODE = 17;
    public static final byte ARROW_LEFT_MODE = 32;
    private static final int ARROW_MIDDLE_OFFSET = 28;
    public static final byte ARROW_NONE_MODE = 0;
    private static final int ARROW_PADDING = 1;
    public static final byte ARROW_RIGHT_MODE = 64;
    public static final byte ARROW_TOP_LEFT_MODE = 9;
    public static final byte ARROW_TOP_MODE = 8;
    public static final byte ARROW_TOP_RIGHT_MODE = 10;
    public static final int LAYOUT_MODE_LTR = 0;
    public static final int LAYOUT_MODE_RTL = 1;
    public static final int LAYOUT_MODE_UNSPECIFIED = 2;
    private static final String TAG = "ArrowPopupView";
    private static final int TRANSLATION_VALUE = 4;
    private boolean mAlphaAnimationEnabled;
    private View mAnchor;
    private View.OnLayoutChangeListener mAnchorTrackListener;
    private AnimationSet mAnimationSet;
    private AnimatorSet mAnimator;
    private AppCompatImageView mArrow;
    private int mArrowBackgroundPaintColor;
    private Drawable mArrowBottom;
    private Drawable mArrowBottomLeft;
    private Drawable mArrowBottomRight;
    private final Runnable mArrowLayoutTask;
    private Drawable mArrowLeft;
    private int mArrowMode;
    private ArrowPopupWindow mArrowPopupWindow;
    private Drawable mArrowRight;
    private int mArrowSpaceLeft;
    private int mArrowSpaceTop;
    private Drawable mArrowTop;
    private Drawable mArrowTopLeft;
    private Drawable mArrowTopRight;
    private Drawable mArrowTopWithTitle;
    private boolean mAutoDismiss;
    private Drawable mBackground;
    private Drawable mBackgroundLeft;
    private Drawable mBackgroundRight;
    private FrameLayout mContentFrame;
    private ArrowPopupContentWrapper mContentFrameWrapper;
    private int mElevation;
    private boolean mEnableTrackAnchor;
    private boolean mHasFirstLayout;
    private Animation.AnimationListener mHideAnimatorListener;
    private boolean mIsDismissing;
    private int mMinBorder;
    private AppCompatButton mNegativeButton;
    private WrapperOnClickListener mNegativeClickListener;
    private int mOffsetX;
    private int mOffsetY;
    private AppCompatButton mPositiveButton;
    private WrapperOnClickListener mPositiveClickListener;
    private int mRtlMode;
    private Animation.AnimationListener mShowAnimationListener;
    private boolean mShowingAnimation;
    private int mSpaceLeft;
    private int mSpaceTop;
    private Drawable mTitleBackground;
    private LinearLayout mTitleLayout;
    private AppCompatTextView mTitleText;
    private Rect mTmpRect;
    private RectF mTmpRectF;
    private View.OnTouchListener mTouchInterceptor;
    private int mTranslationValue;

    @Deprecated
    public float getRollingPercent() {
        return 1.0f;
    }

    @Deprecated
    public void setRollingPercent(float f) {
    }

    public ArrowPopupView(Context context) {
        this(context, null);
    }

    public ArrowPopupView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.arrowPopupViewStyle);
    }

    public ArrowPopupView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mTmpRect = new Rect();
        this.mTmpRectF = new RectF();
        this.mAutoDismiss = true;
        this.mRtlMode = 2;
        this.mShowingAnimation = false;
        this.mShowAnimationListener = new Animation.AnimationListener() { // from class: miuix.popupwidget.internal.widget.ArrowPopupView.1
            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationRepeat(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationStart(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationEnd(Animation animation) {
                ArrowPopupView.this.mAnimationSet = null;
                if (ArrowPopupView.this.mShowingAnimation) {
                    ArrowPopupView.this.animateShowing();
                }
            }
        };
        this.mHideAnimatorListener = new Animation.AnimationListener() { // from class: miuix.popupwidget.internal.widget.ArrowPopupView.2
            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationRepeat(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationStart(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationEnd(Animation animation) {
                ArrowPopupView.this.mIsDismissing = false;
                ArrowPopupView.this.mAnimationSet = null;
                ArrowPopupView.this.mArrowPopupWindow.dismiss();
            }
        };
        this.mEnableTrackAnchor = true;
        this.mHasFirstLayout = false;
        this.mArrowLayoutTask = new Runnable() { // from class: miuix.popupwidget.internal.widget.ArrowPopupView$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.arrowLayout();
            }
        };
        this.mAnchorTrackListener = new View.OnLayoutChangeListener() { // from class: miuix.popupwidget.internal.widget.ArrowPopupView.3
            @Override // android.view.View.OnLayoutChangeListener
            public void onLayoutChange(View view, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9) {
                if (!ArrowPopupView.this.mHasFirstLayout || view == null) {
                    return;
                }
                view.post(ArrowPopupView.this.mArrowLayoutTask);
            }
        };
        this.mArrowMode = 0;
        this.mAlphaAnimationEnabled = true;
        CompatViewMethod.setForceDarkAllowed(this, false);
        this.mAutoDismiss = true;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ArrowPopupView, i, R.style.Widget_ArrowPopupView_DayNight);
        this.mArrowBackgroundPaintColor = typedArrayObtainStyledAttributes.getColor(R.styleable.ArrowPopupView_arrowBackgroundColor, getResources().getColor(R.color.miuix_appcompat_arrow_popup_background_color));
        this.mBackground = typedArrayObtainStyledAttributes.getDrawable(R.styleable.ArrowPopupView_contentBackground);
        this.mBackgroundLeft = typedArrayObtainStyledAttributes.getDrawable(R.styleable.ArrowPopupView_backgroundLeft);
        this.mBackgroundRight = typedArrayObtainStyledAttributes.getDrawable(R.styleable.ArrowPopupView_backgroundRight);
        this.mTitleBackground = typedArrayObtainStyledAttributes.getDrawable(R.styleable.ArrowPopupView_titleBackground);
        this.mArrowTop = typedArrayObtainStyledAttributes.getDrawable(R.styleable.ArrowPopupView_topArrow);
        this.mArrowTopWithTitle = typedArrayObtainStyledAttributes.getDrawable(R.styleable.ArrowPopupView_topArrowWithTitle);
        this.mArrowBottom = typedArrayObtainStyledAttributes.getDrawable(R.styleable.ArrowPopupView_bottomArrow);
        this.mArrowRight = typedArrayObtainStyledAttributes.getDrawable(R.styleable.ArrowPopupView_rightArrow);
        this.mArrowLeft = typedArrayObtainStyledAttributes.getDrawable(R.styleable.ArrowPopupView_leftArrow);
        this.mArrowTopLeft = typedArrayObtainStyledAttributes.getDrawable(R.styleable.ArrowPopupView_topLeftArrow);
        this.mArrowTopRight = typedArrayObtainStyledAttributes.getDrawable(R.styleable.ArrowPopupView_topRightArrow);
        this.mArrowBottomRight = typedArrayObtainStyledAttributes.getDrawable(R.styleable.ArrowPopupView_bottomRightArrow);
        this.mArrowBottomLeft = typedArrayObtainStyledAttributes.getDrawable(R.styleable.ArrowPopupView_bottomLeftArrow);
        this.mElevation = typedArrayObtainStyledAttributes.getDimensionPixelOffset(R.styleable.ArrowPopupView_android_elevation, getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_arrow_popup_window_elevation));
        typedArrayObtainStyledAttributes.recycle();
        this.mMinBorder = context.getResources().getDimensionPixelOffset(R.dimen.miuix_appcompat_arrow_popup_window_min_border);
    }

    public int getPopupElevation() {
        return this.mElevation;
    }

    public void setAutoDismiss(boolean z) {
        this.mAutoDismiss = z;
    }

    public void setLayoutRtlMode(int i) {
        if (i <= 2 && i >= 0) {
            this.mRtlMode = i;
        } else {
            this.mRtlMode = 2;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public int[] getArrowImageHeightAndWidth(int i) {
        int[] iArr = new int[2];
        if (i == 32) {
            iArr[0] = this.mArrowLeft.getIntrinsicHeight();
            iArr[1] = this.mArrowLeft.getIntrinsicWidth();
        } else if (i != 64) {
            switch (i) {
                case 8:
                    iArr[0] = this.mArrowTop.getIntrinsicHeight();
                    iArr[1] = this.mArrowTop.getIntrinsicWidth();
                    break;
                case 9:
                    iArr[0] = this.mArrowTopLeft.getIntrinsicHeight();
                    iArr[1] = this.mArrowTopLeft.getIntrinsicWidth();
                    break;
                case 10:
                    iArr[0] = this.mArrowTopRight.getIntrinsicHeight();
                    iArr[1] = this.mArrowTopRight.getIntrinsicWidth();
                    break;
                default:
                    switch (i) {
                        case 16:
                            iArr[0] = this.mArrowBottom.getIntrinsicHeight();
                            iArr[1] = this.mArrowBottom.getIntrinsicWidth();
                            break;
                        case 17:
                            iArr[0] = this.mArrowBottomRight.getIntrinsicHeight();
                            iArr[1] = this.mArrowBottomRight.getIntrinsicWidth();
                            break;
                        case 18:
                            iArr[0] = this.mArrowBottomLeft.getIntrinsicHeight();
                            iArr[1] = this.mArrowBottomLeft.getIntrinsicWidth();
                            break;
                    }
                    break;
            }
        } else {
            iArr[0] = this.mArrowRight.getIntrinsicHeight();
            iArr[1] = this.mArrowRight.getIntrinsicWidth();
        }
        return iArr;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mArrow = (AppCompatImageView) findViewById(R.id.popup_arrow);
        this.mContentFrame = (FrameLayout) findViewById(android.R.id.content);
        ArrowPopupContentWrapper arrowPopupContentWrapper = (ArrowPopupContentWrapper) findViewById(R.id.content_wrapper);
        this.mContentFrameWrapper = arrowPopupContentWrapper;
        arrowPopupContentWrapper.setArrowBackgroundPaintColor(this.mArrowBackgroundPaintColor);
        this.mContentFrameWrapper.setMinimumHeight(getContext().getResources().getDimensionPixelOffset(R.dimen.miuix_appcompat_arrow_popup_view_min_height));
        if (this.mBackgroundLeft != null && this.mBackgroundRight != null) {
            Rect rect = new Rect();
            this.mBackgroundLeft.getPadding(rect);
            this.mContentFrameWrapper.setPadding(rect.top, rect.top, rect.top, rect.top);
        }
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.title_layout);
        this.mTitleLayout = linearLayout;
        linearLayout.setBackground(this.mTitleBackground);
        this.mTitleText = (AppCompatTextView) findViewById(android.R.id.title);
        this.mPositiveButton = (AppCompatButton) findViewById(16908314);
        this.mNegativeButton = (AppCompatButton) findViewById(16908313);
        this.mPositiveClickListener = new WrapperOnClickListener();
        this.mNegativeClickListener = new WrapperOnClickListener();
        this.mPositiveButton.setOnClickListener(this.mPositiveClickListener);
        this.mNegativeButton.setOnClickListener(this.mNegativeClickListener);
    }

    public int getContentFrameWrapperTopPadding() {
        return this.mContentFrameWrapper.getPaddingTop();
    }

    public int getContentFrameWrapperBottomPadding() {
        return this.mContentFrameWrapper.getPaddingBottom();
    }

    public boolean isTitleEmpty() {
        return TextUtils.isEmpty(this.mTitleText.getText());
    }

    public int getTitleHeight() {
        if (this.mTitleLayout.getVisibility() != 8) {
            return this.mTitleLayout.getMeasuredHeight();
        }
        return 0;
    }

    public void addShadow() {
        addShadow(this.mArrow, new ViewOutlineProvider() { // from class: miuix.popupwidget.internal.widget.ArrowPopupView.4
            @Override // android.view.ViewOutlineProvider
            public void getOutline(View view, Outline outline) {
                if (view.getWidth() == 0 || view.getHeight() == 0) {
                    return;
                }
                int width = view.getWidth();
                int height = view.getHeight();
                boolean z = false;
                Rect rect = new Rect(0, 0, width, height);
                if (width > height) {
                    int i = (width - height) / 2;
                    rect.left += i;
                    rect.right -= i;
                } else {
                    int i2 = (height - width) / 2;
                    rect.top += i2;
                    rect.bottom -= i2;
                }
                Path path = new Path();
                int i3 = ArrowPopupView.this.mArrowMode;
                if (i3 == 32 || i3 == 64) {
                    if ((ArrowPopupView.this.mRtlMode != 1 && ArrowPopupView.this.mArrowMode == 32) || (ArrowPopupView.this.mRtlMode == 1 && ArrowPopupView.this.mArrowMode == 64)) {
                        z = true;
                    }
                    float f = (rect.bottom + rect.top) / 2.0f;
                    if (z) {
                        path.moveTo(rect.right, rect.top);
                        path.quadTo(-rect.width(), f, rect.right, rect.bottom);
                    } else {
                        path.moveTo(rect.left, rect.top);
                        path.quadTo(rect.right + rect.width(), f, rect.left, rect.bottom);
                    }
                    path.close();
                } else {
                    switch (i3) {
                        case 8:
                            float f2 = (rect.right + rect.left) / 2.0f;
                            path.moveTo(rect.left, rect.bottom);
                            path.quadTo(f2, -rect.height(), rect.right, rect.bottom);
                            path.close();
                            break;
                        case 9:
                        case 10:
                            if ((ArrowPopupView.this.mRtlMode != 1 && ArrowPopupView.this.mArrowMode == 9) || (ArrowPopupView.this.mRtlMode == 1 && ArrowPopupView.this.mArrowMode == 10)) {
                                z = true;
                            }
                            path.moveTo(0.0f, ArrowPopupView.this.mArrowTop.getIntrinsicHeight());
                            if (z) {
                                path.quadTo(0.0f, (-ArrowPopupView.this.mArrowTop.getIntrinsicHeight()) * 0.7f, rect.right * 0.52f, ArrowPopupView.this.mArrowTop.getIntrinsicHeight());
                            } else {
                                path.quadTo(rect.right, (-ArrowPopupView.this.mArrowTop.getIntrinsicHeight()) * 0.7f, rect.right * 0.5f, ArrowPopupView.this.mArrowTop.getIntrinsicHeight());
                            }
                            path.close();
                            break;
                    }
                }
                if (path.isConvex()) {
                    outline.setConvexPath(path);
                } else {
                    Log.d(ArrowPopupView.TAG, "outline path is not convex");
                    outline.setOval(rect);
                }
            }
        });
        addShadow(this.mContentFrameWrapper, new ViewOutlineProvider() { // from class: miuix.popupwidget.internal.widget.ArrowPopupView.5
            @Override // android.view.ViewOutlineProvider
            public void getOutline(View view, Outline outline) {
                if (view.getWidth() == 0 || view.getHeight() == 0) {
                    return;
                }
                Rect rect = new Rect(0, 0, view.getWidth(), view.getHeight());
                rect.bottom -= view.getPaddingBottom();
                rect.top += view.getPaddingTop();
                rect.right -= view.getPaddingRight();
                rect.left += view.getPaddingLeft();
                outline.setRoundRect(rect, ArrowPopupView.this.getContext().getResources().getDimensionPixelOffset(R.dimen.miuix_appcompat_arrow_popup_view_round_corners));
            }
        });
    }

    private void addShadow(View view, ViewOutlineProvider viewOutlineProvider) {
        view.setOutlineProvider(viewOutlineProvider);
        view.setElevation(this.mElevation);
    }

    public void setContentView(View view) {
        setContentView(view, new ViewGroup.LayoutParams(-2, -2));
    }

    public void setContentView(View view, ViewGroup.LayoutParams layoutParams) {
        this.mContentFrame.removeAllViews();
        if (view != null) {
            this.mContentFrame.addView(view, layoutParams);
        }
    }

    public void setEnableTrackAnchor(boolean z) {
        this.mEnableTrackAnchor = z;
    }

    public View getContentView() {
        if (this.mContentFrame.getChildCount() > 0) {
            return this.mContentFrame.getChildAt(0);
        }
        return null;
    }

    public void setContentView(int i) {
        setContentView(LayoutInflater.from(getContext()).inflate(i, (ViewGroup) null));
    }

    public void setTitle(CharSequence charSequence) {
        this.mTitleLayout.setVisibility(TextUtils.isEmpty(charSequence) ? 8 : 0);
        this.mTitleText.setText(charSequence);
    }

    public void setNegativeButton(CharSequence charSequence, View.OnClickListener onClickListener) {
        this.mNegativeButton.setText(charSequence);
        this.mNegativeButton.setVisibility(TextUtils.isEmpty(charSequence) ? 8 : 0);
        this.mNegativeClickListener.setOnClickListener(onClickListener);
    }

    public void setPositiveButton(CharSequence charSequence, View.OnClickListener onClickListener) {
        this.mPositiveButton.setText(charSequence);
        this.mPositiveButton.setVisibility(TextUtils.isEmpty(charSequence) ? 8 : 0);
        this.mPositiveClickListener.setOnClickListener(onClickListener);
    }

    public AppCompatButton getPositiveButton() {
        return this.mPositiveButton;
    }

    public AppCompatButton getNegativeButton() {
        return this.mNegativeButton;
    }

    private boolean isVerticalMode() {
        return isTopMode() || isBottomMode();
    }

    private boolean isCertainMode(int i) {
        return (this.mArrowMode & i) == i;
    }

    private boolean isLeftMode() {
        return isCertainMode(32);
    }

    private boolean isRightMode() {
        return isCertainMode(64);
    }

    private boolean isTopMode() {
        return isCertainMode(8);
    }

    private boolean isBottomMode() {
        return isCertainMode(16);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void arrowLayout() {
        int i = this.mRtlMode;
        int i2 = (i == 1 || (i == 2 && ViewUtils.isLayoutRtl(this))) ? -this.mOffsetX : this.mOffsetX;
        if (isVerticalMode()) {
            arrowVerticalLayout(i2);
        } else {
            arrowHorizontalLayout(i2);
        }
        View contentView = getContentView();
        if (contentView != null) {
            ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
            if (contentView.getMeasuredHeight() > this.mContentFrameWrapper.getHeight() - this.mTitleLayout.getHeight()) {
                layoutParams.height = this.mContentFrameWrapper.getHeight() - this.mTitleLayout.getHeight();
                contentView.setLayoutParams(layoutParams);
            } else if (contentView.getMeasuredWidth() > this.mContentFrameWrapper.getWidth()) {
                layoutParams.width = this.mContentFrameWrapper.getWidth();
                contentView.setLayoutParams(layoutParams);
            }
            if (layoutParams.height <= 0 || layoutParams.width <= 0) {
                Log.w(TAG, "Invalid LayoutPrams of content view, please check the anchor view");
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:66:0x017c  */
    /* JADX WARN: Code duplicated, block: B:67:0x017f  */
    /* JADX WARN: Code duplicated, block: B:69:0x0183  */
    /* JADX WARN: Code duplicated, block: B:72:0x018c  */
    /* JADX WARN: Code duplicated, block: B:77:0x01a8  */
    private void arrowVerticalLayout(int i) {
        int i2;
        int i3;
        int i4;
        int i5;
        int width = this.mAnchor.getWidth();
        int height = this.mAnchor.getHeight();
        int width2 = getWidth();
        int height2 = getHeight();
        int iMax = Math.max(this.mContentFrameWrapper.getMeasuredWidth(), this.mContentFrameWrapper.getMinimumWidth());
        int iMax2 = Math.max(this.mContentFrameWrapper.getMeasuredHeight(), this.mContentFrameWrapper.getMinimumHeight());
        float f = getResources().getDisplayMetrics().density;
        int arrowWidth = getArrowWidth(this.mArrowMode);
        int arrowHeight = getArrowHeight(this.mArrowMode);
        int[] iArr = new int[2];
        this.mAnchor.getLocationOnScreen(iArr);
        int i6 = iArr[0];
        int i7 = iArr[1];
        getLocationOnScreen(iArr);
        this.mArrowSpaceLeft = (i6 + ((width - arrowWidth) / 2)) - iArr[0];
        this.mSpaceTop = getTop() + this.mOffsetY;
        if (isBottomMode()) {
            this.mSpaceTop += ((i7 - iArr[1]) - iMax2) + (this.mContentFrameWrapper.getPaddingBottom() - arrowHeight);
        } else if (isTopMode()) {
            this.mSpaceTop += (((i7 + height) - iArr[1]) - this.mContentFrameWrapper.getPaddingTop()) + arrowHeight;
        }
        int i8 = iMax / 2;
        int i9 = iMax - i8;
        int i10 = this.mRtlMode;
        boolean z = i10 == 1 || (i10 == 2 && ViewUtils.isLayoutRtl(this));
        boolean z2 = (!z && this.mArrowMode == 9) || (!z && this.mArrowMode == 18) || ((z && this.mArrowMode == 10) || (z && this.mArrowMode == 17));
        if ((!z && this.mArrowMode == 10) || ((!z && this.mArrowMode == 17) || ((z && this.mArrowMode == 9) || (z && this.mArrowMode == 18)))) {
            this.mSpaceLeft = (int) Math.max(((((i6 + (width / 2)) - iMax) + (f * 28.0f)) + this.mContentFrameWrapper.getPaddingEnd()) - iArr[0], 0.0f);
        } else {
            if (z2) {
                int iMax3 = (int) Math.max((((i6 + (width / 2)) - (f * 28.0f)) - this.mContentFrameWrapper.getPaddingStart()) - iArr[0], 0.0f);
                this.mSpaceLeft = iMax3;
                int i11 = iMax3 + iMax;
                if (i11 > width2) {
                    this.mSpaceLeft = iMax3 + (width2 - i11);
                }
                this.mSpaceLeft = Math.max(this.mSpaceLeft, 0);
                i2 = 0;
            } else {
                int i12 = (i6 + (width / 2)) - iArr[0];
                this.mSpaceLeft = i12;
                int i13 = width2 - i12;
                if (i12 >= i8 && i13 >= i9) {
                    this.mSpaceLeft = i12 - i8;
                } else if (i13 < i9) {
                    this.mSpaceLeft = width2 - iMax;
                } else {
                    i2 = 0;
                    if (i12 < i8) {
                        this.mSpaceLeft = 0;
                    }
                }
            }
            i3 = this.mSpaceLeft + i;
            this.mSpaceLeft = i3;
            i4 = this.mArrowSpaceLeft + i;
            this.mArrowSpaceLeft = i4;
            if (i4 < 0) {
                this.mArrowSpaceLeft = i2;
            } else if (i4 + arrowWidth > width2) {
                this.mArrowSpaceLeft = i4 - ((i4 + arrowWidth) - width2);
            }
            if (i3 + iMax > width2) {
                this.mSpaceLeft = width2 - iMax;
            }
            this.mContentFrameWrapper.setArrowMode(this.mArrowMode);
            this.mContentFrameWrapper.setRtlMode(this.mRtlMode);
            i5 = this.mArrowMode;
            if (i5 != 8 || i5 == 16) {
                this.mContentFrameWrapper.setArrowHorizonOffset(((this.mArrowSpaceLeft + (arrowWidth / 2.0f)) - this.mSpaceLeft) - (iMax / 2.0f));
            }
            this.mContentFrameWrapper.layout(Math.max(this.mSpaceLeft, 0), Math.max(this.mSpaceTop, 0), Math.min(this.mSpaceLeft + iMax, width2), Math.min(Math.max(this.mSpaceTop, 0) + iMax2, height2));
        }
        i2 = 0;
        i3 = this.mSpaceLeft + i;
        this.mSpaceLeft = i3;
        i4 = this.mArrowSpaceLeft + i;
        this.mArrowSpaceLeft = i4;
        if (i4 < 0) {
            this.mArrowSpaceLeft = i2;
        } else if (i4 + arrowWidth > width2) {
            this.mArrowSpaceLeft = i4 - ((i4 + arrowWidth) - width2);
        }
        if (i3 + iMax > width2) {
            this.mSpaceLeft = width2 - iMax;
        }
        this.mContentFrameWrapper.setArrowMode(this.mArrowMode);
        this.mContentFrameWrapper.setRtlMode(this.mRtlMode);
        i5 = this.mArrowMode;
        if (i5 != 8) {
            this.mContentFrameWrapper.setArrowHorizonOffset(((this.mArrowSpaceLeft + (arrowWidth / 2.0f)) - this.mSpaceLeft) - (iMax / 2.0f));
        } else {
            this.mContentFrameWrapper.setArrowHorizonOffset(((this.mArrowSpaceLeft + (arrowWidth / 2.0f)) - this.mSpaceLeft) - (iMax / 2.0f));
        }
        this.mContentFrameWrapper.layout(Math.max(this.mSpaceLeft, 0), Math.max(this.mSpaceTop, 0), Math.min(this.mSpaceLeft + iMax, width2), Math.min(Math.max(this.mSpaceTop, 0) + iMax2, height2));
    }

    private void executeLayoutArrow(int i, int i2, int i3) {
        int right;
        int i4;
        int right2;
        int bottom;
        int measuredHeight;
        int i5 = this.mRtlMode;
        boolean z = i5 == 1 || (i5 == 2 && ViewUtils.isLayoutRtl(this));
        int i6 = this.mArrowMode;
        if (i6 == 9 || i6 == 10) {
            if ((!z && i6 == 9) || (z && i6 == 10)) {
                right = (this.mContentFrameWrapper.getLeft() + this.mContentFrameWrapper.getPaddingStart()) - 1;
            } else {
                right = ((this.mContentFrameWrapper.getRight() - this.mContentFrameWrapper.getPaddingStart()) - i) + 1;
            }
            i3 = (i3 + this.mContentFrameWrapper.getPaddingTop()) - i2;
            AppCompatImageView appCompatImageView = this.mArrow;
            appCompatImageView.layout(right, i3, right + i, appCompatImageView.getMeasuredHeight() + i3);
            i4 = right;
        } else if (i6 == 17 || i6 == 18) {
            if ((!z && i6 == 18) || (z && i6 == 17)) {
                right2 = this.mContentFrameWrapper.getLeft() + this.mContentFrameWrapper.getPaddingStart();
                bottom = this.mContentFrameWrapper.getBottom() - this.mContentFrameWrapper.getPaddingBottom();
                measuredHeight = this.mArrow.getMeasuredHeight();
            } else {
                right2 = (this.mContentFrameWrapper.getRight() - this.mContentFrameWrapper.getPaddingEnd()) - i;
                bottom = this.mContentFrameWrapper.getBottom() - this.mContentFrameWrapper.getPaddingBottom();
                measuredHeight = this.mArrow.getMeasuredHeight();
            }
            int i7 = bottom - (measuredHeight - i2);
            i4 = right2;
            if (this.mArrowMode == 18) {
                AppCompatImageView appCompatImageView2 = this.mArrow;
                appCompatImageView2.layout(i4, i7, i4 + i, appCompatImageView2.getMeasuredHeight() + i7);
            }
            i3 = i7 - 5;
        } else {
            i4 = this.mArrowSpaceLeft;
        }
        AppCompatImageView appCompatImageView3 = this.mArrow;
        appCompatImageView3.layout(i4, i3, i + i4, appCompatImageView3.getDrawable().getIntrinsicHeight() + i3);
    }

    private void arrowHorizontalLayout(int i) {
        int[] iArr = new int[2];
        this.mAnchor.getLocationOnScreen(iArr);
        int i2 = iArr[0];
        int i3 = iArr[1];
        getLocationOnScreen(iArr);
        int width = this.mAnchor.getWidth();
        int height = this.mAnchor.getHeight();
        int width2 = getWidth();
        int height2 = getHeight();
        int iMax = Math.max(this.mContentFrameWrapper.getMeasuredWidth(), this.mContentFrameWrapper.getMinimumWidth());
        int iMax2 = Math.max(this.mContentFrameWrapper.getMeasuredHeight(), this.mContentFrameWrapper.getMinimumHeight());
        int arrowWidth = getArrowWidth(this.mArrowMode);
        int arrowHeight = getArrowHeight(this.mArrowMode);
        int i4 = iArr[1];
        int i5 = ((height / 2) + i3) - i4;
        this.mSpaceTop = i5;
        int i6 = height2 - i5;
        this.mArrowSpaceTop = ((i3 + ((height - arrowHeight) / 2)) - i4) + ((this.mContentFrameWrapper.getPaddingTop() - this.mContentFrameWrapper.getPaddingBottom()) / 2);
        int i7 = iMax2 / 2;
        int i8 = iMax2 - i7;
        this.mSpaceLeft = getLeft() + i;
        if (isRightMode()) {
            int i9 = this.mRtlMode;
            if (i9 == 1 || (i9 == 2 && ViewUtils.isLayoutRtl(this))) {
                this.mSpaceLeft += (((i2 + width) - this.mContentFrameWrapper.getPaddingLeft()) + arrowWidth) - iArr[0];
                this.mContentFrameWrapper.getPaddingLeft();
            } else {
                this.mSpaceLeft += (((i2 - iMax) + this.mContentFrameWrapper.getPaddingRight()) - arrowWidth) - iArr[0];
            }
        } else if (isLeftMode()) {
            int i10 = this.mRtlMode;
            if (i10 == 1 || (i10 == 2 && ViewUtils.isLayoutRtl(this))) {
                this.mSpaceLeft += ((((i2 - iMax) + this.mContentFrameWrapper.getPaddingRight()) - arrowWidth) - iArr[0]) + 1;
            } else {
                this.mSpaceLeft += (((i2 + width) - this.mContentFrameWrapper.getPaddingLeft()) + arrowWidth) - iArr[0];
                this.mContentFrameWrapper.getPaddingLeft();
            }
        }
        int i11 = this.mSpaceTop;
        if (i11 >= i7 && i6 >= i8) {
            this.mSpaceTop = (i11 - i7) + this.mOffsetY;
        } else if (i6 < i8) {
            this.mSpaceTop = (height2 - iMax2) + this.mOffsetY;
        } else if (i11 < i7) {
            this.mSpaceTop = this.mOffsetY;
        }
        int i12 = this.mArrowSpaceTop + this.mOffsetY;
        this.mArrowSpaceTop = i12;
        if (i12 < 0) {
            this.mArrowSpaceTop = 0;
        } else if (i12 + arrowHeight > height2) {
            this.mArrowSpaceTop = i12 - ((i12 + arrowHeight) - height2);
        }
        this.mContentFrameWrapper.setArrowMode(this.mArrowMode);
        this.mContentFrameWrapper.setRtlMode(this.mRtlMode);
        int i13 = this.mArrowMode;
        if (i13 == 32 || i13 == 64) {
            this.mContentFrameWrapper.setArrowVerticalOffset(((this.mArrowSpaceTop + (arrowHeight / 2.0f)) - this.mSpaceTop) - (iMax2 / 2.0f));
        }
        this.mContentFrameWrapper.layout(Math.max(this.mSpaceLeft, 0), Math.max(this.mSpaceTop, 0), Math.min(this.mSpaceLeft + iMax, width2), Math.min(this.mSpaceTop + iMax2, height2));
    }

    public int getArrowWidth(int i) {
        if (i != 32 && i != 64) {
            switch (i) {
                case 8:
                case 9:
                case 10:
                    break;
                default:
                    switch (i) {
                        case 16:
                        case 17:
                        case 18:
                            break;
                        default:
                            return 0;
                    }
                    break;
            }
            return getResources().getDimensionPixelOffset(R.dimen.miuix_appcompat_arrow_popup_arrow_width_vertical);
        }
        return getResources().getDimensionPixelOffset(R.dimen.miuix_appcompat_arrow_popup_arrow_width_horizontal);
    }

    public int getArrowHeight(int i) {
        int i2 = this.mArrowMode;
        if (i2 != 32 && i2 != 64) {
            switch (i2) {
                case 8:
                case 9:
                case 10:
                    break;
                default:
                    switch (i2) {
                        case 16:
                        case 17:
                        case 18:
                            break;
                        default:
                            return 0;
                    }
                    break;
            }
            return getResources().getDimensionPixelOffset(R.dimen.miuix_appcompat_arrow_popup_arrow_height_vertical);
        }
        return getResources().getDimensionPixelOffset(R.dimen.miuix_appcompat_arrow_popup_arrow_height_horizontal);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (!this.mAnchor.isAttachedToWindow()) {
            if (this.mArrowPopupWindow.isShowing()) {
                this.mArrowPopupWindow.dismiss();
            }
        } else {
            if (this.mArrowMode == 0) {
                adjustArrowMode();
            }
            updateArrowDrawable(this.mArrowMode);
            arrowLayout();
            this.mHasFirstLayout = true;
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int right;
        int right2;
        float f;
        if (this.mBackground != null) {
            return;
        }
        int width = this.mSpaceLeft + (this.mContentFrameWrapper.getWidth() / 2);
        int height = this.mSpaceTop + (this.mContentFrameWrapper.getHeight() / 2);
        int i = this.mArrowMode;
        if (i == 8) {
            int measuredWidth = this.mArrowSpaceLeft + (this.mArrow.getMeasuredWidth() / 2);
            right = measuredWidth - this.mSpaceLeft;
            right2 = this.mContentFrameWrapper.getRight() - measuredWidth;
            f = 0.0f;
        } else if (i == 16) {
            int measuredWidth2 = this.mArrowSpaceLeft + (this.mArrow.getMeasuredWidth() / 2);
            right = this.mContentFrameWrapper.getRight() - measuredWidth2;
            right2 = measuredWidth2 - this.mSpaceLeft;
            f = 180.0f;
        } else if (i == 32) {
            int measuredHeight = this.mArrowSpaceTop + (this.mArrow.getMeasuredHeight() / 2);
            right = this.mContentFrameWrapper.getBottom() - measuredHeight;
            right2 = measuredHeight - this.mSpaceTop;
            f = -90.0f;
        } else if (i != 64) {
            f = 0.0f;
            right = 0;
            right2 = 0;
        } else {
            int measuredHeight2 = this.mArrowSpaceTop + (this.mArrow.getMeasuredHeight() / 2);
            right = measuredHeight2 - this.mSpaceTop;
            right2 = this.mContentFrameWrapper.getBottom() - measuredHeight2;
            f = 90.0f;
        }
        int iSave = canvas.save();
        canvas.rotate(f, width, height);
        int i2 = this.mArrowMode;
        if (i2 == 8 || i2 == 16) {
            canvas.translate(this.mSpaceLeft, this.mSpaceTop);
            this.mBackgroundLeft.setBounds(0, 0, right, this.mContentFrameWrapper.getHeight());
            canvas.translate(0.0f, isTopMode() ? this.mTranslationValue : -this.mTranslationValue);
            this.mBackgroundLeft.draw(canvas);
            canvas.translate(right, 0.0f);
            this.mBackgroundRight.setBounds(0, 0, right2, this.mContentFrameWrapper.getHeight());
            this.mBackgroundRight.draw(canvas);
        } else if (i2 == 32 || i2 == 64) {
            canvas.translate(width - (this.mContentFrameWrapper.getHeight() / 2), height - (this.mContentFrameWrapper.getWidth() / 2));
            this.mBackgroundLeft.setBounds(0, 0, right, this.mContentFrameWrapper.getWidth());
            canvas.translate(0.0f, isLeftMode() ? this.mTranslationValue : -this.mTranslationValue);
            this.mBackgroundLeft.draw(canvas);
            canvas.translate(right, 0.0f);
            this.mBackgroundRight.setBounds(0, 0, right2, this.mContentFrameWrapper.getWidth());
            this.mBackgroundRight.draw(canvas);
        }
        canvas.restoreToCount(iSave);
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        final View contentView = getContentView();
        if (contentView != null) {
            contentView.post(new Runnable() { // from class: miuix.popupwidget.internal.widget.ArrowPopupView.6
                @Override // java.lang.Runnable
                public void run() {
                    contentView.requestLayout();
                    contentView.invalidate();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getAnimationPivot(float[] fArr) {
        float right;
        float bottom;
        int height;
        float f;
        int top;
        int top2 = this.mArrow.getTop();
        int bottom2 = this.mArrow.getBottom();
        int left = this.mArrow.getLeft();
        int right2 = this.mArrow.getRight();
        int i = this.mRtlMode;
        boolean z = i == 1 || (i == 2 && ViewUtils.isLayoutRtl(this));
        int i2 = this.mArrowMode;
        if (i2 == 32) {
            ArrowPopupContentWrapper arrowPopupContentWrapper = this.mContentFrameWrapper;
            right = z ? arrowPopupContentWrapper.getRight() : arrowPopupContentWrapper.getLeft();
            bottom = this.mContentFrameWrapper.getBottom();
            height = this.mContentFrameWrapper.getHeight();
        } else {
            if (i2 == 64) {
                ArrowPopupContentWrapper arrowPopupContentWrapper2 = this.mContentFrameWrapper;
                right = z ? arrowPopupContentWrapper2.getLeft() : arrowPopupContentWrapper2.getRight();
                bottom = this.mContentFrameWrapper.getBottom();
                height = this.mContentFrameWrapper.getHeight();
            } else {
                switch (i2) {
                    case 8:
                        right = this.mContentFrameWrapper.getRight() - (this.mContentFrameWrapper.getWidth() / 2.0f);
                        top = this.mContentFrameWrapper.getTop();
                        f = top;
                        break;
                    case 9:
                        ArrowPopupContentWrapper arrowPopupContentWrapper3 = this.mContentFrameWrapper;
                        right = z ? arrowPopupContentWrapper3.getRight() : arrowPopupContentWrapper3.getLeft();
                        top = this.mContentFrameWrapper.getTop();
                        f = top;
                        break;
                    case 10:
                        ArrowPopupContentWrapper arrowPopupContentWrapper4 = this.mContentFrameWrapper;
                        right = z ? arrowPopupContentWrapper4.getLeft() : arrowPopupContentWrapper4.getRight();
                        top = this.mContentFrameWrapper.getTop();
                        f = top;
                        break;
                    default:
                        switch (i2) {
                            case 16:
                                right = this.mContentFrameWrapper.getRight() - (this.mContentFrameWrapper.getWidth() / 2.0f);
                                top = this.mContentFrameWrapper.getBottom();
                                f = top;
                                break;
                            case 17:
                                ArrowPopupContentWrapper arrowPopupContentWrapper5 = this.mContentFrameWrapper;
                                right = z ? arrowPopupContentWrapper5.getLeft() : arrowPopupContentWrapper5.getRight();
                                top = this.mContentFrameWrapper.getBottom();
                                f = top;
                                break;
                            case 18:
                                ArrowPopupContentWrapper arrowPopupContentWrapper6 = this.mContentFrameWrapper;
                                right = z ? arrowPopupContentWrapper6.getRight() : arrowPopupContentWrapper6.getLeft();
                                top = this.mContentFrameWrapper.getBottom();
                                f = top;
                                break;
                            default:
                                right = (right2 + left) / 2;
                                f = (bottom2 + top2) / 2;
                                break;
                        }
                        break;
                }
            }
            fArr[0] = right;
            fArr[1] = f;
        }
        f = bottom - (height / 2.0f);
        fArr[0] = right;
        fArr[1] = f;
    }

    public void animateToShow() {
        invalidate();
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: miuix.popupwidget.internal.widget.ArrowPopupView.7
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public boolean onPreDraw() {
                ArrowPopupView.this.getViewTreeObserver().removeOnPreDrawListener(this);
                if (ArrowPopupView.this.mAnimator != null) {
                    ArrowPopupView.this.mAnimator.cancel();
                }
                if (ArrowPopupView.this.mAnimationSet != null) {
                    ArrowPopupView.this.mAnimationSet.cancel();
                }
                ArrowPopupView.this.mAnimationSet = new AnimationSet(true);
                float[] fArr = new float[2];
                ArrowPopupView.this.getAnimationPivot(fArr);
                ScaleAnimation scaleAnimation = new ScaleAnimation(0.6f, 1.0f, 0.6f, 1.0f, 0, fArr[0], 0, fArr[1]);
                AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
                if (!DeviceHelper.isFeatureWholeAnim()) {
                    ArrowPopupView.this.mAnimationSet.setDuration(0L);
                } else {
                    alphaAnimation.setDuration(100L);
                    scaleAnimation.setDuration(280L);
                }
                ArrowPopupView.this.mAnimationSet.addAnimation(scaleAnimation);
                if (ArrowPopupView.this.mAlphaAnimationEnabled) {
                    ArrowPopupView.this.mAnimationSet.addAnimation(alphaAnimation);
                }
                ArrowPopupView.this.mAnimationSet.setAnimationListener(ArrowPopupView.this.mShowAnimationListener);
                ArrowPopupView.this.mAnimationSet.setInterpolator(new DecelerateInterpolator(1.5f));
                ArrowPopupView arrowPopupView = ArrowPopupView.this;
                arrowPopupView.startAnimation(arrowPopupView.mAnimationSet);
                return true;
            }
        });
    }

    public void animateToDismiss() {
        if (this.mIsDismissing) {
            return;
        }
        AnimatorSet animatorSet = this.mAnimator;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        AnimationSet animationSet = this.mAnimationSet;
        if (animationSet != null) {
            animationSet.cancel();
        }
        this.mAnimationSet = new AnimationSet(true);
        float[] fArr = new float[2];
        getAnimationPivot(fArr);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.6f, 1.0f, 0.6f, 0, fArr[0], 0, fArr[1]);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        if (DeviceHelper.isFeatureWholeAnim()) {
            scaleAnimation.setDuration(150L);
            alphaAnimation.setDuration(150L);
        } else {
            this.mAnimationSet.setDuration(0L);
        }
        this.mAnimationSet.addAnimation(scaleAnimation);
        if (this.mAlphaAnimationEnabled) {
            this.mAnimationSet.addAnimation(alphaAnimation);
        }
        this.mAnimationSet.setAnimationListener(this.mHideAnimatorListener);
        this.mAnimationSet.setInterpolator(new AccelerateInterpolator(2.0f));
        startAnimation(this.mAnimationSet);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void animateShowing() {
        if (DeviceHelper.isFeatureWholeAnim()) {
            AnimationSet animationSet = this.mAnimationSet;
            if (animationSet != null) {
                animationSet.cancel();
            }
            AnimatorSet animatorSet = this.mAnimator;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.mAnimator = animatorSet2;
            animatorSet2.addListener(new AnimatorListenerAdapter() { // from class: miuix.popupwidget.internal.widget.ArrowPopupView.8
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    ArrowPopupView.this.mArrowPopupWindow.dismiss();
                }
            });
            float f = getContext().getResources().getDisplayMetrics().density * 4.0f;
            Property property = View.TRANSLATION_Y;
            int i = this.mRtlMode;
            boolean z = i == 1 || (i == 2 && ViewUtils.isLayoutRtl(this));
            int i2 = this.mArrowMode;
            if (i2 == 16) {
                f = -f;
            } else if (i2 == 32) {
                if (z) {
                    f = -f;
                }
                property = View.TRANSLATION_X;
            } else if (i2 == 64) {
                if (!z) {
                    f = -f;
                }
                property = View.TRANSLATION_X;
            }
            ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(this.mContentFrameWrapper, (Property<ArrowPopupContentWrapper, Float>) property, 0.0f, f, 0.0f);
            objectAnimatorOfFloat.setInterpolator(new AccelerateDecelerateInterpolator());
            objectAnimatorOfFloat.setDuration(1200L);
            if (this.mAutoDismiss) {
                objectAnimatorOfFloat.setRepeatCount(8);
            } else {
                objectAnimatorOfFloat.setRepeatCount(-1);
            }
            objectAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: miuix.popupwidget.internal.widget.ArrowPopupView.9
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ArrowPopupView.this.mTranslationValue = ((Float) valueAnimator.getAnimatedValue()).intValue();
                    int iAbs = Math.abs(ArrowPopupView.this.mTranslationValue);
                    ArrowPopupView arrowPopupView = ArrowPopupView.this;
                    arrowPopupView.invalidate(arrowPopupView.mContentFrameWrapper.getLeft() - iAbs, ArrowPopupView.this.mContentFrameWrapper.getTop() - iAbs, ArrowPopupView.this.mContentFrameWrapper.getRight() + iAbs, ArrowPopupView.this.mContentFrameWrapper.getBottom() + iAbs);
                }
            });
            ObjectAnimator objectAnimatorOfFloat2 = ObjectAnimator.ofFloat(this.mArrow, (Property<AppCompatImageView, Float>) property, 0.0f, f, 0.0f);
            objectAnimatorOfFloat2.setInterpolator(new AccelerateDecelerateInterpolator());
            objectAnimatorOfFloat2.setDuration(1200L);
            if (this.mAutoDismiss) {
                objectAnimatorOfFloat2.setRepeatCount(8);
            } else {
                objectAnimatorOfFloat2.setRepeatCount(-1);
            }
            this.mAnimator.playTogether(objectAnimatorOfFloat, objectAnimatorOfFloat2);
            this.mAnimator.start();
        }
    }

    private void adjustArrowMode() {
        int[] iArr = new int[2];
        this.mAnchor.getLocationInWindow(iArr);
        int width = getWidth();
        int height = getHeight();
        int measuredWidth = this.mContentFrameWrapper.getMeasuredWidth();
        int measuredHeight = this.mContentFrameWrapper.getMeasuredHeight();
        int height2 = this.mAnchor.getHeight();
        int width2 = this.mAnchor.getWidth();
        SparseIntArray sparseIntArray = new SparseIntArray(4);
        int i = 16;
        sparseIntArray.put(16, iArr[1] - measuredHeight);
        sparseIntArray.put(8, ((height - iArr[1]) - height2) - measuredHeight);
        sparseIntArray.put(64, iArr[0] - measuredWidth);
        sparseIntArray.put(32, ((width - iArr[0]) - width2) - measuredWidth);
        int i2 = Integer.MIN_VALUE;
        for (int i3 = 0; i3 < sparseIntArray.size(); i3++) {
            int iKeyAt = sparseIntArray.keyAt(i3);
            if (sparseIntArray.get(iKeyAt) >= this.mMinBorder) {
                i = iKeyAt;
                break;
            }
            if (sparseIntArray.get(iKeyAt) > i2) {
                i2 = sparseIntArray.get(iKeyAt);
                i = iKeyAt;
            }
        }
        setArrowMode(i);
    }

    public int getArrowMode() {
        return this.mArrowMode;
    }

    public void setArrowMode(int i) {
        this.mArrowMode = i;
        updateArrowDrawable(i);
    }

    private void updateArrowDrawable(int i) {
        int i2 = this.mRtlMode;
        boolean z = true;
        if (i2 != 1 && (i2 != 2 || !ViewUtils.isLayoutRtl(this))) {
            z = false;
        }
        if (i == 32) {
            this.mArrow.setImageDrawable(z ? this.mArrowRight : this.mArrowLeft);
            return;
        }
        if (i != 64) {
            switch (i) {
                case 8:
                    this.mArrow.setImageDrawable(this.mTitleLayout.getVisibility() == 0 ? this.mArrowTopWithTitle : this.mArrowTop);
                    break;
                case 9:
                    this.mArrow.setImageDrawable(z ? this.mArrowTopRight : this.mArrowTopLeft);
                    break;
                case 10:
                    this.mArrow.setImageDrawable(z ? this.mArrowTopLeft : this.mArrowTopRight);
                    break;
                default:
                    switch (i) {
                        case 16:
                            this.mArrow.setImageDrawable(this.mArrowBottom);
                            break;
                        case 17:
                            this.mArrow.setImageDrawable(z ? this.mArrowBottomLeft : this.mArrowBottomRight);
                            break;
                        case 18:
                            this.mArrow.setImageDrawable(z ? this.mArrowBottomRight : this.mArrowBottomLeft);
                            break;
                    }
                    break;
            }
            return;
        }
        this.mArrow.setImageDrawable(z ? this.mArrowLeft : this.mArrowRight);
    }

    public void setAnchor(View view) {
        View view2 = this.mAnchor;
        if (view2 != null) {
            view2.removeOnLayoutChangeListener(this.mAnchorTrackListener);
        }
        this.mAnchor = view;
        this.mHasFirstLayout = false;
        if (!this.mEnableTrackAnchor || view == null) {
            return;
        }
        view.addOnLayoutChangeListener(this.mAnchorTrackListener);
    }

    public void setOffset(int i, int i2) {
        this.mOffsetX = i;
        this.mOffsetY = i2;
    }

    public void setArrowPopupWindow(ArrowPopupWindow arrowPopupWindow) {
        this.mArrowPopupWindow = arrowPopupWindow;
    }

    public void setTouchInterceptor(View.OnTouchListener onTouchListener) {
        this.mTouchInterceptor = onTouchListener;
    }

    public void setAlphaAnimation(boolean z) {
        this.mAlphaAnimationEnabled = z;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        View view = this.mAnchor;
        if (view != null) {
            view.removeOnLayoutChangeListener(this.mAnchorTrackListener);
            this.mAnchor.removeCallbacks(this.mArrowLayoutTask);
        }
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        Rect rect = this.mTmpRect;
        this.mContentFrameWrapper.getHitRect(rect);
        if (motionEvent.getAction() == 0 && !rect.contains(x, y)) {
            this.mArrowPopupWindow.dismiss(true);
            return true;
        }
        View.OnTouchListener onTouchListener = this.mTouchInterceptor;
        return onTouchListener != null && onTouchListener.onTouch(view, motionEvent);
    }

    public void enableShowingAnimation(boolean z) {
        this.mShowingAnimation = z;
    }

    class WrapperOnClickListener implements View.OnClickListener {
        public View.OnClickListener mOnClickListener;

        WrapperOnClickListener() {
        }

        public void setOnClickListener(View.OnClickListener onClickListener) {
            this.mOnClickListener = onClickListener;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            View.OnClickListener onClickListener = this.mOnClickListener;
            if (onClickListener != null) {
                onClickListener.onClick(view);
            }
            ArrowPopupView.this.mArrowPopupWindow.dismiss(true);
        }
    }
}
