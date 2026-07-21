package miuix.navigator.adapter;

import android.graphics.drawable.Drawable;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import miuix.navigator.NavigationItem;
import miuix.navigator.Navigator;
import miuix.navigator.R;
import miuix.navigator.adapter.CategoryAdapter.Item;
import miuix.navigator.navigatorinfo.NavigatorInfo;
import miuix.recyclerview.tool.RecyclerViewHelper;

/* JADX INFO: loaded from: classes3.dex */
public abstract class CategoryAdapter<T extends Item> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    static final int VIEW_TYPE_FOOTER = 1;
    static final int VIEW_TYPE_NORMAL = 0;
    private final EditConfig mEditConfig;
    private EditListener mEditListener;
    private boolean mEditing;
    private FooterAdapter mFooterAdapter;
    private final List<T> mList;
    private NavigationAdapter mNavigationAdapter;
    private boolean mShowIconOnEdit;

    public interface EditListener {
        default boolean onEditAction(CategoryAdapter<?> categoryAdapter, int i) {
            return false;
        }

        default void onFinishEdit(CategoryAdapter<?> categoryAdapter) {
        }

        default void onStartEdit(CategoryAdapter<?> categoryAdapter) {
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int i) {
        return 0;
    }

    public abstract boolean isVisible(int i);

    protected void onApplyEdit() {
    }

    public void onBindNormalView(RecyclerView.ViewHolder viewHolder, T t) {
    }

    public abstract RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup);

    public CategoryAdapter(List<T> list, EditConfig editConfig) {
        Objects.requireNonNull(list, "list cannot be null");
        Objects.requireNonNull(editConfig, "editConfig cannot be null");
        this.mList = list;
        this.mEditConfig = editConfig;
        super.setHasStableIds(true);
        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() { // from class: miuix.navigator.adapter.CategoryAdapter.1
            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeRemoved(int i, int i2) {
                if (CategoryAdapter.this.mEditing) {
                    while (i2 > 0) {
                        CategoryAdapter.this.getList().remove(i);
                        i2--;
                    }
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeMoved(int i, int i2, int i3) {
                if (CategoryAdapter.this.mEditing) {
                    RecyclerViewHelper.handleListMove(CategoryAdapter.this.getList(), i, i2, i3);
                }
            }
        });
        setShowIconOnEdit(editConfig.showIconOnEdit());
    }

    public List<T> getList() {
        return this.mList;
    }

    void onAttachNavigationAdapter(NavigationAdapter navigationAdapter) {
        this.mNavigationAdapter = navigationAdapter;
    }

    public Navigator getNavigator() {
        return getNavigationAdapter().getNavigator();
    }

    protected final void startDrag(RecyclerView.ViewHolder viewHolder) {
        getNavigationAdapter().startDrag(viewHolder);
    }

    protected final void createContextMenu(ContextMenu contextMenu, RecyclerView.ViewHolder viewHolder) {
        NavigationAdapter navigationAdapter = getNavigationAdapter();
        if (navigationAdapter != null) {
            if (getEditConfig().getEditMenu() != null) {
                Iterator<EditMenu> it = getEditConfig().getEditMenu().iterator();
                while (it.hasNext()) {
                    it.next().addTo(contextMenu);
                }
            }
            contextMenu.add(1, -3, 131072, R.string.miuix_navigator_multiple_choice);
            navigationAdapter.setContextHolder(viewHolder);
        }
    }

    final NavigationAdapter getNavigationAdapter() {
        return this.mNavigationAdapter;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == 0) {
            return onCreateViewHolder(viewGroup);
        }
        if (i == 1) {
            return onCreateFooterView(viewGroup);
        }
        throw new UnsupportedOperationException("bad viewType " + i);
    }

    public void setFooterAdapter(FooterAdapter footerAdapter) {
        this.mFooterAdapter = footerAdapter;
    }

    public RecyclerView.ViewHolder onCreateFooterView(ViewGroup viewGroup) {
        FooterAdapter footerAdapter = this.mFooterAdapter;
        if (footerAdapter != null) {
            return footerAdapter.onCreateFooterView(viewGroup);
        }
        return new Holder(viewGroup);
    }

    boolean hasFooterView() {
        return this.mFooterAdapter != null;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public final void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (hasFooterView() && i == getList().size()) {
            onBindFooterView(viewHolder);
        } else {
            onBindNormalView(viewHolder, getList().get(i));
        }
    }

    public void onBindFooterView(RecyclerView.ViewHolder viewHolder) {
        FooterAdapter footerAdapter = this.mFooterAdapter;
        if (footerAdapter != null) {
            footerAdapter.onBindFooterView(viewHolder);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public final void setHasStableIds(boolean z) {
        if (!z) {
            throw new UnsupportedOperationException("you must not set hasStableIds to false");
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return getList().size();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public final long getItemId(int i) {
        return getList().get(i).getId();
    }

    public final EditConfig getEditConfig() {
        return this.mEditConfig;
    }

    public void setEditListener(EditListener editListener) {
        this.mEditListener = editListener;
    }

    public EditListener getEditListener() {
        return this.mEditListener;
    }

    public void startEdit() {
        if (!getEditConfig().isEditable()) {
            throw new UnsupportedOperationException("not editable");
        }
        if (this.mEditing || !getNavigationAdapter().onStartEditMode(this)) {
            return;
        }
        this.mEditing = true;
        onStartEdit();
    }

    public void finishEdit(boolean z) {
        if (z) {
            onApplyEdit();
        }
        getNavigationAdapter().onFinishEditMode(this);
        this.mEditing = false;
        onFinishEdit();
    }

    public void dispatchEditAction(int i) {
        if (getEditListener() == null || !getEditListener().onEditAction(this, i)) {
            return;
        }
        notifyDataSetChanged();
    }

    public void dispatchContextAction(RecyclerView.ViewHolder viewHolder, int i) {
        dispatchEditAction(i);
    }

    protected void onStartEdit() {
        EditListener editListener = this.mEditListener;
        if (editListener != null) {
            editListener.onStartEdit(this);
        }
    }

    protected void onFinishEdit() {
        EditListener editListener = this.mEditListener;
        if (editListener != null) {
            editListener.onFinishEdit(this);
        }
    }

    public boolean isEditing() {
        return this.mEditing;
    }

    @Deprecated
    public void setShowIconOnEdit(boolean z) {
        this.mShowIconOnEdit = z;
    }

    @Deprecated
    public boolean getShowIconOnEdit() {
        return this.mShowIconOnEdit;
    }

    public T findItemWithInfo(NavigatorInfo navigatorInfo) {
        if (navigatorInfo != null && navigatorInfo.isStable()) {
            for (T t : getList()) {
                if (navigatorInfo.equals(t.getNavigatorInfo())) {
                    return t;
                }
            }
        }
        return null;
    }

    public static abstract class EditConfig {
        private static final EditConfig UNEDITABLE;
        private final boolean mHasContextMenu;
        private final boolean mShowEmptyDragPlaceholder;

        public abstract boolean allowReorder();

        public abstract List<EditMenu> getEditMenu();

        public abstract boolean hideWhenEmpty();

        public boolean isEditable() {
            return true;
        }

        public abstract boolean multiChoiceMode();

        public abstract boolean showEditWidget();

        public abstract boolean showEmptyCategoryEditWidget();

        public boolean showIconOnEdit() {
            return false;
        }

        static {
            boolean z = false;
            UNEDITABLE = new EditConfig(z, z) { // from class: miuix.navigator.adapter.CategoryAdapter.EditConfig.1
                @Override // miuix.navigator.adapter.CategoryAdapter.EditConfig
                public boolean allowReorder() {
                    return false;
                }

                @Override // miuix.navigator.adapter.CategoryAdapter.EditConfig
                public List<EditMenu> getEditMenu() {
                    return null;
                }

                @Override // miuix.navigator.adapter.CategoryAdapter.EditConfig
                public boolean hideWhenEmpty() {
                    return true;
                }

                @Override // miuix.navigator.adapter.CategoryAdapter.EditConfig
                public boolean isEditable() {
                    return false;
                }

                @Override // miuix.navigator.adapter.CategoryAdapter.EditConfig
                public boolean multiChoiceMode() {
                    return false;
                }

                @Override // miuix.navigator.adapter.CategoryAdapter.EditConfig
                public boolean showEditWidget() {
                    return false;
                }

                @Override // miuix.navigator.adapter.CategoryAdapter.EditConfig
                public boolean showEmptyCategoryEditWidget() {
                    return false;
                }
            };
        }

        public static EditConfig uneditableConfig() {
            return UNEDITABLE;
        }

        protected EditConfig(boolean z, boolean z2) {
            this.mHasContextMenu = z;
            this.mShowEmptyDragPlaceholder = z2;
        }

        public boolean hasContextMenu() {
            return this.mHasContextMenu;
        }

        public boolean showEmptyDragPlaceholder() {
            return this.mShowEmptyDragPlaceholder;
        }

        public static MenuEditConfig menuConfig(boolean z) {
            return new MenuEditConfig(z);
        }

        public static ListEditConfig listConfig(boolean z) {
            return new ListEditConfig(z);
        }

        public static ListEditConfig listConfig(boolean z, boolean z2) {
            return new ListEditConfig(z, z2);
        }
    }

    public static class MenuEditConfig extends EditConfig {
        private final boolean mShowEditWidget;

        @Override // miuix.navigator.adapter.CategoryAdapter.EditConfig
        public boolean allowReorder() {
            return true;
        }

        @Override // miuix.navigator.adapter.CategoryAdapter.EditConfig
        public List<EditMenu> getEditMenu() {
            return null;
        }

        @Override // miuix.navigator.adapter.CategoryAdapter.EditConfig
        public boolean hideWhenEmpty() {
            return false;
        }

        @Override // miuix.navigator.adapter.CategoryAdapter.EditConfig
        public boolean multiChoiceMode() {
            return false;
        }

        @Override // miuix.navigator.adapter.CategoryAdapter.EditConfig
        public boolean showEmptyCategoryEditWidget() {
            return true;
        }

        private MenuEditConfig(boolean z) {
            super(false, false);
            this.mShowEditWidget = z;
        }

        @Override // miuix.navigator.adapter.CategoryAdapter.EditConfig
        public boolean showEditWidget() {
            return this.mShowEditWidget;
        }
    }

    public static class ListEditConfig extends EditConfig {
        private List<EditMenu> mEditMenus;

        @Override // miuix.navigator.adapter.CategoryAdapter.EditConfig
        public boolean allowReorder() {
            return true;
        }

        @Override // miuix.navigator.adapter.CategoryAdapter.EditConfig
        public boolean hideWhenEmpty() {
            return false;
        }

        @Override // miuix.navigator.adapter.CategoryAdapter.EditConfig
        public boolean multiChoiceMode() {
            return true;
        }

        @Override // miuix.navigator.adapter.CategoryAdapter.EditConfig
        public final boolean showEditWidget() {
            return true;
        }

        @Override // miuix.navigator.adapter.CategoryAdapter.EditConfig
        public boolean showEmptyCategoryEditWidget() {
            return false;
        }

        private ListEditConfig(boolean z) {
            super(z, false);
        }

        private ListEditConfig(boolean z, boolean z2) {
            super(z, z2);
        }

        @Override // miuix.navigator.adapter.CategoryAdapter.EditConfig
        public List<EditMenu> getEditMenu() {
            return this.mEditMenus;
        }

        public ListEditConfig addMenu(EditMenu editMenu) {
            if (this.mEditMenus == null) {
                this.mEditMenus = new ArrayList();
            }
            this.mEditMenus.add(editMenu);
            return this;
        }
    }

    public static final class EditMenu {
        public static final int EDIT_ACTION_DESELECT_ALL = -2;
        public static final int EDIT_ACTION_EDIT = -3;
        public static final int EDIT_ACTION_SELECT_ALL = -1;
        public static final int EDIT_TYPE_MULTIPLE = 2;
        public static final int EDIT_TYPE_NONE = 0;
        public static final int EDIT_TYPE_SINGLE = 1;
        private final int mEditAction;
        private final int mEditType;
        private final Drawable mIcon;
        private final int mIconRes;
        private final boolean mShowInActionMode;
        private final boolean mShowInContextMenu;
        private final CharSequence mTitle;
        private final int mTitleRes;

        public EditMenu(int i, int i2, int i3, int i4, boolean z, boolean z2) {
            this(i, i2, null, i3, null, i4, z, z2);
        }

        public EditMenu(int i, CharSequence charSequence, Drawable drawable, int i2, boolean z, boolean z2) {
            this(i, 0, charSequence, 0, drawable, i2, z, z2);
        }

        private EditMenu(int i, int i2, CharSequence charSequence, int i3, Drawable drawable, int i4, boolean z, boolean z2) {
            this.mEditAction = i;
            this.mTitleRes = i2;
            this.mTitle = charSequence;
            this.mIconRes = i3;
            this.mIcon = drawable;
            this.mEditType = i4;
            this.mShowInContextMenu = z;
            this.mShowInActionMode = z2;
        }

        void addTo(Menu menu) {
            MenuItem menuItemAdd;
            if (this.mShowInActionMode) {
                CharSequence charSequence = this.mTitle;
                if (charSequence != null) {
                    menuItemAdd = menu.add(this.mEditType, this.mEditAction, 0, charSequence);
                } else {
                    menuItemAdd = menu.add(this.mEditType, this.mEditAction, 0, this.mTitleRes);
                }
                Drawable drawable = this.mIcon;
                if (drawable != null) {
                    menuItemAdd.setIcon(drawable);
                    return;
                }
                int i = this.mIconRes;
                if (i != 0) {
                    menuItemAdd.setIcon(i);
                }
            }
        }

        void addTo(ContextMenu contextMenu) {
            if (this.mShowInContextMenu) {
                CharSequence charSequence = this.mTitle;
                if (charSequence != null) {
                    contextMenu.add(0, this.mEditAction, 0, charSequence);
                } else {
                    contextMenu.add(0, this.mEditAction, 0, this.mTitleRes);
                }
            }
        }

        int getEditAction() {
            return this.mEditAction;
        }
    }

    public static class Item implements NavigationItem {
        private NavigatorInfo mNavigatorInfo;

        public int getId() {
            NavigatorInfo navigatorInfo = this.mNavigatorInfo;
            if (navigatorInfo == null) {
                return -1;
            }
            return navigatorInfo.getNavigationId();
        }

        @Override // miuix.navigator.NavigationItem
        public void setNavigatorInfo(NavigatorInfo navigatorInfo) {
            this.mNavigatorInfo = navigatorInfo;
        }

        @Override // miuix.navigator.NavigationItem
        public NavigatorInfo getNavigatorInfo() {
            return this.mNavigatorInfo;
        }
    }
}
