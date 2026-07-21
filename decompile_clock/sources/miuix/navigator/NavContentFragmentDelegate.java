package miuix.navigator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.view.ScrollingView;
import androidx.fragment.app.Fragment;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.DelegateFragmentFactory;
import miuix.appcompat.app.FragmentDelegate;
import miuix.internal.util.AttributeResolver;
import miuix.internal.util.ViewUtils;
import miuix.view.ActionModeAnimationListener;
import miuix.view.EditActionMode;
import miuix.view.SearchActionMode;

/* JADX INFO: loaded from: classes3.dex */
class NavContentFragmentDelegate extends NavigatorFragmentDelegate {
    private ActionMode.Callback mContentActionModeWrapper;
    private SearchActionMode.Callback mSearchActionModeWrapper;

    public NavContentFragmentDelegate(final SubNavigator subNavigator, Fragment fragment) {
        super(subNavigator, fragment);
        this.mContentActionModeWrapper = null;
        this.mSearchActionModeWrapper = null;
        fragment.getChildFragmentManager().setFragmentFactory(new DelegateFragmentFactory() { // from class: miuix.navigator.NavContentFragmentDelegate.1
            @Override // miuix.appcompat.app.DelegateFragmentFactory
            public FragmentDelegate createFragmentDelegate(Fragment fragment2) {
                return new NavContentChildFragmentDelegate(subNavigator, fragment2);
            }
        });
    }

    @Override // miuix.appcompat.app.FragmentDelegate
    public void checkThemeLegality() {
        if (AttributeResolver.resolve(getThemedContext(), R.attr.isNavigatorContentTheme) < 0) {
            throw new IllegalStateException("You need to use a Theme.Light/Dark.Content theme (or descendant) with this fragment.");
        }
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setExtraThemeRes(AttributeResolver.resolve(getActivity(), R.attr.navigatorContentStyle));
    }

    @Override // miuix.appcompat.app.FragmentDelegate
    public Animator onCreateAnimator(int i, boolean z, int i2) {
        Animator animatorOnCreateAnimator = super.onCreateAnimator(i, z, i2);
        this.mIsDelegateAnimRunning = false;
        if (animatorOnCreateAnimator != null) {
            animatorOnCreateAnimator.addListener(new AnimatorListenerAdapter() { // from class: miuix.navigator.NavContentFragmentDelegate.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animator) {
                    super.onAnimationStart(animator);
                    NavContentFragmentDelegate.this.mIsDelegateAnimRunning = true;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animator) {
                    super.onAnimationCancel(animator);
                    NavContentFragmentDelegate.this.mIsDelegateAnimRunning = false;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    NavContentFragmentDelegate.this.mIsDelegateAnimRunning = false;
                }
            });
        }
        return animatorOnCreateAnimator;
    }

    @Override // miuix.navigator.NavigatorFragmentDelegate, miuix.appcompat.app.FragmentDelegate
    public void onViewCreated(View view, Bundle bundle) {
        ActionBar actionBar = getActionBar();
        if (actionBar == null) {
            super.onViewCreated(view, bundle);
            return;
        }
        actionBar.setDisplayOptions(8192, 8192);
        super.onViewCreated(view, bundle);
        Navigator.Mode navigationMode = getNavigator().getNavigationMode();
        if (navigationMode == Navigator.Mode.LC || navigationMode == Navigator.Mode.NLC) {
            setBottomMenuMode(2);
        }
    }

    @Override // miuix.navigator.NavigatorFragmentDelegate, miuix.appcompat.app.FragmentDelegate, miuix.appcompat.app.ActionBarDelegateImpl, miuix.appcompat.app.ActionBarDelegate
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        Navigator.Mode navigationMode = getNavigator().getNavigationMode();
        if (navigationMode == Navigator.Mode.LC || navigationMode == Navigator.Mode.NLC) {
            setBottomMenuMode(2);
        }
    }

    @Override // miuix.navigator.NavigatorFragmentDelegate, miuix.appcompat.app.FragmentDelegate
    public void onDestroyView() {
        getNavigator().getBaseNavigator().removeContentSwitch(getView().findViewById(R.id.navigator_switch));
        super.onDestroyView();
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl, miuix.appcompat.app.IContentInsetState
    public void onProcessBindViewWithContentInset(Rect rect) {
        if (isIsInSearchActionMode() || this.mViewWithContentInset == null) {
            return;
        }
        ViewUtils.RelativePadding relativePadding = new ViewUtils.RelativePadding(this.mViewWithContentInsetInitPadding);
        boolean zIsLayoutRtl = ViewUtils.isLayoutRtl(this.mViewWithContentInset);
        relativePadding.start += zIsLayoutRtl ? rect.right : rect.left;
        relativePadding.top += rect.top;
        relativePadding.end += zIsLayoutRtl ? rect.left : rect.right;
        relativePadding.applyToView(this.mViewWithContentInset);
        if ((this.mViewWithContentInset instanceof ViewGroup) && (this.mViewWithContentInset instanceof ScrollingView)) {
            relativePadding.applyToScrollingView((ViewGroup) this.mViewWithContentInset);
        } else {
            relativePadding.applyToView(this.mViewWithContentInset);
        }
    }

    @Override // miuix.appcompat.app.FragmentDelegate, miuix.appcompat.app.ActionBarDelegateImpl, miuix.appcompat.app.ActionBarDelegate
    public ActionMode startActionMode(final ActionMode.Callback callback) {
        if (callback == null) {
            return super.startActionMode(callback);
        }
        if (callback instanceof SearchActionMode.Callback) {
            if (this.mSearchActionModeWrapper == null) {
                this.mSearchActionModeWrapper = new SearchActionMode.Callback() { // from class: miuix.navigator.NavContentFragmentDelegate.3
                    /* JADX WARN: Multi-variable type inference failed */
                    @Override // android.view.ActionMode.Callback
                    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                        ((SearchActionMode) actionMode).addAnimationListener(new ActionModeAnimationListener() { // from class: miuix.navigator.NavContentFragmentDelegate.3.1
                            @Override // miuix.view.ActionModeAnimationListener
                            public void onStop(boolean z) {
                                if (z) {
                                    return;
                                }
                                NavContentFragmentDelegate.this.mIsInSearchActionMode = false;
                            }
                        });
                        return callback.onCreateActionMode(actionMode, menu);
                    }

                    @Override // android.view.ActionMode.Callback
                    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                        return callback.onPrepareActionMode(actionMode, menu);
                    }

                    @Override // android.view.ActionMode.Callback
                    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                        return callback.onActionItemClicked(actionMode, menuItem);
                    }

                    @Override // android.view.ActionMode.Callback
                    public void onDestroyActionMode(ActionMode actionMode) {
                        NavContentFragmentDelegate.this.mSearchActionModeWrapper = null;
                        NavContentFragmentDelegate.this.getNavigator().getBaseNavigator().showBottomTab();
                        callback.onDestroyActionMode(actionMode);
                    }
                };
            }
            this.mIsInSearchActionMode = true;
            getNavigator().getBaseNavigator().hideBottomTab(false);
            ActionMode actionModeStartActionMode = super.startActionMode(this.mSearchActionModeWrapper);
            if (actionModeStartActionMode == null) {
                this.mSearchActionModeWrapper = null;
            }
            return actionModeStartActionMode;
        }
        if (this.mContentActionModeWrapper == null) {
            ActionMode.Callback callback2 = new ActionMode.Callback() { // from class: miuix.navigator.NavContentFragmentDelegate.4
                @Override // android.view.ActionMode.Callback
                public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                    NavContentFragmentDelegate.this.mIsInEditActionMode = true;
                    return callback.onCreateActionMode(actionMode, menu);
                }

                @Override // android.view.ActionMode.Callback
                public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                    return callback.onPrepareActionMode(actionMode, menu);
                }

                @Override // android.view.ActionMode.Callback
                public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                    return callback.onActionItemClicked(actionMode, menuItem);
                }

                @Override // android.view.ActionMode.Callback
                public void onDestroyActionMode(ActionMode actionMode) {
                    NavContentFragmentDelegate.this.mIsInEditActionMode = false;
                    NavContentFragmentDelegate.this.mContentActionModeWrapper = null;
                    NavContentFragmentDelegate.this.getNavigator().getBaseNavigator().showBottomTab();
                    callback.onDestroyActionMode(actionMode);
                }
            };
            this.mContentActionModeWrapper = callback2;
            ActionMode actionModeStartActionMode2 = super.startActionMode(callback2);
            if (actionModeStartActionMode2 != null) {
                getNavigator().getBaseNavigator().hideBottomTab(getBottomMenuMode() == 2);
            } else {
                this.mContentActionModeWrapper = null;
            }
            return actionModeStartActionMode2;
        }
        return super.startActionMode(callback);
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl, miuix.appcompat.app.ActionBarDelegate
    public void onActionModeStarted(ActionMode actionMode) {
        super.onActionModeStarted(actionMode);
        if (actionMode instanceof EditActionMode) {
            getNavigator().requestUserFocus(true);
        } else {
            getNavigator().requestFocus();
        }
        Fragment fragmentFindFragmentByTag = getNavigator().getBaseNavigator().mSecondaryContentSub.getFragmentManager().findFragmentByTag(Navigator.TAG_SECONDARY_CONTENT);
        if (fragmentFindFragmentByTag instanceof miuix.appcompat.app.Fragment) {
            miuix.appcompat.app.Fragment fragment = (miuix.appcompat.app.Fragment) fragmentFindFragmentByTag;
            if (fragment.getDelegate().getActionMode() instanceof EditActionMode) {
                fragment.getDelegate().getActionMode().finish();
            }
        }
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl, miuix.appcompat.app.ActionBarDelegate
    public void onActionModeFinished(ActionMode actionMode) {
        super.onActionModeFinished(actionMode);
        if (getNavigator().isUserFocused()) {
            getNavigator().requestUserFocus(false);
        }
    }
}
