package miuix.navigator;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.DialogFragment;
import miuix.appcompat.app.AlertDialog;

/* JADX INFO: loaded from: classes3.dex */
public class EditDialogFragment extends DialogFragment {
    public static final String ARG_HINT_RES_ID = "miuix::hint_res_id";
    public static final String ARG_LAYOUT_ID = "miuix::layout_id";
    public static final String ARG_MESSAGE_RES_ID = "miuix::message_res_id";
    public static final String ARG_NEGA_BTN_TEXT_ID = "miuix::nega_btn_text_id";
    public static final String ARG_POSITIVE_BUTTON_ENABLE = "miuix::positive_button_enable";
    public static final String ARG_POSI_BTN_TEXT_ID = "miuix::posi_btn_text_id";
    public static final String ARG_THEME_ID = "miuix::theme_id";
    public static final String ARG_TITLE_RES_ID = "miuix::title_res_id";
    private EditDialogListener mEditListener;
    private boolean mPositiveButtonEnable = true;

    public interface EditDialogListener {
        boolean isPositiveBtnEnable(CharSequence charSequence);

        void onNegativeClick();

        void onPositiveClick(CharSequence charSequence);

        void onPositiveDisable();
    }

    public void setEditDialogListener(EditDialogListener editDialogListener) {
        this.mEditListener = editDialogListener;
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean(ARG_POSITIVE_BUTTON_ENABLE, this.mPositiveButtonEnable);
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder;
        Bundle arguments = getArguments();
        if (arguments == null) {
            return new AlertDialog.Builder(requireContext()).setTitle(R.string.miuix_edit_dialog_fragment_empty_arg_title).setNegativeButton(android.R.string.cancel, (DialogInterface.OnClickListener) null).create();
        }
        if (bundle != null) {
            this.mPositiveButtonEnable = bundle.getBoolean(ARG_POSITIVE_BUTTON_ENABLE);
        }
        if (arguments.containsKey(ARG_THEME_ID)) {
            builder = new AlertDialog.Builder(getContext(), getArguments().getInt(ARG_THEME_ID));
        } else {
            builder = new AlertDialog.Builder(getContext());
        }
        builder.setTitle(arguments.getInt(ARG_TITLE_RES_ID));
        if (arguments.containsKey(ARG_MESSAGE_RES_ID)) {
            builder.setMessage(arguments.getInt(ARG_MESSAGE_RES_ID));
        }
        final View viewInflate = LayoutInflater.from(getContext()).inflate(arguments.getInt(ARG_LAYOUT_ID, R.layout.miuix_simple_edit_text_dialog), (ViewGroup) null);
        final EditText editText = (EditText) viewInflate.findViewById(R.id.edit_text);
        if (arguments.containsKey(ARG_HINT_RES_ID)) {
            editText.setHint(arguments.getInt(ARG_HINT_RES_ID));
        }
        builder.setView(viewInflate);
        builder.setPositiveButton(arguments.getInt(ARG_POSI_BTN_TEXT_ID, android.R.string.ok), new DialogInterface.OnClickListener() { // from class: miuix.navigator.EditDialogFragment$$ExternalSyntheticLambda1
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                this.f$0.m1879lambda$onCreateDialog$0$miuixnavigatorEditDialogFragment(editText, dialogInterface, i);
            }
        });
        builder.setNegativeButton(arguments.getInt(ARG_NEGA_BTN_TEXT_ID, android.R.string.cancel), new DialogInterface.OnClickListener() { // from class: miuix.navigator.EditDialogFragment$$ExternalSyntheticLambda2
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                this.f$0.m1880lambda$onCreateDialog$1$miuixnavigatorEditDialogFragment(dialogInterface, i);
            }
        });
        final AlertDialog alertDialogCreate = builder.create();
        editText.addTextChangedListener(new TextWatcher() { // from class: miuix.navigator.EditDialogFragment.1
            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (EditDialogFragment.this.mEditListener != null) {
                    EditDialogFragment editDialogFragment = EditDialogFragment.this;
                    editDialogFragment.mPositiveButtonEnable = editDialogFragment.mEditListener.isPositiveBtnEnable(charSequence);
                    alertDialogCreate.getButton(-1).setEnabled(EditDialogFragment.this.mPositiveButtonEnable);
                    if (EditDialogFragment.this.mPositiveButtonEnable) {
                        return;
                    }
                    EditDialogFragment.this.mEditListener.onPositiveDisable();
                }
            }
        });
        alertDialogCreate.setOnShowListener(new DialogInterface.OnShowListener() { // from class: miuix.navigator.EditDialogFragment$$ExternalSyntheticLambda3
            @Override // android.content.DialogInterface.OnShowListener
            public final void onShow(DialogInterface dialogInterface) {
                this.f$0.m1882lambda$onCreateDialog$3$miuixnavigatorEditDialogFragment(viewInflate, editText, alertDialogCreate, dialogInterface);
            }
        });
        alertDialogCreate.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: miuix.navigator.EditDialogFragment$$ExternalSyntheticLambda4
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                this.f$0.m1883lambda$onCreateDialog$4$miuixnavigatorEditDialogFragment(editText, dialogInterface);
            }
        });
        return alertDialogCreate;
    }

    /* JADX INFO: renamed from: lambda$onCreateDialog$0$miuix-navigator-EditDialogFragment, reason: not valid java name */
    /* synthetic */ void m1879lambda$onCreateDialog$0$miuixnavigatorEditDialogFragment(EditText editText, DialogInterface dialogInterface, int i) {
        EditDialogListener editDialogListener = this.mEditListener;
        if (editDialogListener != null) {
            editDialogListener.onPositiveClick(editText.getText());
        }
    }

    /* JADX INFO: renamed from: lambda$onCreateDialog$1$miuix-navigator-EditDialogFragment, reason: not valid java name */
    /* synthetic */ void m1880lambda$onCreateDialog$1$miuixnavigatorEditDialogFragment(DialogInterface dialogInterface, int i) {
        EditDialogListener editDialogListener = this.mEditListener;
        if (editDialogListener != null) {
            editDialogListener.onNegativeClick();
        }
    }

    /* JADX INFO: renamed from: lambda$onCreateDialog$3$miuix-navigator-EditDialogFragment, reason: not valid java name */
    /* synthetic */ void m1882lambda$onCreateDialog$3$miuixnavigatorEditDialogFragment(View view, final EditText editText, final AlertDialog alertDialog, DialogInterface dialogInterface) {
        view.postDelayed(new Runnable() { // from class: miuix.navigator.EditDialogFragment$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1881lambda$onCreateDialog$2$miuixnavigatorEditDialogFragment(editText, alertDialog);
            }
        }, 100L);
    }

    /* JADX INFO: renamed from: lambda$onCreateDialog$2$miuix-navigator-EditDialogFragment, reason: not valid java name */
    /* synthetic */ void m1881lambda$onCreateDialog$2$miuixnavigatorEditDialogFragment(EditText editText, AlertDialog alertDialog) {
        showSoftInput(editText);
        Button button = alertDialog.getButton(-1);
        if (button != null) {
            button.setEnabled(this.mPositiveButtonEnable);
        }
    }

    /* JADX INFO: renamed from: lambda$onCreateDialog$4$miuix-navigator-EditDialogFragment, reason: not valid java name */
    /* synthetic */ void m1883lambda$onCreateDialog$4$miuixnavigatorEditDialogFragment(EditText editText, DialogInterface dialogInterface) {
        hideSoftInput(editText);
    }

    protected void showSoftInput(View view) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        ((InputMethodManager) view.getContext().getSystemService(InputMethodManager.class)).showSoftInput(view, 0);
    }

    protected void hideSoftInput(View view) {
        view.clearFocus();
    }
}
