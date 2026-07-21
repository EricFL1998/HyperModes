package miuix.navigator;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/* JADX INFO: loaded from: classes3.dex */
class LogUtils {
    private static final FragmentManager.FragmentLifecycleCallbacks CALLBACKS = new FragmentManager.FragmentLifecycleCallbacks() { // from class: miuix.navigator.LogUtils.1
        @Override // androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks
        public void onFragmentAttached(FragmentManager fragmentManager, Fragment fragment, Context context) {
            Log.d(fragment.getClass().getSimpleName(), "ON_ATTACH");
        }

        @Override // androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks
        public void onFragmentCreated(FragmentManager fragmentManager, Fragment fragment, Bundle bundle) {
            Log.d(fragment.getClass().getSimpleName(), "ON_CREATE");
        }

        @Override // androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks
        public void onFragmentViewCreated(FragmentManager fragmentManager, Fragment fragment, View view, Bundle bundle) {
            Log.d(fragment.getClass().getSimpleName(), "ON_VIEW_CREATED");
        }

        @Override // androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks
        public void onFragmentStarted(FragmentManager fragmentManager, Fragment fragment) {
            Log.d(fragment.getClass().getSimpleName(), "ON_START");
        }

        @Override // androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks
        public void onFragmentResumed(FragmentManager fragmentManager, Fragment fragment) {
            Log.d(fragment.getClass().getSimpleName(), "ON_RESUME");
        }

        @Override // androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks
        public void onFragmentPaused(FragmentManager fragmentManager, Fragment fragment) {
            Log.d(fragment.getClass().getSimpleName(), "ON_PAUSE");
        }

        @Override // androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks
        public void onFragmentStopped(FragmentManager fragmentManager, Fragment fragment) {
            Log.d(fragment.getClass().getSimpleName(), "ON_STOP");
        }

        @Override // androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks
        public void onFragmentSaveInstanceState(FragmentManager fragmentManager, Fragment fragment, Bundle bundle) {
            Log.d(fragment.getClass().getSimpleName(), "ON_SAVE_INSTANCE_STATE");
        }

        @Override // androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks
        public void onFragmentViewDestroyed(FragmentManager fragmentManager, Fragment fragment) {
            Log.d(fragment.getClass().getSimpleName(), "ON_DESTROY_VIEW");
        }

        @Override // androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks
        public void onFragmentDestroyed(FragmentManager fragmentManager, Fragment fragment) {
            Log.d(fragment.getClass().getSimpleName(), "ON_DESTROY");
        }

        @Override // androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks
        public void onFragmentDetached(FragmentManager fragmentManager, Fragment fragment) {
            Log.d(fragment.getClass().getSimpleName(), "ON_DETACH");
        }
    };

    private LogUtils() {
    }

    public static void addLifecycleLog(FragmentManager fragmentManager) {
        fragmentManager.registerFragmentLifecycleCallbacks(CALLBACKS, false);
    }
}
