package miuix.appcompat.view.menu;

import android.view.MenuItem;
import android.view.SubMenu;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public class HyperMenuUtils {
    public static void updatePrimaryGroupStatusByPosition(Map<Integer, Boolean> map, int i, int i2, int i3) {
        int i4 = i - i2;
        int i5 = ((i + i3) - i2) - 1;
        int i6 = i4;
        while (i6 >= i4 && i6 <= i5) {
            map.put(Integer.valueOf(i6), Boolean.valueOf(i6 == i4 + i2));
            i6++;
        }
    }

    public static int getGroupIndexAndUpdateStatus(ArrayList<HyperMenuContract.HyperMenuItem> arrayList, Map<Integer, Boolean> map, int i, boolean z) {
        int i2 = -1;
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            HyperMenuContract.HyperMenuItem hyperMenuItem = arrayList.get(i3);
            if (hyperMenuItem instanceof HyperMenuContract.HyperMenuTextItem) {
                HyperMenuContract.HyperMenuTextItem hyperMenuTextItem = (HyperMenuContract.HyperMenuTextItem) hyperMenuItem;
                miuix.appcompat.internal.view.menu.MenuItemImpl menuItem = hyperMenuTextItem.getMenuItem();
                if (hyperMenuTextItem.getItemId() == i) {
                    i2 = i3;
                }
                if (menuItem != null) {
                    if (menuItem.isCheckable() && !hyperMenuTextItem.isExpandable) {
                        hyperMenuTextItem.checkStatus = hyperMenuTextItem.getItemId() == i ? HyperMenuContract.CheckableType.CHECKED : HyperMenuContract.CheckableType.NOT_CHECKED;
                        menuItem.setChecked(hyperMenuTextItem.isChecked());
                    }
                    if (z) {
                        map.put(Integer.valueOf(menuItem.getItemId()), Boolean.valueOf(menuItem.getItemId() == i));
                    }
                }
            }
        }
        return i2;
    }

    public static boolean isMixTypeItemGroup(ArrayList<HyperMenuContract.HyperMenuItem> arrayList) {
        HyperMenuContract.HyperMenuTextItem hyperMenuTextItem;
        miuix.appcompat.internal.view.menu.MenuItemImpl menuItem;
        if (arrayList == null) {
            return false;
        }
        int[] iArr = {0, 0, 0};
        for (int i = 0; i < arrayList.size(); i++) {
            HyperMenuContract.HyperMenuItem hyperMenuItem = arrayList.get(i);
            if ((hyperMenuItem instanceof HyperMenuContract.HyperMenuTextItem) && (menuItem = (hyperMenuTextItem = (HyperMenuContract.HyperMenuTextItem) hyperMenuItem).getMenuItem()) != null && menuItem.isVisible()) {
                countItemType(hyperMenuTextItem, menuItem, iArr);
            }
        }
        return (iArr[0] > 0 && iArr[2] > 0) || (iArr[1] > 0 && iArr[2] > 0);
    }

    private static void countItemType(HyperMenuContract.HyperMenuTextItem hyperMenuTextItem, miuix.appcompat.internal.view.menu.MenuItemImpl menuItemImpl, int[] iArr) {
        if (iArr == null || iArr.length < 3) {
            return;
        }
        if (hyperMenuTextItem.isExpandable) {
            iArr[0] = iArr[0] + 1;
        } else if (menuItemImpl.isCheckable()) {
            iArr[2] = iArr[2] + 1;
        } else {
            iArr[1] = iArr[1] + 1;
        }
    }

    public static boolean hasAnyVisibleSubMenuItem(List<HyperMenuContract.HyperMenuItem> list) {
        if (list != null && !list.isEmpty()) {
            Iterator<HyperMenuContract.HyperMenuItem> it = list.iterator();
            while (it.hasNext()) {
                miuix.appcompat.internal.view.menu.MenuItemImpl menuItem = it.next().getMenuItem();
                if (menuItem != null && menuItem.isVisible()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean subMenuHasAnyVisibleItem(SubMenu subMenu) {
        if (subMenu != null && subMenu.size() != 0) {
            for (int i = 0; i < subMenu.size(); i++) {
                MenuItem item = subMenu.getItem(i);
                if (item != null && item.isVisible()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void setMenuItemChecked(miuix.appcompat.internal.view.menu.MenuItemImpl menuItemImpl, boolean z) {
        if (menuItemImpl == null || menuItemImpl.isChecked() == z) {
            return;
        }
        menuItemImpl.setChecked(z);
    }

    public static boolean visibleItemListIsChanged(miuix.appcompat.internal.view.menu.MenuBuilder menuBuilder) {
        if (menuBuilder == null || menuBuilder.size() == 0) {
            return false;
        }
        ArrayList<miuix.appcompat.internal.view.menu.MenuItemImpl> visibleItems = menuBuilder.getVisibleItems();
        ArrayList arrayList = new ArrayList();
        getRecentVisibleItems(menuBuilder, arrayList);
        if (visibleItems == null || visibleItems.isEmpty()) {
            return true ^ arrayList.isEmpty();
        }
        if (visibleItems.size() != arrayList.size()) {
            return true;
        }
        return adjustListItemIsDiff(arrayList, visibleItems);
    }

    private static boolean adjustListItemIsDiff(ArrayList<miuix.appcompat.internal.view.menu.MenuItemImpl> arrayList, ArrayList<miuix.appcompat.internal.view.menu.MenuItemImpl> arrayList2) {
        for (int i = 0; i < arrayList.size(); i++) {
            if (!arrayList2.contains(arrayList.get(i))) {
                return true;
            }
        }
        return false;
    }

    private static void getRecentVisibleItems(miuix.appcompat.internal.view.menu.MenuBuilder menuBuilder, ArrayList<miuix.appcompat.internal.view.menu.MenuItemImpl> arrayList) {
        for (int i = 0; i < menuBuilder.size(); i++) {
            MenuItem item = menuBuilder.getItem(i);
            if (item.isVisible() && (item instanceof miuix.appcompat.internal.view.menu.MenuItemImpl)) {
                arrayList.add((miuix.appcompat.internal.view.menu.MenuItemImpl) item);
            }
        }
    }

    public static void checkPrimaryItemVisibility(miuix.appcompat.internal.view.menu.MenuBuilder menuBuilder) {
        boolean zSubMenuHasAnyVisibleItem;
        if (menuBuilder == null || menuBuilder.size() == 0) {
            return;
        }
        for (int i = 0; i < menuBuilder.size(); i++) {
            MenuItem item = menuBuilder.getItem(i);
            boolean zIsVisible = item.isVisible();
            if (item.hasSubMenu() && zIsVisible != (zSubMenuHasAnyVisibleItem = subMenuHasAnyVisibleItem(item.getSubMenu()))) {
                item.setVisible(zSubMenuHasAnyVisibleItem);
            }
        }
        if (visibleItemListIsChanged(menuBuilder)) {
            menuBuilder.updateVisibleItemCountLimit();
        }
    }
}
