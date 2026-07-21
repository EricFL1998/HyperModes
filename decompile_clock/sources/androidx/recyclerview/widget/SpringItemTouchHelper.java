package androidx.recyclerview.widget;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import miuix.animation.Folme;
import miuix.animation.utils.EaseManager;
import miuix.device.DeviceUtils;
import miuix.reflect.Reflects;

/* JADX INFO: loaded from: classes.dex */
public class SpringItemTouchHelper extends ItemTouchHelper {
    private static final TimeInterpolator INTERPOLATOR = new EaseManager.SpringInterpolator().setDamping(0.95f).setResponse(0.3f);
    boolean mSpringEnabled;
    private final float[] mTmpPosition;
    private final boolean mUseFolmeRecoverAnimation;

    public SpringItemTouchHelper(ItemTouchHelper.Callback callback) {
        super(callback);
        this.mTmpPosition = new float[2];
        this.mUseFolmeRecoverAnimation = (DeviceUtils.isMiuiLiteV2() || DeviceUtils.isLiteV1StockPlus()) ? false : true;
    }

    @Override // androidx.recyclerview.widget.ItemTouchHelper
    void select(RecyclerView.ViewHolder viewHolder, int i) {
        boolean z = false;
        if (i == 2) {
            if (viewHolder != null && !Folme.isInDraggingState(this.mRecyclerView)) {
                Folme.setDraggingState(this.mRecyclerView, true);
                onStartDrag(viewHolder);
            }
        } else if (i == 0 && Folme.isInDraggingState(this.mRecyclerView)) {
            Folme.setDraggingState(this.mRecyclerView, false);
            if (this.mSelected != null) {
                onStopDrag(this.mSelected);
                z = this.mUseFolmeRecoverAnimation;
            }
        }
        if (z) {
            super_select_overwrite();
        } else {
            super.select(viewHolder, i);
        }
    }

    private void releaseVelocityTracker() {
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    private void super_select_overwrite() {
        boolean z;
        Reflects.set(this, Reflects.getDeclaredField((Class<?>) ItemTouchHelper.class, "mDragScrollStartTimeInMs"), Long.MIN_VALUE);
        endRecoverAnimation(null, true);
        Reflects.set(this, Reflects.getDeclaredField((Class<?>) ItemTouchHelper.class, "mActionState"), 0);
        final RecyclerView.ViewHolder viewHolder = this.mSelected;
        if (viewHolder.itemView.getParent() != null) {
            releaseVelocityTracker();
            Reflects.invoke(this, Reflects.getDeclaredMethod((Class<?>) ItemTouchHelper.class, "getSelectedDxDy", (Class<?>[]) new Class[]{float[].class}), this.mTmpPosition);
            float[] fArr = this.mTmpPosition;
            ItemTouchHelper.RecoverAnimation recoverAnimation = new ItemTouchHelper.RecoverAnimation(viewHolder, 8, 2, fArr[0], fArr[1], 0.0f, 0.0f) { // from class: androidx.recyclerview.widget.SpringItemTouchHelper.1
                @Override // androidx.recyclerview.widget.ItemTouchHelper.RecoverAnimation, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    if (this.mOverridden) {
                        return;
                    }
                    SpringItemTouchHelper.this.mCallback.clearView(SpringItemTouchHelper.this.mRecyclerView, viewHolder);
                    if (SpringItemTouchHelper.this.mOverdrawChild == viewHolder.itemView) {
                        SpringItemTouchHelper.this.removeChildDrawingOrderCallbackIfNecessary(viewHolder.itemView);
                    }
                }
            };
            recoverAnimation.mValueAnimator.setInterpolator(INTERPOLATOR);
            this.mRecoverAnimations.add(recoverAnimation);
            recoverAnimation.start();
            z = true;
        } else {
            removeChildDrawingOrderCallbackIfNecessary(viewHolder.itemView);
            this.mCallback.clearView(this.mRecyclerView, viewHolder);
            z = false;
        }
        this.mSelected = null;
        ViewParent parent = this.mRecyclerView.getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(this.mSelected != null);
        }
        if (!z) {
            this.mRecyclerView.getLayoutManager().requestSimpleAnimationsInNextLayout();
        }
        this.mCallback.onSelectedChanged(this.mSelected, 0);
        this.mRecyclerView.invalidate();
    }

    @Override // androidx.recyclerview.widget.ItemTouchHelper
    View findChildView(MotionEvent motionEvent) {
        if (this.mSelected == null) {
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            for (int size = this.mRecoverAnimations.size() - 1; size >= 0; size--) {
                ItemTouchHelper.RecoverAnimation recoverAnimation = this.mRecoverAnimations.get(size);
                View view = recoverAnimation.mViewHolder.itemView;
                if (hitTest(view, x, y, view.getX() + recoverAnimation.mX, view.getY() + recoverAnimation.mY)) {
                    return view;
                }
            }
            return this.mRecyclerView.findChildViewUnder(x, y);
        }
        return super.findChildView(motionEvent);
    }

    private static boolean hitTest(View view, float f, float f2, float f3, float f4) {
        return f >= f3 && f <= f3 + ((float) view.getWidth()) && f2 >= f4 && f2 <= f4 + ((float) view.getHeight());
    }

    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        if (this.mRecyclerView instanceof miuix.recyclerview.widget.RecyclerView) {
            this.mSpringEnabled = ((miuix.recyclerview.widget.RecyclerView) this.mRecyclerView).getSpringEnabled();
            ((miuix.recyclerview.widget.RecyclerView) this.mRecyclerView).setSpringEnabled(false);
        }
    }

    public void onStopDrag(RecyclerView.ViewHolder viewHolder) {
        if (this.mRecyclerView instanceof miuix.recyclerview.widget.RecyclerView) {
            ((miuix.recyclerview.widget.RecyclerView) this.mRecyclerView).setSpringEnabled(this.mSpringEnabled);
        }
    }
}
