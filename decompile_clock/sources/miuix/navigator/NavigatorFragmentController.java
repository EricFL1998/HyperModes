package miuix.navigator;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import androidx.core.app.MultiWindowModeChangedInfo;
import androidx.core.app.OnMultiWindowModeChangedProvider;
import androidx.core.app.OnPictureInPictureModeChangedProvider;
import androidx.core.app.PictureInPictureModeChangedInfo;
import androidx.core.content.OnConfigurationChangedProvider;
import androidx.core.content.OnTrimMemoryProvider;
import androidx.core.util.Consumer;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentController;
import androidx.fragment.app.FragmentHostCallback;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentOnAttachListener;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.savedstate.SavedStateRegistry;
import androidx.savedstate.SavedStateRegistryController;
import androidx.savedstate.SavedStateRegistryOwner;
import miuix.navigator.app.NavigatorResponsiveProvider;
import miuix.responsive.map.ResponsiveState;
import miuix.responsive.map.ScreenSpec;

/* JADX INFO: loaded from: classes3.dex */
public class NavigatorFragmentController implements LifecycleOwner, NavigatorResponsiveProvider {
    static final int ACTIVITY_CREATED = 2;
    static final int ATTACHED = 0;
    static final int CREATED = 1;
    static final int RESUMED = 4;
    static final int STARTED = 3;
    private FragmentController mController;
    private HostCallbacks mHost;
    private LifecycleRegistry mLifecycle;
    private final SubNavigator mNavigator;
    private OnAttachListener mOnAttachListener;
    private int mRealState;
    private int mHostState = 0;
    private int mFragmentState = 4;

    public interface OnAttachListener {
        void onAttach(NavigatorFragmentController navigatorFragmentController);
    }

    NavigatorFragmentController(SubNavigator subNavigator) {
        this.mNavigator = subNavigator;
        initController(false);
    }

    SubNavigator getNavigator() {
        return this.mNavigator;
    }

    private void initController(boolean z) {
        this.mLifecycle = new LifecycleRegistry(this);
        HostCallbacks hostCallbacks = new HostCallbacks(this.mNavigator.getBaseNavigator().getNavHostFragment());
        this.mHost = hostCallbacks;
        this.mController = FragmentController.createController(hostCallbacks);
        if (z) {
            performRestore(null);
            attachHost();
        }
    }

    private void computeState() {
        dispatchState(Math.min(this.mHostState, this.mFragmentState));
    }

    private void dispatchState(int i) {
        while (true) {
            int i2 = this.mRealState;
            if (i == i2) {
                return;
            }
            if (i > i2) {
                if (i2 == 0) {
                    this.mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
                    this.mController.dispatchCreate();
                } else if (i2 == 1) {
                    this.mController.dispatchActivityCreated();
                } else if (i2 == 2) {
                    this.mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START);
                    this.mController.dispatchStart();
                } else if (i2 == 3) {
                    this.mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
                    this.mController.dispatchResume();
                } else {
                    throw new IllegalStateException("bad lifecycle");
                }
                this.mRealState++;
            } else {
                if (i2 == 1) {
                    this.mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
                    this.mController.dispatchDestroy();
                    initController(true);
                } else if (i2 == 2) {
                    this.mController.dispatchDestroyView();
                } else if (i2 == 3) {
                    this.mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
                    this.mController.dispatchStop();
                } else if (i2 == 4) {
                    this.mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
                    this.mController.dispatchPause();
                } else {
                    throw new IllegalStateException("bad lifecycle");
                }
                this.mRealState--;
            }
        }
    }

    public FragmentManager getFragmentManager() {
        return this.mController.getSupportFragmentManager();
    }

    public void setHostState(int i) {
        this.mHostState = i;
        computeState();
    }

    public void setFragmentState(int i) {
        this.mFragmentState = i;
        computeState();
    }

    void performRestore(Bundle bundle) {
        this.mHost.mSavedStateRegistryController.performRestore(bundle);
    }

    void attachHost() {
        this.mController.attachHost(null);
        OnAttachListener onAttachListener = this.mOnAttachListener;
        if (onAttachListener != null) {
            onAttachListener.onAttach(this);
        }
    }

    void performSaveInstanceState(Bundle bundle) {
        this.mHost.mSavedStateRegistryController.performSave(bundle);
    }

    @Override // androidx.lifecycle.LifecycleOwner
    public Lifecycle getLifecycle() {
        return this.mLifecycle;
    }

    public void setOnAttachListener(OnAttachListener onAttachListener) {
        this.mOnAttachListener = onAttachListener;
    }

    @Override // miuix.responsive.interfaces.IResponsive
    public Object getResponsiveSubject() {
        return this.mHost.mFragment;
    }

    @Override // miuix.responsive.interfaces.IResponsive
    public ResponsiveState getResponsiveState() {
        return this.mHost.mFragment.getResponsiveState();
    }

    @Override // miuix.responsive.interfaces.IResponsive
    public void dispatchResponsiveLayout(Configuration configuration, ScreenSpec screenSpec, boolean z) {
        this.mHost.mFragment.dispatchResponsiveLayout(configuration, screenSpec, z);
    }

    class HostCallbacks extends FragmentHostCallback<NavigatorFragmentController> implements OnConfigurationChangedProvider, OnTrimMemoryProvider, OnMultiWindowModeChangedProvider, OnPictureInPictureModeChangedProvider, ViewModelStoreOwner, SavedStateRegistryOwner, FragmentOnAttachListener, MenuHost {
        private final NavHostFragment mFragment;
        private final SavedStateRegistryController mSavedStateRegistryController;

        HostCallbacks(NavHostFragment navHostFragment) {
            super(navHostFragment.requireContext(), new Handler(Looper.getMainLooper()), 0);
            this.mFragment = navHostFragment;
            SavedStateRegistryController savedStateRegistryControllerCreate = SavedStateRegistryController.create(this);
            this.mSavedStateRegistryController = savedStateRegistryControllerCreate;
            savedStateRegistryControllerCreate.performAttach();
        }

        @Override // androidx.fragment.app.FragmentHostCallback
        public LayoutInflater onGetLayoutInflater() {
            return this.mFragment.getLayoutInflater().cloneInContext(this.mFragment.getContext());
        }

        @Override // androidx.fragment.app.FragmentHostCallback
        public NavigatorFragmentController onGetHost() {
            return NavigatorFragmentController.this;
        }

        @Override // androidx.core.app.OnMultiWindowModeChangedProvider
        public void addOnMultiWindowModeChangedListener(Consumer<MultiWindowModeChangedInfo> consumer) {
            FragmentActivity activity = this.mFragment.getActivity();
            if (activity != null) {
                activity.addOnMultiWindowModeChangedListener(consumer);
            }
        }

        @Override // androidx.core.app.OnMultiWindowModeChangedProvider
        public void removeOnMultiWindowModeChangedListener(Consumer<MultiWindowModeChangedInfo> consumer) {
            FragmentActivity activity = this.mFragment.getActivity();
            if (activity != null) {
                activity.removeOnMultiWindowModeChangedListener(consumer);
            }
        }

        @Override // androidx.core.app.OnPictureInPictureModeChangedProvider
        public void addOnPictureInPictureModeChangedListener(Consumer<PictureInPictureModeChangedInfo> consumer) {
            FragmentActivity activity = this.mFragment.getActivity();
            if (activity != null) {
                activity.addOnPictureInPictureModeChangedListener(consumer);
            }
        }

        @Override // androidx.core.app.OnPictureInPictureModeChangedProvider
        public void removeOnPictureInPictureModeChangedListener(Consumer<PictureInPictureModeChangedInfo> consumer) {
            FragmentActivity activity = this.mFragment.getActivity();
            if (activity != null) {
                activity.removeOnPictureInPictureModeChangedListener(consumer);
            }
        }

        @Override // androidx.core.content.OnConfigurationChangedProvider
        public void addOnConfigurationChangedListener(Consumer<Configuration> consumer) {
            FragmentActivity activity = this.mFragment.getActivity();
            if (activity != null) {
                activity.addOnConfigurationChangedListener(consumer);
            }
        }

        @Override // androidx.core.content.OnConfigurationChangedProvider
        public void removeOnConfigurationChangedListener(Consumer<Configuration> consumer) {
            FragmentActivity activity = this.mFragment.getActivity();
            if (activity != null) {
                activity.removeOnConfigurationChangedListener(consumer);
            }
        }

        @Override // androidx.core.content.OnTrimMemoryProvider
        public void addOnTrimMemoryListener(Consumer<Integer> consumer) {
            FragmentActivity activity = this.mFragment.getActivity();
            if (activity != null) {
                activity.addOnTrimMemoryListener(consumer);
            }
        }

        @Override // androidx.core.content.OnTrimMemoryProvider
        public void removeOnTrimMemoryListener(Consumer<Integer> consumer) {
            FragmentActivity activity = this.mFragment.getActivity();
            if (activity != null) {
                activity.removeOnTrimMemoryListener(consumer);
            }
        }

        @Override // androidx.core.view.MenuHost
        public void addMenuProvider(MenuProvider menuProvider) {
            FragmentActivity activity = this.mFragment.getActivity();
            if (activity != null) {
                activity.addMenuProvider(menuProvider);
            }
        }

        @Override // androidx.core.view.MenuHost
        public void addMenuProvider(MenuProvider menuProvider, LifecycleOwner lifecycleOwner) {
            FragmentActivity activity = this.mFragment.getActivity();
            if (activity != null) {
                activity.addMenuProvider(menuProvider, lifecycleOwner);
            }
        }

        @Override // androidx.core.view.MenuHost
        public void addMenuProvider(MenuProvider menuProvider, LifecycleOwner lifecycleOwner, Lifecycle.State state) {
            FragmentActivity activity = this.mFragment.getActivity();
            if (activity != null) {
                activity.addMenuProvider(menuProvider, lifecycleOwner, state);
            }
        }

        @Override // androidx.core.view.MenuHost
        public void removeMenuProvider(MenuProvider menuProvider) {
            FragmentActivity activity = this.mFragment.getActivity();
            if (activity != null) {
                activity.removeMenuProvider(menuProvider);
            }
        }

        @Override // androidx.core.view.MenuHost
        public void invalidateMenu() {
            FragmentActivity activity = this.mFragment.getActivity();
            if (activity != null) {
                activity.invalidateMenu();
            }
        }

        @Override // androidx.fragment.app.FragmentOnAttachListener
        public void onAttachFragment(FragmentManager fragmentManager, Fragment fragment) {
            fragment.onAttachFragment(fragment);
        }

        @Override // androidx.lifecycle.LifecycleOwner
        public Lifecycle getLifecycle() {
            return NavigatorFragmentController.this.getLifecycle();
        }

        @Override // androidx.lifecycle.ViewModelStoreOwner
        public ViewModelStore getViewModelStore() {
            return this.mFragment.getViewModelStore();
        }

        @Override // androidx.savedstate.SavedStateRegistryOwner
        public SavedStateRegistry getSavedStateRegistry() {
            return this.mSavedStateRegistryController.getSavedStateRegistry();
        }

        @Override // androidx.fragment.app.FragmentHostCallback, androidx.fragment.app.FragmentContainer
        public boolean onHasView() {
            return this.mFragment.getView() != null;
        }

        @Override // androidx.fragment.app.FragmentHostCallback, androidx.fragment.app.FragmentContainer
        public View onFindViewById(int i) {
            return this.mFragment.requireView().findViewById(i);
        }
    }
}
