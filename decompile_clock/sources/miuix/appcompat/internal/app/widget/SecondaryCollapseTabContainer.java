package miuix.appcompat.internal.app.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import androidx.appcompat.app.ActionBar;
import miuix.springback.view.SpringBackLayout;

/* JADX INFO: loaded from: classes2.dex */
public class SecondaryCollapseTabContainer extends SpringBackLayout implements SecondaryTabBar {
    private final SecondaryTabContainerView mTabContainer;

    @Override // miuix.appcompat.internal.app.widget.SecondaryTabBar
    public ViewGroup asViewGroup() {
        return this;
    }

    public SecondaryCollapseTabContainer(Context context) {
        super(context);
        setScrollOrientation(1);
        SecondaryTabContainerView secondaryTabContainerView = new SecondaryTabContainerView(context);
        this.mTabContainer = secondaryTabContainerView;
        addView(secondaryTabContainerView);
        secondaryTabContainerView.setContentHeight(secondaryTabContainerView.getTabContainerHeight());
        setTarget(secondaryTabContainerView);
    }

    @Override // miuix.springback.view.SpringBackLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        setSpringBackEnable(this.mTabContainer.canScrollHorizontally());
    }

    @Override // miuix.appcompat.internal.app.widget.SecondaryTabBar
    public void setParentBlurEnabled(boolean z) {
        this.mTabContainer.setParentBlurEnabled(z);
    }

    @Override // miuix.appcompat.internal.app.widget.SecondaryTabBar
    public void setBadgeVisibility(int i, boolean z) {
        this.mTabContainer.setBadgeVisibility(i, z);
    }

    @Override // miuix.appcompat.internal.app.widget.SecondaryTabBar
    public void setTabBadgeDisappearOnClick(int i, boolean z) {
        this.mTabContainer.setTabBadgeDisappearOnClick(i, z);
    }

    @Override // miuix.appcompat.internal.app.widget.SecondaryTabBar
    public void setTabIconWithPosition(int i, int i2, Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
        this.mTabContainer.setTabIconWithPosition(i, i2, drawable, drawable2, drawable3, drawable4);
    }

    @Override // miuix.appcompat.internal.app.widget.SecondaryTabBar
    public void setTextAppearance(int i, int i2) {
        this.mTabContainer.setTextAppearance(i, i2);
    }

    @Override // miuix.appcompat.internal.app.widget.SecondaryTabBar
    public void setTextAppearance(int i, int i2, int i3) {
        this.mTabContainer.setTextAppearance(i, i2, i3);
    }

    @Override // miuix.appcompat.internal.app.widget.SecondaryTabBar
    public void addTab(ActionBar.Tab tab, boolean z) {
        this.mTabContainer.addTab(tab, z);
    }

    @Override // miuix.appcompat.internal.app.widget.SecondaryTabBar
    public void addTab(ActionBar.Tab tab, int i, boolean z) {
        this.mTabContainer.addTab(tab, i, z);
    }

    @Override // miuix.appcompat.internal.app.widget.SecondaryTabBar
    public void updateTab(int i) {
        this.mTabContainer.updateTab(i);
    }

    @Override // miuix.appcompat.internal.app.widget.SecondaryTabBar
    public void removeTabAt(int i) {
        this.mTabContainer.removeTabAt(i);
    }

    @Override // miuix.appcompat.internal.app.widget.SecondaryTabBar
    public void removeAllTabs() {
        this.mTabContainer.removeAllTabs();
    }

    @Override // miuix.appcompat.internal.app.widget.SecondaryTabBar
    public void animateToTab(int i) {
        this.mTabContainer.animateToTab(i);
    }

    @Override // miuix.appcompat.internal.app.widget.SecondaryTabBar
    public void setTabSelected(int i) {
        this.mTabContainer.setTabSelected(i);
    }

    @Override // miuix.appcompat.app.ActionBar.FragmentViewPagerChangeListener
    public void onPageScrolled(int i, float f, boolean z, boolean z2) {
        this.mTabContainer.onPageScrolled(i, f, z, z2);
    }

    @Override // miuix.appcompat.app.ActionBar.FragmentViewPagerChangeListener
    public void onPageSelected(int i) {
        this.mTabContainer.onPageSelected(i);
    }

    @Override // miuix.appcompat.app.ActionBar.FragmentViewPagerChangeListener
    public void onPageScrollStateChanged(int i) {
        this.mTabContainer.onPageScrollStateChanged(i);
    }
}
