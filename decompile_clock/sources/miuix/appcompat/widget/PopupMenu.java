package miuix.appcompat.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupWindow;
import androidx.appcompat.view.SupportMenuInflater;
import miuix.appcompat.R;
import miuix.appcompat.internal.view.menu.MenuBuilder;
import miuix.internal.widget.IPopupMenuWidget;
import miuix.internal.widget.PopupMenuView;
import miuix.internal.widget.PopupMenuWindow;
import miuix.popupwidget.internal.strategy.IPopupWindowStrategy;

/* JADX INFO: loaded from: classes2.dex */
public class PopupMenu {
    private final View mAnchor;
    private final Context mContext;
    private boolean mIsEnableImmersive;
    private final MenuBuilder mMenu;
    private OnMenuItemClickListener mMenuItemClickListener;
    private OnDismissListener mOnDismissListener;
    private IPopupMenuWidget mPopupMenu;

    public interface OnDismissListener {
        void onDismiss(PopupMenu popupMenu);
    }

    public interface OnMenuItemClickListener {
        boolean onMenuItemClick(MenuItem menuItem);
    }

    public PopupMenu(Context context, View view) {
        this(context, view, 0);
    }

    public PopupMenu(Context context, View view, int i) {
        this.mIsEnableImmersive = true;
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
    }

    private MenuInflater getMenuInflater() {
        return new SupportMenuInflater(this.mContext);
    }

    public Menu getMenu() {
        return this.mMenu;
    }

    public void inflate(int i) {
        getMenuInflater().inflate(i, this.mMenu);
    }

    public void show() {
        IPopupMenuWidget popupMenu = getPopupMenu();
        popupMenu.update(this.mMenu);
        popupMenu.showAsDropDown(this.mAnchor);
    }

    @Deprecated
    public void showAsDropDown(int i, int i2) {
        IPopupMenuWidget popupMenu = getPopupMenu();
        popupMenu.update(this.mMenu);
        popupMenu.showAsDropDown(this.mAnchor);
    }

    public void showAtLocation(View view, int i, int i2, int i3) {
        IPopupMenuWidget popupMenu = getPopupMenu();
        popupMenu.setClippingEnabled(true);
        popupMenu.update(this.mMenu);
        popupMenu.showAtLocation(view, i, i2, i3);
    }

    public void setDimAmount(float f) {
        getPopupMenu().setDimAmount(f);
    }

    public float getDimAmount() {
        return getPopupMenu().getDimAmount();
    }

    public void setWindowManagerFlags(int i) {
        getPopupMenu().setWindowManagerFlags(i);
    }

    public int getWindowManagerFlags() {
        return getPopupMenu().getWindowManagerFlags();
    }

    public boolean isShowing() {
        return getPopupMenu().isShowing();
    }

    public void setStrategy(IPopupWindowStrategy iPopupWindowStrategy) {
        getPopupMenu().setPopupWindowStrategy(iPopupWindowStrategy);
    }

    public void dismiss() {
        getPopupMenu().dismiss();
    }

    public void setEnabledImmersive(boolean z) {
        if (z != this.mIsEnableImmersive) {
            this.mIsEnableImmersive = z;
            this.mPopupMenu = createPopupMenu(z);
        }
    }

    public void setDimEnabled(boolean z) {
        getPopupMenu().setDimEnabled(z);
    }

    public void setSelfBlurEnabled(boolean z) {
        getPopupMenu().setSelfBlurEnabled(z);
    }

    public void setBackgroundBlurEnabled(boolean z) {
        getPopupMenu().setBackgroundBlurEnabled(z);
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        this.mMenuItemClickListener = onMenuItemClickListener;
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.mOnDismissListener = onDismissListener;
    }

    public PopupWindow getWindow() {
        if (this.mIsEnableImmersive) {
            return (PopupWindow) this.mPopupMenu;
        }
        return null;
    }

    private IPopupMenuWidget createPopupMenu(boolean z) {
        if (z) {
            return new PopupMenuWindow(this.mContext) { // from class: miuix.appcompat.widget.PopupMenu.1
                @Override // miuix.internal.widget.PopupMenuWindow
                protected void onMenuItemClick(MenuItem menuItem) {
                    if (PopupMenu.this.mMenuItemClickListener != null) {
                        PopupMenu.this.mMenuItemClickListener.onMenuItemClick(menuItem);
                    }
                }

                @Override // miuix.internal.widget.PopupMenuWindow
                protected void onDismiss() {
                    if (PopupMenu.this.mOnDismissListener != null) {
                        PopupMenu.this.mOnDismissListener.onDismiss(PopupMenu.this);
                    }
                }
            };
        }
        return new PopupMenuView(this.mContext) { // from class: miuix.appcompat.widget.PopupMenu.2
            @Override // miuix.internal.widget.PopupMenuView
            protected void onMenuItemClick(MenuItem menuItem) {
                if (PopupMenu.this.mMenuItemClickListener != null) {
                    PopupMenu.this.mMenuItemClickListener.onMenuItemClick(menuItem);
                }
            }

            @Override // miuix.internal.widget.PopupMenuView
            protected void onDismiss() {
                if (PopupMenu.this.mOnDismissListener != null) {
                    PopupMenu.this.mOnDismissListener.onDismiss(PopupMenu.this);
                }
            }
        };
    }

    private IPopupMenuWidget getPopupMenu() {
        if (this.mPopupMenu == null) {
            this.mPopupMenu = createPopupMenu(this.mIsEnableImmersive);
        }
        return this.mPopupMenu;
    }
}
