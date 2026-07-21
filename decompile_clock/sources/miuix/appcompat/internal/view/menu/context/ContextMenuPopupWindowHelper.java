package miuix.appcompat.internal.view.menu.context;

import android.content.Context;
import android.os.IBinder;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupWindow;
import miuix.appcompat.internal.view.menu.MenuBuilder;
import miuix.appcompat.internal.view.menu.MenuPresenter;
import miuix.appcompat.widget.HyperPopupWindow;

/* JADX INFO: loaded from: classes2.dex */
public class ContextMenuPopupWindowHelper implements PopupWindow.OnDismissListener {
    private View mAnchor;
    private Context mContext;
    private HyperContextMenuImpl mContextMenuPopupWindow;
    private MenuBuilder mMenu;
    private float[] mPoint = new float[2];
    private MenuPresenter.Callback mPresenterCallback;

    public ContextMenuPopupWindowHelper(MenuBuilder menuBuilder) {
        this.mMenu = menuBuilder;
    }

    public void show(IBinder iBinder, View view, float f, float f2) {
        show(iBinder, view, f, f2, view.getRootView());
    }

    public void show(IBinder iBinder, View view, float f, float f2, View view2) {
        this.mContext = this.mMenu.getContext();
        this.mAnchor = view;
        float[] fArr = this.mPoint;
        fArr[0] = f;
        fArr[1] = f2;
        HyperContextMenuImpl hyperContextMenuImpl = new HyperContextMenuImpl(this.mContext, view);
        this.mContextMenuPopupWindow = hyperContextMenuImpl;
        hyperContextMenuImpl.setPopupWindowStrategy(new ContextMenuStrategy(this.mContext, f, f2));
        this.mContextMenuPopupWindow.setOnMenuItemClickListener(new HyperPopupWindow.OnMenuItemClickListener() { // from class: miuix.appcompat.internal.view.menu.context.ContextMenuPopupWindowHelper.1
            @Override // miuix.appcompat.widget.HyperPopupWindow.OnMenuItemClickListener
            public void onMenuItemClick(MenuItem menuItem) {
                ContextMenuPopupWindowHelper.this.mMenu.performItemAction(menuItem, 0);
            }
        });
        this.mContextMenuPopupWindow.setOnDismissListener(this);
        this.mContextMenuPopupWindow.inflate(this.mMenu);
        this.mContextMenuPopupWindow.show();
    }

    public void refreshContextMenuPopupWindow() {
        HyperContextMenuImpl hyperContextMenuImpl = this.mContextMenuPopupWindow;
        if (hyperContextMenuImpl != null) {
            hyperContextMenuImpl.update();
        }
    }

    public void setPresenterCallback(MenuPresenter.Callback callback) {
        this.mPresenterCallback = callback;
    }

    public HyperContextMenuImpl getContextMenuPopupWindow() {
        return this.mContextMenuPopupWindow;
    }

    @Override // android.widget.PopupWindow.OnDismissListener
    public void onDismiss() {
        MenuPresenter.Callback callback = this.mPresenterCallback;
        if (callback != null) {
            callback.onCloseMenu(this.mMenu, true);
        }
        this.mMenu.clearAll();
    }

    public void dismiss() {
        HyperContextMenuImpl hyperContextMenuImpl = this.mContextMenuPopupWindow;
        if (hyperContextMenuImpl != null) {
            hyperContextMenuImpl.dismiss();
            this.mContextMenuPopupWindow = null;
        }
    }
}
