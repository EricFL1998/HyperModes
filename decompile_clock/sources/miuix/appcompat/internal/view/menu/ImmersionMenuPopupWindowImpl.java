package miuix.appcompat.internal.view.menu;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import miuix.appcompat.app.ActionBarDelegateImpl;
import miuix.popupwidget.widget.PopupWindow;

/* JADX INFO: loaded from: classes2.dex */
public class ImmersionMenuPopupWindowImpl extends PopupWindow implements ImmersionMenuPopupWindow {
    private ActionBarDelegateImpl mActionBarDelegate;
    private ImmersionMenuAdapter mAdapter;
    private View mAnchor;
    private ViewGroup mLastParent;

    public ImmersionMenuPopupWindowImpl(ActionBarDelegateImpl actionBarDelegateImpl, Menu menu, View view) {
        super(actionBarDelegateImpl.getThemedContext(), view);
        Context themedContext = actionBarDelegateImpl.getThemedContext();
        this.mActionBarDelegate = actionBarDelegateImpl;
        ImmersionMenuAdapter immersionMenuAdapter = new ImmersionMenuAdapter(themedContext, menu);
        this.mAdapter = immersionMenuAdapter;
        setAdapter(immersionMenuAdapter);
        setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: miuix.appcompat.internal.view.menu.ImmersionMenuPopupWindowImpl.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view2, int i, long j) {
                MenuItem item = ImmersionMenuPopupWindowImpl.this.mAdapter.getItem(i);
                if (!item.hasSubMenu()) {
                    ImmersionMenuPopupWindowImpl.this.mActionBarDelegate.onMenuItemSelected(0, item);
                } else {
                    final SubMenu subMenu = item.getSubMenu();
                    ImmersionMenuPopupWindowImpl.this.setOnDismissListener(new android.widget.PopupWindow.OnDismissListener() { // from class: miuix.appcompat.internal.view.menu.ImmersionMenuPopupWindowImpl.1.1
                        @Override // android.widget.PopupWindow.OnDismissListener
                        public void onDismiss() {
                            ImmersionMenuPopupWindowImpl.this.setOnDismissListener(null);
                            ImmersionMenuPopupWindowImpl.this.update(subMenu);
                            ImmersionMenuPopupWindowImpl.this.show(ImmersionMenuPopupWindowImpl.this.mAnchor);
                        }
                    });
                }
                ImmersionMenuPopupWindowImpl.this.dismiss(true);
            }
        });
    }

    @Override // miuix.appcompat.internal.view.menu.ImmersionMenuPopupWindow
    public void update(Menu menu) {
        this.mAdapter.update(menu);
    }

    @Override // miuix.popupwidget.widget.PopupWindow, miuix.appcompat.internal.view.menu.ImmersionMenuPopupWindow
    public void show(View view, ViewGroup viewGroup) {
        this.mAnchor = view;
        super.show(view, viewGroup);
    }

    @Override // miuix.popupwidget.widget.PopupWindow
    public void show(View view) {
        this.mAnchor = view;
        super.show(view);
    }

    public View getLastAnchor() {
        return this.mAnchor;
    }

    public ViewGroup getLastParent() {
        return this.mLastParent;
    }

    @Override // miuix.appcompat.internal.view.menu.ImmersionMenuPopupWindow
    public void dismiss(boolean z) {
        dismiss();
    }
}
