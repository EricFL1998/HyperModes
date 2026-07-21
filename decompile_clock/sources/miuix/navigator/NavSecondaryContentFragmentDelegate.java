package miuix.navigator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.View;
import androidx.fragment.app.Fragment;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.strategy.CommonActionBarStrategy;
import miuix.internal.util.AttributeResolver;
import miuix.navigator.app.SecondaryContentActionBarStrategy;
import miuix.view.EditActionMode;

/* JADX INFO: loaded from: classes3.dex */
class NavSecondaryContentFragmentDelegate extends NavigatorFragmentDelegate {
    public NavSecondaryContentFragmentDelegate(SubNavigator subNavigator, Fragment fragment) {
        super(subNavigator, fragment);
    }

    @Override // miuix.appcompat.app.FragmentDelegate
    public void checkThemeLegality() {
        if (AttributeResolver.resolve(getThemedContext(), R.attr.isNavigatorSecondaryContentTheme) < 0) {
            throw new IllegalStateException("You need to use a Theme.Light/Dark.SecondaryContent theme (or descendant) with this fragment.");
        }
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setExtraThemeRes(AttributeResolver.resolve(getActivity(), R.attr.navigatorSecondaryContentStyle));
    }

    @Override // miuix.appcompat.app.FragmentDelegate
    public Animator onCreateAnimator(int i, boolean z, int i2) {
        Animator animatorOnCreateAnimator = super.onCreateAnimator(i, z, i2);
        this.mIsDelegateAnimRunning = false;
        if (animatorOnCreateAnimator != null) {
            animatorOnCreateAnimator.addListener(new AnimatorListenerAdapter() { // from class: miuix.navigator.NavSecondaryContentFragmentDelegate.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animator) {
                    super.onAnimationStart(animator);
                    NavSecondaryContentFragmentDelegate.this.mIsDelegateAnimRunning = true;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animator) {
                    super.onAnimationCancel(animator);
                    NavSecondaryContentFragmentDelegate.this.mIsDelegateAnimRunning = false;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    NavSecondaryContentFragmentDelegate.this.mIsDelegateAnimRunning = false;
                }
            });
        }
        if (z) {
            if (animatorOnCreateAnimator != null) {
                animatorOnCreateAnimator.addListener(new AnimatorListenerAdapter() { // from class: miuix.navigator.NavSecondaryContentFragmentDelegate.2
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        NavSecondaryContentFragmentDelegate.this.setSecondaryContentReady();
                    }
                });
            } else {
                setSecondaryContentReady();
            }
        }
        return animatorOnCreateAnimator;
    }

    @Override // miuix.navigator.NavigatorFragmentDelegate, miuix.appcompat.app.FragmentDelegate
    public void onViewCreated(View view, Bundle bundle) {
        updateActionBarOptions();
        super.onViewCreated(view, bundle);
    }

    @Override // miuix.navigator.NavigatorFragmentDelegate, miuix.appcompat.app.FragmentDelegate, miuix.appcompat.app.ActionBarDelegateImpl, miuix.appcompat.app.ActionBarDelegate
    public void onConfigurationChanged(Configuration configuration) {
        updateActionBarOptions();
        super.onConfigurationChanged(configuration);
    }

    private void updateActionBarOptions() {
        ActionBar actionBar = getActionBar();
        if (actionBar == null) {
            return;
        }
        SubNavigator navigator = getNavigator();
        if (navigator != null) {
            Navigator.Mode navigationMode = navigator.getNavigationMode();
            if (navigationMode == Navigator.Mode.C || navigationMode == Navigator.Mode.NC) {
                int displayOptions = actionBar.getDisplayOptions();
                int i = displayOptions | 4;
                if (navigationMode == Navigator.Mode.NC) {
                    i = displayOptions | 8196;
                }
                actionBar.setDisplayOptions(i);
                actionBar.setActionBarStrategy(new CommonActionBarStrategy());
                return;
            }
            if (!(actionBar.getActionBarStrategy() instanceof SecondaryContentActionBarStrategy)) {
                actionBar.setActionBarStrategy(new SecondaryContentActionBarStrategy());
            }
            if (navigator.canPopSecondaryContent()) {
                return;
            }
            actionBar.setDisplayOptions(actionBar.getDisplayOptions() & (-5));
            return;
        }
        actionBar.setDisplayOptions(actionBar.getDisplayOptions() | 4);
    }

    @Override // miuix.navigator.NavigatorFragmentDelegate, miuix.appcompat.app.FragmentDelegate
    public void onDestroyView() {
        getNavigator().getBaseNavigator().removeSecondaryContentSwitch(getView().findViewById(R.id.navigator_switch));
        super.onDestroyView();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSecondaryContentReady() {
        getNavigator().getBaseNavigator().getNavigationView().setSecondaryContentReady(true);
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl, miuix.appcompat.app.ActionBarDelegate
    public void onActionModeStarted(ActionMode actionMode) {
        super.onActionModeStarted(actionMode);
        if (actionMode instanceof EditActionMode) {
            getNavigator().requestUserFocus(true);
        } else {
            getNavigator().requestFocus();
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
