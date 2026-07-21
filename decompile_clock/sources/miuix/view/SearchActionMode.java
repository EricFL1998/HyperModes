package miuix.view;

import android.view.ActionMode;
import android.view.View;
import android.widget.EditText;

/* JADX INFO: loaded from: classes3.dex */
public interface SearchActionMode {

    public interface AnimatedViewListener {
        void onInSearchMode(boolean z);

        void onUpdateOffsetY(int i);
    }

    public interface Callback extends ActionMode.Callback {
    }

    void addAnimationListener(ActionModeAnimationListener actionModeAnimationListener);

    View getCustomView();

    EditText getSearchInput();

    void removeAnimationListener(ActionModeAnimationListener actionModeAnimationListener);

    void resetCustomView();

    void setAnchorApplyExtraPaddingByUser(boolean z);

    void setAnchorView(View view);

    void setAnimateView(View view);

    void setAnimatedViewListener(AnimatedViewListener animatedViewListener);

    void setCustomView(View view);

    void setFitWindowInsetsEnabled(boolean z);

    void setResultView(View view);
}
