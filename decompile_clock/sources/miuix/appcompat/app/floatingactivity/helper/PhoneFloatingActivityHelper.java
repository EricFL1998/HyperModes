package miuix.appcompat.app.floatingactivity.helper;

import android.R;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.view.ViewCompat;
import miuix.appcompat.app.floatingactivity.OnFloatingActivityCallback;
import miuix.appcompat.app.floatingactivity.OnFloatingCallback;
import miuix.internal.util.AttributeResolver;
import miuix.internal.util.ViewUtils;

/* JADX INFO: loaded from: classes2.dex */
public class PhoneFloatingActivityHelper extends BaseFloatingActivityHelper {
    private Drawable mDefaultPanelBg;
    private View mPanel;

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public boolean delegateFinishFloatingActivityInternal() {
        return false;
    }

    @Override // miuix.appcompat.app.floatingactivity.IActivitySwitcherAnimation
    public void executeCloseEnterAnimation() {
    }

    @Override // miuix.appcompat.app.floatingactivity.IActivitySwitcherAnimation
    public void executeCloseExitAnimation() {
    }

    @Override // miuix.appcompat.app.floatingactivity.IActivitySwitcherAnimation
    public void executeOpenEnterAnimation() {
    }

    @Override // miuix.appcompat.app.floatingactivity.IActivitySwitcherAnimation
    public void executeOpenExitAnimation() {
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public void exitFloatingActivityAll() {
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public void hideFloatingBrightPanel() {
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public void hideFloatingDimBackground() {
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public boolean isFloatingModeSupport() {
        return false;
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public void onBackPressed() {
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public void setEnableSwipToDismiss(boolean z) {
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public void setFloatingWindowBorderEnable(boolean z) {
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public void setFloatingWindowMode(boolean z) {
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public void setOnFloatingCallback(OnFloatingCallback onFloatingCallback) {
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public void setOnFloatingWindowCallback(OnFloatingActivityCallback onFloatingActivityCallback) {
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public boolean shouldInterceptBack() {
        return false;
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public void showFloatingBrightPanel() {
    }

    public PhoneFloatingActivityHelper(Context context) {
        this.mDefaultPanelBg = AttributeResolver.resolveDrawable(context, R.attr.windowBackground);
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public ViewGroup replaceSubDecor(View view, boolean z) {
        this.mPanel = view;
        return (ViewGroup) view;
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public void init(View view, boolean z) {
        View view2 = this.mPanel;
        if (view2 != null) {
            if (ViewUtils.isNightMode(view2.getContext())) {
                this.mPanel.setBackground(new ColorDrawable(ViewCompat.MEASURED_STATE_MASK));
            } else {
                this.mPanel.setBackground(this.mDefaultPanelBg);
            }
        }
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public View getFloatingBrightPanel() {
        return this.mPanel;
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public ViewGroup.LayoutParams getFloatingLayoutParam() {
        return this.mPanel.getLayoutParams();
    }
}
