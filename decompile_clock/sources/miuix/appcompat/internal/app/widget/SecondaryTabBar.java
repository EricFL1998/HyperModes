package miuix.appcompat.internal.app.widget;

import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import miuix.appcompat.app.ActionBar;

/* JADX INFO: loaded from: classes2.dex */
public interface SecondaryTabBar extends ActionBar.FragmentViewPagerChangeListener {
    void addTab(androidx.appcompat.app.ActionBar.Tab tab, int i, boolean z);

    void addTab(androidx.appcompat.app.ActionBar.Tab tab, boolean z);

    void animateToTab(int i);

    ViewGroup asViewGroup();

    void removeAllTabs();

    void removeTabAt(int i);

    void setBadgeVisibility(int i, boolean z);

    void setParentBlurEnabled(boolean z);

    void setTabBadgeDisappearOnClick(int i, boolean z);

    void setTabIconWithPosition(int i, int i2, Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4);

    void setTabSelected(int i);

    @Deprecated
    void setTextAppearance(int i, int i2);

    void setTextAppearance(int i, int i2, int i3);

    void updateTab(int i);
}
