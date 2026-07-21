package miuix.internal.widget;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import miuix.popupwidget.widget.PopupWindow;

/* JADX INFO: loaded from: classes2.dex */
public class PopupMenuWindow extends PopupWindow implements IPopupMenuWidget {
    private PopupMenuAdapter mAdapter;
    private View mLastAnchor;

    protected void onDismiss() {
    }

    protected void onMenuItemClick(MenuItem menuItem) {
    }

    @Override // miuix.internal.widget.IPopupMenuWidget
    public void setBackgroundBlurEnabled(boolean z) {
    }

    @Override // miuix.internal.widget.IPopupMenuWidget
    public void setDimEnabled(boolean z) {
    }

    @Override // miuix.internal.widget.IPopupMenuWidget
    public void setSelfBlurEnabled(boolean z) {
    }

    public PopupMenuWindow(Context context) {
        super(context);
        PopupMenuAdapter popupMenuAdapter = new PopupMenuAdapter(context);
        this.mAdapter = popupMenuAdapter;
        setAdapter(popupMenuAdapter);
        setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: miuix.internal.widget.PopupMenuWindow$$ExternalSyntheticLambda1
            @Override // android.widget.AdapterView.OnItemClickListener
            public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
                this.f$0.m1865lambda$new$1$miuixinternalwidgetPopupMenuWindow(adapterView, view, i, j);
            }
        });
        setOnDismissListener(new android.widget.PopupWindow.OnDismissListener() { // from class: miuix.internal.widget.PopupMenuWindow$$ExternalSyntheticLambda2
            @Override // android.widget.PopupWindow.OnDismissListener
            public final void onDismiss() {
                this.f$0.onDismiss();
            }
        });
    }

    /* JADX INFO: renamed from: lambda$new$1$miuix-internal-widget-PopupMenuWindow, reason: not valid java name */
    /* synthetic */ void m1865lambda$new$1$miuixinternalwidgetPopupMenuWindow(AdapterView adapterView, View view, int i, long j) {
        MenuItem item = this.mAdapter.getItem(i);
        if (item.hasSubMenu()) {
            final SubMenu subMenu = item.getSubMenu();
            setOnDismissListener(new android.widget.PopupWindow.OnDismissListener() { // from class: miuix.internal.widget.PopupMenuWindow$$ExternalSyntheticLambda0
                @Override // android.widget.PopupWindow.OnDismissListener
                public final void onDismiss() {
                    this.f$0.m1864lambda$new$0$miuixinternalwidgetPopupMenuWindow(subMenu);
                }
            });
        } else {
            onMenuItemClick(item);
        }
        dismiss();
    }

    /* JADX INFO: renamed from: lambda$new$0$miuix-internal-widget-PopupMenuWindow, reason: not valid java name */
    /* synthetic */ void m1864lambda$new$0$miuixinternalwidgetPopupMenuWindow(SubMenu subMenu) {
        setOnDismissListener(null);
        update(subMenu);
        showAsDropDown(this.mLastAnchor);
    }

    @Override // miuix.internal.widget.IPopupMenuWidget
    public void update(Menu menu) {
        this.mAdapter.update(menu);
    }

    @Override // miuix.popupwidget.widget.PopupWindow, miuix.appcompat.internal.view.menu.ImmersionMenuPopupWindow
    @Deprecated
    public void show(View view, ViewGroup viewGroup) {
        showAsDropDown(view);
    }

    @Override // miuix.popupwidget.widget.PopupWindow, android.widget.PopupWindow, miuix.internal.widget.IPopupMenuWidget
    public void showAsDropDown(View view) {
        this.mLastAnchor = view;
        if (prepareShow(view)) {
            super.showAsDropDown(view);
        }
    }

    @Override // miuix.popupwidget.widget.PopupWindow, android.widget.PopupWindow, miuix.internal.widget.IPopupMenuWidget
    public void showAtLocation(View view, int i, int i2, int i3) {
        if (prepareShow(view)) {
            super.showAtLocation(view.getRootView(), i, i2, i3);
        }
    }
}
