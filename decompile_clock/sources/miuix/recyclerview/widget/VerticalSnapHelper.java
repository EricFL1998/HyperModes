package miuix.recyclerview.widget;

import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.Collection;
import miuix.animation.base.AnimConfig;
import miuix.animation.controller.AnimState;
import miuix.animation.listener.TransitionListener;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.property.IntValueProperty;

/* JADX INFO: loaded from: classes3.dex */
public class VerticalSnapHelper extends SpringSnapHelper {
    @Override // miuix.recyclerview.widget.SpringSnapHelper
    protected void init() {
        super.init();
        this.mProperty = new IntValueProperty("scrollY", 0.2f) { // from class: miuix.recyclerview.widget.VerticalSnapHelper.1
            @Override // miuix.animation.property.IntValueProperty, miuix.animation.property.IIntValueProperty
            public void setIntValue(Object obj, int i) {
                int i2 = VerticalSnapHelper.this.mMin;
                int i3 = VerticalSnapHelper.this.mMax;
                VerticalSnapHelper.this.mCurrentPosition = i;
                if (VerticalSnapHelper.this.mRecyclerView.getSpringEnabled()) {
                    int i4 = VerticalSnapHelper.this.mCurrentPosition - VerticalSnapHelper.this.mLastPosition;
                    if (VerticalSnapHelper.this.mCurrentPosition < i2 && VerticalSnapHelper.this.mLastPosition > i2) {
                        VerticalSnapHelper.this.mRecyclerView.scrollBy(i2 - VerticalSnapHelper.this.mLastPosition, 0);
                    } else if (VerticalSnapHelper.this.mCurrentPosition > i3 && VerticalSnapHelper.this.mLastPosition < i3) {
                        VerticalSnapHelper.this.mRecyclerView.scrollBy(i3 - VerticalSnapHelper.this.mLastPosition, 0);
                    } else if (VerticalSnapHelper.this.mCurrentPosition > i2 && VerticalSnapHelper.this.mLastPosition < i2) {
                        VerticalSnapHelper.this.mRecyclerView.scrollBy(VerticalSnapHelper.this.mCurrentPosition - i2, 0);
                    } else if (VerticalSnapHelper.this.mCurrentPosition < i3 && VerticalSnapHelper.this.mLastPosition > i3) {
                        VerticalSnapHelper.this.mRecyclerView.scrollBy(VerticalSnapHelper.this.mCurrentPosition - i3, 0);
                    } else if (VerticalSnapHelper.this.mCurrentPosition >= i2 && VerticalSnapHelper.this.mCurrentPosition <= i3 && VerticalSnapHelper.this.mLastPosition >= i2 && VerticalSnapHelper.this.mLastPosition <= i3) {
                        VerticalSnapHelper.this.mRecyclerView.scrollBy(0, i4);
                        VerticalSnapHelper.this.mSpringHelper.resetDistance();
                    }
                    if (VerticalSnapHelper.this.mCurrentPosition < i2 || VerticalSnapHelper.this.mCurrentPosition > i3) {
                        VerticalSnapHelper.this.mRecyclerView.dispatchNestedPreScroll(0, i4, null, null, 1);
                        VerticalSnapHelper.this.mRecyclerView.invalidate();
                        if (VerticalSnapHelper.this.mCurrentPosition < i2) {
                            VerticalSnapHelper verticalSnapHelper = VerticalSnapHelper.this;
                            verticalSnapHelper.setSpringVerticalDistance(verticalSnapHelper.mSpringHelper, VerticalSnapHelper.this.mCurrentPosition);
                        } else {
                            VerticalSnapHelper verticalSnapHelper2 = VerticalSnapHelper.this;
                            verticalSnapHelper2.setSpringVerticalDistance(verticalSnapHelper2.mSpringHelper, VerticalSnapHelper.this.mCurrentPosition - i3);
                        }
                    }
                } else {
                    VerticalSnapHelper verticalSnapHelper3 = VerticalSnapHelper.this;
                    verticalSnapHelper3.mCurrentPosition = Math.max(verticalSnapHelper3.mCurrentPosition, i2);
                    VerticalSnapHelper verticalSnapHelper4 = VerticalSnapHelper.this;
                    verticalSnapHelper4.mCurrentPosition = Math.min(verticalSnapHelper4.mCurrentPosition, i3);
                    VerticalSnapHelper.this.mRecyclerView.scrollBy(0, VerticalSnapHelper.this.mCurrentPosition - VerticalSnapHelper.this.mLastPosition);
                }
                VerticalSnapHelper verticalSnapHelper5 = VerticalSnapHelper.this;
                verticalSnapHelper5.mLastPosition = verticalSnapHelper5.mCurrentPosition;
            }

            @Override // miuix.animation.property.IntValueProperty, miuix.animation.property.IIntValueProperty
            public int getIntValue(Object obj) {
                return VerticalSnapHelper.this.mCurrentPosition;
            }
        };
    }

    @Override // miuix.recyclerview.widget.SpringSnapHelper
    void updateConstructData() {
        if (this.mItemHeight == Integer.MAX_VALUE) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) this.mRecyclerView.getLayoutManager();
            View viewFindViewByPosition = linearLayoutManager.findViewByPosition(linearLayoutManager.findFirstVisibleItemPosition());
            if (viewFindViewByPosition != null) {
                linearLayoutManager.getDecoratedBoundsWithMargins(viewFindViewByPosition, this.mBounds);
            }
            this.mItemHeight = this.mBounds.height();
        }
        int itemCount = this.mRecyclerView.getAdapter().getItemCount();
        this.mMin = 0;
        this.mMax = Math.max((itemCount * this.mItemHeight) - this.mRecyclerView.getHeight(), 0);
        int iComputeVerticalScrollOffset = this.mRecyclerView.computeVerticalScrollOffset() + this.mSpringHelper.getVerticalDistance();
        this.mCurrentPosition = iComputeVerticalScrollOffset;
        this.mLastPosition = iComputeVerticalScrollOffset;
    }

    @Override // miuix.recyclerview.widget.SpringSnapHelper
    void snapFromFling(androidx.recyclerview.widget.RecyclerView.LayoutManager layoutManager, int i) {
        this.mFolmeState.getTarget().setVelocity(this.mProperty, i);
        float f = i;
        float fPredictDistance = predictDistance(f, this.mProperty, this.mFriction, this.mVelocityThreshold);
        int i2 = (int) (this.mCurrentPosition + fPredictDistance);
        if ((this.mCurrentPosition == this.mMax || this.mCurrentPosition == this.mMin) && fPredictDistance == 0.0f) {
            return;
        }
        int iComputeFinalDistance = computeFinalDistance(i2, this.mItemHeight, this.mSnapPreference);
        this.mOutBounds = iComputeFinalDistance < this.mMin || iComputeFinalDistance > this.mMax;
        int iMin = Math.min(this.mMax, Math.max(this.mMin, iComputeFinalDistance));
        float frictionTo = getFrictionTo(f, this.mCurrentPosition, this.mProperty, iMin, this.mVelocityThreshold);
        if (this.mOutBounds) {
            frictionTo = Math.min(frictionTo, this.mFriction);
        }
        final AnimConfig ease = new AnimConfig().setEase(-2, this.mDamping, this.mResponse);
        final AnimState animStateAdd = new AnimState().add(this.mProperty, iMin, 4);
        AnimConfig animConfigAddListeners = new AnimConfig().setEase(-4, frictionTo).addListeners(new TransitionListener() { // from class: miuix.recyclerview.widget.VerticalSnapHelper.2
            @Override // miuix.animation.listener.TransitionListener
            public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                super.onUpdate(obj, collection);
                UpdateInfo updateInfoFindBy = UpdateInfo.findBy(collection, VerticalSnapHelper.this.mProperty);
                if (updateInfoFindBy == null) {
                    return;
                }
                if (updateInfoFindBy.getFloatValue() > VerticalSnapHelper.this.mMax || updateInfoFindBy.getFloatValue() < VerticalSnapHelper.this.mMin) {
                    VerticalSnapHelper.this.mFolmeState.to(animStateAdd, ease);
                }
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onCancel(Object obj) {
                VerticalSnapHelper.this.mOutBounds = false;
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onComplete(Object obj) {
                VerticalSnapHelper.this.mOutBounds = false;
            }
        });
        if (Math.abs(i) < this.mVelocityThreshold || frictionTo <= 0.0f) {
            this.mFolmeState.to(animStateAdd, ease);
        } else {
            this.mFolmeState.to(animStateAdd, animConfigAddListeners);
        }
    }

    @Override // miuix.recyclerview.widget.SpringSnapHelper
    int computeFinalDistance(int i, int i2, int i3) {
        if (i3 == 0) {
            int i4 = (i / i2) * i2;
            if (i % i2 <= i2 / 2) {
                i2 = 0;
            }
            return i4 + i2;
        }
        if (i3 == 1) {
            return ((i / i2) * i2) + (i2 / 2);
        }
        if (i3 != 2) {
            return -1;
        }
        return (i / i2) * i2;
    }
}
