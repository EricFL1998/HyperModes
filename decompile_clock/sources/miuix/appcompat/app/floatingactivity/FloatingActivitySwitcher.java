package miuix.appcompat.app.floatingactivity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import miuix.appcompat.app.AppCompatActivity;
import miuix.appcompat.app.floatingactivity.helper.FloatingHelperFactory;

/* JADX INFO: loaded from: classes2.dex */
public class FloatingActivitySwitcher {
    private static final String SAVED_FLOATING_INFO_KEY = "miuix_floating_activity_info_key";
    private static final String TAG = "FloatingActivity";
    private static final HashMap<String, ActivitySpec> mActivityInfoStack = new HashMap<>();
    private static FloatingActivitySwitcher sInstance;
    private boolean mEnableDragToDismiss;
    private WeakReference<View> mLastActivityPanel;
    private final SparseArray<ArrayList<AppCompatActivity>> mActivityCache = new SparseArray<>();
    private final ArrayList<AppCompatActivity> mWillDestroyList = new ArrayList<>();

    static FloatingActivitySwitcher getInstance() {
        return sInstance;
    }

    @Deprecated
    public static void install(AppCompatActivity appCompatActivity) {
        install(appCompatActivity, true, null);
    }

    public static void install(AppCompatActivity appCompatActivity, Bundle bundle) {
        install(appCompatActivity, true, bundle);
    }

    private static void install(AppCompatActivity appCompatActivity, boolean z, Bundle bundle) {
        if (sInstance == null) {
            FloatingActivitySwitcher floatingActivitySwitcher = new FloatingActivitySwitcher();
            sInstance = floatingActivitySwitcher;
            floatingActivitySwitcher.mEnableDragToDismiss = z;
        }
        sInstance.init(appCompatActivity, bundle);
    }

    private FloatingActivitySwitcher() {
    }

    public static void onSaveInstanceState(AppCompatActivity appCompatActivity, Bundle bundle) {
        if (getInstance() == null || bundle == null) {
            return;
        }
        bundle.putParcelable(SAVED_FLOATING_INFO_KEY, getOrCreateActivitySpec(appCompatActivity));
    }

    private void init(AppCompatActivity appCompatActivity, Bundle bundle) {
        if (FloatingHelperFactory.getFloatingHelperType(appCompatActivity) == 0) {
            return;
        }
        stashActivity(appCompatActivity, bundle);
        appCompatActivity.getLifecycle().addObserver(new SingleAppFloatingLifecycleObserver(appCompatActivity));
        appCompatActivity.setEnableSwipToDismiss(this.mEnableDragToDismiss);
        appCompatActivity.setOnFloatingCallback(new DefineOnFloatingActivityCallback(appCompatActivity));
    }

    public void closeAllFloatingPage(String str) {
        ArrayList<AppCompatActivity> arrayList;
        ActivitySpec activitySpec = mActivityInfoStack.get(str);
        if (activitySpec == null || (arrayList = this.mActivityCache.get(activitySpec.taskId)) == null) {
            return;
        }
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            AppCompatActivity appCompatActivity = arrayList.get(size);
            if (!appCompatActivity.getActivityIdentity().equals(str)) {
                appCompatActivity.hideFloatingBrightPanel();
                this.mWillDestroyList.add(appCompatActivity);
                arrayList.remove(appCompatActivity);
                mActivityInfoStack.remove(appCompatActivity.getActivityIdentity());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeTopActivity(String str) {
        ArrayList<AppCompatActivity> arrayList;
        ActivitySpec activitySpec = mActivityInfoStack.get(str);
        if (activitySpec == null || (arrayList = this.mActivityCache.get(activitySpec.taskId)) == null || arrayList.size() <= 0) {
            return;
        }
        arrayList.get(arrayList.size() - 1).realFinish();
    }

    SparseArray<ArrayList<AppCompatActivity>> getActivityCache() {
        return this.mActivityCache;
    }

    ArrayList<AppCompatActivity> getActivityList(int i) {
        return this.mActivityCache.get(i);
    }

    int getActivityIndex(AppCompatActivity appCompatActivity) {
        ArrayList<AppCompatActivity> arrayList;
        if (appCompatActivity == null || (arrayList = this.mActivityCache.get(appCompatActivity.getTaskId())) == null) {
            return -1;
        }
        return arrayList.indexOf(appCompatActivity);
    }

    AppCompatActivity getActivity(String str, int i) {
        ArrayList<AppCompatActivity> arrayList = this.mActivityCache.get(i);
        if (arrayList == null) {
            return null;
        }
        for (AppCompatActivity appCompatActivity : arrayList) {
            if (appCompatActivity.getActivityIdentity().equals(str)) {
                return appCompatActivity;
            }
        }
        return null;
    }

    AppCompatActivity getActivity(String str) {
        ActivitySpec activitySpec = mActivityInfoStack.get(str);
        if (activitySpec != null) {
            return getActivity(str, activitySpec.taskId);
        }
        return null;
    }

    AppCompatActivity getPreviousActivity(AppCompatActivity appCompatActivity) {
        if (appCompatActivity == null) {
            return null;
        }
        ArrayList<AppCompatActivity> arrayList = this.mActivityCache.get(appCompatActivity.getTaskId());
        int iIndexOf = arrayList != null ? arrayList.indexOf(appCompatActivity) : -1;
        if (iIndexOf <= 0) {
            return null;
        }
        int i = iIndexOf - 1;
        for (int i2 = i; i2 >= 0; i2--) {
            AppCompatActivity appCompatActivity2 = arrayList.get(i);
            if (!appCompatActivity2.isFinishing()) {
                return appCompatActivity2;
            }
        }
        return null;
    }

    private boolean isActivityStashed(AppCompatActivity appCompatActivity) {
        return mActivityInfoStack.get(appCompatActivity.getActivityIdentity()) != null;
    }

    public boolean isActivityOpenEnterAnimExecuted(AppCompatActivity appCompatActivity) {
        ActivitySpec activitySpec = mActivityInfoStack.get(appCompatActivity.getActivityIdentity());
        return activitySpec != null && activitySpec.isOpenEnterAnimExecuted;
    }

    public void markActivityOpenEnterAnimExecutedInternal(AppCompatActivity appCompatActivity) {
        ActivitySpec activitySpec = mActivityInfoStack.get(appCompatActivity.getActivityIdentity());
        if (activitySpec != null) {
            activitySpec.isOpenEnterAnimExecuted = true;
        }
    }

    private void hideTopBgs(AppCompatActivity appCompatActivity) {
        ArrayList<AppCompatActivity> arrayList = this.mActivityCache.get(appCompatActivity.getTaskId());
        if (arrayList == null) {
            return;
        }
        int i = 0;
        while (true) {
            if (i >= arrayList.size()) {
                i = -1;
                break;
            } else if (!arrayList.get(i).isFinishing()) {
                break;
            } else {
                i++;
            }
        }
        if (i == -1) {
            return;
        }
        while (true) {
            i++;
            if (i >= arrayList.size()) {
                return;
            } else {
                arrayList.get(i).hideFloatingDimBackground();
            }
        }
    }

    private void stashActivity(AppCompatActivity appCompatActivity, Bundle bundle) {
        if (!isActivityStashed(appCompatActivity)) {
            int taskId = appCompatActivity.getTaskId();
            ArrayList<AppCompatActivity> arrayList = this.mActivityCache.get(taskId);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                this.mActivityCache.put(taskId, arrayList);
            }
            if (bundle != null) {
                ActivitySpec activitySpecRecoverFromSavedInstanceState = recoverFromSavedInstanceState(appCompatActivity, bundle);
                activitySpecRecoverFromSavedInstanceState.activityClassName = appCompatActivity.getClass().getSimpleName();
                activitySpecRecoverFromSavedInstanceState.identity = appCompatActivity.getActivityIdentity();
                insertActivityByIndex(arrayList, activitySpecRecoverFromSavedInstanceState.index, appCompatActivity);
                mActivityInfoStack.put(appCompatActivity.getActivityIdentity(), activitySpecRecoverFromSavedInstanceState);
            } else {
                arrayList.add(appCompatActivity);
                FloatingActivitySwitcher floatingActivitySwitcher = getInstance();
                mActivityInfoStack.put(appCompatActivity.getActivityIdentity(), new ActivitySpec(appCompatActivity.getClass().getSimpleName(), floatingActivitySwitcher == null ? 0 : floatingActivitySwitcher.getActivityIndex(appCompatActivity), appCompatActivity.getActivityIdentity(), appCompatActivity.getTaskId(), false));
            }
        }
        ActivitySpec activitySpec = mActivityInfoStack.get(appCompatActivity.getActivityIdentity());
        if (activitySpec != null) {
            FloatingAnimHelper.markedPageIndex(appCompatActivity, activitySpec.index);
        }
        execEnterNormalRom(appCompatActivity);
        hideTopBgs(appCompatActivity);
    }

    private void insertActivityByIndex(ArrayList<AppCompatActivity> arrayList, int i, AppCompatActivity appCompatActivity) {
        int i2;
        ActivitySpec activitySpec;
        int size = arrayList.size();
        do {
            size--;
            i2 = 0;
            if (size >= 0) {
                activitySpec = mActivityInfoStack.get(arrayList.get(size).getActivityIdentity());
            }
            arrayList.add(i2, appCompatActivity);
        } while (i <= (activitySpec != null ? activitySpec.index : 0));
        i2 = size + 1;
        arrayList.add(i2, appCompatActivity);
    }

    private ActivitySpec recoverFromSavedInstanceState(AppCompatActivity appCompatActivity, Bundle bundle) {
        ActivitySpec activitySpec = (ActivitySpec) bundle.getParcelable(SAVED_FLOATING_INFO_KEY);
        if (activitySpec != null) {
            return activitySpec;
        }
        Log.w(TAG, "FloatingActivitySwitcher restore a full ActivitySpec instance with savedInstanceState fail, Check if you have replaced the theme in the float window !");
        return new ActivitySpec(appCompatActivity.getClass().getSimpleName(), 0, appCompatActivity.getActivityIdentity(), appCompatActivity.getTaskId(), false);
    }

    private void execEnterNormalRom(AppCompatActivity appCompatActivity) {
        if (FloatingAnimHelper.isSupportTransWithClipAnim()) {
            return;
        }
        if (appCompatActivity.isInFloatingWindowMode()) {
            FloatingAnimHelper.clearFloatingWindowAnim(appCompatActivity);
        } else {
            FloatingAnimHelper.execFloatingWindowEnterAnimRomNormal(appCompatActivity);
        }
    }

    public void remove(String str, int i) {
        ArrayList<AppCompatActivity> arrayList = this.mActivityCache.get(i);
        if (arrayList != null) {
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                AppCompatActivity appCompatActivity = arrayList.get(size);
                if (appCompatActivity.getActivityIdentity().equals(str)) {
                    arrayList.remove(size);
                }
                this.mWillDestroyList.remove(appCompatActivity);
            }
            if (arrayList.isEmpty()) {
                this.mActivityCache.remove(i);
            }
        }
        mActivityInfoStack.remove(str);
        if (this.mActivityCache.size() == 0) {
            clear();
        }
    }

    View getLastActivityPanel() {
        WeakReference<View> weakReference = this.mLastActivityPanel;
        if (weakReference == null) {
            return null;
        }
        return weakReference.get();
    }

    void setLastActivityPanel(View view) {
        this.mLastActivityPanel = new WeakReference<>(view);
    }

    public void clear() {
        this.mActivityCache.clear();
        mActivityInfoStack.clear();
        this.mLastActivityPanel = null;
        sInstance = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideBehindPages(String str) {
        ActivitySpec activitySpec = mActivityInfoStack.get(str);
        if (activitySpec != null) {
            ArrayList<AppCompatActivity> arrayList = this.mActivityCache.get(activitySpec.taskId);
            int i = -1;
            if (arrayList != null) {
                for (int i2 = 0; i2 < arrayList.size(); i2++) {
                    if (arrayList.get(i2).getActivityIdentity().equals(str)) {
                        i = i2;
                    }
                }
            }
            for (int i3 = i - 1; i3 >= 0; i3--) {
                arrayList.get(i3).hideFloatingBrightPanel();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showBehindPages(String str) {
        ActivitySpec activitySpec = mActivityInfoStack.get(str);
        if (activitySpec != null) {
            ArrayList<AppCompatActivity> arrayList = this.mActivityCache.get(activitySpec.taskId);
            int i = -1;
            if (arrayList != null) {
                for (int i2 = 0; i2 < arrayList.size(); i2++) {
                    if (arrayList.get(i2).getActivityIdentity().equals(str)) {
                        i = i2;
                    }
                }
            }
            for (int i3 = i - 1; i3 >= 0; i3--) {
                arrayList.get(i3).showFloatingBrightPanel();
            }
        }
    }

    class DefineOnFloatingActivityCallback implements OnFloatingCallback {
        protected String mActivityIdentity;
        protected int mActivityTaskId;

        protected boolean checkActivity(AppCompatActivity appCompatActivity) {
            return appCompatActivity != null;
        }

        public DefineOnFloatingActivityCallback(AppCompatActivity appCompatActivity) {
            this.mActivityIdentity = appCompatActivity.getActivityIdentity();
            this.mActivityTaskId = appCompatActivity.getTaskId();
        }

        protected String getActivityIdentity() {
            return this.mActivityIdentity;
        }

        protected int getActivityTaskId() {
            return this.mActivityTaskId;
        }

        @Override // miuix.appcompat.app.floatingactivity.OnFloatingActivityCallback
        public boolean onFinish(int i) {
            if (checkFinishEnable(i)) {
                return false;
            }
            if (shouldTopFloatingClose(i)) {
                FloatingActivitySwitcher.this.closeTopActivity(getActivityIdentity());
            } else {
                FloatingActivitySwitcher.this.closeAllFloatingPage(getActivityIdentity());
            }
            return false;
        }

        private boolean checkFinishEnable(int i) {
            return !FloatingActivitySwitcher.this.mEnableDragToDismiss && (i == 1 || i == 2);
        }

        private boolean shouldTopFloatingClose(int i) {
            ArrayList arrayList = (ArrayList) FloatingActivitySwitcher.this.mActivityCache.get(getActivityTaskId());
            return (i == 4 || i == 3) && (arrayList != null && arrayList.size() > 1);
        }

        @Override // miuix.appcompat.app.floatingactivity.OnFloatingCallback
        public void onDragStart() {
            FloatingActivitySwitcher.this.hideBehindPages(getActivityIdentity());
        }

        @Override // miuix.appcompat.app.floatingactivity.OnFloatingCallback
        public void onDragEnd() {
            FloatingActivitySwitcher.this.showBehindPages(getActivityIdentity());
        }

        @Override // miuix.appcompat.app.floatingactivity.OnFloatingCallback
        public void onHideBehindPage() {
            FloatingActivitySwitcher.this.hideBehindPages(getActivityIdentity());
        }

        @Override // miuix.appcompat.app.floatingactivity.OnFloatingCallback
        public int getPageCount() {
            ArrayList arrayList = (ArrayList) FloatingActivitySwitcher.this.mActivityCache.get(getActivityTaskId());
            if (arrayList != null) {
                return arrayList.size();
            }
            return 0;
        }

        @Override // miuix.appcompat.app.floatingactivity.OnFloatingCallback
        public void closeAllPage() {
            Iterator it = FloatingActivitySwitcher.this.mWillDestroyList.iterator();
            while (it.hasNext()) {
                ((AppCompatActivity) it.next()).realFinish();
            }
            FloatingActivitySwitcher.this.mWillDestroyList.clear();
        }

        @Override // miuix.appcompat.app.floatingactivity.OnFloatingCallback
        public boolean isFirstPageEnterAnimExecuteEnable() {
            ArrayList arrayList;
            ActivitySpec activitySpec = (ActivitySpec) FloatingActivitySwitcher.mActivityInfoStack.get(getActivityIdentity());
            if (activitySpec == null || (arrayList = (ArrayList) FloatingActivitySwitcher.this.mActivityCache.get(activitySpec.taskId)) == null) {
                return true;
            }
            if (arrayList.size() > 1) {
                Iterator it = arrayList.iterator();
                int i = 0;
                while (it.hasNext()) {
                    if (!((AppCompatActivity) it.next()).isFinishing()) {
                        i++;
                    }
                    if (i > 1) {
                        return false;
                    }
                }
            }
            AppCompatActivity appCompatActivity = arrayList.size() == 0 ? null : (AppCompatActivity) arrayList.get(0);
            if (appCompatActivity == null || appCompatActivity.isFinishing() || ((ActivitySpec) FloatingActivitySwitcher.mActivityInfoStack.get(appCompatActivity.getActivityIdentity())) == null) {
                return true;
            }
            return !activitySpec.isOpenEnterAnimExecuted;
        }

        @Override // miuix.appcompat.app.floatingactivity.OnFloatingCallback
        public boolean isFirstPageExitAnimExecuteEnable() {
            ArrayList arrayList;
            ActivitySpec activitySpec = (ActivitySpec) FloatingActivitySwitcher.mActivityInfoStack.get(getActivityIdentity());
            if (activitySpec == null || (arrayList = (ArrayList) FloatingActivitySwitcher.this.mActivityCache.get(activitySpec.taskId)) == null) {
                return true;
            }
            Iterator it = arrayList.iterator();
            int i = 0;
            while (it.hasNext()) {
                if (!((AppCompatActivity) it.next()).isFinishing()) {
                    i++;
                }
            }
            return i == 1;
        }

        @Override // miuix.appcompat.app.floatingactivity.OnFloatingCallback
        public void markActivityOpenEnterAnimExecuted(AppCompatActivity appCompatActivity) {
            FloatingActivitySwitcher.this.markActivityOpenEnterAnimExecutedInternal(appCompatActivity);
        }

        @Override // miuix.appcompat.app.floatingactivity.OnFloatingCallback
        public void getSnapShotAndSetPanel(AppCompatActivity appCompatActivity) {
            FloatingActivitySwitcher floatingActivitySwitcher;
            AppCompatActivity previousActivity;
            View viewGenerateSnapShotView;
            if (appCompatActivity == null || (floatingActivitySwitcher = FloatingActivitySwitcher.getInstance()) == null || (previousActivity = floatingActivitySwitcher.getPreviousActivity(appCompatActivity)) == null) {
                return;
            }
            int i = 0;
            do {
                viewGenerateSnapShotView = SnapShotViewHelper.generateSnapShotView(previousActivity, appCompatActivity);
                i++;
                if (viewGenerateSnapShotView != null) {
                    break;
                }
            } while (i < 3);
            floatingActivitySwitcher.setLastActivityPanel(viewGenerateSnapShotView);
            addLastActivityPanel(previousActivity);
        }

        @Override // miuix.appcompat.app.floatingactivity.OnFloatingCallback
        public boolean isFirstPage() {
            ArrayList arrayList;
            ActivitySpec activitySpec = (ActivitySpec) FloatingActivitySwitcher.mActivityInfoStack.get(getActivityIdentity());
            if (activitySpec != null && (arrayList = (ArrayList) FloatingActivitySwitcher.this.mActivityCache.get(activitySpec.taskId)) != null) {
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    AppCompatActivity appCompatActivity = (AppCompatActivity) arrayList.get(i);
                    if (!appCompatActivity.isFinishing()) {
                        return appCompatActivity.getActivityIdentity().equals(getActivityIdentity());
                    }
                }
            }
            return false;
        }

        private void addLastActivityPanel(AppCompatActivity appCompatActivity) {
            View lastActivityPanel;
            ViewGroup viewGroup;
            FloatingActivitySwitcher floatingActivitySwitcher = FloatingActivitySwitcher.getInstance();
            if (floatingActivitySwitcher == null || (lastActivityPanel = floatingActivitySwitcher.getLastActivityPanel()) == null || (viewGroup = (ViewGroup) appCompatActivity.getFloatingBrightPanel().getParent()) == null) {
                return;
            }
            viewGroup.getOverlay().clear();
            viewGroup.getOverlay().add(lastActivityPanel);
        }
    }

    private static ActivitySpec getOrCreateActivitySpec(AppCompatActivity appCompatActivity) {
        ActivitySpec activitySpec = mActivityInfoStack.get(appCompatActivity.getActivityIdentity());
        FloatingActivitySwitcher floatingActivitySwitcher = getInstance();
        if (activitySpec == null) {
            activitySpec = new ActivitySpec(appCompatActivity.getClass().getSimpleName(), floatingActivitySwitcher == null ? 0 : floatingActivitySwitcher.getActivityIndex(appCompatActivity), appCompatActivity.getActivityIdentity(), appCompatActivity.getTaskId(), false);
        }
        return activitySpec;
    }

    private static class ActivitySpec implements Parcelable {
        public static final Parcelable.Creator<ActivitySpec> CREATOR = new Parcelable.Creator<ActivitySpec>() { // from class: miuix.appcompat.app.floatingactivity.FloatingActivitySwitcher.ActivitySpec.1
            @Override // android.os.Parcelable.Creator
            public ActivitySpec createFromParcel(Parcel parcel) {
                return new ActivitySpec(parcel);
            }

            @Override // android.os.Parcelable.Creator
            public ActivitySpec[] newArray(int i) {
                return new ActivitySpec[i];
            }
        };
        private String activityClassName;
        private String identity;
        private int index;
        private boolean isOpenEnterAnimExecuted;
        private boolean isPreDestroy = false;
        private int taskId;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public ActivitySpec(String str, int i, String str2, int i2, boolean z) {
            this.activityClassName = str;
            this.index = i;
            this.identity = str2;
            this.taskId = i2;
            this.isOpenEnterAnimExecuted = z;
        }

        protected ActivitySpec(Parcel parcel) {
            this.activityClassName = "";
            this.index = 0;
            this.taskId = 0;
            this.isOpenEnterAnimExecuted = false;
            this.activityClassName = parcel.readString();
            this.index = parcel.readInt();
            this.identity = parcel.readString();
            this.taskId = parcel.readInt();
            this.isOpenEnterAnimExecuted = parcel.readByte() != 0;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(this.activityClassName);
            parcel.writeInt(this.index);
            parcel.writeString(this.identity);
            parcel.writeInt(this.taskId);
            parcel.writeByte(this.isOpenEnterAnimExecuted ? (byte) 1 : (byte) 0);
        }

        public String toString() {
            return "{ activityClassName : " + this.activityClassName + "; index : " + this.index + "; identity : " + this.identity + "; taskId : " + this.taskId + "; isOpenEnterAnimExecuted : " + this.isOpenEnterAnimExecuted + "; }";
        }

        public boolean isPreDestroy() {
            return this.isPreDestroy;
        }

        public void setIsDestroy(boolean z) {
            this.isPreDestroy = z;
        }
    }
}
