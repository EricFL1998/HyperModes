package miuix.appcompat.view.menu;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public class HyperMenuAdapter extends HyperBaseAdapter {
    private static final int DEFAULT_FOREIGN_KEY = -1;
    private Map<Integer, ArrayList<HyperMenuContract.HyperMenuItem>> mGroupedMap;
    private boolean mOverflowOnly;
    private boolean mPrimaryCheckedMapFirstModified;
    private Map<Integer, Boolean> mPrimaryItemCheckedMap;
    private List<HyperMenuContract.HyperMenuItem> mPrimaryMenuItems;
    private boolean mSavePrimaryStatusByIdEnabled;
    private boolean mSecondaryCheckedMapFirstModified;
    private Map<Integer, Boolean[]> mSecondaryItemCheckedMap;
    private Map<Integer, BaseAdapter> mSecondaryMenuMap;
    private Map<Integer, Boolean> mUserPreCheckedPrimaryMap;
    private Map<Integer, Boolean[]> mUserPreCheckedSecondaryMap;

    public HyperMenuAdapter(Context context) {
        this(context, null, false);
    }

    public HyperMenuAdapter(Context context, miuix.appcompat.internal.view.menu.MenuBuilder menuBuilder, boolean z) {
        this.mPrimaryMenuItems = new ArrayList();
        this.mSecondaryMenuMap = new HashMap();
        this.mGroupedMap = new HashMap();
        this.mPrimaryItemCheckedMap = new HashMap();
        this.mSavePrimaryStatusByIdEnabled = false;
        this.mSecondaryItemCheckedMap = new HashMap();
        this.mPrimaryCheckedMapFirstModified = true;
        this.mSecondaryCheckedMapFirstModified = true;
        this.mInflater = LayoutInflater.from(context);
        this.mMenuItemList = this.mPrimaryMenuItems;
        this.mOverflowOnly = z;
        if (menuBuilder != null) {
            buildGroupedMenuItems(menuBuilder);
        }
    }

    private void buildGroupedMenuItems(miuix.appcompat.internal.view.menu.MenuBuilder menuBuilder) {
        Map<Integer, BaseAdapter> map;
        if (menuBuilder == null || (map = this.mSecondaryMenuMap) == null || this.mPrimaryMenuItems == null || this.mGroupedMap == null) {
            return;
        }
        map.clear();
        this.mPrimaryMenuItems.clear();
        this.mGroupedMap.clear();
        HyperMenuUtils.checkPrimaryItemVisibility(menuBuilder);
        ArrayList<miuix.appcompat.internal.view.menu.MenuItemImpl> nonActionItems = this.mOverflowOnly ? menuBuilder.getNonActionItems() : menuBuilder.getVisibleItems();
        if (nonActionItems != null) {
            assembleGroupData(this.mGroupedMap, nonActionItems);
        }
        selectPrimaryMenu(this.mGroupedMap);
        if (nonActionItems != null) {
            assembleSecondaryMenu(this.mGroupedMap, nonActionItems);
        }
        removeInvalidatePrimaryItem(true);
    }

    private void assembleSecondaryMenu(Map<Integer, ArrayList<HyperMenuContract.HyperMenuItem>> map, ArrayList<miuix.appcompat.internal.view.menu.MenuItemImpl> arrayList) {
        ArrayList<miuix.appcompat.internal.view.menu.MenuItemImpl> arrayList2 = new ArrayList();
        for (int i = 0; i < arrayList.size(); i++) {
            miuix.appcompat.internal.view.menu.MenuItemImpl menuItemImpl = arrayList.get(i);
            Intent intent = menuItemImpl.getIntent();
            int intExtra = intent != null ? intent.getIntExtra(HyperMenuContract.HYPER_MENU_GROUP_FOREIGN_KEY, -1) : -1;
            if (intExtra != -1) {
                ArrayList<HyperMenuContract.HyperMenuItem> arrayList3 = map.get(Integer.valueOf(menuItemImpl.getGroupId()));
                HyperMenuContract.HyperMenuItem hyperMenuItemFindPrimaryItemByForeignKey = findPrimaryItemByForeignKey(this.mPrimaryMenuItems, intExtra);
                if (arrayList3 != null && hyperMenuItemFindPrimaryItemByForeignKey != null && hyperMenuItemFindPrimaryItemByForeignKey.getMenuItem() != null) {
                    if (hyperMenuItemFindPrimaryItemByForeignKey.getMenuItem().isVisible() != HyperMenuUtils.hasAnyVisibleSubMenuItem(arrayList3)) {
                        arrayList2.add(hyperMenuItemFindPrimaryItemByForeignKey.getMenuItem());
                    }
                    ArrayList arrayList4 = new ArrayList(arrayList3);
                    handleDefaultCheckedStatus(arrayList4, false, hyperMenuItemFindPrimaryItemByForeignKey.getMenuItem().getItemId());
                    HyperMenuContract.HyperMenuTextItem hyperMenuTextItem = new HyperMenuContract.HyperMenuTextItem(hyperMenuItemFindPrimaryItemByForeignKey.getMenuItem());
                    hyperMenuTextItem.isHeaderItem = true;
                    arrayList4.add(0, hyperMenuTextItem);
                    arrayList4.add(1, new HyperMenuContract.HyperMenuDivider());
                    HyperSecondaryAdapter hyperSecondaryAdapter = new HyperSecondaryAdapter(this.mInflater, arrayList4, this.mSecondaryItemCheckedMap);
                    hyperSecondaryAdapter.setOptionalIconsVisible(getOptionalIconsVisible());
                    this.mSecondaryMenuMap.put(Integer.valueOf(hyperMenuItemFindPrimaryItemByForeignKey.getItemId()), hyperSecondaryAdapter);
                }
            }
        }
        for (miuix.appcompat.internal.view.menu.MenuItemImpl menuItemImpl2 : arrayList2) {
            menuItemImpl2.setVisible(!menuItemImpl2.isVisible());
        }
        arrayList2.clear();
    }

    private void selectPrimaryMenu(Map<Integer, ArrayList<HyperMenuContract.HyperMenuItem>> map) {
        Iterator<Integer> it = map.keySet().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            ArrayList<HyperMenuContract.HyperMenuItem> arrayList = map.get(it.next());
            boolean z = false;
            if (arrayList != null && arrayList.size() > 0) {
                z = arrayList.get(0).getMenuItem().getGroupId() == 0;
            }
            List<HyperMenuContract.HyperMenuItem> listFilterVisibleItems = filterVisibleItems(arrayList);
            if (z && !listFilterVisibleItems.isEmpty()) {
                List<HyperMenuContract.HyperMenuItem> list = this.mPrimaryMenuItems;
                list.addAll(list.size(), listFilterVisibleItems);
                this.mPrimaryMenuItems.add(new HyperMenuContract.HyperMenuDivider());
            }
        }
        if (!this.mPrimaryMenuItems.isEmpty()) {
            List<HyperMenuContract.HyperMenuItem> list2 = this.mPrimaryMenuItems;
            list2.remove(list2.size() - 1);
        }
        handleDefaultCheckedStatus(this.mPrimaryMenuItems, true, -1);
    }

    private void assembleGroupData(Map<Integer, ArrayList<HyperMenuContract.HyperMenuItem>> map, ArrayList<miuix.appcompat.internal.view.menu.MenuItemImpl> arrayList) {
        int intExtra;
        for (int i = 0; i < arrayList.size(); i++) {
            miuix.appcompat.internal.view.menu.MenuItemImpl menuItemImpl = arrayList.get(i);
            int groupId = menuItemImpl.getGroupId();
            Intent intent = menuItemImpl.getIntent();
            if (intent != null) {
                groupId = intent.getIntExtra(HyperMenuContract.HYPER_MENU_GROUP_ID, menuItemImpl.getGroupId());
                intExtra = intent.getIntExtra(HyperMenuContract.HYPER_MENU_ITEM_FOREIGN_KEY, -1);
            } else {
                intExtra = -1;
            }
            ArrayList<HyperMenuContract.HyperMenuItem> arrayList2 = map.get(Integer.valueOf(groupId));
            if (arrayList2 == null) {
                arrayList2 = new ArrayList<>();
            }
            HyperMenuContract.HyperMenuTextItem hyperMenuTextItem = new HyperMenuContract.HyperMenuTextItem(menuItemImpl);
            if (intExtra != -1) {
                hyperMenuTextItem.isExpandable = true;
                hyperMenuTextItem.itemForeignKey = intExtra;
            } else {
                hyperMenuTextItem.isExpandable = false;
                hyperMenuTextItem.itemForeignKey = -1;
            }
            arrayList2.add(hyperMenuTextItem);
            map.put(Integer.valueOf(groupId), arrayList2);
        }
    }

    private HyperMenuContract.HyperMenuItem findPrimaryItemByForeignKey(List<HyperMenuContract.HyperMenuItem> list, int i) {
        int i2 = 0;
        while (true) {
            if (i2 >= list.size()) {
                return null;
            }
            HyperMenuContract.HyperMenuItem hyperMenuItem = list.get(i2);
            Intent intent = hyperMenuItem.getMenuItem() != null ? hyperMenuItem.getMenuItem().getIntent() : null;
            int intExtra = intent != null ? intent.getIntExtra(HyperMenuContract.HYPER_MENU_ITEM_FOREIGN_KEY, -1) : -1;
            if (intExtra != -1 && intExtra == i) {
                return hyperMenuItem;
            }
            i2++;
        }
    }

    private void buildMenuItems(miuix.appcompat.internal.view.menu.MenuBuilder menuBuilder) {
        List<HyperMenuContract.HyperMenuItem> list;
        if (menuBuilder == null || this.mSecondaryMenuMap == null || (list = this.mPrimaryMenuItems) == null || this.mGroupedMap == null) {
            return;
        }
        list.clear();
        this.mSecondaryMenuMap.clear();
        this.mGroupedMap.clear();
        HyperMenuUtils.checkPrimaryItemVisibility(menuBuilder);
        ArrayList<miuix.appcompat.internal.view.menu.MenuItemImpl> nonActionItems = this.mOverflowOnly ? menuBuilder.getNonActionItems() : menuBuilder.getVisibleItems();
        ArrayList<Integer> arrayList = new ArrayList<>();
        if (nonActionItems != null) {
            buildDefaultSecondaryMenuData(this.mGroupedMap, nonActionItems, arrayList);
        }
        for (int i = 0; i < arrayList.size(); i++) {
            List<HyperMenuContract.HyperMenuItem> listFilterVisibleItems = filterVisibleItems(this.mGroupedMap.get(arrayList.get(i)));
            if (!listFilterVisibleItems.isEmpty()) {
                List<HyperMenuContract.HyperMenuItem> list2 = this.mPrimaryMenuItems;
                list2.addAll(list2.size(), listFilterVisibleItems);
                this.mPrimaryMenuItems.add(new HyperMenuContract.HyperMenuDivider());
            }
        }
        if (!this.mPrimaryMenuItems.isEmpty()) {
            List<HyperMenuContract.HyperMenuItem> list3 = this.mPrimaryMenuItems;
            list3.remove(list3.size() - 1);
        }
        handleDefaultCheckedStatus(this.mPrimaryMenuItems, true, -1);
        removeInvalidatePrimaryItem(false);
    }

    private List<HyperMenuContract.HyperMenuItem> filterVisibleItems(List<HyperMenuContract.HyperMenuItem> list) {
        ArrayList arrayList = new ArrayList();
        if (list == null) {
            return arrayList;
        }
        for (HyperMenuContract.HyperMenuItem hyperMenuItem : list) {
            if (hyperMenuItem.getMenuItem() != null && hyperMenuItem.getMenuItem().isVisible()) {
                arrayList.add(hyperMenuItem);
            }
        }
        return arrayList;
    }

    private void removeInvalidatePrimaryItem(boolean z) {
        if (this.mPrimaryMenuItems == null || this.mSecondaryMenuMap == null) {
            return;
        }
        ArrayList arrayList = new ArrayList();
        for (HyperMenuContract.HyperMenuItem hyperMenuItem : this.mPrimaryMenuItems) {
            miuix.appcompat.internal.view.menu.MenuItemImpl menuItem = hyperMenuItem.getMenuItem();
            boolean z2 = false;
            boolean z3 = hyperMenuItem instanceof HyperMenuContract.HyperMenuTextItem ? ((HyperMenuContract.HyperMenuTextItem) hyperMenuItem).isExpandable : false;
            if (z) {
                z2 = z3;
            } else if (menuItem != null && menuItem.hasSubMenu()) {
                z2 = true;
            }
            if (menuItem != null && z2 && !this.mSecondaryMenuMap.containsKey(Integer.valueOf(menuItem.getItemId()))) {
                arrayList.add(hyperMenuItem);
            }
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            this.mPrimaryMenuItems.remove((HyperMenuContract.HyperMenuItem) it.next());
        }
        arrayList.clear();
    }

    private void handleDefaultCheckedStatus(List<HyperMenuContract.HyperMenuItem> list, boolean z, int i) {
        if (list == null || list.isEmpty()) {
            return;
        }
        boolean z2 = true;
        boolean z3 = (z || i == -1) ? false : true;
        Boolean[] boolArr = z3 ? this.mSecondaryItemCheckedMap.get(Integer.valueOf(i)) : null;
        if (z3 && boolArr == null) {
            boolArr = new Boolean[list.size()];
        } else {
            z2 = false;
        }
        for (int i2 = 0; i2 < list.size(); i2++) {
            HyperMenuContract.HyperMenuItem hyperMenuItem = list.get(i2);
            HyperMenuContract.HyperMenuTextItem hyperMenuTextItem = hyperMenuItem instanceof HyperMenuContract.HyperMenuTextItem ? (HyperMenuContract.HyperMenuTextItem) hyperMenuItem : null;
            miuix.appcompat.internal.view.menu.MenuItemImpl menuItem = hyperMenuTextItem != null ? hyperMenuTextItem.getMenuItem() : null;
            if (menuItem == null || !menuItem.isCheckable()) {
                if (z) {
                    int itemId = menuItem != null ? menuItem.getItemId() : i2;
                    if (!this.mSavePrimaryStatusByIdEnabled) {
                        itemId = i2;
                    }
                    this.mPrimaryItemCheckedMap.put(Integer.valueOf(itemId), false);
                }
            } else if (z) {
                int itemId2 = this.mSavePrimaryStatusByIdEnabled ? menuItem.getItemId() : i2;
                Boolean bool = this.mPrimaryItemCheckedMap.get(Integer.valueOf(itemId2));
                this.mPrimaryItemCheckedMap.put(Integer.valueOf(itemId2), Boolean.valueOf(bool != null ? bool.booleanValue() : menuItem.isChecked()));
                hyperMenuTextItem.checkStatus = Boolean.TRUE.equals(this.mPrimaryItemCheckedMap.get(Integer.valueOf(itemId2))) ? HyperMenuContract.CheckableType.CHECKED : HyperMenuContract.CheckableType.NOT_CHECKED;
                HyperMenuUtils.setMenuItemChecked(menuItem, hyperMenuTextItem.isChecked());
            } else if (z3) {
                if (z2) {
                    boolArr[i2] = Boolean.valueOf(menuItem.isChecked());
                }
                hyperMenuTextItem.checkStatus = Boolean.TRUE.equals(boolArr[i2]) ? HyperMenuContract.CheckableType.CHECKED : HyperMenuContract.CheckableType.NOT_CHECKED;
            }
        }
        if (z3) {
            this.mSecondaryItemCheckedMap.put(Integer.valueOf(i), boolArr);
        }
    }

    private void buildDefaultSecondaryMenuData(Map<Integer, ArrayList<HyperMenuContract.HyperMenuItem>> map, ArrayList<miuix.appcompat.internal.view.menu.MenuItemImpl> arrayList, ArrayList<Integer> arrayList2) {
        ArrayList<miuix.appcompat.internal.view.menu.MenuItemImpl> arrayList3 = new ArrayList();
        for (int i = 0; i < arrayList.size(); i++) {
            miuix.appcompat.internal.view.menu.MenuItemImpl menuItemImpl = arrayList.get(i);
            int groupId = menuItemImpl.getGroupId();
            if (!arrayList2.contains(Integer.valueOf(groupId))) {
                arrayList2.add(Integer.valueOf(groupId));
            }
            ArrayList<HyperMenuContract.HyperMenuItem> arrayList4 = map.get(Integer.valueOf(groupId));
            if (arrayList4 == null) {
                arrayList4 = new ArrayList<>();
            }
            boolean zHasSubMenu = menuItemImpl.hasSubMenu();
            HyperMenuContract.HyperMenuTextItem hyperMenuTextItem = new HyperMenuContract.HyperMenuTextItem(menuItemImpl);
            hyperMenuTextItem.isExpandable = zHasSubMenu;
            arrayList4.add(hyperMenuTextItem);
            map.put(Integer.valueOf(groupId), arrayList4);
            ArrayList<HyperMenuContract.HyperMenuItem> arrayListBuildDefaultSubMenuData = (zHasSubMenu && (menuItemImpl.getSubMenu() instanceof miuix.appcompat.internal.view.menu.SubMenuBuilder)) ? buildDefaultSubMenuData((miuix.appcompat.internal.view.menu.SubMenuBuilder) menuItemImpl.getSubMenu(), menuItemImpl.getItemId()) : null;
            if (zHasSubMenu && menuItemImpl.isVisible() != HyperMenuUtils.hasAnyVisibleSubMenuItem(arrayListBuildDefaultSubMenuData)) {
                arrayList3.add(menuItemImpl);
            }
            if (arrayListBuildDefaultSubMenuData != null) {
                HyperMenuContract.HyperMenuTextItem hyperMenuTextItem2 = new HyperMenuContract.HyperMenuTextItem(menuItemImpl);
                hyperMenuTextItem2.isHeaderItem = true;
                arrayListBuildDefaultSubMenuData.add(0, hyperMenuTextItem2);
                arrayListBuildDefaultSubMenuData.add(1, new HyperMenuContract.HyperMenuDivider());
                HyperSecondaryAdapter hyperSecondaryAdapter = new HyperSecondaryAdapter(this.mInflater, arrayListBuildDefaultSubMenuData, this.mSecondaryItemCheckedMap);
                hyperSecondaryAdapter.setOptionalIconsVisible(getOptionalIconsVisible());
                this.mSecondaryMenuMap.put(Integer.valueOf(menuItemImpl.getItemId()), hyperSecondaryAdapter);
            }
        }
        for (miuix.appcompat.internal.view.menu.MenuItemImpl menuItemImpl2 : arrayList3) {
            menuItemImpl2.setVisible(!menuItemImpl2.isVisible());
        }
        arrayList3.clear();
    }

    private ArrayList<HyperMenuContract.HyperMenuItem> buildDefaultSubMenuData(miuix.appcompat.internal.view.menu.SubMenuBuilder subMenuBuilder, int i) {
        if (subMenuBuilder != null && i != -1) {
            ArrayList<HyperMenuContract.HyperMenuItem> arrayList = new ArrayList<>();
            ArrayList<miuix.appcompat.internal.view.menu.MenuItemImpl> visibleItems = subMenuBuilder.getVisibleItems();
            if (visibleItems != null && visibleItems.size() != 0) {
                Boolean[] boolArr = this.mSecondaryItemCheckedMap.get(Integer.valueOf(i));
                boolean z = true;
                if (boolArr == null) {
                    boolArr = new Boolean[visibleItems.size()];
                } else if (boolArr.length != visibleItems.size()) {
                    boolArr = new Boolean[visibleItems.size()];
                } else {
                    z = false;
                }
                for (int i2 = 0; i2 < visibleItems.size(); i2++) {
                    miuix.appcompat.internal.view.menu.MenuItemImpl menuItemImpl = visibleItems.get(i2);
                    if (z) {
                        boolArr[i2] = Boolean.valueOf(menuItemImpl.isChecked());
                    }
                    HyperMenuContract.HyperMenuTextItem hyperMenuTextItem = new HyperMenuContract.HyperMenuTextItem(menuItemImpl);
                    if (menuItemImpl != null && menuItemImpl.isCheckable()) {
                        hyperMenuTextItem.checkStatus = Boolean.TRUE.equals(boolArr[i2]) ? HyperMenuContract.CheckableType.CHECKED : HyperMenuContract.CheckableType.NOT_CHECKED;
                        HyperMenuUtils.setMenuItemChecked(menuItemImpl, hyperMenuTextItem.isChecked());
                    }
                    arrayList.add(hyperMenuTextItem);
                }
                this.mSecondaryItemCheckedMap.put(Integer.valueOf(i), boolArr);
                return arrayList;
            }
        }
        return null;
    }

    public void resumePrimaryItemClickStatus(int i, int i2) {
        HyperMenuContract.HyperMenuItem hyperMenuItem;
        miuix.appcompat.internal.view.menu.MenuItemImpl menuItem;
        int groupId;
        List<HyperMenuContract.HyperMenuItem> list = this.mPrimaryMenuItems;
        if (list == null || list.size() == 0) {
            return;
        }
        boolean z = false;
        int i3 = 0;
        while (true) {
            if (i3 >= this.mPrimaryMenuItems.size()) {
                hyperMenuItem = null;
                break;
            }
            hyperMenuItem = this.mPrimaryMenuItems.get(i3);
            if (hyperMenuItem.getItemId() == i) {
                break;
            } else {
                i3++;
            }
        }
        if (hyperMenuItem == null || (menuItem = hyperMenuItem.getMenuItem()) == null) {
            return;
        }
        Intent intent = menuItem.getIntent();
        if (intent != null) {
            groupId = intent.getIntExtra(HyperMenuContract.HYPER_MENU_GROUP_ID, menuItem.getGroupId());
        } else {
            groupId = menuItem.getGroupId();
        }
        ArrayList<HyperMenuContract.HyperMenuItem> arrayList = this.mGroupedMap.get(Integer.valueOf(groupId));
        boolean zIsMixTypeItemGroup = HyperMenuUtils.isMixTypeItemGroup(arrayList);
        if (zIsMixTypeItemGroup && menuItem.isCheckable()) {
            int i4 = this.mSavePrimaryStatusByIdEnabled ? i : i2;
            Boolean bool = this.mPrimaryItemCheckedMap.get(Integer.valueOf(i4));
            if (!(hyperMenuItem instanceof HyperMenuContract.HyperMenuTextItem ? ((HyperMenuContract.HyperMenuTextItem) hyperMenuItem).isExpandable : false)) {
                Map<Integer, Boolean> map = this.mPrimaryItemCheckedMap;
                Integer numValueOf = Integer.valueOf(i4);
                if (bool == null ? !menuItem.isChecked() : !bool.booleanValue()) {
                    z = true;
                }
                map.put(numValueOf, Boolean.valueOf(z));
                HyperMenuUtils.setMenuItemChecked(menuItem, Boolean.TRUE.equals(this.mPrimaryItemCheckedMap.get(Integer.valueOf(i4))));
            }
        }
        int groupIndexAndUpdateStatus = (arrayList == null || zIsMixTypeItemGroup) ? -1 : HyperMenuUtils.getGroupIndexAndUpdateStatus(arrayList, this.mPrimaryItemCheckedMap, i, this.mSavePrimaryStatusByIdEnabled);
        if (!zIsMixTypeItemGroup && groupIndexAndUpdateStatus != -1 && arrayList != null && !this.mSavePrimaryStatusByIdEnabled) {
            HyperMenuUtils.updatePrimaryGroupStatusByPosition(this.mPrimaryItemCheckedMap, i2, groupIndexAndUpdateStatus, arrayList.size());
        }
        notifyDataSetChanged();
    }

    public void setSavePrimaryStatusByIdEnabled(boolean z) {
        this.mSavePrimaryStatusByIdEnabled = z;
    }

    @Override // miuix.appcompat.view.menu.HyperBaseAdapter
    public void preCheckPrimaryItem(Map<Integer, Boolean> map) {
        if (map == null) {
            return;
        }
        this.mUserPreCheckedPrimaryMap = map;
        this.mPrimaryItemCheckedMap.clear();
        Iterator<Integer> it = map.keySet().iterator();
        while (it.hasNext()) {
            int iIntValue = it.next().intValue();
            this.mPrimaryItemCheckedMap.put(Integer.valueOf(iIntValue), map.get(Integer.valueOf(iIntValue)));
        }
        this.mPrimaryCheckedMapFirstModified = true;
    }

    @Override // miuix.appcompat.view.menu.HyperBaseAdapter
    public void preCheckSecondaryItem(Map<Integer, Boolean[]> map) {
        if (map == null) {
            return;
        }
        this.mUserPreCheckedSecondaryMap = map;
        this.mSecondaryItemCheckedMap.clear();
        Iterator<Integer> it = map.keySet().iterator();
        while (it.hasNext()) {
            int iIntValue = it.next().intValue();
            this.mSecondaryItemCheckedMap.put(Integer.valueOf(iIntValue), map.get(Integer.valueOf(iIntValue)));
        }
        this.mSecondaryCheckedMapFirstModified = true;
    }

    public Map<Integer, Boolean> getPrimaryCheckedMap() {
        return this.mPrimaryItemCheckedMap;
    }

    public Map<Integer, Boolean[]> getSecondaryCheckedMap() {
        return this.mSecondaryItemCheckedMap;
    }

    public void copyPrimaryCheckedData(Map<Integer, Boolean> map) {
        Map<Integer, Boolean> map2;
        if (map == null || (map2 = this.mPrimaryItemCheckedMap) == null) {
            return;
        }
        Iterator<Integer> it = map2.keySet().iterator();
        while (it.hasNext()) {
            int iIntValue = it.next().intValue();
            Boolean bool = this.mPrimaryItemCheckedMap.get(Integer.valueOf(iIntValue));
            if (bool != null) {
                map.put(Integer.valueOf(iIntValue), bool);
            }
        }
    }

    public void copySecondaryCheckedData(Map<Integer, Boolean[]> map) {
        Map<Integer, Boolean[]> map2;
        if (map == null || (map2 = this.mSecondaryItemCheckedMap) == null) {
            return;
        }
        Iterator<Integer> it = map2.keySet().iterator();
        while (it.hasNext()) {
            int iIntValue = it.next().intValue();
            Boolean[] boolArr = this.mSecondaryItemCheckedMap.get(Integer.valueOf(iIntValue));
            if (boolArr != null && boolArr.length > 0) {
                Boolean[] boolArr2 = new Boolean[boolArr.length];
                System.arraycopy(boolArr, 0, boolArr2, 0, boolArr.length);
                map.put(Integer.valueOf(iIntValue), boolArr2);
            }
        }
    }

    @Override // miuix.appcompat.view.menu.HyperBaseAdapter, android.widget.Adapter
    public MenuItem getItem(int i) {
        return this.mPrimaryMenuItems.get(i).getMenuItem();
    }

    @Override // miuix.appcompat.view.menu.HyperBaseAdapter
    public HyperMenuContract.HyperMenuItem getHyperMenuItem(int i) {
        return this.mPrimaryMenuItems.get(i);
    }

    @Override // miuix.appcompat.view.menu.HyperBaseAdapter, android.widget.Adapter
    public long getItemId(int i) {
        return this.mPrimaryMenuItems.get(i).getItemId();
    }

    public BaseAdapter getSecondaryAdapterByItemId(long j) {
        return this.mSecondaryMenuMap.get(Integer.valueOf((int) j));
    }

    public boolean hasSubMenu(long j) {
        return this.mSecondaryMenuMap.get(Integer.valueOf((int) j)) != null;
    }

    public boolean hasSubMenu() {
        return !this.mSecondaryMenuMap.isEmpty();
    }

    public void update(miuix.appcompat.internal.view.menu.MenuBuilder menuBuilder) {
        update(menuBuilder, false);
    }

    public void update(miuix.appcompat.internal.view.menu.MenuBuilder menuBuilder, boolean z) {
        Map<Integer, Boolean> map = this.mUserPreCheckedPrimaryMap;
        if (map != null && this.mPrimaryItemCheckedMap != null && !this.mPrimaryCheckedMapFirstModified) {
            preCheckPrimaryItem(map);
        }
        Map<Integer, Boolean[]> map2 = this.mUserPreCheckedSecondaryMap;
        if (map2 != null && this.mSecondaryItemCheckedMap != null && !this.mSecondaryCheckedMapFirstModified) {
            preCheckSecondaryItem(map2);
        }
        if (z) {
            buildGroupedMenuItems(menuBuilder);
        } else {
            buildMenuItems(menuBuilder);
        }
        this.mPrimaryCheckedMapFirstModified = false;
        this.mSecondaryCheckedMapFirstModified = false;
        notifyDataSetChanged();
    }
}
