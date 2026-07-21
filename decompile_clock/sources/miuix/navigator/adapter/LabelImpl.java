package miuix.navigator.adapter;

import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.RecyclerView;
import miuix.navigator.NavigationItem;
import miuix.navigator.Navigator;
import miuix.navigator.navigatorinfo.NavigatorInfo;

/* JADX INFO: loaded from: classes3.dex */
public class LabelImpl extends Navigator.Label implements NavigationItem, NavigationAdapterItem {
    private RecyclerView.ViewHolder mHolder;
    private Drawable mIcon = null;
    private int mIconRes = -1;
    private final int mId;
    private NavigatorInfo mNavigatorInfo;
    private CharSequence mTitle;

    @Override // miuix.navigator.adapter.NavigationAdapterItem
    public int getItemCount() {
        return 1;
    }

    @Override // miuix.navigator.adapter.NavigationAdapterItem
    public int getItemViewType(int i) {
        return -1;
    }

    public LabelImpl(int i) {
        this.mId = i;
    }

    @Override // miuix.navigator.Navigator.Label, miuix.navigator.NavigationItem
    public NavigatorInfo getNavigatorInfo() {
        return this.mNavigatorInfo;
    }

    @Override // miuix.navigator.Navigator.Label, miuix.navigator.NavigationItem
    public void setNavigatorInfo(NavigatorInfo navigatorInfo) {
        this.mNavigatorInfo = navigatorInfo;
    }

    @Override // miuix.navigator.Navigator.Label
    public void setTitle(CharSequence charSequence) {
        this.mTitle = charSequence;
        notifyChanged();
    }

    @Override // miuix.navigator.Navigator.Label, miuix.navigator.adapter.NavigationAdapterItem
    public CharSequence getTitle() {
        return this.mTitle;
    }

    @Override // miuix.navigator.Navigator.Label
    public Drawable getIcon() {
        return this.mIcon;
    }

    @Override // miuix.navigator.Navigator.Label
    public int getIconResId() {
        return this.mIconRes;
    }

    @Override // miuix.navigator.Navigator.Label
    public void setIcon(Drawable drawable) {
        this.mIcon = drawable;
        this.mIconRes = -1;
        notifyChanged();
    }

    @Override // miuix.navigator.Navigator.Label
    public void setIcon(int i) {
        this.mIconRes = i;
        this.mIcon = null;
        notifyChanged();
    }

    @Override // miuix.navigator.adapter.NavigationAdapterItem
    public long getItemId(int i) {
        return this.mId;
    }

    @Override // miuix.navigator.adapter.NavigationAdapterItem
    public void handleBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        this.mHolder = viewHolder;
        NavigationAdapter navigationAdapter = (NavigationAdapter) viewHolder.getBindingAdapter();
        if (navigationAdapter != null) {
            navigationAdapter.getLabelAdapter().onBindViewHolder(viewHolder, this);
        }
    }

    @Override // miuix.navigator.adapter.NavigationAdapterItem
    public int findNavigatorInfo(NavigatorInfo navigatorInfo) {
        return navigatorInfo.equals(this.mNavigatorInfo) ? 0 : -1;
    }

    void notifyChanged() {
        RecyclerView.ViewHolder viewHolder = this.mHolder;
        if (viewHolder == null || viewHolder.getBindingAdapter() == null) {
            return;
        }
        this.mHolder.getBindingAdapter().notifyItemChanged(this.mHolder.getBindingAdapterPosition());
    }
}
