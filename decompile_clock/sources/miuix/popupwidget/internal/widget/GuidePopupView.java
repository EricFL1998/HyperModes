package miuix.popupwidget.internal.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.appcompat.widget.AppCompatTextView;
import miuix.popupwidget.R;
import miuix.popupwidget.widget.GuidePopupWindow;

/* JADX INFO: loaded from: classes3.dex */
public class GuidePopupView extends FrameLayout implements View.OnTouchListener {
    public static final int ARROW_BOTTOM_LEFT_MODE = 7;
    public static final int ARROW_BOTTOM_MODE = 0;
    public static final int ARROW_BOTTOM_RIGHT_MODE = 5;
    public static final int ARROW_LEFT_MODE = 3;
    public static final int ARROW_NONE_MODE = -1;
    public static final int ARROW_RIGHT_MODE = 2;
    public static final int ARROW_TOP_LEFT_MODE = 4;
    public static final int ARROW_TOP_MODE = 1;
    public static final int ARROW_TOP_RIGHT_MODE = 6;
    private View mAnchor;
    private int mAnchorHeight;
    private int mAnchorLocationX;
    private int mAnchorLocationY;
    private int mAnchorWidth;
    private ObjectAnimator mAnimator;
    private int mArrowMode;
    private int mColorBackground;
    private Context mContext;
    private int mDefaultOffset;
    private GuidePopupWindow mGuidePopupWindow;
    private Animator.AnimatorListener mHideAnimatorListener;
    private boolean mIsDismissing;
    private boolean mIsMirrored;
    private float mLineLength;
    private int mMinBorder;
    private LinearLayout mMirroredTextGroup;
    private int mOffsetX;
    private int mOffsetY;
    private final Paint mPaint;
    private Animator.AnimatorListener mShowAnimatorListener;
    private float mStartPointRadius;
    private float mTextCircleRadius;
    private ColorStateList mTextColor;
    private LinearLayout mTextGroup;
    private int mTextSize;
    private View.OnTouchListener mTouchInterceptor;
    private boolean mUseDefaultOffset;

    public GuidePopupView(Context context) {
        this(context, null);
    }

    public GuidePopupView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.guidePopupViewStyle);
    }

    public GuidePopupView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mUseDefaultOffset = true;
        this.mTextColor = null;
        Paint paint = new Paint();
        this.mPaint = paint;
        this.mShowAnimatorListener = new AnimatorListenerAdapter() { // from class: miuix.popupwidget.internal.widget.GuidePopupView.1
            private boolean mCancel;

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
                this.mCancel = false;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                this.mCancel = true;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                if (this.mCancel) {
                    return;
                }
                GuidePopupView.this.mAnimator = null;
            }
        };
        this.mHideAnimatorListener = new AnimatorListenerAdapter() { // from class: miuix.popupwidget.internal.widget.GuidePopupView.2
            private boolean mCancel;

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
                this.mCancel = false;
                GuidePopupView.this.mIsDismissing = true;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                this.mCancel = true;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                if (this.mCancel) {
                    return;
                }
                GuidePopupView.this.mIsDismissing = false;
                GuidePopupView.this.mAnimator = null;
                GuidePopupView.this.mGuidePopupWindow.dismiss();
                GuidePopupView.this.setArrowMode(0);
            }
        };
        this.mArrowMode = -1;
        this.mContext = context;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.GuidePopupView, i, R.style.Widget_GuidePopupView_DayNight);
        this.mStartPointRadius = typedArrayObtainStyledAttributes.getDimension(R.styleable.GuidePopupView_startPointRadius, 0.0f);
        this.mLineLength = typedArrayObtainStyledAttributes.getDimension(R.styleable.GuidePopupView_lineLength, 0.0f);
        this.mTextCircleRadius = typedArrayObtainStyledAttributes.getDimension(R.styleable.GuidePopupView_textCircleRadius, 0.0f);
        this.mColorBackground = typedArrayObtainStyledAttributes.getColor(R.styleable.GuidePopupView_android_colorBackground, 0);
        paint.setColor(typedArrayObtainStyledAttributes.getColor(R.styleable.GuidePopupView_paintColor, -1));
        this.mTextSize = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.GuidePopupView_android_textSize, 15);
        this.mTextColor = typedArrayObtainStyledAttributes.getColorStateList(R.styleable.GuidePopupView_android_textColor);
        typedArrayObtainStyledAttributes.recycle();
        this.mMinBorder = (int) (this.mLineLength + (this.mTextCircleRadius * 2.5f));
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mTextGroup = (LinearLayout) findViewById(R.id.text_group);
        this.mMirroredTextGroup = (LinearLayout) findViewById(R.id.mirrored_text_group);
    }

    /* JADX WARN: Code duplicated, block: B:28:0x005a  */
    /* JADX WARN: Code duplicated, block: B:40:0x0079  */
    /* JADX WARN: Code duplicated, block: B:43:0x0081  */
    /* JADX WARN: Code duplicated, block: B:44:0x0083  */
    private void adjustArrowMode() {
        int i;
        int width = getWidth();
        int height = getHeight();
        int i2 = this.mAnchorLocationY;
        int i3 = this.mAnchorHeight;
        int i4 = this.mAnchorLocationX;
        int i5 = this.mAnchorWidth;
        int[] iArr = {i2, (height - i2) - i3, i4, (width - i4) - i5};
        int i6 = i4 + (i5 / 2);
        int i7 = i2 + (i3 / 2);
        int i8 = 0;
        int i9 = Integer.MIN_VALUE;
        int i10 = 0;
        while (true) {
            i = 4;
            if (i8 >= 4) {
                i8 = i10;
                break;
            }
            int i11 = iArr[i8];
            if (i11 >= this.mMinBorder) {
                break;
            }
            if (i11 > i9) {
                i10 = i8;
                i9 = i11;
            }
            i8++;
        }
        if (i8 == 0) {
            float f = i6;
            float f2 = this.mTextCircleRadius;
            if (f < f2) {
                i = 7;
            } else if (width - i6 < f2) {
                i = 5;
            } else {
                i = i8;
            }
        } else if (i8 != 1) {
            if (i8 == 2) {
                float f3 = i7;
                float f4 = this.mTextCircleRadius;
                if (f3 < f4) {
                    i = 6;
                } else if (height - i7 < f4) {
                    i = 5;
                }
            } else if (i8 == 3) {
                float f5 = i7;
                float f6 = this.mTextCircleRadius;
                if (f5 >= f6) {
                    if (height - i7 < f6) {
                        i = 7;
                    }
                }
            }
            i = i8;
        } else {
            float f7 = i6;
            float f8 = this.mTextCircleRadius;
            if (f7 >= f8) {
                if (width - i6 < f8) {
                    i = 6;
                } else {
                    i = i8;
                }
            }
        }
        setArrowMode(i);
    }

    private void arrowLayout() {
        caculateDefaultOffset();
        drawText(this.mArrowMode, this.mTextGroup, this.mOffsetX, this.mOffsetY);
        if (this.mIsMirrored) {
            drawText(getMirroredMode(), this.mMirroredTextGroup, -this.mOffsetX, -this.mOffsetY);
        }
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.mAnchorWidth == 0 || this.mAnchorHeight == 0) {
            setAnchor(this.mAnchor);
        }
        this.mTextCircleRadius = (float) Math.max(Math.sqrt(Math.pow(this.mTextGroup.getMeasuredWidth(), 2.0d) + Math.pow(this.mTextGroup.getMeasuredHeight(), 2.0d)) / 2.0d, this.mTextCircleRadius);
        if (this.mIsMirrored) {
            this.mTextCircleRadius = (float) Math.max(Math.sqrt(Math.pow(this.mMirroredTextGroup.getMeasuredWidth(), 2.0d) + Math.pow(this.mMirroredTextGroup.getMeasuredHeight(), 2.0d)) / 2.0d, this.mTextCircleRadius);
        }
        if (this.mArrowMode == -1) {
            adjustArrowMode();
        } else {
            arrowLayout();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.save();
        canvas.translate(this.mAnchorLocationX, this.mAnchorLocationY);
        this.mAnchor.setDrawingCacheEnabled(true);
        this.mAnchor.buildDrawingCache();
        Bitmap drawingCache = this.mAnchor.getDrawingCache();
        if (drawingCache != null) {
            canvas.drawBitmap(drawingCache, 0.0f, 0.0f, (Paint) null);
        }
        this.mAnchor.setDrawingCacheEnabled(false);
        canvas.restore();
        drawPopup(canvas, this.mArrowMode, this.mOffsetX, this.mOffsetY);
        if (this.mIsMirrored) {
            drawPopup(canvas, getMirroredMode(), -this.mOffsetX, -this.mOffsetY);
        }
    }

    private void drawPopup(Canvas canvas, int i, int i2, int i3) {
        float f;
        this.mPaint.setAntiAlias(true);
        this.mPaint.setStyle(Paint.Style.FILL);
        float f2 = this.mAnchorLocationX + (this.mAnchorWidth / 2) + i2;
        float f3 = this.mAnchorLocationY + (this.mAnchorHeight / 2) + i3;
        switch (i) {
            case 0:
                f = 180.0f;
                break;
            case 1:
            default:
                f = 0.0f;
                break;
            case 2:
                f = 90.0f;
                break;
            case 3:
                f = -90.0f;
                break;
            case 4:
                f = -45.0f;
                break;
            case 5:
                f = 135.0f;
                break;
            case 6:
                f = 45.0f;
                break;
            case 7:
                f = -135.0f;
                break;
        }
        canvas.save();
        canvas.rotate(f, f2, f3);
        canvas.translate(0.0f, this.mDefaultOffset);
        int iSave = canvas.save();
        canvas.clipRect(f2 - 2.0f, f3, f2 + 2.0f, f3 + this.mStartPointRadius, Region.Op.DIFFERENCE);
        canvas.drawCircle(f2, f3, this.mStartPointRadius, this.mPaint);
        canvas.restoreToCount(iSave);
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeWidth(4.0f);
        canvas.drawLine(f2, f3, f2, f3 + this.mLineLength, this.mPaint);
        float f4 = f3 + this.mLineLength + this.mTextCircleRadius;
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeWidth(4.0f);
        canvas.drawCircle(f2, f4, this.mTextCircleRadius, this.mPaint);
        canvas.restore();
    }

    private void drawText(int i, LinearLayout linearLayout, int i2, int i3) {
        int measuredWidth;
        float f;
        int measuredHeight;
        int i4;
        int measuredHeight2;
        float f2 = this.mDefaultOffset + this.mLineLength + this.mTextCircleRadius;
        int i5 = this.mAnchorLocationX + (this.mAnchorWidth / 2);
        int i6 = this.mAnchorLocationY + (this.mAnchorHeight / 2);
        switch (i) {
            case 0:
            case 5:
            case 7:
                measuredWidth = i5 - (linearLayout.getMeasuredWidth() / 2);
                f = i6 - f2;
                measuredHeight = linearLayout.getMeasuredHeight() / 2;
                i4 = (int) (f - measuredHeight);
                break;
            case 1:
            case 4:
            case 6:
                measuredWidth = i5 - (linearLayout.getMeasuredWidth() / 2);
                f = i6 + f2;
                measuredHeight = linearLayout.getMeasuredHeight() / 2;
                i4 = (int) (f - measuredHeight);
                break;
            case 2:
                measuredWidth = (int) ((i5 - f2) - (linearLayout.getMeasuredWidth() / 2));
                measuredHeight2 = linearLayout.getMeasuredHeight() / 2;
                i4 = i6 - measuredHeight2;
                break;
            case 3:
                measuredWidth = (int) ((i5 + f2) - (linearLayout.getMeasuredWidth() / 2));
                measuredHeight2 = linearLayout.getMeasuredHeight() / 2;
                i4 = i6 - measuredHeight2;
                break;
            default:
                measuredWidth = 0;
                i4 = 0;
                break;
        }
        int iSin = (int) (((double) f2) * Math.sin(0.7853981633974483d));
        int i7 = (int) (f2 - iSin);
        if (i != 4) {
            if (i != 5) {
                if (i == 6) {
                    measuredWidth -= iSin;
                } else if (i == 7) {
                    measuredWidth += iSin;
                }
                int i8 = measuredWidth + i2;
                int i9 = i4 + i3;
                linearLayout.layout(i8, i9, linearLayout.getMeasuredWidth() + i8, linearLayout.getMeasuredHeight() + i9);
            }
            measuredWidth -= iSin;
            i4 += i7;
            int i10 = measuredWidth + i2;
            int i11 = i4 + i3;
            linearLayout.layout(i10, i11, linearLayout.getMeasuredWidth() + i10, linearLayout.getMeasuredHeight() + i11);
        }
        measuredWidth += iSin;
        i4 -= i7;
        int i12 = measuredWidth + i2;
        int i13 = i4 + i3;
        linearLayout.layout(i12, i13, linearLayout.getMeasuredWidth() + i12, linearLayout.getMeasuredHeight() + i13);
    }

    private void caculateDefaultOffset() {
        if (!this.mUseDefaultOffset) {
            this.mDefaultOffset = 0;
            return;
        }
        int i = this.mAnchorWidth / 2;
        int i2 = this.mAnchorHeight / 2;
        int iSqrt = (int) Math.sqrt(Math.pow(i, 2.0d) + Math.pow(i2, 2.0d));
        int i3 = this.mArrowMode;
        if (i3 == 0 || i3 == 1) {
            this.mDefaultOffset = i2;
        } else if (i3 == 2 || i3 == 3) {
            this.mDefaultOffset = i;
        } else {
            this.mDefaultOffset = iSqrt;
        }
    }

    private int getMirroredMode() {
        int i = this.mArrowMode;
        if (i == -1) {
            return -1;
        }
        return i % 2 == 0 ? i + 1 : i - 1;
    }

    public void animateToShow() {
        setAlpha(0.0f);
        invalidate();
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: miuix.popupwidget.internal.widget.GuidePopupView.3
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public boolean onPreDraw() {
                GuidePopupView.this.getViewTreeObserver().removeOnPreDrawListener(this);
                if (GuidePopupView.this.mAnimator != null) {
                    GuidePopupView.this.mAnimator.cancel();
                }
                GuidePopupView guidePopupView = GuidePopupView.this;
                guidePopupView.mAnimator = ObjectAnimator.ofFloat(guidePopupView, (Property<GuidePopupView, Float>) View.ALPHA, 1.0f);
                GuidePopupView.this.mAnimator.setDuration(300L);
                GuidePopupView.this.mAnimator.addListener(GuidePopupView.this.mShowAnimatorListener);
                GuidePopupView.this.mAnimator.start();
                return true;
            }
        });
    }

    public void animateToDismiss() {
        if (this.mIsDismissing) {
            return;
        }
        ObjectAnimator objectAnimator = this.mAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
        ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(this, (Property<GuidePopupView, Float>) View.ALPHA, 0.0f);
        this.mAnimator = objectAnimatorOfFloat;
        objectAnimatorOfFloat.setDuration(200L);
        this.mAnimator.addListener(this.mHideAnimatorListener);
        this.mAnimator.start();
    }

    public int getArrowMode() {
        return this.mArrowMode;
    }

    public void setArrowMode(int i) {
        this.mArrowMode = i;
    }

    public void setArrowMode(int i, boolean z) {
        setArrowMode(i);
        this.mIsMirrored = z;
        if (z) {
            this.mMirroredTextGroup.setVisibility(0);
        } else {
            this.mMirroredTextGroup.setVisibility(8);
        }
    }

    public void setAnchor(View view) {
        this.mAnchor = view;
        this.mAnchorWidth = view.getWidth();
        this.mAnchorHeight = this.mAnchor.getHeight();
        int[] iArr = new int[2];
        this.mAnchor.getLocationInWindow(iArr);
        this.mAnchorLocationX = iArr[0];
        this.mAnchorLocationY = iArr[1];
    }

    public void setOffset(int i, int i2) {
        this.mOffsetX = i;
        this.mOffsetY = i2;
        this.mUseDefaultOffset = false;
    }

    public void clearOffset() {
        setOffset(0, 0);
        this.mUseDefaultOffset = true;
    }

    public void setGuidePopupWindow(GuidePopupWindow guidePopupWindow) {
        this.mGuidePopupWindow = guidePopupWindow;
    }

    public int getColorBackground() {
        return this.mColorBackground;
    }

    public void addGuideTextView(LinearLayout linearLayout, String str) {
        String[] strArrSplit;
        if (TextUtils.isEmpty(str) || (strArrSplit = str.split("\n")) == null || strArrSplit.length == 0) {
            return;
        }
        for (String str2 : strArrSplit) {
            AppCompatTextView appCompatTextView = (AppCompatTextView) inflate(this.mContext, R.layout.miuix_appcompat_guide_popup_text_view, null);
            appCompatTextView.setText(str2);
            appCompatTextView.setTextSize(0, this.mTextSize);
            ColorStateList colorStateList = this.mTextColor;
            if (colorStateList != null) {
                appCompatTextView.setTextColor(colorStateList);
            }
            linearLayout.addView(appCompatTextView);
        }
    }

    public void setTouchInterceptor(View.OnTouchListener onTouchListener) {
        this.mTouchInterceptor = onTouchListener;
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        int i = this.mAnchorLocationX;
        Rect rect = new Rect(i, this.mAnchorLocationY, this.mAnchor.getWidth() + i, this.mAnchorLocationY + this.mAnchor.getHeight());
        if (motionEvent.getAction() == 0 && rect.contains(x, y)) {
            this.mAnchor.callOnClick();
            return true;
        }
        this.mGuidePopupWindow.dismiss(true);
        return true;
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
            GuidePopupView.this.mGuidePopupWindow.dismiss(true);
        }
    }
}
