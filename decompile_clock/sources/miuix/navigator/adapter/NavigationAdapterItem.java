package miuix.navigator.adapter;

import androidx.recyclerview.widget.RecyclerView;
import miuix.navigator.navigatorinfo.NavigatorInfo;

/* JADX INFO: loaded from: classes3.dex */
interface NavigationAdapterItem {
    int findNavigatorInfo(NavigatorInfo navigatorInfo);

    int getItemCount();

    long getItemId(int i);

    int getItemViewType(int i);

    CharSequence getTitle();

    void handleBindViewHolder(RecyclerView.ViewHolder viewHolder, int i);
}
