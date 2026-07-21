package miuix.appcompat.internal.view;

import android.content.Context;
import android.graphics.Rect;
import android.view.ActionMode;
import android.view.View;
import android.widget.EditText;
import miuix.appcompat.internal.app.widget.SearchActionModeView;
import miuix.view.ActionModeAnimationListener;
import miuix.view.SearchActionMode;

/* JADX INFO: loaded from: classes2.dex */
public class SearchActionModeImpl extends ActionModeImpl implements SearchActionMode {
    public SearchActionModeImpl(Context context, ActionMode.Callback callback) {
        super(context, callback);
    }

    @Override // miuix.view.SearchActionMode
    public void setAnchorView(View view) {
        ((SearchActionModeView) this.mActionModeView.get()).setAnchorView(view);
    }

    @Override // miuix.view.SearchActionMode
    public void setAnimateView(View view) {
        ((SearchActionModeView) this.mActionModeView.get()).setAnimateView(view);
    }

    @Override // miuix.view.SearchActionMode
    public void setResultView(View view) {
        ((SearchActionModeView) this.mActionModeView.get()).setResultView(view);
    }

    @Override // miuix.appcompat.internal.view.ActionModeImpl, android.view.ActionMode
    public void setCustomView(View view) {
        ((SearchActionModeView) this.mActionModeView.get()).setCustomView(view);
    }

    @Override // miuix.view.SearchActionMode
    public void resetCustomView() {
        ((SearchActionModeView) this.mActionModeView.get()).resetCustomView();
    }

    @Override // miuix.appcompat.internal.view.ActionModeImpl, android.view.ActionMode
    public View getCustomView() {
        return ((SearchActionModeView) this.mActionModeView.get()).getCustomView();
    }

    @Override // miuix.view.SearchActionMode
    public EditText getSearchInput() {
        return ((SearchActionModeView) this.mActionModeView.get()).getSearchInput();
    }

    @Override // miuix.view.SearchActionMode
    public void addAnimationListener(ActionModeAnimationListener actionModeAnimationListener) {
        this.mActionModeView.get().addAnimationListener(actionModeAnimationListener);
    }

    @Override // miuix.view.SearchActionMode
    public void removeAnimationListener(ActionModeAnimationListener actionModeAnimationListener) {
        this.mActionModeView.get().removeAnimationListener(actionModeAnimationListener);
    }

    @Override // miuix.view.SearchActionMode
    public void setAnimatedViewListener(SearchActionMode.AnimatedViewListener animatedViewListener) {
        ((SearchActionModeView) this.mActionModeView.get()).setAnimatedViewListener(animatedViewListener);
    }

    @Override // miuix.view.SearchActionMode
    public void setAnchorApplyExtraPaddingByUser(boolean z) {
        ((SearchActionModeView) this.mActionModeView.get()).setAnchorApplyExtraPaddingByUser(z);
    }

    @Override // miuix.view.SearchActionMode
    public void setFitWindowInsetsEnabled(boolean z) {
        ((SearchActionModeView) this.mActionModeView.get()).setFitWindowInsetsEnabled(z);
    }

    public void setPendingInsets(Rect rect) {
        SearchActionModeView searchActionModeView = this.mActionModeView != null ? (SearchActionModeView) this.mActionModeView.get() : null;
        if (searchActionModeView != null) {
            searchActionModeView.rePaddingAndRelayout(rect);
        }
    }
}
