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
public class HyperActionMenuPresenter extends EndActionMenuPresenter {
    private Map<Integer, Boolean> mHyperMenuPrimaryCheckedMap;
    private Map<Integer, Boolean[]> mHyperMenuSecondaryCheckedMap;
    private boolean mSavePrimaryStatusByIdEnabled;

    public HyperActionMenuPresenter(Context context, ActionBarOverlayLayout actionBarOverlayLayout, int i, int i2) {
        this(context, actionBarOverlayLayout, i, i2, 0, 0);
    }

    public HyperActionMenuPresenter(Context context, ActionBarOverlayLayout actionBarOverlayLayout, int i, int i2, int i3, int i4) {
        super(context, actionBarOverlayLayout, i, i2, i3, i4);
        this.mHyperMenuPrimaryCheckedMap = new HashMap();
        this.mHyperMenuSecondaryCheckedMap = new HashMap();
        this.mSavePrimaryStatusByIdEnabled = false;
    }

    public void setHyperMenuSaveStatusByIdEnabled(boolean z) {
        this.mSavePrimaryStatusByIdEnabled = z;
    }

    @Override // miuix.appcompat.internal.view.menu.action.ActionMenuPresenter
    protected ActionMenuPresenter.OverflowMenu getOverflowMenu() {
        if (shouldShowPopupOverflow()) {
            HyperPopupOverflowMenu hyperPopupOverflowMenu = new HyperPopupOverflowMenu(this.mContext, this.mMenu, this.mOverflowButton, this.mDecorView, true);
            hyperPopupOverflowMenu.restoreHyperMenuPrimaryCheckedData(this.mHyperMenuPrimaryCheckedMap);
            hyperPopupOverflowMenu.restoreHyperMenuSecondaryCheckedData(this.mHyperMenuSecondaryCheckedMap);
            hyperPopupOverflowMenu.setSaveStatusByIdEnabled(this.mSavePrimaryStatusByIdEnabled);
            return hyperPopupOverflowMenu;
        }
        return super.getOverflowMenu();
    }

    public Map<Integer, Boolean> getHyperPrimaryCheckedData() {
        return this.mHyperMenuPrimaryCheckedMap;
    }

    public Map<Integer, Boolean[]> getHyperSecondaryCheckedData() {
        return this.mHyperMenuSecondaryCheckedMap;
    }

    public void restorePrimaryMenuCheckedData(Map<Integer, Boolean> map) {
        if (map == null) {
            return;
        }
        Iterator<Integer> it = map.keySet().iterator();
        while (it.hasNext()) {
            int iIntValue = it.next().intValue();
            Boolean bool = map.get(Integer.valueOf(iIntValue));
            if (bool != null) {
                this.mHyperMenuPrimaryCheckedMap.put(Integer.valueOf(iIntValue), bool);
            }
        }
    }

    public void restoreSecondaryMenuCheckedData(Map<Integer, Boolean[]> map) {
        if (map == null) {
            return;
        }
        Iterator<Integer> it = map.keySet().iterator();
        while (it.hasNext()) {
            int iIntValue = it.next().intValue();
            Boolean[] boolArr = map.get(Integer.valueOf(iIntValue));
            Boolean[] boolArr2 = new Boolean[boolArr.length];
            System.arraycopy(boolArr, 0, boolArr2, 0, boolArr.length);
            this.mHyperMenuSecondaryCheckedMap.put(Integer.valueOf(iIntValue), boolArr2);
        }
    }

    public class HyperPopupOverflowMenu extends HyperPopupHelper implements ActionMenuPresenter.OverflowMenu {
        @Override // miuix.appcompat.internal.view.menu.action.ActionMenuPresenter.OverflowMenu
        public void update(MenuBuilder menuBuilder) {
        }

        public HyperPopupOverflowMenu(Context context, MenuBuilder menuBuilder, View view, View view2, boolean z) {
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
            setCallback(HyperActionMenuPresenter.this.mPopupPresenterCallback);
            int overflowMenuAnimationGravity = HyperActionMenuPresenter.this.getOverflowMenuAnimationGravity(view);
            if (overflowMenuAnimationGravity != -1) {
                setAnimationGravity(overflowMenuAnimationGravity);
            }
        }

        @Override // miuix.appcompat.internal.view.menu.HyperPopupHelper, android.widget.PopupWindow.OnDismissListener
        public void onDismiss() {
            super.onDismiss();
            HyperActionMenuPresenter.this.mMenu.close();
            HyperActionMenuPresenter.this.mOverflowMenu = null;
        }

        @Override // miuix.appcompat.internal.view.menu.HyperPopupHelper, miuix.appcompat.internal.view.menu.action.ActionMenuPresenter.OverflowMenu
        public void dismiss(boolean z) {
            super.dismiss(z);
            if (HyperActionMenuPresenter.this.mOverflowButton != null) {
                HyperActionMenuPresenter.this.mOverflowButton.setSelected(false);
            }
        }
    }
}
