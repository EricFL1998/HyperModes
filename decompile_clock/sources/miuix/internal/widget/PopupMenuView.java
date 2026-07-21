package miuix.internal.widget;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import miuix.popupwidget.internal.strategy.IPopupWindowStrategy;
import miuix.popupwidget.widget.PopupView;

/* JADX INFO: loaded from: classes2.dex */
public class PopupMenuView implements IPopupMenuWidget {
    private PopupMenuAdapter mAdapter;
    private View mAnchor;
    private final Context mContext;
    private View mDecorView;
    private PopupView mPopupView;

    @Override // miuix.internal.widget.IPopupMenuWidget
    public float getDimAmount() {
        return 0.0f;
    }

    @Override // miuix.internal.widget.IPopupMenuWidget
    public int getWindowManagerFlags() {
        return 0;
    }

    protected void onDismiss() {
    }

    protected void onMenuItemClick(MenuItem menuItem) {
    }

    @Override // miuix.internal.widget.IPopupMenuWidget
    public void setClippingEnabled(boolean z) {
    }

    @Override // miuix.internal.widget.IPopupMenuWidget
    public void setDimAmount(float f) {
    }

    @Override // miuix.internal.widget.IPopupMenuWidget
    public void setPopupWindowStrategy(IPopupWindowStrategy iPopupWindowStrategy) {
    }

    @Override // miuix.internal.widget.IPopupMenuWidget
    public void setWindowManagerFlags(int i) {
    }

    public PopupMenuView(Context context) {
        this.mContext = context;
        this.mPopupView = new PopupView(context);
        initAdapter(context);
        setupListener();
    }

    public void setDecorView(View view) {
        this.mDecorView = view;
    }

    public void setAnchor(View view) {
        this.mAnchor = view;
    }

    @Override // miuix.internal.widget.IPopupMenuWidget
    public void update(Menu menu) {
        this.mAdapter.update(menu);
    }

    @Override // miuix.internal.widget.IPopupMenuWidget
    public void showAsDropDown(View view) {
        this.mPopupView.setAnchorView(view).setDecorView(this.mDecorView);
        this.mPopupView.showWithAnchor();
    }

    @Override // miuix.internal.widget.IPopupMenuWidget
    public void showAtLocation(View view, int i, int i2, int i3) {
        this.mPopupView.setDecorView(this.mDecorView);
        this.mPopupView.showAtLocation(view, i, i2, i3);
    }

    @Override // miuix.internal.widget.IPopupMenuWidget
    public boolean isShowing() {
        return this.mPopupView.isShowing();
    }

    public void show() {
        this.mPopupView.setAnchorView(this.mAnchor).setDecorView(this.mDecorView).setAdapter(this.mAdapter);
        this.mPopupView.showWithAnchor();
    }

    @Override // miuix.internal.widget.IPopupMenuWidget
    public void dismiss() {
        this.mPopupView.dismiss();
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.mPopupView.setOnItemClickListener(onItemClickListener);
    }

    public void setOnDismissListener(PopupView.OnDismissListener onDismissListener) {
        this.mPopupView.setOnDismissListener(onDismissListener);
    }

    @Override // miuix.internal.widget.IPopupMenuWidget
    public void setSelfBlurEnabled(boolean z) {
        this.mPopupView.setSelfBlurEnabled(z);
    }

    @Override // miuix.internal.widget.IPopupMenuWidget
    public void setDimEnabled(boolean z) {
        this.mPopupView.setDimEnabled(z);
    }

    @Override // miuix.internal.widget.IPopupMenuWidget
    public void setBackgroundBlurEnabled(boolean z) {
        this.mPopupView.setBackgroundBlurEnabled(z);
    }

    private void initAdapter(Context context) {
        PopupMenuAdapter popupMenuAdapter = new PopupMenuAdapter(context);
        this.mAdapter = popupMenuAdapter;
        this.mPopupView.setAdapter(popupMenuAdapter);
    }

    private void setupListener() {
        this.mPopupView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: miuix.internal.widget.PopupMenuView.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                PopupMenuView.this.onMenuItemClick(PopupMenuView.this.mAdapter.getItem(i));
                PopupMenuView.this.dismiss();
            }
        });
        setOnDismissListener(new PopupView.OnDismissListener() { // from class: miuix.internal.widget.PopupMenuView$$ExternalSyntheticLambda0
            @Override // miuix.popupwidget.widget.PopupView.OnDismissListener
            public final void onDismiss() {
                this.f$0.onDismiss();
            }
        });
    }
}
