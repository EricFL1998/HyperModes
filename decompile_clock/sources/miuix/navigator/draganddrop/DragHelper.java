package miuix.navigator.draganddrop;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.ArrayMap;
import android.view.DragEvent;
import android.view.View;
import android.view.animation.Interpolator;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Map;
import java.util.Set;
import miuix.animation.Folme;
import miuix.animation.base.AnimConfig;
import miuix.animation.property.FloatProperty;
import miuix.animation.utils.EaseManager;
import miuix.navigator.R;
import miuix.navigator.adapter.CategoryAdapter;
import miuix.navigator.adapter.CategoryAdapterWrapper;
import miuix.navigator.adapter.NavigationAdapter;

/* JADX INFO: loaded from: classes3.dex */
public class DragHelper extends RecyclerView.ItemDecoration implements View.OnDragListener, RecyclerView.OnChildAttachStateChangeListener {
    private static final long DELAY_HOVER_PRESS = 1000;
    private static final long DRAG_SCROLL_ACCELERATION_LIMIT_TIME_MS = 2000;
    private int mDelta;
    private int mDragInsertZone;
    private Map<CategoryAdapterWrapper, DragStartFeedback> mFeedbackMap;
    private View mHoverOnView;
    private CategoryAdapterWrapper mInsertedAdapter;
    private int mInsertedPosition;
    private CategoryAdapterWrapper mLastAdapter;
    private int mMaxDragScroll;
    private CategoryAdapterWrapper mPendingInsertAdapter;
    private int mPendingInsertPosition;
    private RecyclerView mRecyclerView;
    private int mScrollZone;
    private int mViewSize;
    private static final Interpolator sDragScrollInterpolator = new Interpolator() { // from class: miuix.navigator.draganddrop.DragHelper$$ExternalSyntheticLambda0
        @Override // android.animation.TimeInterpolator
        public final float getInterpolation(float f) {
            return DragHelper.lambda$static$0(f);
        }
    };
    private static final Interpolator sDragViewScrollCapInterpolator = new Interpolator() { // from class: miuix.navigator.draganddrop.DragHelper$$ExternalSyntheticLambda1
        @Override // android.animation.TimeInterpolator
        public final float getInterpolation(float f) {
            return DragHelper.lambda$static$1(f);
        }
    };
    private static final FloatProperty<MaskHelper> ALPHA = new FloatProperty<MaskHelper>("alpha") { // from class: miuix.navigator.draganddrop.DragHelper.2
        @Override // miuix.animation.property.FloatProperty
        public float getValue(MaskHelper maskHelper) {
            return maskHelper.getAlpha();
        }

        @Override // miuix.animation.property.FloatProperty
        public void setValue(MaskHelper maskHelper, float f) {
            maskHelper.setAlpha(f);
        }
    };
    private float mX = -1.0f;
    private float mY = -1.0f;
    private final RectF mRect = new RectF();
    private final Rect mBounds = new Rect();
    private final MaskHelper mMaskHelper = new MaskHelper();
    private long mMsStartScroll = -1;
    private final Runnable mScrollRunnable = new Runnable() { // from class: miuix.navigator.draganddrop.DragHelper.1
        @Override // java.lang.Runnable
        public void run() {
            long jUptimeMillis = SystemClock.uptimeMillis();
            if (DragHelper.this.mMsStartScroll == -1) {
                DragHelper.this.mMsStartScroll = jUptimeMillis;
            }
            if (DragHelper.this.mRecyclerView.canScrollVertically(DragHelper.this.mDelta)) {
                DragHelper.this.mRecyclerView.scrollBy(0, interpolateOutOfBoundsScroll(DragHelper.this.mDelta, jUptimeMillis - DragHelper.this.mMsStartScroll));
            }
            DragHelper.this.handleDragLocation();
        }

        private int interpolateOutOfBoundsScroll(int i, long j) {
            int iSignum = (int) (((int) (((int) Math.signum(i)) * getMaxDragScroll() * DragHelper.sDragViewScrollCapInterpolator.getInterpolation(Math.min(1.0f, (Math.abs(i) * 1.0f) / DragHelper.this.mViewSize)))) * DragHelper.sDragScrollInterpolator.getInterpolation(j <= DragHelper.DRAG_SCROLL_ACCELERATION_LIMIT_TIME_MS ? j / 2000.0f : 1.0f));
            if (iSignum == 0) {
                return i > 0 ? 1 : -1;
            }
            return iSignum;
        }

        private int getMaxDragScroll() {
            return DragHelper.this.mMaxDragScroll;
        }
    };
    private final Runnable mPressRunnable = new Runnable() { // from class: miuix.navigator.draganddrop.DragHelper$$ExternalSyntheticLambda2
        @Override // java.lang.Runnable
        public final void run() {
            this.f$0.m1911lambda$new$2$miuixnavigatordraganddropDragHelper();
        }
    };
    private final Runnable mInsertPlaceholderRunnable = new Runnable() { // from class: miuix.navigator.draganddrop.DragHelper$$ExternalSyntheticLambda3
        @Override // java.lang.Runnable
        public final void run() {
            this.f$0.m1912lambda$new$3$miuixnavigatordraganddropDragHelper();
        }
    };
    private final Runnable mRemovePlaceholderRunnable = new Runnable() { // from class: miuix.navigator.draganddrop.DragHelper$$ExternalSyntheticLambda4
        @Override // java.lang.Runnable
        public final void run() {
            this.f$0.m1913lambda$new$4$miuixnavigatordraganddropDragHelper();
        }
    };

    static /* synthetic */ float lambda$static$0(float f) {
        return f * f * f * f * f;
    }

    static /* synthetic */ float lambda$static$1(float f) {
        float f2 = f - 1.0f;
        return (f2 * f2 * f2 * f2 * f2) + 1.0f;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.OnChildAttachStateChangeListener
    public void onChildViewAttachedToWindow(View view) {
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
    public void onDrawOver(Canvas canvas, RecyclerView recyclerView, RecyclerView.State state) {
        if (!this.mMaskHelper.isEnabled() || this.mMaskHelper.mSkipSet == null) {
            return;
        }
        int childCount = recyclerView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = recyclerView.getChildAt(i);
            CategoryAdapterWrapper categoryAdapterWrapper = (CategoryAdapterWrapper) childAt.getTag(R.id.miuix_navigator_drag_helper_token);
            if ((categoryAdapterWrapper == null || !this.mMaskHelper.mSkipSet.contains(categoryAdapterWrapper)) && childAt.getId() != R.id.navigation_item_drag_placeholder) {
                int iRound = Math.round(childAt.getTranslationY());
                this.mBounds.set(childAt.getLeft(), childAt.getTop() + iRound, childAt.getRight(), childAt.getBottom() + iRound);
                canvas.drawRect(this.mBounds, this.mMaskHelper.mPaint);
            }
        }
    }

    public void attachToRecyclerView(RecyclerView recyclerView) {
        RecyclerView recyclerView2 = this.mRecyclerView;
        if (recyclerView2 == recyclerView) {
            return;
        }
        if (recyclerView2 != null) {
            destroyCallBacks();
        }
        this.mRecyclerView = recyclerView;
        if (recyclerView != null) {
            initResources();
            setupCallBacks();
        }
    }

    public void initResources() {
        RecyclerView recyclerView = this.mRecyclerView;
        if (recyclerView == null) {
            return;
        }
        TypedArray typedArrayObtainStyledAttributes = recyclerView.getContext().obtainStyledAttributes(R.styleable.NavigatorDragAndDrop);
        this.mScrollZone = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.NavigatorDragAndDrop_navigatorDragScrollZone, 0);
        this.mDragInsertZone = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.NavigatorDragAndDrop_navigatorDragItemInsertZone, 0);
        this.mDragInsertZone = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.NavigatorDragAndDrop_navigatorDragItemInsertZone, 0);
        typedArrayObtainStyledAttributes.recycle();
        TypedArray typedArrayObtainStyledAttributes2 = this.mRecyclerView.getContext().obtainStyledAttributes(new int[]{R.attr.navigatorItemMinHeight});
        this.mViewSize = typedArrayObtainStyledAttributes2.getDimensionPixelSize(0, 0);
        typedArrayObtainStyledAttributes2.recycle();
        this.mMaxDragScroll = this.mRecyclerView.getResources().getDimensionPixelSize(R.dimen.item_touch_helper_max_drag_scroll_per_frame);
    }

    private void setupCallBacks() {
        this.mRecyclerView.addItemDecoration(this);
        this.mRecyclerView.setOnDragListener(this);
        this.mRecyclerView.addOnChildAttachStateChangeListener(this);
    }

    private void destroyCallBacks() {
        this.mRecyclerView.removeItemDecoration(this);
        this.mRecyclerView.setOnDragListener(null);
        this.mRecyclerView.removeOnChildAttachStateChangeListener(this);
        this.mRecyclerView.removeCallbacks(this.mScrollRunnable);
        this.mMsStartScroll = -1L;
        this.mRecyclerView.removeCallbacks(this.mPressRunnable);
        this.mRecyclerView.removeCallbacks(this.mInsertPlaceholderRunnable);
        cleanDragPlaceholder();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.OnChildAttachStateChangeListener
    public void onChildViewDetachedFromWindow(View view) {
        if (this.mHoverOnView == view) {
            this.mHoverOnView = null;
            handleDragLocation();
        }
    }

    @Override // android.view.View.OnDragListener
    public boolean onDrag(View view, DragEvent dragEvent) {
        NavigationAdapter navigationAdapter;
        RecyclerView recyclerView = this.mRecyclerView;
        if (recyclerView == null || view != recyclerView || (navigationAdapter = (NavigationAdapter) recyclerView.getAdapter()) == null || navigationAdapter.isEditing()) {
            return false;
        }
        int action = dragEvent.getAction();
        if (action == 1) {
            return handleDragStart(navigationAdapter, dragEvent);
        }
        if (action == 2) {
            this.mX = dragEvent.getX();
            this.mY = dragEvent.getY();
            handleDragLocation();
            return true;
        }
        if (action == 3) {
            this.mX = dragEvent.getX();
            this.mY = dragEvent.getY();
            handleDragLocation();
            return handleDrop(dragEvent);
        }
        if (action == 4) {
            handleDragEnd();
            return true;
        }
        if (action != 6) {
            return true;
        }
        handleDragExit();
        return true;
    }

    private boolean handleDragStart(NavigationAdapter navigationAdapter, DragEvent dragEvent) {
        ArrayMap arrayMap = new ArrayMap();
        this.mFeedbackMap = arrayMap;
        navigationAdapter.dispatchOnDragStart(arrayMap, dragEvent);
        this.mRecyclerView.invalidate();
        return !this.mFeedbackMap.isEmpty();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDragLocation() {
        this.mRect.set(0.0f, 0.0f, this.mRecyclerView.getWidth(), this.mRecyclerView.getHeight());
        if (!this.mRect.contains(this.mX, this.mY)) {
            handleDragExit();
            return;
        }
        this.mLastAdapter = null;
        boolean z = true;
        this.mMaskHelper.setMask(true);
        scrollIfNecessary();
        View viewFindChildViewUnder = this.mRecyclerView.findChildViewUnder(this.mX, this.mY);
        int i = this.mDragInsertZone / 2;
        if (viewFindChildViewUnder == null) {
            viewFindChildViewUnder = this.mRecyclerView.findChildViewUnder(this.mX, Math.max(this.mY - i, 0.0f));
            if (viewFindChildViewUnder == null || viewFindChildViewUnder.getTag(R.id.miuix_navigator_drag_helper_footer) != null) {
                if (this.mInsertedAdapter == null) {
                    cleanUp();
                    return;
                }
                return;
            }
            z = false;
        }
        handleDragLocationStep2(viewFindChildViewUnder, z, i);
    }

    private void handleDragLocationStep2(View view, boolean z, int i) {
        CategoryAdapterWrapper categoryAdapterWrapper = (CategoryAdapterWrapper) view.getTag(R.id.miuix_navigator_drag_helper_token);
        if (categoryAdapterWrapper == null) {
            cleanUp();
            return;
        }
        if (!categoryAdapterWrapper.isExpanded()) {
            if (view.getId() == R.id.navigation_item_category && z) {
                handleHoverOnViewUnchecked(view);
                return;
            } else {
                cleanUp();
                return;
            }
        }
        DragStartFeedback dragStartFeedback = this.mFeedbackMap.get(categoryAdapterWrapper);
        if (dragStartFeedback == null) {
            cleanUp();
        } else {
            this.mLastAdapter = categoryAdapterWrapper;
            handleDragLocationStep3(view, z, i, dragStartFeedback);
        }
    }

    private void handleDragLocationStep3(View view, boolean z, int i, DragStartFeedback dragStartFeedback) {
        boolean z2 = view.getTag(R.id.miuix_navigator_drag_helper_footer) != null;
        boolean z3 = dragStartFeedback.canAccept() && this.mLastAdapter.hasAliveItems();
        boolean zCanInsert = dragStartFeedback.canInsert();
        if (!z3 && !zCanInsert) {
            cleanUp();
            return;
        }
        if (!z3) {
            handleInsertPlaceholder(view, !z2 && this.mY > ((float) ((view.getTop() + view.getBottom()) / 2)));
            return;
        }
        if (!zCanInsert) {
            if (z) {
                if (z2) {
                    view = null;
                }
                handleHoverOnView(view);
                return;
            }
            cleanUp();
            return;
        }
        int top = view.getTop() + i;
        int bottom = view.getBottom() - (this.mDragInsertZone - i);
        float f = top;
        float f2 = this.mY;
        if (f <= f2 && f2 <= bottom) {
            if (z2) {
                view = null;
            }
            handleHoverOnView(view);
            return;
        }
        handleInsertPlaceholder(view, !z2 && f2 > ((float) bottom));
    }

    private boolean handleDrop(DragEvent dragEvent) {
        CategoryAdapterWrapper categoryAdapterWrapper = this.mLastAdapter;
        int i = 0;
        if (categoryAdapterWrapper == null) {
            return false;
        }
        DragStartFeedback dragStartFeedback = this.mFeedbackMap.get(categoryAdapterWrapper);
        if (this.mHoverOnView != null && dragStartFeedback.canAccept()) {
            boolean zOnDropAccept = categoryAdapterWrapper.getNavigatorDragListener().onDropAccept(dragEvent, this.mRecyclerView.getChildViewHolder(this.mHoverOnView));
            this.mHoverOnView.setPressed(false);
            this.mHoverOnView = null;
            this.mRecyclerView.removeCallbacks(this.mPressRunnable);
            return zOnDropAccept;
        }
        if (!dragStartFeedback.canInsert()) {
            return false;
        }
        CategoryAdapterWrapper categoryAdapterWrapper2 = this.mInsertedAdapter;
        if (categoryAdapterWrapper2 != null) {
            categoryAdapterWrapper2.preReplaceDragPlaceholder();
            this.mInsertedAdapter = null;
            i = this.mInsertedPosition;
        } else {
            categoryAdapterWrapper2 = this.mPendingInsertAdapter;
            if (categoryAdapterWrapper2 != null) {
                this.mPendingInsertAdapter = null;
                i = this.mPendingInsertPosition;
                this.mRecyclerView.removeCallbacks(this.mInsertPlaceholderRunnable);
            }
            boolean zOnDropInsert = categoryAdapterWrapper.getNavigatorDragListener().onDropInsert(dragEvent, categoryAdapterWrapper.getAdapter(), i);
            categoryAdapterWrapper.postReplaceDragPlaceholder(zOnDropInsert);
            return zOnDropInsert;
        }
        categoryAdapterWrapper = categoryAdapterWrapper2;
        boolean zOnDropInsert2 = categoryAdapterWrapper.getNavigatorDragListener().onDropInsert(dragEvent, categoryAdapterWrapper.getAdapter(), i);
        categoryAdapterWrapper.postReplaceDragPlaceholder(zOnDropInsert2);
        return zOnDropInsert2;
    }

    private void handleDragExit() {
        if (this.mX == -1.0f) {
            return;
        }
        this.mX = -1.0f;
        this.mY = -1.0f;
        this.mLastAdapter = null;
        this.mMaskHelper.setMask(false);
        cleanUp();
    }

    private void handleDragEnd() {
        this.mFeedbackMap = null;
        handleDragExit();
    }

    private void cleanUp() {
        cleanUp(true);
    }

    private void cleanUp(boolean z) {
        View view = this.mHoverOnView;
        if (view != null) {
            view.setPressed(false);
            this.mHoverOnView = null;
        }
        this.mRecyclerView.removeCallbacks(this.mPressRunnable);
        this.mPendingInsertAdapter = null;
        this.mRecyclerView.removeCallbacks(this.mInsertPlaceholderRunnable);
        if (z) {
            cleanDragPlaceholder();
        }
    }

    private void scrollIfNecessary() {
        this.mRect.set(0.0f, 0.0f, this.mRecyclerView.getWidth(), this.mScrollZone);
        if (this.mRect.contains(this.mX, this.mY) && this.mRecyclerView.canScrollVertically(-1)) {
            startScroll((int) (this.mY - this.mScrollZone));
            return;
        }
        this.mRect.set(0.0f, this.mRecyclerView.getHeight() - this.mScrollZone, this.mRecyclerView.getWidth(), this.mRecyclerView.getHeight());
        if (this.mRect.contains(this.mX, this.mY) && this.mRecyclerView.canScrollVertically(1)) {
            startScroll((int) (this.mY - (this.mRecyclerView.getHeight() - this.mScrollZone)));
        } else {
            this.mRecyclerView.removeCallbacks(this.mScrollRunnable);
            this.mMsStartScroll = -1L;
        }
    }

    private void startScroll(int i) {
        this.mDelta = i;
        this.mRecyclerView.removeCallbacks(this.mScrollRunnable);
        this.mRecyclerView.postOnAnimation(this.mScrollRunnable);
    }

    private void handleHoverOnView(View view) {
        if (view != null && (view.getId() == R.id.navigation_item_category || view.getId() == R.id.navigation_item_drag_placeholder)) {
            view = null;
        }
        handleHoverOnViewUnchecked(view);
    }

    private void handleHoverOnViewUnchecked(View view) {
        if (this.mHoverOnView == view) {
            return;
        }
        cleanUp(false);
        this.mHoverOnView = view;
        if (view != null) {
            view.setPressed(true);
            this.mRecyclerView.postOnAnimationDelayed(this.mPressRunnable, 1000L);
        }
    }

    private void handleInsertPlaceholder(View view, boolean z) {
        int bindingAdapterPosition;
        CategoryAdapterWrapper categoryAdapterWrapper;
        CategoryAdapter.EditConfig editConfig = this.mLastAdapter.getAdapter().getEditConfig();
        if (!editConfig.isEditable() || !editConfig.multiChoiceMode()) {
            cleanUp();
            return;
        }
        if (view.getId() == R.id.navigation_item_drag_placeholder) {
            if (this.mLastAdapter == this.mInsertedAdapter) {
                cleanUp(false);
                return;
            }
        } else {
            if (view.getId() != R.id.navigation_item_category) {
                bindingAdapterPosition = this.mRecyclerView.getChildViewHolder(view).getBindingAdapterPosition();
                if (z) {
                    bindingAdapterPosition++;
                }
            }
            categoryAdapterWrapper = this.mLastAdapter;
            if (categoryAdapterWrapper != this.mInsertedAdapter && bindingAdapterPosition == this.mInsertedPosition) {
                cleanUp(false);
                return;
            }
            if (categoryAdapterWrapper == this.mPendingInsertAdapter || bindingAdapterPosition != this.mPendingInsertPosition) {
                cleanUp(false);
                this.mPendingInsertAdapter = this.mLastAdapter;
                this.mPendingInsertPosition = bindingAdapterPosition;
                this.mRecyclerView.removeCallbacks(this.mInsertPlaceholderRunnable);
                this.mRecyclerView.postOnAnimation(this.mInsertPlaceholderRunnable);
            }
            return;
        }
        bindingAdapterPosition = 0;
        categoryAdapterWrapper = this.mLastAdapter;
        if (categoryAdapterWrapper != this.mInsertedAdapter) {
        }
        if (categoryAdapterWrapper == this.mPendingInsertAdapter) {
        }
        cleanUp(false);
        this.mPendingInsertAdapter = this.mLastAdapter;
        this.mPendingInsertPosition = bindingAdapterPosition;
        this.mRecyclerView.removeCallbacks(this.mInsertPlaceholderRunnable);
        this.mRecyclerView.postOnAnimation(this.mInsertPlaceholderRunnable);
    }

    private void cleanDragPlaceholder() {
        this.mRecyclerView.postOnAnimation(this.mRemovePlaceholderRunnable);
    }

    /* JADX INFO: renamed from: lambda$new$2$miuix-navigator-draganddrop-DragHelper, reason: not valid java name */
    /* synthetic */ void m1911lambda$new$2$miuixnavigatordraganddropDragHelper() {
        View view = this.mHoverOnView;
        if (view == null || this.mRecyclerView == null || view.getParent() != this.mRecyclerView) {
            return;
        }
        CategoryAdapterWrapper categoryAdapterWrapper = this.mLastAdapter;
        NavigatorDragListener navigatorDragListener = categoryAdapterWrapper == null ? null : categoryAdapterWrapper.getNavigatorDragListener();
        if (navigatorDragListener != null) {
            RecyclerView.ViewHolder childViewHolder = this.mRecyclerView.getChildViewHolder(this.mHoverOnView);
            if (childViewHolder.getBindingAdapter() == this.mLastAdapter.getAdapter()) {
                navigatorDragListener.onDragHover(childViewHolder);
                return;
            }
        }
        if (this.mHoverOnView.isActivated()) {
            return;
        }
        this.mHoverOnView.performClick();
    }

    /* JADX INFO: renamed from: lambda$new$3$miuix-navigator-draganddrop-DragHelper, reason: not valid java name */
    /* synthetic */ void m1912lambda$new$3$miuixnavigatordraganddropDragHelper() {
        if (this.mPendingInsertAdapter == null) {
            return;
        }
        CategoryAdapterWrapper categoryAdapterWrapper = this.mInsertedAdapter;
        if (categoryAdapterWrapper != null) {
            categoryAdapterWrapper.removeDragPlaceholder();
        }
        CategoryAdapterWrapper categoryAdapterWrapper2 = this.mPendingInsertAdapter;
        this.mInsertedAdapter = categoryAdapterWrapper2;
        int i = this.mPendingInsertPosition;
        this.mInsertedPosition = i;
        this.mPendingInsertAdapter = null;
        categoryAdapterWrapper2.insertDragPlaceholder(i);
    }

    /* JADX INFO: renamed from: lambda$new$4$miuix-navigator-draganddrop-DragHelper, reason: not valid java name */
    /* synthetic */ void m1913lambda$new$4$miuixnavigatordraganddropDragHelper() {
        CategoryAdapterWrapper categoryAdapterWrapper = this.mInsertedAdapter;
        if (categoryAdapterWrapper != null) {
            categoryAdapterWrapper.removeDragPlaceholder();
            this.mInsertedAdapter = null;
        }
    }

    public class MaskHelper {
        private final AnimConfig mConfig;
        private boolean mMask;
        private final Paint mPaint;
        private Set<CategoryAdapterWrapper> mSkipSet;

        public boolean isEnabled() {
            return false;
        }

        private MaskHelper() {
            Paint paint = new Paint();
            this.mPaint = paint;
            this.mConfig = new AnimConfig().setEase(EaseManager.getStyle(4, 100.0f));
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            paint.setColor(ViewCompat.MEASURED_STATE_MASK);
            paint.setAlpha(0);
        }

        public void setAlpha(float f) {
            this.mPaint.setAlpha((int) (256.0f * f));
            DragHelper.this.mRecyclerView.invalidate();
            if (f == 0.0f) {
                this.mSkipSet = null;
            }
        }

        public float getAlpha() {
            return this.mPaint.getAlpha() / 256.0f;
        }

        void setMask(boolean z) {
            if (this.mMask == z) {
                return;
            }
            this.mMask = z;
            if (z) {
                this.mSkipSet = DragHelper.this.mFeedbackMap.keySet();
            }
            Folme.useValue(this).add(DragHelper.ALPHA, z ? 0.7f : 0.0f).to(this.mConfig);
        }
    }
}
