package miuix.navigator.navigatorinfo;

import android.content.res.Configuration;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.Iterator;
import miuix.navigator.BottomTab;
import miuix.navigator.NavigationItem;
import miuix.navigator.Navigator;
import miuix.navigator.NavigatorImpl;
import miuix.navigator.adapter.CategoryAdapter;
import miuix.navigator.adapter.CategoryImpl;
import miuix.navigator.adapter.LabelAdapter;
import miuix.navigator.adapter.LabelImpl;
import miuix.navigator.adapter.NavigationAdapter;

/* JADX INFO: loaded from: classes3.dex */
public class NavigatorInfoManager {
    private static final String TAG_SELECTED_ID = "selectedPosition";
    private final NavigationAdapter mAdapter;
    private NavigatorInfo mSelectedInfo;
    private final ArrayList<Navigator.Label> mLabels = new ArrayList<>();
    private final ArrayList<Navigator.Category> mCategories = new ArrayList<>();
    private final ArrayList<BottomTab> mTabs = new ArrayList<>();

    public NavigatorInfoManager(NavigatorImpl navigatorImpl) {
        this.mAdapter = new NavigationAdapter(navigatorImpl.getByTag("miuix.navigation"));
    }

    public NavigationAdapter getAdapter() {
        return this.mAdapter;
    }

    public void navigate(NavigatorInfo navigatorInfo, Navigator navigator) {
        if (navigatorInfo != null && navigatorInfo.onNavigate(navigator)) {
            boolean z = !navigatorInfo.equals(this.mSelectedInfo);
            if (z) {
                this.mAdapter.notifyItemChanged(this.mSelectedInfo);
            }
            this.mSelectedInfo = navigatorInfo;
            if (z) {
                this.mAdapter.notifyItemChanged(navigatorInfo);
            }
        }
    }

    public NavigatorInfo getSelectedInfo() {
        return this.mSelectedInfo;
    }

    public Navigator.Label newLabel(int i) {
        return new LabelImpl(i);
    }

    public void addLabel(Navigator.Label label) {
        addLabel(label, -1);
    }

    public void addLabel(Navigator.Label label, int i) {
        this.mLabels.add(label);
        this.mAdapter.addLabel(label, i);
    }

    public void removeLabel(int i) {
        removeLabel(findLabel(i));
    }

    public void removeLabel(Navigator.Label label) {
        if (label == null) {
            return;
        }
        this.mLabels.remove(label);
        this.mAdapter.removeLabel(label);
    }

    public Navigator.Label findLabel(int i) {
        for (Navigator.Label label : this.mLabels) {
            NavigatorInfo navigatorInfo = label.getNavigatorInfo();
            if (navigatorInfo != null && navigatorInfo.isStable() && navigatorInfo.getNavigationId() == i) {
                return label;
            }
        }
        return null;
    }

    public Navigator.Label findLabel(NavigatorInfo navigatorInfo) {
        if (navigatorInfo != null && navigatorInfo.isStable()) {
            for (Navigator.Label label : this.mLabels) {
                if (navigatorInfo.equals(label.getNavigatorInfo())) {
                    return label;
                }
            }
        }
        return null;
    }

    public LabelAdapter getLabelAdapter() {
        return this.mAdapter.getLabelAdapter();
    }

    public void setLabelAdapter(LabelAdapter labelAdapter) {
        this.mAdapter.setLabelAdapter(labelAdapter);
    }

    public Navigator.Category newCategory(int i) {
        return newCategory(i, -1);
    }

    public Navigator.Category newCategory(int i, int i2) {
        return new CategoryImpl(i, i2);
    }

    public void addCategory(Navigator.Category category) {
        addCategory(category, -1);
    }

    public void addCategory(Navigator.Category category, int i) {
        this.mCategories.add(category);
        this.mAdapter.addCategory(category, i);
    }

    public void removeCategory(int i) {
        removeCategory(findCategory(i));
    }

    public void removeCategory(Navigator.Category category) {
        if (category == null) {
            return;
        }
        this.mCategories.remove(category);
        this.mAdapter.removeCategory(category);
    }

    public Navigator.Category findCategory(int i) {
        for (Navigator.Category category : this.mCategories) {
            if (category.getId() == i) {
                return category;
            }
        }
        return null;
    }

    public NavigationItem findCategoryItem(NavigatorInfo navigatorInfo) {
        if (navigatorInfo != null && navigatorInfo.isStable()) {
            Iterator<Navigator.Category> it = this.mCategories.iterator();
            while (it.hasNext()) {
                CategoryAdapter.Item itemFindItemWithInfo = it.next().getAdapter().findItemWithInfo(navigatorInfo);
                if (itemFindItemWithInfo != null) {
                    return itemFindItemWithInfo;
                }
            }
        }
        return null;
    }

    public BottomTab newTab() {
        return new BottomTab();
    }

    public void addTab(BottomTab bottomTab) {
        addTab(bottomTab, -1);
    }

    public void addTab(BottomTab bottomTab, int i) {
        this.mTabs.add(bottomTab);
    }

    public BottomTab findTab(int i) {
        for (BottomTab bottomTab : this.mTabs) {
            NavigatorInfo navigatorInfo = bottomTab.getNavigatorInfo();
            if (navigatorInfo != null && navigatorInfo.isStable() && navigatorInfo.getNavigationId() == i) {
                return bottomTab;
            }
        }
        return null;
    }

    public BottomTab findTab(NavigatorInfo navigatorInfo) {
        if (navigatorInfo != null && navigatorInfo.isStable()) {
            for (BottomTab bottomTab : this.mTabs) {
                if (navigatorInfo.equals(bottomTab.getNavigatorInfo())) {
                    return bottomTab;
                }
            }
        }
        return null;
    }

    public void onConfigurationChanged(Configuration configuration) {
        this.mAdapter.onConfigurationChanged(configuration);
    }

    public void onSaveState(Bundle bundle) {
        NavigatorInfo navigatorInfo = this.mSelectedInfo;
        if (navigatorInfo != null) {
            bundle.putInt(TAG_SELECTED_ID, navigatorInfo.getNavigationId());
        }
    }

    public void onRestoreState(Bundle bundle) {
        if (bundle.containsKey(TAG_SELECTED_ID)) {
            this.mSelectedInfo = new NavigatorInfo(bundle.getInt(TAG_SELECTED_ID)) { // from class: miuix.navigator.navigatorinfo.NavigatorInfoManager.1
                @Override // miuix.navigator.navigatorinfo.NavigatorInfo
                public boolean onNavigate(Navigator navigator) {
                    return true;
                }
            };
        }
    }
}
