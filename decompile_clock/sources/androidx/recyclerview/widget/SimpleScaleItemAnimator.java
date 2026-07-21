package androidx.recyclerview.widget;

import android.view.View;

/* JADX INFO: loaded from: classes.dex */
public abstract class SimpleScaleItemAnimator extends SimpleItemAnimator {
    public abstract boolean animateMove(RecyclerView.ViewHolder viewHolder, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8);

    @Override // androidx.recyclerview.widget.SimpleItemAnimator, androidx.recyclerview.widget.RecyclerView.ItemAnimator
    public boolean animateDisappearance(RecyclerView.ViewHolder viewHolder, RecyclerView.ItemAnimator.ItemHolderInfo itemHolderInfo, RecyclerView.ItemAnimator.ItemHolderInfo itemHolderInfo2) {
        int i = itemHolderInfo.left;
        int i2 = itemHolderInfo.top;
        View view = viewHolder.itemView;
        int left = itemHolderInfo2 == null ? view.getLeft() : itemHolderInfo2.left;
        int top = itemHolderInfo2 == null ? view.getTop() : itemHolderInfo2.top;
        int right = itemHolderInfo2 == null ? view.getRight() : itemHolderInfo2.right;
        int bottom = itemHolderInfo2 == null ? view.getBottom() : itemHolderInfo2.bottom;
        int width = itemHolderInfo2 == null ? view.getWidth() : itemHolderInfo.right - itemHolderInfo.left;
        int height = itemHolderInfo2 == null ? view.getHeight() : itemHolderInfo.bottom - itemHolderInfo.top;
        int width2 = itemHolderInfo2 == null ? view.getWidth() : itemHolderInfo2.right - itemHolderInfo2.left;
        int height2 = itemHolderInfo2 == null ? view.getHeight() : itemHolderInfo2.bottom - itemHolderInfo2.top;
        if (!viewHolder.isRemoved() && (i != left || i2 != top || width != width2 || height != height2)) {
            view.layout(left, top, view.getWidth() + left, view.getHeight() + top);
            return animateMove(viewHolder, i, i2, left, top, itemHolderInfo.right - itemHolderInfo.left, right - left, itemHolderInfo.bottom - itemHolderInfo.top, bottom - top);
        }
        return animateRemove(viewHolder);
    }

    @Override // androidx.recyclerview.widget.SimpleItemAnimator, androidx.recyclerview.widget.RecyclerView.ItemAnimator
    public boolean animateAppearance(RecyclerView.ViewHolder viewHolder, RecyclerView.ItemAnimator.ItemHolderInfo itemHolderInfo, RecyclerView.ItemAnimator.ItemHolderInfo itemHolderInfo2) {
        if (itemHolderInfo != null) {
            int i = itemHolderInfo.left;
            int i2 = itemHolderInfo.top;
            int i3 = itemHolderInfo2.left;
            int i4 = itemHolderInfo2.top;
            int i5 = itemHolderInfo.right - itemHolderInfo.left;
            int i6 = itemHolderInfo.bottom - itemHolderInfo.top;
            int i7 = itemHolderInfo2.right - itemHolderInfo2.left;
            int i8 = itemHolderInfo2.bottom - itemHolderInfo2.top;
            if (i != i3 || i2 != i4 || i5 != i7 || i6 != i8) {
                return animateMove(viewHolder, i, i2, i3, i4, i5, i7, i6, i8);
            }
        }
        return animateAdd(viewHolder);
    }

    @Override // androidx.recyclerview.widget.SimpleItemAnimator, androidx.recyclerview.widget.RecyclerView.ItemAnimator
    public boolean animatePersistence(RecyclerView.ViewHolder viewHolder, RecyclerView.ItemAnimator.ItemHolderInfo itemHolderInfo, RecyclerView.ItemAnimator.ItemHolderInfo itemHolderInfo2) {
        int i = itemHolderInfo.left;
        int i2 = itemHolderInfo.top;
        int i3 = itemHolderInfo2.left;
        int i4 = itemHolderInfo2.top;
        int i5 = itemHolderInfo.right - itemHolderInfo.left;
        int i6 = itemHolderInfo.bottom - itemHolderInfo.top;
        int i7 = itemHolderInfo2.right - itemHolderInfo2.left;
        int i8 = itemHolderInfo2.bottom - itemHolderInfo2.top;
        if (i != i3 || i2 != i4 || i5 != i7 || i6 != i8) {
            return animateMove(viewHolder, i, i2, i3, i4, i5, i7, i6, i8);
        }
        dispatchMoveFinished(viewHolder);
        return false;
    }

    @Override // androidx.recyclerview.widget.SimpleItemAnimator
    public boolean animateMove(RecyclerView.ViewHolder viewHolder, int i, int i2, int i3, int i4) {
        int width = viewHolder.itemView.getWidth();
        int height = viewHolder.itemView.getHeight();
        return animateMove(viewHolder, i, i2, i3, i4, width, width, height, height);
    }
}
