package androidx.preference;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.widget.TextView;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public abstract class PreferenceDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {

    @Deprecated
    protected static final String ARG_KEY = "key";
    private static final String SAVE_STATE_ICON = "PreferenceDialogFragment.icon";
    private static final String SAVE_STATE_LAYOUT = "PreferenceDialogFragment.layout";
    private static final String SAVE_STATE_MESSAGE = "PreferenceDialogFragment.message";
    private static final String SAVE_STATE_NEGATIVE_TEXT = "PreferenceDialogFragment.negativeText";
    private static final String SAVE_STATE_POSITIVE_TEXT = "PreferenceDialogFragment.positiveText";
    private static final String SAVE_STATE_TITLE = "PreferenceDialogFragment.title";
    private BitmapDrawable mDialogIcon;
    private int mDialogLayoutRes;
    private CharSequence mDialogMessage;
    private CharSequence mDialogTitle;
    private CharSequence mNegativeButtonText;
    private CharSequence mPositiveButtonText;
    private DialogPreference mPreference;
    private int mWhichButtonClicked;

    protected boolean needInputMethod() {
        return false;
    }

    @Deprecated
    public abstract void onDialogClosed(boolean z);

    @Deprecated
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
    }

    @Deprecated
    public PreferenceDialogFragment() {
    }

    @Override // android.app.DialogFragment, android.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ComponentCallbacks2 targetFragment = getTargetFragment();
        if (!(targetFragment instanceof DialogPreference.TargetFragment)) {
            throw new IllegalStateException("Target fragment must implement TargetFragment interface");
        }
        DialogPreference.TargetFragment targetFragment2 = (DialogPreference.TargetFragment) targetFragment;
        String string = getArguments().getString(ARG_KEY);
        if (bundle == null) {
            DialogPreference dialogPreference = (DialogPreference) targetFragment2.findPreference(string);
            this.mPreference = dialogPreference;
            this.mDialogTitle = dialogPreference.getDialogTitle();
            this.mPositiveButtonText = this.mPreference.getPositiveButtonText();
            this.mNegativeButtonText = this.mPreference.getNegativeButtonText();
            this.mDialogMessage = this.mPreference.getDialogMessage();
            this.mDialogLayoutRes = this.mPreference.getDialogLayoutResource();
            Drawable dialogIcon = this.mPreference.getDialogIcon();
            if (dialogIcon == null || (dialogIcon instanceof BitmapDrawable)) {
                this.mDialogIcon = (BitmapDrawable) dialogIcon;
                return;
            }
            Bitmap bitmapCreateBitmap = Bitmap.createBitmap(dialogIcon.getIntrinsicWidth(), dialogIcon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmapCreateBitmap);
            dialogIcon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            dialogIcon.draw(canvas);
            this.mDialogIcon = new BitmapDrawable(getResources(), bitmapCreateBitmap);
            return;
        }
        this.mDialogTitle = bundle.getCharSequence(SAVE_STATE_TITLE);
        this.mPositiveButtonText = bundle.getCharSequence(SAVE_STATE_POSITIVE_TEXT);
        this.mNegativeButtonText = bundle.getCharSequence(SAVE_STATE_NEGATIVE_TEXT);
        this.mDialogMessage = bundle.getCharSequence(SAVE_STATE_MESSAGE);
        this.mDialogLayoutRes = bundle.getInt(SAVE_STATE_LAYOUT, 0);
        Bitmap bitmap = (Bitmap) bundle.getParcelable(SAVE_STATE_ICON);
        if (bitmap != null) {
            this.mDialogIcon = new BitmapDrawable(getResources(), bitmap);
        }
    }

    @Override // android.app.DialogFragment, android.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putCharSequence(SAVE_STATE_TITLE, this.mDialogTitle);
        bundle.putCharSequence(SAVE_STATE_POSITIVE_TEXT, this.mPositiveButtonText);
        bundle.putCharSequence(SAVE_STATE_NEGATIVE_TEXT, this.mNegativeButtonText);
        bundle.putCharSequence(SAVE_STATE_MESSAGE, this.mDialogMessage);
        bundle.putInt(SAVE_STATE_LAYOUT, this.mDialogLayoutRes);
        BitmapDrawable bitmapDrawable = this.mDialogIcon;
        if (bitmapDrawable != null) {
            bundle.putParcelable(SAVE_STATE_ICON, bitmapDrawable.getBitmap());
        }
    }

    @Override // android.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Activity activity = getActivity();
        this.mWhichButtonClicked = -2;
        AlertDialog.Builder negativeButton = new AlertDialog.Builder(activity).setTitle(this.mDialogTitle).setIcon(this.mDialogIcon).setPositiveButton(this.mPositiveButtonText, this).setNegativeButton(this.mNegativeButtonText, this);
        View viewOnCreateDialogView = onCreateDialogView(activity);
        if (viewOnCreateDialogView != null) {
            onBindDialogView(viewOnCreateDialogView);
            negativeButton.setView(viewOnCreateDialogView);
        } else {
            negativeButton.setMessage(this.mDialogMessage);
        }
        onPrepareDialogBuilder(negativeButton);
        AlertDialog alertDialogCreate = negativeButton.create();
        if (needInputMethod()) {
            requestInputMethod(alertDialogCreate);
        }
        return alertDialogCreate;
    }

    @Deprecated
    public DialogPreference getPreference() {
        if (this.mPreference == null) {
            this.mPreference = (DialogPreference) ((DialogPreference.TargetFragment) getTargetFragment()).findPreference(getArguments().getString(ARG_KEY));
        }
        return this.mPreference;
    }

    private void requestInputMethod(Dialog dialog) {
        Window window = dialog.getWindow();
        if (Build.VERSION.SDK_INT >= 30) {
            Api30Impl.showIme(window);
        } else {
            window.setSoftInputMode(5);
        }
    }

    @Deprecated
    protected View onCreateDialogView(Context context) {
        int i = this.mDialogLayoutRes;
        if (i == 0) {
            return null;
        }
        return LayoutInflater.from(context).inflate(i, (ViewGroup) null);
    }

    @Deprecated
    protected void onBindDialogView(View view) {
        int i;
        View viewFindViewById = view.findViewById(android.R.id.message);
        if (viewFindViewById != null) {
            CharSequence charSequence = this.mDialogMessage;
            if (TextUtils.isEmpty(charSequence)) {
                i = 8;
            } else {
                if (viewFindViewById instanceof TextView) {
                    ((TextView) viewFindViewById).setText(charSequence);
                }
                i = 0;
            }
            if (viewFindViewById.getVisibility() != i) {
                viewFindViewById.setVisibility(i);
            }
        }
    }

    @Override // android.content.DialogInterface.OnClickListener
    @Deprecated
    public void onClick(DialogInterface dialogInterface, int i) {
        this.mWhichButtonClicked = i;
    }

    @Override // android.app.DialogFragment, android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        onDialogClosed(this.mWhichButtonClicked == -1);
    }

    private static class Api30Impl {
        private Api30Impl() {
        }

        static void showIme(Window window) {
            window.getDecorView().getWindowInsetsController().show(WindowInsets.Type.ime());
        }
    }
}
