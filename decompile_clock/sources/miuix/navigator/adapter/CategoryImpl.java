package miuix.navigator.adapter;

import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.TextView;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.recyclerview.widget.RecyclerView;
import miuix.animation.Folme;
import miuix.animation.base.AnimConfig;
import miuix.animation.property.FloatProperty;
import miuix.animation.property.ViewProperty;
import miuix.animation.utils.EaseManager;
import miuix.navigator.Navigator;
import miuix.navigator.R;
import miuix.navigator.draganddrop.NavigatorDragListener;
import miuix.navigator.navigatorinfo.NavigatorInfo;

/* JADX INFO: loaded from: classes3.dex */
public class CategoryImpl extends Navigator.Category implements NavigationAdapterItem {
    private static final int COLLAPSE = 2;
    private static final int EMPTY = 0;
    private static final int EXPAND = 1;
    private AccessibilityManager mAccessibilityManager;
    CategoryAdapterWrapper mAdapter;
    private boolean mEmpty;
    private final int mFooterType;
    private RecyclerView.ViewHolder mHolder;
    private CharSequence mTitle;
    private final int mType;
    private static final AnimConfig ROTATION_CONFIG = new AnimConfig().setEase(EaseManager.getStyle(-2, 0.95f, 0.35f));
    private static final AnimConfig HIDE_CONFIG = new AnimConfig().setEase(EaseManager.getStyle(4, 100.0f));
    private static final AnimConfig SHOW_CONFIG = new AnimConfig().setEase(EaseManager.getStyle(4, 300.0f));
    private boolean mExpanded = true;
    private boolean mCollapsable = true;
    private int mState = 1;

    public CategoryImpl(int i, int i2) {
        this.mType = i;
        this.mFooterType = i2;
    }

    @Override // miuix.navigator.Navigator.Category
    public void setTitle(CharSequence charSequence) {
        this.mTitle = charSequence;
        RecyclerView.ViewHolder viewHolder = this.mHolder;
        if (viewHolder == null || viewHolder.getBindingAdapter() == null) {
            return;
        }
        this.mHolder.getBindingAdapter().notifyItemChanged(this.mHolder.getBindingAdapterPosition());
    }

    @Override // miuix.navigator.Navigator.Category
    public CategoryAdapter<? extends CategoryAdapter.Item> getAdapter() {
        CategoryAdapterWrapper categoryAdapterWrapper = this.mAdapter;
        if (categoryAdapterWrapper == null) {
            return null;
        }
        return categoryAdapterWrapper.getBaseAdapter();
    }

    @Override // miuix.navigator.Navigator.Category
    public void setAdapter(CategoryAdapter<? extends CategoryAdapter.Item> categoryAdapter) {
        if (categoryAdapter.hasFooterView() && this.mFooterType == -1) {
            throw new IllegalArgumentException("footerId must be defined for adapter that has a footer view");
        }
        this.mAdapter = new CategoryAdapterWrapper(categoryAdapter, this);
        RecyclerView.ViewHolder viewHolder = this.mHolder;
        if (viewHolder == null || viewHolder.getBindingAdapter() == null) {
            return;
        }
        this.mHolder.getBindingAdapter().notifyDataSetChanged();
    }

    @Override // miuix.navigator.Navigator.Category
    public void setNavigatorDragListener(NavigatorDragListener navigatorDragListener) {
        CategoryAdapterWrapper categoryAdapterWrapper = this.mAdapter;
        if (categoryAdapterWrapper != null) {
            categoryAdapterWrapper.setNavigatorDragListener(navigatorDragListener);
        }
    }

    @Override // miuix.navigator.Navigator.Category
    public void setCollapsable(boolean z) {
        this.mCollapsable = z;
        if (z || this.mExpanded) {
            return;
        }
        setExpand(true);
    }

    @Override // miuix.navigator.Navigator.Category
    public int getId() {
        return this.mType;
    }

    @Override // miuix.navigator.Navigator.Category
    public int getFooterId() {
        return this.mFooterType;
    }

    @Override // miuix.navigator.Navigator.Category, miuix.navigator.adapter.NavigationAdapterItem
    public CharSequence getTitle() {
        return this.mTitle;
    }

    @Override // miuix.navigator.adapter.NavigationAdapterItem
    public int getItemCount() {
        CategoryAdapterWrapper categoryAdapterWrapper;
        if (!this.mExpanded || (categoryAdapterWrapper = this.mAdapter) == null) {
            return 1;
        }
        int itemCount = categoryAdapterWrapper.getItemCount();
        return this.mAdapter.getBaseAdapter().hasFooterView() ? itemCount + 2 : itemCount + 1;
    }

    @Override // miuix.navigator.adapter.NavigationAdapterItem
    public int getItemViewType(int i) {
        if (i == 0) {
            return -2;
        }
        if (isFooterView(i)) {
            return this.mFooterType;
        }
        return this.mAdapter.getItemViewType(i - 1);
    }

    @Override // miuix.navigator.adapter.NavigationAdapterItem
    public long getItemId(int i) {
        if (i == 0) {
            return this.mType;
        }
        if (isFooterView(i)) {
            return this.mFooterType;
        }
        return this.mAdapter.getItemId(i - 1);
    }

    @Override // miuix.navigator.adapter.NavigationAdapterItem
    public void handleBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (i == 0) {
            this.mHolder = viewHolder;
            setGeneral();
            setOnCategoryClick();
            bindWidget();
            viewHolder.itemView.setTag(R.id.miuix_navigator_drag_helper_token, this.mAdapter);
            return;
        }
        this.mAdapter.bindViewHolder(viewHolder, i - 1);
    }

    @Override // miuix.navigator.adapter.NavigationAdapterItem
    public int findNavigatorInfo(NavigatorInfo navigatorInfo) {
        int navigatorInfoPosition = this.mAdapter.getNavigatorInfoPosition(navigatorInfo);
        if (navigatorInfoPosition >= 0) {
            return navigatorInfoPosition + 1;
        }
        return -1;
    }

    private boolean isFooterView(int i) {
        CategoryAdapterWrapper categoryAdapterWrapper;
        return this.mExpanded && (categoryAdapterWrapper = this.mAdapter) != null && categoryAdapterWrapper.getBaseAdapter().hasFooterView() && i > this.mAdapter.getItemCount();
    }

    private void setGeneral() {
        View view = this.mHolder.itemView;
        boolean z = this.mEmpty && getAdapter().getEditConfig().hideWhenEmpty();
        if (getTitle() == null || z) {
            view.setVisibility(8);
            view.getLayoutParams().height = 0;
        } else {
            view.setVisibility(0);
            view.getLayoutParams().height = -2;
            ((TextView) view.findViewById(android.R.id.title)).setText(this.mTitle);
        }
        view.setTag(R.id.miuix_navigator_category_hide_divider, Boolean.valueOf(z));
    }

    private void setOnCategoryClick() {
        this.mHolder.itemView.setOnClickListener(this.mCollapsable ? new View.OnClickListener() { // from class: miuix.navigator.adapter.CategoryImpl$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m1897lambda$setOnCategoryClick$0$miuixnavigatoradapterCategoryImpl(view);
            }
        } : null);
        ViewCompat.setAccessibilityDelegate(this.mHolder.itemView, new AccessibilityDelegateCompat() { // from class: miuix.navigator.adapter.CategoryImpl.1
            @Override // androidx.core.view.AccessibilityDelegateCompat
            public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
                CategoryImpl categoryImpl = CategoryImpl.this;
                accessibilityNodeInfoCompat.setStateDescription(categoryImpl.getStateDescription(view, categoryImpl.mExpanded));
            }
        });
    }

    /* JADX INFO: renamed from: lambda$setOnCategoryClick$0$miuix-navigator-adapter-CategoryImpl, reason: not valid java name */
    /* synthetic */ void m1897lambda$setOnCategoryClick$0$miuixnavigatoradapterCategoryImpl(View view) {
        if (setExpand(!this.mExpanded)) {
            view.announceForAccessibility(getStateDescription(view, this.mExpanded));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getStateDescription(View view, boolean z) {
        if (z) {
            return view.getResources().getString(R.string.miuix_appcompat_accessibility_expand_state);
        }
        return view.getResources().getString(R.string.miuix_appcompat_accessibility_collapse_state);
    }

    private void bindWidget() {
        View viewFindViewById = this.mHolder.itemView.findViewById(R.id.navigation_item_category_arrow);
        View viewFindViewById2 = this.mHolder.itemView.findViewById(R.id.navigation_item_category_edit);
        int i = this.mState;
        Float fValueOf = Float.valueOf(1.0f);
        Float fValueOf2 = Float.valueOf(0.0f);
        if (i == 0) {
            Folme.useAt(viewFindViewById).state().setTo(ViewProperty.AUTO_ALPHA, fValueOf2);
            Folme.useAt(viewFindViewById2).state().setTo(ViewProperty.AUTO_ALPHA, fValueOf);
        } else if (i == 1) {
            Folme.useAt(viewFindViewById).state().setTo(ViewProperty.ROTATION, 0).setTo(ViewProperty.AUTO_ALPHA, fValueOf);
            Folme.useAt(viewFindViewById2).state().setTo(ViewProperty.AUTO_ALPHA, fValueOf2);
        } else {
            Folme.useAt(viewFindViewById).state().setTo(ViewProperty.ROTATION, 180).setTo(ViewProperty.AUTO_ALPHA, fValueOf);
            Folme.useAt(viewFindViewById2).state().setTo(ViewProperty.AUTO_ALPHA, fValueOf2);
        }
        viewFindViewById2.setOnClickListener(getAdapter().getEditConfig().isEditable() ? new View.OnClickListener() { // from class: miuix.navigator.adapter.CategoryImpl$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m1896lambda$bindWidget$1$miuixnavigatoradapterCategoryImpl(view);
            }
        } : null);
    }

    /* JADX INFO: renamed from: lambda$bindWidget$1$miuix-navigator-adapter-CategoryImpl, reason: not valid java name */
    /* synthetic */ void m1896lambda$bindWidget$1$miuixnavigatoradapterCategoryImpl(View view) {
        getAdapter().startEdit();
    }

    boolean isExpanded() {
        return this.mExpanded;
    }

    private boolean setExpand(boolean z) {
        this.mExpanded = z;
        RecyclerView.ViewHolder viewHolder = this.mHolder;
        if (viewHolder == null || viewHolder.getBindingAdapter() == null || this.mAdapter == null) {
            return false;
        }
        int bindingAdapterPosition = this.mHolder.getBindingAdapterPosition();
        int itemCount = this.mAdapter.getItemCount() + (this.mAdapter.getBaseAdapter().hasFooterView() ? 1 : 0);
        if (itemCount != 0) {
            if (this.mExpanded) {
                this.mHolder.getBindingAdapter().notifyItemRangeInserted(bindingAdapterPosition + 1, itemCount);
            } else {
                this.mHolder.getBindingAdapter().notifyItemRangeRemoved(bindingAdapterPosition + 1, itemCount);
            }
        }
        animateWidgetToCurrent();
        return true;
    }

    void setCurrentEmpty(boolean z) {
        if (this.mEmpty != z) {
            this.mExpanded = true;
            this.mEmpty = z;
        }
        if (getAdapter() != null) {
            animateWidgetToCurrent();
        }
    }

    private void animateWidgetToCurrent() {
        if (this.mEmpty && !getAdapter().getEditConfig().hideWhenEmpty() && getAdapter().getEditConfig().showEmptyCategoryEditWidget()) {
            animateWidgetTo(0);
        } else {
            animateWidgetTo(this.mExpanded ? 1 : 2);
        }
    }

    private void animateWidgetTo(int i) {
        if (this.mState == i) {
            return;
        }
        RecyclerView.ViewHolder viewHolder = this.mHolder;
        if (viewHolder == null || viewHolder.getBindingAdapter() == null) {
            this.mState = i;
            return;
        }
        View viewFindViewById = this.mHolder.itemView.findViewById(R.id.navigation_item_category_arrow);
        View viewFindViewById2 = this.mHolder.itemView.findViewById(R.id.navigation_item_category_edit);
        if (i == 0) {
            Folme.useAt(viewFindViewById).state().add((FloatProperty) ViewProperty.AUTO_ALPHA, 0.0f).to(HIDE_CONFIG);
            Folme.useAt(viewFindViewById2).state().add((FloatProperty) ViewProperty.AUTO_ALPHA, 1.0f).to(SHOW_CONFIG);
        } else {
            int i2 = this.mState;
            if (i2 == 0) {
                Folme.useAt(viewFindViewById2).state().add((FloatProperty) ViewProperty.AUTO_ALPHA, 0.0f).to(HIDE_CONFIG);
                Folme.useAt(viewFindViewById).state().add((FloatProperty) ViewProperty.AUTO_ALPHA, 1.0f).to(SHOW_CONFIG);
            } else if (i2 == 1) {
                Folme.useAt(viewFindViewById).state().add((FloatProperty) ViewProperty.ROTATION, 180).to(ROTATION_CONFIG);
            } else {
                Folme.useAt(viewFindViewById).state().add((FloatProperty) ViewProperty.ROTATION, 0).to(ROTATION_CONFIG);
            }
        }
        this.mState = i;
    }
}
