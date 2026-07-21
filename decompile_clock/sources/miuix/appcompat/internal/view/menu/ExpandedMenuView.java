package miuix.appcompat.internal.view.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/* JADX INFO: loaded from: classes2.dex */
public final class ExpandedMenuView extends ListView implements MenuBuilder.ItemInvoker, MenuView, AdapterView.OnItemClickListener {
    private int mAnimations;
    private MenuBuilder mMenu;

    @Override // miuix.appcompat.internal.view.menu.MenuView
    public boolean filterLeftoverView(int i) {
        return false;
    }

    @Override // miuix.appcompat.internal.view.menu.MenuView
    public boolean hasBackgroundView() {
        return false;
    }

    public ExpandedMenuView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setOnItemClickListener(this);
    }

    @Override // miuix.appcompat.internal.view.menu.MenuView
    public void initialize(MenuBuilder menuBuilder) {
        this.mMenu = menuBuilder;
    }

    @Override // android.widget.ListView, android.widget.AbsListView, android.widget.AdapterView, android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setChildrenDrawingCacheEnabled(false);
    }

    @Override // miuix.appcompat.internal.view.menu.MenuBuilder.ItemInvoker
    public boolean invokeItem(MenuItemImpl menuItemImpl, int i) {
        return this.mMenu.performItemAction(menuItemImpl, i);
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView adapterView, View view, int i, long j) {
        invokeItem((MenuItemImpl) getAdapter().getItem(i), 0);
    }

    @Override // miuix.appcompat.internal.view.menu.MenuView
    public int getWindowAnimations() {
        return this.mAnimations;
    }
}
