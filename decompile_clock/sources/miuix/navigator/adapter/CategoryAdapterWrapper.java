package miuix.navigator.adapter;

import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import miuix.navigator.R;
import miuix.navigator.draganddrop.NavigatorDragListener;
import miuix.navigator.navigatorinfo.NavigatorInfo;

/* JADX INFO: loaded from: classes3.dex */
public class CategoryAdapterWrapper extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final Object PAYLOAD_PLACEHOLDER = new Object();
    private final CategoryAdapter<? extends CategoryAdapter.Item> mAdapter;
    private final CategoryImpl mCategory;
    private boolean mEmptyDragPlaceholderActive;
    private NavigatorDragListener mNavigatorDragListener;
    private boolean mPreReplaceDragPlaceholder;
    private boolean mShowingEmptyDragPlaceholder;
    private int mCachedItemCount = -1;
    private int mDragPlaceholderPosition = -1;
    private long mDragPlaceholderId = 2147483648L;

    static /* synthetic */ int access$412(CategoryAdapterWrapper categoryAdapterWrapper, int i) {
        int i2 = categoryAdapterWrapper.mCachedItemCount + i;
        categoryAdapterWrapper.mCachedItemCount = i2;
        return i2;
    }

    static /* synthetic */ int access$420(CategoryAdapterWrapper categoryAdapterWrapper, int i) {
        int i2 = categoryAdapterWrapper.mCachedItemCount - i;
        categoryAdapterWrapper.mCachedItemCount = i2;
        return i2;
    }

    CategoryAdapterWrapper(CategoryAdapter<? extends CategoryAdapter.Item> categoryAdapter, CategoryImpl categoryImpl) {
        if (categoryAdapter == null) {
            throw new NullPointerException("adapter must not be null");
        }
        this.mAdapter = categoryAdapter;
        this.mCategory = categoryImpl;
        categoryAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() { // from class: miuix.navigator.adapter.CategoryAdapterWrapper.1
            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onChanged() {
                CategoryAdapterWrapper.this.calculateItemCount();
                CategoryAdapterWrapper.this.refresh();
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeChanged(int i, int i2) {
                if (i2 == 1) {
                    if (CategoryAdapterWrapper.this.mAdapter.isVisible(i)) {
                        for (int i3 = i - 1; i3 >= 0; i3--) {
                            if (!CategoryAdapterWrapper.this.mAdapter.isVisible(i3)) {
                                i--;
                            }
                        }
                        CategoryAdapterWrapper.this.getNavigationAdapter().notifyItemChanged((CategoryAdapterWrapper.this.mAdapter.isEditing() ? 0 : CategoryAdapterWrapper.this.getCategoryOffset()) + i);
                        return;
                    }
                    return;
                }
                CategoryAdapterWrapper.this.refresh();
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeInserted(int i, int i2) {
                CategoryAdapterWrapper.access$412(CategoryAdapterWrapper.this, i2);
                if (CategoryAdapterWrapper.this.mPreReplaceDragPlaceholder) {
                    if (i != CategoryAdapterWrapper.this.mDragPlaceholderPosition || i2 != 1) {
                        CategoryAdapterWrapper.this.mPreReplaceDragPlaceholder = false;
                        CategoryAdapterWrapper.this.mDragPlaceholderPosition = -1;
                        CategoryAdapterWrapper.this.refresh();
                        return;
                    }
                    CategoryAdapterWrapper.this.notifyDropItemReplacing();
                    return;
                }
                if (i2 == 1) {
                    if (CategoryAdapterWrapper.this.mAdapter.isVisible(i)) {
                        for (int i3 = i - 1; i3 >= 0; i3--) {
                            if (!CategoryAdapterWrapper.this.mAdapter.isVisible(i3)) {
                                i--;
                            }
                        }
                        CategoryAdapterWrapper.this.getNavigationAdapter().notifyItemInserted((CategoryAdapterWrapper.this.mAdapter.isEditing() ? 0 : CategoryAdapterWrapper.this.getCategoryOffset()) + i);
                        return;
                    }
                    return;
                }
                CategoryAdapterWrapper.this.refresh();
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeRemoved(int i, int i2) {
                if (i2 == 1) {
                    if (!CategoryAdapterWrapper.this.mAdapter.isVisible(i)) {
                        return;
                    }
                    for (int i3 = i - 1; i3 >= 0; i3--) {
                        if (!CategoryAdapterWrapper.this.mAdapter.isVisible(i3)) {
                            i--;
                        }
                    }
                    CategoryAdapterWrapper.this.getNavigationAdapter().notifyItemRemoved((CategoryAdapterWrapper.this.mAdapter.isEditing() ? 0 : CategoryAdapterWrapper.this.getCategoryOffset()) + i);
                }
                CategoryAdapterWrapper.access$420(CategoryAdapterWrapper.this, i2);
                CategoryAdapterWrapper.this.refresh();
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeMoved(int i, int i2, int i3) {
                if (CategoryAdapterWrapper.this.getNavigationAdapter() == null || i3 != 1) {
                    CategoryAdapterWrapper.this.refresh();
                } else {
                    int categoryOffset = CategoryAdapterWrapper.this.mAdapter.isEditing() ? 0 : CategoryAdapterWrapper.this.getCategoryOffset();
                    CategoryAdapterWrapper.this.getNavigationAdapter().notifyItemMoved(i + categoryOffset, categoryOffset + i2);
                }
            }
        });
        calculateItemCount();
        refresh();
    }

    public NavigationAdapter getNavigationAdapter() {
        return this.mAdapter.getNavigationAdapter();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        int i2 = (i == this.mCategory.getFooterId() && this.mAdapter.hasFooterView()) ? 1 : 0;
        RecyclerView.ViewHolder viewHolderCreateViewHolder = this.mAdapter.createViewHolder(viewGroup, i2);
        viewHolderCreateViewHolder.itemView.setTag(R.id.miuix_navigator_drag_helper_token, this);
        if (i2 != 0) {
            viewHolderCreateViewHolder.itemView.setTag(R.id.miuix_navigator_drag_helper_footer, Object.class);
        }
        return viewHolderCreateViewHolder;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (this.mDragPlaceholderPosition != i) {
            this.mAdapter.bindViewHolder(viewHolder, unwrapPosition(i));
        } else {
            viewHolder.itemView.setTag(R.id.miuix_navigator_drag_helper_token, this);
            getNavigatorDragListener().onBindDragPlaceholder(viewHolder, !(this.mShowingEmptyDragPlaceholder && !this.mEmptyDragPlaceholderActive));
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int i) {
        if (i == this.mDragPlaceholderPosition) {
            return -3;
        }
        return this.mCategory.getId();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void setHasStableIds(boolean z) {
        this.mAdapter.setHasStableIds(z);
    }

    CategoryAdapter<? extends CategoryAdapter.Item> getBaseAdapter() {
        return this.mAdapter;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public long getItemId(int i) {
        return i == this.mDragPlaceholderPosition ? this.mDragPlaceholderId : this.mAdapter.getItemId(unwrapPosition(i));
    }

    private int unwrapPosition(int i) {
        int i2 = this.mDragPlaceholderPosition;
        if (i2 != -1 && i >= i2) {
            i--;
        }
        int itemCount = this.mAdapter.getItemCount();
        int i3 = 0;
        for (int i4 = 0; i4 < itemCount; i4++) {
            if (!this.mAdapter.isVisible(i4)) {
                i3++;
            }
            if (i3 + i == i4) {
                return i4;
            }
        }
        return i3 + i;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        if (this.mCachedItemCount == -1) {
            calculateItemCount();
        }
        return this.mCachedItemCount + (this.mDragPlaceholderPosition != -1 ? 1 : 0);
    }

    public boolean hasAliveItems() {
        return this.mCachedItemCount != 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void calculateItemCount() {
        int itemCount = this.mAdapter.getItemCount();
        int i = 0;
        for (int i2 = 0; i2 < itemCount; i2++) {
            if (!this.mAdapter.isVisible(i2)) {
                i++;
            }
        }
        this.mCachedItemCount = itemCount - i;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int findRelativeAdapterPositionIn(RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter, RecyclerView.ViewHolder viewHolder, int i) {
        return i == this.mDragPlaceholderPosition ? i : this.mAdapter.findRelativeAdapterPositionIn(adapter, viewHolder, unwrapPosition(i));
    }

    public int getNavigatorInfoPosition(NavigatorInfo navigatorInfo) {
        List<T> list = this.mAdapter.getList();
        int i = 0;
        for (int i2 = 0; i2 < list.size(); i2++) {
            if (i == this.mDragPlaceholderPosition) {
                i++;
            }
            if (navigatorInfo.equals(((CategoryAdapter.Item) list.get(i2)).getNavigatorInfo())) {
                if (this.mAdapter.isVisible(i2)) {
                    return i;
                }
                return -1;
            }
            if (this.mAdapter.isVisible(i2)) {
                i++;
            }
        }
        return -1;
    }

    public CategoryAdapter<CategoryAdapter.Item> getAdapter() {
        return this.mAdapter;
    }

    public boolean isExpanded() {
        return this.mCategory.isExpanded();
    }

    public void insertDragPlaceholder(int i) {
        if (this.mShowingEmptyDragPlaceholder) {
            if (this.mEmptyDragPlaceholderActive) {
                return;
            }
            this.mEmptyDragPlaceholderActive = true;
            getNavigationAdapter().notifyItemChanged(getCategoryOffset(), PAYLOAD_PLACEHOLDER);
            return;
        }
        this.mDragPlaceholderPosition = i;
        if (getNavigationAdapter() != null) {
            getNavigationAdapter().notifyItemInserted(i + getCategoryOffset());
        }
    }

    public void removeDragPlaceholder() {
        if (this.mShowingEmptyDragPlaceholder) {
            if (this.mEmptyDragPlaceholderActive) {
                this.mEmptyDragPlaceholderActive = false;
                getNavigationAdapter().notifyItemChanged(getCategoryOffset(), PAYLOAD_PLACEHOLDER);
                return;
            }
            return;
        }
        int i = this.mDragPlaceholderPosition;
        this.mDragPlaceholderPosition = -1;
        if (getNavigationAdapter() != null) {
            getNavigationAdapter().notifyItemRemoved(i + getCategoryOffset());
        }
    }

    public void preReplaceDragPlaceholder() {
        this.mPreReplaceDragPlaceholder = true;
    }

    public void postReplaceDragPlaceholder(boolean z) {
        if (this.mPreReplaceDragPlaceholder) {
            this.mPreReplaceDragPlaceholder = false;
            if (!z) {
                removeDragPlaceholder();
            } else {
                notifyDropItemReplacing();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyDropItemReplacing() {
        this.mPreReplaceDragPlaceholder = false;
        int i = this.mDragPlaceholderPosition;
        this.mDragPlaceholderPosition = -1;
        setShowingEmptyDragPlaceholder(false);
        this.mEmptyDragPlaceholderActive = false;
        if (getNavigationAdapter() != null) {
            getNavigationAdapter().notifyItemChanged(i + getCategoryOffset());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refresh() {
        if (!hasAliveItems()) {
            this.mCategory.setCurrentEmpty(true);
            if (this.mAdapter.getEditConfig().multiChoiceMode() && !this.mAdapter.getEditConfig().hideWhenEmpty() && this.mAdapter.getEditConfig().showEmptyDragPlaceholder()) {
                setShowingEmptyDragPlaceholder(true);
            }
        } else {
            this.mCategory.setCurrentEmpty(false);
            setShowingEmptyDragPlaceholder(false);
        }
        if (getNavigationAdapter() != null) {
            getNavigationAdapter().notifyDataSetChanged();
        }
    }

    private void setShowingEmptyDragPlaceholder(boolean z) {
        if (this.mShowingEmptyDragPlaceholder != z) {
            this.mShowingEmptyDragPlaceholder = z;
            this.mDragPlaceholderPosition = z ? 0 : -1;
        }
    }

    public void setNavigatorDragListener(NavigatorDragListener navigatorDragListener) {
        this.mNavigatorDragListener = navigatorDragListener;
    }

    public NavigatorDragListener getNavigatorDragListener() {
        return this.mNavigatorDragListener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getCategoryOffset() {
        return getNavigationAdapter().getCategoryPosition(this.mCategory) + 1;
    }
}
