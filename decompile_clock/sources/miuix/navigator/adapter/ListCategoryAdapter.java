package miuix.navigator.adapter;

import android.graphics.drawable.Drawable;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miuix.internal.util.AttributeResolver;
import miuix.navigator.R;
import miuix.navigator.navigatorinfo.NavigatorInfo;

/* JADX INFO: loaded from: classes3.dex */
public class ListCategoryAdapter extends CategoryAdapter<Item> {
    private final int mItemLayoutRes;
    private final WidgetProvider<Item> mWidgetProvider;

    @Override // miuix.navigator.adapter.CategoryAdapter
    public boolean isVisible(int i) {
        return true;
    }

    public ListCategoryAdapter(List<Item> list) {
        this(list, CategoryAdapter.EditConfig.listConfig(false));
    }

    public ListCategoryAdapter(List<Item> list, CategoryAdapter.EditConfig editConfig) {
        this(list, R.layout.miuix_navigator_item_label, editConfig);
    }

    public ListCategoryAdapter(List<Item> list, int i, CategoryAdapter.EditConfig editConfig) {
        this(list, i, null, editConfig);
    }

    public ListCategoryAdapter(List<Item> list, int i, WidgetProvider<Item> widgetProvider, CategoryAdapter.EditConfig editConfig) {
        super(list, editConfig);
        this.mItemLayoutRes = i;
        this.mWidgetProvider = widgetProvider;
    }

    @Override // miuix.navigator.adapter.CategoryAdapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        View viewInflate = LayoutInflater.from(viewGroup.getContext()).inflate(this.mItemLayoutRes, viewGroup, false);
        viewInflate.getBackground().setAlpha(0);
        FrameLayout frameLayout = (FrameLayout) viewInflate.findViewById(android.R.id.widget_frame);
        if (getEditConfig().showEditWidget()) {
            CheckBox checkBox = new CheckBox(viewGroup.getContext());
            checkBox.setId(R.id.miuix_navigator_edit_widget);
            checkBox.setClickable(false);
            checkBox.setFocusable(false);
            checkBox.setVisibility(8);
            frameLayout.addView(checkBox);
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
        final TextView textView = (TextView) viewHolder.itemView.findViewById(android.R.id.title);
        textView.setText(item.mTitle);
        setupIconView((ImageView) viewHolder.itemView.findViewById(android.R.id.icon), item);
        final ViewGroup viewGroup = (ViewGroup) viewHolder.itemView.findViewById(android.R.id.widget_frame);
        setupWidgetFrame(viewGroup, item);
        final CheckBox checkBox = (CheckBox) viewHolder.itemView.findViewById(R.id.miuix_navigator_edit_widget);
        setupEditWidget(checkBox, viewHolder, item);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() { // from class: miuix.navigator.adapter.ListCategoryAdapter$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m1899x85d7f787(viewHolder, checkBox, view);
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
            View view = viewHolder.itemView;
            if (currentInfo != null && currentInfo.equals(item.getNavigatorInfo())) {
                z = true;
            }
            view.setActivated(z);
        }
        setupRearrangeButton(viewHolder.itemView.findViewById(R.id.rearrange_button), viewHolder);
        if (checkBox != null) {
            checkBox.setImportantForAccessibility(2);
        }
        ViewCompat.setAccessibilityDelegate(viewHolder.itemView, new AccessibilityDelegateCompat() { // from class: miuix.navigator.adapter.ListCategoryAdapter.1
            @Override // androidx.core.view.AccessibilityDelegateCompat
            public void onInitializeAccessibilityNodeInfo(View view2, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(view2, accessibilityNodeInfoCompat);
                accessibilityNodeInfoCompat.setCheckable(true);
                textView.setImportantForAccessibility(2);
                TextView textView2 = (TextView) viewGroup.findViewById(R.id.miuix_navigator_item_widget_hint);
                if (textView2 != null) {
                    accessibilityNodeInfoCompat.setContentDescription(((Object) item.mTitle) + textView2.getText().toString());
                } else {
                    accessibilityNodeInfoCompat.setContentDescription(item.mTitle);
                }
                CheckBox checkBox2 = checkBox;
                if (checkBox2 == null || checkBox2.getVisibility() != 0) {
                    accessibilityNodeInfoCompat.setClickable(true ^ view2.isActivated());
                    accessibilityNodeInfoCompat.setChecked(view2.isActivated());
                    accessibilityNodeInfoCompat.removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK);
                } else {
                    accessibilityNodeInfoCompat.setContentDescription(item.mTitle);
                    accessibilityNodeInfoCompat.setClickable(true);
                    accessibilityNodeInfoCompat.setClassName(CheckBox.class.getName());
                    accessibilityNodeInfoCompat.setChecked(checkBox.isChecked());
                }
            }
        });
    }

    /* JADX INFO: renamed from: lambda$onBindNormalView$0$miuix-navigator-adapter-ListCategoryAdapter, reason: not valid java name */
    /* synthetic */ void m1899x85d7f787(RecyclerView.ViewHolder viewHolder, CheckBox checkBox, View view) {
        int bindingAdapterPosition = viewHolder.getBindingAdapterPosition();
        int size = getList().size();
        if (bindingAdapterPosition == -1 || bindingAdapterPosition >= size) {
            return;
        }
        Item item = getList().get(bindingAdapterPosition);
        if (!isEditing()) {
            getNavigator().navigate(item.getNavigatorInfo());
        } else if (checkBox != null) {
            checkBox.setChecked(!item.mChecked);
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

    private void setupEditWidget(CheckBox checkBox, final RecyclerView.ViewHolder viewHolder, Item item) {
        if (checkBox == null) {
            return;
        }
        checkBox.setOnCheckedChangeListener(null);
        if (isEditing()) {
            checkBox.setVisibility(0);
            checkBox.setChecked(item.mChecked);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: miuix.navigator.adapter.ListCategoryAdapter$$ExternalSyntheticLambda2
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    this.f$0.m1900xf724ab71(viewHolder, compoundButton, z);
                }
            });
            return;
        }
        checkBox.setVisibility(8);
    }

    /* JADX INFO: renamed from: lambda$setupEditWidget$1$miuix-navigator-adapter-ListCategoryAdapter, reason: not valid java name */
    /* synthetic */ void m1900xf724ab71(RecyclerView.ViewHolder viewHolder, CompoundButton compoundButton, boolean z) {
        getList().get(viewHolder.getBindingAdapterPosition()).mChecked = z;
        Iterator<Item> it = getList().iterator();
        int i = 0;
        while (it.hasNext()) {
            i += it.next().mChecked ? 1 : 0;
        }
        notifyCheckStateChanged(i == getList().size(), i);
    }

    private void setupRearrangeButton(View view, final RecyclerView.ViewHolder viewHolder) {
        if (isEditing() && getEditConfig().allowReorder()) {
            view.setVisibility(0);
            view.setOnTouchListener(new View.OnTouchListener() { // from class: miuix.navigator.adapter.ListCategoryAdapter$$ExternalSyntheticLambda3
                @Override // android.view.View.OnTouchListener
                public final boolean onTouch(View view2, MotionEvent motionEvent) {
                    return this.f$0.m1903x8c8009ef(viewHolder, view2, motionEvent);
                }
            });
        } else {
            view.setOnTouchListener(null);
            view.setVisibility(8);
        }
    }

    /* JADX INFO: renamed from: lambda$setupRearrangeButton$2$miuix-navigator-adapter-ListCategoryAdapter, reason: not valid java name */
    /* synthetic */ boolean m1903x8c8009ef(RecyclerView.ViewHolder viewHolder, View view, MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() != 0) {
            return true;
        }
        startDrag(viewHolder);
        return true;
    }

    private void setupLongClick(final RecyclerView.ViewHolder viewHolder) {
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() { // from class: miuix.navigator.adapter.ListCategoryAdapter$$ExternalSyntheticLambda0
            @Override // android.view.View.OnLongClickListener
            public final boolean onLongClick(View view) {
                return this.f$0.m1901x9b355c71(viewHolder, view);
            }
        });
        if (!isEditing() && getEditConfig().hasContextMenu()) {
            viewHolder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() { // from class: miuix.navigator.adapter.ListCategoryAdapter$$ExternalSyntheticLambda1
                @Override // android.view.View.OnCreateContextMenuListener
                public final void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                    this.f$0.m1902x35d61ef2(viewHolder, contextMenu, view, contextMenuInfo);
                }
            });
        } else {
            viewHolder.itemView.setOnCreateContextMenuListener(null);
        }
    }

    /* JADX INFO: renamed from: lambda$setupLongClick$3$miuix-navigator-adapter-ListCategoryAdapter, reason: not valid java name */
    /* synthetic */ boolean m1901x9b355c71(RecyclerView.ViewHolder viewHolder, View view) {
        if (!isEditing()) {
            if (getEditConfig().hasContextMenu()) {
                return false;
            }
            view.setPressed(false);
            startEdit(viewHolder);
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

    /* JADX INFO: renamed from: lambda$setupLongClick$4$miuix-navigator-adapter-ListCategoryAdapter, reason: not valid java name */
    /* synthetic */ void m1902x35d61ef2(RecyclerView.ViewHolder viewHolder, ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        createContextMenu(contextMenu, viewHolder);
    }

    @Override // miuix.navigator.adapter.CategoryAdapter
    public void dispatchEditAction(int i) {
        boolean zOnEditAction;
        if (i == -1) {
            Iterator<Item> it = getList().iterator();
            while (it.hasNext()) {
                it.next().mChecked = true;
            }
            notifyItemRangeChanged(0, getList().size());
            notifyCheckStateChanged(true, getList().size());
            return;
        }
        if (i == -2) {
            Iterator<Item> it2 = getList().iterator();
            while (it2.hasNext()) {
                it2.next().mChecked = false;
            }
            notifyItemRangeChanged(0, getList().size());
            notifyCheckStateChanged(false, 0);
            return;
        }
        CategoryAdapter.EditListener editListener = getEditListener();
        if (editListener == null) {
            zOnEditAction = false;
        } else if (editListener instanceof EditListener) {
            zOnEditAction = ((EditListener) getEditListener()).onEditAction(this, i, getSelection());
        } else {
            zOnEditAction = getEditListener().onEditAction(this, i);
        }
        if (zOnEditAction) {
            int size = getList().size();
            int i2 = 0;
            for (int i3 = 0; i3 < size; i3++) {
                if (getList().get(i3).mChecked) {
                    i2++;
                }
            }
            notifyDataSetChanged();
            notifyCheckStateChanged(i2 == size && size > 0, i2);
        }
    }

    private int[] getSelection() {
        int size = getList().size();
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < size; i++) {
            if (getList().get(i).mChecked) {
                arrayList.add(Integer.valueOf(i));
            }
        }
        int size2 = arrayList.size();
        int[] iArr = new int[size2];
        for (int i2 = 0; i2 < size2; i2++) {
            iArr[i2] = ((Integer) arrayList.get(i2)).intValue();
        }
        return iArr;
    }

    @Override // miuix.navigator.adapter.CategoryAdapter
    public void dispatchContextAction(RecyclerView.ViewHolder viewHolder, int i) {
        if (i == -3) {
            startEdit(viewHolder);
            return;
        }
        if (!(getEditListener() instanceof EditListener)) {
            super.dispatchContextAction(viewHolder, i);
            return;
        }
        if (((EditListener) getEditListener()).onEditAction(this, i, new int[]{viewHolder.getBindingAdapterPosition()})) {
            notifyDataSetChanged();
        }
    }

    protected final void notifyCheckStateChanged(boolean z, int i) {
        getNavigationAdapter().onCheckStateChanged(z, i);
    }

    void dispatchSelectionChanged(Menu menu) {
        if (getEditListener() instanceof EditListener) {
            ((EditListener) getEditListener()).onSelectionChanged(this, getSelection(), menu);
        }
    }

    private void startEdit(RecyclerView.ViewHolder viewHolder) {
        int itemCount = getItemCount();
        int bindingAdapterPosition = viewHolder.getBindingAdapterPosition();
        super.startEdit();
        if (bindingAdapterPosition == -1 || bindingAdapterPosition >= itemCount) {
            notifyCheckStateChanged(false, 0);
        } else {
            getList().get(bindingAdapterPosition).mChecked = true;
            notifyCheckStateChanged(itemCount == 1, 1);
        }
    }

    @Override // miuix.navigator.adapter.CategoryAdapter
    protected void onStartEdit() {
        Iterator<Item> it = getList().iterator();
        while (it.hasNext()) {
            it.next().mChecked = false;
        }
        super.onStartEdit();
    }

    public static class Item extends CategoryAdapter.Item {
        private boolean mChecked;
        private final Drawable mIcon;
        private final int mIconRes;
        private final CharSequence mTitle;

        public Item(CharSequence charSequence, Drawable drawable) {
            this.mChecked = false;
            this.mTitle = charSequence;
            this.mIcon = drawable;
            this.mIconRes = 0;
        }

        public Item(CharSequence charSequence, int i) {
            this.mChecked = false;
            this.mTitle = charSequence;
            this.mIconRes = i;
            this.mIcon = null;
        }

        public CharSequence getTitle() {
            return this.mTitle;
        }
    }

    public interface EditListener extends CategoryAdapter.EditListener {
        default void onSelectionChanged(CategoryAdapter<?> categoryAdapter, int[] iArr, Menu menu) {
            menu.setGroupEnabled(1, iArr.length == 1);
            menu.setGroupEnabled(2, iArr.length != 0);
        }

        default boolean onEditAction(CategoryAdapter<?> categoryAdapter, int i, int[] iArr) {
            return onEditAction(categoryAdapter, i);
        }
    }
}
