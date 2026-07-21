package miuix.nestedheader.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import androidx.core.view.NestedScrollingChild3;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParentHelper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miuix.core.view.NestedContentInsetObserver;
import miuix.core.view.NestedCoordinatorObserver;
import miuix.core.view.ScrollStateDispatcher;
import miuix.core.view.ViewCompatOnScrollChangeListener;
import miuix.nestedheader.R;

/* JADX INFO: loaded from: classes3.dex */
public class NestedScrollingLayout extends FrameLayout implements NestedScrollingParent3, NestedScrollingChild3, NestedCoordinatorObserver, NestedContentInsetObserver {
    private static final String TAG = "NestedScrollingLayout";
    protected int mContentInsetBottom;
    protected int mContentInsetTop;
    protected int mCoordinatorHeightTotalGap;
    protected int mCurrentCoordinatorHeightGap;
    protected boolean mEnableOverScrollTo;
    protected boolean mHeaderCloseOnInit;
    private long mHeaderOpenTime;
    private boolean mHeaderViewVisible;
    protected boolean mInSearchMode;
    private boolean mIsFirstSetScrollingRange;
    private boolean mIsHeaderOpen;
    protected boolean mIsOverlayMode;
    protected boolean mIsSelfScrollFirst;
    private boolean mNestedFlingInConsumedProgress;
    private long mNestedFlingStartInConsumedTime;
    private boolean mNestedScrollAcceptedFling;
    private boolean mNestedScrollInConsumedProgress;
    private final NestedScrollingChildHelper mNestedScrollingChildHelper;
    private final NestedScrollingParentHelper mNestedScrollingParentHelper;
    private final int[] mNestedScrollingV2ConsumedCompat;
    private List<OnNestedChangedListener> mOnNestedChangedListeners;
    private ViewCompatOnScrollChangeListener mOnScrollChangeListener;
    protected float mOverScrollToRatio;
    protected int mOverScrollingTo;
    protected final int[] mParentOffsetInWindow;
    private final int[] mParentScrollConsumed;
    private int mPreOverScrollingTo;
    private int mScrollType;
    protected View mScrollableView;
    private int mScrollableViewId;
    private int mScrollingFrom;
    private int mScrollingProgress;
    private int mScrollingTo;
    private boolean mStickyViewVisible;
    private boolean mTriggerViewVisible;
    protected Boolean mUserSetOverlayMode;

    public interface OnNestedChangedListener {
        void onStartNestedScroll(int i);

        void onStopNestedScroll(int i);

        void onStopNestedScrollAccepted(int i);
    }

    protected void onScrollingProgressUpdated(int i) {
    }

    static /* synthetic */ int access$020(NestedScrollingLayout nestedScrollingLayout, int i) {
        int i2 = nestedScrollingLayout.mPreOverScrollingTo - i;
        nestedScrollingLayout.mPreOverScrollingTo = i2;
        return i2;
    }

    public NestedScrollingLayout(Context context) {
        this(context, null);
    }

    public NestedScrollingLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public NestedScrollingLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mNestedScrollingV2ConsumedCompat = new int[2];
        this.mParentOffsetInWindow = new int[2];
        this.mUserSetOverlayMode = null;
        this.mParentScrollConsumed = new int[2];
        this.mPreOverScrollingTo = 0;
        this.mOverScrollingTo = 0;
        this.mCurrentCoordinatorHeightGap = 0;
        this.mCoordinatorHeightTotalGap = 0;
        this.mIsFirstSetScrollingRange = true;
        this.mHeaderOpenTime = 0L;
        this.mNestedFlingStartInConsumedTime = 0L;
        this.mIsHeaderOpen = false;
        this.mHeaderViewVisible = false;
        this.mStickyViewVisible = false;
        this.mTriggerViewVisible = false;
        this.mOnScrollChangeListener = null;
        this.mOnNestedChangedListeners = new ArrayList();
        this.mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        this.mNestedScrollingChildHelper = miuix.core.view.NestedScrollingChildHelper.obtain(this);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.NestedScrollingLayout);
        this.mScrollableViewId = typedArrayObtainStyledAttributes.getResourceId(R.styleable.NestedScrollingLayout_scrollableView, android.R.id.list);
        this.mIsSelfScrollFirst = typedArrayObtainStyledAttributes.getBoolean(R.styleable.NestedScrollingLayout_selfScrollFirst, true);
        this.mHeaderCloseOnInit = typedArrayObtainStyledAttributes.getBoolean(R.styleable.NestedScrollingLayout_headerClose, false);
        this.mEnableOverScrollTo = typedArrayObtainStyledAttributes.getBoolean(R.styleable.NestedScrollingLayout_overScrollTo, false);
        this.mOverScrollToRatio = typedArrayObtainStyledAttributes.getFloat(R.styleable.NestedScrollingLayout_overScrollToRatio, 0.5f);
        this.mScrollType = typedArrayObtainStyledAttributes.getInt(R.styleable.NestedScrollingLayout_scrollType, 0);
        typedArrayObtainStyledAttributes.recycle();
        setNestedScrollingEnabled(true);
    }

    public void setScrollType(int i) {
        this.mScrollType = i;
    }

    public int getScrollType() {
        return this.mScrollType;
    }

    public void setSelfScrollFirst(boolean z) {
        this.mIsSelfScrollFirst = z;
    }

    public void setHeaderCloseOnInit(boolean z) {
        this.mHeaderCloseOnInit = z;
    }

    public void setEnableOverScrollTo(boolean z) {
        if (this.mScrollableView instanceof ScrollStateDispatcher) {
            this.mEnableOverScrollTo = z;
        }
    }

    public void setOverScrollToRatio(float f) {
        this.mOverScrollToRatio = f;
    }

    public int getNestedScrollableValue() {
        return getScrollingFrom();
    }

    public void updateCoordinatorHeightGapInfo(int i, int i2) {
        this.mCurrentCoordinatorHeightGap = i;
        this.mCoordinatorHeightTotalGap = i2;
    }

    @Override // miuix.core.view.NestedContentInsetObserver
    public void onContentInsetChanged(Rect rect) {
        if (this.mContentInsetTop == rect.top && this.mContentInsetBottom == rect.bottom) {
            return;
        }
        this.mContentInsetTop = Math.max(0, rect.top);
        this.mContentInsetBottom = Math.max(0, rect.bottom);
        requestLayout();
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        View viewFindViewById = findViewById(this.mScrollableViewId);
        this.mScrollableView = viewFindViewById;
        if (viewFindViewById == null) {
            throw new IllegalArgumentException("The scrollableView attribute is required and must refer to a valid child.");
        }
        if (viewFindViewById instanceof ScrollStateDispatcher) {
            ViewCompatOnScrollChangeListener viewCompatOnScrollChangeListener = new ViewCompatOnScrollChangeListener() { // from class: miuix.nestedheader.widget.NestedScrollingLayout.1
                @Override // miuix.core.view.ViewCompatOnScrollChangeListener
                public void onStateChanged(int i, int i2, boolean z) {
                }

                @Override // miuix.core.view.ViewCompatOnScrollChangeListener
                public void onScrollChange(View view, int i, int i2, int i3, int i4) {
                    if (NestedScrollingLayout.this.mEnableOverScrollTo) {
                        NestedScrollingLayout.access$020(NestedScrollingLayout.this, i2 - i4);
                        if (NestedScrollingLayout.this.mScrollingProgress < NestedScrollingLayout.this.mScrollingTo || NestedScrollingLayout.this.mPreOverScrollingTo < 0) {
                            return;
                        }
                        NestedScrollingLayout nestedScrollingLayout = NestedScrollingLayout.this;
                        nestedScrollingLayout.mOverScrollingTo = nestedScrollingLayout.obtainSpringBackDistance(nestedScrollingLayout.mPreOverScrollingTo);
                        NestedScrollingLayout.this.dispatchScrollingProgressUpdated();
                    }
                }
            };
            this.mOnScrollChangeListener = viewCompatOnScrollChangeListener;
            ((ScrollStateDispatcher) this.mScrollableView).addOnScrollChangeListener(viewCompatOnScrollChangeListener);
        } else {
            this.mEnableOverScrollTo = false;
        }
        this.mScrollableView.setNestedScrollingEnabled(true);
    }

    protected int getScrollableViewMaxHeightWithoutOverlay() {
        if (getMeasuredHeight() < this.mContentInsetTop) {
            return getMeasuredHeight();
        }
        return getMeasuredHeight() - this.mContentInsetTop;
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.mScrollableView.getLayoutParams().height == -1) {
            if (!this.mIsOverlayMode) {
                this.mScrollableView.measure(View.MeasureSpec.makeMeasureSpec(this.mScrollableView.getMeasuredWidth(), BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(getScrollableViewMaxHeightWithoutOverlay(), BasicMeasure.EXACTLY));
                Log.d(TAG, "onMeasure in NoOverlayMode mScrollableView " + this.mScrollableView.getMeasuredHeight() + " viewHeight " + getMeasuredHeight());
                return;
            }
            if (getClipToPadding()) {
                return;
            }
            this.mScrollableView.measure(View.MeasureSpec.makeMeasureSpec(this.mScrollableView.getMeasuredWidth(), BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), BasicMeasure.EXACTLY));
        }
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        onUpdateScrollingRangeOnLayout(z, i, i2, i3, i4);
    }

    public void onUpdateScrollingRangeOnLayout(boolean z, int i, int i2, int i3, int i4) {
        dispatchScrollingProgressUpdated();
    }

    public void setScrollingRange(int i, int i2, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, boolean z7) {
        if (i > i2) {
            Log.w(TAG, "wrong scrolling range: [%d, %d], making from=to");
            i = i2;
        }
        this.mScrollingFrom = i;
        this.mScrollingTo = i2;
        this.mHeaderViewVisible = z;
        this.mTriggerViewVisible = z2;
        this.mStickyViewVisible = z3;
        if (this.mScrollingProgress < i) {
            this.mScrollingProgress = i;
        }
        if (this.mScrollingProgress > i2 && i2 >= 0) {
            this.mScrollingProgress = i2;
        }
        boolean z8 = z4 && this.mIsFirstSetScrollingRange;
        if ((z8 || z5 || z7) && z) {
            if (this.mIsFirstSetScrollingRange && this.mHeaderCloseOnInit) {
                this.mScrollingProgress = getHeaderCloseProgress();
            } else {
                this.mScrollingProgress = 0;
            }
            this.mIsFirstSetScrollingRange = false;
        } else if (z8 || z5) {
            this.mScrollingProgress = 0;
            this.mIsFirstSetScrollingRange = false;
        }
        dispatchScrollingProgressUpdated();
    }

    public void updateScrollingProgress(int i) {
        this.mScrollingProgress = i;
    }

    public void updateHeaderOpen(boolean z) {
        if (!this.mIsHeaderOpen && z) {
            this.mHeaderOpenTime = SystemClock.elapsedRealtime();
        }
        this.mIsHeaderOpen = z;
    }

    public int getScrollingProgress() {
        return this.mScrollingProgress;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchScrollingProgressUpdated() {
        onScrollingProgressUpdated(this.mScrollingProgress);
    }

    public int getScrollingFrom() {
        return this.mScrollingFrom;
    }

    public int getScrollingTo() {
        return this.mScrollingTo;
    }

    protected int getHeaderProgressFrom() {
        if (this.mIsOverlayMode) {
            return this.mScrollingFrom + this.mContentInsetTop;
        }
        return this.mScrollingFrom;
    }

    protected int getHeaderProgressTo() {
        if (this.mIsOverlayMode) {
            return this.mScrollingFrom + this.mContentInsetTop;
        }
        return this.mScrollingFrom;
    }

    protected int getHeaderCloseProgress() {
        if (this.mIsOverlayMode) {
            return this.mScrollingFrom + this.mContentInsetTop;
        }
        return this.mScrollingFrom;
    }

    @Override // android.view.View, androidx.core.view.NestedScrollingChild
    public void setNestedScrollingEnabled(boolean z) {
        this.mNestedScrollingChildHelper.setNestedScrollingEnabled(z);
    }

    @Override // android.view.View, androidx.core.view.NestedScrollingChild
    public boolean isNestedScrollingEnabled() {
        return this.mNestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    @Override // android.view.View, androidx.core.view.NestedScrollingChild
    public boolean startNestedScroll(int i) {
        return this.mNestedScrollingChildHelper.startNestedScroll(i);
    }

    @Override // androidx.core.view.NestedScrollingChild2
    public boolean startNestedScroll(int i, int i2) {
        return this.mNestedScrollingChildHelper.startNestedScroll(i, i2);
    }

    @Override // androidx.core.view.NestedScrollingChild2
    public boolean dispatchNestedPreScroll(int i, int i2, int[] iArr, int[] iArr2, int i3) {
        return this.mNestedScrollingChildHelper.dispatchNestedPreScroll(i, i2, iArr, iArr2, i3);
    }

    @Override // androidx.core.view.NestedScrollingChild2
    public boolean dispatchNestedScroll(int i, int i2, int i3, int i4, int[] iArr, int i5) {
        return this.mNestedScrollingChildHelper.dispatchNestedScroll(i, i2, i3, i4, iArr, i5);
    }

    @Override // androidx.core.view.NestedScrollingChild3
    public void dispatchNestedScroll(int i, int i2, int i3, int i4, int[] iArr, int i5, int[] iArr2) {
        this.mNestedScrollingChildHelper.dispatchNestedScroll(i, i2, i3, i4, iArr, i5, iArr2);
    }

    @Override // androidx.core.view.NestedScrollingChild2
    public void stopNestedScroll(int i) {
        this.mNestedScrollingChildHelper.stopNestedScroll(i);
    }

    @Override // androidx.core.view.NestedScrollingChild2
    public boolean hasNestedScrollingParent(int i) {
        return this.mNestedScrollingChildHelper.hasNestedScrollingParent(i);
    }

    @Override // android.view.View, androidx.core.view.NestedScrollingChild
    public void stopNestedScroll() {
        this.mNestedScrollingChildHelper.stopNestedScroll();
    }

    @Override // androidx.core.view.NestedScrollingParent2
    public boolean onStartNestedScroll(View view, View view2, int i, int i2) {
        sendStartNestedScroll(i2);
        return this.mNestedScrollingChildHelper.startNestedScroll(i, i2) || onStartNestedScroll(view, view, i);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
    public boolean onStartNestedScroll(View view, View view2, int i) {
        boolean z = (i & 2) != 0;
        if (this.mNestedScrollingChildHelper.startNestedScroll(i)) {
            return true;
        }
        return isEnabled() && z;
    }

    @Override // androidx.core.view.NestedScrollingParent2
    public void onNestedScrollAccepted(View view, View view2, int i, int i2) {
        onNestedScrollAccepted(view, view2, i);
        if (i2 != 0) {
            this.mNestedScrollAcceptedFling = true;
        } else {
            this.mNestedScrollAcceptedFling = false;
        }
    }

    @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
    public void onNestedScrollAccepted(View view, View view2, int i) {
        this.mNestedScrollingParentHelper.onNestedScrollAccepted(view, view2, i);
        startNestedScroll(i & 2);
    }

    @Override // androidx.core.view.NestedScrollingParent2
    public void onNestedPreScroll(View view, int i, int i2, int[] iArr, int i3) {
        if (i3 != 0) {
            if (!this.mNestedFlingInConsumedProgress) {
                this.mNestedFlingStartInConsumedTime = SystemClock.elapsedRealtime();
            }
            this.mNestedFlingInConsumedProgress = true;
        } else {
            this.mNestedScrollInConsumedProgress = true;
        }
        nestedPreScrollTrigger(i, i2, iArr);
        if (this.mIsSelfScrollFirst) {
            nestedPreScrollHeader(i, i2, iArr);
        }
        nestedPreScrollParent(i, i2, iArr, this.mParentScrollConsumed, i3);
        nestedPreScrollSelfAfterParentConsumed(i, i2, iArr);
    }

    private void nestedPreScrollHeader(int i, int i2, int[] iArr) {
        if (this.mScrollingProgress >= getHeaderCloseProgress() && i2 > iArr[1]) {
            int iMax = Math.max(getHeaderCloseProgress(), this.mScrollingProgress - i2);
            int i3 = this.mScrollingProgress - iMax;
            this.mScrollingProgress = iMax;
            dispatchScrollingProgressUpdated();
            iArr[1] = iArr[1] + i3;
        }
    }

    private void nestedPreScrollTrigger(int i, int i2, int[] iArr) {
        if (i2 > iArr[1]) {
            int iMax = Math.max(0, Math.min(this.mScrollingTo, this.mScrollingProgress - i2));
            int i3 = this.mScrollingProgress;
            int i4 = i3 - iMax;
            if (i3 == iMax || i3 < 0) {
                return;
            }
            this.mScrollingProgress = iMax;
            dispatchScrollingProgressUpdated();
            iArr[1] = iArr[1] + i4;
        }
    }

    private void nestedPreScrollSelfAfterParentConsumed(int i, int i2, int[] iArr) {
        if (i2 > iArr[1]) {
            int iMax = Math.max(getScrollingFrom(), Math.min(this.mScrollingTo, this.mScrollingProgress - i2));
            int i3 = this.mScrollingProgress - iMax;
            this.mScrollingProgress = iMax;
            dispatchScrollingProgressUpdated();
            iArr[1] = iArr[1] + i3;
        }
    }

    private void nestedPreScrollParent(int i, int i2, int[] iArr, int[] iArr2, int i3) {
        if (dispatchNestedPreScroll(i - iArr[0], i2 - iArr[1], iArr2, null, i3)) {
            iArr[0] = iArr[0] + iArr2[0];
            iArr[1] = iArr[1] + iArr2[1];
        }
    }

    @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
    public void onNestedPreScroll(View view, int i, int i2, int[] iArr) {
        onNestedPreScroll(view, i, i2, iArr, 0);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
    public void onNestedScroll(View view, int i, int i2, int i3, int i4) {
        onNestedScroll(view, i, i2, i3, i4, 0);
    }

    @Override // androidx.core.view.NestedScrollingParent2
    public void onNestedScroll(View view, int i, int i2, int i3, int i4, int i5) {
        onNestedScroll(view, i, i2, i3, i4, 0, this.mNestedScrollingV2ConsumedCompat);
    }

    @Override // androidx.core.view.NestedScrollingParent3
    public void onNestedScroll(View view, int i, int i2, int i3, int i4, int i5, int[] iArr) {
        if (!this.mIsSelfScrollFirst && !this.mInSearchMode) {
            nestedScrollHeader(i3, i4, iArr, i5);
        }
        nestedScrollSticky(i3, i4, iArr, i5);
        int i6 = iArr[0];
        int i7 = iArr[1];
        dispatchNestedScroll(i6, i7, i3 - i6, i4 - i7, this.mParentOffsetInWindow, i5, iArr);
        nestedScrollAfterParentConsumed(i3, i4, i3, i4 - iArr[1], iArr, i5);
    }

    private void nestedScrollHeader(int i, int i2, int[] iArr, int i3) {
        if (i2 >= 0 || this.mScrollingProgress >= getHeaderProgressTo()) {
            return;
        }
        int iMax = Math.max(this.mScrollingFrom, Math.min(getHeaderProgressTo(), this.mScrollingProgress - i2));
        int i4 = this.mScrollingProgress - iMax;
        this.mScrollingProgress = iMax;
        dispatchScrollingProgressUpdated();
        iArr[1] = iArr[1] + i4;
    }

    private void nestedScrollSticky(int i, int i2, int[] iArr, int i3) {
        if (i2 >= 0 || this.mScrollingProgress >= getStickyScrollToOnNested() || !this.mIsOverlayMode) {
            return;
        }
        int iMax = Math.max(this.mScrollingFrom, Math.min(getStickyScrollToOnNested(), this.mScrollingProgress - i2));
        int i4 = this.mScrollingProgress - iMax;
        this.mScrollingProgress = iMax;
        dispatchScrollingProgressUpdated();
        iArr[1] = iArr[1] + i4;
    }

    protected int getStickyScrollToOnNested() {
        return this.mScrollingFrom + this.mContentInsetTop;
    }

    private void nestedScrollAfterParentConsumed(int i, int i2, int i3, int i4, int[] iArr, int i5) {
        int i6;
        boolean z;
        int i7;
        if (i2 >= 0 || i4 == 0) {
            return;
        }
        int i8 = this.mScrollingProgress;
        int i9 = i8 - i4;
        boolean z2 = i5 == 0;
        int i10 = this.mScrollingFrom;
        boolean z3 = i9 > i10;
        boolean z4 = this.mTriggerViewVisible;
        boolean z5 = z4 && !this.mHeaderViewVisible && !z2 && z3 && i8 == i10;
        boolean z6 = z4 && !this.mHeaderViewVisible && !z2 && i8 >= (i7 = this.mScrollingTo) && i9 >= i7;
        boolean z7 = z4 && !z2 && this.mHeaderViewVisible && ((!(z = this.mIsHeaderOpen) && i9 < 0) || (z && this.mHeaderOpenTime <= this.mNestedFlingStartInConsumedTime));
        if (z2 || !z4 || z6 || z7) {
            i6 = this.mScrollingTo;
        } else {
            i6 = z5 ? i10 : 0;
        }
        if (this.mInSearchMode) {
            i6 = this.mScrollingTo;
        }
        int iMax = Math.max(i10, Math.min(i6, i9));
        int i11 = this.mScrollingProgress - iMax;
        this.mScrollingProgress = iMax;
        dispatchScrollingProgressUpdated();
        iArr[0] = iArr[0];
        iArr[1] = iArr[1] + i11;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int obtainSpringBackDistance(int i) {
        int measuredHeight = getMeasuredHeight();
        return (int) (obtainDampingDistance(Math.min((Math.abs(i) * 1.0f) / measuredHeight, 1.0f), measuredHeight) * this.mOverScrollToRatio);
    }

    private float obtainDampingDistance(float f, int i) {
        double dMin = Math.min(f, 1.0f);
        return ((float) (((Math.pow(dMin, 3.0d) / 3.0d) - Math.pow(dMin, 2.0d)) + dMin)) * i;
    }

    @Override // androidx.core.view.NestedScrollingParent2
    public void onStopNestedScroll(View view, int i) {
        this.mNestedScrollingParentHelper.onStopNestedScroll(view, i);
        sendStopNestedScroll(i);
        stopNestedScroll(i);
        if (this.mNestedScrollInConsumedProgress) {
            this.mNestedScrollInConsumedProgress = false;
            if (this.mNestedFlingInConsumedProgress || this.mNestedScrollAcceptedFling) {
                return;
            }
        } else if (this.mNestedFlingInConsumedProgress) {
            this.mNestedFlingInConsumedProgress = false;
        }
        notifyStopNestedScrollAccepted(i);
    }

    public boolean getAcceptedNestedFlingInConsumedProgress() {
        return this.mNestedFlingInConsumedProgress;
    }

    public void addOnScrollListener(OnNestedChangedListener onNestedChangedListener) {
        this.mOnNestedChangedListeners.add(onNestedChangedListener);
    }

    public void removeOnScrollListener(OnNestedChangedListener onNestedChangedListener) {
        this.mOnNestedChangedListeners.remove(onNestedChangedListener);
    }

    private void sendStartNestedScroll(int i) {
        Iterator<OnNestedChangedListener> it = this.mOnNestedChangedListeners.iterator();
        while (it.hasNext()) {
            it.next().onStartNestedScroll(i);
        }
    }

    private void notifyStopNestedScrollAccepted(int i) {
        Iterator<OnNestedChangedListener> it = this.mOnNestedChangedListeners.iterator();
        while (it.hasNext()) {
            it.next().onStopNestedScrollAccepted(i);
        }
    }

    private void sendStopNestedScroll(int i) {
        Iterator<OnNestedChangedListener> it = this.mOnNestedChangedListeners.iterator();
        while (it.hasNext()) {
            it.next().onStopNestedScroll(i);
        }
    }
}
