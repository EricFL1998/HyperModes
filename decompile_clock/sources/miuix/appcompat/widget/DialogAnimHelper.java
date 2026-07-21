package miuix.appcompat.widget;

import android.view.View;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.widget.dialoganim.IDialogAnim;
import miuix.appcompat.widget.dialoganim.PadDialogAnim;
import miuix.appcompat.widget.dialoganim.PhoneDialogAnim;

/* JADX INFO: loaded from: classes2.dex */
public class DialogAnimHelper {
    private IDialogAnim mDialogAnim;
    private boolean mDiscardImeAnimEnabled = false;

    public interface OnDismiss {
        void end();
    }

    public void executeShowAnim(View view, View view2, boolean z, boolean z2, AlertDialog.OnDialogShowAnimListener onDialogShowAnimListener) {
        if (this.mDialogAnim == null) {
            if (z) {
                this.mDialogAnim = new PadDialogAnim();
            } else {
                PhoneDialogAnim phoneDialogAnim = new PhoneDialogAnim();
                this.mDialogAnim = phoneDialogAnim;
                phoneDialogAnim.setDiscardImeAnimEnabled(this.mDiscardImeAnimEnabled);
            }
        }
        this.mDialogAnim.executeShowAnim(view, view2, z2, onDialogShowAnimListener);
    }

    public void setDiscardImeAnimEnabled(boolean z) {
        this.mDiscardImeAnimEnabled = z;
    }

    public void cancelAnimator() {
        IDialogAnim iDialogAnim = this.mDialogAnim;
        if (iDialogAnim != null) {
            iDialogAnim.cancelAnimator();
        }
    }

    public void executeDismissAnim(View view, boolean z, View view2, OnDismiss onDismiss) {
        if (this.mDialogAnim == null) {
            if (z) {
                this.mDialogAnim = new PadDialogAnim();
            } else {
                this.mDialogAnim = new PhoneDialogAnim();
            }
        }
        this.mDialogAnim.executeDismissAnim(view, view2, onDismiss);
        this.mDialogAnim = null;
    }
}
