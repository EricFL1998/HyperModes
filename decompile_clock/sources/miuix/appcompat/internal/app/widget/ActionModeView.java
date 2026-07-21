package miuix.appcompat.internal.app.widget;

import android.view.ActionMode;
import miuix.view.ActionModeAnimationListener;

/* JADX INFO: loaded from: classes2.dex */
public interface ActionModeView {
    public static final int ANIMATION_DURATION = 300;

    void addAnimationListener(ActionModeAnimationListener actionModeAnimationListener);

    void animateToVisibility(boolean z);

    void closeMode();

    int getViewHeight();

    void initForMode(ActionMode actionMode);

    void killMode();

    void notifyAnimationEnd(boolean z);

    void notifyAnimationStart(boolean z);

    void notifyAnimationUpdate(boolean z, float f);

    void removeAnimationListener(ActionModeAnimationListener actionModeAnimationListener);
}
