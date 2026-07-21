package miuix.navigator;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/* JADX INFO: loaded from: classes3.dex */
class NavigatorLayoutManager extends LinearLayoutManager {
    NavigatorLayoutManager(Context context) {
        super(context);
    }

    @Override // androidx.recyclerview.widget.LinearLayoutManager
    protected void calculateExtraLayoutSpace(RecyclerView.State state, int[] iArr) {
        int height = (getHeight() - getPaddingTop()) - getPaddingBottom();
        iArr[1] = height;
        iArr[0] = height;
    }
}
