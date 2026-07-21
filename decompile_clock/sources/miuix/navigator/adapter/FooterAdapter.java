package miuix.navigator.adapter;

import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;

/* JADX INFO: loaded from: classes3.dex */
public abstract class FooterAdapter {
    public abstract void onBindFooterView(RecyclerView.ViewHolder viewHolder);

    public abstract RecyclerView.ViewHolder onCreateFooterView(ViewGroup viewGroup);
}
