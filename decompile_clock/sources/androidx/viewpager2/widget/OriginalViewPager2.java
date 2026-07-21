package androidx.viewpager2.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.view.accessibility.AccessibilityViewCommand;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import miuix.androidbasewidget.adapter.StatefulAdapter;
import miuix.spring.view.SpringStateListener;
import miuix.viewpager2.R;

/* JADX INFO: loaded from: classes.dex */
public class OriginalViewPager2 extends ViewGroup {
    public static final int OFFSCREEN_PAGE_LIMIT_DEFAULT = -1;
    public static final int ORIENTATION_HORIZONTAL = 0;
    public static final int ORIENTATION_VERTICAL = 1;
    public static final int SCROLL_STATE_DRAGGING = 1;
    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_SETTLING = 2;
    private static final String TAG = "OriginalViewPager2";
    static boolean sFeatureEnhancedA11yEnabled = true;
    AccessibilityProvider mAccessibilityProvider;
    int mCurrentItem;
    private RecyclerView.AdapterDataObserver mCurrentItemDataSetChangeObserver;
    boolean mCurrentItemDirty;
    private OriginalCompositeOnPageChangeCallback mExternalPageChangeCallbacks;
    private OriginalFakeDrag mFakeDragger;
    private LinearLayoutManager mLayoutManager;
    private int mOffscreenPageLimit;
    private OriginalCompositeOnPageChangeCallback mPageChangeEventDispatcher;
    private OriginalPageTransformerAdapter mPageTransformerAdapter;
    private PagerSnapHelper mPagerSnapHelper;
    private Parcelable mPendingAdapterState;
    private int mPendingCurrentItem;
    miuix.recyclerview.widget.RecyclerView mRecyclerView;
    private RecyclerView.ItemAnimator mSavedItemAnimator;
    private boolean mSavedItemAnimatorPresent;
    OriginalScrollEventAdapter mScrollEventAdapter;
    private final Rect mTmpChildRect;
    private final Rect mTmpContainerRect;
    private boolean mUserInputEnabled;
    private int savedLastWidth;

    @Retention(RetentionPolicy.SOURCE)
    public @interface OffscreenPageLimit {
    }

    public static abstract class OnPageChangeCallback {
        public void onPageScrollStateChanged(int i) {
        }

        public void onPageScrolled(int i, float f, int i2) {
        }

        public void onPageSelected(int i) {
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface Orientation {
    }

    public interface PageTransformer {
        void transformPage(View view, float f);
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface ScrollState {
    }

    public OriginalViewPager2(Context context) {
        super(context);
        this.mTmpContainerRect = new Rect();
        this.mTmpChildRect = new Rect();
        this.mExternalPageChangeCallbacks = new OriginalCompositeOnPageChangeCallback(3);
        this.mCurrentItemDirty = false;
        this.mCurrentItemDataSetChangeObserver = new DataSetChangeObserver() { // from class: androidx.viewpager2.widget.OriginalViewPager2.1
            @Override // androidx.viewpager2.widget.OriginalViewPager2.DataSetChangeObserver, androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onChanged() {
                OriginalViewPager2.this.mCurrentItemDirty = true;
                OriginalViewPager2.this.mScrollEventAdapter.notifyDataSetChangeHappened();
            }
        };
        this.mPendingCurrentItem = -1;
        this.mSavedItemAnimator = null;
        this.mSavedItemAnimatorPresent = false;
        this.mUserInputEnabled = true;
        this.mOffscreenPageLimit = -1;
        initialize(context, null);
    }

    public OriginalViewPager2(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mTmpContainerRect = new Rect();
        this.mTmpChildRect = new Rect();
        this.mExternalPageChangeCallbacks = new OriginalCompositeOnPageChangeCallback(3);
        this.mCurrentItemDirty = false;
        this.mCurrentItemDataSetChangeObserver = new DataSetChangeObserver() { // from class: androidx.viewpager2.widget.OriginalViewPager2.1
            @Override // androidx.viewpager2.widget.OriginalViewPager2.DataSetChangeObserver, androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onChanged() {
                OriginalViewPager2.this.mCurrentItemDirty = true;
                OriginalViewPager2.this.mScrollEventAdapter.notifyDataSetChangeHappened();
            }
        };
        this.mPendingCurrentItem = -1;
        this.mSavedItemAnimator = null;
        this.mSavedItemAnimatorPresent = false;
        this.mUserInputEnabled = true;
        this.mOffscreenPageLimit = -1;
        initialize(context, attributeSet);
    }

    public OriginalViewPager2(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mTmpContainerRect = new Rect();
        this.mTmpChildRect = new Rect();
        this.mExternalPageChangeCallbacks = new OriginalCompositeOnPageChangeCallback(3);
        this.mCurrentItemDirty = false;
        this.mCurrentItemDataSetChangeObserver = new DataSetChangeObserver() { // from class: androidx.viewpager2.widget.OriginalViewPager2.1
            @Override // androidx.viewpager2.widget.OriginalViewPager2.DataSetChangeObserver, androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onChanged() {
                OriginalViewPager2.this.mCurrentItemDirty = true;
                OriginalViewPager2.this.mScrollEventAdapter.notifyDataSetChangeHappened();
            }
        };
        this.mPendingCurrentItem = -1;
        this.mSavedItemAnimator = null;
        this.mSavedItemAnimatorPresent = false;
        this.mUserInputEnabled = true;
        this.mOffscreenPageLimit = -1;
        initialize(context, attributeSet);
    }

    public OriginalViewPager2(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mTmpContainerRect = new Rect();
        this.mTmpChildRect = new Rect();
        this.mExternalPageChangeCallbacks = new OriginalCompositeOnPageChangeCallback(3);
        this.mCurrentItemDirty = false;
        this.mCurrentItemDataSetChangeObserver = new DataSetChangeObserver() { // from class: androidx.viewpager2.widget.OriginalViewPager2.1
            @Override // androidx.viewpager2.widget.OriginalViewPager2.DataSetChangeObserver, androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onChanged() {
                OriginalViewPager2.this.mCurrentItemDirty = true;
                OriginalViewPager2.this.mScrollEventAdapter.notifyDataSetChangeHappened();
            }
        };
        this.mPendingCurrentItem = -1;
        this.mSavedItemAnimator = null;
        this.mSavedItemAnimatorPresent = false;
        this.mUserInputEnabled = true;
        this.mOffscreenPageLimit = -1;
        initialize(context, attributeSet);
    }

    public float getSpringX() {
        return this.mRecyclerView.getSpringX();
    }

    public float getSpringY() {
        return this.mRecyclerView.getSpringY();
    }

    public void addSpringStateListener(SpringStateListener springStateListener) {
        this.mRecyclerView.addSpringStateListener(springStateListener);
    }

    public void removeSpringStateListener(SpringStateListener springStateListener) {
        this.mRecyclerView.removeSpringStateListener(springStateListener);
    }

    private void initialize(Context context, AttributeSet attributeSet) {
        AccessibilityProvider basicAccessibilityProvider;
        if (sFeatureEnhancedA11yEnabled) {
            basicAccessibilityProvider = new PageAwareAccessibilityProvider();
        } else {
            basicAccessibilityProvider = new BasicAccessibilityProvider();
        }
        this.mAccessibilityProvider = basicAccessibilityProvider;
        RecyclerViewImpl recyclerViewImpl = new RecyclerViewImpl(context);
        this.mRecyclerView = recyclerViewImpl;
        recyclerViewImpl.setId(ViewCompat.generateViewId());
        this.mRecyclerView.setDescendantFocusability(131072);
        LinearLayoutManagerImpl linearLayoutManagerImpl = new LinearLayoutManagerImpl(context);
        this.mLayoutManager = linearLayoutManagerImpl;
        this.mRecyclerView.setLayoutManager(linearLayoutManagerImpl);
        this.mRecyclerView.setScrollingTouchSlop(1);
        setOrientation(context, attributeSet);
        this.mRecyclerView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        this.mRecyclerView.addOnChildAttachStateChangeListener(enforceChildFillListener());
        this.mScrollEventAdapter = new OriginalScrollEventAdapter(this);
        this.mFakeDragger = new OriginalFakeDrag(this, this.mScrollEventAdapter, this.mRecyclerView);
        PagerSnapHelperImpl pagerSnapHelperImpl = new PagerSnapHelperImpl();
        this.mPagerSnapHelper = pagerSnapHelperImpl;
        pagerSnapHelperImpl.attachToRecyclerView(this.mRecyclerView);
        this.mRecyclerView.addOnScrollListener(this.mScrollEventAdapter);
        OriginalCompositeOnPageChangeCallback originalCompositeOnPageChangeCallback = new OriginalCompositeOnPageChangeCallback(3);
        this.mPageChangeEventDispatcher = originalCompositeOnPageChangeCallback;
        this.mScrollEventAdapter.setOnPageChangeCallback(originalCompositeOnPageChangeCallback);
        OnPageChangeCallback onPageChangeCallback = new OnPageChangeCallback() { // from class: androidx.viewpager2.widget.OriginalViewPager2.2
            @Override // androidx.viewpager2.widget.OriginalViewPager2.OnPageChangeCallback
            public void onPageSelected(int i) {
                if (OriginalViewPager2.this.mCurrentItem != i) {
                    OriginalViewPager2.this.mCurrentItem = i;
                    OriginalViewPager2.this.mAccessibilityProvider.onSetNewCurrentItem();
                }
            }

            @Override // androidx.viewpager2.widget.OriginalViewPager2.OnPageChangeCallback
            public void onPageScrollStateChanged(int i) {
                if (i == 0) {
                    OriginalViewPager2.this.updateCurrentItem();
                }
            }
        };
        OnPageChangeCallback onPageChangeCallback2 = new OnPageChangeCallback() { // from class: androidx.viewpager2.widget.OriginalViewPager2.3
            @Override // androidx.viewpager2.widget.OriginalViewPager2.OnPageChangeCallback
            public void onPageSelected(int i) {
                OriginalViewPager2.this.clearFocus();
                if (OriginalViewPager2.this.hasFocus()) {
                    OriginalViewPager2.this.mRecyclerView.requestFocus(2);
                }
            }
        };
        this.mPageChangeEventDispatcher.addOnPageChangeCallback(onPageChangeCallback);
        this.mPageChangeEventDispatcher.addOnPageChangeCallback(onPageChangeCallback2);
        this.mAccessibilityProvider.onInitialize(this.mPageChangeEventDispatcher, this.mRecyclerView);
        this.mPageChangeEventDispatcher.addOnPageChangeCallback(this.mExternalPageChangeCallbacks);
        OriginalPageTransformerAdapter originalPageTransformerAdapter = new OriginalPageTransformerAdapter(this.mLayoutManager);
        this.mPageTransformerAdapter = originalPageTransformerAdapter;
        this.mPageChangeEventDispatcher.addOnPageChangeCallback(originalPageTransformerAdapter);
        miuix.recyclerview.widget.RecyclerView recyclerView = this.mRecyclerView;
        attachViewToParent(recyclerView, 0, recyclerView.getLayoutParams());
    }

    private RecyclerView.OnChildAttachStateChangeListener enforceChildFillListener() {
        return new RecyclerView.OnChildAttachStateChangeListener() { // from class: androidx.viewpager2.widget.OriginalViewPager2.4
            @Override // androidx.recyclerview.widget.RecyclerView.OnChildAttachStateChangeListener
            public void onChildViewDetachedFromWindow(View view) {
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnChildAttachStateChangeListener
            public void onChildViewAttachedToWindow(View view) {
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
                if (layoutParams.width != -1 || layoutParams.height != -1) {
                    throw new IllegalStateException("Pages must fill the whole ViewPager2 (use match_parent)");
                }
            }
        };
    }

    @Override // android.view.ViewGroup, android.view.View
    public CharSequence getAccessibilityClassName() {
        if (this.mAccessibilityProvider.handlesGetAccessibilityClassName()) {
            return this.mAccessibilityProvider.onGetAccessibilityClassName();
        }
        return super.getAccessibilityClassName();
    }

    private void setOrientation(Context context, AttributeSet attributeSet) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ViewPager2);
        if (Build.VERSION.SDK_INT >= 29) {
            saveAttributeDataForStyleable(context, R.styleable.ViewPager2, attributeSet, typedArrayObtainStyledAttributes, 0, 0);
        }
        try {
            setOrientation(typedArrayObtainStyledAttributes.getInt(R.styleable.ViewPager2_android_orientation, 0));
        } finally {
            typedArrayObtainStyledAttributes.recycle();
        }
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.mRecyclerViewId = this.mRecyclerView.getId();
        int i = this.mPendingCurrentItem;
        if (i == -1) {
            i = this.mCurrentItem;
        }
        savedState.mCurrentItem = i;
        Parcelable parcelable = this.mPendingAdapterState;
        if (parcelable != null) {
            savedState.mAdapterState = parcelable;
        } else {
            Object adapter = this.mRecyclerView.getAdapter();
            if (adapter instanceof StatefulAdapter) {
                savedState.mAdapterState = ((StatefulAdapter) adapter).saveState();
            }
        }
        return savedState;
    }

    @Override // android.view.View
    protected void onRestoreInstanceState(Parcelable parcelable) {
        if (!(parcelable instanceof SavedState)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.mPendingCurrentItem = savedState.mCurrentItem;
        this.mPendingAdapterState = savedState.mAdapterState;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void restorePendingState() {
        RecyclerView.Adapter adapter;
        if (this.mPendingCurrentItem == -1 || (adapter = getAdapter()) == 0) {
            return;
        }
        Parcelable parcelable = this.mPendingAdapterState;
        if (parcelable != null) {
            if (adapter instanceof StatefulAdapter) {
                ((StatefulAdapter) adapter).restoreState(parcelable);
            }
            this.mPendingAdapterState = null;
        }
        int iMax = Math.max(0, Math.min(this.mPendingCurrentItem, adapter.getItemCount() - 1));
        this.mCurrentItem = iMax;
        this.mPendingCurrentItem = -1;
        this.mRecyclerView.scrollToPosition(iMax);
        this.mAccessibilityProvider.onRestorePendingState();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> sparseArray) {
        Parcelable parcelable = sparseArray.get(getId());
        if (parcelable instanceof SavedState) {
            int i = ((SavedState) parcelable).mRecyclerViewId;
            sparseArray.put(this.mRecyclerView.getId(), sparseArray.get(i));
            sparseArray.remove(i);
        }
        super.dispatchRestoreInstanceState(sparseArray);
        restorePendingState();
    }

    static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() { // from class: androidx.viewpager2.widget.OriginalViewPager2.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.ClassLoaderCreator
            public SavedState createFromParcel(Parcel parcel, ClassLoader classLoader) {
                return new SavedState(parcel, classLoader);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return createFromParcel(parcel, (ClassLoader) null);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        Parcelable mAdapterState;
        int mCurrentItem;
        int mRecyclerViewId;

        SavedState(Parcel parcel, ClassLoader classLoader) {
            super(parcel, classLoader);
            readValues(parcel, classLoader);
        }

        SavedState(Parcel parcel) {
            super(parcel);
            readValues(parcel, null);
        }

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        private void readValues(Parcel parcel, ClassLoader classLoader) {
            this.mRecyclerViewId = parcel.readInt();
            this.mCurrentItem = parcel.readInt();
            this.mAdapterState = parcel.readParcelable(classLoader);
        }

        @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.mRecyclerViewId);
            parcel.writeInt(this.mCurrentItem);
            parcel.writeParcelable(this.mAdapterState, i);
        }
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        RecyclerView.Adapter adapter2 = this.mRecyclerView.getAdapter();
        this.mAccessibilityProvider.onDetachAdapter(adapter2);
        unregisterCurrentItemDataSetTracker(adapter2);
        this.mRecyclerView.setAdapter(adapter);
        this.mCurrentItem = 0;
        restorePendingState();
        this.mAccessibilityProvider.onAttachAdapter(adapter);
        registerCurrentItemDataSetTracker(adapter);
    }

    private void registerCurrentItemDataSetTracker(RecyclerView.Adapter<?> adapter) {
        if (adapter != null) {
            adapter.registerAdapterDataObserver(this.mCurrentItemDataSetChangeObserver);
        }
    }

    private void unregisterCurrentItemDataSetTracker(RecyclerView.Adapter<?> adapter) {
        if (adapter != null) {
            adapter.unregisterAdapterDataObserver(this.mCurrentItemDataSetChangeObserver);
        }
    }

    public RecyclerView.Adapter getAdapter() {
        return this.mRecyclerView.getAdapter();
    }

    @Override // android.view.ViewGroup
    public void onViewAdded(View view) {
        throw new IllegalStateException(getClass().getSimpleName() + " does not support direct child views");
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        measureChild(this.mRecyclerView, i, i2);
        int measuredWidth = this.mRecyclerView.getMeasuredWidth();
        int measuredHeight = this.mRecyclerView.getMeasuredHeight();
        int measuredState = this.mRecyclerView.getMeasuredState();
        int paddingLeft = measuredWidth + getPaddingLeft() + getPaddingRight();
        int paddingTop = measuredHeight + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(resolveSizeAndState(Math.max(paddingLeft, getSuggestedMinimumWidth()), i, measuredState), resolveSizeAndState(Math.max(paddingTop, getSuggestedMinimumHeight()), i2, measuredState << 16));
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int measuredWidth = this.mRecyclerView.getMeasuredWidth();
        int measuredHeight = this.mRecyclerView.getMeasuredHeight();
        this.mTmpContainerRect.left = getPaddingLeft();
        int i5 = i3 - i;
        this.mTmpContainerRect.right = i5 - getPaddingRight();
        this.mTmpContainerRect.top = getPaddingTop();
        this.mTmpContainerRect.bottom = (i4 - i2) - getPaddingBottom();
        Gravity.apply(8388659, measuredWidth, measuredHeight, this.mTmpContainerRect, this.mTmpChildRect);
        this.mRecyclerView.layout(this.mTmpChildRect.left, this.mTmpChildRect.top, this.mTmpChildRect.right, this.mTmpChildRect.bottom);
        if (this.mCurrentItemDirty) {
            updateCurrentItem();
        }
        if (z && isRtl()) {
            if (this.savedLastWidth != i5) {
                setCurrentItemInternal(getCurrentItem(), false, true);
            }
            this.savedLastWidth = i5;
        }
    }

    void updateCurrentItem() {
        PagerSnapHelper pagerSnapHelper = this.mPagerSnapHelper;
        if (pagerSnapHelper == null) {
            throw new IllegalStateException("Design assumption violated.");
        }
        View viewFindSnapView = pagerSnapHelper.findSnapView(this.mLayoutManager);
        if (viewFindSnapView == null) {
            return;
        }
        int position = this.mLayoutManager.getPosition(viewFindSnapView);
        if (position != this.mCurrentItem && getScrollState() == 0) {
            this.mPageChangeEventDispatcher.onPageSelected(position);
        }
        this.mCurrentItemDirty = false;
    }

    int getPageSize() {
        int height;
        int paddingBottom;
        miuix.recyclerview.widget.RecyclerView recyclerView = this.mRecyclerView;
        if (getOrientation() == 0) {
            height = recyclerView.getWidth() - recyclerView.getPaddingLeft();
            paddingBottom = recyclerView.getPaddingRight();
        } else {
            height = recyclerView.getHeight() - recyclerView.getPaddingTop();
            paddingBottom = recyclerView.getPaddingBottom();
        }
        return height - paddingBottom;
    }

    public void setOrientation(int i) {
        this.mLayoutManager.setOrientation(i);
        this.mAccessibilityProvider.onSetOrientation();
    }

    public int getOrientation() {
        return this.mLayoutManager.getOrientation();
    }

    public boolean isRtl() {
        return this.mLayoutManager.getLayoutDirection() == 1;
    }

    public void setCurrentItem(int i) {
        setCurrentItem(i, true);
    }

    public void setCurrentItem(int i, boolean z) {
        if (isFakeDragging()) {
            throw new IllegalStateException("Cannot change current item when ViewPager2 is fake dragging");
        }
        setCurrentItemInternal(i, z, false);
    }

    void setCurrentItemInternal(int i, boolean z, boolean z2) {
        RecyclerView.Adapter adapter = getAdapter();
        if (adapter == null) {
            if (this.mPendingCurrentItem != -1) {
                this.mPendingCurrentItem = Math.max(i, 0);
                return;
            }
            return;
        }
        if (adapter.getItemCount() <= 0) {
            return;
        }
        int iMin = Math.min(Math.max(i, 0), adapter.getItemCount() - 1);
        if (z2 && iMin == this.mCurrentItem) {
            this.mRecyclerView.scrollToPosition(iMin);
            return;
        }
        if (iMin == this.mCurrentItem && this.mScrollEventAdapter.isIdle()) {
            return;
        }
        int i2 = this.mCurrentItem;
        if (iMin == i2 && z) {
            return;
        }
        double relativeScrollPosition = i2;
        this.mCurrentItem = iMin;
        this.mAccessibilityProvider.onSetNewCurrentItem();
        if (!this.mScrollEventAdapter.isIdle()) {
            relativeScrollPosition = this.mScrollEventAdapter.getRelativeScrollPosition();
        }
        this.mScrollEventAdapter.notifyProgrammaticScroll(iMin, z);
        if (!z) {
            this.mRecyclerView.scrollToPosition(iMin);
            return;
        }
        double d = iMin;
        if (Math.abs(d - relativeScrollPosition) > 3.0d) {
            this.mRecyclerView.scrollToPosition(d > relativeScrollPosition ? iMin - 3 : iMin + 3);
            this.mRecyclerView.post(new SmoothScrollToPosition(iMin, this.mRecyclerView));
        } else {
            this.mRecyclerView.smoothScrollToPosition(iMin);
        }
    }

    public int getCurrentItem() {
        return this.mCurrentItem;
    }

    public int getScrollState() {
        return this.mScrollEventAdapter.getScrollState();
    }

    public boolean beginFakeDrag() {
        return this.mFakeDragger.beginFakeDrag();
    }

    public boolean fakeDragBy(float f) {
        return this.mFakeDragger.fakeDragBy(f);
    }

    public boolean endFakeDrag() {
        return this.mFakeDragger.endFakeDrag();
    }

    public boolean isFakeDragging() {
        return this.mFakeDragger.isFakeDragging();
    }

    void snapToPage() {
        View viewFindSnapView = this.mPagerSnapHelper.findSnapView(this.mLayoutManager);
        if (viewFindSnapView == null) {
            return;
        }
        int[] iArrCalculateDistanceToFinalSnap = this.mPagerSnapHelper.calculateDistanceToFinalSnap(this.mLayoutManager, viewFindSnapView);
        int i = iArrCalculateDistanceToFinalSnap[0];
        if (i == 0 && iArrCalculateDistanceToFinalSnap[1] == 0) {
            return;
        }
        this.mRecyclerView.smoothScrollBy(i, iArrCalculateDistanceToFinalSnap[1]);
    }

    public void setUserInputEnabled(boolean z) {
        this.mUserInputEnabled = z;
        this.mAccessibilityProvider.onSetUserInputEnabled();
    }

    public boolean isUserInputEnabled() {
        return this.mUserInputEnabled;
    }

    public void setOffscreenPageLimit(int i) {
        if (i < 1 && i != -1) {
            throw new IllegalArgumentException("Offscreen page limit must be OFFSCREEN_PAGE_LIMIT_DEFAULT or a number > 0");
        }
        this.mOffscreenPageLimit = i;
        this.mRecyclerView.requestLayout();
    }

    public int getOffscreenPageLimit() {
        return this.mOffscreenPageLimit;
    }

    @Override // android.view.View
    public boolean canScrollHorizontally(int i) {
        return this.mRecyclerView.canScrollHorizontally(i);
    }

    @Override // android.view.View
    public boolean canScrollVertically(int i) {
        return this.mRecyclerView.canScrollVertically(i);
    }

    public void registerOnPageChangeCallback(OnPageChangeCallback onPageChangeCallback) {
        this.mExternalPageChangeCallbacks.addOnPageChangeCallback(onPageChangeCallback);
    }

    public void unregisterOnPageChangeCallback(OnPageChangeCallback onPageChangeCallback) {
        this.mExternalPageChangeCallbacks.removeOnPageChangeCallback(onPageChangeCallback);
    }

    public void setPageTransformer(PageTransformer pageTransformer) {
        if (pageTransformer != null) {
            if (!this.mSavedItemAnimatorPresent) {
                this.mSavedItemAnimator = this.mRecyclerView.getItemAnimator();
                this.mSavedItemAnimatorPresent = true;
            }
            this.mRecyclerView.setItemAnimator(null);
        } else if (this.mSavedItemAnimatorPresent) {
            this.mRecyclerView.setItemAnimator(this.mSavedItemAnimator);
            this.mSavedItemAnimator = null;
            this.mSavedItemAnimatorPresent = false;
        }
        if (pageTransformer == this.mPageTransformerAdapter.getPageTransformer()) {
            return;
        }
        this.mPageTransformerAdapter.setPageTransformer(pageTransformer);
        requestTransform();
    }

    public void requestTransform() {
        if (this.mPageTransformerAdapter.getPageTransformer() == null) {
            return;
        }
        double relativeScrollPosition = this.mScrollEventAdapter.getRelativeScrollPosition();
        int i = (int) relativeScrollPosition;
        float f = (float) (relativeScrollPosition - ((double) i));
        this.mPageTransformerAdapter.onPageScrolled(i, f, Math.round(getPageSize() * f));
    }

    @Override // android.view.View
    public void setLayoutDirection(int i) {
        super.setLayoutDirection(i);
        this.mAccessibilityProvider.onSetLayoutDirection();
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        this.mAccessibilityProvider.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
    }

    @Override // android.view.View
    public boolean performAccessibilityAction(int i, Bundle bundle) {
        if (this.mAccessibilityProvider.handlesPerformAccessibilityAction(i, bundle)) {
            return this.mAccessibilityProvider.onPerformAccessibilityAction(i, bundle);
        }
        return super.performAccessibilityAction(i, bundle);
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        setCurrentItemInternal(getCurrentItem(), false, true);
    }

    public void setSpringEnabled(boolean z) {
        this.mRecyclerView.setSpringEnabled(z);
    }

    private class RecyclerViewImpl extends miuix.recyclerview.widget.RecyclerView {
        private float initX;
        private float initY;
        private boolean mIsScrolledX;
        private boolean mIsScrolledY;
        private int mTouchSlop;

        RecyclerViewImpl(Context context) {
            super(context);
            this.mIsScrolledX = false;
            this.mIsScrolledY = false;
            this.mTouchSlop = ViewConfiguration.get(context).getScaledPagingTouchSlop();
        }

        @Override // androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
        public CharSequence getAccessibilityClassName() {
            if (OriginalViewPager2.this.mAccessibilityProvider.handlesRvGetAccessibilityClassName()) {
                return OriginalViewPager2.this.mAccessibilityProvider.onRvGetAccessibilityClassName();
            }
            return super.getAccessibilityClassName();
        }

        @Override // android.view.View
        public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
            super.onInitializeAccessibilityEvent(accessibilityEvent);
            accessibilityEvent.setFromIndex(OriginalViewPager2.this.mCurrentItem);
            accessibilityEvent.setToIndex(OriginalViewPager2.this.mCurrentItem);
            OriginalViewPager2.this.mAccessibilityProvider.onRvInitializeAccessibilityEvent(accessibilityEvent);
        }

        @Override // androidx.recyclerview.widget.SpringRecyclerView, androidx.recyclerview.widget.RemixRecyclerView, androidx.recyclerview.widget.RecyclerView, android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            return OriginalViewPager2.this.isUserInputEnabled() && super.onTouchEvent(motionEvent);
        }

        @Override // androidx.recyclerview.widget.SpringRecyclerView, androidx.recyclerview.widget.RemixRecyclerView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (!OriginalViewPager2.this.isUserInputEnabled()) {
                return false;
            }
            if (motionEvent.getActionMasked() == 0) {
                this.initX = motionEvent.getX();
                this.initY = motionEvent.getY();
                this.mIsScrolledX = false;
                this.mIsScrolledY = false;
            } else if (motionEvent.getActionMasked() == 2) {
                float fAbs = Math.abs(motionEvent.getX() - this.initX);
                float fAbs2 = Math.abs(motionEvent.getY() - this.initY);
                int i = this.mTouchSlop;
                if (fAbs > i && fAbs * 0.5f > fAbs2 && !this.mIsScrolledY) {
                    this.mIsScrolledX = true;
                } else if (fAbs2 > i) {
                    this.mIsScrolledX = false;
                    this.mIsScrolledY = true;
                }
            } else if (motionEvent.getActionMasked() == 1 || motionEvent.getActionMasked() == 3) {
                this.mIsScrolledX = false;
                this.mIsScrolledY = false;
            }
            if (this.mIsScrolledY || !this.mIsScrolledX) {
                return false;
            }
            MotionEvent motionEventObtain = MotionEvent.obtain(motionEvent);
            motionEventObtain.setAction(0);
            super.onInterceptTouchEvent(motionEventObtain);
            return true;
        }
    }

    private class LinearLayoutManagerImpl extends LinearLayoutManager {
        @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
        public boolean requestChildRectangleOnScreen(RecyclerView recyclerView, View view, Rect rect, boolean z, boolean z2) {
            return false;
        }

        LinearLayoutManagerImpl(Context context) {
            super(context);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
        public boolean performAccessibilityAction(RecyclerView.Recycler recycler, RecyclerView.State state, int i, Bundle bundle) {
            if (OriginalViewPager2.this.mAccessibilityProvider.handlesLmPerformAccessibilityAction(i)) {
                return OriginalViewPager2.this.mAccessibilityProvider.onLmPerformAccessibilityAction(i);
            }
            return super.performAccessibilityAction(recycler, state, i, bundle);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
        public void onInitializeAccessibilityNodeInfo(RecyclerView.Recycler recycler, RecyclerView.State state, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            super.onInitializeAccessibilityNodeInfo(recycler, state, accessibilityNodeInfoCompat);
            OriginalViewPager2.this.mAccessibilityProvider.onLmInitializeAccessibilityNodeInfo(accessibilityNodeInfoCompat);
        }

        @Override // androidx.recyclerview.widget.LinearLayoutManager
        protected void calculateExtraLayoutSpace(RecyclerView.State state, int[] iArr) {
            int offscreenPageLimit = OriginalViewPager2.this.getOffscreenPageLimit();
            if (offscreenPageLimit == -1) {
                super.calculateExtraLayoutSpace(state, iArr);
                return;
            }
            int pageSize = OriginalViewPager2.this.getPageSize() * offscreenPageLimit;
            iArr[0] = pageSize;
            iArr[1] = pageSize;
        }
    }

    private class PagerSnapHelperImpl extends PagerSnapHelper {
        PagerSnapHelperImpl() {
        }

        @Override // androidx.recyclerview.widget.PagerSnapHelper, androidx.recyclerview.widget.SnapHelper
        public View findSnapView(RecyclerView.LayoutManager layoutManager) {
            if (OriginalViewPager2.this.isFakeDragging()) {
                return null;
            }
            return super.findSnapView(layoutManager);
        }
    }

    private static class SmoothScrollToPosition implements Runnable {
        private final int mPosition;
        private final miuix.recyclerview.widget.RecyclerView mRecyclerView;

        SmoothScrollToPosition(int i, miuix.recyclerview.widget.RecyclerView recyclerView) {
            this.mPosition = i;
            this.mRecyclerView = recyclerView;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.mRecyclerView.smoothScrollToPosition(this.mPosition);
        }
    }

    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        this.mRecyclerView.addItemDecoration(itemDecoration);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration, int i) {
        this.mRecyclerView.addItemDecoration(itemDecoration, i);
    }

    public RecyclerView.ItemDecoration getItemDecorationAt(int i) {
        return this.mRecyclerView.getItemDecorationAt(i);
    }

    public int getItemDecorationCount() {
        return this.mRecyclerView.getItemDecorationCount();
    }

    public void invalidateItemDecorations() {
        this.mRecyclerView.invalidateItemDecorations();
    }

    public void removeItemDecorationAt(int i) {
        this.mRecyclerView.removeItemDecorationAt(i);
    }

    public void removeItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        this.mRecyclerView.removeItemDecoration(itemDecoration);
    }

    private abstract class AccessibilityProvider {
        boolean handlesGetAccessibilityClassName() {
            return false;
        }

        boolean handlesLmPerformAccessibilityAction(int i) {
            return false;
        }

        boolean handlesPerformAccessibilityAction(int i, Bundle bundle) {
            return false;
        }

        boolean handlesRvGetAccessibilityClassName() {
            return false;
        }

        void onAttachAdapter(RecyclerView.Adapter<?> adapter) {
        }

        void onDetachAdapter(RecyclerView.Adapter<?> adapter) {
        }

        void onInitialize(OriginalCompositeOnPageChangeCallback originalCompositeOnPageChangeCallback, miuix.recyclerview.widget.RecyclerView recyclerView) {
        }

        void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        }

        void onLmInitializeAccessibilityNodeInfo(AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
        }

        void onRestorePendingState() {
        }

        void onRvInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        }

        void onSetLayoutDirection() {
        }

        void onSetNewCurrentItem() {
        }

        void onSetOrientation() {
        }

        void onSetUserInputEnabled() {
        }

        private AccessibilityProvider() {
        }

        String onGetAccessibilityClassName() {
            throw new IllegalStateException("Not implemented.");
        }

        boolean onPerformAccessibilityAction(int i, Bundle bundle) {
            throw new IllegalStateException("Not implemented.");
        }

        boolean onLmPerformAccessibilityAction(int i) {
            throw new IllegalStateException("Not implemented.");
        }

        CharSequence onRvGetAccessibilityClassName() {
            throw new IllegalStateException("Not implemented.");
        }
    }

    class BasicAccessibilityProvider extends AccessibilityProvider {
        @Override // androidx.viewpager2.widget.OriginalViewPager2.AccessibilityProvider
        public boolean handlesRvGetAccessibilityClassName() {
            return true;
        }

        BasicAccessibilityProvider() {
            super();
        }

        @Override // androidx.viewpager2.widget.OriginalViewPager2.AccessibilityProvider
        public boolean handlesLmPerformAccessibilityAction(int i) {
            return (i == 8192 || i == 4096) && !OriginalViewPager2.this.isUserInputEnabled();
        }

        @Override // androidx.viewpager2.widget.OriginalViewPager2.AccessibilityProvider
        public boolean onLmPerformAccessibilityAction(int i) {
            if (handlesLmPerformAccessibilityAction(i)) {
                return false;
            }
            throw new IllegalStateException();
        }

        @Override // androidx.viewpager2.widget.OriginalViewPager2.AccessibilityProvider
        public void onLmInitializeAccessibilityNodeInfo(AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            if (OriginalViewPager2.this.isUserInputEnabled()) {
                return;
            }
            accessibilityNodeInfoCompat.removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_BACKWARD);
            accessibilityNodeInfoCompat.removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_FORWARD);
            accessibilityNodeInfoCompat.setScrollable(false);
        }

        @Override // androidx.viewpager2.widget.OriginalViewPager2.AccessibilityProvider
        public CharSequence onRvGetAccessibilityClassName() {
            if (!handlesRvGetAccessibilityClassName()) {
                throw new IllegalStateException();
            }
            return "androidx.viewpager.widget.ViewPager";
        }
    }

    class PageAwareAccessibilityProvider extends AccessibilityProvider {
        private final AccessibilityViewCommand mActionPageBackward;
        private final AccessibilityViewCommand mActionPageForward;
        private RecyclerView.AdapterDataObserver mAdapterDataObserver;

        @Override // androidx.viewpager2.widget.OriginalViewPager2.AccessibilityProvider
        public boolean handlesGetAccessibilityClassName() {
            return true;
        }

        @Override // androidx.viewpager2.widget.OriginalViewPager2.AccessibilityProvider
        public boolean handlesPerformAccessibilityAction(int i, Bundle bundle) {
            return i == 8192 || i == 4096;
        }

        PageAwareAccessibilityProvider() {
            super();
            this.mActionPageForward = new AccessibilityViewCommand() { // from class: androidx.viewpager2.widget.OriginalViewPager2.PageAwareAccessibilityProvider.1
                @Override // androidx.core.view.accessibility.AccessibilityViewCommand
                public boolean perform(View view, AccessibilityViewCommand.CommandArguments commandArguments) {
                    PageAwareAccessibilityProvider.this.setCurrentItemFromAccessibilityCommand(((OriginalViewPager2) view).getCurrentItem() + 1);
                    return true;
                }
            };
            this.mActionPageBackward = new AccessibilityViewCommand() { // from class: androidx.viewpager2.widget.OriginalViewPager2.PageAwareAccessibilityProvider.2
                @Override // androidx.core.view.accessibility.AccessibilityViewCommand
                public boolean perform(View view, AccessibilityViewCommand.CommandArguments commandArguments) {
                    PageAwareAccessibilityProvider.this.setCurrentItemFromAccessibilityCommand(((OriginalViewPager2) view).getCurrentItem() - 1);
                    return true;
                }
            };
        }

        @Override // androidx.viewpager2.widget.OriginalViewPager2.AccessibilityProvider
        public void onInitialize(OriginalCompositeOnPageChangeCallback originalCompositeOnPageChangeCallback, miuix.recyclerview.widget.RecyclerView recyclerView) {
            ViewCompat.setImportantForAccessibility(recyclerView, 2);
            this.mAdapterDataObserver = new DataSetChangeObserver() { // from class: androidx.viewpager2.widget.OriginalViewPager2.PageAwareAccessibilityProvider.3
                @Override // androidx.viewpager2.widget.OriginalViewPager2.DataSetChangeObserver, androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
                public void onChanged() {
                    PageAwareAccessibilityProvider.this.updatePageAccessibilityActions();
                }
            };
            if (ViewCompat.getImportantForAccessibility(OriginalViewPager2.this) == 0) {
                ViewCompat.setImportantForAccessibility(OriginalViewPager2.this, 1);
            }
        }

        @Override // androidx.viewpager2.widget.OriginalViewPager2.AccessibilityProvider
        public String onGetAccessibilityClassName() {
            if (!handlesGetAccessibilityClassName()) {
                throw new IllegalStateException();
            }
            return "androidx.viewpager.widget.ViewPager";
        }

        @Override // androidx.viewpager2.widget.OriginalViewPager2.AccessibilityProvider
        public void onRestorePendingState() {
            updatePageAccessibilityActions();
        }

        @Override // androidx.viewpager2.widget.OriginalViewPager2.AccessibilityProvider
        public void onAttachAdapter(RecyclerView.Adapter<?> adapter) {
            updatePageAccessibilityActions();
            if (adapter != null) {
                adapter.registerAdapterDataObserver(this.mAdapterDataObserver);
            }
        }

        @Override // androidx.viewpager2.widget.OriginalViewPager2.AccessibilityProvider
        public void onDetachAdapter(RecyclerView.Adapter<?> adapter) {
            if (adapter != null) {
                adapter.unregisterAdapterDataObserver(this.mAdapterDataObserver);
            }
        }

        @Override // androidx.viewpager2.widget.OriginalViewPager2.AccessibilityProvider
        public void onSetOrientation() {
            updatePageAccessibilityActions();
        }

        @Override // androidx.viewpager2.widget.OriginalViewPager2.AccessibilityProvider
        public void onSetNewCurrentItem() {
            updatePageAccessibilityActions();
        }

        @Override // androidx.viewpager2.widget.OriginalViewPager2.AccessibilityProvider
        public void onSetUserInputEnabled() {
            updatePageAccessibilityActions();
        }

        @Override // androidx.viewpager2.widget.OriginalViewPager2.AccessibilityProvider
        public void onSetLayoutDirection() {
            updatePageAccessibilityActions();
        }

        @Override // androidx.viewpager2.widget.OriginalViewPager2.AccessibilityProvider
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            addCollectionInfo(accessibilityNodeInfo);
            addScrollActions(accessibilityNodeInfo);
        }

        @Override // androidx.viewpager2.widget.OriginalViewPager2.AccessibilityProvider
        public boolean onPerformAccessibilityAction(int i, Bundle bundle) {
            int currentItem;
            if (!handlesPerformAccessibilityAction(i, bundle)) {
                throw new IllegalStateException();
            }
            if (i == 8192) {
                currentItem = OriginalViewPager2.this.getCurrentItem() - 1;
            } else {
                currentItem = OriginalViewPager2.this.getCurrentItem() + 1;
            }
            setCurrentItemFromAccessibilityCommand(currentItem);
            return true;
        }

        @Override // androidx.viewpager2.widget.OriginalViewPager2.AccessibilityProvider
        public void onRvInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
            accessibilityEvent.setSource(OriginalViewPager2.this);
            accessibilityEvent.setClassName(onGetAccessibilityClassName());
        }

        void setCurrentItemFromAccessibilityCommand(int i) {
            if (OriginalViewPager2.this.isUserInputEnabled()) {
                OriginalViewPager2.this.setCurrentItemInternal(i, true, false);
            }
        }

        void updatePageAccessibilityActions() {
            int itemCount;
            OriginalViewPager2 originalViewPager2 = OriginalViewPager2.this;
            int i = android.R.id.accessibilityActionPageLeft;
            ViewCompat.removeAccessibilityAction(originalViewPager2, android.R.id.accessibilityActionPageLeft);
            ViewCompat.removeAccessibilityAction(originalViewPager2, android.R.id.accessibilityActionPageRight);
            ViewCompat.removeAccessibilityAction(originalViewPager2, android.R.id.accessibilityActionPageUp);
            ViewCompat.removeAccessibilityAction(originalViewPager2, android.R.id.accessibilityActionPageDown);
            if (OriginalViewPager2.this.getAdapter() == null || (itemCount = OriginalViewPager2.this.getAdapter().getItemCount()) == 0 || !OriginalViewPager2.this.isUserInputEnabled()) {
                return;
            }
            if (OriginalViewPager2.this.getOrientation() == 0) {
                boolean zIsRtl = OriginalViewPager2.this.isRtl();
                int i2 = zIsRtl ? 16908360 : 16908361;
                if (zIsRtl) {
                    i = 16908361;
                }
                if (OriginalViewPager2.this.mCurrentItem < itemCount - 1) {
                    ViewCompat.replaceAccessibilityAction(originalViewPager2, new AccessibilityNodeInfoCompat.AccessibilityActionCompat(i2, null), null, this.mActionPageForward);
                }
                if (OriginalViewPager2.this.mCurrentItem > 0) {
                    ViewCompat.replaceAccessibilityAction(originalViewPager2, new AccessibilityNodeInfoCompat.AccessibilityActionCompat(i, null), null, this.mActionPageBackward);
                    return;
                }
                return;
            }
            if (OriginalViewPager2.this.mCurrentItem < itemCount - 1) {
                ViewCompat.replaceAccessibilityAction(originalViewPager2, new AccessibilityNodeInfoCompat.AccessibilityActionCompat(android.R.id.accessibilityActionPageDown, null), null, this.mActionPageForward);
            }
            if (OriginalViewPager2.this.mCurrentItem > 0) {
                ViewCompat.replaceAccessibilityAction(originalViewPager2, new AccessibilityNodeInfoCompat.AccessibilityActionCompat(android.R.id.accessibilityActionPageUp, null), null, this.mActionPageBackward);
            }
        }

        private void addCollectionInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            int itemCount;
            int itemCount2;
            if (OriginalViewPager2.this.getAdapter() == null) {
                itemCount = 0;
                itemCount2 = 0;
            } else if (OriginalViewPager2.this.getOrientation() == 1) {
                itemCount = OriginalViewPager2.this.getAdapter().getItemCount();
                itemCount2 = 0;
            } else {
                itemCount2 = OriginalViewPager2.this.getAdapter().getItemCount();
                itemCount = 0;
            }
            AccessibilityNodeInfoCompat.wrap(accessibilityNodeInfo).setCollectionInfo(AccessibilityNodeInfoCompat.CollectionInfoCompat.obtain(itemCount, itemCount2, false, 0));
        }

        private void addScrollActions(AccessibilityNodeInfo accessibilityNodeInfo) {
            int itemCount;
            RecyclerView.Adapter adapter = OriginalViewPager2.this.getAdapter();
            if (adapter == null || (itemCount = adapter.getItemCount()) == 0 || !OriginalViewPager2.this.isUserInputEnabled()) {
                return;
            }
            if (OriginalViewPager2.this.mCurrentItem > 0) {
                accessibilityNodeInfo.addAction(8192);
            }
            if (OriginalViewPager2.this.mCurrentItem < itemCount - 1) {
                accessibilityNodeInfo.addAction(4096);
            }
            accessibilityNodeInfo.setScrollable(true);
        }
    }

    private static abstract class DataSetChangeObserver extends RecyclerView.AdapterDataObserver {
        @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
        public abstract void onChanged();

        private DataSetChangeObserver() {
        }

        @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
        public final void onItemRangeChanged(int i, int i2) {
            onChanged();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
        public final void onItemRangeChanged(int i, int i2, Object obj) {
            onChanged();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
        public final void onItemRangeInserted(int i, int i2) {
            onChanged();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
        public final void onItemRangeRemoved(int i, int i2) {
            onChanged();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
        public final void onItemRangeMoved(int i, int i2, int i3) {
            onChanged();
        }
    }
}
