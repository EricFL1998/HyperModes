package miuix.appcompat.internal.app.widget;

import android.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;
import miuix.appcompat.internal.view.ActionBarPolicy;

/* JADX INFO: loaded from: classes2.dex */
public class CollapseTabContainer extends ScrollingTabContainerView {
    @Override // miuix.appcompat.internal.app.widget.ScrollingTabContainerView
    int getDefaultTabTextStyle() {
        return R.attr.actionBarTabTextStyle;
    }

    @Override // miuix.appcompat.internal.app.widget.ScrollingTabContainerView
    int getTabViewMarginHorizontal() {
        return 0;
    }

    public CollapseTabContainer(Context context) {
        super(context);
        setContentHeight(getTabContainerHeight());
    }

    @Override // miuix.appcompat.internal.app.widget.ScrollingTabContainerView
    int getTabBarLayoutRes() {
        return miuix.appcompat.R.layout.miuix_appcompat_action_bar_tabbar;
    }

    @Override // miuix.appcompat.internal.app.widget.ScrollingTabContainerView
    int getTabViewLayoutRes() {
        return miuix.appcompat.R.layout.miuix_appcompat_action_bar_tab;
    }

    @Override // miuix.appcompat.internal.app.widget.ScrollingTabContainerView
    int getTabContainerHeight() {
        return ActionBarPolicy.get(getContext()).getTabContainerHeight();
    }

    @Override // miuix.appcompat.internal.app.widget.ScrollingTabContainerView
    public View updateCustomTabView(ViewGroup viewGroup, View view, TextView textView, ImageView imageView) {
        ViewParent parent = view.getParent();
        if (parent != this) {
            if (parent != null) {
                ((ViewGroup) parent).removeView(view);
            }
            viewGroup.addView(view);
        }
        if (textView != null) {
            textView.setVisibility(8);
        }
        if (imageView != null) {
            imageView.setVisibility(8);
            imageView.setImageDrawable(null);
        }
        return view;
    }

    @Override // miuix.appcompat.internal.app.widget.ScrollingTabContainerView, android.widget.HorizontalScrollView, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        int measuredHeight = getMeasuredHeight();
        int measuredHeight2 = this.mTabLayout.getMeasuredHeight();
        int i5 = (measuredHeight - measuredHeight2) / 2;
        this.mTabLayout.layout(i, i5, this.mTabLayout.getMeasuredWidth(), measuredHeight2 + i5);
    }
}
