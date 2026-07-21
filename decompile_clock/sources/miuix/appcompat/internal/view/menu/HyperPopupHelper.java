package miuix.appcompat.internal.view.menu;

import android.content.Context;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import java.util.Map;
import miuix.appcompat.view.menu.HyperMenuAdapter;
import miuix.appcompat.widget.HyperPopupWindow;

/* JADX INFO: loaded from: classes2.dex */
public class HyperPopupHelper implements HyperPopupWindow.OnMenuItemClickListener, View.OnKeyListener, PopupWindow.OnDismissListener, MenuPresenter {
    private View mAnchorView;
    private Context mContext;
    private View mFenceDecor;
    boolean mForceShowIcon;
    private HyperMenuAdapter mHyperMenuAdapter;
    private HyperPopupWindow mHyperPopup;
    private MenuBuilder mMenu;
    private boolean mOverflowOnly;
    private int mPopupAnimationGravity;
    private int mPopupHorizontalOffset;
    private int mPopupMaxHeight;
    private int mPopupVerticalOffset;
    private MenuPresenter.Callback mPresenterCallback;
    private Map<Integer, Boolean> mPrimaryCheckedMap;
    private boolean mSavePrimaryStatusByIdEnabled;
    private Map<Integer, Boolean[]> mSecondaryCheckedMap;

    @Override // miuix.appcompat.internal.view.menu.MenuPresenter
    public boolean collapseItemActionView(MenuBuilder menuBuilder, MenuItemImpl menuItemImpl) {
        return false;
    }

    @Override // miuix.appcompat.internal.view.menu.MenuPresenter
    public boolean expandItemActionView(MenuBuilder menuBuilder, MenuItemImpl menuItemImpl) {
        return false;
    }

    @Override // miuix.appcompat.internal.view.menu.MenuPresenter
    public boolean flagActionItems() {
        return false;
    }

    @Override // miuix.appcompat.internal.view.menu.MenuPresenter
    public int getId() {
        return 0;
    }

    @Override // miuix.appcompat.internal.view.menu.MenuPresenter
    public void initForMenu(Context context, MenuBuilder menuBuilder) {
    }

    @Override // miuix.appcompat.internal.view.menu.MenuPresenter
    public void onRestoreInstanceState(Parcelable parcelable) {
    }

    @Override // miuix.appcompat.internal.view.menu.MenuPresenter
    public Parcelable onSaveInstanceState() {
        return null;
    }

    @Override // miuix.appcompat.internal.view.menu.MenuPresenter
    public boolean onSubMenuSelected(SubMenuBuilder subMenuBuilder) {
        return true;
    }

    public HyperPopupHelper(Context context, MenuBuilder menuBuilder) {
        this(context, menuBuilder, null, false);
    }

    public HyperPopupHelper(Context context, MenuBuilder menuBuilder, View view) {
        this(context, menuBuilder, view, false);
    }

    public HyperPopupHelper(Context context, MenuBuilder menuBuilder, View view, boolean z) {
        this(context, menuBuilder, view, null, z);
    }

    public HyperPopupHelper(Context context, MenuBuilder menuBuilder, View view, View view2, boolean z) {
        this.mPopupHorizontalOffset = 0;
        this.mPopupAnimationGravity = -1;
        this.mPopupMaxHeight = 0;
        this.mSavePrimaryStatusByIdEnabled = false;
        this.mContext = context;
        this.mMenu = menuBuilder;
        this.mOverflowOnly = z;
        this.mAnchorView = view;
        this.mFenceDecor = view2;
        menuBuilder.addMenuPresenter(this);
    }

    public void setSaveStatusByIdEnabled(boolean z) {
        this.mSavePrimaryStatusByIdEnabled = z;
    }

    public void setAnimationGravity(int i) {
        this.mPopupAnimationGravity = i;
    }

    public void setAnchorView(View view) {
        this.mAnchorView = view;
    }

    public void setFenceDecor(View view) {
        this.mFenceDecor = view;
    }

    public void setForceShowIcon(boolean z) {
        this.mForceShowIcon = z;
    }

    public void setVerticalOffset(int i) {
        this.mPopupVerticalOffset = i;
    }

    public void setPopupMaxHeight(int i) {
        this.mPopupMaxHeight = i;
    }

    public void show() {
        if (!tryShow()) {
            throw new IllegalStateException("MenuPopupHelper cannot be used without an anchor");
        }
    }

    public void restoreHyperMenuPrimaryCheckedData(Map<Integer, Boolean> map) {
        this.mPrimaryCheckedMap = map;
    }

    public void restoreHyperMenuSecondaryCheckedData(Map<Integer, Boolean[]> map) {
        this.mSecondaryCheckedMap = map;
    }

    public boolean tryShow() {
        HyperPopupWindow hyperPopupWindow = new HyperPopupWindow(this.mContext, this.mFenceDecor);
        this.mHyperPopup = hyperPopupWindow;
        hyperPopupWindow.setDropDownGravity(8388693);
        this.mHyperPopup.setOnDismissListener(this);
        this.mHyperPopup.setOnMenuItemClickListener(this);
        this.mHyperPopup.setWindowAnimationEnabled(false);
        HyperMenuAdapter hyperMenuAdapter = new HyperMenuAdapter(this.mContext, null, this.mOverflowOnly);
        this.mHyperMenuAdapter = hyperMenuAdapter;
        hyperMenuAdapter.setOptionalIconsVisible(this.mMenu.getOptionalIconsVisible());
        this.mHyperMenuAdapter.setSavePrimaryStatusByIdEnabled(this.mSavePrimaryStatusByIdEnabled);
        Map<Integer, Boolean> map = this.mPrimaryCheckedMap;
        if (map != null) {
            this.mHyperMenuAdapter.preCheckPrimaryItem(map);
        }
        Map<Integer, Boolean[]> map2 = this.mSecondaryCheckedMap;
        if (map2 != null) {
            this.mHyperMenuAdapter.preCheckSecondaryItem(map2);
        }
        this.mHyperMenuAdapter.update(this.mMenu);
        this.mHyperPopup.setAdapter(this.mHyperMenuAdapter);
        this.mHyperPopup.setSecondaryMenuEnabled(this.mHyperMenuAdapter.hasSubMenu());
        this.mHyperPopup.setHorizontalOffset(this.mPopupHorizontalOffset);
        this.mHyperPopup.setVerticalOffset(this.mPopupVerticalOffset);
        int i = this.mPopupMaxHeight;
        if (i > 0) {
            this.mHyperPopup.setMaxAllowedHeight(i);
        }
        this.mHyperPopup.show(this.mAnchorView);
        this.mHyperPopup.getContentView().setOnKeyListener(this);
        return true;
    }

    public void dismiss(boolean z) {
        if (isShowing()) {
            this.mHyperPopup.setOnDismissListener(new PopupWindow.OnDismissListener() { // from class: miuix.appcompat.internal.view.menu.HyperPopupHelper$$ExternalSyntheticLambda0
                @Override // android.widget.PopupWindow.OnDismissListener
                public final void onDismiss() {
                    this.f$0.m1837x8ce5c74a();
                }
            });
            this.mHyperPopup.dismiss();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: saveData, reason: merged with bridge method [inline-methods] */
    public void m1837x8ce5c74a() {
        HyperMenuAdapter hyperMenuAdapter = this.mHyperMenuAdapter;
        if (hyperMenuAdapter != null) {
            hyperMenuAdapter.copyPrimaryCheckedData(this.mPrimaryCheckedMap);
            this.mHyperMenuAdapter.copySecondaryCheckedData(this.mSecondaryCheckedMap);
        }
    }

    @Override // android.widget.PopupWindow.OnDismissListener
    public void onDismiss() {
        m1837x8ce5c74a();
        this.mHyperPopup = null;
        this.mMenu.close();
    }

    public boolean isShowing() {
        HyperPopupWindow hyperPopupWindow = this.mHyperPopup;
        return (hyperPopupWindow == null || !hyperPopupWindow.isShowing() || this.mHyperPopup.isInDismissAnimation()) ? false : true;
    }

    @Override // android.view.View.OnKeyListener
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (keyEvent.getAction() != 1 || i != 82) {
            return false;
        }
        dismiss(false);
        return true;
    }

    @Override // miuix.appcompat.internal.view.menu.MenuPresenter
    public MenuView getMenuView(ViewGroup viewGroup) {
        throw new UnsupportedOperationException("MenuPopupHelpers manage their own views");
    }

    @Override // miuix.appcompat.internal.view.menu.MenuPresenter
    public void updateMenuView(boolean z) {
        HyperMenuAdapter hyperMenuAdapter = this.mHyperMenuAdapter;
        if (hyperMenuAdapter != null) {
            hyperMenuAdapter.notifyDataSetChanged();
        }
    }

    @Override // miuix.appcompat.internal.view.menu.MenuPresenter
    public void setCallback(MenuPresenter.Callback callback) {
        this.mPresenterCallback = callback;
    }

    @Override // miuix.appcompat.internal.view.menu.MenuPresenter
    public void onCloseMenu(MenuBuilder menuBuilder, boolean z) {
        if (menuBuilder != this.mMenu) {
            return;
        }
        dismiss(true);
        MenuPresenter.Callback callback = this.mPresenterCallback;
        if (callback != null) {
            callback.onCloseMenu(menuBuilder, z);
        }
    }

    @Override // miuix.appcompat.widget.HyperPopupWindow.OnMenuItemClickListener
    public void onMenuItemClick(MenuItem menuItem) {
        this.mMenu.performItemAction(menuItem, 0);
    }
}
