package miuix.appcompat.app.floatingactivity.helper;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import miuix.appcompat.app.AppCompatActivity;
import miuix.appcompat.app.floatingactivity.IActivitySwitcherAnimation;
import miuix.appcompat.app.floatingactivity.OnFloatingActivityCallback;
import miuix.appcompat.app.floatingactivity.OnFloatingCallback;

/* JADX INFO: loaded from: classes2.dex */
public abstract class BaseFloatingActivityHelper implements IActivitySwitcherAnimation {
    public abstract boolean delegateFinishFloatingActivityInternal();

    public abstract void exitFloatingActivityAll();

    public abstract View getFloatingBrightPanel();

    public abstract ViewGroup.LayoutParams getFloatingLayoutParam();

    public abstract void hideFloatingBrightPanel();

    public abstract void hideFloatingDimBackground();

    public abstract void init(View view, boolean z);

    public abstract boolean isFloatingModeSupport();

    public abstract void onBackPressed();

    public abstract ViewGroup replaceSubDecor(View view, boolean z);

    public abstract void setEnableSwipToDismiss(boolean z);

    public abstract void setFloatingWindowBorderEnable(boolean z);

    public abstract void setFloatingWindowMode(boolean z);

    public abstract void setOnFloatingCallback(OnFloatingCallback onFloatingCallback);

    public abstract void setOnFloatingWindowCallback(OnFloatingActivityCallback onFloatingActivityCallback);

    public abstract boolean shouldInterceptBack();

    public abstract void showFloatingBrightPanel();

    public static boolean isFloatingWindow(Context context) {
        return (context instanceof AppCompatActivity) && ((AppCompatActivity) context).isInFloatingWindowMode();
    }
}
