package miuix.navigator;

import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import miuix.navigator.adapter.CategoryAdapter;
import miuix.navigator.adapter.LabelAdapter;
import miuix.navigator.draganddrop.NavigatorDragListener;
import miuix.navigator.navigation.NavigationBarView;
import miuix.navigator.navigatorinfo.NavigatorInfo;
import miuix.navigator.navigatorinfo.NavigatorInfoProvider;

/* JADX INFO: loaded from: classes3.dex */
public abstract class Navigator implements NavigatorFragmentListener {
    public static final String KEY_MIUIX_NAVIGATION_INIT_OPEN = "miuix:navigationInitOpen";
    public static final String KEY_MIUIX_NAVIGATOR_MODE = "miuix:navigatorMode";
    public static final String KEY_MIUIX_NAVIGATOR_STATE = "miuix:navigatorState";
    public static final String KEY_MIUIX_NAVIGATOR_STRATEGY = "miuix:navigatorStrategy";
    public static final int SPLIT_ANIMATION_DEFAULT_STYLE = 0;
    public static final int SPLIT_ANIMATION_SECTION_STYLE = 1;
    public static final int SPLIT_ANIMATION_SHADE_STYLE = 2;
    public static final String TAG_CONTENT = "miuix.content";
    public static final String TAG_NAVIGATION = "miuix.navigation";
    public static final String TAG_ROOT = "miuix.root";
    public static final String TAG_SECONDARY_CONTENT = "miuix.secondaryContent";
    public static final int VIEW_TYPE_CATEGORY = -2;
    public static final int VIEW_TYPE_DRAG_PLACEHOLDER = -3;
    public static final int VIEW_TYPE_LABEL = -1;

    public static abstract class Category {
        public abstract CategoryAdapter<? extends CategoryAdapter.Item> getAdapter();

        public abstract int getFooterId();

        public abstract int getId();

        public abstract CharSequence getTitle();

        public abstract void setAdapter(CategoryAdapter<? extends CategoryAdapter.Item> categoryAdapter);

        public abstract void setCollapsable(boolean z);

        public abstract void setNavigatorDragListener(NavigatorDragListener navigatorDragListener);

        public abstract void setTitle(CharSequence charSequence);
    }

    public static abstract class Label implements NavigationItem {
        public abstract Drawable getIcon();

        public abstract int getIconResId();

        @Override // miuix.navigator.NavigationItem
        public abstract NavigatorInfo getNavigatorInfo();

        public abstract CharSequence getTitle();

        public abstract void setIcon(int i);

        public abstract void setIcon(Drawable drawable);

        @Override // miuix.navigator.NavigationItem
        public abstract void setNavigatorInfo(NavigatorInfo navigatorInfo);

        public abstract void setTitle(CharSequence charSequence);
    }

    public enum Mode {
        C,
        NC,
        LC,
        NLC
    }

    public interface NavigatorStateListener {
        default void onContentCloseBegin() {
        }

        default void onContentCloseCancel() {
        }

        default void onContentCloseFinish() {
        }

        default void onContentOpenBegin() {
        }

        default void onContentOpenCancel() {
        }

        default void onContentOpenFinish() {
        }

        default void onContentRatioChanged(float f) {
        }

        default void onNavigationCloseBegin() {
        }

        default void onNavigationCloseCancel() {
        }

        default void onNavigationCloseFinish() {
        }

        default void onNavigationOpenBegin() {
        }

        default void onNavigationOpenCancel() {
        }

        default void onNavigationOpenFinish() {
        }

        default void onNavigationRatioChanged(float f) {
        }
    }

    public enum Zone {
        NORMAL,
        NAVIGATION,
        CONTENT,
        SECONDARY_CONTENT
    }

    public abstract void addCategory(Category category);

    public abstract void addCategory(Category category, int i);

    public abstract void addLabel(Label label);

    public abstract void addLabel(Label label, int i);

    public abstract void addNavigatorFragmentListener(NavigatorFragmentListener navigatorFragmentListener);

    public abstract void addNavigatorStateListener(NavigatorStateListener navigatorStateListener);

    public abstract void addTab(BottomTab bottomTab);

    public abstract void addTab(BottomTab bottomTab, int i);

    public abstract void applyBottomNavigationBlur(boolean z);

    public abstract boolean canPopSecondaryContent();

    public abstract void closeContent();

    public abstract void closeContent(boolean z);

    public abstract void closeNavigation();

    public abstract void closeNavigation(boolean z);

    public Navigator getBaseNavigator() {
        return this;
    }

    public abstract Menu getBottomTabMenu();

    public abstract NavigationBarView getBottomTabView();

    public abstract Navigator getByTag(String str);

    public abstract int getContentVisibility();

    public abstract NavigatorInfo getCurrentInfo();

    public abstract FragmentManager getFragmentManager();

    public abstract LabelAdapter getLabelAdapter();

    public abstract int getNavigationMenu();

    public abstract Mode getNavigationMode();

    public abstract int getNavigationVisibility();

    public abstract OnBackPressedCallback getOnBackPressedCallback();

    public abstract int getSecondaryContentVisibility();

    public abstract NavigatorStrategy getStrategy();

    public abstract String getTag();

    public abstract void immerseSecondaryContent(boolean z);

    public abstract boolean isContentOpen();

    public abstract boolean isFocused();

    public abstract boolean isNavigationInitOpen();

    public abstract boolean isNavigationOpen();

    public abstract boolean isNavigationOverlay();

    public abstract void navigate(NavigatorInfo navigatorInfo);

    public abstract Category newCategory(int i);

    public abstract Category newCategory(int i, int i2);

    public abstract Label newLabel(int i);

    public abstract BottomTab newTab();

    @Override // miuix.navigator.NavigatorFragmentListener
    public abstract void onBottomNavigationVisibilityChanged(boolean z, int i);

    public abstract void onConfigurationChanged(Configuration configuration);

    @Override // miuix.navigator.NavigatorFragmentListener
    public abstract void onContentVisibilityChanged(int i);

    public abstract void onCreate(Bundle bundle);

    @Override // miuix.navigator.NavigatorFragmentListener
    public abstract void onNavigationVisibilityChanged(int i);

    @Override // miuix.navigator.NavigatorFragmentListener
    public abstract void onNavigatorModeChanged(Mode mode, Mode mode2);

    public abstract void onSaveInstanceState(Bundle bundle);

    @Override // miuix.navigator.NavigatorFragmentListener
    public abstract void onSecondaryContentVisibilityChanged(int i);

    public abstract void openContent();

    public abstract void openContent(boolean z);

    public abstract void openNavigation();

    public abstract void openNavigation(boolean z);

    public abstract void removeCategory(int i);

    public abstract void removeCategory(Category category);

    public abstract void removeLabel(int i);

    public abstract void removeLabel(Label label);

    public abstract void removeNavigatorFragmentListener(NavigatorFragmentListener navigatorFragmentListener);

    public abstract void removeNavigatorStateListener(NavigatorStateListener navigatorStateListener);

    public abstract void requestFocus(boolean z);

    public abstract void selectTab(int i);

    public abstract void setBottomNavigationBackgroundVisible(boolean z);

    public abstract void setBottomTabMenu(int i, NavigatorInfoProvider navigatorInfoProvider);

    public abstract void setBottomTabStyle(int i);

    public void setContentExpandedMaxWidthWithDp(int i) {
    }

    public void setContentExpandedPaddingWithDp(int i) {
    }

    public abstract void setCrossBackground(int i);

    public abstract void setCrossBackground(View view);

    public abstract void setLabelAdapter(LabelAdapter labelAdapter);

    public abstract void setNavigationMenu(int i);

    public abstract void setNavigationMode(Mode mode);

    public abstract void setNavigationWidthConfig(int i, int i2, int i3);

    public void setSplitAnimationMaskBlurRadiusWithPx(int i) {
    }

    public void setSplitAnimationStyle(int i) {
    }

    public abstract void setStrategy(NavigatorStrategy navigatorStrategy);

    public abstract void setTabSelectListener(BottomNavigation.OnItemSelectedListener onItemSelectedListener);

    public abstract void toggleContent();

    public abstract void toggleContent(boolean z);

    public abstract void toggleNavigation();

    public abstract void toggleNavigation(boolean z);

    public static Navigator get(Fragment fragment) {
        if (fragment.getHost() instanceof NavigatorFragmentController) {
            return ((NavigatorFragmentController) fragment.getHost()).getNavigator();
        }
        if (fragment instanceof NavHostFragment) {
            return ((NavHostFragment) fragment).getNavigator(fragment);
        }
        return null;
    }

    public static Zone getZone(Fragment fragment) {
        return getZone(get(fragment));
    }

    public static Zone getZone(Navigator navigator) {
        if (navigator != null) {
            if (navigator instanceof ContentSubNavigator) {
                return Zone.CONTENT;
            }
            if (navigator instanceof SecondaryContentSubNavigator) {
                return Zone.SECONDARY_CONTENT;
            }
            if (navigator instanceof NavigationSubNavigator) {
                return Zone.NAVIGATION;
            }
        }
        return Zone.NORMAL;
    }

    protected Navigator() {
    }

    public void requestFocus() {
        requestFocus(false);
    }
}
