package miuix.appcompat.internal.app.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import androidx.core.view.MotionEventCompat;
import miuix.appcompat.R;
import miuix.appcompat.app.ActionBar;
import miuix.internal.util.DeviceHelper;
import miuix.overscroller.widget.AnimationHelper;
import miuix.overscroller.widget.OverScroller;

/* JADX INFO: loaded from: classes2.dex */
public class ActionBarMovableLayout extends ActionBarOverlayLayout {
    private static final boolean DBG = false;
    public static final int DEFAULT_SPRING_BACK_DURATION = 800;
    public static final int STATE_DOWN = 1;
    public static final int STATE_UNKNOWN = -1;
    public static final int STATE_UP = 0;
    private static final String TAG = "ActionBarMovableLayout";
    private int mActivePointerId;
    private boolean mFlinging;
    private int mInitialMotionY;
    private boolean mInitialMotionYSet;
    private boolean mIsBeingDragged;
    private boolean mIsSpringBackEnabled;
    private float mLastMotionX;
    private float mLastMotionY;
    private final int mMaximumVelocity;
    private final int mMinimumVelocity;
    private int mMotionY;
    private ActionBar.OnScrollListener mOnScrollListener;
    private int mOverScrollDistance;
    private int mScrollRange;
    private int mScrollStart;
    private OverScroller mScroller;
    private int mState;
    private View mTabScrollView;
    private int mTabViewVisibility;
    private final int mTouchSlop;
    private VelocityTracker mVelocityTracker;

    @Override // android.view.View
    protected int computeVerticalScrollExtent() {
        return 0;
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void requestDisallowInterceptTouchEvent(boolean z) {
    }

    public ActionBarMovableLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mState = -1;
        this.mScrollRange = -1;
        this.mInitialMotionY = -1;
        this.mTabViewVisibility = 8;
        this.mIsSpringBackEnabled = true;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ActionBarMovableLayout, R.attr.actionBarMovableLayoutStyle, 0);
        if (DeviceHelper.isFeatureWholeAnim()) {
            this.mOverScrollDistance = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.ActionBarMovableLayout_overScrollRange, 0);
        }
        this.mScrollRange = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.ActionBarMovableLayout_scrollRange, -1);
        this.mInitialMotionY = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.ActionBarMovableLayout_scrollStart, -1);
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        this.mTouchSlop = viewConfiguration.getScaledTouchSlop();
        this.mScroller = new OverScroller(context);
        this.mMinimumVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        this.mMaximumVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        setOverScrollMode(0);
        typedArrayObtainStyledAttributes.recycle();
    }

    /* JADX WARN: Code duplicated, block: B:25:0x0042  */
    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        View contentMask = getContentMask();
        if (contentMask != null && contentMask.getVisibility() == 0) {
            return false;
        }
        int action = motionEvent.getAction();
        if (action == 2 && this.mIsBeingDragged) {
            return true;
        }
        int i = action & 255;
        if (i == 0) {
            this.mLastMotionY = motionEvent.getY();
            this.mLastMotionX = motionEvent.getX();
            this.mActivePointerId = motionEvent.getPointerId(0);
            initOrResetVelocityTracker();
            this.mVelocityTracker.addMovement(motionEvent);
            this.mScroller.forceFinished(true);
        } else if (i == 1) {
            this.mIsBeingDragged = false;
            this.mActivePointerId = -1;
            recycleVelocityTracker();
            onScrollEnd();
        } else if (i != 2) {
            if (i == 3) {
                this.mIsBeingDragged = false;
                this.mActivePointerId = -1;
                recycleVelocityTracker();
                onScrollEnd();
            } else if (i == 6) {
                onSecondaryPointerUp(motionEvent);
            }
        } else if (shouldStartScroll(motionEvent)) {
            this.mIsBeingDragged = true;
            initVelocityTrackerIfNotExists();
            this.mVelocityTracker.addMovement(motionEvent);
            onScrollBegin();
        }
        return this.mIsBeingDragged;
    }

    /* JADX WARN: Code duplicated, block: B:31:0x00a5  */
    /* JADX WARN: Code duplicated, block: B:33:0x00a9  */
    /* JADX WARN: Code duplicated, block: B:35:0x00b9  */
    /* JADX WARN: Code duplicated, block: B:36:0x00bd  */
    /* JADX WARN: Code duplicated, block: B:38:0x00cf  */
    /* JADX WARN: Code duplicated, block: B:39:0x00d3  */
    @Override // miuix.appcompat.internal.app.widget.ActionBarOverlayLayout, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int iComputeVerticalVelocity;
        initVelocityTrackerIfNotExists();
        this.mVelocityTracker.addMovement(motionEvent);
        int action = motionEvent.getAction() & 255;
        if (action == 0) {
            this.mLastMotionY = motionEvent.getY();
            this.mActivePointerId = motionEvent.getPointerId(0);
        } else if (action == 1) {
            if (this.mIsBeingDragged) {
                this.mIsBeingDragged = false;
                this.mActivePointerId = -1;
                iComputeVerticalVelocity = computeVerticalVelocity();
                if (Math.abs(iComputeVerticalVelocity) > this.mMinimumVelocity) {
                    fling(iComputeVerticalVelocity);
                } else {
                    if (this.mScroller.springBack(0, this.mMotionY, 0, 0, 0, getScrollRange())) {
                        invalidate();
                    } else {
                        springBack();
                    }
                }
            }
        } else if (action != 2) {
            if (action != 3) {
                if (action == 5) {
                    int actionIndex = motionEvent.getActionIndex();
                    this.mLastMotionY = (int) motionEvent.getY(actionIndex);
                    this.mActivePointerId = motionEvent.getPointerId(actionIndex);
                } else if (action == 6) {
                    onSecondaryPointerUp(motionEvent);
                    this.mLastMotionY = (int) motionEvent.getY(motionEvent.findPointerIndex(this.mActivePointerId));
                }
            } else if (this.mIsBeingDragged) {
                this.mIsBeingDragged = false;
                this.mActivePointerId = -1;
                iComputeVerticalVelocity = computeVerticalVelocity();
                if (Math.abs(iComputeVerticalVelocity) > this.mMinimumVelocity) {
                    fling(iComputeVerticalVelocity);
                } else {
                    if (this.mScroller.springBack(0, this.mMotionY, 0, 0, 0, getScrollRange())) {
                        invalidate();
                    } else {
                        springBack();
                    }
                }
            }
        } else if (this.mIsBeingDragged) {
            int iFindPointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
            if (iFindPointerIndex == -1) {
                return false;
            }
            float y = motionEvent.getY(iFindPointerIndex);
            boolean zOverScrollBy = overScrollBy(0, (int) (y - this.mLastMotionY), 0, this.mMotionY, 0, getScrollRange(), 0, getOverScrollDistance(), true);
            this.mLastMotionY = y;
            if (zOverScrollBy) {
                if (this.mMotionY == 0) {
                    this.mIsBeingDragged = false;
                    this.mActivePointerId = -1;
                    motionEvent.setAction(0);
                    dispatchTouchEvent(motionEvent);
                }
                this.mVelocityTracker.clear();
            }
        } else if (shouldStartScroll(motionEvent)) {
            this.mIsBeingDragged = true;
            initVelocityTrackerIfNotExists();
            this.mVelocityTracker.addMovement(motionEvent);
            onScrollBegin();
        }
        return true;
    }

    @Override // android.view.View
    public void computeScroll() {
        if (this.mScroller.computeScrollOffset()) {
            int i = this.mMotionY;
            int currY = this.mScroller.getCurrY();
            if (i != currY) {
                overScrollBy(0, currY - i, 0, this.mMotionY, 0, getScrollRange(), 0, getOverScrollDistance(), true);
            }
            AnimationHelper.postInvalidateOnAnimation(this);
            return;
        }
        if (this.mFlinging) {
            springBack();
            this.mFlinging = false;
        }
    }

    @Override // android.view.View
    protected boolean overScrollBy(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, boolean z) {
        int overScrollMode = getOverScrollMode();
        boolean z2 = true;
        int i9 = i4 + i2;
        if (!(overScrollMode == 0 || (overScrollMode == 1 && (computeVerticalScrollRange() > computeVerticalScrollExtent())))) {
            i8 = 0;
        }
        int i10 = i8 + i6;
        if (i9 > i10) {
            i9 = i10;
        } else if (i9 < 0) {
            i9 = 0;
        } else {
            z2 = false;
        }
        onOverScrolled(0, i9, false, z2);
        return z2;
    }

    @Override // android.view.View
    protected void onOverScrolled(int i, int i2, boolean z, boolean z2) {
        ActionBar.OnScrollListener onScrollListener;
        onScroll(i2);
        this.mMotionY = i2;
        if (i2 == 0 && z2) {
            int iComputeVerticalVelocity = computeVerticalVelocity();
            if (Math.abs(iComputeVerticalVelocity) <= this.mMinimumVelocity * 2 || (onScrollListener = this.mOnScrollListener) == null) {
                return;
            }
            onScrollListener.onFling((-iComputeVerticalVelocity) * 0.2f, 500);
        }
    }

    public void setInitialMotionY(int i) {
        this.mInitialMotionY = i;
    }

    public void setOverScrollDistance(int i) {
        if (DeviceHelper.isFeatureWholeAnim()) {
            this.mOverScrollDistance = i;
        }
    }

    public int getOverScrollDistance() {
        if (DeviceHelper.isFeatureWholeAnim()) {
            return this.mOverScrollDistance;
        }
        return 0;
    }

    public void setScrollRange(int i) {
        this.mScrollRange = i;
    }

    public int getScrollRange() {
        return this.mScrollRange;
    }

    public void setOnScrollListener(ActionBar.OnScrollListener onScrollListener) {
        this.mOnScrollListener = onScrollListener;
    }

    public void setScrollStart(int i) {
        this.mScrollStart = i;
    }

    public int getScrollStart() {
        return this.mScrollStart;
    }

    public void setSpringBackEnabled(boolean z) {
        this.mIsSpringBackEnabled = z;
    }

    private boolean inChild(View view, int i, int i2) {
        if (view == null) {
            return false;
        }
        int y = (int) view.getY();
        int x = (int) view.getX();
        int y2 = (int) (view.getY() + view.getHeight());
        int x2 = (int) (view.getX() + view.getWidth());
        if (view == this.mTabScrollView) {
            int top = this.mActionBarTop.getTop();
            y += top;
            y2 += top;
        }
        return i2 >= y && i2 < y2 && i >= x && i < x2;
    }

    private void initOrResetVelocityTracker() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        } else {
            velocityTracker.clear();
        }
    }

    private void initVelocityTrackerIfNotExists() {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void recycleVelocityTracker() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    void ensureTabScrollView() {
        this.mTabScrollView = this.mActionBarTop.getTabContainer();
    }

    @Override // android.view.ViewGroup
    protected void measureChildWithMargins(View view, int i, int i2, int i3, int i4) {
        if (view != this.mContentView) {
            super.measureChildWithMargins(view, i, i2, i3, i4);
            return;
        }
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        view.measure(getChildMeasureSpec(i, getPaddingLeft() + getPaddingRight() + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin + i2, marginLayoutParams.width), getChildMeasureSpec(i3, ((((((getPaddingTop() + getPaddingBottom()) + marginLayoutParams.bottomMargin) + this.mActionBarView.getMeasuredHeight()) + ((ViewGroup.MarginLayoutParams) this.mActionBarView.getLayoutParams()).topMargin) - getScrollRange()) - getOverScrollDistance()) - this.mScrollStart, marginLayoutParams.height));
    }

    @Override // miuix.appcompat.internal.app.widget.ActionBarOverlayLayout, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        boolean z2 = !this.mInitialMotionYSet || isTabViewVisibilityChanged();
        if (!this.mInitialMotionYSet) {
            if (this.mInitialMotionY < 0) {
                this.mInitialMotionY = this.mScrollRange;
            }
            this.mMotionY = this.mInitialMotionY;
            this.mInitialMotionYSet = true;
        }
        if (z2) {
            applyTranslationY(this.mMotionY);
        }
    }

    @Override // android.view.View
    protected int computeVerticalScrollRange() {
        return getScrollRange();
    }

    protected int computeVerticalVelocity() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        velocityTracker.computeCurrentVelocity(1000, this.mMaximumVelocity);
        return (int) velocityTracker.getYVelocity(this.mActivePointerId);
    }

    protected void fling(int i) {
        int overScrollDistance = getOverScrollDistance();
        this.mScroller.fling(0, this.mMotionY, 0, i, 0, 0, 0, getScrollRange(), 0, overScrollDistance);
        this.mFlinging = true;
        postInvalidate();
    }

    protected boolean shouldStartScroll(MotionEvent motionEvent) {
        int i;
        ActionBar.OnScrollListener onScrollListener;
        ActionBar.OnScrollListener onScrollListener2;
        int i2 = this.mActivePointerId;
        if (i2 == -1) {
            return false;
        }
        int iFindPointerIndex = motionEvent.findPointerIndex(i2);
        if (iFindPointerIndex == -1) {
            Log.w(TAG, "invalid pointer index");
            return false;
        }
        float x = motionEvent.getX(iFindPointerIndex);
        float y = motionEvent.getY(iFindPointerIndex);
        int i3 = (int) (y - this.mLastMotionY);
        int iAbs = Math.abs(i3);
        int i4 = (int) x;
        int i5 = (int) y;
        boolean z = (inChild(this.mContentView, i4, i5) || inChild(this.mTabScrollView, i4, i5)) && iAbs > this.mTouchSlop && iAbs > ((int) Math.abs(x - this.mLastMotionX)) && ((i = this.mMotionY) != 0 ? i3 <= 0 || i < getOverScrollDistance() || (onScrollListener = this.mOnScrollListener) == null || !onScrollListener.onContentScrolled() : i3 >= 0 && ((onScrollListener2 = this.mOnScrollListener) == null || !onScrollListener2.onContentScrolled()));
        if (z) {
            this.mLastMotionY = y;
            this.mLastMotionX = x;
            this.mState = i3 > 0 ? 1 : 0;
            ViewParent parent = getParent();
            if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(true);
            }
        }
        return z;
    }

    protected void onScrollBegin() {
        ActionBar.OnScrollListener onScrollListener = this.mOnScrollListener;
        if (onScrollListener != null) {
            onScrollListener.onStartScroll();
        }
    }

    protected void onScroll(float f) {
        applyTranslationY(f);
        ActionBar.OnScrollListener onScrollListener = this.mOnScrollListener;
        if (onScrollListener != null) {
            onScrollListener.onScroll(this.mState, f / this.mScrollRange);
        }
    }

    protected void onScrollEnd() {
        this.mState = -1;
        ActionBar.OnScrollListener onScrollListener = this.mOnScrollListener;
        if (onScrollListener != null) {
            onScrollListener.onStopScroll();
        }
    }

    protected float motionToTranslation(float f) {
        float f2 = (((-this.mOverScrollDistance) + f) - this.mScrollRange) - this.mScrollStart;
        ensureTabScrollView();
        View view = this.mTabScrollView;
        return (view == null || view.getVisibility() != 0) ? f2 : f2 - this.mTabScrollView.getHeight();
    }

    protected void applyTranslationY(float f) {
        float fMotionToTranslation = motionToTranslation(f);
        this.mContentView.setTranslationY(fMotionToTranslation);
        ensureTabScrollView();
        View view = this.mTabScrollView;
        if (view != null) {
            view.setTranslationY(fMotionToTranslation);
        }
    }

    protected void springBack() {
        if (this.mIsSpringBackEnabled) {
            int scrollRange = getScrollRange();
            int i = this.mMotionY;
            this.mScroller.startScroll(0, i, 0, i > scrollRange / 2 ? scrollRange - i : -i, 800);
            AnimationHelper.postInvalidateOnAnimation(this);
        }
    }

    private void onSecondaryPointerUp(MotionEvent motionEvent) {
        int action = (motionEvent.getAction() & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8;
        if (motionEvent.getPointerId(action) == this.mActivePointerId) {
            int i = action == 0 ? 1 : 0;
            this.mLastMotionY = (int) motionEvent.getY(i);
            this.mActivePointerId = motionEvent.getPointerId(i);
            VelocityTracker velocityTracker = this.mVelocityTracker;
            if (velocityTracker != null) {
                velocityTracker.clear();
            }
        }
    }

    private boolean isTabViewVisibilityChanged() {
        int visibility;
        ensureTabScrollView();
        View view = this.mTabScrollView;
        if (view == null || (visibility = view.getVisibility()) == this.mTabViewVisibility) {
            return false;
        }
        this.mTabViewVisibility = visibility;
        return true;
    }

    public void setMotionY(int i) {
        this.mMotionY = i;
        onScroll(i);
    }
}
