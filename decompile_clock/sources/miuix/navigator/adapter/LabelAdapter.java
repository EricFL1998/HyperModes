package miuix.navigator.adapter;

import android.R;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.recyclerview.widget.RecyclerView;
import miuix.navigator.Navigator;
import miuix.navigator.navigatorinfo.NavigatorInfo;

/* JADX INFO: loaded from: classes3.dex */
public class LabelAdapter {
    private NavigationAdapter mNavigationAdapter;
    private Navigator mNavigator;
    private final WidgetProvider<Navigator.Label> mWidgetProvider;

    public LabelAdapter() {
        this(null);
    }

    public LabelAdapter(WidgetProvider<Navigator.Label> widgetProvider) {
        this.mWidgetProvider = widgetProvider;
    }

    void onAttachNavigationAdapter(NavigationAdapter navigationAdapter) {
        this.mNavigationAdapter = navigationAdapter;
        this.mNavigator = navigationAdapter.getNavigator();
    }

    public Navigator getNavigator() {
        return this.mNavigator;
    }

    public void onPrepareViewHolder(RecyclerView.ViewHolder viewHolder) {
        ViewGroup viewGroup = (ViewGroup) viewHolder.itemView.findViewById(R.id.widget_frame);
        WidgetProvider<Navigator.Label> widgetProvider = this.mWidgetProvider;
        if (widgetProvider != null) {
            widgetProvider.onPrepareWidget(viewGroup);
        }
    }

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final Navigator.Label label) {
        ((TextView) viewHolder.itemView.findViewById(R.id.title)).setText(label.getTitle());
        ImageView imageView = (ImageView) viewHolder.itemView.findViewById(R.id.icon);
        if (label.getIcon() != null) {
            imageView.setImageDrawable(label.getIcon());
        } else if (label.getIconResId() != -1) {
            imageView.setImageResource(label.getIconResId());
        } else {
            imageView.setImageDrawable(null);
        }
        NavigatorInfo currentInfo = this.mNavigator.getCurrentInfo();
        viewHolder.itemView.setActivated(currentInfo != null && currentInfo.equals(label.getNavigatorInfo()));
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() { // from class: miuix.navigator.adapter.LabelAdapter$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m1898lambda$onBindViewHolder$0$miuixnavigatoradapterLabelAdapter(label, view);
            }
        });
        setupWidgetFrame((ViewGroup) viewHolder.itemView.findViewById(R.id.widget_frame), label);
        setAccessibilityDelegateLabelItemView(viewHolder.itemView);
    }

    /* JADX INFO: renamed from: lambda$onBindViewHolder$0$miuix-navigator-adapter-LabelAdapter, reason: not valid java name */
    /* synthetic */ void m1898lambda$onBindViewHolder$0$miuixnavigatoradapterLabelAdapter(Navigator.Label label, View view) {
        getNavigator().navigate(label.getNavigatorInfo());
    }

    private void setAccessibilityDelegateLabelItemView(View view) {
        ViewCompat.setAccessibilityDelegate(view, new AccessibilityDelegateCompat() { // from class: miuix.navigator.adapter.LabelAdapter.1
            @Override // androidx.core.view.AccessibilityDelegateCompat
            public void onInitializeAccessibilityNodeInfo(View view2, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(view2, accessibilityNodeInfoCompat);
                accessibilityNodeInfoCompat.setCheckable(true);
                accessibilityNodeInfoCompat.setChecked(view2.isActivated());
                accessibilityNodeInfoCompat.setClickable(true ^ view2.isActivated());
                if (!view2.isActivated()) {
                    accessibilityNodeInfoCompat.addAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK);
                } else {
                    accessibilityNodeInfoCompat.removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK);
                }
            }
        });
    }

    private void setupWidgetFrame(ViewGroup viewGroup, Navigator.Label label) {
        if (viewGroup == null) {
            return;
        }
        if (this.mWidgetProvider != null) {
            viewGroup.setVisibility(0);
            this.mWidgetProvider.onSetupWidget(viewGroup, label, false);
        } else {
            viewGroup.setVisibility(8);
        }
    }

    public final void notifyChanged(Navigator.Label label) {
        if (label instanceof LabelImpl) {
            ((LabelImpl) label).notifyChanged();
        }
    }

    public final void notifyDataSetChanged() {
        NavigationAdapter navigationAdapter = this.mNavigationAdapter;
        if (navigationAdapter != null) {
            navigationAdapter.notifyDataSetChanged();
        }
    }
}
