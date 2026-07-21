package miuix.recyclerview.widget;

import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.Collection;
import miuix.animation.Folme;
import miuix.animation.base.AnimConfig;
import miuix.animation.controller.AnimState;
import miuix.animation.controller.FolmeState;
import miuix.animation.listener.TransitionListener;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.property.IntValueProperty;

/* JADX INFO: loaded from: classes3.dex */
public class HorizontalSnapHelper extends SpringSnapHelper {
    @Override // miuix.recyclerview.widget.SpringSnapHelper
    protected void init() {
        super.init();
        this.mFolmeState = (FolmeState) Folme.useValue(this);
        this.mProperty = new IntValueProperty("scrollX", 0.2f) { // from class: miuix.recyclerview.widget.HorizontalSnapHelper.1
            @Override // miuix.animation.property.IntValueProperty, miuix.animation.property.IIntValueProperty
            public void setIntValue(Object obj, int i) {
                int i2 = HorizontalSnapHelper.this.mMin;
                int i3 = HorizontalSnapHelper.this.mMax;
                HorizontalSnapHelper.this.mCurrentPosition = i;
                if (HorizontalSnapHelper.this.mRecyclerView.getSpringEnabled()) {
                    int i4 = HorizontalSnapHelper.this.mCurrentPosition - HorizontalSnapHelper.this.mLastPosition;
                    if (HorizontalSnapHelper.this.mCurrentPosition < i2 && HorizontalSnapHelper.this.mLastPosition > i2) {
                        HorizontalSnapHelper.this.mRecyclerView.scrollBy(i2 - HorizontalSnapHelper.this.mLastPosition, 0);
                    } else if (HorizontalSnapHelper.this.mCurrentPosition > i3 && HorizontalSnapHelper.this.mLastPosition < i3) {
                        HorizontalSnapHelper.this.mRecyclerView.scrollBy(i3 - HorizontalSnapHelper.this.mLastPosition, 0);
                    } else if (HorizontalSnapHelper.this.mCurrentPosition > i2 && HorizontalSnapHelper.this.mLastPosition < i2) {
                        HorizontalSnapHelper.this.mRecyclerView.scrollBy(HorizontalSnapHelper.this.mCurrentPosition - i2, 0);
                    } else if (HorizontalSnapHelper.this.mCurrentPosition < i3 && HorizontalSnapHelper.this.mLastPosition > i3) {
                        HorizontalSnapHelper.this.mRecyclerView.scrollBy(HorizontalSnapHelper.this.mCurrentPosition - i3, 0);
                    } else if (HorizontalSnapHelper.this.mCurrentPosition >= i2 && HorizontalSnapHelper.this.mCurrentPosition <= i3 && HorizontalSnapHelper.this.mLastPosition >= i2 && HorizontalSnapHelper.this.mLastPosition <= i3) {
                        HorizontalSnapHelper.this.mRecyclerView.scrollBy(i4, 0);
                        HorizontalSnapHelper.this.mSpringHelper.resetDistance();
                    }
                    if (HorizontalSnapHelper.this.mCurrentPosition < i2 || HorizontalSnapHelper.this.mCurrentPosition > i3) {
                        HorizontalSnapHelper.this.mRecyclerView.dispatchNestedPreScroll(i4, 0, null, null, 1);
                        HorizontalSnapHelper.this.mRecyclerView.invalidate();
                        if (HorizontalSnapHelper.this.mCurrentPosition < i2) {
                            HorizontalSnapHelper horizontalSnapHelper = HorizontalSnapHelper.this;
                            horizontalSnapHelper.setSpringHorizontalDistance(horizontalSnapHelper.mSpringHelper, HorizontalSnapHelper.this.mCurrentPosition);
                        } else {
                            HorizontalSnapHelper horizontalSnapHelper2 = HorizontalSnapHelper.this;
                            horizontalSnapHelper2.setSpringHorizontalDistance(horizontalSnapHelper2.mSpringHelper, HorizontalSnapHelper.this.mCurrentPosition - i3);
                        }
                    }
                } else {
                    HorizontalSnapHelper horizontalSnapHelper3 = HorizontalSnapHelper.this;
                    horizontalSnapHelper3.mCurrentPosition = Math.max(horizontalSnapHelper3.mCurrentPosition, i2);
                    HorizontalSnapHelper horizontalSnapHelper4 = HorizontalSnapHelper.this;
                    horizontalSnapHelper4.mCurrentPosition = Math.min(horizontalSnapHelper4.mCurrentPosition, i3);
                    HorizontalSnapHelper.this.mRecyclerView.scrollBy(HorizontalSnapHelper.this.mCurrentPosition - HorizontalSnapHelper.this.mLastPosition, 0);
                }
                HorizontalSnapHelper horizontalSnapHelper5 = HorizontalSnapHelper.this;
                horizontalSnapHelper5.mLastPosition = horizontalSnapHelper5.mCurrentPosition;
            }

            @Override // miuix.animation.property.IntValueProperty, miuix.animation.property.IIntValueProperty
            public int getIntValue(Object obj) {
                return HorizontalSnapHelper.this.mCurrentPosition;
            }
        };
    }

    @Override // miuix.recyclerview.widget.SpringSnapHelper
    void updateConstructData() {
        if (this.mRecyclerView.getLayoutManager() == null || this.mRecyclerView.getAdapter() == null) {
            return;
        }
        if (this.mItemWidth == Integer.MAX_VALUE) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) this.mRecyclerView.getLayoutManager();
            View viewFindViewByPosition = linearLayoutManager.findViewByPosition(linearLayoutManager.findFirstVisibleItemPosition());
            if (viewFindViewByPosition != null) {
                linearLayoutManager.getDecoratedBoundsWithMargins(viewFindViewByPosition, this.mBounds);
            }
            this.mItemWidth = this.mBounds.width();
        }
        int itemCount = this.mRecyclerView.getAdapter().getItemCount();
        this.mMin = 0;
        this.mMax = Math.max((itemCount * this.mItemWidth) - this.mRecyclerView.getWidth(), 0);
        int iComputeHorizontalScrollOffset = this.mRecyclerView.computeHorizontalScrollOffset() + this.mSpringHelper.getHorizontalDistance();
        this.mCurrentPosition = iComputeHorizontalScrollOffset;
        this.mLastPosition = iComputeHorizontalScrollOffset;
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
        int iComputeFinalDistance = computeFinalDistance(i2, this.mItemWidth, this.mSnapPreference);
        this.mOutBounds = iComputeFinalDistance < this.mMin || iComputeFinalDistance > this.mMax;
        int iMin = Math.min(this.mMax, Math.max(this.mMin, iComputeFinalDistance));
        float frictionTo = getFrictionTo(f, this.mCurrentPosition, this.mProperty, iMin, this.mVelocityThreshold);
        if (this.mOutBounds) {
            frictionTo = Math.min(frictionTo, this.mFriction);
        }
        final AnimConfig ease = new AnimConfig().setEase(-2, this.mDamping, this.mResponse);
        final AnimState animStateAdd = new AnimState().add(this.mProperty, iMin, 4);
        AnimConfig animConfigAddListeners = new AnimConfig().setEase(-4, frictionTo).addListeners(new TransitionListener() { // from class: miuix.recyclerview.widget.HorizontalSnapHelper.2
            @Override // miuix.animation.listener.TransitionListener
            public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                super.onUpdate(obj, collection);
                UpdateInfo updateInfoFindBy = UpdateInfo.findBy(collection, HorizontalSnapHelper.this.mProperty);
                if (updateInfoFindBy == null) {
                    return;
                }
                if (updateInfoFindBy.getFloatValue() > HorizontalSnapHelper.this.mMax || updateInfoFindBy.getFloatValue() < HorizontalSnapHelper.this.mMin) {
                    HorizontalSnapHelper.this.mFolmeState.to(animStateAdd, ease);
                }
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onCancel(Object obj) {
                HorizontalSnapHelper.this.mOutBounds = false;
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onComplete(Object obj) {
                HorizontalSnapHelper.this.mOutBounds = false;
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
        if (this.mRecyclerView.getLayoutDirection() == 0) {
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
        int i5 = this.mMax - i;
        if (i3 != 0) {
            if (i3 == 1) {
                return this.mMax - (((i5 / i2) * i2) + (i2 / 2));
            }
            if (i3 != 2) {
                return -1;
            }
            return this.mMax - ((i5 / i2) * i2);
        }
        int i6 = this.mMax;
        int i7 = (i5 / i2) * i2;
        if (i5 % i2 <= i2 / 2) {
            i2 = 0;
        }
        return i6 - (i7 + i2);
    }
}
