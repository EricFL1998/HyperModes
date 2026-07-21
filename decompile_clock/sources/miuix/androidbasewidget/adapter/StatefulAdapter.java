package miuix.androidbasewidget.adapter;

import android.os.Parcelable;

/* JADX INFO: loaded from: classes2.dex */
public interface StatefulAdapter {
    void restoreState(Parcelable parcelable);

    Parcelable saveState();
}
