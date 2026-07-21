package miuix.appcompat.internal.view.menu.action;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import miuix.appcompat.R;
import miuix.appcompat.internal.view.menu.MenuBuilder;
import miuix.appcompat.internal.view.menu.MenuItemImpl;
import miuix.appcompat.internal.view.menu.MenuView;
import miuix.core.util.MiuixUIUtils;

/* JADX INFO: loaded from: classes2.dex */
public class ActionMenuItemView extends LinearLayout implements MenuView.ItemView {
    private final ActionMenuItemViewChildren mChildren;
    private boolean mIsCheckable;
    private MenuItemImpl mItemData;
    private MenuBuilder.ItemInvoker mItemInvoker;

    @Override // miuix.appcompat.internal.view.menu.MenuView.ItemView
    public boolean prefersCondensedTitle() {
        return false;
    }

    @Override // miuix.appcompat.internal.view.menu.MenuView.ItemView
    public void setShortcut(boolean z, char c) {
    }

    @Override // miuix.appcompat.internal.view.menu.MenuView.ItemView
    public boolean showsIcon() {
        return true;
    }

    public ActionMenuItemView(Context context) {
        this(context, null, 0);
    }

    public ActionMenuItemView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ActionMenuItemView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        boolean z = false;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ActionMenuItemView, R.attr.actionButtonStyle, 0);
        if (typedArrayObtainStyledAttributes.getBoolean(R.styleable.ActionMenuItemView_largeFontAdaptationEnabled, true) && MiuixUIUtils.getFontLevel(context) == 2) {
            z = true;
        }
        typedArrayObtainStyledAttributes.recycle();
        ActionMenuItemViewChildren actionMenuItemViewChildren = new ActionMenuItemViewChildren(this);
        this.mChildren = actionMenuItemViewChildren;
        actionMenuItemViewChildren.setLargeFontEnabled(z);
    }

    @Override // miuix.appcompat.internal.view.menu.MenuView.ItemView
    public void initialize(MenuItemImpl menuItemImpl, int i) {
        this.mItemData = menuItemImpl;
        setSelected(false);
        setTitle(menuItemImpl.getTitle());
        setIcon(menuItemImpl.getIcon());
        setCheckable(menuItemImpl.isCheckable());
        setChecked(menuItemImpl.isChecked());
        setEnabled(menuItemImpl.isEnabled());
        setClickable(true);
        this.mChildren.setContentDescription(menuItemImpl.getContentDescription());
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mChildren.onConfigurationChanged(configuration);
    }

    @Override // android.view.View, miuix.appcompat.internal.view.menu.MenuView.ItemView
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        this.mChildren.setEnabled(z);
    }

    @Override // miuix.appcompat.internal.view.menu.MenuView.ItemView
    public MenuItemImpl getItemData() {
        return this.mItemData;
    }

    @Override // miuix.appcompat.internal.view.menu.MenuView.ItemView
    public void setTitle(CharSequence charSequence) {
        this.mChildren.setText(charSequence);
    }

    @Override // miuix.appcompat.internal.view.menu.MenuView.ItemView
    public void setCheckable(boolean z) {
        this.mIsCheckable = z;
    }

    @Override // miuix.appcompat.internal.view.menu.MenuView.ItemView
    public void setChecked(boolean z) {
        if (this.mIsCheckable) {
            setSelected(z);
        }
    }

    @Override // miuix.appcompat.internal.view.menu.MenuView.ItemView
    public void setIcon(Drawable drawable) {
        this.mChildren.setIcon(drawable);
    }

    @Override // android.view.View
    public boolean performClick() {
        if (super.performClick()) {
            return true;
        }
        MenuBuilder.ItemInvoker itemInvoker = this.mItemInvoker;
        if (itemInvoker == null || !itemInvoker.invokeItem(this.mItemData, 0)) {
            return false;
        }
        playSoundEffect(0);
        return true;
    }

    @Override // miuix.appcompat.internal.view.menu.MenuView.ItemView
    public void setItemInvoker(MenuBuilder.ItemInvoker itemInvoker) {
        this.mItemInvoker = itemInvoker;
    }
}
