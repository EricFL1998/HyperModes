package miuix.preference;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import miuix.appcompat.app.AlertDialog;

/* JADX INFO: loaded from: classes3.dex */
public class EditTextPreferenceDialogFragmentCompat extends androidx.preference.EditTextPreferenceDialogFragmentCompat {
    private IPreferenceDialogFragment mImpl = new IPreferenceDialogFragment() { // from class: miuix.preference.EditTextPreferenceDialogFragmentCompat.1
        @Override // miuix.preference.IPreferenceDialogFragment
        public boolean needInputMethod() {
            return true;
        }

        @Override // miuix.preference.IPreferenceDialogFragment
        public View onCreateDialogView(Context context) {
            return EditTextPreferenceDialogFragmentCompat.this.onCreateDialogView(context);
        }

        @Override // miuix.preference.IPreferenceDialogFragment
        public void onPrepareDialogBuilder(AlertDialog.Builder builder) {
            EditTextPreferenceDialogFragmentCompat.this.onPrepareDialogBuilder(builder);
        }

        @Override // miuix.preference.IPreferenceDialogFragment
        public void onBindDialogView(View view) {
            EditTextPreferenceDialogFragmentCompat.this.onBindDialogView(view);
        }
    };
    private PreferenceDialogFragmentCompatDelegate mDelegate = new PreferenceDialogFragmentCompatDelegate(this.mImpl, this);

    public static EditTextPreferenceDialogFragmentCompat newInstance(String str) {
        EditTextPreferenceDialogFragmentCompat editTextPreferenceDialogFragmentCompat = new EditTextPreferenceDialogFragmentCompat();
        Bundle bundle = new Bundle(1);
        bundle.putString("key", str);
        editTextPreferenceDialogFragmentCompat.setArguments(bundle);
        return editTextPreferenceDialogFragmentCompat;
    }

    @Override // androidx.preference.PreferenceDialogFragmentCompat, androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        return this.mDelegate.onCreateDialog(bundle);
    }

    @Override // androidx.preference.PreferenceDialogFragmentCompat
    protected final void onPrepareDialogBuilder(androidx.appcompat.app.AlertDialog.Builder builder) {
        throw new UnsupportedOperationException("using miuix builder instead");
    }

    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(new BuilderDelegate(getContext(), builder));
    }
}
