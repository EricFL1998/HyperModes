package com.android.deskclock.worldclock;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import miuix.appcompat.app.AppCompatActivity;

/* JADX INFO: loaded from: classes.dex */
public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private AppCompatActivity mActivity;
    private final ItemTouchHelperAdapter mAdapter;

    public interface ItemTouchHelperAdapter {
        void onDragEnd(RecyclerView.ViewHolder viewHolder, int i);

        boolean onDragStart(RecyclerView.ViewHolder viewHolder, int i);

        boolean onItemMove(int i, int i2);

        int onMovementFlags();
    }

    @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
    }

    public ItemTouchHelperCallback(ItemTouchHelperAdapter itemTouchHelperAdapter, AppCompatActivity appCompatActivity) {
        this.mAdapter = itemTouchHelperAdapter;
        this.mActivity = appCompatActivity;
    }

    @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            return makeMovementFlags(this.mAdapter.onMovementFlags(), 0);
        }
        return 0;
    }

    @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
        if (viewHolder.getItemViewType() != viewHolder2.getItemViewType()) {
            return false;
        }
        this.mAdapter.onItemMove(viewHolder.getAdapterPosition(), viewHolder2.getAdapterPosition());
        return true;
    }

    @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int i) {
        super.onSelectedChanged(viewHolder, i);
        if (i != 0) {
            this.mAdapter.onDragStart(viewHolder, i);
            viewHolder.itemView.setHapticFeedbackEnabled(true);
        } else {
            this.mAdapter.onDragEnd(viewHolder, i);
        }
    }

    @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
    }
}
