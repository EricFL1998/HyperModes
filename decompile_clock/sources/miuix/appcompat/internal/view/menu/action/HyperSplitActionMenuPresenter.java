package miuix.appcompat.internal.view.menu.action;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import miuix.appcompat.R;
import miuix.appcompat.internal.app.widget.ActionBarOverlayLayout;
import miuix.appcompat.internal.view.menu.HyperPopupHelper;
import miuix.appcompat.internal.view.menu.MenuBuilder;
import miuix.internal.util.AttributeResolver;

/* JADX INFO: loaded from: classes2.dex */
public class HyperSplitActionMenuPresenter extends ActionMenuPresenter {
    private Map<Integer, Boolean> mHyperSplitMenuPrimaryCheckedMap;
    private Map<Integer, Boolean[]> mHyperSplitMenuSecondaryCheckedMap;
    private boolean mSavePrimaryStatusByIdEnabled;

    public HyperSplitActionMenuPresenter(Context context, ActionBarOverlayLayout actionBarOverlayLayout, int i, int i2) {
        super(context, actionBarOverlayLayout, i, i2);
        this.mHyperSplitMenuPrimaryCheckedMap = new HashMap();
        this.mHyperSplitMenuSecondaryCheckedMap = new HashMap();
        this.mSavePrimaryStatusByIdEnabled = false;
    }

    public HyperSplitActionMenuPresenter(Context context, ActionBarOverlayLayout actionBarOverlayLayout, int i, int i2, int i3, int i4) {
        super(context, actionBarOverlayLayout, i, i2, i3, i4);
        this.mHyperSplitMenuPrimaryCheckedMap = new HashMap();
        this.mHyperSplitMenuSecondaryCheckedMap = new HashMap();
        this.mSavePrimaryStatusByIdEnabled = false;
    }

    public void setHyperSplitMenuSaveStatusByIdEnabled(boolean z) {
        this.mSavePrimaryStatusByIdEnabled = z;
    }

    @Override // miuix.appcompat.internal.view.menu.action.ActionMenuPresenter
    protected ActionMenuPresenter.OverflowMenu getOverflowMenu() {
        if (shouldShowPopupOverflow()) {
            HyperSplitPopupOverflowMenu hyperSplitPopupOverflowMenu = new HyperSplitPopupOverflowMenu(this.mContext, this.mMenu, this.mOverflowButton, this.mDecorView, true);
            hyperSplitPopupOverflowMenu.restoreHyperMenuPrimaryCheckedData(this.mHyperSplitMenuPrimaryCheckedMap);
            hyperSplitPopupOverflowMenu.restoreHyperMenuSecondaryCheckedData(this.mHyperSplitMenuSecondaryCheckedMap);
            hyperSplitPopupOverflowMenu.setSaveStatusByIdEnabled(this.mSavePrimaryStatusByIdEnabled);
            return hyperSplitPopupOverflowMenu;
        }
        return super.getOverflowMenu();
    }

    public Map<Integer, Boolean> getHyperSplitMenuPrimaryCheckedMap() {
        return this.mHyperSplitMenuPrimaryCheckedMap;
    }

    public Map<Integer, Boolean[]> getHyperSplitMenuSecondaryCheckedMap() {
        return this.mHyperSplitMenuSecondaryCheckedMap;
    }

    public void restoreHyperSplitPrimaryCheckedData(Map<Integer, Boolean> map) {
        if (map == null) {
            return;
        }
        Iterator<Integer> it = map.keySet().iterator();
        while (it.hasNext()) {
            int iIntValue = it.next().intValue();
            Boolean bool = map.get(Integer.valueOf(iIntValue));
            if (bool != null) {
                this.mHyperSplitMenuPrimaryCheckedMap.put(Integer.valueOf(iIntValue), bool);
            }
        }
    }

    public void restoreHyperSplitSecondaryCheckedData(Map<Integer, Boolean[]> map) {
        if (map == null) {
            return;
        }
        Iterator<Integer> it = map.keySet().iterator();
        while (it.hasNext()) {
            int iIntValue = it.next().intValue();
            Boolean[] boolArr = map.get(Integer.valueOf(iIntValue));
            if (boolArr != null) {
                Boolean[] boolArr2 = new Boolean[boolArr.length];
                System.arraycopy(boolArr, 0, boolArr2, 0, boolArr.length);
                this.mHyperSplitMenuSecondaryCheckedMap.put(Integer.valueOf(iIntValue), boolArr2);
            }
        }
    }

    public class HyperSplitPopupOverflowMenu extends HyperPopupHelper implements ActionMenuPresenter.OverflowMenu {
        @Override // miuix.appcompat.internal.view.menu.action.ActionMenuPresenter.OverflowMenu
        public void update(MenuBuilder menuBuilder) {
        }

        public HyperSplitPopupOverflowMenu(Context context, MenuBuilder menuBuilder, View view, View view2, boolean z) {
            int iComplexToDimensionPixelSize;
            super(context, menuBuilder, view, view2, z);
            TypedValue typedValueResolveTypedValue = AttributeResolver.resolveTypedValue(context, R.attr.overflowMenuMaxHeight);
            if (typedValueResolveTypedValue == null || typedValueResolveTypedValue.type != 5) {
                iComplexToDimensionPixelSize = 0;
            } else if (typedValueResolveTypedValue.resourceId > 0) {
                iComplexToDimensionPixelSize = context.getResources().getDimensionPixelSize(typedValueResolveTypedValue.resourceId);
            } else {
                iComplexToDimensionPixelSize = TypedValue.complexToDimensionPixelSize(typedValueResolveTypedValue.data, context.getResources().getDisplayMetrics());
            }
            if (iComplexToDimensionPixelSize > 0) {
                setPopupMaxHeight(iComplexToDimensionPixelSize);
            }
            setCallback(HyperSplitActionMenuPresenter.this.mPopupPresenterCallback);
            int overflowMenuAnimationGravity = HyperSplitActionMenuPresenter.this.getOverflowMenuAnimationGravity(view);
            if (overflowMenuAnimationGravity != -1) {
                setAnimationGravity(overflowMenuAnimationGravity);
            }
        }

        @Override // miuix.appcompat.internal.view.menu.HyperPopupHelper, android.widget.PopupWindow.OnDismissListener
        public void onDismiss() {
            super.onDismiss();
            HyperSplitActionMenuPresenter.this.mMenu.close();
            HyperSplitActionMenuPresenter.this.mOverflowMenu = null;
        }

        @Override // miuix.appcompat.internal.view.menu.HyperPopupHelper, miuix.appcompat.internal.view.menu.action.ActionMenuPresenter.OverflowMenu
        public void dismiss(boolean z) {
            super.dismiss(z);
            if (HyperSplitActionMenuPresenter.this.mOverflowButton != null) {
                HyperSplitActionMenuPresenter.this.mOverflowButton.setSelected(false);
            }
        }
    }
}
