package miuix.navigator.adapter;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.view.ActionMode;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SpringItemTouchHelper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import miuix.animation.Folme;
import miuix.animation.base.AnimConfig;
import miuix.animation.property.FloatProperty;
import miuix.animation.property.ViewProperty;
import miuix.animation.utils.EaseManager;
import miuix.navigator.NavigationFragment;
import miuix.navigator.Navigator;
import miuix.navigator.NavigatorFragmentListener;
import miuix.navigator.NavigatorImpl;
import miuix.navigator.R;
import miuix.navigator.draganddrop.DragHelper;
import miuix.navigator.draganddrop.DragStartFeedback;
import miuix.navigator.draganddrop.NavigatorDragListener;
import miuix.navigator.navigatorinfo.NavigatorInfo;
import miuix.recyclerview.widget.MiuiDefaultItemAnimator;
import miuix.view.EditActionMode;

/* JADX INFO: loaded from: classes3.dex */
public final class NavigationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ActionMode.Callback {
    private ActionMode mActionMode;
    private boolean mAttachedToRecyclerView;
    private int mCategoryLayoutRes;
    private RecyclerView.ViewHolder mContextMenuHolder;
    private int mContextMenuOpenCount;
    private int mDragPlaceholderLayoutRes;
    private int mDragTranslationZ;
    private boolean mEditAllChecked;
    private int mEditCheckedCount;
    private int mEditingType;
    private LabelAdapter mLabelAdapter;
    private int mLabelLayoutRes;
    private final Navigator mNavigator;
    private static final Object PAYLOAD_SELECTED_CHANGED = new Object();
    private static final int[] ATTRS = {R.attr.navigatorCategoryLayout, R.attr.navigatorLabelLayout, R.attr.navigatorDragPlaceholderLayout};
    private final List<NavigationAdapterItem> mList = new ArrayList();
    private final ViewProperty DRAG_BACKGROUND_ALPHA = new ViewProperty("dragBackgroundAlpha") { // from class: miuix.navigator.adapter.NavigationAdapter.1
        @Override // miuix.animation.property.FloatProperty
        public float getValue(View view) {
            return view.getBackground().getAlpha() / 255.0f;
        }

        @Override // miuix.animation.property.FloatProperty
        public void setValue(View view, float f) {
            view.getBackground().setAlpha((int) (f * 255.0f));
        }
    };
    private final ItemTouchHelper mItemTouchHelper = new SpringItemTouchHelper(new ItemTouchHelper.Callback() { // from class: miuix.navigator.adapter.NavigationAdapter.2
        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public boolean isLongPressDragEnabled() {
            return false;
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            RecyclerView.Adapter<? extends RecyclerView.ViewHolder> bindingAdapter = viewHolder.getBindingAdapter();
            if ((bindingAdapter instanceof MenuCategoryAdapter) && ((MenuCategoryAdapter) bindingAdapter).isHolderImmutable(viewHolder)) {
                return 0;
            }
            return makeMovementFlags(3, 0);
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
            RecyclerView.Adapter<? extends RecyclerView.ViewHolder> bindingAdapter = viewHolder2.getBindingAdapter();
            if ((bindingAdapter instanceof MenuCategoryAdapter) && ((MenuCategoryAdapter) bindingAdapter).isHolderImmutable(viewHolder2)) {
                return false;
            }
            int bindingAdapterPosition = viewHolder.getBindingAdapterPosition();
            int bindingAdapterPosition2 = viewHolder2.getBindingAdapterPosition();
            if (NavigationAdapter.this.mEditingAdapter == null) {
                return true;
            }
            NavigationAdapter.this.mEditingAdapter.notifyItemMoved(bindingAdapterPosition, bindingAdapterPosition2);
            return true;
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float f, float f2, int i, boolean z) {
            View view = viewHolder.itemView;
            view.setTranslationX(f);
            view.setTranslationY(f2);
        }
    }) { // from class: miuix.navigator.adapter.NavigationAdapter.3
        private final AnimConfig mDragUpConfig = new AnimConfig().setEase(EaseManager.getStyle(-2, 0.9f, 0.2f));
        private final AnimConfig mDragDownConfig = new AnimConfig().setEase(EaseManager.getStyle(-2, 0.9f, 0.35f));

        @Override // androidx.recyclerview.widget.SpringItemTouchHelper
        public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
            super.onStartDrag(viewHolder);
            Folme.useAt(viewHolder.itemView).state().add((FloatProperty) NavigationAdapter.this.DRAG_BACKGROUND_ALPHA, 1.0f).add((FloatProperty) ViewProperty.TRANSLATION_Z, NavigationAdapter.this.mDragTranslationZ).to(this.mDragUpConfig);
            Folme.getTarget(viewHolder.itemView).setMinVisibleChange(0.00390625f, NavigationAdapter.this.DRAG_BACKGROUND_ALPHA);
        }

        @Override // androidx.recyclerview.widget.SpringItemTouchHelper
        public void onStopDrag(RecyclerView.ViewHolder viewHolder) {
            super.onStopDrag(viewHolder);
            viewHolder.itemView.setHapticFeedbackEnabled(true);
            Folme.useAt(viewHolder.itemView).state().add((FloatProperty) NavigationAdapter.this.DRAG_BACKGROUND_ALPHA, 0.0f).add((FloatProperty) ViewProperty.TRANSLATION_Z, 0).to(this.mDragDownConfig);
        }
    };
    private final DragHelper mDragHelper = new DragHelper();
    private boolean mEditing = false;
    private CategoryAdapter<? extends CategoryAdapter.Item> mEditingAdapter = null;

    @Override // android.view.ActionMode.Callback
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        return true;
    }

    public NavigationAdapter(Navigator navigator) {
        this.mNavigator = navigator;
        setLabelAdapter(new LabelAdapter());
        initResources();
        setHasStableIds(true);
        navigator.addNavigatorFragmentListener(new NavigatorFragmentListener() { // from class: miuix.navigator.adapter.NavigationAdapter.4
            @Override // miuix.navigator.NavigatorFragmentListener
            public void onNavigationVisibilityChanged(int i) {
                if (NavigationAdapter.this.mEditing && (i & 3) == 0) {
                    NavigationAdapter.this.mEditingAdapter.finishEdit(NavigationAdapter.this.mEditingAdapter.getEditConfig().multiChoiceMode());
                }
            }
        });
    }

    public Navigator getNavigator() {
        return this.mNavigator;
    }

    private NavigatorImpl getBaseNavigator() {
        return (NavigatorImpl) this.mNavigator.getBaseNavigator();
    }

    private void initResources() {
        TypedArray typedArrayObtainStyledAttributes = getBaseNavigator().getContext().obtainStyledAttributes(ATTRS);
        int resourceId = typedArrayObtainStyledAttributes.getResourceId(0, R.layout.miuix_navigator_item_category);
        int resourceId2 = typedArrayObtainStyledAttributes.getResourceId(1, R.layout.miuix_navigator_item_label);
        int resourceId3 = typedArrayObtainStyledAttributes.getResourceId(2, R.layout.miuix_navigator_item_drag_placeholder);
        typedArrayObtainStyledAttributes.recycle();
        if (resourceId != this.mCategoryLayoutRes || resourceId2 != this.mLabelLayoutRes || resourceId3 != this.mDragPlaceholderLayoutRes) {
            this.mCategoryLayoutRes = resourceId;
            this.mLabelLayoutRes = resourceId2;
            this.mDragPlaceholderLayoutRes = resourceId3;
            NavigationFragment navigationFragment = (NavigationFragment) this.mNavigator.getFragmentManager().findFragmentByTag("miuix.navigation");
            if (navigationFragment != null && navigationFragment.getRecyclerView() != null && navigationFragment.getRecyclerView().getAdapter() == this) {
                navigationFragment.getRecyclerView().setAdapter(null);
                navigationFragment.getRecyclerView().setAdapter(this);
            }
        }
        this.mDragTranslationZ = getBaseNavigator().getContext().getResources().getDimensionPixelSize(R.dimen.miuix_navigator_item_dragging_translation_z);
        this.mDragHelper.initResources();
    }

    public void onConfigurationChanged(Configuration configuration) {
        initResources();
    }

    public void notifyItemChanged(NavigatorInfo navigatorInfo) {
        int iFindNavigationItemPosition;
        if (navigatorInfo != null && (iFindNavigationItemPosition = findNavigationItemPosition(navigatorInfo)) >= 0) {
            notifyItemChanged(iFindNavigationItemPosition, PAYLOAD_SELECTED_CHANGED);
        }
    }

    public int findNavigationItemPosition(NavigatorInfo navigatorInfo) {
        int itemCount = 0;
        for (NavigationAdapterItem navigationAdapterItem : this.mList) {
            int iFindNavigatorInfo = navigationAdapterItem.findNavigatorInfo(navigatorInfo);
            if (iFindNavigatorInfo >= 0) {
                return itemCount + iFindNavigatorInfo;
            }
            itemCount += navigationAdapterItem.getItemCount();
        }
        return -1;
    }

    public int getCategoryPosition(CategoryImpl categoryImpl) {
        int itemCount = 0;
        for (NavigationAdapterItem navigationAdapterItem : this.mList) {
            if (navigationAdapterItem == categoryImpl) {
                return itemCount;
            }
            itemCount += navigationAdapterItem.getItemCount();
        }
        return -1;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void addLabel(Navigator.Label label, int i) {
        if (!(label instanceof LabelImpl)) {
            throw new UnsupportedOperationException("use newLabel() to make Label instance");
        }
        if (i == -1) {
            this.mList.add((LabelImpl) label);
        } else {
            this.mList.add(i, (LabelImpl) label);
        }
        if (this.mAttachedToRecyclerView) {
            notifyNavigationItemInserted((NavigationAdapterItem) label);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void removeLabel(Navigator.Label label) {
        if (label instanceof LabelImpl) {
            removeNavigationAdapterItemAndNotify((NavigationAdapterItem) label);
        }
    }

    public LabelAdapter getLabelAdapter() {
        return this.mLabelAdapter;
    }

    public void setLabelAdapter(LabelAdapter labelAdapter) {
        this.mLabelAdapter = labelAdapter;
        labelAdapter.onAttachNavigationAdapter(this);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void addCategory(Navigator.Category category, int i) {
        if (!(category instanceof CategoryImpl)) {
            throw new UnsupportedOperationException("use newCategory() to make Category instance");
        }
        if (i == -1) {
            this.mList.add((CategoryImpl) category);
        } else {
            this.mList.add(i, (CategoryImpl) category);
        }
        category.getAdapter().onAttachNavigationAdapter(this);
        if (this.mAttachedToRecyclerView) {
            notifyNavigationItemInserted((NavigationAdapterItem) category);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void removeCategory(Navigator.Category category) {
        if (category instanceof CategoryImpl) {
            if (this.mEditing && this.mEditingAdapter == category.getAdapter()) {
                this.mEditingAdapter.finishEdit(false);
            }
            category.getAdapter().onAttachNavigationAdapter(null);
            removeNavigationAdapterItemAndNotify((NavigationAdapterItem) category);
        }
    }

    public boolean isEditing() {
        return this.mEditing;
    }

    public void dispatchOnDragStart(Map<CategoryAdapterWrapper, DragStartFeedback> map, DragEvent dragEvent) {
        CategoryAdapterWrapper categoryAdapterWrapper;
        NavigatorDragListener navigatorDragListener;
        for (NavigationAdapterItem navigationAdapterItem : this.mList) {
            if (navigationAdapterItem instanceof CategoryImpl) {
                categoryAdapterWrapper = ((CategoryImpl) navigationAdapterItem).mAdapter;
                navigatorDragListener = categoryAdapterWrapper.getNavigatorDragListener();
            } else {
                categoryAdapterWrapper = null;
                navigatorDragListener = null;
            }
            if (navigatorDragListener != null) {
                DragStartFeedback dragStartFeedback = new DragStartFeedback();
                navigatorDragListener.onDragStart(dragEvent, dragStartFeedback);
                if (dragStartFeedback.canInsert() && !categoryAdapterWrapper.getBaseAdapter().getEditConfig().multiChoiceMode()) {
                    dragStartFeedback.setCanInsert(false);
                }
                if (dragStartFeedback.canInsert() || dragStartFeedback.canAccept()) {
                    map.put(categoryAdapterWrapper, dragStartFeedback);
                }
            }
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        this.mAttachedToRecyclerView = true;
        this.mItemTouchHelper.attachToRecyclerView(recyclerView);
        this.mDragHelper.attachToRecyclerView(recyclerView);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        this.mAttachedToRecyclerView = false;
        this.mItemTouchHelper.attachToRecyclerView(null);
        this.mDragHelper.attachToRecyclerView(null);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i < 0) {
            return createInternalView(viewGroup, i);
        }
        for (NavigationAdapterItem navigationAdapterItem : this.mList) {
            if (navigationAdapterItem instanceof CategoryImpl) {
                CategoryImpl categoryImpl = (CategoryImpl) navigationAdapterItem;
                if (i == categoryImpl.getId() || i == categoryImpl.getFooterId()) {
                    return categoryImpl.mAdapter.createViewHolder(viewGroup, i);
                }
            }
        }
        throw new IllegalArgumentException("bad viewType");
    }

    private RecyclerView.ViewHolder createInternalView(ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflaterFrom = LayoutInflater.from(viewGroup.getContext());
        if (i != -1) {
            if (i == -2) {
                return new Holder(layoutInflaterFrom.inflate(this.mCategoryLayoutRes, viewGroup, false));
            }
            if (i == -3) {
                return new Holder(layoutInflaterFrom.inflate(this.mDragPlaceholderLayoutRes, viewGroup, false));
            }
            throw new IllegalArgumentException("bad viewType");
        }
        View viewInflate = layoutInflaterFrom.inflate(this.mLabelLayoutRes, viewGroup, false);
        viewInflate.getBackground().setAlpha(0);
        ((TextView) viewInflate.findViewById(android.R.id.title)).setMaxLines(1);
        Holder holder = new Holder(viewInflate);
        getLabelAdapter().onPrepareViewHolder(holder);
        return holder;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (this.mEditing) {
            this.mEditingAdapter.bindViewHolder(viewHolder, i);
            return;
        }
        for (NavigationAdapterItem navigationAdapterItem : this.mList) {
            int itemCount = navigationAdapterItem.getItemCount();
            if (i < itemCount) {
                navigationAdapterItem.handleBindViewHolder(viewHolder, i);
                return;
            }
            i -= itemCount;
        }
        throw new IndexOutOfBoundsException("pos: " + i + " limit: " + this.mList.size());
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        if (this.mEditing) {
            return this.mEditingAdapter.getItemCount();
        }
        Iterator<NavigationAdapterItem> it = this.mList.iterator();
        int itemCount = 0;
        while (it.hasNext()) {
            itemCount += it.next().getItemCount();
        }
        return itemCount;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int i) {
        if (this.mEditing) {
            return this.mEditingType;
        }
        for (NavigationAdapterItem navigationAdapterItem : this.mList) {
            int itemCount = navigationAdapterItem.getItemCount();
            if (i < itemCount) {
                return navigationAdapterItem.getItemViewType(i);
            }
            i -= itemCount;
        }
        throw new IndexOutOfBoundsException("pos: " + i + " limit: " + this.mList.size());
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public long getItemId(int i) {
        if (this.mEditing) {
            return this.mEditingAdapter.getItemId(i);
        }
        for (NavigationAdapterItem navigationAdapterItem : this.mList) {
            int itemCount = navigationAdapterItem.getItemCount();
            if (i < itemCount) {
                return navigationAdapterItem.getItemId(i);
            }
            i -= itemCount;
        }
        throw new IndexOutOfBoundsException("pos: " + i + " limit: " + this.mList.size());
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int findRelativeAdapterPositionIn(RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter, RecyclerView.ViewHolder viewHolder, int i) {
        int iFindRelativeAdapterPositionIn;
        if (this.mEditing) {
            return this.mEditingAdapter.findRelativeAdapterPositionIn(adapter, viewHolder, i);
        }
        int iFindRelativeAdapterPositionIn2 = super.findRelativeAdapterPositionIn(adapter, viewHolder, i);
        if (iFindRelativeAdapterPositionIn2 != -1) {
            return iFindRelativeAdapterPositionIn2;
        }
        for (NavigationAdapterItem navigationAdapterItem : this.mList) {
            if ((navigationAdapterItem instanceof CategoryImpl) && (iFindRelativeAdapterPositionIn = ((CategoryImpl) navigationAdapterItem).mAdapter.findRelativeAdapterPositionIn(adapter, viewHolder, i - 1)) != -1) {
                return iFindRelativeAdapterPositionIn;
            }
            i -= navigationAdapterItem.getItemCount();
            if (i < 0) {
                break;
            }
        }
        return -1;
    }

    public RecyclerView.ItemAnimator getItemAnimator() {
        return new MiuiDefaultItemAnimator() { // from class: miuix.navigator.adapter.NavigationAdapter.5
            @Override // miuix.recyclerview.widget.MiuiDefaultItemAnimator, androidx.recyclerview.widget.RecyclerView.ItemAnimator
            public boolean canReuseUpdatedViewHolder(RecyclerView.ViewHolder viewHolder, List<Object> list) {
                return list.contains(NavigationAdapter.PAYLOAD_SELECTED_CHANGED) || super.canReuseUpdatedViewHolder(viewHolder, list);
            }
        };
    }

    boolean onStartEditMode(CategoryAdapter<? extends CategoryAdapter.Item> categoryAdapter) {
        NavigationFragment navigationFragment = (NavigationFragment) this.mNavigator.getFragmentManager().findFragmentByTag("miuix.navigation");
        if (navigationFragment == null || navigationFragment.getRecyclerView() == null) {
            return false;
        }
        this.mEditing = true;
        this.mEditingAdapter = categoryAdapter;
        for (NavigationAdapterItem navigationAdapterItem : this.mList) {
            if (navigationAdapterItem instanceof CategoryImpl) {
                CategoryImpl categoryImpl = (CategoryImpl) navigationAdapterItem;
                if (categoryImpl.getAdapter() == categoryAdapter) {
                    this.mEditingType = categoryImpl.getId();
                    break;
                }
            }
        }
        notifyDataSetChanged();
        ((LinearLayoutManager) navigationFragment.getRecyclerView().getLayoutManager()).scrollToPositionWithOffset(0, 0);
        navigationFragment.startActionMode(this);
        getBaseNavigator().onEditingChanged(true);
        return true;
    }

    void onFinishEditMode(CategoryAdapter<? extends CategoryAdapter.Item> categoryAdapter) {
        this.mEditing = false;
        NavigationFragment navigationFragment = (NavigationFragment) this.mNavigator.getFragmentManager().findFragmentByTag("miuix.navigation");
        if (navigationFragment == null || navigationFragment.getRecyclerView() == null) {
            return;
        }
        int itemCount = 0;
        for (NavigationAdapterItem navigationAdapterItem : this.mList) {
            if ((navigationAdapterItem instanceof CategoryImpl) && ((CategoryImpl) navigationAdapterItem).getAdapter() == this.mEditingAdapter) {
                break;
            } else {
                itemCount += navigationAdapterItem.getItemCount();
            }
        }
        this.mEditingAdapter = null;
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) navigationFragment.getRecyclerView().getLayoutManager();
        int iFindFirstVisibleItemPosition = itemCount + linearLayoutManager.findFirstVisibleItemPosition();
        notifyDataSetChanged();
        linearLayoutManager.scrollToPositionWithOffset(iFindFirstVisibleItemPosition, 0);
        getBaseNavigator().onEditingChanged(false);
        ActionMode actionMode = this.mActionMode;
        if (actionMode != null) {
            actionMode.finish();
        }
    }

    void onCheckStateChanged(boolean z, int i) {
        int i2;
        if (z != this.mEditAllChecked) {
            this.mEditAllChecked = z;
            EditActionMode editActionMode = (EditActionMode) this.mActionMode;
            if (z) {
                i2 = R.drawable.miuix_appcompat_action_mode_title_button_deselect_all;
            } else {
                i2 = R.drawable.miuix_appcompat_action_mode_title_button_select_all;
            }
            editActionMode.setButton(16908314, (CharSequence) null, i2);
        }
        if (i != this.mEditCheckedCount) {
            this.mEditCheckedCount = i;
            Resources resources = getBaseNavigator().getContext().getResources();
            int i3 = R.plurals.miuix_appcompat_items_selected;
            int i4 = this.mEditCheckedCount;
            String quantityString = resources.getQuantityString(i3, i4, Integer.valueOf(i4));
            this.mActionMode.setTitle(quantityString);
            ((ListCategoryAdapter) this.mEditingAdapter).dispatchSelectionChanged(this.mActionMode.getMenu());
            if (this.mEditAllChecked || this.mEditCheckedCount == 0) {
                announceAccessibilityEvent(quantityString);
            }
        }
    }

    private void announceAccessibilityEvent(String str) {
        ((EditActionMode) this.mActionMode).announceAccessibilityEvent(str);
    }

    @Override // android.view.ActionMode.Callback
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        this.mActionMode = actionMode;
        if (this.mEditingAdapter.getEditConfig().multiChoiceMode()) {
            prepareEditListActionMode(actionMode);
        } else {
            prepareEditMenuActionMode(actionMode);
        }
        menu.clear();
        if (this.mEditingAdapter.getEditConfig().getEditMenu() == null) {
            return true;
        }
        Iterator<CategoryAdapter.EditMenu> it = this.mEditingAdapter.getEditConfig().getEditMenu().iterator();
        while (it.hasNext()) {
            it.next().addTo(menu);
        }
        announceAccessibilityEvent(getBaseNavigator().getContext().getResources().getString(R.string.miuix_navigator_toolbar_screen_bottom));
        return true;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void prepareEditListActionMode(ActionMode actionMode) {
        this.mEditAllChecked = false;
        this.mEditCheckedCount = 0;
        Resources resources = getBaseNavigator().getContext().getResources();
        int i = R.plurals.miuix_appcompat_items_selected;
        int i2 = this.mEditCheckedCount;
        actionMode.setTitle(resources.getQuantityString(i, i2, Integer.valueOf(i2)));
        EditActionMode editActionMode = (EditActionMode) actionMode;
        editActionMode.setButton(16908313, (CharSequence) null, R.drawable.miuix_appcompat_action_mode_title_button_cancel);
        editActionMode.setButton(16908314, (CharSequence) null, R.drawable.miuix_appcompat_action_mode_title_button_select_all);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void prepareEditMenuActionMode(ActionMode actionMode) {
        actionMode.setTitle(R.string.miuix_appcompat_edit);
        EditActionMode editActionMode = (EditActionMode) actionMode;
        editActionMode.setButton(16908313, (CharSequence) null, R.drawable.miuix_appcompat_action_mode_title_button_cancel);
        editActionMode.setButton(16908314, (CharSequence) null, R.drawable.miuix_appcompat_action_mode_title_button_confirm);
    }

    @Override // android.view.ActionMode.Callback
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 16908313) {
            this.mEditingAdapter.finishEdit(false);
            actionMode.finish();
            return true;
        }
        if (itemId == 16908314) {
            if (this.mEditingAdapter.getEditConfig().multiChoiceMode()) {
                this.mEditingAdapter.dispatchEditAction(this.mEditAllChecked ? -2 : -1);
            } else {
                this.mEditingAdapter.finishEdit(true);
                actionMode.finish();
            }
            return true;
        }
        if (this.mEditingAdapter.getEditConfig().getEditMenu() != null) {
            for (CategoryAdapter.EditMenu editMenu : this.mEditingAdapter.getEditConfig().getEditMenu()) {
                if (itemId == editMenu.getEditAction()) {
                    this.mEditingAdapter.dispatchEditAction(editMenu.getEditAction());
                    return true;
                }
            }
        }
        return false;
    }

    @Override // android.view.ActionMode.Callback
    public void onDestroyActionMode(ActionMode actionMode) {
        this.mActionMode = null;
        if (this.mEditing) {
            CategoryAdapter<? extends CategoryAdapter.Item> categoryAdapter = this.mEditingAdapter;
            categoryAdapter.finishEdit(categoryAdapter.getEditConfig().multiChoiceMode());
        }
    }

    public void startDrag(RecyclerView.ViewHolder viewHolder) {
        this.mItemTouchHelper.startDrag(viewHolder);
    }

    public void setContextHolder(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder == null) {
            int i = this.mContextMenuOpenCount - 1;
            this.mContextMenuOpenCount = i;
            if (i > 0) {
                return;
            }
        }
        RecyclerView.ViewHolder viewHolder2 = this.mContextMenuHolder;
        if (viewHolder2 != null) {
            viewHolder2.itemView.setSelected(false);
        }
        this.mContextMenuHolder = viewHolder;
        if (viewHolder != null) {
            this.mContextMenuOpenCount++;
            viewHolder.itemView.setSelected(true);
        }
    }

    public void onContextEditAction(int i) {
        RecyclerView.Adapter<? extends RecyclerView.ViewHolder> bindingAdapter = this.mContextMenuHolder.getBindingAdapter();
        if (bindingAdapter instanceof CategoryAdapter) {
            ((CategoryAdapter) bindingAdapter).dispatchContextAction(this.mContextMenuHolder, i);
        }
    }

    private void notifyNavigationItemInserted(NavigationAdapterItem navigationAdapterItem) {
        NavigationAdapterItem next;
        Iterator<NavigationAdapterItem> it = this.mList.iterator();
        int itemCount = 0;
        while (it.hasNext() && navigationAdapterItem != (next = it.next())) {
            itemCount += next.getItemCount();
        }
        notifyItemRangeInserted(itemCount, navigationAdapterItem.getItemCount());
    }

    private void removeNavigationAdapterItemAndNotify(NavigationAdapterItem navigationAdapterItem) {
        int itemCount = 0;
        for (NavigationAdapterItem navigationAdapterItem2 : this.mList) {
            if (navigationAdapterItem == navigationAdapterItem2) {
                this.mList.remove(navigationAdapterItem2);
                if (this.mAttachedToRecyclerView) {
                    notifyItemRangeRemoved(itemCount, navigationAdapterItem2.getItemCount());
                    return;
                }
                return;
            }
            itemCount += navigationAdapterItem2.getItemCount();
        }
    }
}
