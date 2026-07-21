package miuix.navigation;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import androidx.core.view.ViewCompat;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.List;
import miuix.animation.Folme;
import miuix.animation.IStateStyle;
import miuix.animation.base.AnimConfig;
import miuix.animation.listener.TransitionListener;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.utils.EaseManager;
import miuix.core.util.WindowUtils;
import miuix.internal.util.ViewUtils;
import miuix.navigation.utils.Utils;

/* JADX INFO: loaded from: classes.dex */
public class SplitLayout extends ViewGroup {
    private static final int ABSOLUTE = 0;
    private static final String PROPERTY = "ratio";
    private static final int RELATIVE_TO_PARENT = 1;
    public static final int SPLIT_MODE_OVERLAY = 0;
    public static final int SPLIT_MODE_PUSH_AWAY = 2;
    public static final int SPLIT_MODE_SQUEEZED = 1;
    public static final String TAG_CLOSE = "close";
    public static final String TAG_OPEN = "open";
    private final AnimConfig mAnimConfig;
    private boolean mCollapseOnTouchOutside;
    private String mContentIdString;
    private View mContentView;
    private int mDividerColor;
    private Paint mDividerPaint;
    private int mDividerWidth;
    private boolean mFirstMeasure;
    private boolean mIsContentEnable;
    private boolean mIsContentOpen;
    private boolean mIsNavigationEnable;
    private boolean mIsNavigationOpen;
    private boolean mIsShowDivider;
    private final Paint mMaskPaint;
    private final RectF mMaskRect;
    private float mNavigationDefaultRatio;
    private String mNavigationIdString;
    private View mNavigationView;
    private final WidthDescription mOverlayWidthDescription;
    private float mRatio;
    private SplitListener mSplitListener;
    private int mSplitMode;
    private final WidthDescription mSqueezedWidthDescription;
    private final IStateStyle mStateStyle;
    private int mTrackingPointerId;

    public interface SplitListener {
        void onSplitClose();

        void onSplitEnd(String str);

        void onSplitOpen();

        void onSplitProgress(float f);

        void onSplitStart(String str);
    }

    public static abstract class SplitListenerAdapter implements SplitListener {
        @Override // miuix.navigation.SplitLayout.SplitListener
        public void onSplitClose() {
        }

        @Override // miuix.navigation.SplitLayout.SplitListener
        public void onSplitEnd(String str) {
        }

        @Override // miuix.navigation.SplitLayout.SplitListener
        public void onSplitOpen() {
        }

        @Override // miuix.navigation.SplitLayout.SplitListener
        public void onSplitProgress(float f) {
        }

        @Override // miuix.navigation.SplitLayout.SplitListener
        public void onSplitStart(String str) {
        }
    }

    public SplitLayout(Context context) {
        this(context, null);
    }

    public SplitLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.splitLayoutStyle);
    }

    public SplitLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mNavigationDefaultRatio = 0.0f;
        this.mFirstMeasure = true;
        this.mMaskPaint = new Paint(1);
        this.mMaskRect = new RectF();
        this.mCollapseOnTouchOutside = true;
        this.mIsNavigationOpen = true;
        this.mIsContentOpen = true;
        this.mIsNavigationEnable = true;
        this.mIsContentEnable = true;
        this.mRatio = 1.0f;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.SplitLayout, i, R.style.Widget_SplitLayout);
        this.mOverlayWidthDescription = WidthDescription.parseValue(typedArrayObtainStyledAttributes.peekValue(R.styleable.SplitLayout_overlayNavigationWidth), getResources(), this.mNavigationDefaultRatio);
        this.mSqueezedWidthDescription = WidthDescription.parseValue(typedArrayObtainStyledAttributes.peekValue(R.styleable.SplitLayout_squeezedNavigationWidth), getResources(), this.mNavigationDefaultRatio);
        this.mSplitMode = typedArrayObtainStyledAttributes.getInt(R.styleable.SplitLayout_splitMode, 0);
        this.mIsShowDivider = typedArrayObtainStyledAttributes.getBoolean(R.styleable.SplitLayout_isShowDivider, true);
        this.mDividerWidth = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.SplitLayout_dividerWidth, 1);
        this.mDividerColor = typedArrayObtainStyledAttributes.getColor(R.styleable.SplitLayout_dividerColor, 436207616);
        this.mNavigationIdString = typedArrayObtainStyledAttributes.getString(R.styleable.SplitLayout_navigationId);
        this.mContentIdString = typedArrayObtainStyledAttributes.getString(R.styleable.SplitLayout_contentId);
        typedArrayObtainStyledAttributes.recycle();
        this.mStateStyle = Folme.useValue(this);
        this.mAnimConfig = new AnimConfig().setEase(EaseManager.getStyle(-2, 0.9f, 0.4f)).addListeners(new SplitTransitionListener(this));
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        prepareChildView();
    }

    private void prepareChildView() {
        String resourceEntryName;
        int childCount = getChildCount();
        if (childCount < 2) {
            throw new IllegalStateException("SplitLayout must have at least two child views!");
        }
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            int id = childAt.getId();
            if (id != -1) {
                try {
                    resourceEntryName = getResources().getResourceEntryName(id);
                } catch (Resources.NotFoundException unused) {
                    resourceEntryName = null;
                }
                String str = this.mNavigationIdString;
                if (str != null && str.equals(resourceEntryName)) {
                    this.mNavigationView = childAt;
                } else {
                    String str2 = this.mContentIdString;
                    if (str2 != null && str2.equals(resourceEntryName)) {
                        this.mContentView = childAt;
                    }
                }
            }
        }
        View view = this.mNavigationView;
        if (view == null) {
            throw new IllegalStateException("You must set navigationId in layout");
        }
        if (this.mContentView == null) {
            throw new IllegalStateException("You must set contentId in layout");
        }
        bringChildToFront(view);
    }

    public float getNavigationDefaultRatio() {
        if (this.mNavigationDefaultRatio == 0.0f) {
            Point windowSizeDp = WindowUtils.getWindowSizeDp(this);
            if (windowSizeDp.x > 0 && windowSizeDp.y > 0) {
                this.mNavigationDefaultRatio = Utils.getNaviRatio(windowSizeDp);
            }
        }
        return this.mNavigationDefaultRatio;
    }

    public void setShowDivider(boolean z) {
        if (z != this.mIsShowDivider) {
            this.mIsShowDivider = z;
            if (this.mSplitMode == 1) {
                requestLayout();
            }
        }
    }

    public void setCollapseOnTouchOutside(boolean z) {
        this.mCollapseOnTouchOutside = z;
    }

    public void setSplitMode(int i) {
        if (this.mSplitMode != i) {
            this.mSplitMode = i;
            checkMode();
            requestLayout();
        }
        setRatio(this.mRatio);
    }

    public int getSplitMode() {
        return this.mSplitMode;
    }

    public void setRatio(float f) {
        if (f < 0.0f || f > 1.0f) {
            return;
        }
        this.mRatio = f;
        onRatioUpdated(f);
    }

    private void onRatioUpdated(float f) {
        boolean zIsLayoutRtl = ViewUtils.isLayoutRtl(this);
        int i = this.mSplitMode;
        if (i == 0) {
            View view = this.mNavigationView;
            if (view != null && view.getMeasuredWidth() != 0) {
                View view2 = this.mNavigationView;
                view2.setTranslationX((1.0f - f) * (zIsLayoutRtl ? 1 : -1) * view2.getMeasuredWidth());
                this.mNavigationView.setAlpha(1.0f);
            }
            View view3 = this.mContentView;
            if (view3 != null) {
                view3.setTranslationX(0.0f);
            }
            invalidate();
            return;
        }
        if (i == 1) {
            View view4 = this.mNavigationView;
            if (view4 != null && view4.getMeasuredWidth() != 0) {
                View view5 = this.mNavigationView;
                view5.setTranslationX((1.0f - f) * (zIsLayoutRtl ? 1 : -1) * view5.getMeasuredWidth());
                this.mNavigationView.setAlpha(this.mRatio);
                requestLayout();
            }
            View view6 = this.mContentView;
            if (view6 != null) {
                view6.setTranslationX(0.0f);
                return;
            }
            return;
        }
        View view7 = this.mNavigationView;
        if (view7 != null && view7.getMeasuredWidth() != 0) {
            View view8 = this.mNavigationView;
            view8.setTranslationX((1.0f - f) * (-view8.getMeasuredWidth()));
            this.mNavigationView.setAlpha(1.0f);
        }
        View view9 = this.mContentView;
        if (view9 != null && view9.getMeasuredWidth() != 0) {
            View view10 = this.mContentView;
            view10.setTranslationX(f * view10.getMeasuredWidth());
        }
        invalidate();
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.mIsContentEnable = this.mIsContentEnable;
        savedState.mIsContentOpen = this.mIsContentOpen;
        savedState.mIsNavigationEnable = this.mIsNavigationEnable;
        savedState.mIsNavigationOpen = this.mIsNavigationOpen;
        savedState.mRatio = this.mRatio;
        return savedState;
    }

    @Override // android.view.View
    protected void onRestoreInstanceState(Parcelable parcelable) {
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.mIsContentEnable = savedState.mIsContentEnable;
        this.mIsContentOpen = savedState.mIsContentOpen;
        this.mIsNavigationEnable = savedState.mIsNavigationEnable;
        this.mIsNavigationOpen = savedState.mIsNavigationOpen;
        this.mRatio = savedState.mRatio;
    }

    public float getRatio() {
        return this.mRatio;
    }

    public void openNavigation() {
        openNavigation(true);
    }

    public void openNavigation(boolean z) {
        switchNavigationInternal(true, z);
    }

    public void closeNavigation() {
        closeNavigation(true);
    }

    public void closeNavigation(boolean z) {
        switchNavigationInternal(false, z);
    }

    private void switchNavigationInternal(boolean z, boolean z2) {
        if (this.mFirstMeasure) {
            z2 = false;
        } else if (this.mIsNavigationOpen == z) {
            return;
        }
        if (z) {
            this.mIsNavigationOpen = true;
            this.mIsContentOpen = this.mSplitMode != 2;
            if (z2) {
                requestLayout();
                this.mStateStyle.to(TAG_OPEN, PROPERTY, Float.valueOf(1.0f), this.mAnimConfig);
                return;
            }
            requestLayout();
            this.mStateStyle.setTo(TAG_OPEN, PROPERTY, Float.valueOf(1.0f), this.mAnimConfig);
            SplitListener splitListener = this.mSplitListener;
            if (splitListener != null) {
                splitListener.onSplitOpen();
                return;
            }
            return;
        }
        this.mIsNavigationOpen = false;
        this.mIsContentOpen = true;
        if (z2) {
            this.mStateStyle.to(TAG_CLOSE, PROPERTY, Float.valueOf(0.0f), this.mAnimConfig);
            return;
        }
        this.mStateStyle.setTo(TAG_CLOSE, PROPERTY, Float.valueOf(0.0f), this.mAnimConfig);
        SplitListener splitListener2 = this.mSplitListener;
        if (splitListener2 != null) {
            splitListener2.onSplitClose();
        }
    }

    public boolean isNavigationOpen() {
        return this.mIsNavigationOpen;
    }

    public boolean isContentOpen() {
        return this.mIsContentOpen;
    }

    public void toggle() {
        toggle(true);
    }

    public void toggle(boolean z) {
        if (isNavigationOpen()) {
            closeNavigation(z);
        } else {
            openNavigation(z);
        }
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        this.mNavigationDefaultRatio = 0.0f;
    }

    public void resetToDefaultNavigationRatio() {
        resetToDefaultNavigationRatio(getSplitMode());
    }

    public void resetToDefaultNavigationRatio(int i) {
        if (i == 0) {
            this.mOverlayWidthDescription.type = 1;
            this.mOverlayWidthDescription.useDefault = true;
            this.mOverlayWidthDescription.value = this.mNavigationDefaultRatio;
        } else if (i == 1) {
            this.mSqueezedWidthDescription.type = 1;
            this.mSqueezedWidthDescription.useDefault = true;
            this.mSqueezedWidthDescription.value = this.mNavigationDefaultRatio;
        }
        requestLayout();
    }

    public void setNavigationWidth(int i, float f) {
        if (i == 0) {
            this.mOverlayWidthDescription.type = 1;
            this.mOverlayWidthDescription.useDefault = false;
            this.mOverlayWidthDescription.value = Math.max(Math.min(1.0f, f), 0.0f);
        } else if (i == 1) {
            this.mSqueezedWidthDescription.type = 1;
            this.mSqueezedWidthDescription.useDefault = false;
            this.mSqueezedWidthDescription.value = Math.max(Math.min(1.0f, f), 0.0f);
        }
        requestLayout();
    }

    public void setNavigationWidth(int i, int i2) {
        if (i == 0) {
            this.mOverlayWidthDescription.type = 0;
            this.mOverlayWidthDescription.useDefault = false;
            this.mOverlayWidthDescription.value = i2;
        } else if (i == 1) {
            this.mSqueezedWidthDescription.type = 0;
            this.mSqueezedWidthDescription.useDefault = false;
            this.mSqueezedWidthDescription.value = i2;
        }
        requestLayout();
    }

    public void setNavigationWidthPercent(float f) {
        setNavigationWidth(getSplitMode(), f);
        requestLayout();
    }

    public void setNavigationWidth(int i) {
        setNavigationWidth(getSplitMode(), i);
        requestLayout();
    }

    public void setSplitListener(SplitListener splitListener) {
        this.mSplitListener = splitListener;
    }

    private void checkMode() {
        if (getChildCount() < 2) {
            return;
        }
        View childAt = getChildAt(0);
        View childAt2 = getChildAt(1);
        if (this.mSplitMode == 0) {
            if (childAt2.getId() == R.id.miuix_content) {
                childAt.bringToFront();
            }
        } else if (childAt2.getId() == R.id.miuix_navigation) {
            childAt.bringToFront();
        }
    }

    public void setNavigationEnable(boolean z) {
        this.mIsNavigationEnable = z;
        if (!z) {
            this.mIsContentEnable = true;
        }
        if (this.mSplitMode == 1) {
            invalidate();
        }
    }

    public void setContentEnable(boolean z) {
        this.mIsContentEnable = z;
        if (!z) {
            this.mIsNavigationEnable = true;
        }
        if (this.mSplitMode == 1) {
            invalidate();
        }
    }

    public boolean isNavigationEnable() {
        return this.mIsNavigationEnable;
    }

    public boolean isContentEnable() {
        return this.mIsContentEnable;
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        setMeasuredDimension(size, size2);
        int navigationWidth = getNavigationWidth(size);
        measureChild(this.mNavigationView, View.MeasureSpec.makeMeasureSpec(navigationWidth, BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(size2, BasicMeasure.EXACTLY));
        int i3 = this.mSplitMode;
        if (i3 == 0) {
            measureChildWithPadding(this.mContentView, i, size, i2);
        } else if (i3 == 1) {
            if (this.mRatio >= 0.5f) {
                size = (size - navigationWidth) - (shouldDrawDivider() ? this.mDividerWidth : 0);
            }
            measureChildWithPadding(this.mContentView, i, size, i2);
        } else if (i3 == 2) {
            measureChildWithPadding(this.mContentView, i, size, i2);
        }
        if (this.mFirstMeasure) {
            onRatioUpdated(this.mRatio);
            this.mFirstMeasure = false;
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5 = this.mSplitMode;
        if (i5 == 1) {
            View view = this.mNavigationView;
            ViewUtils.layoutChildView(this, view, 0, 0, view.getMeasuredWidth(), this.mNavigationView.getMeasuredHeight());
            ViewUtils.layoutChildView(this, this.mContentView, (int) ((this.mRatio * this.mNavigationView.getMeasuredWidth()) + (shouldDrawDivider() ? this.mDividerWidth : 0)), 0, i3, this.mContentView.getMeasuredHeight());
            return;
        }
        if (i5 != 0 || isNavigationOpen()) {
            View view2 = this.mNavigationView;
            ViewUtils.layoutChildView(this, view2, 0, 0, view2.getMeasuredWidth(), this.mNavigationView.getMeasuredHeight());
        }
        View view3 = this.mContentView;
        if (view3 != null) {
            ViewUtils.layoutChildView(this, view3, 0, 0, view3.getMeasuredWidth(), this.mContentView.getMeasuredHeight());
            if (this.mSplitMode == 2) {
                float measuredWidth = this.mRatio * this.mContentView.getMeasuredWidth();
                if (this.mContentView.getTranslationX() != measuredWidth) {
                    this.mContentView.setTranslationX(measuredWidth);
                }
            }
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawDivider(canvas);
        drawMask(canvas);
    }

    private boolean shouldDrawDivider() {
        return this.mIsShowDivider && this.mRatio > 0.0f && this.mSplitMode == 1;
    }

    private void drawDivider(Canvas canvas) {
        if (shouldDrawDivider()) {
            if (this.mDividerPaint == null) {
                Paint paint = new Paint();
                this.mDividerPaint = paint;
                paint.setColor(this.mDividerColor);
                this.mDividerPaint.setStrokeWidth(this.mDividerWidth);
            }
            boolean zIsLayoutRtl = ViewUtils.isLayoutRtl(this);
            float measuredWidth = this.mRatio * this.mNavigationView.getMeasuredWidth();
            if (zIsLayoutRtl) {
                measuredWidth = getWidth() - measuredWidth;
            }
            float f = measuredWidth;
            canvas.drawLine(f, 0.0f, f, this.mNavigationView.getMeasuredHeight(), this.mDividerPaint);
        }
    }

    private void drawMask(Canvas canvas) {
        this.mMaskPaint.setColor(ViewCompat.MEASURED_STATE_MASK);
        this.mMaskPaint.setStyle(Paint.Style.FILL);
        boolean zIsLayoutRtl = ViewUtils.isLayoutRtl(this);
        int i = this.mSplitMode;
        if (i == 0) {
            float measuredWidth = this.mRatio * this.mNavigationView.getMeasuredWidth();
            this.mMaskRect.set(zIsLayoutRtl ? 0.0f : measuredWidth, 0.0f, zIsLayoutRtl ? getWidth() - measuredWidth : getWidth(), getHeight());
            this.mMaskPaint.setAlpha((int) (this.mRatio * 0.3f * 255.0f));
            canvas.drawRect(this.mMaskRect, this.mMaskPaint);
            return;
        }
        if (i == 2) {
            this.mMaskRect.set(0.0f, 0.0f, getWidth() * this.mRatio, getHeight());
            this.mMaskPaint.setAlpha((int) ((1.0f - this.mRatio) * 0.3f * 255.0f));
            canvas.drawRect(this.mMaskRect, this.mMaskPaint);
            return;
        }
        if (i == 1) {
            this.mMaskRect.set(zIsLayoutRtl ? getWidth() - (this.mNavigationView.getMeasuredWidth() * this.mRatio) : 0.0f, 0.0f, zIsLayoutRtl ? getWidth() : this.mNavigationView.getMeasuredWidth() * this.mRatio, getHeight());
            this.mMaskPaint.setAlpha((int) ((1.0f - this.mRatio) * 0.3f * 255.0f));
            canvas.drawRect(this.mMaskRect, this.mMaskPaint);
            if (!this.mIsNavigationEnable) {
                this.mMaskPaint.setColor(getResources().getColor(R.color.miuix_split_mask_color));
                this.mMaskRect.set(zIsLayoutRtl ? getWidth() - this.mNavigationView.getMeasuredWidth() : 0.0f, 0.0f, zIsLayoutRtl ? getWidth() : this.mNavigationView.getMeasuredWidth(), getHeight());
                canvas.drawRect(this.mMaskRect, this.mMaskPaint);
            }
            if (this.mIsContentEnable) {
                return;
            }
            this.mMaskPaint.setColor(getResources().getColor(R.color.miuix_split_mask_color));
            float measuredWidth2 = this.mNavigationView.getMeasuredWidth() + (shouldDrawDivider() ? this.mDividerWidth : 0);
            float width = getWidth() - measuredWidth2;
            RectF rectF = this.mMaskRect;
            if (zIsLayoutRtl) {
                measuredWidth2 = 0.0f;
            }
            if (!zIsLayoutRtl) {
                width = getWidth();
            }
            rectF.set(measuredWidth2, 0.0f, width, getHeight());
            canvas.drawRect(this.mMaskRect, this.mMaskPaint);
        }
    }

    private void measureChildWithPadding(View view, int i, int i2, int i3) {
        view.measure(getChildMeasureSpec(i, getPaddingLeft() + getPaddingRight(), i2), getChildMeasureSpec(i3, getPaddingTop() + getPaddingBottom(), view.getLayoutParams().height));
    }

    private int getNavigationWidth(int i) {
        int i2 = this.mSplitMode;
        if (i2 == 2) {
            return i;
        }
        if (i2 == 0 || i2 == 1) {
            WidthDescription widthDescription = i2 == 1 ? this.mSqueezedWidthDescription : this.mOverlayWidthDescription;
            if (!widthDescription.useDefault) {
                int i3 = widthDescription.type;
                if (i3 == 0) {
                    return (int) widthDescription.value;
                }
                if (i3 == 1) {
                    return (int) (widthDescription.value * i);
                }
            }
        }
        return (int) (getNavigationDefaultRatio() * i);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (this.mCollapseOnTouchOutside && this.mSplitMode == 0 && isNavigationOpen()) {
            int x = (int) motionEvent.getX();
            int y = (int) motionEvent.getY();
            if (x >= this.mNavigationView.getMeasuredWidth() && x <= getRight() && y >= getTop() && y <= getBottom()) {
                closeNavigation(true);
                return true;
            }
        } else if (this.mSplitMode == 1 && (!isContentEnable() || !isNavigationEnable())) {
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked == 0) {
                this.mTrackingPointerId = motionEvent.getPointerId(0);
            } else if (actionMasked == 5) {
                this.mTrackingPointerId = motionEvent.getPointerId(motionEvent.getActionIndex());
            } else if (actionMasked == 6) {
                int pointerCount = motionEvent.getPointerCount();
                int actionIndex = motionEvent.getActionIndex();
                if (motionEvent.getPointerId(actionIndex) == this.mTrackingPointerId) {
                    int i = pointerCount - 1;
                    if (actionIndex == i) {
                        i = pointerCount - 2;
                    }
                    this.mTrackingPointerId = motionEvent.getPointerId(i);
                }
            }
            int iFindPointerIndex = motionEvent.findPointerIndex(this.mTrackingPointerId);
            int pointerCount2 = motionEvent.getPointerCount();
            if (iFindPointerIndex >= pointerCount2 || iFindPointerIndex < 0) {
                int pointerId = motionEvent.getPointerId(pointerCount2 - 1);
                this.mTrackingPointerId = pointerId;
                iFindPointerIndex = motionEvent.findPointerIndex(pointerId);
            }
            int x2 = (int) motionEvent.getX(iFindPointerIndex);
            int y2 = (int) motionEvent.getY(iFindPointerIndex);
            if (!isContentEnable() && isPointInView(this.mContentView, x2, y2)) {
                motionEvent.setAction(3);
                super.dispatchTouchEvent(motionEvent);
                return true;
            }
            if (!isNavigationEnable() && isPointInView(this.mNavigationView, x2, y2)) {
                motionEvent.setAction(3);
                super.dispatchTouchEvent(motionEvent);
                return true;
            }
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    private boolean isPointInView(View view, int i, int i2) {
        float x = view.getX();
        float width = view.getWidth() + x;
        float y = view.getY();
        float height = view.getHeight() + y;
        float f = i;
        if (f >= x && f <= width) {
            float f2 = i2;
            if (f2 <= height && f2 >= y) {
                return true;
            }
        }
        return false;
    }

    private static class WidthDescription {
        public int type;
        public boolean useDefault;
        public float value;

        private WidthDescription() {
        }

        static WidthDescription parseValue(TypedValue typedValue, Resources resources, float f) {
            WidthDescription widthDescription = new WidthDescription();
            if (typedValue == null || (typedValue.type >= 16 && typedValue.type <= 31)) {
                widthDescription.type = 1;
                widthDescription.value = f;
                widthDescription.useDefault = true;
            } else {
                if (typedValue.type == 6) {
                    widthDescription.type = 1;
                    widthDescription.value = TypedValue.complexToFloat(typedValue.data);
                    widthDescription.useDefault = false;
                    return widthDescription;
                }
                if (typedValue.type == 4) {
                    widthDescription.type = 1;
                    widthDescription.value = typedValue.getFloat();
                    widthDescription.useDefault = false;
                    return widthDescription;
                }
                if (typedValue.type == 5) {
                    widthDescription.type = 0;
                    widthDescription.value = TypedValue.complexToDimensionPixelSize(typedValue.data, resources.getDisplayMetrics());
                    widthDescription.useDefault = false;
                    return widthDescription;
                }
            }
            return widthDescription;
        }
    }

    static class SplitTransitionListener extends TransitionListener {
        WeakReference<SplitLayout> mView;

        SplitTransitionListener(SplitLayout splitLayout) {
            this.mView = new WeakReference<>(splitLayout);
        }

        @Override // miuix.animation.listener.TransitionListener
        public void onBegin(Object obj) {
            SplitLayout splitLayout = this.mView.get();
            if (splitLayout == null || splitLayout.mSplitListener == null || obj == null) {
                return;
            }
            splitLayout.mSplitListener.onSplitStart(obj.toString());
        }

        @Override // miuix.animation.listener.TransitionListener
        public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
            SplitLayout splitLayout = this.mView.get();
            if (splitLayout == null || splitLayout.mSplitListener == null || !(collection instanceof List) || collection.size() <= 0) {
                return;
            }
            splitLayout.mSplitListener.onSplitProgress(((UpdateInfo) ((List) collection).get(0)).getFloatValue());
        }

        @Override // miuix.animation.listener.TransitionListener
        public void onComplete(Object obj) {
            SplitLayout splitLayout = this.mView.get();
            if (splitLayout == null || obj == null || splitLayout.mSplitListener == null) {
                return;
            }
            splitLayout.mSplitListener.onSplitEnd(obj.toString());
            if (obj.toString().equals(SplitLayout.TAG_OPEN)) {
                splitLayout.mSplitListener.onSplitOpen();
            } else if (obj.toString().equals(SplitLayout.TAG_CLOSE)) {
                splitLayout.mSplitListener.onSplitClose();
            }
        }
    }

    static class SavedState extends View.BaseSavedState {
        public static final Parcelable.ClassLoaderCreator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() { // from class: miuix.navigation.SplitLayout.SavedState.1
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }

            @Override // android.os.Parcelable.ClassLoaderCreator
            public SavedState createFromParcel(Parcel parcel, ClassLoader classLoader) {
                return new SavedState(parcel, classLoader);
            }
        };
        private boolean mIsContentEnable;
        private boolean mIsContentOpen;
        private boolean mIsNavigationEnable;
        private boolean mIsNavigationOpen;
        private float mRatio;

        SavedState(Parcelable parcelable) {
            super(parcelable);
            this.mIsNavigationOpen = true;
            this.mIsContentOpen = true;
            this.mIsNavigationEnable = true;
            this.mIsContentEnable = true;
            this.mRatio = 1.0f;
        }

        public SavedState(Parcel parcel) {
            super(parcel);
            this.mIsNavigationOpen = true;
            this.mIsContentOpen = true;
            this.mIsNavigationEnable = true;
            this.mIsContentEnable = true;
            this.mRatio = 1.0f;
            this.mIsNavigationOpen = parcel.readInt() != 0;
            this.mIsContentOpen = parcel.readInt() != 0;
            this.mIsNavigationEnable = parcel.readInt() != 0;
            this.mIsContentEnable = parcel.readInt() != 0;
            this.mRatio = parcel.readFloat();
        }

        public SavedState(Parcel parcel, ClassLoader classLoader) {
            super(parcel, classLoader);
            this.mIsNavigationOpen = true;
            this.mIsContentOpen = true;
            this.mIsNavigationEnable = true;
            this.mIsContentEnable = true;
            this.mRatio = 1.0f;
            this.mIsNavigationOpen = parcel.readInt() != 0;
            this.mIsContentOpen = parcel.readInt() != 0;
            this.mIsNavigationEnable = parcel.readInt() != 0;
            this.mIsContentEnable = parcel.readInt() != 0;
            this.mRatio = parcel.readFloat();
        }

        @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.mIsNavigationOpen ? 1 : 0);
            parcel.writeInt(this.mIsContentOpen ? 1 : 0);
            parcel.writeInt(this.mIsNavigationEnable ? 1 : 0);
            parcel.writeInt(this.mIsContentEnable ? 1 : 0);
            parcel.writeFloat(this.mRatio);
        }
    }
}
