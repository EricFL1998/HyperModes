package miuix.navigator.adapter;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import miuix.internal.util.AttributeResolver;
import miuix.navigator.R;
import miuix.navigator.navigatorinfo.DetailFragmentNavInfo;
import miuix.navigator.navigatorinfo.NavigatorInfo;
import miuix.slidingwidget.widget.SlidingButton;

/* JADX INFO: loaded from: classes3.dex */
public class MenuCategoryAdapter extends CategoryAdapter<Item> {
    private int mImmutableCount;
    private final int mItemLayoutRes;
    private List<OriginItem> mOriginList;
    private final WidgetProvider<Item> mWidgetProvider;

    public MenuCategoryAdapter(List<Item> list) {
        this(list, R.layout.miuix_navigator_item_label);
    }

    public MenuCategoryAdapter(List<Item> list, int i) {
        this(list, i, null, CategoryAdapter.EditConfig.menuConfig(true));
    }

    public MenuCategoryAdapter(List<Item> list, int i, WidgetProvider<Item> widgetProvider, CategoryAdapter.EditConfig editConfig) {
        super(list, editConfig);
        this.mItemLayoutRes = i;
        this.mWidgetProvider = widgetProvider;
    }

    public void setImmutableCount(int i) {
        this.mImmutableCount = i;
    }

    public int getImmutableCount() {
        return this.mImmutableCount;
    }

    boolean isHolderImmutable(RecyclerView.ViewHolder viewHolder) {
        int immutableCount = getImmutableCount();
        if (immutableCount == 0) {
            return false;
        }
        if (immutableCount > 0) {
            return viewHolder.getBindingAdapterPosition() < immutableCount;
        }
        return immutableCount <= viewHolder.getBindingAdapterPosition() - viewHolder.getBindingAdapter().getItemCount();
    }

    @Override // miuix.navigator.adapter.CategoryAdapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        View viewInflate = LayoutInflater.from(viewGroup.getContext()).inflate(this.mItemLayoutRes, viewGroup, false);
        viewInflate.getBackground().setAlpha(0);
        FrameLayout frameLayout = (FrameLayout) viewInflate.findViewById(android.R.id.widget_frame);
        if (getEditConfig().showEditWidget()) {
            SlidingButton slidingButton = new SlidingButton(viewGroup.getContext());
            slidingButton.setId(R.id.miuix_navigator_edit_widget);
            slidingButton.setClickable(false);
            slidingButton.setFocusable(false);
            slidingButton.setVisibility(8);
            frameLayout.addView(slidingButton);
        }
        ImageView imageView = (ImageView) viewInflate.findViewById(R.id.rearrange_button);
        imageView.setImageResource(AttributeResolver.resolve(imageView.getContext(), R.attr.navigatorRearrangeIcon));
        WidgetProvider<Item> widgetProvider = this.mWidgetProvider;
        if (widgetProvider != null) {
            widgetProvider.onPrepareWidget(frameLayout);
        }
        return new Holder(viewInflate);
    }

    @Override // miuix.navigator.adapter.CategoryAdapter
    public void onBindNormalView(final RecyclerView.ViewHolder viewHolder, final Item item) {
        boolean z = false;
        viewHolder.itemView.setPressed(false);
        if (Build.VERSION.SDK_INT >= 28) {
            viewHolder.itemView.setOutlineAmbientShadowColor(ViewCompat.MEASURED_STATE_MASK);
            viewHolder.itemView.setOutlineSpotShadowColor(BasicMeasure.EXACTLY);
        }
        ((TextView) viewHolder.itemView.findViewById(android.R.id.title)).setText(item.mTitle);
        setupIconView((ImageView) viewHolder.itemView.findViewById(android.R.id.icon), item);
        setupWidgetFrame((ViewGroup) viewHolder.itemView.findViewById(android.R.id.widget_frame), item);
        final SlidingButton slidingButton = (SlidingButton) viewHolder.itemView.findViewById(R.id.miuix_navigator_edit_widget);
        if (slidingButton != null) {
            slidingButton.setImportantForAccessibility(2);
        }
        setupEditWidget(slidingButton, viewHolder, item);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() { // from class: miuix.navigator.adapter.MenuCategoryAdapter$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m1904x8f7b58a6(viewHolder, slidingButton, view);
            }
        });
        if (getEditConfig().isEditable()) {
            setupLongClick(viewHolder);
        } else {
            viewHolder.itemView.setLongClickable(false);
        }
        if (isEditing()) {
            viewHolder.itemView.setActivated(false);
        } else {
            NavigatorInfo currentInfo = getNavigator().getCurrentInfo();
            viewHolder.itemView.setActivated(currentInfo != null && currentInfo.equals(item.getNavigatorInfo()));
        }
        boolean zIsHolderImmutable = isHolderImmutable(viewHolder);
        if (isEditing() && zIsHolderImmutable) {
            z = true;
        }
        float f = z ? 0.3f : 1.0f;
        viewHolder.itemView.setImportantForAccessibility(z ? 2 : 1);
        if (Build.VERSION.SDK_INT >= 29) {
            viewHolder.itemView.setTransitionAlpha(f);
        } else {
            viewHolder.itemView.setAlpha(f);
        }
        setupRearrangeButton(viewHolder.itemView.findViewById(R.id.rearrange_button), viewHolder, zIsHolderImmutable);
        ViewCompat.setAccessibilityDelegate(viewHolder.itemView, new AccessibilityDelegateCompat() { // from class: miuix.navigator.adapter.MenuCategoryAdapter.1
            @Override // androidx.core.view.AccessibilityDelegateCompat
            public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
                SlidingButton slidingButton2 = slidingButton;
                if (slidingButton2 != null && slidingButton2.getVisibility() == 0) {
                    accessibilityNodeInfoCompat.setClickable(true);
                    accessibilityNodeInfoCompat.setCheckable(true);
                    accessibilityNodeInfoCompat.setChecked(view.isActivated() || slidingButton.isChecked());
                    accessibilityNodeInfoCompat.setContentDescription(item.mTitle);
                    accessibilityNodeInfoCompat.setClassName(Switch.class.getName());
                    return;
                }
                if (MenuCategoryAdapter.this.isEditing()) {
                    accessibilityNodeInfoCompat.setClickable(false);
                    accessibilityNodeInfoCompat.setCheckable(false);
                    accessibilityNodeInfoCompat.removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK);
                } else if (item.getNavigatorInfo() instanceof DetailFragmentNavInfo) {
                    accessibilityNodeInfoCompat.setClickable(true);
                    accessibilityNodeInfoCompat.setCheckable(false);
                } else if (item.getNavigatorInfo() != null) {
                    accessibilityNodeInfoCompat.setCheckable(true);
                    accessibilityNodeInfoCompat.setChecked(view.isActivated());
                    accessibilityNodeInfoCompat.setClickable(!view.isActivated());
                    accessibilityNodeInfoCompat.removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK);
                }
            }
        });
    }

    /* JADX INFO: renamed from: lambda$onBindNormalView$0$miuix-navigator-adapter-MenuCategoryAdapter, reason: not valid java name */
    /* synthetic */ void m1904x8f7b58a6(RecyclerView.ViewHolder viewHolder, SlidingButton slidingButton, View view) {
        int bindingAdapterPosition = viewHolder.getBindingAdapterPosition();
        if (bindingAdapterPosition >= 0) {
            Item item = getList().get(bindingAdapterPosition);
            if (!isEditing()) {
                getNavigator().navigate(item.getNavigatorInfo());
            } else if (slidingButton != null) {
                slidingButton.setChecked(!item.mVisible);
            }
        }
    }

    public WidgetProvider<Item> getWidgetProvider() {
        return this.mWidgetProvider;
    }

    private void setupIconView(ImageView imageView, Item item) {
        if (item.mIcon != null) {
            imageView.setImageDrawable(item.mIcon);
        } else {
            imageView.setImageResource(item.mIconRes);
        }
        if (isEditing()) {
            imageView.setVisibility(getShowIconOnEdit() ? 0 : 8);
        } else {
            imageView.setVisibility(0);
        }
    }

    private void setupWidgetFrame(ViewGroup viewGroup, Item item) {
        if (viewGroup == null) {
            return;
        }
        if (getWidgetProvider() != null) {
            viewGroup.setVisibility(0);
            getWidgetProvider().onSetupWidget(viewGroup, item, isEditing());
        } else if (isEditing()) {
            viewGroup.setVisibility(0);
        } else {
            viewGroup.setVisibility(8);
        }
    }

    private void setupEditWidget(SlidingButton slidingButton, final RecyclerView.ViewHolder viewHolder, Item item) {
        if (slidingButton == null) {
            return;
        }
        slidingButton.setOnCheckedChangeListener(null);
        if (isEditing() && !isHolderImmutable(viewHolder)) {
            slidingButton.setVisibility(0);
            slidingButton.setChecked(item.mVisible);
            slidingButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: miuix.navigator.adapter.MenuCategoryAdapter$$ExternalSyntheticLambda3
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    this.f$0.m1905xc80c90(viewHolder, compoundButton, z);
                }
            });
            return;
        }
        slidingButton.setVisibility(8);
    }

    /* JADX INFO: renamed from: lambda$setupEditWidget$1$miuix-navigator-adapter-MenuCategoryAdapter, reason: not valid java name */
    /* synthetic */ void m1905xc80c90(RecyclerView.ViewHolder viewHolder, CompoundButton compoundButton, boolean z) {
        int bindingAdapterPosition = viewHolder.getBindingAdapterPosition();
        if (bindingAdapterPosition < 0 || bindingAdapterPosition >= getList().size()) {
            return;
        }
        getList().get(bindingAdapterPosition).mVisible = z;
    }

    private void setupRearrangeButton(View view, final RecyclerView.ViewHolder viewHolder, boolean z) {
        if (isEditing() && getEditConfig().allowReorder() && !z) {
            view.setVisibility(0);
            view.setOnTouchListener(new View.OnTouchListener() { // from class: miuix.navigator.adapter.MenuCategoryAdapter$$ExternalSyntheticLambda4
                @Override // android.view.View.OnTouchListener
                public final boolean onTouch(View view2, MotionEvent motionEvent) {
                    return this.f$0.m1908x96236b0e(viewHolder, view2, motionEvent);
                }
            });
        } else {
            view.setOnTouchListener(null);
            view.setVisibility(8);
        }
    }

    /* JADX INFO: renamed from: lambda$setupRearrangeButton$2$miuix-navigator-adapter-MenuCategoryAdapter, reason: not valid java name */
    /* synthetic */ boolean m1908x96236b0e(RecyclerView.ViewHolder viewHolder, View view, MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() != 0) {
            return true;
        }
        startDrag(viewHolder);
        return true;
    }

    private void setupLongClick(final RecyclerView.ViewHolder viewHolder) {
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() { // from class: miuix.navigator.adapter.MenuCategoryAdapter$$ExternalSyntheticLambda1
            @Override // android.view.View.OnLongClickListener
            public final boolean onLongClick(View view) {
                return this.f$0.m1906xa4d8bd90(viewHolder, view);
            }
        });
        if (getEditConfig().hasContextMenu()) {
            viewHolder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() { // from class: miuix.navigator.adapter.MenuCategoryAdapter$$ExternalSyntheticLambda2
                @Override // android.view.View.OnCreateContextMenuListener
                public final void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                    this.f$0.m1907x3f798011(viewHolder, contextMenu, view, contextMenuInfo);
                }
            });
        }
    }

    /* JADX INFO: renamed from: lambda$setupLongClick$3$miuix-navigator-adapter-MenuCategoryAdapter, reason: not valid java name */
    /* synthetic */ boolean m1906xa4d8bd90(RecyclerView.ViewHolder viewHolder, View view) {
        if (!isEditing()) {
            if (getEditConfig().hasContextMenu()) {
                return false;
            }
            view.setPressed(false);
            startEdit();
            return true;
        }
        if (!getEditConfig().allowReorder()) {
            return false;
        }
        view.setPressed(false);
        startDrag(viewHolder);
        view.setHapticFeedbackEnabled(false);
        return true;
    }

    /* JADX INFO: renamed from: lambda$setupLongClick$4$miuix-navigator-adapter-MenuCategoryAdapter, reason: not valid java name */
    /* synthetic */ void m1907x3f798011(RecyclerView.ViewHolder viewHolder, ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        createContextMenu(contextMenu, viewHolder);
    }

    @Override // miuix.navigator.adapter.CategoryAdapter
    public boolean isVisible(int i) {
        return isEditing() || getList().get(i).mVisible;
    }

    @Override // miuix.navigator.adapter.CategoryAdapter
    protected void onStartEdit() {
        int size = getList().size();
        this.mOriginList = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            this.mOriginList.add(new OriginItem(getList().get(i)));
        }
        super.onStartEdit();
    }

    @Override // miuix.navigator.adapter.CategoryAdapter
    protected void onApplyEdit() {
        this.mOriginList = null;
        super.onApplyEdit();
    }

    @Override // miuix.navigator.adapter.CategoryAdapter
    protected void onFinishEdit() {
        List<OriginItem> list = this.mOriginList;
        if (list != null) {
            for (int size = list.size() - 1; size >= 0; size--) {
                OriginItem originItem = this.mOriginList.get(size);
                getList().set(size, originItem.mOrigin);
                originItem.mOrigin.mVisible = originItem.mVisible;
            }
            this.mOriginList = null;
        }
        notifyDataSetChanged();
        super.onFinishEdit();
    }

    public static class Item extends CategoryAdapter.Item {
        private final Drawable mIcon;
        private final int mIconRes;
        private final String mTitle;
        private boolean mVisible;

        public Item(String str, Drawable drawable) {
            this.mVisible = true;
            this.mTitle = str;
            this.mIcon = drawable;
            this.mIconRes = 0;
        }

        public Item(String str, int i) {
            this.mVisible = true;
            this.mTitle = str;
            this.mIconRes = i;
            this.mIcon = null;
        }

        public void setVisible(boolean z) {
            this.mVisible = z;
        }
    }

    private static class OriginItem {
        private final Item mOrigin;
        private final boolean mVisible;

        OriginItem(Item item) {
            this.mOrigin = item;
            this.mVisible = item.mVisible;
        }
    }
}
