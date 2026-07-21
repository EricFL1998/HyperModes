package miuix.appcompat.view.menu;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import java.util.List;
import java.util.Map;
import miuix.androidbasewidget.widget.CheckedTextView;
import miuix.appcompat.R;
import miuix.internal.util.AnimHelper;
import miuix.internal.util.TaggingDrawableUtil;

/* JADX INFO: loaded from: classes2.dex */
public class HyperBaseAdapter extends BaseAdapter {
    public static final int TYPE_MENU_DIVIDER = 1;
    public static final int TYPE_MENU_ITEM = 0;
    protected LayoutInflater mInflater;
    protected List<HyperMenuContract.HyperMenuItem> mMenuItemList;
    private boolean mOptionalIconsVisible;

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getViewTypeCount() {
        return 2;
    }

    public void preCheckPrimaryItem(Map<Integer, Boolean> map) {
    }

    public void preCheckSecondaryItem(Map<Integer, Boolean[]> map) {
    }

    public HyperBaseAdapter() {
    }

    public HyperBaseAdapter(LayoutInflater layoutInflater, List<HyperMenuContract.HyperMenuItem> list) {
        this.mInflater = layoutInflater;
        this.mMenuItemList = list;
    }

    public HyperMenuContract.HyperMenuItem getHyperMenuItem(int i) {
        return this.mMenuItemList.get(i);
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.mMenuItemList.size();
    }

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return this.mMenuItemList.get(i).getItemId();
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getItemViewType(int i) {
        return this.mMenuItemList.get(i) instanceof HyperMenuContract.HyperMenuDivider ? 1 : 0;
    }

    @Override // android.widget.Adapter
    public MenuItem getItem(int i) {
        return this.mMenuItemList.get(i).getMenuItem();
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        int itemViewType = getItemViewType(i);
        if (itemViewType == 0) {
            return handleMenuItem(i, view, viewGroup);
        }
        if (itemViewType == 1) {
            return handleDivider(view, viewGroup);
        }
        return null;
    }

    private View handleMenuItem(int i, View view, ViewGroup viewGroup) {
        MenuItemViewHolder menuItemViewHolder;
        HyperMenuContract.HyperMenuTextItem hyperMenuTextItem = (HyperMenuContract.HyperMenuTextItem) this.mMenuItemList.get(i);
        if (view == null || view.getTag().getClass() != MenuItemViewHolder.class) {
            MenuItemViewHolder menuItemViewHolder2 = new MenuItemViewHolder();
            View viewInflate = this.mInflater.inflate(R.layout.miuix_appcompat_hyper_popup_menu_item, viewGroup, false);
            menuItemViewHolder2.titleView = (CheckedTextView) viewInflate.findViewById(android.R.id.text1);
            menuItemViewHolder2.iconView = (ImageView) viewInflate.findViewById(android.R.id.icon);
            menuItemViewHolder2.arrow = (ImageView) viewInflate.findViewById(R.id.arrow);
            viewInflate.setTag(menuItemViewHolder2);
            AnimHelper.addItemPressEffect(viewInflate);
            menuItemViewHolder = menuItemViewHolder2;
            view = viewInflate;
        } else {
            menuItemViewHolder = (MenuItemViewHolder) view.getTag();
        }
        view.setEnabled(hyperMenuTextItem.getMenuItem().isEnabled());
        menuItemViewHolder.titleView.setText(hyperMenuTextItem.getMenuItem().getTitle());
        menuItemViewHolder.titleView.setChecked(hyperMenuTextItem.isChecked());
        menuItemViewHolder.titleView.setEnabled(hyperMenuTextItem.getMenuItem().isEnabled());
        if (this.mOptionalIconsVisible && hyperMenuTextItem.getMenuItem().getIcon() != null) {
            menuItemViewHolder.iconView.setSelected(hyperMenuTextItem.getMenuItem().isChecked());
            menuItemViewHolder.iconView.setImageDrawable(hyperMenuTextItem.getMenuItem().getIcon());
            menuItemViewHolder.iconView.setEnabled(hyperMenuTextItem.getMenuItem().isEnabled());
            menuItemViewHolder.iconView.setVisibility(0);
        } else {
            menuItemViewHolder.iconView.setVisibility(8);
        }
        menuItemViewHolder.arrow.setVisibility((hyperMenuTextItem.isExpandable || hyperMenuTextItem.isHeaderItem) ? 0 : 8);
        TaggingDrawableUtil.updateItemPadding(view, i, this.mMenuItemList.size());
        if (HyperMenuContract.CheckableType.NON_SUPPORT.equals(hyperMenuTextItem.checkStatus)) {
            setAccessibilityDelegateNonCheckable(view, hyperMenuTextItem);
        } else {
            setAccessibilityDelegate(view, menuItemViewHolder.titleView);
        }
        return view;
    }

    private void setAccessibilityDelegate(View view, final CheckedTextView checkedTextView) {
        ViewCompat.setAccessibilityDelegate(view, new AccessibilityDelegateCompat() { // from class: miuix.appcompat.view.menu.HyperBaseAdapter.1
            @Override // androidx.core.view.AccessibilityDelegateCompat
            public void onInitializeAccessibilityNodeInfo(View view2, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(view2, accessibilityNodeInfoCompat);
                accessibilityNodeInfoCompat.setCheckable(true);
                accessibilityNodeInfoCompat.setChecked(checkedTextView.isChecked());
                String string = checkedTextView.getText().toString();
                if (!TextUtils.isEmpty(string)) {
                    accessibilityNodeInfoCompat.setContentDescription(string);
                }
                if (checkedTextView.isChecked()) {
                    return;
                }
                accessibilityNodeInfoCompat.addAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK);
            }
        });
        ViewCompat.setAccessibilityDelegate(checkedTextView, new AccessibilityDelegateCompat() { // from class: miuix.appcompat.view.menu.HyperBaseAdapter.2
            @Override // androidx.core.view.AccessibilityDelegateCompat
            public void onInitializeAccessibilityNodeInfo(View view2, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(view2, accessibilityNodeInfoCompat);
                accessibilityNodeInfoCompat.setCheckable(false);
            }
        });
    }

    private void setAccessibilityDelegateNonCheckable(View view, final HyperMenuContract.HyperMenuTextItem hyperMenuTextItem) {
        ViewCompat.setAccessibilityDelegate(view, new AccessibilityDelegateCompat() { // from class: miuix.appcompat.view.menu.HyperBaseAdapter.3
            @Override // androidx.core.view.AccessibilityDelegateCompat
            public void onInitializeAccessibilityNodeInfo(View view2, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(view2, accessibilityNodeInfoCompat);
                accessibilityNodeInfoCompat.setClickable(true);
                HyperMenuContract.HyperMenuTextItem hyperMenuTextItem2 = hyperMenuTextItem;
                if (hyperMenuTextItem2 != null) {
                    String string = hyperMenuTextItem2.getMenuItem() != null ? hyperMenuTextItem.getMenuItem().getTitle().toString() : null;
                    if (!TextUtils.isEmpty(string)) {
                        accessibilityNodeInfoCompat.setContentDescription(string);
                    }
                    if (hyperMenuTextItem.isExpandable && !hyperMenuTextItem.isHeaderItem) {
                        accessibilityNodeInfoCompat.setStateDescription(view2.getContext().getResources().getString(R.string.miuix_appcompat_accessibility_collapse_state));
                    } else if (hyperMenuTextItem.isHeaderItem) {
                        accessibilityNodeInfoCompat.setStateDescription(view2.getContext().getResources().getString(R.string.miuix_appcompat_accessibility_expand_state));
                    }
                }
            }
        });
    }

    private View handleDivider(View view, ViewGroup viewGroup) {
        if (view != null && view.getTag().getClass() == MenuDividerHolder.class) {
            return view;
        }
        MenuDividerHolder menuDividerHolder = new MenuDividerHolder();
        View viewInflate = this.mInflater.inflate(R.layout.miuix_appcompat_popup_menu_divider, viewGroup, false);
        viewInflate.setTag(menuDividerHolder);
        return viewInflate;
    }

    @Override // android.widget.BaseAdapter, android.widget.ListAdapter
    public boolean isEnabled(int i) {
        return getItemViewType(i) == 0;
    }

    public void setOptionalIconsVisible(boolean z) {
        this.mOptionalIconsVisible = z;
    }

    public boolean getOptionalIconsVisible() {
        return this.mOptionalIconsVisible;
    }

    class MenuItemViewHolder {
        ImageView arrow;
        ImageView iconView;
        CheckedTextView titleView;

        MenuItemViewHolder() {
        }
    }

    class MenuDividerHolder {
        MenuDividerHolder() {
        }
    }
}
