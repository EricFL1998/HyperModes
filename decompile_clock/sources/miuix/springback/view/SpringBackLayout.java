package miuix.springback.view;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.OverScroller;
import androidx.core.view.NestedScrollingChild3;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.widget.ListViewCompat;
import androidx.core.widget.NestedScrollView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miuix.core.util.EnvStateManager;
import miuix.core.view.NestedCurrentFling;
import miuix.core.view.ScrollStateDispatcher;
import miuix.core.view.ViewCompatOnScrollChangeListener;
import miuix.os.Build;
import miuix.overscroller.widget.AnimationHelper;
import miuix.reflect.ReflectionHelper;
import miuix.springback.R;

/* JADX INFO: loaded from: classes3.dex */
public class SpringBackLayout extends ViewGroup implements NestedScrollingParent3, NestedScrollingChild3, NestedCurrentFling, ScrollStateDispatcher {
    public static final int ANGLE = 4;
    public static final int HORIZONTAL = 1;
    private static final int INVALID_ID = -1;
    private static final int INVALID_POINTER = -1;
    private static final int MAX_FLING_CONSUME_COUNTER = 4;
    public static final int SPRING_BACK_BOTTOM = 2;
    public static final int SPRING_BACK_TOP = 1;
    private static final String TAG = "SpringBackLayout";
    public static final int UNCHECK_ORIENTATION = 0;
    private static final int VELOCITY_THRESHOLD = 2000;
    public static final int VERTICAL = 2;
    private int consumeNestFlingCounter;
    private int mActivePointerId;
    private EmptyStateInflationDelegate mDelegate;
    private int mFakeScrollX;
    private int mFakeScrollY;
    private SpringBackLayoutHelper mHelper;
    private boolean mInGlobalRomMode;
    private int mInitPaddingTop;
    private float mInitialDownX;
    private float mInitialDownY;
    private float mInitialMotionX;
    private float mInitialMotionY;
    private boolean mIsBeingDragged;
    private OverScroller mListOverScroller;
    private AbsListView.OnScrollListener mListViewScrollListener;
    private boolean mNestedFlingInProgress;
    private int mNestedScrollAxes;
    private boolean mNestedScrollInProgress;
    private final NestedScrollingChildHelper mNestedScrollingChildHelper;
    private final NestedScrollingParentHelper mNestedScrollingParentHelper;
    private final int[] mNestedScrollingV2ConsumedCompat;
    private List<ViewCompatOnScrollChangeListener> mOnScrollChangeListeners;
    private OnSpringListener mOnSpringListener;
    private int mOriginScrollOrientation;
    private final int[] mParentOffsetInWindow;
    private final int[] mParentScrollConsumed;
    protected int mScreenHeight;
    protected int mScreenWidth;
    private boolean mScrollByFling;
    private int mScrollOrientation;
    private int mScrollState;
    private float mScrollVelocity;
    private boolean mSpringBackEnable;
    private int mSpringBackMode;
    private SpringOverScroller mSpringOverScroller;
    private SpringScroller mSpringScroller;
    private View mTarget;
    private int mTargetId;
    private float mTotalFlingUnconsumed;
    private float mTotalScrollBottomUnconsumed;
    private float mTotalScrollTopUnconsumed;
    private int mTouchSlop;
    private float mVelocityX;
    private float mVelocityY;

    public interface OnSpringListener {
        boolean onSpringBack();
    }

    public SpringBackLayout(Context context) {
        this(context, null);
    }

    public SpringBackLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mActivePointerId = -1;
        this.consumeNestFlingCounter = 0;
        this.mParentScrollConsumed = new int[2];
        this.mParentOffsetInWindow = new int[2];
        this.mNestedScrollingV2ConsumedCompat = new int[2];
        this.mOnScrollChangeListeners = new ArrayList();
        this.mScrollState = 0;
        this.mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        this.mNestedScrollingChildHelper = miuix.core.view.NestedScrollingChildHelper.obtain(this);
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.SpringBackLayout);
        this.mTargetId = typedArrayObtainStyledAttributes.getResourceId(R.styleable.SpringBackLayout_scrollableView, -1);
        this.mOriginScrollOrientation = typedArrayObtainStyledAttributes.getInt(R.styleable.SpringBackLayout_scrollOrientation, 2);
        this.mSpringBackMode = typedArrayObtainStyledAttributes.getInt(R.styleable.SpringBackLayout_springBackMode, 3);
        int resourceId = typedArrayObtainStyledAttributes.getResourceId(R.styleable.SpringBackLayout_emptyStateView, -1);
        typedArrayObtainStyledAttributes.recycle();
        this.mSpringScroller = new SpringScroller();
        this.mSpringOverScroller = new SpringOverScroller();
        this.mHelper = new SpringBackLayoutHelper(this, this.mOriginScrollOrientation);
        if (resourceId != -1) {
            this.mDelegate = new EmptyStateInflationDelegate(this, resourceId);
        }
        setNestedScrollingEnabled(true);
        Point screenSize = EnvStateManager.getScreenSize(context);
        this.mScreenWidth = screenSize.x;
        this.mScreenHeight = screenSize.y;
        boolean z = Build.IS_INTERNATIONAL_BUILD;
        this.mInGlobalRomMode = z;
        if (z) {
            this.mSpringBackEnable = false;
        } else {
            this.mSpringBackEnable = true;
        }
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mInitPaddingTop = getPaddingTop();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EmptyStateInflationDelegate emptyStateInflationDelegate = this.mDelegate;
        if (emptyStateInflationDelegate != null) {
            emptyStateInflationDelegate.onDetachedFromWindow();
        }
    }

    public void setSpringBackEnable(boolean z) {
        if (this.mInGlobalRomMode) {
            return;
        }
        this.mSpringBackEnable = z;
    }

    public void setSpringBackEnableOnTriggerAttached(boolean z) {
        this.mSpringBackEnable = z;
    }

    public boolean springBackEnable() {
        return this.mSpringBackEnable;
    }

    public void setScrollOrientation(int i) {
        this.mOriginScrollOrientation = i;
        this.mHelper.mTargetScrollOrientation = i;
    }

    public void setSpringBackMode(int i) {
        this.mSpringBackMode = i;
    }

    public int getSpringBackMode() {
        return this.mSpringBackMode;
    }

    private int getFakeScrollX() {
        return this.mFakeScrollX;
    }

    private int getFakeScrollY() {
        return this.mFakeScrollY;
    }

    public int getSpringScrollX() {
        if (this.mSpringBackEnable) {
            return getScrollX();
        }
        return getFakeScrollX();
    }

    public int getSpringScrollY() {
        if (this.mSpringBackEnable) {
            return getScrollY();
        }
        return getFakeScrollY();
    }

    @Override // android.view.View
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        View view = this.mTarget;
        if (view == null || !(view instanceof NestedScrollingChild3) || z == this.mTarget.isNestedScrollingEnabled()) {
            return;
        }
        this.mTarget.setNestedScrollingEnabled(z);
    }

    private boolean supportTopSpringBackMode() {
        return (this.mSpringBackMode & 1) != 0;
    }

    private boolean supportBottomSpringBackMode() {
        return (this.mSpringBackMode & 2) != 0;
    }

    public void setTarget(View view) {
        this.mTarget = view;
        View view2 = this.mTarget;
        if ((view2 instanceof NestedScrollingChild3) && !view2.isNestedScrollingEnabled()) {
            this.mTarget.setNestedScrollingEnabled(true);
        }
        if (this.mTarget.getOverScrollMode() == 2 || !this.mSpringBackEnable) {
            return;
        }
        this.mTarget.setOverScrollMode(2);
    }

    private void ensureTarget() {
        ensureTargetInitialized();
        ensureNestedScrollingEnabled();
        ensureOverScrollMode();
        ensureAbsListViewSetup();
    }

    private void ensureTargetInitialized() {
        if (this.mTarget == null) {
            int i = this.mTargetId;
            if (i == -1) {
                throw new IllegalArgumentException("invalid target Id");
            }
            this.mTarget = findViewById(i);
        }
        if (this.mTarget == null) {
            throw new IllegalArgumentException("fail to get target");
        }
    }

    private void ensureNestedScrollingEnabled() {
        if (isEnabled()) {
            View view = this.mTarget;
            if (!(view instanceof NestedScrollingChild3) || view.isNestedScrollingEnabled()) {
                return;
            }
            this.mTarget.setNestedScrollingEnabled(true);
        }
    }

    private void ensureOverScrollMode() {
        if (this.mTarget.getOverScrollMode() == 2 || !this.mSpringBackEnable) {
            return;
        }
        this.mTarget.setOverScrollMode(2);
    }

    private void ensureAbsListViewSetup() {
        View view = this.mTarget;
        if ((view instanceof AbsListView) && this.mSpringBackEnable) {
            setupAbsListView((AbsListView) view);
        }
    }

    private void setupAbsListView(AbsListView absListView) {
        if (this.mListViewScrollListener == null) {
            this.mListViewScrollListener = new AbsListView.OnScrollListener() { // from class: miuix.springback.view.SpringBackLayout.1
                private long mLastEventTime = -1;
                private int mLastScrollY = -1;
                private int mScrollState;

                @Override // android.widget.AbsListView.OnScrollListener
                public void onScrollStateChanged(AbsListView absListView2, int i) {
                    this.mScrollState = i;
                }

                @Override // android.widget.AbsListView.OnScrollListener
                public void onScroll(AbsListView absListView2, int i, int i2, int i3) {
                    if (this.mScrollState != 2) {
                        return;
                    }
                    float fComputeScrollVelocity = computeScrollVelocity(absListView2);
                    if (SpringBackLayout.this.isTargetScrollToTop(2) || SpringBackLayout.this.isTargetScrollToBottom(2)) {
                        float listViewScrollVelocity = SpringBackLayout.this.getListViewScrollVelocity(absListView2);
                        if (listViewScrollVelocity != 0.0f) {
                            SpringBackLayout.this.mScrollVelocity = (SpringBackLayout.this.isTargetScrollToTop(2) ? 1.0f : -1.0f) * listViewScrollVelocity;
                        }
                        if (Math.abs(SpringBackLayout.this.mScrollVelocity) <= 2000.0f) {
                            SpringBackLayout.this.dispatchScrollState(0);
                            return;
                        }
                        SpringBackLayout.this.dispatchScrollState(0);
                        SpringBackLayout.this.mSpringScroller.forceStop();
                        SpringBackLayout.this.mSpringOverScroller.forceStop();
                        SpringBackLayout.this.mSpringOverScroller.startOverScroll(SpringBackLayout.this.getScrollX(), SpringBackLayout.this.getScrollY(), -SpringBackLayout.this.mScrollVelocity, 2);
                        AnimationHelper.postInvalidateOnAnimation(SpringBackLayout.this);
                        return;
                    }
                    SpringBackLayout.this.mScrollVelocity = fComputeScrollVelocity;
                }

                /* JADX WARN: Code duplicated, block: B:9:0x0028  */
                private float computeScrollVelocity(AbsListView absListView2) {
                    float f;
                    long jCurrentTimeMillis = System.currentTimeMillis();
                    long j = jCurrentTimeMillis - this.mLastEventTime;
                    int scrollY = getScrollY(absListView2);
                    int i = this.mLastScrollY;
                    if (i == -1 || this.mLastEventTime == -1) {
                        f = 0.0f;
                    } else {
                        int i2 = scrollY - i;
                        if (j > 0) {
                            f = (i2 / j) * 1000.0f;
                        } else {
                            f = 0.0f;
                        }
                    }
                    this.mLastEventTime = jCurrentTimeMillis;
                    this.mLastScrollY = scrollY;
                    return f;
                }

                private int getScrollY(AbsListView absListView2) {
                    View childAt = absListView2.getChildAt(0);
                    if (childAt == null) {
                        return 0;
                    }
                    return childAt.getTop() - (absListView2.getFirstVisiblePosition() * childAt.getHeight());
                }
            };
        }
        absListView.setOnScrollListener(this.mListViewScrollListener);
    }

    public View getTarget() {
        return this.mTarget;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.mTarget.getVisibility() != 8) {
            int measuredWidth = this.mTarget.getMeasuredWidth();
            int measuredHeight = this.mTarget.getMeasuredHeight();
            int paddingLeft = getPaddingLeft();
            int paddingTop = getPaddingTop();
            this.mTarget.layout(paddingLeft, paddingTop, measuredWidth + paddingLeft, measuredHeight + paddingTop);
        }
    }

    @Override // android.view.View
    public void onMeasure(int i, int i2) {
        int iMin;
        int iMin2;
        ensureTarget();
        int mode = View.MeasureSpec.getMode(i);
        int mode2 = View.MeasureSpec.getMode(i2);
        measureChild(this.mTarget, i, i2);
        if (mode == 0) {
            iMin = this.mTarget.getMeasuredWidth() + getPaddingLeft() + getPaddingRight();
        } else if (mode == 1073741824) {
            iMin = View.MeasureSpec.getSize(i);
        } else {
            iMin = Math.min(View.MeasureSpec.getSize(i), this.mTarget.getMeasuredWidth() + getPaddingLeft() + getPaddingRight());
        }
        if (mode2 == 0) {
            iMin2 = this.mTarget.getMeasuredHeight() + getPaddingTop() + getPaddingBottom();
        } else if (mode2 == 1073741824) {
            iMin2 = View.MeasureSpec.getSize(i2);
        } else {
            iMin2 = Math.min(View.MeasureSpec.getSize(i2), this.mTarget.getMeasuredHeight() + getPaddingTop() + getPaddingBottom());
        }
        setMeasuredDimension(iMin, iMin2);
    }

    @Override // android.view.View
    public void computeScroll() {
        super.computeScroll();
        if (this.mSpringScroller.computeScrollOffset()) {
            scrollTo(this.mSpringScroller.getCurrX(), this.mSpringScroller.getCurrY());
            if (!this.mSpringScroller.isFinished()) {
                AnimationHelper.postInvalidateOnAnimation(this);
                return;
            }
            if ((getSpringScrollX() != 0 || getSpringScrollY() != 0) && this.mScrollState != 2) {
                Log.d(TAG, "Scroll stop but state is not correct.");
                springBack(this.mNestedScrollAxes != 2 ? 1 : 2);
                return;
            } else {
                dispatchScrollState(0);
                return;
            }
        }
        if (this.mSpringOverScroller.computeScrollOffset()) {
            scrollTo(this.mSpringOverScroller.getCurrX(), this.mSpringOverScroller.getCurrY());
            if (!this.mSpringOverScroller.isFinished()) {
                AnimationHelper.postInvalidateOnAnimation(this);
            } else {
                springBack(2);
            }
        }
    }

    @Override // android.view.View
    public void scrollTo(int i, int i2) {
        if (this.mSpringBackEnable) {
            super.scrollTo(i, i2);
            return;
        }
        int i3 = this.mFakeScrollX;
        if (i3 == i && this.mFakeScrollY == i2) {
            return;
        }
        int i4 = this.mFakeScrollY;
        this.mFakeScrollX = i;
        this.mFakeScrollY = i2;
        onScrollChanged(i, i2, i3, i4);
        if (!awakenScrollBars()) {
            postInvalidateOnAnimation();
        }
        requestLayout();
    }

    @Override // android.view.View
    protected void onScrollChanged(int i, int i2, int i3, int i4) {
        super.onScrollChanged(i, i2, i3, i4);
        Iterator<ViewCompatOnScrollChangeListener> it = this.mOnScrollChangeListeners.iterator();
        while (it.hasNext()) {
            it.next().onScrollChange(this, i, i2, i3, i4);
        }
    }

    private boolean isVerticalTargetScrollToTop() {
        View view = this.mTarget;
        if (view instanceof ListView) {
            return !ListViewCompat.canScrollList((ListView) view, -1);
        }
        return !view.canScrollVertically(-1);
    }

    private boolean isHorizontalTargetScrollToTop() {
        return !this.mTarget.canScrollHorizontally(-1);
    }

    private boolean isTargetScrollOrientation(int i) {
        return this.mScrollOrientation == i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isTargetScrollToTop(int i) {
        if (i == 2) {
            View view = this.mTarget;
            if (view instanceof ListView) {
                return !ListViewCompat.canScrollList((ListView) view, -1);
            }
            return !view.canScrollVertically(-1);
        }
        return !this.mTarget.canScrollHorizontally(-1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isTargetScrollToBottom(int i) {
        if (i == 2) {
            View view = this.mTarget;
            if (view instanceof ListView) {
                return !ListViewCompat.canScrollList((ListView) view, 1);
            }
            return !view.canScrollVertically(1);
        }
        return !this.mTarget.canScrollHorizontally(1);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == 0 && this.mScrollState == 2 && this.mHelper.isTouchInTarget(motionEvent)) {
            dispatchScrollState(1);
        }
        if (motionEvent.getActionMasked() == 1) {
            int scrollX = getScrollX();
            int scrollY = getScrollY();
            if (this.mScrollState != 2 && scrollX == 0 && scrollY == 0) {
                dispatchScrollState(0);
            }
        }
        View view = this.mTarget;
        if (view != null && (view instanceof AbsListView)) {
            return handleAbsListViewTouchEvent(motionEvent);
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    private boolean handleAbsListViewTouchEvent(MotionEvent motionEvent) {
        if (isEnabled() && springBackEnable()) {
            if (motionEvent.getActionMasked() == 0) {
                if (!this.mSpringScroller.isFinished()) {
                    this.mSpringScroller.forceStop();
                }
                if (!this.mSpringOverScroller.isFinished()) {
                    this.mSpringOverScroller.forceStop();
                }
            }
            return onListViewVerticalDispatchTouchEvent(motionEvent);
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    private boolean shouldSkipListViewEventHandling(MotionEvent motionEvent) {
        if (!isTargetScrollToBottom(2) && !isTargetScrollToTop(2)) {
            this.mInitialMotionY = motionEvent.getY();
            return true;
        }
        if (!isTargetScrollToTop(2) || this.mSpringBackEnable) {
            return isTargetScrollToBottom(2) && !this.mSpringBackEnable;
        }
        return true;
    }

    private boolean onListViewVerticalDispatchTouchEvent(MotionEvent motionEvent) {
        if (shouldSkipListViewEventHandling(motionEvent)) {
            return super.dispatchTouchEvent(motionEvent);
        }
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            return handleAbsListViewActionDown(motionEvent);
        }
        if (actionMasked != 1) {
            if (actionMasked == 2) {
                return handleAbsListViewActionMove(motionEvent);
            }
            if (actionMasked != 3) {
                return super.dispatchTouchEvent(motionEvent);
            }
        }
        return handleAbsListViewActionUp(motionEvent);
    }

    private boolean handleAbsListViewActionDown(MotionEvent motionEvent) {
        int pointerId = motionEvent.getPointerId(0);
        this.mActivePointerId = pointerId;
        int iFindPointerIndex = motionEvent.findPointerIndex(pointerId);
        if (iFindPointerIndex < 0) {
            return super.dispatchTouchEvent(motionEvent);
        }
        this.mInitialDownY = motionEvent.getY(iFindPointerIndex);
        if (getScrollY() != 0) {
            this.mIsBeingDragged = true;
            this.mInitialMotionY = this.mInitialDownY + getScrollY();
        } else {
            this.mIsBeingDragged = false;
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    private boolean handleAbsListViewActionMove(MotionEvent motionEvent) {
        int i = this.mActivePointerId;
        if (i == -1) {
            Log.e(TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
            return super.dispatchTouchEvent(motionEvent);
        }
        int iFindPointerIndex = motionEvent.findPointerIndex(i);
        boolean z = false;
        if (iFindPointerIndex < 0) {
            Log.e(TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
            return false;
        }
        float y = motionEvent.getY(iFindPointerIndex);
        if (isTargetScrollToBottom(2) && isTargetScrollToTop(2)) {
            z = true;
        }
        if ((z || !isTargetScrollToTop(2)) && (!z || y <= this.mInitialDownY)) {
            if (this.mInitialDownY - y > this.mTouchSlop && !this.mIsBeingDragged) {
                this.mIsBeingDragged = true;
                dispatchScrollState(1);
                this.mInitialMotionY = y;
            }
        } else if (y - this.mInitialDownY > this.mTouchSlop && !this.mIsBeingDragged) {
            this.mIsBeingDragged = true;
            dispatchScrollState(1);
            this.mInitialMotionY = y;
        }
        if (!this.mIsBeingDragged) {
            return super.dispatchTouchEvent(motionEvent);
        }
        return handleOverscrollMovements(motionEvent, iFindPointerIndex);
    }

    private boolean handleAbsListViewActionUp(MotionEvent motionEvent) {
        if (this.mIsBeingDragged) {
            this.mIsBeingDragged = false;
            motionEvent.setAction(3);
            if (getScrollY() != 0) {
                springBack(2);
            }
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    private boolean handleOverscrollMovements(MotionEvent motionEvent, int i) {
        float y = motionEvent.getY(i);
        float fSignum = Math.signum(y - this.mInitialMotionY) * obtainSpringBackDistance(y - this.mInitialMotionY, 2);
        if (fSignum != 0.0f) {
            if ((fSignum > 0.0f && isTargetScrollToTop(2)) || (fSignum < 0.0f && isTargetScrollToBottom(2))) {
                moveTarget(fSignum, 2);
                return false;
            }
            moveTarget(0.0f, 2);
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (!this.mSpringBackEnable || !isEnabled() || this.mNestedFlingInProgress || this.mNestedScrollInProgress || this.mTarget.isNestedScrollingEnabled()) {
            return false;
        }
        if (motionEvent.getActionMasked() == 0) {
            if (!this.mSpringScroller.isFinished()) {
                this.mSpringScroller.forceStop();
            }
            if (!this.mSpringOverScroller.isFinished()) {
                this.mSpringOverScroller.forceStop();
            }
        }
        if (!supportTopSpringBackMode() && !supportBottomSpringBackMode()) {
            return false;
        }
        View view = this.mTarget;
        if (view != null && (view instanceof AbsListView)) {
            return super.onInterceptTouchEvent(motionEvent);
        }
        int i = this.mOriginScrollOrientation;
        if ((i & 4) != 0) {
            checkOrientation(motionEvent);
            if (isTargetScrollOrientation(2) && (this.mOriginScrollOrientation & 1) != 0 && getScrollX() == 0.0f) {
                return false;
            }
            if (isTargetScrollOrientation(1) && (this.mOriginScrollOrientation & 2) != 0 && getScrollY() == 0.0f) {
                return false;
            }
            if (isTargetScrollOrientation(2) || isTargetScrollOrientation(1)) {
                disallowParentInterceptTouchEvent(true);
            }
        } else {
            this.mScrollOrientation = i;
        }
        if (isTargetScrollOrientation(2)) {
            return onVerticalInterceptTouchEvent(motionEvent);
        }
        if (isTargetScrollOrientation(1)) {
            return onHorizontalInterceptTouchEvent(motionEvent);
        }
        return false;
    }

    private void disallowParentInterceptTouchEvent(boolean z) {
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(z);
        }
    }

    private void checkOrientation(MotionEvent motionEvent) {
        this.mHelper.checkOrientation(motionEvent);
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            this.mInitialDownY = this.mHelper.mInitialDownY;
            this.mInitialDownX = this.mHelper.mInitialDownX;
            this.mActivePointerId = this.mHelper.mActivePointerId;
            if (getScrollY() != 0) {
                this.mScrollOrientation = 2;
                requestDisallowParentInterceptTouchEvent(true);
            } else if (getScrollX() != 0) {
                this.mScrollOrientation = 1;
                requestDisallowParentInterceptTouchEvent(true);
            } else {
                this.mScrollOrientation = 0;
            }
            if ((this.mOriginScrollOrientation & 2) != 0) {
                checkScrollStart(2);
                return;
            } else {
                checkScrollStart(1);
                return;
            }
        }
        if (actionMasked != 1) {
            if (actionMasked == 2) {
                if (this.mScrollOrientation != 0 || this.mHelper.mScrollOrientation == 0) {
                    return;
                }
                this.mScrollOrientation = this.mHelper.mScrollOrientation;
                return;
            }
            if (actionMasked != 3) {
                if (actionMasked != 6) {
                    return;
                }
                onSecondaryPointerUp(motionEvent);
                return;
            }
        }
        disallowParentInterceptTouchEvent(false);
        if ((this.mOriginScrollOrientation & 2) != 0) {
            springBack(2);
        } else {
            springBack(1);
        }
    }

    /* JADX WARN: Code duplicated, block: B:57:0x00a8  */
    private boolean onVerticalInterceptTouchEvent(MotionEvent motionEvent) {
        boolean z = false;
        if (!isTargetScrollToTop(2) && !isTargetScrollToBottom(2)) {
            return false;
        }
        if (isTargetScrollToTop(2) && !supportTopSpringBackMode()) {
            return false;
        }
        if (isTargetScrollToBottom(2) && !supportBottomSpringBackMode()) {
            return false;
        }
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            int pointerId = motionEvent.getPointerId(0);
            this.mActivePointerId = pointerId;
            int iFindPointerIndex = motionEvent.findPointerIndex(pointerId);
            if (iFindPointerIndex < 0) {
                return false;
            }
            this.mInitialDownY = motionEvent.getY(iFindPointerIndex);
            if (getScrollY() != 0) {
                this.mIsBeingDragged = true;
                this.mInitialMotionY = this.mInitialDownY;
            } else {
                this.mIsBeingDragged = false;
            }
        } else if (actionMasked == 1) {
            this.mIsBeingDragged = false;
            this.mActivePointerId = -1;
        } else if (actionMasked == 2) {
            int i = this.mActivePointerId;
            if (i == -1) {
                Log.e(TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
                return false;
            }
            int iFindPointerIndex2 = motionEvent.findPointerIndex(i);
            if (iFindPointerIndex2 < 0) {
                Log.e(TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                return false;
            }
            float y = motionEvent.getY(iFindPointerIndex2);
            if (isTargetScrollToBottom(2) && isTargetScrollToTop(2)) {
                z = true;
            }
            if ((z || !isTargetScrollToTop(2)) && (!z || y <= this.mInitialDownY)) {
                if (this.mInitialDownY - y > this.mTouchSlop && !this.mIsBeingDragged) {
                    this.mIsBeingDragged = true;
                    dispatchScrollState(1);
                    this.mInitialMotionY = y;
                }
            } else if (y - this.mInitialDownY > this.mTouchSlop && !this.mIsBeingDragged) {
                this.mIsBeingDragged = true;
                dispatchScrollState(1);
                this.mInitialMotionY = y;
            }
        } else if (actionMasked == 3) {
            this.mIsBeingDragged = false;
            this.mActivePointerId = -1;
        } else if (actionMasked == 6) {
            onSecondaryPointerUp(motionEvent);
        }
        return this.mIsBeingDragged;
    }

    /* JADX WARN: Code duplicated, block: B:58:0x00a8  */
    private boolean onHorizontalInterceptTouchEvent(MotionEvent motionEvent) {
        boolean z = false;
        if (!isTargetScrollToTop(1) && !isTargetScrollToBottom(1)) {
            return false;
        }
        if (isTargetScrollToTop(1) && !supportTopSpringBackMode()) {
            return false;
        }
        if (isTargetScrollToBottom(1) && !supportBottomSpringBackMode()) {
            return false;
        }
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            int pointerId = motionEvent.getPointerId(0);
            this.mActivePointerId = pointerId;
            int iFindPointerIndex = motionEvent.findPointerIndex(pointerId);
            if (iFindPointerIndex < 0) {
                return false;
            }
            this.mInitialDownX = motionEvent.getX(iFindPointerIndex);
            if (getScrollX() != 0) {
                this.mIsBeingDragged = true;
                this.mInitialMotionX = this.mInitialDownX;
            } else {
                this.mIsBeingDragged = false;
            }
        } else if (actionMasked == 1) {
            this.mIsBeingDragged = false;
            this.mActivePointerId = -1;
        } else if (actionMasked == 2) {
            int i = this.mActivePointerId;
            if (i == -1) {
                Log.e(TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
                return false;
            }
            int iFindPointerIndex2 = motionEvent.findPointerIndex(i);
            if (iFindPointerIndex2 < 0) {
                Log.e(TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                return false;
            }
            float x = motionEvent.getX(iFindPointerIndex2);
            if (isTargetScrollToBottom(1) && isTargetScrollToTop(1)) {
                z = true;
            }
            if ((z || !isTargetScrollToTop(1)) && (!z || x <= this.mInitialDownX)) {
                if (this.mInitialDownX - x > this.mTouchSlop && !this.mIsBeingDragged) {
                    this.mIsBeingDragged = true;
                    dispatchScrollState(1);
                    this.mInitialMotionX = x;
                }
            } else if (x - this.mInitialDownX > this.mTouchSlop && !this.mIsBeingDragged) {
                this.mIsBeingDragged = true;
                dispatchScrollState(1);
                this.mInitialMotionX = x;
            }
        } else if (actionMasked == 3) {
            this.mIsBeingDragged = false;
            this.mActivePointerId = -1;
        } else if (actionMasked == 6) {
            onSecondaryPointerUp(motionEvent);
        }
        return this.mIsBeingDragged;
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void requestDisallowInterceptTouchEvent(boolean z) {
        if (isEnabled() && this.mSpringBackEnable) {
            return;
        }
        super.requestDisallowInterceptTouchEvent(z);
    }

    public void internalRequestDisallowInterceptTouchEvent(boolean z) {
        super.requestDisallowInterceptTouchEvent(z);
    }

    public void requestDisallowParentInterceptTouchEvent(boolean z) {
        ViewParent parent = getParent();
        parent.requestDisallowInterceptTouchEvent(z);
        while (parent != null) {
            if (parent instanceof SpringBackLayout) {
                ((SpringBackLayout) parent).internalRequestDisallowInterceptTouchEvent(z);
            }
            parent = parent.getParent();
        }
    }

    private boolean handleAbsListViewTouch(MotionEvent motionEvent) {
        if (this.mIsBeingDragged) {
            return true;
        }
        return super.onTouchEvent(motionEvent);
    }

    private boolean shouldSkipTouchProcessing(MotionEvent motionEvent) {
        return !isEnabled() || this.mNestedFlingInProgress || this.mNestedScrollInProgress || this.mTarget.isNestedScrollingEnabled();
    }

    private boolean shouldStopScrollers(MotionEvent motionEvent) {
        return motionEvent.getActionMasked() == 0;
    }

    private void stopActiveScrollers() {
        if (!this.mSpringScroller.isFinished()) {
            this.mSpringScroller.forceStop();
        }
        if (this.mSpringOverScroller.isFinished()) {
            return;
        }
        this.mSpringOverScroller.forceStop();
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.mTarget instanceof AbsListView) {
            return handleAbsListViewTouch(motionEvent);
        }
        if (shouldSkipTouchProcessing(motionEvent)) {
            return false;
        }
        if (shouldStopScrollers(motionEvent)) {
            stopActiveScrollers();
        }
        if (isTargetScrollOrientation(2)) {
            return onVerticalTouchEvent(motionEvent);
        }
        if (isTargetScrollOrientation(1)) {
            return onHorizontalTouchEvent(motionEvent);
        }
        return false;
    }

    private boolean onHorizontalTouchEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (!isTargetScrollToTop(1) && !isTargetScrollToBottom(1)) {
            return onScrollEvent(motionEvent, actionMasked, 1);
        }
        if (isTargetScrollToBottom(1)) {
            return onScrollUpEvent(motionEvent, actionMasked, 1);
        }
        return onScrollDownEvent(motionEvent, actionMasked, 1);
    }

    private boolean onVerticalTouchEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (!isTargetScrollToTop(2) && !isTargetScrollToBottom(2)) {
            return onScrollEvent(motionEvent, actionMasked, 2);
        }
        if (isTargetScrollToBottom(2)) {
            return onScrollUpEvent(motionEvent, actionMasked, 2);
        }
        return onScrollDownEvent(motionEvent, actionMasked, 2);
    }

    private boolean onScrollEvent(MotionEvent motionEvent, int i, int i2) {
        float fSignum;
        float fObtainSpringBackDistance;
        int actionIndex;
        if (i == 0) {
            this.mActivePointerId = motionEvent.getPointerId(0);
            checkScrollStart(i2);
        } else {
            if (i != 1) {
                if (i == 2) {
                    int iFindPointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
                    if (iFindPointerIndex < 0) {
                        Log.e(TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                        return false;
                    }
                    if (this.mIsBeingDragged) {
                        if (i2 == 2) {
                            float y = motionEvent.getY(iFindPointerIndex);
                            fSignum = Math.signum(y - this.mInitialMotionY);
                            fObtainSpringBackDistance = obtainSpringBackDistance(y - this.mInitialMotionY, i2);
                        } else {
                            float x = motionEvent.getX(iFindPointerIndex);
                            fSignum = Math.signum(x - this.mInitialMotionX);
                            fObtainSpringBackDistance = obtainSpringBackDistance(x - this.mInitialMotionX, i2);
                        }
                        requestDisallowParentInterceptTouchEvent(true);
                        moveTarget(fSignum * fObtainSpringBackDistance, i2);
                    }
                } else if (i != 3) {
                    if (i == 5) {
                        int iFindPointerIndex2 = motionEvent.findPointerIndex(this.mActivePointerId);
                        if (iFindPointerIndex2 < 0) {
                            Log.e(TAG, "Got ACTION_POINTER_DOWN event but have an invalid active pointer id.");
                            return false;
                        }
                        if (i2 == 2) {
                            float y2 = motionEvent.getY(iFindPointerIndex2) - this.mInitialDownY;
                            actionIndex = motionEvent.getActionIndex();
                            if (actionIndex < 0) {
                                Log.e(TAG, "Got ACTION_POINTER_DOWN event but have an invalid action index.");
                                return false;
                            }
                            float y3 = motionEvent.getY(actionIndex) - y2;
                            this.mInitialDownY = y3;
                            this.mInitialMotionY = y3;
                        } else {
                            float x2 = motionEvent.getX(iFindPointerIndex2) - this.mInitialDownX;
                            actionIndex = motionEvent.getActionIndex();
                            if (actionIndex < 0) {
                                Log.e(TAG, "Got ACTION_POINTER_DOWN event but have an invalid action index.");
                                return false;
                            }
                            float x3 = motionEvent.getX(actionIndex) - x2;
                            this.mInitialDownX = x3;
                            this.mInitialMotionX = x3;
                        }
                        this.mActivePointerId = motionEvent.getPointerId(actionIndex);
                    } else if (i == 6) {
                        onSecondaryPointerUp(motionEvent);
                    }
                }
            }
            if (motionEvent.findPointerIndex(this.mActivePointerId) < 0) {
                Log.e(TAG, "Got ACTION_UP event but don't have an active pointer id.");
                return false;
            }
            if (this.mIsBeingDragged) {
                this.mIsBeingDragged = false;
                springBack(i2);
            }
            this.mActivePointerId = -1;
            return false;
        }
        return true;
    }

    private void checkVerticalScrollStart(int i) {
        if (getScrollY() != 0) {
            this.mIsBeingDragged = true;
            float fObtainTouchDistance = obtainTouchDistance(Math.abs(getScrollY()), Math.abs(obtainMaxSpringBackDistance(i)), 2);
            if (getScrollY() < 0) {
                this.mInitialDownY -= fObtainTouchDistance;
            } else {
                this.mInitialDownY += fObtainTouchDistance;
            }
            this.mInitialMotionY = this.mInitialDownY;
            return;
        }
        this.mIsBeingDragged = false;
    }

    private void checkScrollStart(int i) {
        if (i == 2) {
            checkVerticalScrollStart(i);
        } else {
            checkHorizontalScrollStart(i);
        }
    }

    private void checkHorizontalScrollStart(int i) {
        if (getScrollX() != 0) {
            this.mIsBeingDragged = true;
            float fObtainTouchDistance = obtainTouchDistance(Math.abs(getScrollX()), Math.abs(obtainMaxSpringBackDistance(i)), 2);
            if (getScrollX() < 0) {
                this.mInitialDownX -= fObtainTouchDistance;
            } else {
                this.mInitialDownX += fObtainTouchDistance;
            }
            this.mInitialMotionX = this.mInitialDownX;
            return;
        }
        this.mIsBeingDragged = false;
    }

    private boolean onScrollDownEvent(MotionEvent motionEvent, int i, int i2) {
        float fSignum;
        float fObtainSpringBackDistance;
        int actionIndex;
        if (i == 0) {
            this.mActivePointerId = motionEvent.getPointerId(0);
            checkScrollStart(i2);
        } else {
            if (i != 1) {
                if (i == 2) {
                    int iFindPointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
                    if (iFindPointerIndex < 0) {
                        Log.e(TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                        return false;
                    }
                    if (this.mIsBeingDragged) {
                        if (i2 == 2) {
                            float y = motionEvent.getY(iFindPointerIndex);
                            fSignum = Math.signum(y - this.mInitialMotionY);
                            fObtainSpringBackDistance = obtainSpringBackDistance(y - this.mInitialMotionY, i2);
                        } else {
                            float x = motionEvent.getX(iFindPointerIndex);
                            fSignum = Math.signum(x - this.mInitialMotionX);
                            fObtainSpringBackDistance = obtainSpringBackDistance(x - this.mInitialMotionX, i2);
                        }
                        float f = fSignum * fObtainSpringBackDistance;
                        if (f > 0.0f) {
                            requestDisallowParentInterceptTouchEvent(true);
                            moveTarget(f, i2);
                        } else {
                            moveTarget(0.0f, i2);
                            return false;
                        }
                    }
                } else if (i != 3) {
                    if (i == 5) {
                        int iFindPointerIndex2 = motionEvent.findPointerIndex(this.mActivePointerId);
                        if (iFindPointerIndex2 < 0) {
                            Log.e(TAG, "Got ACTION_POINTER_DOWN event but have an invalid active pointer id.");
                            return false;
                        }
                        if (i2 == 2) {
                            float y2 = motionEvent.getY(iFindPointerIndex2) - this.mInitialDownY;
                            actionIndex = motionEvent.getActionIndex();
                            if (actionIndex < 0) {
                                Log.e(TAG, "Got ACTION_POINTER_DOWN event but have an invalid action index.");
                                return false;
                            }
                            float y3 = motionEvent.getY(actionIndex) - y2;
                            this.mInitialDownY = y3;
                            this.mInitialMotionY = y3;
                        } else {
                            float x2 = motionEvent.getX(iFindPointerIndex2) - this.mInitialDownX;
                            actionIndex = motionEvent.getActionIndex();
                            if (actionIndex < 0) {
                                Log.e(TAG, "Got ACTION_POINTER_DOWN event but have an invalid action index.");
                                return false;
                            }
                            float x3 = motionEvent.getX(actionIndex) - x2;
                            this.mInitialDownX = x3;
                            this.mInitialMotionX = x3;
                        }
                        this.mActivePointerId = motionEvent.getPointerId(actionIndex);
                    } else if (i == 6) {
                        onSecondaryPointerUp(motionEvent);
                    }
                }
            }
            if (motionEvent.findPointerIndex(this.mActivePointerId) < 0) {
                Log.e(TAG, "Got ACTION_UP event but don't have an active pointer id.");
                return false;
            }
            if (this.mIsBeingDragged) {
                this.mIsBeingDragged = false;
                springBack(i2);
            }
            this.mActivePointerId = -1;
            return false;
        }
        return true;
    }

    private void moveTarget(float f, int i) {
        if (i == 2) {
            scrollTo(0, (int) (-f));
        } else {
            scrollTo((int) (-f), 0);
        }
    }

    private void springBack(int i) {
        springBack(0.0f, i, true);
    }

    private void springBack(float f, int i, boolean z) {
        OnSpringListener onSpringListener = this.mOnSpringListener;
        if (onSpringListener == null || !onSpringListener.onSpringBack()) {
            this.mSpringScroller.forceStop();
            this.mSpringOverScroller.forceStop();
            int scrollX = getScrollX();
            int scrollY = getScrollY();
            this.mSpringScroller.scrollByFling(scrollX, 0.0f, scrollY, 0.0f, f, i, false);
            if (scrollX == 0 && scrollY == 0 && f == 0.0f) {
                dispatchScrollState(0);
            } else {
                dispatchScrollState(2);
            }
            if (z) {
                AnimationHelper.postInvalidateOnAnimation(this);
            }
        }
    }

    private boolean onScrollUpEvent(MotionEvent motionEvent, int i, int i2) {
        float fSignum;
        float fObtainSpringBackDistance;
        int actionIndex;
        if (i == 0) {
            this.mActivePointerId = motionEvent.getPointerId(0);
            checkScrollStart(i2);
        } else {
            if (i != 1) {
                if (i == 2) {
                    int iFindPointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
                    if (iFindPointerIndex < 0) {
                        Log.e(TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                        return false;
                    }
                    if (this.mIsBeingDragged) {
                        if (i2 == 2) {
                            float y = motionEvent.getY(iFindPointerIndex);
                            fSignum = Math.signum(this.mInitialMotionY - y);
                            fObtainSpringBackDistance = obtainSpringBackDistance(this.mInitialMotionY - y, i2);
                        } else {
                            float x = motionEvent.getX(iFindPointerIndex);
                            fSignum = Math.signum(this.mInitialMotionX - x);
                            fObtainSpringBackDistance = obtainSpringBackDistance(this.mInitialMotionX - x, i2);
                        }
                        float f = fSignum * fObtainSpringBackDistance;
                        if (f > 0.0f) {
                            requestDisallowParentInterceptTouchEvent(true);
                            moveTarget(-f, i2);
                        } else {
                            moveTarget(0.0f, i2);
                            return false;
                        }
                    }
                } else if (i != 3) {
                    if (i == 5) {
                        int iFindPointerIndex2 = motionEvent.findPointerIndex(this.mActivePointerId);
                        if (iFindPointerIndex2 < 0) {
                            Log.e(TAG, "Got ACTION_POINTER_DOWN event but have an invalid active pointer id.");
                            return false;
                        }
                        if (i2 == 2) {
                            float y2 = motionEvent.getY(iFindPointerIndex2) - this.mInitialDownY;
                            actionIndex = motionEvent.getActionIndex();
                            if (actionIndex < 0) {
                                Log.e(TAG, "Got ACTION_POINTER_DOWN event but have an invalid action index.");
                                return false;
                            }
                            float y3 = motionEvent.getY(actionIndex) - y2;
                            this.mInitialDownY = y3;
                            this.mInitialMotionY = y3;
                        } else {
                            float x2 = motionEvent.getX(iFindPointerIndex2) - this.mInitialDownX;
                            actionIndex = motionEvent.getActionIndex();
                            if (actionIndex < 0) {
                                Log.e(TAG, "Got ACTION_POINTER_DOWN event but have an invalid action index.");
                                return false;
                            }
                            float x3 = motionEvent.getX(actionIndex) - x2;
                            this.mInitialDownX = x3;
                            this.mInitialMotionX = x3;
                        }
                        this.mActivePointerId = motionEvent.getPointerId(actionIndex);
                    } else if (i == 6) {
                        onSecondaryPointerUp(motionEvent);
                    }
                }
            }
            if (motionEvent.findPointerIndex(this.mActivePointerId) < 0) {
                Log.e(TAG, "Got ACTION_UP event but don't have an active pointer id.");
                return false;
            }
            if (this.mIsBeingDragged) {
                this.mIsBeingDragged = false;
                springBack(i2);
            }
            this.mActivePointerId = -1;
            return false;
        }
        return true;
    }

    private void onSecondaryPointerUp(MotionEvent motionEvent) {
        int actionIndex = motionEvent.getActionIndex();
        if (motionEvent.getPointerId(actionIndex) == this.mActivePointerId) {
            this.mActivePointerId = motionEvent.getPointerId(actionIndex == 0 ? 1 : 0);
        }
    }

    protected int getSpringBackRange(int i) {
        return i == 2 ? this.mScreenHeight : this.mScreenWidth;
    }

    protected float obtainSpringBackDistance(float f, int i) {
        int springBackRange = getSpringBackRange(i);
        return obtainDampingDistance(Math.min(Math.abs(f) / springBackRange, 1.0f), springBackRange);
    }

    protected float obtainMaxSpringBackDistance(int i) {
        return obtainDampingDistance(1.0f, getSpringBackRange(i));
    }

    protected float obtainDampingDistance(float f, int i) {
        double dMin = Math.min(f, 1.0f);
        return ((float) (((Math.pow(dMin, 3.0d) / 3.0d) - Math.pow(dMin, 2.0d)) + dMin)) * i;
    }

    protected float obtainTouchDistance(float f, float f2, int i) {
        int springBackRange = getSpringBackRange(i);
        if (Math.abs(f) >= Math.abs(f2)) {
            f = f2;
        }
        double d = springBackRange;
        return (float) (d - (Math.pow(d, 0.6666666666666666d) * Math.pow(springBackRange - (f * 3.0f), 0.3333333333333333d)));
    }

    @Override // androidx.core.view.NestedScrollingParent3
    public void onNestedScroll(View view, int i, int i2, int i3, int i4, int i5, int[] iArr) {
        boolean z = this.mNestedScrollAxes == 2;
        int i6 = z ? i2 : i;
        int i7 = z ? iArr[1] : iArr[0];
        dispatchNestedScroll(i, i2, i3, i4, this.mParentOffsetInWindow, i5, iArr);
        if (this.mSpringBackEnable) {
            int i8 = (z ? iArr[1] : iArr[0]) - i7;
            int i9 = z ? i4 - i8 : i3 - i8;
            int i10 = i9 != 0 ? i9 : 0;
            int i11 = z ? 2 : 1;
            if (i10 < 0 && isTargetScrollToTop(i11) && supportTopSpringBackMode()) {
                if (i5 != 0) {
                    float fObtainMaxSpringBackDistance = obtainMaxSpringBackDistance(i11);
                    if (this.mVelocityY != 0.0f || this.mVelocityX != 0.0f) {
                        this.mScrollByFling = true;
                        if (i6 != 0 && (-i10) <= fObtainMaxSpringBackDistance) {
                            this.mSpringScroller.setFirstStep(i10);
                        }
                        dispatchScrollState(2);
                        return;
                    }
                    if (this.mTotalScrollTopUnconsumed != 0.0f) {
                        return;
                    }
                    float f = fObtainMaxSpringBackDistance - this.mTotalFlingUnconsumed;
                    if (this.consumeNestFlingCounter < 4) {
                        if (f <= Math.abs(i10)) {
                            this.mTotalFlingUnconsumed += f;
                            iArr[1] = (int) (iArr[1] + f);
                        } else {
                            this.mTotalFlingUnconsumed += Math.abs(i10);
                            iArr[1] = iArr[1] + i9;
                        }
                        dispatchScrollState(2);
                        moveTarget(obtainSpringBackDistance(this.mTotalFlingUnconsumed, i11), i11);
                        this.consumeNestFlingCounter++;
                        return;
                    }
                    return;
                }
                if (this.mSpringScroller.isFinished()) {
                    this.mTotalScrollTopUnconsumed += Math.abs(i10);
                    dispatchScrollState(1);
                    moveTarget(obtainSpringBackDistance(this.mTotalScrollTopUnconsumed, i11), i11);
                    iArr[1] = iArr[1] + i9;
                    return;
                }
                return;
            }
            if (i10 > 0 && isTargetScrollToBottom(i11) && supportBottomSpringBackMode()) {
                if (i5 != 0) {
                    float fObtainMaxSpringBackDistance2 = obtainMaxSpringBackDistance(i11);
                    if (this.mVelocityY != 0.0f || this.mVelocityX != 0.0f) {
                        this.mScrollByFling = true;
                        if (i6 != 0 && i10 <= fObtainMaxSpringBackDistance2) {
                            this.mSpringScroller.setFirstStep(i10);
                        }
                        dispatchScrollState(2);
                        return;
                    }
                    if (this.mTotalScrollBottomUnconsumed != 0.0f) {
                        return;
                    }
                    float f2 = fObtainMaxSpringBackDistance2 - this.mTotalFlingUnconsumed;
                    if (this.consumeNestFlingCounter < 4) {
                        if (f2 <= Math.abs(i10)) {
                            this.mTotalFlingUnconsumed += f2;
                            iArr[1] = (int) (iArr[1] + f2);
                        } else {
                            this.mTotalFlingUnconsumed += Math.abs(i10);
                            iArr[1] = iArr[1] + i9;
                        }
                        dispatchScrollState(2);
                        moveTarget(-obtainSpringBackDistance(this.mTotalFlingUnconsumed, i11), i11);
                        this.consumeNestFlingCounter++;
                        return;
                    }
                    return;
                }
                if (this.mSpringScroller.isFinished()) {
                    this.mTotalScrollBottomUnconsumed += Math.abs(i10);
                    dispatchScrollState(1);
                    moveTarget(-obtainSpringBackDistance(this.mTotalScrollBottomUnconsumed, i11), i11);
                    iArr[1] = iArr[1] + i9;
                }
            }
        }
    }

    @Override // androidx.core.view.NestedScrollingParent2
    public void onNestedScroll(View view, int i, int i2, int i3, int i4, int i5) {
        onNestedScroll(view, i, i2, i3, i4, i5, this.mNestedScrollingV2ConsumedCompat);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
    public void onNestedScroll(View view, int i, int i2, int i3, int i4) {
        onNestedScroll(view, i, i2, i3, i4, 0, this.mNestedScrollingV2ConsumedCompat);
    }

    @Override // androidx.core.view.NestedScrollingParent2
    public boolean onStartNestedScroll(View view, View view2, int i, int i2) {
        this.mNestedScrollAxes = i;
        boolean z = i == 2;
        if (((z ? 2 : 1) & this.mOriginScrollOrientation) == 0) {
            return false;
        }
        if (this.mSpringBackEnable) {
            if (!onStartNestedScroll(view, view, i)) {
                return false;
            }
            float scrollY = z ? getScrollY() : getScrollX();
            if (i2 != 0 && scrollY != 0.0f && (this.mTarget instanceof NestedScrollView)) {
                return false;
            }
        }
        this.mNestedScrollingChildHelper.startNestedScroll(i, i2);
        return true;
    }

    @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
    public boolean onStartNestedScroll(View view, View view2, int i) {
        return isEnabled();
    }

    @Override // androidx.core.view.NestedScrollingParent2
    public void onNestedScrollAccepted(View view, View view2, int i, int i2) {
        if (this.mSpringBackEnable) {
            boolean z = this.mNestedScrollAxes == 2;
            int i3 = z ? 2 : 1;
            float scrollY = z ? getScrollY() : getScrollX();
            if (i2 != 0) {
                if (scrollY == 0.0f) {
                    this.mTotalFlingUnconsumed = 0.0f;
                } else {
                    this.mTotalFlingUnconsumed = obtainTouchDistance(Math.abs(scrollY), Math.abs(obtainMaxSpringBackDistance(i3)), i3);
                }
                this.mNestedFlingInProgress = true;
                this.consumeNestFlingCounter = 0;
            } else {
                if (scrollY == 0.0f) {
                    this.mTotalScrollTopUnconsumed = 0.0f;
                    this.mTotalScrollBottomUnconsumed = 0.0f;
                } else if (scrollY < 0.0f) {
                    this.mTotalScrollTopUnconsumed = obtainTouchDistance(Math.abs(scrollY), Math.abs(obtainMaxSpringBackDistance(i3)), i3);
                    this.mTotalScrollBottomUnconsumed = 0.0f;
                } else {
                    this.mTotalScrollTopUnconsumed = 0.0f;
                    this.mTotalScrollBottomUnconsumed = obtainTouchDistance(Math.abs(scrollY), Math.abs(obtainMaxSpringBackDistance(i3)), i3);
                }
                this.mNestedScrollInProgress = true;
            }
            this.mVelocityY = 0.0f;
            this.mVelocityX = 0.0f;
            this.mScrollByFling = false;
            this.mSpringScroller.forceStop();
            this.mSpringOverScroller.forceStop();
        }
        onNestedScrollAccepted(view, view2, i);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
    public void onNestedScrollAccepted(View view, View view2, int i) {
        this.mNestedScrollingParentHelper.onNestedScrollAccepted(view, view2, i);
        startNestedScroll(i & 2);
    }

    @Override // androidx.core.view.NestedScrollingParent2
    public void onNestedPreScroll(View view, int i, int i2, int[] iArr, int i3) {
        if (this.mSpringBackEnable) {
            if (this.mNestedScrollAxes == 2) {
                onNestedPreScroll(i2, iArr, i3);
            } else {
                onNestedPreScroll(i, iArr, i3);
            }
        }
        int[] iArr2 = this.mParentScrollConsumed;
        if (dispatchNestedPreScroll(i - iArr[0], i2 - iArr[1], iArr2, null, i3)) {
            iArr[0] = iArr[0] + iArr2[0];
            iArr[1] = iArr[1] + iArr2[1];
        }
    }

    private void onNestedPreScroll(int i, int[] iArr, int i2) {
        boolean z = this.mNestedScrollAxes == 2;
        int i3 = z ? 2 : 1;
        int iAbs = Math.abs(z ? getScrollY() : getScrollX());
        float f = 0.0f;
        if (i2 == 0) {
            if (i > 0) {
                float f2 = this.mTotalScrollTopUnconsumed;
                if (f2 > 0.0f) {
                    float f3 = i;
                    if (f3 > f2) {
                        consumeDelta((int) f2, iArr, i3);
                        this.mTotalScrollTopUnconsumed = 0.0f;
                    } else {
                        this.mTotalScrollTopUnconsumed = f2 - f3;
                        consumeDelta(i, iArr, i3);
                    }
                    dispatchScrollState(1);
                    moveTarget(obtainSpringBackDistance(this.mTotalScrollTopUnconsumed, i3), i3);
                    return;
                }
            }
            if (i < 0) {
                float f4 = this.mTotalScrollBottomUnconsumed;
                if ((-f4) < 0.0f) {
                    float f5 = i;
                    if (f5 < (-f4)) {
                        consumeDelta((int) f4, iArr, i3);
                        this.mTotalScrollBottomUnconsumed = 0.0f;
                    } else {
                        this.mTotalScrollBottomUnconsumed = f4 + f5;
                        consumeDelta(i, iArr, i3);
                    }
                    dispatchScrollState(1);
                    moveTarget(-obtainSpringBackDistance(this.mTotalScrollBottomUnconsumed, i3), i3);
                    return;
                }
                return;
            }
            return;
        }
        float f6 = i3 == 2 ? this.mVelocityY : this.mVelocityX;
        if (i > 0) {
            float f7 = this.mTotalScrollTopUnconsumed;
            if (f7 > 0.0f) {
                if (f6 > 2000.0f) {
                    float fObtainSpringBackDistance = obtainSpringBackDistance(f7, i3);
                    float f8 = i;
                    if (f8 > fObtainSpringBackDistance) {
                        consumeDelta((int) fObtainSpringBackDistance, iArr, i3);
                        this.mTotalScrollTopUnconsumed = 0.0f;
                    } else {
                        consumeDelta(i, iArr, i3);
                        f = fObtainSpringBackDistance - f8;
                        this.mTotalScrollTopUnconsumed = obtainTouchDistance(f, Math.signum(f) * Math.abs(obtainMaxSpringBackDistance(i3)), i3);
                    }
                    moveTarget(f, i3);
                    dispatchScrollState(1);
                    return;
                }
                if (!this.mScrollByFling) {
                    this.mScrollByFling = true;
                    springBack(f6, i3, false);
                }
                if (this.mSpringScroller.computeScrollOffset()) {
                    scrollTo(this.mSpringScroller.getCurrX(), this.mSpringScroller.getCurrY());
                    this.mTotalScrollTopUnconsumed = obtainTouchDistance(iAbs, Math.abs(obtainMaxSpringBackDistance(i3)), i3);
                } else {
                    this.mTotalScrollTopUnconsumed = 0.0f;
                }
                consumeDelta(i, iArr, i3);
                return;
            }
        }
        if (i < 0) {
            float f9 = this.mTotalScrollBottomUnconsumed;
            if ((-f9) < 0.0f) {
                if (f6 < -2000.0f) {
                    float fObtainSpringBackDistance2 = obtainSpringBackDistance(f9, i3);
                    float f10 = i;
                    if (f10 < (-fObtainSpringBackDistance2)) {
                        consumeDelta((int) fObtainSpringBackDistance2, iArr, i3);
                        this.mTotalScrollBottomUnconsumed = 0.0f;
                    } else {
                        consumeDelta(i, iArr, i3);
                        f = fObtainSpringBackDistance2 + f10;
                        this.mTotalScrollBottomUnconsumed = obtainTouchDistance(f, Math.signum(f) * Math.abs(obtainMaxSpringBackDistance(i3)), i3);
                    }
                    dispatchScrollState(1);
                    moveTarget(-f, i3);
                    return;
                }
                if (!this.mScrollByFling) {
                    this.mScrollByFling = true;
                    springBack(f6, i3, false);
                }
                if (this.mSpringScroller.computeScrollOffset()) {
                    scrollTo(this.mSpringScroller.getCurrX(), this.mSpringScroller.getCurrY());
                    this.mTotalScrollBottomUnconsumed = obtainTouchDistance(iAbs, Math.abs(obtainMaxSpringBackDistance(i3)), i3);
                } else {
                    this.mTotalScrollBottomUnconsumed = 0.0f;
                }
                consumeDelta(i, iArr, i3);
                return;
            }
        }
        if (i != 0) {
            if ((this.mTotalScrollBottomUnconsumed == 0.0f || this.mTotalScrollTopUnconsumed == 0.0f) && this.mScrollByFling && getScrollY() == 0) {
                consumeDelta(i, iArr, i3);
            }
        }
    }

    private void consumeDelta(int i, int[] iArr, int i2) {
        if (i2 == 2) {
            iArr[1] = i;
        } else {
            iArr[0] = i;
        }
    }

    @Override // android.view.View, androidx.core.view.NestedScrollingChild
    public void setNestedScrollingEnabled(boolean z) {
        this.mNestedScrollingChildHelper.setNestedScrollingEnabled(z);
    }

    @Override // android.view.View, androidx.core.view.NestedScrollingChild
    public boolean isNestedScrollingEnabled() {
        return this.mNestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    @Override // androidx.core.view.NestedScrollingParent2
    public void onStopNestedScroll(View view, int i) {
        this.mNestedScrollingParentHelper.onStopNestedScroll(view, i);
        stopNestedScroll(i);
        if (this.mSpringBackEnable) {
            boolean z = this.mNestedScrollAxes == 2;
            int i2 = z ? 2 : 1;
            if (this.mNestedScrollInProgress) {
                this.mNestedScrollInProgress = false;
                float scrollY = z ? getScrollY() : getScrollX();
                if (!this.mNestedFlingInProgress && scrollY != 0.0f) {
                    springBack(i2);
                    return;
                } else {
                    if (scrollY != 0.0f) {
                        stopNestedFlingScroll(i2);
                        return;
                    }
                    return;
                }
            }
            if (this.mNestedFlingInProgress) {
                stopNestedFlingScroll(i2);
            }
        }
    }

    private void stopNestedFlingScroll(int i) {
        this.mNestedFlingInProgress = false;
        if (this.mScrollByFling) {
            if (this.mSpringScroller.isFinished()) {
                springBack(i == 2 ? this.mVelocityY : this.mVelocityX, i, false);
            }
            AnimationHelper.postInvalidateOnAnimation(this);
            return;
        }
        springBack(i);
    }

    @Override // android.view.View, androidx.core.view.NestedScrollingChild
    public void stopNestedScroll() {
        this.mNestedScrollingChildHelper.stopNestedScroll();
    }

    @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
    public boolean onNestedFling(View view, float f, float f2, boolean z) {
        return dispatchNestedFling(f, f2, z);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
    public boolean onNestedPreFling(View view, float f, float f2) {
        return dispatchNestedPreFling(f, f2);
    }

    @Override // androidx.core.view.NestedScrollingChild3
    public void dispatchNestedScroll(int i, int i2, int i3, int i4, int[] iArr, int i5, int[] iArr2) {
        this.mNestedScrollingChildHelper.dispatchNestedScroll(i, i2, i3, i4, iArr, i5, iArr2);
    }

    @Override // androidx.core.view.NestedScrollingChild2
    public boolean startNestedScroll(int i, int i2) {
        return this.mNestedScrollingChildHelper.startNestedScroll(i, i2);
    }

    @Override // android.view.View, androidx.core.view.NestedScrollingChild
    public boolean startNestedScroll(int i) {
        return this.mNestedScrollingChildHelper.startNestedScroll(i);
    }

    @Override // androidx.core.view.NestedScrollingChild2
    public void stopNestedScroll(int i) {
        this.mNestedScrollingChildHelper.stopNestedScroll(i);
    }

    @Override // androidx.core.view.NestedScrollingChild2
    public boolean hasNestedScrollingParent(int i) {
        return this.mNestedScrollingChildHelper.hasNestedScrollingParent(i);
    }

    @Override // androidx.core.view.NestedScrollingChild2
    public boolean dispatchNestedScroll(int i, int i2, int i3, int i4, int[] iArr, int i5) {
        return this.mNestedScrollingChildHelper.dispatchNestedScroll(i, i2, i3, i4, iArr, i5);
    }

    @Override // androidx.core.view.NestedScrollingChild2
    public boolean dispatchNestedPreScroll(int i, int i2, int[] iArr, int[] iArr2, int i3) {
        return this.mNestedScrollingChildHelper.dispatchNestedPreScroll(i, i2, iArr, iArr2, i3);
    }

    @Override // android.view.View, androidx.core.view.NestedScrollingChild
    public boolean dispatchNestedPreFling(float f, float f2) {
        return this.mNestedScrollingChildHelper.dispatchNestedPreFling(f, f2);
    }

    @Override // android.view.View, androidx.core.view.NestedScrollingChild
    public boolean dispatchNestedFling(float f, float f2, boolean z) {
        return this.mNestedScrollingChildHelper.dispatchNestedFling(f, f2, z);
    }

    @Override // android.view.View, androidx.core.view.NestedScrollingChild
    public boolean dispatchNestedPreScroll(int i, int i2, int[] iArr, int[] iArr2) {
        return this.mNestedScrollingChildHelper.dispatchNestedPreScroll(i, i2, iArr, iArr2);
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        Point screenSize = EnvStateManager.getScreenSize(getContext());
        this.mScreenWidth = screenSize.x;
        this.mScreenHeight = screenSize.y;
    }

    public void smoothScrollTo(int i, int i2) {
        if (i - getScrollX() == 0 && i2 - getScrollY() == 0) {
            return;
        }
        this.mSpringScroller.forceStop();
        this.mSpringScroller.scrollByFling(getScrollX(), i, getScrollY(), i2, 0.0f, 2, true);
        dispatchScrollState(2);
        AnimationHelper.postInvalidateOnAnimation(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchScrollState(int i) {
        int i2 = this.mScrollState;
        if (i2 != i) {
            this.mScrollState = i;
            Iterator<ViewCompatOnScrollChangeListener> it = this.mOnScrollChangeListeners.iterator();
            while (it.hasNext()) {
                it.next().onStateChanged(i2, i, this.mSpringScroller.isFinished());
            }
        }
    }

    @Override // miuix.core.view.ScrollStateDispatcher
    public void addOnScrollChangeListener(ViewCompatOnScrollChangeListener viewCompatOnScrollChangeListener) {
        this.mOnScrollChangeListeners.add(viewCompatOnScrollChangeListener);
    }

    @Override // miuix.core.view.ScrollStateDispatcher
    public void removeOnScrollChangeListener(ViewCompatOnScrollChangeListener viewCompatOnScrollChangeListener) {
        this.mOnScrollChangeListeners.remove(viewCompatOnScrollChangeListener);
    }

    public void setOnSpringListener(OnSpringListener onSpringListener) {
        this.mOnSpringListener = onSpringListener;
    }

    public boolean hasSpringListener() {
        return this.mOnSpringListener != null;
    }

    @Override // miuix.core.view.NestedCurrentFling
    public boolean onNestedCurrentFling(float f, float f2) {
        this.mVelocityX = f;
        this.mVelocityY = f2;
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float getListViewScrollVelocity(AbsListView absListView) {
        try {
            if (this.mListOverScroller == null) {
                this.mListOverScroller = (OverScroller) ReflectionHelper.getFieldValue(Class.forName("android.widget.AbsListView$FlingRunnable"), ReflectionHelper.getFieldValue(AbsListView.class, absListView, "mFlingRunnable"), "mScroller");
            }
            return this.mListOverScroller.getCurrVelocity();
        } catch (Exception unused) {
            Log.e(TAG, "get listView scroll velocity failed..");
            return 0.0f;
        }
    }

    public void inflateEmptyState() {
        EmptyStateInflationDelegate emptyStateInflationDelegate = this.mDelegate;
        if (emptyStateInflationDelegate != null) {
            emptyStateInflationDelegate.inflate();
        }
    }

    public void deflateEmptyState() {
        EmptyStateInflationDelegate emptyStateInflationDelegate = this.mDelegate;
        if (emptyStateInflationDelegate != null) {
            emptyStateInflationDelegate.deflate();
        }
    }

    public View getEmptyState() {
        EmptyStateInflationDelegate emptyStateInflationDelegate = this.mDelegate;
        if (emptyStateInflationDelegate != null) {
            return emptyStateInflationDelegate.getEmptyState();
        }
        return null;
    }

    public void setOnEmptyStateInflateListener(EmptyStateInflationDelegate.OnInflateListener onInflateListener) {
        EmptyStateInflationDelegate emptyStateInflationDelegate = this.mDelegate;
        if (emptyStateInflationDelegate != null) {
            emptyStateInflationDelegate.setOnInflateListener(onInflateListener);
        }
    }

    public void setOnEmptyStateDeflateListener(EmptyStateInflationDelegate.OnDeflateListener onDeflateListener) {
        EmptyStateInflationDelegate emptyStateInflationDelegate = this.mDelegate;
        if (emptyStateInflationDelegate != null) {
            emptyStateInflationDelegate.setOnDeflateListener(onDeflateListener);
        }
    }

    public boolean isEmptyStateEnabled() {
        EmptyStateInflationDelegate emptyStateInflationDelegate = this.mDelegate;
        if (emptyStateInflationDelegate != null) {
            return emptyStateInflationDelegate.isEnabled();
        }
        return false;
    }

    public boolean isEmptyStateInflated() {
        EmptyStateInflationDelegate emptyStateInflationDelegate = this.mDelegate;
        if (emptyStateInflationDelegate != null) {
            return emptyStateInflationDelegate.isInflated();
        }
        return false;
    }
}
