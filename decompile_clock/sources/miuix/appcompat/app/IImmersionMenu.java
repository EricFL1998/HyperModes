package miuix.appcompat.app;

import android.view.View;
import android.view.ViewGroup;

/* JADX INFO: loaded from: classes2.dex */
public interface IImmersionMenu {
    void dismissImmersionMenu(boolean z);

    void hideEndOverflowMenu();

    void hideOverflowMenu();

    void setImmersionMenuEnabled(boolean z);

    void showEndOverflowMenu();

    void showImmersionMenu();

    void showImmersionMenu(View view, ViewGroup viewGroup);

    void showOverflowMenu();
}
