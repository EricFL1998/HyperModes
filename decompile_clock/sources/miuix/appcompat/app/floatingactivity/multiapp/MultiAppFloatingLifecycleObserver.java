package miuix.appcompat.app.floatingactivity.multiapp;

import android.view.View;
import android.view.ViewGroup;
import miuix.animation.base.AnimConfig;
import miuix.animation.listener.TransitionListener;
import miuix.appcompat.app.AppCompatActivity;
import miuix.appcompat.app.floatingactivity.FloatingAnimHelper;
import miuix.appcompat.app.floatingactivity.FloatingLifecycleObserver;
import miuix.appcompat.app.floatingactivity.FloatingSwitcherAnimHelper;

/* JADX INFO: loaded from: classes2.dex */
public class MultiAppFloatingLifecycleObserver extends FloatingLifecycleObserver {
    public MultiAppFloatingLifecycleObserver(AppCompatActivity appCompatActivity) {
        super(appCompatActivity);
    }

    @Override // miuix.appcompat.app.floatingactivity.FloatingLifecycleObserver
    public void onDestroy() {
        MultiAppFloatingActivitySwitcher multiAppFloatingActivitySwitcher = MultiAppFloatingActivitySwitcher.getInstance();
        if (multiAppFloatingActivitySwitcher != null) {
            multiAppFloatingActivitySwitcher.clearActivitySpecTask(getActivityTaskId(), getActivityIdentity());
            multiAppFloatingActivitySwitcher.remove(getActivityTaskId(), getActivityIdentity());
            if (multiAppFloatingActivitySwitcher.getCurrentPageCount(getActivityTaskId()) <= 0) {
                multiAppFloatingActivitySwitcher.setLastActivityPanel(null);
            }
        }
    }

    @Override // miuix.appcompat.app.floatingactivity.FloatingLifecycleObserver
    public void onCreate() {
        final AppCompatActivity activity;
        MultiAppFloatingActivitySwitcher multiAppFloatingActivitySwitcher = MultiAppFloatingActivitySwitcher.getInstance();
        if (multiAppFloatingActivitySwitcher == null || (activity = multiAppFloatingActivitySwitcher.getActivity(getActivityTaskId(), getActivityIdentity())) == null) {
            return;
        }
        multiAppFloatingActivitySwitcher.postEnterAnimationTask(getActivityTaskId(), getActivityIdentity(), new Runnable() { // from class: miuix.appcompat.app.floatingactivity.multiapp.MultiAppFloatingLifecycleObserver.1
            @Override // java.lang.Runnable
            public void run() {
                MultiAppFloatingActivitySwitcher multiAppFloatingActivitySwitcher2 = MultiAppFloatingActivitySwitcher.getInstance();
                if (multiAppFloatingActivitySwitcher2 != null) {
                    if (multiAppFloatingActivitySwitcher2.getCurrentPageCount(MultiAppFloatingLifecycleObserver.this.getActivityTaskId()) > 1 || multiAppFloatingActivitySwitcher2.getServicePageCount(MultiAppFloatingLifecycleObserver.this.getActivityTaskId()) > 1) {
                        if (FloatingAnimHelper.isSupportTransWithClipAnim()) {
                            AppCompatActivity appCompatActivity = activity;
                            FloatingAnimHelper.preformFloatingExitAnimWithClip(appCompatActivity, appCompatActivity.isInFloatingWindowMode());
                        } else if (activity.isInFloatingWindowMode()) {
                            activity.executeOpenEnterAnimation();
                            multiAppFloatingActivitySwitcher2.notifyPreviousActivitySlide(MultiAppFloatingLifecycleObserver.this.getActivityTaskId(), MultiAppFloatingLifecycleObserver.this.getActivityIdentity());
                        }
                    }
                }
            }
        });
        execEnterNotInFloatingWindowMode(activity);
    }

    private void execEnterNotInFloatingWindowMode(AppCompatActivity appCompatActivity) {
        int iObtainPageIndex = FloatingAnimHelper.obtainPageIndex(appCompatActivity);
        boolean z = iObtainPageIndex >= 0 && !appCompatActivity.isInFloatingWindowMode();
        MultiAppFloatingActivitySwitcher multiAppFloatingActivitySwitcher = MultiAppFloatingActivitySwitcher.getInstance();
        if (multiAppFloatingActivitySwitcher != null) {
            if (!z || iObtainPageIndex != 0) {
                if (z) {
                    multiAppFloatingActivitySwitcher.markActivityOpenEnterAnimExecutedInternal(appCompatActivity.getTaskId(), appCompatActivity.getActivityIdentity());
                }
            } else {
                multiAppFloatingActivitySwitcher.markActivityOpenEnterAnimExecutedInternal(appCompatActivity.getTaskId(), appCompatActivity.getActivityIdentity());
                if (FloatingAnimHelper.isSupportTransWithClipAnim()) {
                    FloatingAnimHelper.preformFloatingExitAnimWithClip(appCompatActivity, false);
                } else {
                    FloatingAnimHelper.execFloatingWindowEnterAnimRomNormal(appCompatActivity);
                }
            }
        }
    }

    @Override // miuix.appcompat.app.floatingactivity.FloatingLifecycleObserver
    public void onResume() {
        AppCompatActivity activity;
        MultiAppFloatingActivitySwitcher multiAppFloatingActivitySwitcher = MultiAppFloatingActivitySwitcher.getInstance();
        if (multiAppFloatingActivitySwitcher == null || (activity = multiAppFloatingActivitySwitcher.getActivity(getActivityTaskId(), getActivityIdentity())) == null) {
            return;
        }
        multiAppFloatingActivitySwitcher.updateResumeState(getActivityTaskId(), getActivityIdentity(), true);
        multiAppFloatingActivitySwitcher.checkBg(getActivityTaskId(), getActivityIdentity());
        if (!multiAppFloatingActivitySwitcher.isAboveActivityFinishing(getActivityTaskId(), getActivityIdentity()) || FloatingAnimHelper.isSupportTransWithClipAnim()) {
            return;
        }
        activity.executeCloseEnterAnimation();
        executeCloseExit(activity);
    }

    private void executeCloseExit(AppCompatActivity appCompatActivity) {
        final View lastActivityPanel;
        MultiAppFloatingActivitySwitcher multiAppFloatingActivitySwitcher = MultiAppFloatingActivitySwitcher.getInstance();
        if (multiAppFloatingActivitySwitcher == null || (lastActivityPanel = multiAppFloatingActivitySwitcher.getLastActivityPanel()) == null) {
            return;
        }
        final View floatingBrightPanel = appCompatActivity.getFloatingBrightPanel();
        lastActivityPanel.post(new Runnable() { // from class: miuix.appcompat.app.floatingactivity.multiapp.MultiAppFloatingLifecycleObserver.2
            @Override // java.lang.Runnable
            public void run() {
                View childAt = ((ViewGroup) lastActivityPanel).getChildAt(0);
                AnimConfig animConfig = FloatingSwitcherAnimHelper.getAnimConfig(0, null);
                animConfig.addListeners(new TransitionListener() { // from class: miuix.appcompat.app.floatingactivity.multiapp.MultiAppFloatingLifecycleObserver.2.1
                    @Override // miuix.animation.listener.TransitionListener
                    public void onComplete(Object obj) {
                        super.onComplete(obj);
                        ((ViewGroup) floatingBrightPanel.getParent()).getOverlay().remove(lastActivityPanel);
                        MultiAppFloatingActivitySwitcher multiAppFloatingActivitySwitcher2 = MultiAppFloatingActivitySwitcher.getInstance();
                        if (multiAppFloatingActivitySwitcher2 != null) {
                            multiAppFloatingActivitySwitcher2.setLastActivityPanel(null);
                        }
                    }
                });
                FloatingSwitcherAnimHelper.executeCloseExitAnimation(childAt, animConfig);
            }
        });
    }

    @Override // miuix.appcompat.app.floatingactivity.FloatingLifecycleObserver
    public void onPause() {
        MultiAppFloatingActivitySwitcher multiAppFloatingActivitySwitcher = MultiAppFloatingActivitySwitcher.getInstance();
        if (multiAppFloatingActivitySwitcher != null) {
            multiAppFloatingActivitySwitcher.updateResumeState(getActivityTaskId(), getActivityIdentity(), false);
        }
    }
}
