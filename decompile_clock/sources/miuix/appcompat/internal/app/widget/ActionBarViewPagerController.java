package miuix.appcompat.internal.app.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager.widget.OriginalViewPager;
import java.util.ArrayList;
import java.util.Iterator;
import miuix.appcompat.R;
import miuix.appcompat.app.ActionBar;
import miuix.internal.util.DeviceHelper;
import miuix.springback.view.SpringBackLayout;
import miuix.viewpager.widget.ViewPager;

/* JADX INFO: loaded from: classes2.dex */
public class ActionBarViewPagerController {
    private ActionBarImpl mActionBar;
    private ObjectAnimator mActionMenuChangeAnimator;
    private ActionMenuChangeAnimatorObject mActionMenuChangeAnimatorObject;
    private ArrayList<ActionBar.FragmentViewPagerChangeListener> mListeners;
    private DynamicFragmentPagerAdapter mPagerAdapter;
    private SpringBackLayout mSpringBackLayout;
    private androidx.appcompat.app.ActionBar.TabListener mTabListener = new androidx.appcompat.app.ActionBar.TabListener() { // from class: miuix.appcompat.internal.app.widget.ActionBarViewPagerController.1
        @Override // androidx.appcompat.app.ActionBar.TabListener
        public void onTabReselected(androidx.appcompat.app.ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        }

        @Override // androidx.appcompat.app.ActionBar.TabListener
        public void onTabUnselected(androidx.appcompat.app.ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        }

        @Override // androidx.appcompat.app.ActionBar.TabListener
        public void onTabSelected(androidx.appcompat.app.ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            int count = ActionBarViewPagerController.this.mPagerAdapter.getCount();
            for (int i = 0; i < count; i++) {
                if (ActionBarViewPagerController.this.mPagerAdapter.getTabAt(i) == tab) {
                    ActionBarViewPagerController.this.mViewPager.setCurrentItem(i, tab instanceof ActionBarImpl.TabImpl ? ((ActionBarImpl.TabImpl) tab).mWithAnim : true);
                    return;
                }
            }
        }
    };
    private ViewPager mViewPager;
    private View mViewPagerDecor;

    private static class ScrollStatus {
        private static final float THRESHOLD = 1.0E-4f;
        int mFromPos;
        private float mOffsetAtScroll;
        private int mPosAtScroll;
        boolean mScrollBegin;
        boolean mScrollEnd;
        int mToPos;

        private ScrollStatus() {
            this.mPosAtScroll = -1;
        }

        void update(int i, float f) {
            if (f < 1.0E-4f) {
                onScrollEnd();
            } else if (this.mPosAtScroll != i) {
                onScrollPositionChange(i, f);
            } else if (this.mScrollBegin) {
                onScrollBegin(i, f);
            }
        }

        private void onScrollPositionChange(int i, float f) {
            this.mPosAtScroll = i;
            this.mOffsetAtScroll = f;
            this.mScrollBegin = true;
            this.mScrollEnd = false;
        }

        private void onScrollBegin(int i, float f) {
            this.mScrollBegin = false;
            boolean z = f > this.mOffsetAtScroll;
            this.mFromPos = z ? i : i + 1;
            if (z) {
                i++;
            }
            this.mToPos = i;
        }

        private void onScrollEnd() {
            this.mFromPos = this.mToPos;
            this.mPosAtScroll = -1;
            this.mOffsetAtScroll = 0.0f;
            this.mScrollEnd = true;
        }
    }

    ActionBarViewPagerController(ActionBarImpl actionBarImpl, FragmentManager fragmentManager, Lifecycle lifecycle, boolean z) {
        this.mActionBar = actionBarImpl;
        ActionBarOverlayLayout actionBarOverlayLayout = actionBarImpl.getActionBarOverlayLayout();
        Context context = actionBarOverlayLayout.getContext();
        View viewFindViewById = actionBarOverlayLayout.findViewById(R.id.view_pager);
        if (viewFindViewById instanceof ViewPager) {
            this.mViewPager = (ViewPager) viewFindViewById;
        } else {
            ViewPager viewPager = new ViewPager(context);
            this.mViewPager = viewPager;
            viewPager.setId(R.id.view_pager);
            SpringBackLayout springBackLayout = new SpringBackLayout(context);
            this.mSpringBackLayout = springBackLayout;
            springBackLayout.setScrollOrientation(5);
            springBackLayout.addView(this.mViewPager, new OriginalViewPager.LayoutParams());
            springBackLayout.setTarget(this.mViewPager);
            springBackLayout.setSpringBackEnable(this.mViewPager.isDraggable());
            ((ViewGroup) actionBarOverlayLayout.findViewById(android.R.id.content)).addView(springBackLayout, new ViewGroup.LayoutParams(-1, -1));
        }
        DynamicFragmentPagerAdapter dynamicFragmentPagerAdapter = new DynamicFragmentPagerAdapter(context, fragmentManager);
        this.mPagerAdapter = dynamicFragmentPagerAdapter;
        this.mViewPager.setAdapter(dynamicFragmentPagerAdapter);
        this.mViewPager.addOnPageChangeListener(new OriginalViewPager.OnPageChangeListener() { // from class: miuix.appcompat.internal.app.widget.ActionBarViewPagerController.2
            ScrollStatus mStatus = new ScrollStatus();

            @Override // androidx.viewpager.widget.OriginalViewPager.OnPageChangeListener
            public void onPageScrolled(int i, float f, int i2) {
                this.mStatus.update(i, f);
                if (this.mStatus.mScrollBegin || ActionBarViewPagerController.this.mListeners == null) {
                    return;
                }
                boolean zHasActionMenu = ActionBarViewPagerController.this.mPagerAdapter.hasActionMenu(this.mStatus.mFromPos);
                boolean zHasActionMenu2 = ActionBarViewPagerController.this.mPagerAdapter.hasActionMenu(this.mStatus.mToPos);
                if (ActionBarViewPagerController.this.mPagerAdapter.isRTL()) {
                    i = ActionBarViewPagerController.this.mPagerAdapter.toIndexForRTL(i);
                    if (!this.mStatus.mScrollEnd) {
                        i--;
                        f = 1.0f - f;
                    }
                }
                Iterator it = ActionBarViewPagerController.this.mListeners.iterator();
                while (it.hasNext()) {
                    ((ActionBar.FragmentViewPagerChangeListener) it.next()).onPageScrolled(i, f, zHasActionMenu, zHasActionMenu2);
                }
            }

            @Override // androidx.viewpager.widget.OriginalViewPager.OnPageChangeListener
            public void onPageSelected(int i) {
                int indexForRTL = ActionBarViewPagerController.this.mPagerAdapter.toIndexForRTL(i);
                ActionBarViewPagerController.this.mActionBar.setSelectedNavigationItem(indexForRTL);
                ActionBarViewPagerController.this.mPagerAdapter.setPrimaryItem((ViewGroup) ActionBarViewPagerController.this.mViewPager, i, (Object) ActionBarViewPagerController.this.mPagerAdapter.getFragment(i, false, false));
                if (ActionBarViewPagerController.this.mListeners != null) {
                    Iterator it = ActionBarViewPagerController.this.mListeners.iterator();
                    while (it.hasNext()) {
                        ((ActionBar.FragmentViewPagerChangeListener) it.next()).onPageSelected(indexForRTL);
                    }
                }
            }

            @Override // androidx.viewpager.widget.OriginalViewPager.OnPageChangeListener
            public void onPageScrollStateChanged(int i) {
                if (ActionBarViewPagerController.this.mListeners != null) {
                    Iterator it = ActionBarViewPagerController.this.mListeners.iterator();
                    while (it.hasNext()) {
                        ((ActionBar.FragmentViewPagerChangeListener) it.next()).onPageScrollStateChanged(i);
                    }
                }
            }
        });
        if (z && DeviceHelper.isFeatureWholeAnim()) {
            addOnFragmentViewPagerChangeListener(new ViewPagerScrollEffect(this.mViewPager, this.mPagerAdapter));
        }
    }

    int addFragmentTab(String str, androidx.appcompat.app.ActionBar.Tab tab, Class<? extends Fragment> cls, Bundle bundle, boolean z) {
        ((ActionBarImpl.TabImpl) tab).setInternalTabListener(this.mTabListener);
        this.mActionBar.internalAddTab(tab);
        int iAddFragment = this.mPagerAdapter.addFragment(str, cls, bundle, tab, z);
        if (this.mPagerAdapter.isRTL()) {
            this.mViewPager.setCurrentItem(this.mPagerAdapter.getCount() - 1);
        }
        return iAddFragment;
    }

    int addFragmentTab(String str, androidx.appcompat.app.ActionBar.Tab tab, int i, Class<? extends Fragment> cls, Bundle bundle, boolean z) {
        ((ActionBarImpl.TabImpl) tab).setInternalTabListener(this.mTabListener);
        this.mActionBar.internalAddTab(tab, i);
        int iAddFragment = this.mPagerAdapter.addFragment(str, i, cls, bundle, tab, z);
        if (this.mPagerAdapter.isRTL()) {
            this.mViewPager.setCurrentItem(this.mPagerAdapter.getCount() - 1);
        }
        return iAddFragment;
    }

    void replaceFragmentTab(String str, int i, Class<? extends Fragment> cls, Bundle bundle, boolean z) {
        this.mActionBar.updateTab(i);
        this.mPagerAdapter.replaceFragmentAt(str, i, cls, bundle, this.mActionBar.getTabAt(i), z);
    }

    void removeFragmentAt(int i) {
        this.mPagerAdapter.removeFragmentAt(i);
        this.mActionBar.internalRemoveTabAt(i);
    }

    void removeFragmentTab(String str) {
        int iFindPositionByTag = this.mPagerAdapter.findPositionByTag(str);
        if (iFindPositionByTag >= 0) {
            removeFragmentAt(iFindPositionByTag);
        }
    }

    void removeFragmentTab(androidx.appcompat.app.ActionBar.Tab tab) {
        this.mActionBar.internalRemoveTab(tab);
        this.mPagerAdapter.removeFragment(tab);
    }

    void removeAllFragmentTab() {
        this.mActionBar.internalRemoveAllTabs();
        this.mPagerAdapter.removeAllFragment();
    }

    Fragment getFragmentAt(int i) {
        return this.mPagerAdapter.getFragment(i, true);
    }

    int getFragmentTabCount() {
        return this.mPagerAdapter.getCount();
    }

    void removeFragment(Fragment fragment) {
        int iRemoveFragment = this.mPagerAdapter.removeFragment(fragment);
        if (iRemoveFragment >= 0) {
            this.mActionBar.internalRemoveTabAt(iRemoveFragment);
        }
    }

    void setFragmentActionMenuAt(int i, boolean z) {
        this.mPagerAdapter.setFragmentActionMenuAt(i, z);
        if (i == this.mViewPager.getCurrentItem()) {
            if (this.mActionMenuChangeAnimatorObject == null) {
                ActionMenuChangeAnimatorObject actionMenuChangeAnimatorObject = new ActionMenuChangeAnimatorObject();
                this.mActionMenuChangeAnimatorObject = actionMenuChangeAnimatorObject;
                ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(actionMenuChangeAnimatorObject, "Value", 0.0f, 1.0f);
                this.mActionMenuChangeAnimator = objectAnimatorOfFloat;
                objectAnimatorOfFloat.setDuration(DeviceHelper.isFeatureWholeAnim() ? this.mViewPager.getContext().getResources().getInteger(android.R.integer.config_shortAnimTime) : 0L);
            }
            this.mActionMenuChangeAnimatorObject.reset(i, z);
            this.mActionMenuChangeAnimator.start();
        }
    }

    void addOnFragmentViewPagerChangeListener(ActionBar.FragmentViewPagerChangeListener fragmentViewPagerChangeListener) {
        if (this.mListeners == null) {
            this.mListeners = new ArrayList<>();
        }
        this.mListeners.add(fragmentViewPagerChangeListener);
    }

    void removeOnFragmentViewPagerChangeListener(ActionBar.FragmentViewPagerChangeListener fragmentViewPagerChangeListener) {
        ArrayList<ActionBar.FragmentViewPagerChangeListener> arrayList = this.mListeners;
        if (arrayList != null) {
            arrayList.remove(fragmentViewPagerChangeListener);
        }
    }

    int getViewPagerOffscreenPageLimit() {
        return this.mViewPager.getOffscreenPageLimit();
    }

    void setViewPagerOffscreenPageLimit(int i) {
        this.mViewPager.setOffscreenPageLimit(i);
    }

    void setViewPagerDecor(View view) {
        View view2 = this.mViewPagerDecor;
        if (view2 != null) {
            this.mViewPager.removeView(view2);
        }
        if (view != null) {
            this.mViewPagerDecor = view;
            OriginalViewPager.LayoutParams layoutParams = new OriginalViewPager.LayoutParams();
            layoutParams.isDecor = true;
            this.mViewPager.addView(this.mViewPagerDecor, -1, layoutParams);
        }
    }

    void setViewPagerDraggable(boolean z) {
        this.mViewPager.setDraggable(z);
        SpringBackLayout springBackLayout = this.mSpringBackLayout;
        if (springBackLayout != null) {
            springBackLayout.setSpringBackEnable(z);
        }
    }

    class ActionMenuChangeAnimatorObject {
        private int mPosition;
        private boolean mShowActionMenu;

        ActionMenuChangeAnimatorObject() {
        }

        void reset(int i, boolean z) {
            this.mPosition = i;
            this.mShowActionMenu = z;
        }

        public void setValue(float f) {
            if (ActionBarViewPagerController.this.mListeners != null) {
                for (ActionBar.FragmentViewPagerChangeListener fragmentViewPagerChangeListener : ActionBarViewPagerController.this.mListeners) {
                    if (fragmentViewPagerChangeListener instanceof ActionBarContainer) {
                        boolean z = this.mShowActionMenu;
                        fragmentViewPagerChangeListener.onPageScrolled(this.mPosition, 1.0f - f, z, !z);
                    }
                }
            }
        }
    }
}
