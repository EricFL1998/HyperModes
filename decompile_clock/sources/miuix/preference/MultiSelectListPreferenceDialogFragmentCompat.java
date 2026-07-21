package miuix.preference;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import miuix.appcompat.app.AlertDialog;

/* JADX INFO: loaded from: classes3.dex */
public class MultiSelectListPreferenceDialogFragmentCompat extends androidx.preference.MultiSelectListPreferenceDialogFragmentCompat {
    private IPreferenceDialogFragment mImpl = new IPreferenceDialogFragment() { // from class: miuix.preference.MultiSelectListPreferenceDialogFragmentCompat.1
        @Override // miuix.preference.IPreferenceDialogFragment
        public boolean needInputMethod() {
            return false;
        }

        @Override // miuix.preference.IPreferenceDialogFragment
        public View onCreateDialogView(Context context) {
            return MultiSelectListPreferenceDialogFragmentCompat.this.onCreateDialogView(context);
        }

        @Override // miuix.preference.IPreferenceDialogFragment
        public void onPrepareDialogBuilder(AlertDialog.Builder builder) {
            MultiSelectListPreferenceDialogFragmentCompat.this.onPrepareDialogBuilder(builder);
        }

        @Override // miuix.preference.IPreferenceDialogFragment
        public void onBindDialogView(View view) {
            MultiSelectListPreferenceDialogFragmentCompat.this.onBindDialogView(view);
        }
    };
    private PreferenceDialogFragmentCompatDelegate mDelegate = new PreferenceDialogFragmentCompatDelegate(this.mImpl, this);

    public static MultiSelectListPreferenceDialogFragmentCompat newInstance(String str) {
        MultiSelectListPreferenceDialogFragmentCompat multiSelectListPreferenceDialogFragmentCompat = new MultiSelectListPreferenceDialogFragmentCompat();
        Bundle bundle = new Bundle(1);
        bundle.putString("key", str);
        multiSelectListPreferenceDialogFragmentCompat.setArguments(bundle);
        return multiSelectListPreferenceDialogFragmentCompat;
    }

    @Override // androidx.preference.PreferenceDialogFragmentCompat, androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        return this.mDelegate.onCreateDialog(bundle);
    }

    @Override // androidx.preference.MultiSelectListPreferenceDialogFragmentCompat, androidx.preference.PreferenceDialogFragmentCompat
    protected final void onPrepareDialogBuilder(androidx.appcompat.app.AlertDialog.Builder builder) {
        throw new UnsupportedOperationException("using miuix builder instead");
    }

    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(new BuilderDelegate(getContext(), builder));
    }
}
