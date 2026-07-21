package miuix.appcompat.app;

import miuix.container.ExtraPaddingObserver;

/* JADX INFO: loaded from: classes2.dex */
public interface IActivity extends IImmersionMenu, IContentInsetState, ExtraPaddingObserver {
    void checkThemeLegality();

    int getTranslucentStatus();

    boolean isFloatingWindowTheme();

    boolean isInFloatingWindowMode();

    void setFloatingWindowBorderEnable(boolean z);

    void setFloatingWindowMode(boolean z);

    void setTranslucentStatus(int i);
}
