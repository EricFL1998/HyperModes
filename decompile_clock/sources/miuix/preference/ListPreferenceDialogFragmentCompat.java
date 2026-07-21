package miuix.preference;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import miuix.appcompat.app.AlertDialog;

/* JADX INFO: loaded from: classes3.dex */
public class ListPreferenceDialogFragmentCompat extends androidx.preference.ListPreferenceDialogFragmentCompat {
    private static final String TAG = "ListPreferenceDialogFragmentCompat";
    private IPreferenceDialogFragment mImpl = new IPreferenceDialogFragment() { // from class: miuix.preference.ListPreferenceDialogFragmentCompat.1
        @Override // miuix.preference.IPreferenceDialogFragment
        public boolean needInputMethod() {
            return false;
        }

        @Override // miuix.preference.IPreferenceDialogFragment
        public View onCreateDialogView(Context context) {
            return ListPreferenceDialogFragmentCompat.this.onCreateDialogView(context);
        }

        @Override // miuix.preference.IPreferenceDialogFragment
        public void onPrepareDialogBuilder(AlertDialog.Builder builder) {
            ListPreferenceDialogFragmentCompat.this.onPrepareDialogBuilder(builder);
        }

        @Override // miuix.preference.IPreferenceDialogFragment
        public void onBindDialogView(View view) {
            ListPreferenceDialogFragmentCompat.this.onBindDialogView(view);
        }
    };
    private PreferenceDialogFragmentCompatDelegate mDelegate = new PreferenceDialogFragmentCompatDelegate(this.mImpl, this);

    public static ListPreferenceDialogFragmentCompat newInstance(String str) {
        ListPreferenceDialogFragmentCompat listPreferenceDialogFragmentCompat = new ListPreferenceDialogFragmentCompat();
        Bundle bundle = new Bundle(1);
        bundle.putString("key", str);
        listPreferenceDialogFragmentCompat.setArguments(bundle);
        return listPreferenceDialogFragmentCompat;
    }

    @Override // androidx.preference.ListPreferenceDialogFragmentCompat, androidx.preference.PreferenceDialogFragmentCompat, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.d(TAG, "onCreate");
    }

    @Override // androidx.preference.PreferenceDialogFragmentCompat, androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Dialog dialogOnCreateDialog = this.mDelegate.onCreateDialog(bundle);
        Log.d(TAG, "onCreateDialog");
        return dialogOnCreateDialog;
    }

    @Override // androidx.preference.ListPreferenceDialogFragmentCompat, androidx.preference.PreferenceDialogFragmentCompat
    protected final void onPrepareDialogBuilder(androidx.appcompat.app.AlertDialog.Builder builder) {
        throw new UnsupportedOperationException("using miuix builder instead");
    }

    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(new BuilderDelegate(getContext(), builder));
    }
}
