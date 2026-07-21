package miuix.appcompat.app.floatingactivity;

import miuix.appcompat.app.AppCompatActivity;

/* JADX INFO: loaded from: classes2.dex */
public interface OnFloatingCallback extends OnFloatingActivityCallback {
    void closeAllPage();

    @Deprecated
    int getPageCount();

    void getSnapShotAndSetPanel(AppCompatActivity appCompatActivity);

    boolean isFirstPage();

    boolean isFirstPageEnterAnimExecuteEnable();

    boolean isFirstPageExitAnimExecuteEnable();

    void markActivityOpenEnterAnimExecuted(AppCompatActivity appCompatActivity);

    void onDragEnd();

    void onDragStart();

    void onHideBehindPage();
}
