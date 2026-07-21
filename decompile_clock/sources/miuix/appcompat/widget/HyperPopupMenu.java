package miuix.appcompat.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.PopupWindow;
import androidx.appcompat.view.SupportMenuInflater;
import java.util.Map;
import miuix.appcompat.R;
import miuix.appcompat.internal.view.menu.MenuBuilder;
import miuix.appcompat.view.menu.HyperMenuAdapter;
import miuix.appcompat.view.menu.HyperMenuInflater;
import miuix.popupwidget.internal.strategy.IPopupWindowStrategy;

/* JADX INFO: loaded from: classes2.dex */
public class HyperPopupMenu {
    private HyperMenuAdapter mAdapter;
    private final View mAnchor;
    private final Context mContext;
    private HyperPopupWindow mHyperPopupWindow;
    private final MenuBuilder mMenu;
    private boolean mPrimaryCheckedMapUseIdAsKeyEnabled;
    private Map<Integer, Boolean> mPrimaryPreCheckedMap;
    private Map<Integer, Boolean[]> mSecondaryPreCheckedMap;

    public HyperPopupMenu(Context context, View view) {
        this(context, view, 0);
    }

    public HyperPopupMenu(Context context, View view, int i) {
        this.mPrimaryCheckedMapUseIdAsKeyEnabled = false;
        if (i == 0) {
            TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(null, R.styleable.miuiPopupMenu, R.attr.miuiPopupMenuStyle, 0);
            try {
                int resourceId = typedArrayObtainStyledAttributes.getResourceId(R.styleable.miuiPopupMenu_miuiPopupTheme, 0);
                typedArrayObtainStyledAttributes.recycle();
                i = resourceId;
            } catch (Throwable th) {
                typedArrayObtainStyledAttributes.recycle();
                throw th;
            }
        }
        if (i != 0) {
            this.mContext = new ContextThemeWrapper(context, i);
        } else {
            this.mContext = context;
        }
        this.mAnchor = view;
        this.mMenu = new MenuBuilder(this.mContext);
        HyperPopupWindow hyperPopupWindow = new HyperPopupWindow(context);
        this.mHyperPopupWindow = hyperPopupWindow;
        hyperPopupWindow.setWindowAnimationEnabled(false);
    }

    private MenuInflater getHyperMenuInflater() {
        return new HyperMenuInflater(this.mContext);
    }

    private MenuInflater getDefaultMenuInflater() {
        return new SupportMenuInflater(this.mContext);
    }

    public void preCheckPrimaryItem(Map<Integer, Boolean> map) {
        this.mPrimaryPreCheckedMap = map;
    }

    public void preCheckSecondaryItem(Map<Integer, Boolean[]> map) {
        this.mSecondaryPreCheckedMap = map;
    }

    public void setPrimaryCheckedMapUseIdAsKeyEnabled(boolean z) {
        this.mPrimaryCheckedMapUseIdAsKeyEnabled = z;
    }

    public void savePrimaryCheckedMap(Map<Integer, Boolean> map) {
        HyperMenuAdapter hyperMenuAdapter = this.mAdapter;
        if (hyperMenuAdapter == null) {
            return;
        }
        hyperMenuAdapter.copyPrimaryCheckedData(map);
    }

    public void saveSecondaryCheckedMap(Map<Integer, Boolean[]> map) {
        HyperMenuAdapter hyperMenuAdapter = this.mAdapter;
        if (hyperMenuAdapter == null) {
            return;
        }
        hyperMenuAdapter.copySecondaryCheckedData(map);
    }

    public Menu getMenu() {
        return this.mMenu;
    }

    public void inflate(int i, boolean z) {
        getHyperMenuInflater().inflate(i, this.mMenu);
        HyperMenuAdapter hyperMenuAdapter = new HyperMenuAdapter(this.mContext);
        this.mAdapter = hyperMenuAdapter;
        hyperMenuAdapter.preCheckPrimaryItem(this.mPrimaryPreCheckedMap);
        this.mAdapter.preCheckSecondaryItem(this.mSecondaryPreCheckedMap);
        this.mAdapter.setSavePrimaryStatusByIdEnabled(this.mPrimaryCheckedMapUseIdAsKeyEnabled);
        this.mAdapter.update(this.mMenu, z);
    }

    public void inflate(int i) {
        getDefaultMenuInflater().inflate(i, this.mMenu);
        HyperMenuAdapter hyperMenuAdapter = new HyperMenuAdapter(this.mContext);
        this.mAdapter = hyperMenuAdapter;
        hyperMenuAdapter.preCheckPrimaryItem(this.mPrimaryPreCheckedMap);
        this.mAdapter.preCheckSecondaryItem(this.mSecondaryPreCheckedMap);
        this.mAdapter.setSavePrimaryStatusByIdEnabled(this.mPrimaryCheckedMapUseIdAsKeyEnabled);
        this.mAdapter.update(this.mMenu);
    }

    public void inflate(MenuBuilder menuBuilder) {
        HyperMenuAdapter hyperMenuAdapter = new HyperMenuAdapter(this.mContext);
        this.mAdapter = hyperMenuAdapter;
        hyperMenuAdapter.preCheckPrimaryItem(this.mPrimaryPreCheckedMap);
        this.mAdapter.preCheckSecondaryItem(this.mSecondaryPreCheckedMap);
        this.mAdapter.setSavePrimaryStatusByIdEnabled(this.mPrimaryCheckedMapUseIdAsKeyEnabled);
        this.mAdapter.update(menuBuilder);
    }

    public void setPopupWindowStrategy(IPopupWindowStrategy iPopupWindowStrategy) {
        this.mHyperPopupWindow.setPopupWindowStrategy(iPopupWindowStrategy);
    }

    public boolean isShowing() {
        return this.mHyperPopupWindow.isShowing();
    }

    public void dismiss() {
        this.mHyperPopupWindow.dismiss();
    }

    public void setAutoDismiss(boolean z) {
        this.mHyperPopupWindow.setAutoDismiss(z);
    }

    public void setWindowAnimationEnabled(boolean z) {
        this.mHyperPopupWindow.setWindowAnimationEnabled(z);
    }

    public boolean notifyDataChanged() {
        return notifyDataChanged(false);
    }

    public boolean notifyDataChanged(boolean z) {
        MenuBuilder menuBuilder;
        HyperMenuAdapter hyperMenuAdapter = this.mAdapter;
        if (hyperMenuAdapter == null || (menuBuilder = this.mMenu) == null) {
            return false;
        }
        hyperMenuAdapter.update(menuBuilder, z);
        return true;
    }

    public void show() {
        this.mHyperPopupWindow.setSecondaryMenuEnabled(this.mAdapter.hasSubMenu());
        this.mHyperPopupWindow.setAdapter(this.mAdapter);
        this.mHyperPopupWindow.show(this.mAnchor);
    }

    public void update() {
        this.mHyperPopupWindow.update();
    }

    public void setOnMenuItemClickListener(HyperPopupWindow.OnMenuItemClickListener onMenuItemClickListener) {
        this.mHyperPopupWindow.setOnMenuItemClickListener(onMenuItemClickListener);
    }

    public void setOnDismissListener(PopupWindow.OnDismissListener onDismissListener) {
        this.mHyperPopupWindow.setOnDismissListener(onDismissListener);
    }

    public void setClippingEnabled(boolean z) {
        this.mHyperPopupWindow.setClippingEnabled(z);
    }
}
