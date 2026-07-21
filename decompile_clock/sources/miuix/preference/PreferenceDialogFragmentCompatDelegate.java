package miuix.preference;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.preference.DialogPreference;
import androidx.preference.PreferenceDialogFragmentCompat;
import miuix.appcompat.app.AlertDialog;

/* JADX INFO: loaded from: classes3.dex */
class PreferenceDialogFragmentCompatDelegate {
    private PreferenceDialogFragmentCompat mFragmentCompat;
    private IPreferenceDialogFragment mInternal;

    public PreferenceDialogFragmentCompatDelegate(IPreferenceDialogFragment iPreferenceDialogFragment, PreferenceDialogFragmentCompat preferenceDialogFragmentCompat) {
        this.mInternal = iPreferenceDialogFragment;
        this.mFragmentCompat = preferenceDialogFragmentCompat;
    }

    public Dialog onCreateDialog(Bundle bundle) {
        Context context = this.mFragmentCompat.getContext();
        DialogPreference preference = this.mFragmentCompat.getPreference();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (preference == null) {
            Log.w("PreferenceDialogFragmentCompatDelegate", "Associated preference is null. Cannot create a valid dialog.");
        } else {
            BuilderDelegate builderDelegate = new BuilderDelegate(context, builder);
            builderDelegate.setTitle(preference.getDialogTitle());
            builderDelegate.setIcon(preference.getDialogIcon());
            builderDelegate.setPositiveButton(preference.getPositiveButtonText(), this.mFragmentCompat);
            builderDelegate.setNegativeButton(preference.getNegativeButtonText(), this.mFragmentCompat);
            View viewOnCreateDialogView = this.mInternal.onCreateDialogView(context);
            if (viewOnCreateDialogView != null) {
                this.mInternal.onBindDialogView(viewOnCreateDialogView);
                builderDelegate.setView(viewOnCreateDialogView);
            } else {
                builderDelegate.setMessage(preference.getDialogMessage());
            }
            this.mInternal.onPrepareDialogBuilder(builder);
        }
        AlertDialog alertDialogCreate = builder.create();
        if (this.mInternal.needInputMethod()) {
            requestInputMethod(alertDialogCreate);
        }
        return alertDialogCreate;
    }

    private void requestInputMethod(Dialog dialog) {
        dialog.getWindow().setSoftInputMode(5);
    }
}
