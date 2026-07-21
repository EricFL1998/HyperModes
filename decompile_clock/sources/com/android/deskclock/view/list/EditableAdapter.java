package com.android.deskclock.view.list;

import android.content.res.Resources;
import android.util.SparseIntArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import androidx.recyclerview.widget.RecyclerView;
import com.android.deskclock.R;
import com.android.deskclock.TabNavigatorContentFragment;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import miuix.view.ActionModeAnimationListener;
import miuix.view.EditActionMode;

/* JADX INFO: loaded from: classes.dex */
public abstract class EditableAdapter extends RecyclerView.Adapter {
    private ActionMode mActionMode;
    private MultiChoiceModeListener mActionModeCallback;
    private OnActionAnimListener mAnimListener;
    private SparseIntArray mCheckedIdStates;
    private RecyclerView mRecyclerView;
    private boolean mAnim = false;
    private MultiChoiceModeListener mInternalActionModeCallback = new MultiChoiceModeListener() { // from class: com.android.deskclock.view.list.EditableAdapter.1
        @Override // com.android.deskclock.view.list.EditableAdapter.MultiChoiceModeListener
        public void onAllItemCheckedStateChanged(ActionMode actionMode, boolean z) {
            EditableAdapter.this.mActionModeCallback.onAllItemCheckedStateChanged(actionMode, z);
            EditableAdapter editableAdapter = EditableAdapter.this;
            editableAdapter.updateActionModeTitle(actionMode, editableAdapter.mCheckedIdStates.size());
        }

        @Override // android.widget.AbsListView.MultiChoiceModeListener
        public void onItemCheckedStateChanged(ActionMode actionMode, int i, long j, boolean z) {
            EditableAdapter.this.mActionModeCallback.onItemCheckedStateChanged(actionMode, i, j, z);
            EditableAdapter editableAdapter = EditableAdapter.this;
            editableAdapter.updateActionModeTitle(actionMode, editableAdapter.mCheckedIdStates.size());
        }

        @Override // android.view.ActionMode.Callback
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return EditableAdapter.this.mActionModeCallback.onPrepareActionMode(actionMode, menu);
        }

        @Override // android.view.ActionMode.Callback
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            if (!EditableAdapter.this.mActionModeCallback.onCreateActionMode(actionMode, menu)) {
                return false;
            }
            EditableAdapter.this.mActionMode = actionMode;
            EditableAdapter editableAdapter = EditableAdapter.this;
            editableAdapter.updateActionModeTitle(actionMode, editableAdapter.mCheckedIdStates.size());
            ((EditActionMode) EditableAdapter.this.mActionMode).addAnimationListener(EditableAdapter.this.mInternalActionModeAnimationListener);
            return true;
        }

        @Override // android.view.ActionMode.Callback
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            return EditableAdapter.this.mActionModeCallback.onActionItemClicked(actionMode, menuItem);
        }

        @Override // android.view.ActionMode.Callback
        public void onDestroyActionMode(ActionMode actionMode) {
            EditableAdapter.this.mActionMode = null;
            EditableAdapter.this.mActionModeCallback.onDestroyActionMode(actionMode);
            EditableAdapter.this.mCheckedIdStates.clear();
        }
    };
    private ActionModeAnimationListener mInternalActionModeAnimationListener = new ActionModeAnimationListener() { // from class: com.android.deskclock.view.list.EditableAdapter.2
        Set<ViewHolderEditableCallback> holders = new HashSet();

        @Override // miuix.view.ActionModeAnimationListener
        public void onStart(boolean z) {
            EditableAdapter.this.mAnim = true;
            this.holders.clear();
            int childCount = EditableAdapter.this.mRecyclerView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                Object childViewHolder = EditableAdapter.this.mRecyclerView.getChildViewHolder(EditableAdapter.this.mRecyclerView.getChildAt(i));
                if (childViewHolder instanceof ViewHolderEditableCallback) {
                    ViewHolderEditableCallback viewHolderEditableCallback = (ViewHolderEditableCallback) childViewHolder;
                    viewHolderEditableCallback.onAnimationStart(z);
                    this.holders.add(viewHolderEditableCallback);
                }
            }
            if (EditableAdapter.this.mAnimListener != null) {
                EditableAdapter.this.mAnimListener.onAnimStart();
            }
        }

        @Override // miuix.view.ActionModeAnimationListener
        public void onUpdate(boolean z, float f) {
            int childCount = EditableAdapter.this.mRecyclerView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                Object childViewHolder = EditableAdapter.this.mRecyclerView.getChildViewHolder(EditableAdapter.this.mRecyclerView.getChildAt(i));
                if (childViewHolder instanceof ViewHolderEditableCallback) {
                    ViewHolderEditableCallback viewHolderEditableCallback = (ViewHolderEditableCallback) childViewHolder;
                    if (viewHolderEditableCallback.hasAnimationStarted()) {
                        viewHolderEditableCallback.onAnimationUpdate(z, f);
                    }
                }
            }
        }

        @Override // miuix.view.ActionModeAnimationListener
        public void onStop(boolean z) {
            Iterator<ViewHolderEditableCallback> it = this.holders.iterator();
            while (it.hasNext()) {
                it.next().onAnimationStop(z);
            }
            this.holders.clear();
            EditableAdapter.this.notifyDataSetChanged();
            if (EditableAdapter.this.mAnimListener != null) {
                EditableAdapter.this.mAnimListener.onAnimStop();
            }
            EditableAdapter.this.mAnim = false;
        }
    };

    public interface MultiChoiceModeListener extends AbsListView.MultiChoiceModeListener {
        void onAllItemCheckedStateChanged(ActionMode actionMode, boolean z);
    }

    public interface OnActionAnimListener {
        void onAnimStart();

        void onAnimStop();
    }

    public abstract int getEditableItemCount();

    public abstract boolean isItemEditable(int i);

    public EditableAdapter(RecyclerView recyclerView) {
        setHasStableIds(true);
        this.mCheckedIdStates = new SparseIntArray();
        this.mRecyclerView = recyclerView;
        recyclerView.setAdapter(this);
    }

    public void setOnActionAnimListener(OnActionAnimListener onActionAnimListener) {
        this.mAnimListener = onActionAnimListener;
    }

    public boolean isDoingActionAnim() {
        return this.mAnim;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof EditableViewHolder) {
            ((EditableViewHolder) viewHolder).onUpdateEditable(isInActionMode(), isItemChecked(i));
        }
    }

    public boolean isInActionMode() {
        return this.mActionMode != null;
    }

    public void startActionMode(MultiChoiceModeListener multiChoiceModeListener, TabNavigatorContentFragment tabNavigatorContentFragment) {
        this.mActionModeCallback = multiChoiceModeListener;
        tabNavigatorContentFragment.startActionMode(this.mInternalActionModeCallback);
    }

    public void finishActionMode() {
        ActionMode actionMode = this.mActionMode;
        if (actionMode != null) {
            actionMode.finish();
        }
    }

    public boolean isItemChecked(int i) {
        return this.mCheckedIdStates.indexOfValue(i) >= 0;
    }

    public boolean isAllItemsChecked() {
        return getEditableItemCount() == getCheckedItemCount();
    }

    public boolean isAllItemsUnChecked() {
        return getCheckedItemCount() == 0;
    }

    public int getCheckedItemCount() {
        return this.mCheckedIdStates.size();
    }

    public void setItemChecked(int i, boolean z) {
        setItemChecked(i, z, false);
    }

    public void swapCheckedStates(int i, int i2) {
        int itemId = (int) getItemId(i);
        boolean z = this.mCheckedIdStates.indexOfKey(itemId) >= 0;
        int itemId2 = (int) getItemId(i2);
        boolean z2 = this.mCheckedIdStates.indexOfKey(itemId2) >= 0;
        if (z) {
            this.mCheckedIdStates.put(itemId, i2);
        }
        if (z2) {
            this.mCheckedIdStates.put(itemId2, i);
        }
    }

    private void setItemChecked(int i, boolean z, boolean z2) {
        ActionMode actionMode;
        int itemId = (int) getItemId(i);
        if (z != (this.mCheckedIdStates.indexOfKey(itemId) >= 0)) {
            if (z) {
                this.mCheckedIdStates.put(itemId, i);
            } else {
                this.mCheckedIdStates.delete(itemId);
            }
            long j = itemId;
            View viewByItemId = getViewByItemId(j);
            if (viewByItemId == null) {
                notifyItemChanged(i);
            } else {
                updateOnScreenCheckedView(viewByItemId, i, itemId);
            }
            if (z2 || (actionMode = this.mActionMode) == null || this.mActionModeCallback == null) {
                return;
            }
            this.mInternalActionModeCallback.onItemCheckedStateChanged(actionMode, i, j, z);
        }
    }

    public void toggleItemChecked(int i) {
        if (i != -1) {
            setItemChecked(i, !isItemChecked(i));
        }
    }

    public void setAllItemsChecked(boolean z) {
        MultiChoiceModeListener multiChoiceModeListener;
        int itemCount = getItemCount();
        for (int i = 0; i < itemCount; i++) {
            if (isItemEditable(i)) {
                setItemChecked(i, z, true);
            }
        }
        ActionMode actionMode = this.mActionMode;
        if (actionMode == null || (multiChoiceModeListener = this.mInternalActionModeCallback) == null) {
            return;
        }
        multiChoiceModeListener.onAllItemCheckedStateChanged(actionMode, z);
    }

    public int[] getCheckedItemIds() {
        int size = this.mCheckedIdStates.size();
        int[] iArr = new int[this.mCheckedIdStates.size()];
        for (int i = 0; i < size; i++) {
            iArr[i] = this.mCheckedIdStates.keyAt(i);
        }
        return iArr;
    }

    public int[] getCheckedItemPostions() {
        int size = this.mCheckedIdStates.size();
        int[] iArr = new int[this.mCheckedIdStates.size()];
        for (int i = 0; i < size; i++) {
            iArr[i] = this.mCheckedIdStates.valueAt(i);
        }
        return iArr;
    }

    public long[] getCheckedItemIndexs() {
        int size = this.mCheckedIdStates.size();
        long[] jArr = new long[this.mCheckedIdStates.size()];
        for (int i = 0; i < size; i++) {
            jArr[i] = this.mCheckedIdStates.valueAt(i);
        }
        return jArr;
    }

    private void updateOnScreenCheckedView(View view, int i, int i2) {
        Object childViewHolder = this.mRecyclerView.getChildViewHolder(view);
        if (childViewHolder == null || !(childViewHolder instanceof ViewHolderEditableCallback)) {
            return;
        }
        ((ViewHolderEditableCallback) childViewHolder).onUpdateEditable(isInActionMode(), this.mCheckedIdStates.indexOfKey(i2) >= 0);
    }

    private View getViewByItemId(long j) {
        int childCount = this.mRecyclerView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.mRecyclerView.getChildAt(i);
            if (this.mRecyclerView.getChildItemId(childAt) == j) {
                return childAt;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateActionModeTitle(ActionMode actionMode, int i) {
        if (actionMode != null) {
            Resources resources = this.mRecyclerView.getResources();
            if (i == 0) {
                actionMode.setTitle(resources.getString(R.string.miuix_appcompat_select_item));
            } else {
                actionMode.setTitle(String.format(resources.getQuantityString(R.plurals.miuix_appcompat_items_selected, i), Integer.valueOf(i)));
            }
        }
    }
}
