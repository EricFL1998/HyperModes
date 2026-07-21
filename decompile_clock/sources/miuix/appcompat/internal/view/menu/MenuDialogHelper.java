package miuix.appcompat.internal.view.menu;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import miuix.appcompat.R;
import miuix.appcompat.app.AlertDialog;

/* JADX INFO: loaded from: classes2.dex */
public class MenuDialogHelper implements DialogInterface.OnKeyListener, DialogInterface.OnClickListener, DialogInterface.OnDismissListener, MenuPresenter.Callback {
    private AlertDialog mDialog;
    private MenuBuilder mMenu;
    ListMenuPresenter mPresenter;
    private MenuPresenter.Callback mPresenterCallback;

    public MenuDialogHelper(MenuBuilder menuBuilder) {
        this.mMenu = menuBuilder;
    }

    public void show(IBinder iBinder) {
        MenuBuilder menuBuilder = this.mMenu;
        AlertDialog.Builder builder = new AlertDialog.Builder(menuBuilder.getContext());
        ListMenuPresenter listMenuPresenter = new ListMenuPresenter(menuBuilder.getContext(), R.layout.miuix_appcompat_dialog_list_menu_item_layout);
        this.mPresenter = listMenuPresenter;
        listMenuPresenter.setCallback(this);
        this.mMenu.addMenuPresenter(this.mPresenter);
        builder.setAdapter(this.mPresenter.getAdapter(), this);
        View headerView = menuBuilder.getHeaderView();
        if (headerView != null) {
            builder.setCustomTitle(headerView);
        } else {
            builder.setIcon(menuBuilder.getHeaderIcon()).setTitle(menuBuilder.getHeaderTitle());
        }
        builder.setNegativeButton(android.R.string.cancel, (DialogInterface.OnClickListener) null);
        builder.setOnKeyListener(this);
        AlertDialog alertDialogCreate = builder.create();
        this.mDialog = alertDialogCreate;
        alertDialogCreate.setOnDismissListener(this);
        WindowManager.LayoutParams attributes = this.mDialog.getWindow().getAttributes();
        attributes.type = 1003;
        if (iBinder != null) {
            attributes.token = iBinder;
        }
        attributes.flags |= 131072;
        if (menuBuilder.getContext() != null) {
            Resources resources = menuBuilder.getContext().getResources();
            attributes.setTitle(resources != null ? resources.getString(R.string.miuix_appcompat_show_dialog_description) : null);
        }
        this.mDialog.show();
        attributes.setTitle("");
    }

    @Override // android.content.DialogInterface.OnKeyListener
    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
        View decorView;
        KeyEvent.DispatcherState keyDispatcherState;
        View decorView2;
        KeyEvent.DispatcherState keyDispatcherState2;
        if (i == 82 || i == 4) {
            if (keyEvent.getAction() == 0 && keyEvent.getRepeatCount() == 0) {
                AlertDialog alertDialog = this.mDialog;
                if (alertDialog == null) {
                    return false;
                }
                Window window = alertDialog.getWindow();
                if (window != null && (decorView2 = window.getDecorView()) != null && (keyDispatcherState2 = decorView2.getKeyDispatcherState()) != null) {
                    keyDispatcherState2.startTracking(keyEvent, this);
                    return true;
                }
            } else if (keyEvent.getAction() == 1 && !keyEvent.isCanceled()) {
                AlertDialog alertDialog2 = this.mDialog;
                if (alertDialog2 == null) {
                    return false;
                }
                Window window2 = alertDialog2.getWindow();
                if (window2 != null && (decorView = window2.getDecorView()) != null && (keyDispatcherState = decorView.getKeyDispatcherState()) != null && keyDispatcherState.isTracking(keyEvent)) {
                    this.mMenu.closeInternal(true);
                    dialogInterface.dismiss();
                    return true;
                }
            }
        }
        return this.mMenu.performShortcut(i, keyEvent, 0);
    }

    public void setPresenterCallback(MenuPresenter.Callback callback) {
        this.mPresenterCallback = callback;
    }

    public void dismiss() {
        AlertDialog alertDialog = this.mDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.mDialog = null;
        }
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        this.mPresenter.onCloseMenu(this.mMenu, true);
    }

    @Override // miuix.appcompat.internal.view.menu.MenuPresenter.Callback
    public void onCloseMenu(MenuBuilder menuBuilder, boolean z) {
        if (z || menuBuilder == this.mMenu) {
            dismiss();
        }
        MenuPresenter.Callback callback = this.mPresenterCallback;
        if (callback != null) {
            callback.onCloseMenu(menuBuilder, z);
        }
    }

    @Override // miuix.appcompat.internal.view.menu.MenuPresenter.Callback
    public boolean onOpenSubMenu(MenuBuilder menuBuilder) {
        MenuPresenter.Callback callback = this.mPresenterCallback;
        return callback != null && callback.onOpenSubMenu(menuBuilder);
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        this.mMenu.performItemAction((MenuItemImpl) this.mPresenter.getAdapter().getItem(i), 0);
    }
}
