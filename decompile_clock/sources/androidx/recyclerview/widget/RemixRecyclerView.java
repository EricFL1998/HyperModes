package androidx.recyclerview.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import androidx.core.view.MotionEventCompat;
import miuix.animation.utils.VelocityMonitor;
import miuix.core.view.NestedCurrentFling;
import miuix.overscroller.widget.AnimationHelper;
import miuix.overscroller.widget.OverScroller;
import miuix.recyclerview.R;
import miuix.util.HapticFeedbackCompat;
import miuix.view.HapticCompat;
import miuix.view.HapticFeedbackConstants;

/* JADX INFO: loaded from: classes.dex */
abstract class RemixRecyclerView extends RecyclerView {
    private static final int INVALID_POINTER = -1;
    private static final int MAX_POINTER_COUNT = 5;
    private final int mMaxFlingVelocity;
    private boolean mMouseEvent;
    private long mMouseEventTime;
    private int mScrollPointerId;
    private boolean mSpringEnabled;
    private final VelocityMonitor[] mVelocityMonitor;

    protected boolean isOverScrolling() {
        return false;
    }

    public RemixRecyclerView(Context context) {
        this(context, null);
    }

    public RemixRecyclerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.recyclerViewStyle);
    }

    public RemixRecyclerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mVelocityMonitor = new VelocityMonitor[5];
        this.mScrollPointerId = -1;
        this.mSpringEnabled = true;
        this.mMouseEvent = false;
        this.mMouseEventTime = 0L;
        this.mMaxFlingVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
    }

    @Override // androidx.recyclerview.widget.RecyclerView, android.view.View
    public boolean onGenericMotionEvent(MotionEvent motionEvent) {
        boolean zIsFromSource = MotionEventCompat.isFromSource(motionEvent, 8194);
        this.mMouseEvent = zIsFromSource;
        if (zIsFromSource) {
            this.mMouseEventTime = System.currentTimeMillis();
        }
        return super.onGenericMotionEvent(motionEvent);
    }

    @Override // android.view.View
    public void setOverScrollMode(int i) {
        super.setOverScrollMode(i);
        if (i == 2) {
            this.mSpringEnabled = false;
        }
    }

    public void setSpringEnabled(boolean z) {
        this.mSpringEnabled = z;
    }

    public boolean getSpringEnabled() {
        return this.mSpringEnabled && (!this.mMouseEvent || (((System.currentTimeMillis() - this.mMouseEventTime) > 10L ? 1 : ((System.currentTimeMillis() - this.mMouseEventTime) == 10L ? 0 : -1)) > 0));
    }

    @Override // androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        trackVelocity(motionEvent);
        return super.onInterceptTouchEvent(motionEvent);
    }

    private void trackVelocity(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        int actionIndex = motionEvent.getActionIndex();
        if (actionMasked == 0) {
            this.mScrollPointerId = motionEvent.getPointerId(0);
            resetVelocity(motionEvent, actionIndex);
            updateVelocity(motionEvent, actionIndex);
        } else if (actionMasked != 2) {
            if (actionMasked != 5) {
                return;
            }
            this.mScrollPointerId = motionEvent.getPointerId(actionIndex);
            resetVelocity(motionEvent, actionIndex);
            updateVelocity(motionEvent, actionIndex);
        } else {
            for (int i = 0; i < motionEvent.getPointerCount(); i++) {
                updateVelocity(motionEvent, i);
            }
        }
    }

    private void resetVelocity(MotionEvent motionEvent, int i) {
        int pointerId = motionEvent.getPointerId(i) % 5;
        checkVelocityMonitor(pointerId);
        this.mVelocityMonitor[pointerId].clear();
    }

    private void updateVelocity(MotionEvent motionEvent, int i) {
        int pointerId = motionEvent.getPointerId(i) % 5;
        checkVelocityMonitor(pointerId);
        if (Build.VERSION.SDK_INT >= 29) {
            this.mVelocityMonitor[pointerId].update(motionEvent.getRawX(i), motionEvent.getRawY(i));
        } else {
            this.mVelocityMonitor[pointerId].update(motionEvent.getRawX(), motionEvent.getRawY());
        }
    }

    protected float getVelocityFromMonitor(int i) {
        int i2 = this.mScrollPointerId;
        if (i2 == -1) {
            return 0.0f;
        }
        int i3 = i2 % 5;
        checkVelocityMonitor(i3);
        return this.mVelocityMonitor[i3].getVelocity(i);
    }

    private void checkVelocityMonitor(int i) {
        VelocityMonitor[] velocityMonitorArr = this.mVelocityMonitor;
        if (velocityMonitorArr[i] == null) {
            velocityMonitorArr[i] = new VelocityMonitor();
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean zIsFromSource = MotionEventCompat.isFromSource(motionEvent, 8194);
        this.mMouseEvent = zIsFromSource;
        if (zIsFromSource) {
            this.mMouseEventTime = System.currentTimeMillis();
        }
        trackVelocity(motionEvent);
        return super.onTouchEvent(motionEvent);
    }

    class ViewFlinger extends RecyclerView.ViewFlinger {
        int mCurrentFlingVelocityX;
        int mCurrentFlingVelocityY;
        int mDragFlingVelocityX;
        int mDragFlingVelocityY;
        private boolean mEatRunOnAnimationRequest;
        private HapticFeedbackCompat mHapticFeedbackCompat;
        private boolean mHasReachEdgeBeforeFling;
        boolean mInterimTarget;
        Interpolator mInterpolator;
        private int mLastFlingX;
        private int mLastFlingY;
        OverScroller mOverScroller;
        private boolean mReSchedulePostAnimationCallback;

        protected void checkDoneScrolling() {
        }

        ViewFlinger() {
            super();
            this.mInterpolator = RecyclerView.sQuinticInterpolator;
            this.mEatRunOnAnimationRequest = false;
            this.mReSchedulePostAnimationCallback = false;
            this.mCurrentFlingVelocityX = 0;
            this.mCurrentFlingVelocityY = 0;
            this.mDragFlingVelocityX = 0;
            this.mDragFlingVelocityY = 0;
            this.mInterimTarget = false;
            this.mOverScroller = new OverScroller(RemixRecyclerView.this.getContext(), RecyclerView.sQuinticInterpolator);
        }

        /* JADX WARN: Code duplicated, block: B:120:0x024a  */
        /* JADX WARN: Code duplicated, block: B:122:0x0252  */
        /* JADX WARN: Code duplicated, block: B:123:0x025c  */
        /* JADX WARN: Code duplicated, block: B:126:0x0267  */
        @Override // androidx.recyclerview.widget.RecyclerView.ViewFlinger, java.lang.Runnable
        public void run() {
            int i;
            int i2;
            boolean z;
            boolean zCanScrollHorizontally;
            if (RemixRecyclerView.this.mLayout == null) {
                stop();
                return;
            }
            this.mReSchedulePostAnimationCallback = false;
            this.mEatRunOnAnimationRequest = true;
            RemixRecyclerView.this.consumePendingUpdateOperations();
            OverScroller overScroller = this.mOverScroller;
            if (overScroller.computeScrollOffset()) {
                int currX = overScroller.getCurrX();
                int currY = overScroller.getCurrY();
                if (this.mOverScroller.getMode() == 1) {
                    this.mCurrentFlingVelocityX = (int) overScroller.getCurrVelocityX();
                    this.mCurrentFlingVelocityY = (int) overScroller.getCurrVelocityY();
                }
                if (!RemixRecyclerView.this.isOverScrolling()) {
                    this.mDragFlingVelocityX = (int) overScroller.getCurrVelocityX();
                    this.mDragFlingVelocityY = (int) overScroller.getCurrVelocityY();
                }
                int i3 = currX - this.mLastFlingX;
                int i4 = currY - this.mLastFlingY;
                this.mLastFlingX = currX;
                this.mLastFlingY = currY;
                RemixRecyclerView.this.mReusableIntPair[0] = 0;
                RemixRecyclerView.this.mReusableIntPair[1] = 0;
                if (this.mOverScroller.getMode() == 1) {
                    View viewFindViewById = RemixRecyclerView.this.getRootView().findViewById(android.R.id.content);
                    for (ViewParent parent = RemixRecyclerView.this.getParent(); parent != null && ((!(parent instanceof NestedCurrentFling) || !((NestedCurrentFling) parent).onNestedCurrentFling(this.mOverScroller.getCurrVelocityX(), this.mOverScroller.getCurrVelocityY())) && (!(parent instanceof ViewGroup) || parent != viewFindViewById)); parent = parent.getParent()) {
                    }
                }
                RemixRecyclerView remixRecyclerView = RemixRecyclerView.this;
                if (remixRecyclerView.dispatchNestedPreScroll(i3, i4, remixRecyclerView.mReusableIntPair, null, 1)) {
                    i3 -= RemixRecyclerView.this.mReusableIntPair[0];
                    i4 -= RemixRecyclerView.this.mReusableIntPair[1];
                }
                if (RemixRecyclerView.this.getOverScrollMode() != 2) {
                    RemixRecyclerView.this.considerReleasingGlowsOnScroll(i3, i4);
                }
                if (RemixRecyclerView.this.mAdapter != null) {
                    RemixRecyclerView.this.mReusableIntPair[0] = 0;
                    RemixRecyclerView.this.mReusableIntPair[1] = 0;
                    RemixRecyclerView remixRecyclerView2 = RemixRecyclerView.this;
                    remixRecyclerView2.scrollStep(i3, i4, remixRecyclerView2.mReusableIntPair);
                    i = RemixRecyclerView.this.mReusableIntPair[0];
                    i2 = RemixRecyclerView.this.mReusableIntPair[1];
                    i3 -= i;
                    i4 -= i2;
                    RecyclerView.SmoothScroller smoothScroller = RemixRecyclerView.this.mLayout.mSmoothScroller;
                    if (smoothScroller != null && !smoothScroller.isPendingInitialRun() && smoothScroller.isRunning()) {
                        int itemCount = RemixRecyclerView.this.mState.getItemCount();
                        if (itemCount == 0) {
                            smoothScroller.stop();
                        } else if (smoothScroller.getTargetPosition() >= itemCount) {
                            smoothScroller.setTargetPosition(itemCount - 1);
                            smoothScroller.onAnimation(i, i2);
                        } else {
                            smoothScroller.onAnimation(i, i2);
                        }
                    }
                } else {
                    i = 0;
                    i2 = 0;
                }
                if (!RemixRecyclerView.this.mItemDecorations.isEmpty()) {
                    RemixRecyclerView.this.invalidate();
                }
                RemixRecyclerView.this.mReusableIntPair[0] = 0;
                RemixRecyclerView.this.mReusableIntPair[1] = 0;
                RemixRecyclerView remixRecyclerView3 = RemixRecyclerView.this;
                remixRecyclerView3.dispatchNestedScroll(i, i2, i3, i4, null, 1, remixRecyclerView3.mReusableIntPair);
                int i5 = i3 - RemixRecyclerView.this.mReusableIntPair[0];
                int i6 = i4 - RemixRecyclerView.this.mReusableIntPair[1];
                if (i != 0 || i2 != 0) {
                    RemixRecyclerView.this.dispatchOnScrolled(i, i2);
                }
                if (!RemixRecyclerView.this.awakenScrollBars()) {
                    RemixRecyclerView.this.invalidate();
                }
                boolean z2 = overScroller.isFinished() || (((overScroller.getCurrX() == overScroller.getFinalX()) || i5 != 0) && ((overScroller.getCurrY() == overScroller.getFinalY()) || i6 != 0));
                RecyclerView.SmoothScroller smoothScroller2 = RemixRecyclerView.this.mLayout.mSmoothScroller;
                if ((smoothScroller2 == null || !smoothScroller2.isPendingInitialRun()) && z2) {
                    if (RemixRecyclerView.this.getOverScrollMode() != 2) {
                        int currVelocity = (int) overScroller.getCurrVelocity();
                        int i7 = i5 < 0 ? -currVelocity : i5 > 0 ? currVelocity : 0;
                        if (i6 < 0) {
                            currVelocity = -currVelocity;
                        } else if (i6 <= 0) {
                            currVelocity = 0;
                        }
                        RemixRecyclerView.this.absorbGlows(i7, currVelocity);
                    }
                    if (RemixRecyclerView.this.mLayout.canScrollVertically()) {
                        zCanScrollHorizontally = RemixRecyclerView.this.canScrollVertically(this.mOverScroller.getFinalY() > this.mOverScroller.getStartY() ? 1 : -1);
                    } else {
                        if (RemixRecyclerView.this.mLayout.canScrollHorizontally()) {
                            zCanScrollHorizontally = RemixRecyclerView.this.canScrollHorizontally(this.mOverScroller.getFinalX() > this.mOverScroller.getStartX() ? 1 : -1);
                        } else {
                            z = false;
                        }
                        if (!RemixRecyclerView.this.mSpringEnabled && this.mOverScroller.getMode() == 1 && !this.mHasReachEdgeBeforeFling && z) {
                            if (HapticCompat.doesSupportHaptic(HapticCompat.HapticVersion.HAPTIC_VERSION_2)) {
                                if (RemixRecyclerView.this.isHapticFeedbackEnabled()) {
                                    getHapticFeedbackCompat().performExtHapticFeedbackAsync(201);
                                }
                            } else {
                                HapticCompat.performHapticFeedbackAsync(RemixRecyclerView.this, HapticFeedbackConstants.MIUI_SCROLL_EDGE);
                            }
                        }
                        if (RecyclerView.ALLOW_THREAD_GAP_WORK) {
                            RemixRecyclerView.this.mPrefetchRegistry.clearPrefetchPositions();
                        }
                        checkDoneScrolling();
                    }
                    z = !zCanScrollHorizontally;
                    if (!RemixRecyclerView.this.mSpringEnabled) {
                        if (HapticCompat.doesSupportHaptic(HapticCompat.HapticVersion.HAPTIC_VERSION_2)) {
                            if (RemixRecyclerView.this.isHapticFeedbackEnabled()) {
                                getHapticFeedbackCompat().performExtHapticFeedbackAsync(201);
                            }
                        } else {
                            HapticCompat.performHapticFeedbackAsync(RemixRecyclerView.this, HapticFeedbackConstants.MIUI_SCROLL_EDGE);
                        }
                    }
                    if (RecyclerView.ALLOW_THREAD_GAP_WORK) {
                        RemixRecyclerView.this.mPrefetchRegistry.clearPrefetchPositions();
                    }
                    checkDoneScrolling();
                } else {
                    postOnAnimation();
                    if (RemixRecyclerView.this.mGapWorker != null) {
                        RemixRecyclerView.this.mGapWorker.postFromTraversal(RemixRecyclerView.this, i, i2);
                    }
                }
            }
            RecyclerView.SmoothScroller smoothScroller3 = RemixRecyclerView.this.mLayout.mSmoothScroller;
            if (smoothScroller3 != null && smoothScroller3.isPendingInitialRun()) {
                smoothScroller3.onAnimation(0, 0);
            }
            this.mEatRunOnAnimationRequest = false;
            if (this.mReSchedulePostAnimationCallback) {
                internalPostOnAnimation();
                return;
            }
            RemixRecyclerView.this.setScrollState(0);
            RemixRecyclerView.this.stopNestedScroll(1);
            this.mCurrentFlingVelocityY = 0;
            this.mCurrentFlingVelocityX = 0;
            this.mDragFlingVelocityY = 0;
            this.mDragFlingVelocityX = 0;
        }

        private HapticFeedbackCompat getHapticFeedbackCompat() {
            if (this.mHapticFeedbackCompat == null) {
                this.mHapticFeedbackCompat = new HapticFeedbackCompat(RemixRecyclerView.this.getContext());
            }
            return this.mHapticFeedbackCompat;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.ViewFlinger
        void postOnAnimation() {
            if (this.mEatRunOnAnimationRequest) {
                this.mReSchedulePostAnimationCallback = true;
            } else {
                internalPostOnAnimation();
            }
        }

        private void internalPostOnAnimation() {
            RemixRecyclerView.this.removeCallbacks(this);
            AnimationHelper.postOnAnimation(RemixRecyclerView.this, this);
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r13v19 */
        /* JADX WARN: Type inference failed for: r13v20 */
        /* JADX WARN: Type inference failed for: r13v9 */
        @Override // androidx.recyclerview.widget.RecyclerView.ViewFlinger
        public void fling(int i, int i2) {
            RemixRecyclerView.this.setScrollState(2);
            this.mLastFlingY = 0;
            this.mLastFlingX = 0;
            if (this.mInterpolator != RecyclerView.sQuinticInterpolator) {
                this.mInterpolator = RecyclerView.sQuinticInterpolator;
                this.mOverScroller = new OverScroller(RemixRecyclerView.this.getContext(), RecyclerView.sQuinticInterpolator);
            }
            int i3 = i != 0 ? -((int) RemixRecyclerView.this.getVelocityFromMonitor(0)) : i;
            int i4 = i2 != 0 ? -((int) RemixRecyclerView.this.getVelocityFromMonitor(1)) : i2;
            if (i3 != 0) {
                i = i3;
            }
            if (i4 != 0) {
                i2 = i4;
            }
            int iMax = Math.max(-RemixRecyclerView.this.mMaxFlingVelocity, Math.min(i, RemixRecyclerView.this.mMaxFlingVelocity));
            int iMax2 = Math.max(-RemixRecyclerView.this.mMaxFlingVelocity, Math.min(i2, RemixRecyclerView.this.mMaxFlingVelocity));
            boolean zCanScrollHorizontally = RemixRecyclerView.this.mLayout.canScrollHorizontally();
            ?? r13 = zCanScrollHorizontally;
            if (RemixRecyclerView.this.mLayout.canScrollVertically()) {
                r13 = (zCanScrollHorizontally ? 1 : 0) | 2;
            }
            if (r13 == 2) {
                this.mHasReachEdgeBeforeFling = !RemixRecyclerView.this.canScrollVertically(iMax2 > 0 ? 1 : -1);
            } else if (r13 == 1) {
                this.mHasReachEdgeBeforeFling = !RemixRecyclerView.this.canScrollHorizontally(iMax > 0 ? 1 : -1);
            }
            this.mOverScroller.fling(0, 0, iMax, iMax2, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
            postOnAnimation();
        }

        /* JADX WARN: Code duplicated, block: B:22:0x0065  */
        @Override // androidx.recyclerview.widget.RecyclerView.ViewFlinger
        public void smoothScrollBy(int i, int i2, int i3, Interpolator interpolator) {
            boolean z;
            if (RemixRecyclerView.this.isOverScrolling()) {
                return;
            }
            if (i3 == Integer.MIN_VALUE) {
                computeScrollDuration(i, i2, 0, 0);
            }
            if (interpolator == null) {
                interpolator = RecyclerView.sQuinticInterpolator;
            }
            if (this.mOverScroller.getMode() == 2 && !this.mInterimTarget) {
                this.mCurrentFlingVelocityY = (int) this.mOverScroller.getCurrVelocityY();
                this.mCurrentFlingVelocityX = (int) this.mOverScroller.getCurrVelocityX();
            }
            if (RemixRecyclerView.this.mLayout.mSmoothScroller instanceof LinearSmoothScroller) {
                float f = ((LinearSmoothScroller) RemixRecyclerView.this.mLayout.mSmoothScroller).mInterimTargetDx * 1.2f;
                float f2 = ((LinearSmoothScroller) RemixRecyclerView.this.mLayout.mSmoothScroller).mInterimTargetDy * 1.2f;
                if (f == i && f2 == i2) {
                    z = true;
                } else {
                    z = false;
                }
            } else {
                z = false;
            }
            this.mInterimTarget = z;
            if (this.mInterpolator != interpolator) {
                this.mInterpolator = interpolator;
                this.mOverScroller = new OverScroller(RemixRecyclerView.this.getContext(), interpolator);
            }
            this.mLastFlingY = 0;
            this.mLastFlingX = 0;
            RemixRecyclerView.this.setScrollState(2);
            this.mOverScroller.startScrollByFling(0, 0, i, i2, this.mCurrentFlingVelocityX, this.mCurrentFlingVelocityY);
            postOnAnimation();
        }

        private float distanceInfluenceForSnapDuration(float f) {
            return (float) Math.sin((f - 0.5f) * 0.47123894f);
        }

        private int computeScrollDuration(int i, int i2, int i3, int i4) {
            int iRound;
            int iAbs = Math.abs(i);
            int iAbs2 = Math.abs(i2);
            boolean z = iAbs > iAbs2;
            int iSqrt = (int) Math.sqrt((i3 * i3) + (i4 * i4));
            int iSqrt2 = (int) Math.sqrt((i * i) + (i2 * i2));
            RemixRecyclerView remixRecyclerView = RemixRecyclerView.this;
            int width = z ? remixRecyclerView.getWidth() : remixRecyclerView.getHeight();
            int i5 = width / 2;
            float f = width;
            float f2 = i5;
            float fDistanceInfluenceForSnapDuration = f2 + (distanceInfluenceForSnapDuration(Math.min(1.0f, (iSqrt2 * 1.0f) / f)) * f2);
            if (iSqrt > 0) {
                iRound = Math.round(Math.abs(fDistanceInfluenceForSnapDuration / iSqrt) * 1000.0f) * 4;
            } else {
                if (!z) {
                    iAbs = iAbs2;
                }
                iRound = (int) (((iAbs / f) + 1.0f) * 300.0f);
            }
            return Math.min(iRound, 2000);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.ViewFlinger
        public void stop() {
            RemixRecyclerView.this.removeCallbacks(this);
            this.mOverScroller.abortAnimation();
        }

        void resetFlingPosition() {
            this.mLastFlingY = 0;
            this.mLastFlingX = 0;
            this.mOverScroller.resetPosition();
        }
    }
}
