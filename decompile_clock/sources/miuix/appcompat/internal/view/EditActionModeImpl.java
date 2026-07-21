package miuix.appcompat.internal.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.View;
import miuix.appcompat.R;
import miuix.appcompat.internal.app.widget.ActionBarContextView;
import miuix.appcompat.internal.app.widget.ActionModeView;
import miuix.view.ActionModeAnimationListener;
import miuix.view.EditActionMode;

/* JADX INFO: loaded from: classes2.dex */
public class EditActionModeImpl extends ActionModeImpl implements EditActionMode {
    private boolean mAnnounceAccessibilityEnabled;
    private String mFinishEditActionModeDescription;
    private String mStartEditActionModeDescription;

    @Override // miuix.appcompat.internal.view.ActionModeImpl, android.view.ActionMode
    public void setCustomView(View view) {
    }

    @Override // miuix.appcompat.internal.view.ActionModeImpl, android.view.ActionMode
    public void setSubtitle(int i) {
    }

    @Override // miuix.appcompat.internal.view.ActionModeImpl, android.view.ActionMode
    public void setSubtitle(CharSequence charSequence) {
    }

    public EditActionModeImpl(Context context, ActionMode.Callback callback) {
        super(context, callback);
        this.mAnnounceAccessibilityEnabled = true;
    }

    @Override // miuix.appcompat.internal.view.ActionModeImpl, android.view.ActionMode
    public void setTitle(CharSequence charSequence) {
        ((ActionBarContextView) this.mActionModeView.get()).setTitle(charSequence);
    }

    @Override // miuix.appcompat.internal.view.ActionModeImpl, android.view.ActionMode
    public void setTitle(int i) {
        setTitle(this.mContext.getResources().getString(i));
    }

    @Override // miuix.appcompat.internal.view.ActionModeImpl, android.view.ActionMode
    public CharSequence getTitle() {
        return ((ActionBarContextView) this.mActionModeView.get()).getTitle();
    }

    @Override // miuix.view.EditActionMode
    public void setButton(int i, CharSequence charSequence) {
        ((ActionBarContextView) this.mActionModeView.get()).setButton(i, charSequence);
    }

    @Override // miuix.view.EditActionMode
    public void setButton(int i, CharSequence charSequence, CharSequence charSequence2, int i2) {
        ((ActionBarContextView) this.mActionModeView.get()).setButton(i, charSequence, charSequence2, i2);
    }

    @Override // miuix.view.EditActionMode
    public void setButton(int i, CharSequence charSequence, int i2, CharSequence charSequence2, int i3) {
        ((ActionBarContextView) this.mActionModeView.get()).setButton(i, charSequence, i2, charSequence2, i3);
    }

    @Override // miuix.view.EditActionMode
    public void setButton(int i, int i2) {
        setButton(i, this.mContext.getResources().getString(i2));
    }

    @Override // miuix.view.EditActionMode
    public void setButton(int i, CharSequence charSequence, int i2) {
        ((ActionBarContextView) this.mActionModeView.get()).setButton(i, charSequence, i2);
    }

    @Override // miuix.view.EditActionMode
    public void setButton(int i, int i2, int i3) {
        setButton(i, this.mContext.getResources().getString(i2), i3);
    }

    @Override // miuix.view.EditActionMode
    public void setAnnounceAccessibilityEnabled(boolean z) {
        this.mAnnounceAccessibilityEnabled = z;
    }

    @Override // miuix.view.EditActionMode
    public void setStartEditActionModeDescription(int i) {
        this.mStartEditActionModeDescription = this.mContext.getResources().getString(i);
    }

    @Override // miuix.view.EditActionMode
    public void setFinishEditActionModeDescription(int i) {
        this.mFinishEditActionModeDescription = this.mContext.getResources().getString(i);
    }

    @Override // miuix.view.EditActionMode
    public void addAnimationListener(ActionModeAnimationListener actionModeAnimationListener) {
        this.mActionModeView.get().addAnimationListener(actionModeAnimationListener);
    }

    @Override // miuix.view.EditActionMode
    public void removeAnimationListener(ActionModeAnimationListener actionModeAnimationListener) {
        this.mActionModeView.get().removeAnimationListener(actionModeAnimationListener);
    }

    @Override // miuix.appcompat.internal.view.ActionModeImpl
    public boolean dispatchOnCreate() {
        boolean zDispatchOnCreate = super.dispatchOnCreate();
        if (this.mAnnounceAccessibilityEnabled && zDispatchOnCreate) {
            announceAccessibilityEvent(getStartEditActionModeDescription());
        }
        return zDispatchOnCreate;
    }

    private String getStartEditActionModeDescription() {
        if (TextUtils.isEmpty(this.mStartEditActionModeDescription)) {
            return this.mContext.getResources().getString(R.string.miuix_appcompat_accessibility_start_edit_action_mode);
        }
        return this.mStartEditActionModeDescription;
    }

    private String getFinishEditActionModeDescription() {
        if (TextUtils.isEmpty(this.mFinishEditActionModeDescription)) {
            return this.mContext.getResources().getString(R.string.miuix_appcompat_accessibility_finish_edit_action_mode);
        }
        return this.mFinishEditActionModeDescription;
    }

    @Override // miuix.view.EditActionMode
    public void announceAccessibilityEvent(String str) {
        ActionModeView actionModeView = this.mActionModeView.get();
        if (actionModeView instanceof ActionBarContextView) {
            ((ActionBarContextView) actionModeView).announceForAccessibility(str);
        }
    }

    @Override // miuix.appcompat.internal.view.ActionModeImpl, android.view.ActionMode
    public void finish() {
        super.finish();
        if (this.mAnnounceAccessibilityEnabled) {
            announceAccessibilityEvent(getFinishEditActionModeDescription());
        }
    }
}
