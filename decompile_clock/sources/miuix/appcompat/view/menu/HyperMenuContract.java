package miuix.appcompat.view.menu;

/* JADX INFO: loaded from: classes2.dex */
public class HyperMenuContract {
    public static final String HYPER_MENU_GROUP_FOREIGN_KEY = "miuix:hyperMenu:groupForeignKey";
    public static final String HYPER_MENU_GROUP_ID = "miuix:hyperMenu:groupId";
    public static final String HYPER_MENU_INTENT_ACTION = "miuix:hyperMenu:intentAction";
    public static final String HYPER_MENU_INTENT_CLASS = "miuix:hyperMenu:intentClass";
    public static final String HYPER_MENU_ITEM_FOREIGN_KEY = "miuix:hyperMenu:itemForeignKey";
    public static final int MENU_ITEM_DIVIDER_ITEM_ID = -100;
    public static final int MENU_ITEM_OTHER_ITEM_ID = -200;

    public enum CheckableType {
        NON_SUPPORT,
        NOT_CHECKED,
        CHECKED
    }

    private HyperMenuContract() {
    }

    public static class HyperMenuItem {
        private final int itemId;
        private final miuix.appcompat.internal.view.menu.MenuItemImpl menuItem;

        public HyperMenuItem(miuix.appcompat.internal.view.menu.MenuItemImpl menuItemImpl, int i) {
            this.menuItem = menuItemImpl;
            this.itemId = i;
        }

        public HyperMenuItem(miuix.appcompat.internal.view.menu.MenuItemImpl menuItemImpl) {
            this.menuItem = menuItemImpl;
            if (menuItemImpl != null) {
                this.itemId = menuItemImpl.getItemId();
            } else {
                this.itemId = HyperMenuContract.MENU_ITEM_OTHER_ITEM_ID;
            }
        }

        public miuix.appcompat.internal.view.menu.MenuItemImpl getMenuItem() {
            return this.menuItem;
        }

        public int getItemId() {
            return this.itemId;
        }
    }

    public static class HyperMenuDivider extends HyperMenuItem {
        public HyperMenuDivider() {
            this(null, -100);
        }

        public HyperMenuDivider(miuix.appcompat.internal.view.menu.MenuItemImpl menuItemImpl, int i) {
            super(menuItemImpl, i);
        }

        public HyperMenuDivider(miuix.appcompat.internal.view.menu.MenuItemImpl menuItemImpl) {
            super(menuItemImpl);
        }
    }

    public static class HyperMenuTextItem extends HyperMenuItem {
        public CheckableType checkStatus;
        public boolean isExpandable;
        public boolean isHeaderItem;
        public int itemForeignKey;

        public HyperMenuTextItem(miuix.appcompat.internal.view.menu.MenuItemImpl menuItemImpl) {
            super(menuItemImpl);
            this.checkStatus = CheckableType.NON_SUPPORT;
            this.itemForeignKey = -1;
        }

        public boolean isChecked() {
            return this.checkStatus == CheckableType.CHECKED;
        }
    }
}
