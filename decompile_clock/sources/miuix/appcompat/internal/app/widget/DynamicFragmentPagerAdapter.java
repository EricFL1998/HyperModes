package miuix.appcompat.internal.app.widget;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import java.util.ArrayList;

/* JADX INFO: compiled from: ActionBarViewPagerController.java */
/* JADX INFO: loaded from: classes2.dex */
class DynamicFragmentPagerAdapter extends PagerAdapter {
    private Context mContext;
    private FragmentManager mFragmentManager;
    private ArrayList<FragmentInfo> mFragmentInfos = new ArrayList<>();
    private FragmentTransaction mCurTransaction = null;
    private Fragment mCurrentPrimaryItem = null;

    @Override // androidx.viewpager.widget.PagerAdapter
    public void startUpdate(ViewGroup viewGroup) {
    }

    /* JADX INFO: compiled from: ActionBarViewPagerController.java */
    class FragmentInfo {
        Bundle args;
        Class<? extends Fragment> clazz;
        Fragment fragment = null;
        boolean hasActionMenu;
        ActionBar.Tab tab;
        String tag;

        FragmentInfo(String str, Class<? extends Fragment> cls, Bundle bundle, ActionBar.Tab tab, boolean z) {
            this.tag = str;
            this.clazz = cls;
            this.args = bundle;
            this.tab = tab;
            this.hasActionMenu = z;
        }
    }

    public DynamicFragmentPagerAdapter(Context context, FragmentManager fragmentManager) {
        this.mContext = context;
        this.mFragmentManager = fragmentManager;
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
        if (this.mCurTransaction == null) {
            this.mCurTransaction = this.mFragmentManager.beginTransaction();
        }
        this.mCurTransaction.detach((Fragment) obj);
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public void setPrimaryItem(ViewGroup viewGroup, int i, Object obj) {
        Fragment fragment = (Fragment) obj;
        Fragment fragment2 = this.mCurrentPrimaryItem;
        if (fragment != fragment2) {
            if (fragment2 != null) {
                fragment2.setMenuVisibility(false);
                this.mCurrentPrimaryItem.setUserVisibleHint(false);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }
            this.mCurrentPrimaryItem = fragment;
        }
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public void finishUpdate(ViewGroup viewGroup) {
        FragmentTransaction fragmentTransaction = this.mCurTransaction;
        if (fragmentTransaction != null) {
            fragmentTransaction.commitAllowingStateLoss();
            this.mCurTransaction = null;
            this.mFragmentManager.executePendingTransactions();
        }
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public boolean isViewFromObject(View view, Object obj) {
        return ((Fragment) obj).getView() == view;
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public Object instantiateItem(ViewGroup viewGroup, int i) {
        if (this.mCurTransaction == null) {
            this.mCurTransaction = this.mFragmentManager.beginTransaction();
        }
        Fragment fragment = getFragment(i, true, false);
        if (fragment.getFragmentManager() != null) {
            this.mCurTransaction.attach(fragment);
        } else {
            this.mCurTransaction.add(viewGroup.getId(), fragment, this.mFragmentInfos.get(i).tag);
        }
        if (fragment != this.mCurrentPrimaryItem) {
            fragment.setMenuVisibility(false);
            fragment.setUserVisibleHint(false);
        }
        return fragment;
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public int getCount() {
        return this.mFragmentInfos.size();
    }

    public boolean hasActionMenu(int i) {
        if (i < 0 || i >= this.mFragmentInfos.size()) {
            return false;
        }
        return this.mFragmentInfos.get(i).hasActionMenu;
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public int getItemPosition(Object obj) {
        int size = this.mFragmentInfos.size();
        for (int i = 0; i < size; i++) {
            if (obj == this.mFragmentInfos.get(i).fragment) {
                return i;
            }
        }
        return -2;
    }

    ActionBar.Tab getTabAt(int i) {
        return this.mFragmentInfos.get(i).tab;
    }

    Fragment getFragment(int i, boolean z) {
        return getFragment(i, z, true);
    }

    Fragment getFragment(int i, boolean z, boolean z2) {
        if (this.mFragmentInfos.isEmpty() || i < 0 || i > this.mFragmentInfos.size() - 1) {
            return null;
        }
        ArrayList<FragmentInfo> arrayList = this.mFragmentInfos;
        if (z2) {
            i = toIndexForRTL(i);
        }
        FragmentInfo fragmentInfo = arrayList.get(i);
        if (fragmentInfo.fragment == null) {
            fragmentInfo.fragment = this.mFragmentManager.findFragmentByTag(fragmentInfo.tag);
            if (fragmentInfo.fragment == null && z && fragmentInfo.clazz != null) {
                fragmentInfo.fragment = Fragment.instantiate(this.mContext, fragmentInfo.clazz.getName(), fragmentInfo.args);
                fragmentInfo.clazz = null;
                fragmentInfo.args = null;
            }
        }
        return fragmentInfo.fragment;
    }

    int addFragment(String str, Class<? extends Fragment> cls, Bundle bundle, ActionBar.Tab tab, boolean z) {
        if (isRTL()) {
            this.mFragmentInfos.add(0, new FragmentInfo(str, cls, bundle, tab, z));
        } else {
            this.mFragmentInfos.add(new FragmentInfo(str, cls, bundle, tab, z));
        }
        notifyDataSetChanged();
        return this.mFragmentInfos.size() - 1;
    }

    int addFragment(String str, int i, Class<? extends Fragment> cls, Bundle bundle, ActionBar.Tab tab, boolean z) {
        FragmentInfo fragmentInfo = new FragmentInfo(str, cls, bundle, tab, z);
        if (isRTL()) {
            if (i >= this.mFragmentInfos.size()) {
                this.mFragmentInfos.add(0, fragmentInfo);
            } else {
                this.mFragmentInfos.add(toIndexForRTL(i) + 1, fragmentInfo);
            }
        } else {
            this.mFragmentInfos.add(i, fragmentInfo);
        }
        notifyDataSetChanged();
        return i;
    }

    void replaceFragmentAt(String str, int i, Class<? extends Fragment> cls, Bundle bundle, ActionBar.Tab tab, boolean z) {
        removeFragmentFromManager(getFragment(i, false));
        this.mFragmentInfos.remove(toIndexForRTL(i));
        FragmentInfo fragmentInfo = new FragmentInfo(str, cls, bundle, tab, z);
        if (isRTL()) {
            if (i >= this.mFragmentInfos.size()) {
                this.mFragmentInfos.add(0, fragmentInfo);
            } else {
                this.mFragmentInfos.add(toIndexForRTL(i) + 1, fragmentInfo);
            }
        } else {
            this.mFragmentInfos.add(i, fragmentInfo);
        }
        notifyDataSetChanged();
    }

    void removeFragmentAt(int i) {
        removeFragmentFromManager(getFragment(i, false));
        this.mFragmentInfos.remove(toIndexForRTL(i));
        notifyDataSetChanged();
    }

    int findPositionByTag(String str) {
        int size = this.mFragmentInfos.size();
        for (int i = 0; i < size; i++) {
            if (this.mFragmentInfos.get(i).tag.equals(str)) {
                return toIndexForRTL(i);
            }
        }
        return -1;
    }

    int removeFragment(ActionBar.Tab tab) {
        int size = this.mFragmentInfos.size();
        for (int i = 0; i < size; i++) {
            FragmentInfo fragmentInfo = this.mFragmentInfos.get(i);
            if (fragmentInfo.tab == tab) {
                removeFragmentFromManager(fragmentInfo.fragment);
                this.mFragmentInfos.remove(i);
                if (this.mCurrentPrimaryItem == fragmentInfo.fragment) {
                    this.mCurrentPrimaryItem = null;
                }
                notifyDataSetChanged();
                return toIndexForRTL(i);
            }
        }
        return -1;
    }

    int removeFragment(Fragment fragment) {
        int size = this.mFragmentInfos.size();
        for (int i = 0; i < size; i++) {
            if (getFragment(i, false, false) == fragment) {
                removeFragmentFromManager(fragment);
                this.mFragmentInfos.remove(i);
                if (this.mCurrentPrimaryItem == fragment) {
                    this.mCurrentPrimaryItem = null;
                }
                notifyDataSetChanged();
                return toIndexForRTL(i);
            }
        }
        return -1;
    }

    void removeAllFragment() {
        removeAllFragmentFromManager();
        this.mFragmentInfos.clear();
        this.mCurrentPrimaryItem = null;
        notifyDataSetChanged();
    }

    void setFragmentActionMenuAt(int i, boolean z) {
        FragmentInfo fragmentInfo = this.mFragmentInfos.get(toIndexForRTL(i));
        if (fragmentInfo.hasActionMenu != z) {
            fragmentInfo.hasActionMenu = z;
            notifyDataSetChanged();
        }
    }

    private void removeAllFragmentFromManager() {
        FragmentTransaction fragmentTransactionBeginTransaction = this.mFragmentManager.beginTransaction();
        int size = this.mFragmentInfos.size();
        for (int i = 0; i < size; i++) {
            fragmentTransactionBeginTransaction.remove(getFragment(i, false));
        }
        fragmentTransactionBeginTransaction.commitAllowingStateLoss();
        this.mFragmentManager.executePendingTransactions();
    }

    private void removeFragmentFromManager(Fragment fragment) {
        FragmentManager fragmentManager;
        if (fragment == null || (fragmentManager = fragment.getFragmentManager()) == null) {
            return;
        }
        FragmentTransaction fragmentTransactionBeginTransaction = fragmentManager.beginTransaction();
        fragmentTransactionBeginTransaction.remove(fragment);
        fragmentTransactionBeginTransaction.commitAllowingStateLoss();
        fragmentManager.executePendingTransactions();
    }

    int toIndexForRTL(int i) {
        if (!isRTL()) {
            return i;
        }
        int size = this.mFragmentInfos.size() - 1;
        if (size > i) {
            return size - i;
        }
        return 0;
    }

    boolean isRTL() {
        return this.mContext.getResources().getConfiguration().getLayoutDirection() == 1;
    }
}
