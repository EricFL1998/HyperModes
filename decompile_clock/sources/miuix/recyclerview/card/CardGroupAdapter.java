package miuix.recyclerview.card;

import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.miui.support.drawable.CardStateDrawable;
import java.util.ArrayList;
import miuix.recyclerview.card.base.ViewOutlineHelper;

/* JADX INFO: loaded from: classes3.dex */
public abstract class CardGroupAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    public static final int GROUP_ID_NONE = Integer.MIN_VALUE;
    public static final int GROUP_TYPE_BODY = 3;
    public static final int GROUP_TYPE_FOOTER = 4;
    public static final int GROUP_TYPE_HEADER = 2;
    public static final int GROUP_TYPE_NONE = 0;
    public static final int GROUP_TYPE_SINGLE = 1;
    public static final String TAG = "CardGroupAdapter";
    private long mAnimatorDuration;
    private float mCardRadius;
    private RecyclerView mRecyclerView;
    private final SparseIntArray mTypeMap = new SparseIntArray(64);
    private boolean isNeedItemPressEffect = true;
    private int mRemovedGroupId = -1;
    private int mClickPosition = -1;
    private final RecyclerView.AdapterDataObserver mObserver = new RecyclerView.AdapterDataObserver() { // from class: miuix.recyclerview.card.CardGroupAdapter.1
        @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
        public void onChanged() {
            super.onChanged();
            CardGroupAdapter.this.updateGroupInfo();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeChanged(int i, int i2) {
            super.onItemRangeChanged(i, i2);
            CardGroupAdapter.this.updateGroupInfo();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeInserted(int i, int i2) {
            super.onItemRangeInserted(i, i2);
            CardGroupAdapter.this.updateGroupInfo();
            CardGroupAdapter.this.notifyCardItemRangeChanged(i, i2);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeMoved(int i, int i2, int i3) {
            super.onItemRangeMoved(i, i2, i3);
            CardGroupAdapter.this.updateGroupInfo();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeRemoved(int i, int i2) {
            super.onItemRangeRemoved(i, i2);
            CardGroupAdapter.this.updateGroupInfo();
            CardGroupAdapter.this.notifyCardItemRangeChanged(i, i2);
        }
    };

    public interface ISameGroupRangeRemove {
        boolean removeDataPositions(ArrayList<Integer> arrayList);
    }

    public abstract int getItemViewGroup(int i);

    public abstract void setHasStableIds();

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyCardItemRangeChanged(int i, int i2) {
        int i3 = i > 0 ? i - 1 : 0;
        notifyItemRangeChanged(i3, ((i + i2) - i3) + 1);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.mRecyclerView = recyclerView;
        this.mCardRadius = recyclerView.getContext().getResources().getDimensionPixelSize(R.dimen.miuix_recyclerview_card_group_radius);
        registerAdapterDataObserver(this.mObserver);
        RecyclerView.ItemAnimator itemAnimator = this.mRecyclerView.getItemAnimator();
        if (itemAnimator != null) {
            this.mAnimatorDuration = itemAnimator.getAddDuration();
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        unregisterAdapterDataObserver(this.mObserver);
        this.mRecyclerView = null;
    }

    protected CardGroupAdapter() {
        setHasStableIds();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(VH vh, int i) {
        ViewOutlineHelper.setItemCardOutline(vh, getItemViewGroupType(i), isSupportLayoutManager() ? this.mCardRadius : 0.0f, i == this.mClickPosition, this.mAnimatorDuration);
        if (isSupportLayoutManager()) {
            setFirstItemMargin(vh, i);
        }
        if (this.isNeedItemPressEffect) {
            if (vh.itemView.getForeground() == null) {
                TypedArray typedArrayObtainStyledAttributes = vh.itemView.getContext().getTheme().obtainStyledAttributes(new int[]{R.attr.cardGroupItemForegroundEffect});
                Drawable drawable = typedArrayObtainStyledAttributes.getDrawable(0);
                if (Build.VERSION.SDK_INT <= 31 && (drawable instanceof CardStateDrawable)) {
                    int itemViewGroupType = getItemViewGroupType(i);
                    ((CardStateDrawable) drawable.mutate()).setRadiusMode(isSupportLayoutManager() ? (int) this.mCardRadius : 0, itemViewGroupType != 0 ? itemViewGroupType : 3);
                }
                vh.itemView.setForeground(drawable);
                typedArrayObtainStyledAttributes.recycle();
                return;
            }
            Drawable foreground = vh.itemView.getForeground();
            int itemViewGroupType2 = getItemViewGroupType(i);
            if (Build.VERSION.SDK_INT > 31 || !(foreground instanceof CardStateDrawable) || itemViewGroupType2 == ((CardStateDrawable) foreground.mutate()).getRadiusMode()) {
                return;
            }
            ((CardStateDrawable) foreground.mutate()).setRadiusMode(isSupportLayoutManager() ? (int) this.mCardRadius : 0, itemViewGroupType2 != 0 ? itemViewGroupType2 : 3);
            vh.itemView.setForeground(foreground);
        }
    }

    private void setItemBackground(int i, Drawable drawable, View view) {
        CardStateDrawable cardStateDrawable = (CardStateDrawable) drawable.mutate();
        int i2 = isSupportLayoutManager() ? (int) this.mCardRadius : 0;
        if (i == 0) {
            i = 3;
        }
        cardStateDrawable.setRadiusMode(i2, i);
        view.setBackground(drawable);
    }

    private boolean isSupportLayoutManager() {
        if (this.mRecyclerView.getItemDecorationCount() > 0) {
            RecyclerView.ItemDecoration itemDecorationAt = this.mRecyclerView.getItemDecorationAt(0);
            if (itemDecorationAt instanceof CardItemDecoration) {
                return ((CardItemDecoration) itemDecorationAt).isSupportLayoutManager(this.mRecyclerView.getLayoutManager());
            }
        }
        return false;
    }

    private void setFirstItemMargin(RecyclerView.ViewHolder viewHolder, int i) {
        ViewGroup.MarginLayoutParams marginLayoutParams;
        if (i == 0) {
            if (this.mRecyclerView.getItemDecorationCount() > 0) {
                RecyclerView.ItemDecoration itemDecorationAt = this.mRecyclerView.getItemDecorationAt(0);
                if (itemDecorationAt instanceof CardItemDecoration) {
                    Rect rectOffsetsRect = ((CardItemDecoration) itemDecorationAt).offsetsRect(this, i);
                    ViewGroup.LayoutParams layoutParams = viewHolder.itemView.getLayoutParams();
                    if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                        marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
                    } else if (layoutParams != null) {
                        marginLayoutParams = new ViewGroup.MarginLayoutParams(layoutParams);
                    } else {
                        marginLayoutParams = new ViewGroup.MarginLayoutParams(new ViewGroup.LayoutParams(-2, -2));
                    }
                    marginLayoutParams.topMargin = rectOffsetsRect.top;
                    marginLayoutParams.bottomMargin = rectOffsetsRect.bottom;
                    viewHolder.itemView.setLayoutParams(marginLayoutParams);
                    return;
                }
                return;
            }
            return;
        }
        ViewGroup.LayoutParams layoutParams2 = viewHolder.itemView.getLayoutParams();
        if (layoutParams2 instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams2 = (ViewGroup.MarginLayoutParams) layoutParams2;
            marginLayoutParams2.topMargin = 0;
            marginLayoutParams2.bottomMargin = 0;
            viewHolder.itemView.setLayoutParams(marginLayoutParams2);
        }
    }

    public int getItemViewGroupType(int i) {
        return this.mTypeMap.get(i);
    }

    public void updateGroupInfo() {
        int itemCount = getItemCount();
        int i = Integer.MIN_VALUE;
        int i2 = 0;
        while (i2 < itemCount) {
            int itemViewGroup = getItemViewGroup(i2);
            if (itemViewGroup != i) {
                this.mTypeMap.put(i2, 2);
                int i3 = i2 - 1;
                if (i3 >= 0) {
                    int i4 = this.mTypeMap.get(i3);
                    if (i4 == 2) {
                        this.mTypeMap.put(i3, 1);
                    } else if (i4 == 3) {
                        this.mTypeMap.put(i3, 4);
                    }
                }
            } else {
                this.mTypeMap.put(i2, 3);
            }
            if (itemViewGroup == Integer.MIN_VALUE) {
                this.mTypeMap.put(i2, 0);
            }
            i2++;
            i = itemViewGroup;
        }
        int itemCount2 = getItemCount() - 1;
        int i5 = this.mTypeMap.get(itemCount2);
        if (i5 == 2) {
            this.mTypeMap.put(itemCount2, 1);
        } else if (i5 == 3) {
            this.mTypeMap.put(itemCount2, 4);
        }
    }

    public int getRemovedGroupId() {
        return this.mRemovedGroupId;
    }

    public void setRemoveGroupId(int i) {
        if (isInRemovedAnimator()) {
            return;
        }
        this.mRemovedGroupId = i;
    }

    public void setClickPosition(int i) {
        this.mClickPosition = i;
    }

    public void rangeRemoveFromSameGroup(int i, int i2, ISameGroupRangeRemove iSameGroupRangeRemove) {
        if (isInRemovedAnimator()) {
            return;
        }
        this.mRemovedGroupId = getItemViewGroup(i);
        ArrayList<Integer> arrayList = new ArrayList<>();
        int i3 = 0;
        for (int i4 = i; i4 < i + i2 && this.mRemovedGroupId == getItemViewGroup(i4); i4++) {
            i3++;
            arrayList.add(Integer.valueOf(i4));
        }
        if (iSameGroupRangeRemove == null || !iSameGroupRangeRemove.removeDataPositions(arrayList)) {
            return;
        }
        updateGroupInfo();
        notifyItemRangeRemoved(i, i3);
        notifyItemRangeChanged(0, getItemCount());
    }

    private boolean isInRemovedAnimator() {
        RecyclerView recyclerView = this.mRecyclerView;
        if (recyclerView == null) {
            return false;
        }
        RecyclerView.ItemAnimator itemAnimator = recyclerView.getItemAnimator();
        if (itemAnimator instanceof CardDefaultItemAnimator) {
            return ((CardDefaultItemAnimator) itemAnimator).isOnRemoveAnimation();
        }
        return false;
    }

    public void setCardRadius(float f) {
        this.mCardRadius = f;
    }

    public void setNeedItemPressEffect(boolean z) {
        this.isNeedItemPressEffect = z;
    }
}
