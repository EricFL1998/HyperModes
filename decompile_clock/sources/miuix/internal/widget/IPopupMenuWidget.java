package miuix.internal.widget;

import android.view.Menu;
import android.view.View;
import miuix.popupwidget.internal.strategy.IPopupWindowStrategy;

/* JADX INFO: loaded from: classes2.dex */
public interface IPopupMenuWidget {
    void dismiss();

    float getDimAmount();

    int getWindowManagerFlags();

    boolean isShowing();

    void setBackgroundBlurEnabled(boolean z);

    void setClippingEnabled(boolean z);

    void setDimAmount(float f);

    void setDimEnabled(boolean z);

    void setPopupWindowStrategy(IPopupWindowStrategy iPopupWindowStrategy);

    void setSelfBlurEnabled(boolean z);

    void setWindowManagerFlags(int i);

    void showAsDropDown(View view);

    void showAtLocation(View view, int i, int i2, int i3);

    void update(Menu menu);
}
