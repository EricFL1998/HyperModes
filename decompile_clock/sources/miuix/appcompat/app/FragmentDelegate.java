package miuix.appcompat.app;

import android.animation.Animator;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.ActionMode;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import androidx.activity.result.ActivityResultCaller;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import java.util.List;
import miuix.appcompat.R;
import miuix.appcompat.internal.app.widget.ActionBarImpl;
import miuix.appcompat.internal.app.widget.ActionBarOverlayLayout;
import miuix.appcompat.internal.app.widget.ActionBarView;
import miuix.appcompat.internal.util.AnimationUtils;
import miuix.appcompat.internal.view.SimpleWindowCallback;
import miuix.appcompat.internal.view.menu.MenuBuilder;
import miuix.container.ExtraPaddingObserver;
import miuix.container.ExtraPaddingPolicy;
import miuix.core.util.EnvStateManager;
import miuix.core.util.WindowBaseInfo;
import miuix.internal.util.AttributeResolver;
import miuix.os.DeviceHelper;
import miuix.responsive.interfaces.IResponsive;
import miuix.responsive.map.ResponsiveState;
import miuix.responsive.map.ScreenSpec;
import miuix.responsive.page.manager.BaseResponseStateManager;
import miuix.view.SearchActionMode;

/* JADX INFO: loaded from: classes2.dex */
public class FragmentDelegate extends ActionBarDelegateImpl implements IResponsive<androidx.fragment.app.Fragment> {
    private static final int INVALIDATE_MENU_POSTED = 16;
    public static final int MENU_INVALIDATE = 1;
    private int mExtraThemeRes;
    private androidx.fragment.app.Fragment mFragment;
    private final Handler mHandler;
    private View mInflatedView;
    private byte mInvalidateMenuFlags;
    private Runnable mInvalidateMenuRunnable;
    protected boolean mIsInEditActionMode;
    protected boolean mIsInSearchActionMode;
    private boolean mIsUserResponsiveEnabled;
    private BaseResponseStateManager mResponsiveStateManager;
    private boolean mSplitActionBarEnable;
    private View mSubDecor;
    private Context mThemedContext;
    private final Window.Callback mWindowCallback;

    public void checkThemeLegality() {
    }

    @Override // miuix.appcompat.app.ActionBarDelegate
    public View onCreatePanelView(int i) {
        return null;
    }

    static /* synthetic */ byte access$372(FragmentDelegate fragmentDelegate, int i) {
        byte b = (byte) (i & fragmentDelegate.mInvalidateMenuFlags);
        fragmentDelegate.mInvalidateMenuFlags = b;
        return b;
    }

    public FragmentDelegate(androidx.fragment.app.Fragment fragment) {
        super((AppCompatActivity) fragment.getActivity());
        this.mIsUserResponsiveEnabled = false;
        this.mIsInEditActionMode = false;
        this.mIsInSearchActionMode = false;
        this.mSplitActionBarEnable = false;
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mWindowCallback = new SimpleWindowCallback() { // from class: miuix.appcompat.app.FragmentDelegate.1
            @Override // miuix.appcompat.internal.view.SimpleWindowCallback, android.view.Window.Callback
            public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
                return FragmentDelegate.this.onWindowStartingActionMode(callback);
            }

            @Override // miuix.appcompat.internal.view.SimpleWindowCallback, android.view.Window.Callback
            public void onActionModeStarted(ActionMode actionMode) {
                ((IFragment) FragmentDelegate.this.mFragment).onActionModeStarted(actionMode);
            }

            @Override // miuix.appcompat.internal.view.SimpleWindowCallback, android.view.Window.Callback
            public void onActionModeFinished(ActionMode actionMode) {
                ((IFragment) FragmentDelegate.this.mFragment).onActionModeFinished(actionMode);
            }

            @Override // miuix.appcompat.internal.view.SimpleWindowCallback, android.view.Window.Callback
            public boolean onMenuItemSelected(int i, MenuItem menuItem) {
                return FragmentDelegate.this.onMenuItemSelected(i, menuItem);
            }

            @Override // miuix.appcompat.internal.view.SimpleWindowCallback, android.view.Window.Callback
            public void onPanelClosed(int i, Menu menu) {
                FragmentDelegate.this.onPanelClosed(i, menu);
            }
        };
        this.mFragment = fragment;
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl
    public LifecycleOwner getLifecycleOwner() {
        return this.mFragment;
    }

    public void setResponsiveEnabled(boolean z) {
        this.mIsUserResponsiveEnabled = z;
    }

    public Animator onCreateAnimator(int i, boolean z, int i2) {
        return AnimationUtils.createAnimator(this.mFragment, i2);
    }

    @Override // miuix.appcompat.app.ActionBarDelegate
    public ActionBar createActionBar() {
        if (!this.mFragment.isAdded() || this.mActionBarView == null) {
            return null;
        }
        return new ActionBarImpl(this.mFragment);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        TypedArray typedArrayObtainStyledAttributes = getThemedContext().obtainStyledAttributes(R.styleable.Window);
        if (typedArrayObtainStyledAttributes.getBoolean(R.styleable.Window_responsiveEnabled, this.mIsUserResponsiveEnabled)) {
            this.mResponsiveStateManager = new BaseResponseStateManager(this) { // from class: miuix.appcompat.app.FragmentDelegate.2
                @Override // miuix.responsive.page.manager.BaseStateManager
                protected Context getContext() {
                    return FragmentDelegate.this.getThemedContext();
                }
            };
        }
        if (!typedArrayObtainStyledAttributes.hasValue(R.styleable.Window_windowActionBar)) {
            typedArrayObtainStyledAttributes.recycle();
            throw new IllegalStateException("You need to use a miuix theme (or descendant) with this fragment.");
        }
        if (typedArrayObtainStyledAttributes.getBoolean(R.styleable.Window_windowActionBar, false)) {
            requestWindowFeature(8);
        }
        if (typedArrayObtainStyledAttributes.getBoolean(R.styleable.Window_windowActionBarOverlay, false)) {
            requestWindowFeature(9);
        }
        boolean z = typedArrayObtainStyledAttributes.getBoolean(R.styleable.Window_windowExtraPaddingHorizontalEnable, this.mExtraPaddingEnable);
        if (this.mExtraPaddingEnable) {
            z = true;
        }
        setExtraHorizontalPaddingEnable(z);
        boolean z2 = typedArrayObtainStyledAttributes.getBoolean(R.styleable.Window_windowExtraPaddingHorizontalInitEnable, this.mExtraPaddingInitEnable);
        if (this.mExtraPaddingInitEnable) {
            z2 = true;
        }
        setExtraHorizontalPaddingInitEnable(z2);
        boolean z3 = typedArrayObtainStyledAttributes.getBoolean(R.styleable.Window_windowExtraPaddingApplyToContentEnable, this.mExtraPaddingApplyToContentEnable);
        if (this.mExtraPaddingApplyToContentEnable) {
            z3 = true;
        }
        setExtraPaddingApplyToContentEnable(z3);
        setTranslucentStatus(typedArrayObtainStyledAttributes.getInt(R.styleable.Window_windowTranslucentStatus, 0));
        LayoutInflater layoutInflaterCloneInContext = layoutInflater.cloneInContext(getThemedContext());
        if (this.mHasActionBar) {
            installSubDecor(getThemedContext(), viewGroup, layoutInflaterCloneInContext);
            if (this.mSubDecor instanceof ActionBarOverlayLayout) {
                if (!this.mUserExtraPaddingPolicy) {
                    initExtraPaddingPolicy();
                }
                ActionBarOverlayLayout actionBarOverlayLayout = (ActionBarOverlayLayout) this.mSubDecor;
                actionBarOverlayLayout.setExtraHorizontalPaddingEnable(isExtraHorizontalPaddingEnable());
                actionBarOverlayLayout.setExtraHorizontalPaddingInitEnable(this.mExtraPaddingInitEnable);
                actionBarOverlayLayout.setExtraPaddingApplyToContentEnable(isExtraPaddingApplyToContentEnable());
                actionBarOverlayLayout.setExtraPaddingPolicy(this.mExtraPaddingPolicy);
            }
            ViewGroup viewGroup2 = (ViewGroup) this.mSubDecor.findViewById(android.R.id.content);
            View viewOnInflateView = ((IFragment) this.mFragment).onInflateView(layoutInflaterCloneInContext, viewGroup2, bundle);
            this.mInflatedView = viewOnInflateView;
            if (viewOnInflateView != null && viewOnInflateView.getParent() != viewGroup2) {
                if (this.mInflatedView.getParent() != null) {
                    ((ViewGroup) this.mInflatedView.getParent()).removeView(this.mInflatedView);
                }
                viewGroup2.removeAllViews();
                viewGroup2.addView(this.mInflatedView);
            }
            if (this.mSplitActionBarEnable) {
                setHyperSplitMenuEnabled(typedArrayObtainStyledAttributes.getBoolean(R.styleable.Window_hyperSplitMenuEnabled, false));
            }
            if (typedArrayObtainStyledAttributes.getBoolean(R.styleable.Window_endActionMenuEnabled, false)) {
                setEndActionMenuEnabled(true, typedArrayObtainStyledAttributes.getBoolean(R.styleable.Window_hyperActionMenuEnabled, false), false);
            } else {
                byte b = this.mInvalidateMenuFlags;
                if ((b & 16) == 0) {
                    this.mInvalidateMenuFlags = (byte) (b | 16);
                    this.mHandler.post(getInvalidateMenuRunnable());
                }
            }
        } else {
            View viewOnInflateView2 = ((IFragment) this.mFragment).onInflateView(layoutInflaterCloneInContext, viewGroup, bundle);
            this.mInflatedView = viewOnInflateView2;
            this.mSubDecor = viewOnInflateView2;
            if (viewOnInflateView2 != null) {
                if (!this.mUserExtraPaddingPolicy) {
                    initExtraPaddingPolicy();
                }
                if (!((IFragment) this.mFragment).acceptExtraPaddingFromParent()) {
                    if (this.mExtraPaddingInitEnable) {
                        Context context = this.mFragment.getContext();
                        if (this.mExtraPaddingPolicy != null && context != null) {
                            updateExtraPaddingHorizontal(context, this.mExtraPaddingPolicy, -1, -1);
                        }
                    }
                    this.mSubDecor.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: miuix.appcompat.app.FragmentDelegate.3
                        @Override // android.view.View.OnLayoutChangeListener
                        public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                            Context context2 = FragmentDelegate.this.mFragment.getContext();
                            if (FragmentDelegate.this.mExtraPaddingPolicy == null || context2 == null) {
                                return;
                            }
                            FragmentDelegate fragmentDelegate = FragmentDelegate.this;
                            if (fragmentDelegate.updateExtraPaddingHorizontal(context2, fragmentDelegate.mExtraPaddingPolicy, i3 - i, i4 - i2)) {
                                if (FragmentDelegate.this.mExtraPaddingObserver != null) {
                                    for (int i9 = 0; i9 < FragmentDelegate.this.mExtraPaddingObserver.size(); i9++) {
                                        FragmentDelegate.this.mExtraPaddingObserver.get(i9).onExtraPaddingChanged(FragmentDelegate.this.mExtraHorizontalPadding);
                                    }
                                }
                                ((IFragment) FragmentDelegate.this.mFragment).onExtraPaddingChanged(FragmentDelegate.this.mExtraHorizontalPadding);
                            }
                        }
                    });
                }
            }
        }
        typedArrayObtainStyledAttributes.recycle();
        return this.mSubDecor;
    }

    public void onViewCreated(View view, Bundle bundle) {
        ((IFragment) this.mFragment).onViewInflated(this.mInflatedView, bundle);
    }

    final void installSubDecor(Context context, ViewGroup viewGroup, LayoutInflater layoutInflater) {
        if (!this.mSubDecorInstalled) {
            FragmentActivity activity = this.mFragment.getActivity();
            boolean z = activity instanceof AppCompatActivity;
            if (z) {
                AppCompatActivity appCompatActivity = (AppCompatActivity) activity;
                appCompatActivity.setExtraHorizontalPaddingEnable(false);
                appCompatActivity.setExtraPaddingApplyToContentEnable(false);
            }
            this.mSubDecorInstalled = true;
            ActionBarOverlayLayout actionBarOverlayLayout = (ActionBarOverlayLayout) layoutInflater.inflate(R.layout.miuix_appcompat_screen_action_bar, viewGroup, false);
            actionBarOverlayLayout.setLifecycleOwner(getLifecycleOwner());
            actionBarOverlayLayout.setCallback(this.mWindowCallback);
            ActivityResultCaller activityResultCaller = this.mFragment;
            if (activityResultCaller instanceof IFragment) {
                actionBarOverlayLayout.setContentInsetStateCallback((IContentInsetState) activityResultCaller);
                actionBarOverlayLayout.addExtraPaddingObserver((ExtraPaddingObserver) this.mFragment);
            }
            actionBarOverlayLayout.setRootSubDecor(false);
            actionBarOverlayLayout.setOverlayMode(this.mOverlayActionBar);
            actionBarOverlayLayout.setTranslucentStatus(getTranslucentStatus());
            if (this.mExtraThemeRes != 0) {
                checkThemeLegality();
                ((IFragment) this.mFragment).checkThemeLegality();
                actionBarOverlayLayout.setBackground(AttributeResolver.resolveDrawable(context, android.R.attr.windowBackground));
            }
            if (z) {
                actionBarOverlayLayout.onFloatingModeChanged(((AppCompatActivity) activity).isInFloatingWindowMode());
            }
            this.mActionBarView = (ActionBarView) actionBarOverlayLayout.findViewById(R.id.action_bar);
            this.mActionBarView.setLifecycleOwner(getLifecycleOwner());
            this.mActionBarView.setWindowCallback(this.mWindowCallback);
            if (this.mFeatureIndeterminateProgress) {
                this.mActionBarView.initIndeterminateProgress();
            }
            if (isEndActionMenuEnabled()) {
                this.mActionBarView.setEndActionMenuEnable(true);
            }
            boolean zEquals = "splitActionBarWhenNarrow".equals(getUiOptionsFromMetadata());
            if (zEquals) {
                this.mSplitActionBarEnable = context.getResources().getBoolean(R.bool.abc_split_action_bar_is_narrow);
            } else {
                TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(R.styleable.Window);
                this.mSplitActionBarEnable = typedArrayObtainStyledAttributes.getBoolean(R.styleable.Window_windowSplitActionBar, false);
                typedArrayObtainStyledAttributes.recycle();
            }
            if (this.mSplitActionBarEnable) {
                addSplitActionBar(true, zEquals, actionBarOverlayLayout);
            }
            updateOptionsMenu(1);
            this.mSubDecor = actionBarOverlayLayout;
            return;
        }
        if (this.mSubDecor.getParent() == null || !(this.mSubDecor.getParent() instanceof ViewGroup)) {
            return;
        }
        ViewGroup viewGroup2 = (ViewGroup) this.mSubDecor.getParent();
        if (viewGroup2.getChildCount() == 0) {
            viewGroup2.endViewTransition(this.mSubDecor);
        }
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl, miuix.appcompat.app.ActionBarDelegate
    public void onConfigurationChanged(Configuration configuration) {
        int iDetectType;
        BaseResponseStateManager baseResponseStateManager = this.mResponsiveStateManager;
        if (baseResponseStateManager != null) {
            baseResponseStateManager.beforeConfigurationChanged(this.mFragment.getResources().getConfiguration());
        }
        super.onConfigurationChanged(configuration);
        if (!this.mUserExtraPaddingPolicy && this.mDeviceType != (iDetectType = DeviceHelper.detectType(this.mActivity))) {
            this.mDeviceType = iDetectType;
            initExtraPaddingPolicy();
            View view = this.mSubDecor;
            if (view instanceof ActionBarOverlayLayout) {
                ((ActionBarOverlayLayout) view).setExtraPaddingPolicy(this.mExtraPaddingPolicy);
            }
        }
        View view2 = this.mSubDecor;
        if (view2 != null && (view2 instanceof ActionBarOverlayLayout)) {
            ActionBarOverlayLayout actionBarOverlayLayout = (ActionBarOverlayLayout) view2;
            if (!this.mUserExtraPaddingPolicy) {
                actionBarOverlayLayout.setExtraPaddingPolicy(getExtraPaddingPolicy());
            }
            FragmentActivity activity = this.mFragment.getActivity();
            if (activity instanceof AppCompatActivity) {
                ((ActionBarOverlayLayout) this.mSubDecor).onFloatingModeChanged(((AppCompatActivity) activity).isInFloatingWindowMode());
            }
        }
        BaseResponseStateManager baseResponseStateManager2 = this.mResponsiveStateManager;
        if (baseResponseStateManager2 != null) {
            baseResponseStateManager2.afterConfigurationChanged(configuration);
        }
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl, miuix.container.ExtraPaddingProcessor
    public void setExtraPaddingPolicy(ExtraPaddingPolicy extraPaddingPolicy) {
        super.setExtraPaddingPolicy(extraPaddingPolicy);
        View view = this.mSubDecor;
        if (view instanceof ActionBarOverlayLayout) {
            ((ActionBarOverlayLayout) view).setExtraPaddingPolicy(this.mExtraPaddingPolicy);
        }
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl, miuix.container.ExtraPaddingProcessor
    public void addExtraPaddingObserver(ExtraPaddingObserver extraPaddingObserver) {
        super.addExtraPaddingObserver(extraPaddingObserver);
        View view = this.mSubDecor;
        if (view instanceof ActionBarOverlayLayout) {
            ((ActionBarOverlayLayout) view).addExtraPaddingObserver(extraPaddingObserver);
        }
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl, miuix.container.ExtraPaddingProcessor
    public void removeExtraPaddingObserver(ExtraPaddingObserver extraPaddingObserver) {
        if (this.mExtraPaddingObserver != null) {
            this.mExtraPaddingObserver.remove(extraPaddingObserver);
        }
        View view = this.mSubDecor;
        if (view instanceof ActionBarOverlayLayout) {
            ((ActionBarOverlayLayout) view).removeExtraPaddingObserver(extraPaddingObserver);
        }
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl, miuix.container.ExtraPaddingProcessor
    public void setExtraHorizontalPaddingEnable(boolean z) {
        super.setExtraHorizontalPaddingEnable(z);
        View view = this.mSubDecor;
        if (view instanceof ActionBarOverlayLayout) {
            ((ActionBarOverlayLayout) view).setExtraHorizontalPaddingEnable(z);
        }
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl, miuix.container.ExtraPaddingProcessor
    public void setExtraHorizontalPaddingInitEnable(boolean z) {
        super.setExtraHorizontalPaddingInitEnable(z);
        View view = this.mSubDecor;
        if (view instanceof ActionBarOverlayLayout) {
            ((ActionBarOverlayLayout) view).setExtraHorizontalPaddingInitEnable(this.mExtraPaddingInitEnable);
        }
    }

    public boolean acceptExtraPaddingFromParent() {
        return hasActionBar() || !isExtraHorizontalPaddingEnable() || this.mExtraPaddingPolicy == null;
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl
    public void setExtraPaddingApplyToContentEnable(boolean z) {
        super.setExtraPaddingApplyToContentEnable(z);
        View view = this.mSubDecor;
        if (view instanceof ActionBarOverlayLayout) {
            ((ActionBarOverlayLayout) view).setExtraPaddingApplyToContentEnable(z);
        }
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl
    public View getView() {
        return this.mSubDecor;
    }

    public View getInflatedView() {
        return this.mInflatedView;
    }

    @Override // miuix.appcompat.app.ActionBarDelegate
    public void invalidateOptionsMenu() {
        byte b = this.mInvalidateMenuFlags;
        if ((b & 16) == 0) {
            this.mInvalidateMenuFlags = (byte) (b | 16);
            getInvalidateMenuRunnable().run();
        }
    }

    @Override // miuix.appcompat.app.ActionBarDelegate
    public boolean onCreatePanelMenu(int i, Menu menu) {
        if (i == 0) {
            return ((IFragment) this.mFragment).onCreatePanelMenu(i, menu);
        }
        return false;
    }

    @Override // miuix.appcompat.app.ActionBarDelegate
    public boolean onPreparePanel(int i, View view, Menu menu) {
        if (i != 0) {
            return false;
        }
        ((IFragment) this.mFragment).onPreparePanel(i, null, menu);
        return true;
    }

    @Override // miuix.appcompat.app.ActionBarDelegate
    public void onPanelViewAdded(int i, View view, Menu menu, Menu menu2) {
        if (i == 0) {
            ((IFragment) this.mFragment).onOptionsMenuViewAdded(menu, menu2);
        }
    }

    @Override // miuix.appcompat.app.ActionBarDelegate
    public boolean onMenuItemSelected(int i, MenuItem menuItem) {
        if (i == 0) {
            return this.mFragment.onOptionsItemSelected(menuItem);
        }
        if (i == 6) {
            return this.mFragment.onContextItemSelected(menuItem);
        }
        return false;
    }

    @Override // miuix.appcompat.app.ActionBarDelegate
    public void onPanelClosed(int i, Menu menu) {
        ((IFragment) this.mFragment).onPanelClosed(i, menu);
        if (i == 0) {
            this.mFragment.onOptionsMenuClosed(menu);
        }
    }

    @Override // miuix.appcompat.internal.view.menu.MenuBuilder.Callback
    public boolean onMenuItemSelected(MenuBuilder menuBuilder, MenuItem menuItem) {
        return onMenuItemSelected(0, menuItem);
    }

    private Runnable getInvalidateMenuRunnable() {
        if (this.mInvalidateMenuRunnable == null) {
            this.mInvalidateMenuRunnable = new InvalidateMenuRunnable();
        }
        return this.mInvalidateMenuRunnable;
    }

    public void updateOptionsMenu(int i) {
        this.mInvalidateMenuFlags = (byte) ((i & 1) | this.mInvalidateMenuFlags);
    }

    @Override // miuix.appcompat.app.IContentInsetState
    public Rect getContentInset() {
        if (!this.mHasActionBar && this.mContentInset == null) {
            ActivityResultCaller parentFragment = this.mFragment.getParentFragment();
            if (parentFragment instanceof IFragment) {
                this.mContentInset = ((IFragment) parentFragment).getContentInset();
            } else if (parentFragment == null) {
                this.mContentInset = getActivity().getContentInset();
            }
        } else if (this.mHasActionBar) {
            View view = this.mSubDecor;
            if (view instanceof ActionBarOverlayLayout) {
                this.mContentInset = ((ActionBarOverlayLayout) view).getContentInset();
            }
        }
        return this.mContentInset;
    }

    @Override // miuix.appcompat.app.IContentInsetState
    public boolean requestDispatchContentInset() {
        View view = this.mSubDecor;
        if (view instanceof ActionBarOverlayLayout) {
            ((ActionBarOverlayLayout) view).requestDispatchContentInset();
            return true;
        }
        ActivityResultCaller parentFragment = this.mFragment.getParentFragment();
        if (parentFragment instanceof IFragment ? ((IFragment) parentFragment).requestDispatchContentInset() : false) {
            return false;
        }
        return getActivity().requestDispatchContentInset();
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl, miuix.appcompat.app.ActionBarDelegate
    public void registerCoordinateScrollView(View view) {
        super.registerCoordinateScrollView(view);
        if (hasActionBar()) {
            return;
        }
        ActivityResultCaller parentFragment = this.mFragment.getParentFragment();
        ActionBar actionBar = parentFragment instanceof IFragment ? ((IFragment) parentFragment).getActionBar() : null;
        if (actionBar != null) {
            actionBar.registerCoordinateScrollView(view);
        }
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl, miuix.appcompat.app.ActionBarDelegate
    public void unregisterCoordinateScrollView(View view) {
        super.unregisterCoordinateScrollView(view);
        if (hasActionBar()) {
            return;
        }
        ActivityResultCaller parentFragment = this.mFragment.getParentFragment();
        ActionBar actionBar = parentFragment instanceof IFragment ? ((IFragment) parentFragment).getActionBar() : null;
        if (actionBar != null) {
            actionBar.unregisterCoordinateScrollView(view);
        }
    }

    @Override // miuix.appcompat.app.ActionBarDelegate
    public void setBottomExtraInset(int i) {
        View view = this.mSubDecor;
        if (view instanceof ActionBarOverlayLayout) {
            ((ActionBarOverlayLayout) view).setBottomExtraInset(i);
        }
    }

    @Override // miuix.appcompat.app.ActionBarDelegate
    public void setBottomMenuMode(int i) {
        View view = this.mSubDecor;
        if (view instanceof ActionBarOverlayLayout) {
            ((ActionBarOverlayLayout) view).setBottomMenuMode(i);
        }
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl, miuix.appcompat.app.ActionBarDelegate
    public int getBottomMenuMode() {
        View view = this.mSubDecor;
        if (view instanceof ActionBarOverlayLayout) {
            return ((ActionBarOverlayLayout) view).getBottomMenuMode();
        }
        return super.getBottomMenuMode();
    }

    @Override // miuix.appcompat.app.ActionBarDelegate, miuix.appcompat.app.IContentInsetState
    public void setCorrectNestedScrollMotionEventEnabled(boolean z) {
        View view = this.mSubDecor;
        if (view instanceof ActionBarOverlayLayout) {
            ((ActionBarOverlayLayout) view).setCorrectNestedScrollMotionEventEnabled(z);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // miuix.appcompat.app.ActionBarDelegateImpl, miuix.appcompat.app.IContentInsetState
    public void onContentInsetChanged(Rect rect) {
        super.onContentInsetChanged(rect);
        List<androidx.fragment.app.Fragment> fragments = this.mFragment.getChildFragmentManager().getFragments();
        int size = fragments.size();
        for (int i = 0; i < size; i++) {
            androidx.fragment.app.Fragment fragment = fragments.get(i);
            if ((fragment instanceof IFragment) && fragment.isAdded()) {
                IFragment iFragment = (IFragment) fragment;
                if (!iFragment.hasActionBar()) {
                    iFragment.onContentInsetChanged(rect);
                }
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // miuix.container.ExtraPaddingObserver
    public void onExtraPaddingChanged(int i) {
        this.mExtraHorizontalPadding = i;
        List<androidx.fragment.app.Fragment> fragments = this.mFragment.getChildFragmentManager().getFragments();
        int size = fragments.size();
        for (int i2 = 0; i2 < size; i2++) {
            androidx.fragment.app.Fragment fragment = fragments.get(i2);
            if ((fragment instanceof IFragment) && fragment.isAdded()) {
                IFragment iFragment = (IFragment) fragment;
                if (iFragment.acceptExtraPaddingFromParent() && iFragment.isExtraHorizontalPaddingEnable()) {
                    iFragment.onExtraPaddingChanged(i);
                }
            }
        }
    }

    public boolean isInEditActionMode() {
        ActivityResultCaller parentFragment = this.mFragment.getParentFragment();
        if (!hasActionBar() && (parentFragment instanceof IFragment)) {
            return ((IFragment) parentFragment).isInEditActionMode();
        }
        return this.mIsInEditActionMode;
    }

    public boolean isIsInSearchActionMode() {
        ActivityResultCaller parentFragment = this.mFragment.getParentFragment();
        if (!hasActionBar() && (parentFragment instanceof IFragment)) {
            return ((IFragment) parentFragment).isIsInSearchActionMode();
        }
        return this.mIsInSearchActionMode;
    }

    public boolean isRegisterResponsive() {
        return this.mResponsiveStateManager != null;
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl, miuix.appcompat.app.ActionBarDelegate
    public ActionMode startActionMode(ActionMode.Callback callback) {
        if (callback instanceof SearchActionMode.Callback) {
            addContentMask((ActionBarOverlayLayout) this.mSubDecor);
        }
        return this.mSubDecor.startActionMode(callback);
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl, miuix.appcompat.app.ActionBarDelegate
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
        if (getActionBar() != null) {
            return ((ActionBarImpl) getActionBar()).startActionMode(callback);
        }
        return null;
    }

    public void setExtraThemeRes(int i) {
        this.mExtraThemeRes = i;
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl
    public Context getThemedContext() {
        if (this.mThemedContext == null) {
            this.mThemedContext = this.mActivity;
            if (this.mExtraThemeRes != 0) {
                this.mThemedContext = new ContextThemeWrapper(this.mThemedContext, this.mExtraThemeRes);
            }
        }
        return this.mThemedContext;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // miuix.appcompat.app.ActionBarDelegateImpl
    protected boolean onCreateImmersionMenu(MenuBuilder menuBuilder) {
        return ((IFragment) this.mFragment).onCreateOptionsMenu(menuBuilder);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // miuix.appcompat.app.ActionBarDelegateImpl
    protected boolean onPrepareImmersionMenu(MenuBuilder menuBuilder) {
        this.mFragment.onPrepareOptionsMenu(menuBuilder);
        return true;
    }

    public void setOnStatusBarChangeListener(OnStatusBarChangeListener onStatusBarChangeListener) {
        View view = this.mSubDecor;
        if (view == null || !(view instanceof ActionBarOverlayLayout)) {
            return;
        }
        ((ActionBarOverlayLayout) view).setOnStatusBarChangeListener(onStatusBarChangeListener);
    }

    public void onDestroyView() {
        onDestroy();
        if (this.mExtraPaddingObserver != null) {
            this.mExtraPaddingObserver.clear();
        }
        this.mInflatedView = null;
        this.mSubDecor = null;
        this.mSubDecorInstalled = false;
        this.mHasAddSplitActionBar = false;
        this.mActionBar = null;
        this.mActionBarView = null;
        Runnable runnable = this.mInvalidateMenuRunnable;
        if (runnable != null) {
            this.mHandler.removeCallbacks(runnable);
            this.mInvalidateMenuRunnable = null;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // miuix.responsive.interfaces.IResponsive
    public androidx.fragment.app.Fragment getResponsiveSubject() {
        return this.mFragment;
    }

    @Override // miuix.responsive.interfaces.IResponsive
    public ResponsiveState getResponsiveState() {
        BaseResponseStateManager baseResponseStateManager = this.mResponsiveStateManager;
        if (baseResponseStateManager != null) {
            return baseResponseStateManager.getState();
        }
        return null;
    }

    @Override // miuix.responsive.interfaces.IResponsive
    public void dispatchResponsiveLayout(Configuration configuration, ScreenSpec screenSpec, boolean z) {
        onResponsiveLayout(configuration, screenSpec, z);
    }

    @Override // miuix.responsive.interfaces.IResponsive
    public void onResponsiveLayout(Configuration configuration, ScreenSpec screenSpec, boolean z) {
        ActivityResultCaller activityResultCaller = this.mFragment;
        if (activityResultCaller instanceof IResponsive) {
            ((IResponsive) activityResultCaller).onResponsiveLayout(configuration, screenSpec, z);
        }
    }

    public boolean ismSplitActionBarEnable() {
        return this.mSplitActionBarEnable;
    }

    private class InvalidateMenuRunnable implements Runnable {
        private InvalidateMenuRunnable() {
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r0v3, types: [android.view.Menu, miuix.appcompat.internal.view.menu.MenuBuilder] */
        /* JADX WARN: Type inference fix 'apply assigned field type' failed
        java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
        	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
        	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
        	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
         */
        @Override // java.lang.Runnable
        public void run() {
            if (FragmentDelegate.this.isEndActionMenuEnabled() || FragmentDelegate.this.ismSplitActionBarEnable()) {
                ?? CreateMenu = FragmentDelegate.this.createMenu();
                boolean zOnCreatePanelMenu = FragmentDelegate.this.onCreatePanelMenu(0, CreateMenu);
                if (zOnCreatePanelMenu) {
                    zOnCreatePanelMenu = FragmentDelegate.this.onPreparePanel(0, null, CreateMenu);
                }
                if (zOnCreatePanelMenu) {
                    FragmentDelegate.this.setMenu(CreateMenu);
                } else {
                    FragmentDelegate.this.setMenu(null);
                }
            } else {
                FragmentDelegate.this.setMenu(null);
            }
            FragmentDelegate.access$372(FragmentDelegate.this, -18);
        }
    }

    public void hideBottomMenu(boolean z) {
        View view = this.mSubDecor;
        if (view instanceof ActionBarOverlayLayout) {
            ((ActionBarOverlayLayout) view).hideBottomMenu(z);
        }
    }

    public void showBottomMenu(boolean z) {
        View view = this.mSubDecor;
        if (view instanceof ActionBarOverlayLayout) {
            ((ActionBarOverlayLayout) view).showBottomMenu(z);
        }
    }

    public void addGroupButtons(GroupButtonsConfig groupButtonsConfig, boolean z) {
        super.addGroupButtons(groupButtonsConfig);
        View view = this.mSubDecor;
        if (view instanceof ActionBarOverlayLayout) {
            ((ActionBarOverlayLayout) view).addGroupButtons(groupButtonsConfig, z);
        }
    }

    public void setGroupButtonsPanelBackground(Drawable drawable) {
        View view = this.mSubDecor;
        if (view instanceof ActionBarOverlayLayout) {
            ((ActionBarOverlayLayout) view).setGroupButtonsPanelBackground(drawable);
        }
    }

    public void setGroupButtonsPanelBackgroundColor(int i) {
        View view = this.mSubDecor;
        if (view instanceof ActionBarOverlayLayout) {
            ((ActionBarOverlayLayout) view).setGroupButtonsPanelBackgroundColor(i);
        }
    }

    public void setGroupButtonsPanelBackgroundResource(int i) {
        View view = this.mSubDecor;
        if (view instanceof ActionBarOverlayLayout) {
            ((ActionBarOverlayLayout) view).setGroupButtonsPanelBackgroundResource(i);
        }
    }

    public void setBottomMenuCustomView(View view) {
        View view2 = this.mSubDecor;
        if (view2 instanceof ActionBarOverlayLayout) {
            ((ActionBarOverlayLayout) view2).setBottomMenuCustomView(view);
        }
    }

    public void removeBottomMenuCustomView() {
        View view = this.mSubDecor;
        if (view instanceof ActionBarOverlayLayout) {
            ((ActionBarOverlayLayout) view).removeBottomMenuCustomView();
        }
    }

    public void showBottomMenuCustomView() {
        View view = this.mSubDecor;
        if (view instanceof ActionBarOverlayLayout) {
            ((ActionBarOverlayLayout) view).showBottomMenuCustomView();
        }
    }

    public void hideBottomMenuCustomView() {
        View view = this.mSubDecor;
        if (view instanceof ActionBarOverlayLayout) {
            ((ActionBarOverlayLayout) view).hideBottomMenuCustomView();
        }
    }

    public void setBottomMenuCustomViewTranslationYWithPx(int i) {
        View view = this.mSubDecor;
        if (view instanceof ActionBarOverlayLayout) {
            ((ActionBarOverlayLayout) view).setBottomMenuCustomViewTranslationYWithPx(i);
        }
    }

    public int getBottomMenuCustomViewTranslationY() {
        View view = this.mSubDecor;
        if (view instanceof ActionBarOverlayLayout) {
            return ((ActionBarOverlayLayout) view).getBottomMenuCustomViewTranslationY();
        }
        return 0;
    }

    public void setNestedScrollingParentEnabled(boolean z) {
        View view = this.mSubDecor;
        if (view instanceof ActionBarOverlayLayout) {
            ((ActionBarOverlayLayout) view).setNestedScrollingParentEnabled(z);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean updateExtraPaddingHorizontal(Context context, ExtraPaddingPolicy extraPaddingPolicy, int i, int i2) {
        Resources resources = context.getResources();
        WindowBaseInfo windowInfo = EnvStateManager.getWindowInfo(context, resources.getConfiguration());
        if (i == -1) {
            i = windowInfo.windowSize.x;
        }
        int i3 = i;
        if (i2 == -1) {
            i2 = windowInfo.windowSize.y;
        }
        float f = resources.getDisplayMetrics().density;
        extraPaddingPolicy.onContainerSizeChanged(windowInfo.windowSizeDp.x, windowInfo.windowSizeDp.y, i3, i2, f, false);
        return setExtraHorizontalPadding(extraPaddingPolicy.isEnable() ? (int) (extraPaddingPolicy.getExtraPaddingDp() * f) : 0);
    }
}
