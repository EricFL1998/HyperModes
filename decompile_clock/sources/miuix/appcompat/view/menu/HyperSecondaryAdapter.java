package miuix.appcompat.view.menu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import java.util.Map;
import miuix.appcompat.R;

/* JADX INFO: loaded from: classes2.dex */
public class HyperSecondaryAdapter extends HyperBaseAdapter {
    private View mHeaderView;
    private Map<Integer, Boolean[]> mSecondaryItemCheckedMap;

    protected HyperSecondaryAdapter(LayoutInflater layoutInflater, List<HyperMenuContract.HyperMenuItem> list, Map<Integer, Boolean[]> map) {
        super(layoutInflater, list);
        this.mSecondaryItemCheckedMap = map;
    }

    @Override // miuix.appcompat.view.menu.HyperBaseAdapter, android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        View view2 = super.getView(i, view, viewGroup);
        if (i == 0) {
            view2.setId(R.id.tag_secondary_popup_menu_item_head);
            this.mHeaderView = view2;
        }
        return view2;
    }

    public void resumeSecondaryItemClickStatus(int i) {
        if (this.mMenuItemList == null || this.mMenuItemList.size() <= 2) {
            return;
        }
        int itemId = this.mMenuItemList.get(0).getItemId();
        Boolean[] boolArr = this.mSecondaryItemCheckedMap.get(Integer.valueOf(itemId));
        if (boolArr == null) {
            boolArr = new Boolean[this.mMenuItemList.size() - 2];
        }
        for (int i2 = 0; i2 < this.mMenuItemList.size(); i2++) {
            HyperMenuContract.HyperMenuItem hyperMenuItem = this.mMenuItemList.get(i2);
            HyperMenuContract.HyperMenuTextItem hyperMenuTextItem = hyperMenuItem instanceof HyperMenuContract.HyperMenuTextItem ? (HyperMenuContract.HyperMenuTextItem) hyperMenuItem : null;
            miuix.appcompat.internal.view.menu.MenuItemImpl menuItem = hyperMenuTextItem != null ? hyperMenuTextItem.getMenuItem() : null;
            if (menuItem != null && menuItem.isCheckable() && !hyperMenuTextItem.isHeaderItem && i2 >= 2) {
                int i3 = i2 - 2;
                boolArr[i3] = Boolean.valueOf(hyperMenuTextItem.getItemId() == i);
                hyperMenuTextItem.checkStatus = Boolean.TRUE.equals(boolArr[i3]) ? HyperMenuContract.CheckableType.CHECKED : HyperMenuContract.CheckableType.NOT_CHECKED;
                menuItem.setChecked(hyperMenuTextItem.isChecked());
            }
        }
        this.mSecondaryItemCheckedMap.put(Integer.valueOf(itemId), boolArr);
        notifyDataSetChanged();
    }

    View getHeaderView() {
        return this.mHeaderView;
    }
}
