package miuix.preference;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import androidx.appcompat.app.AlertDialog;

/* JADX INFO: loaded from: classes3.dex */
class BuilderDelegate extends AlertDialog.Builder {
    private miuix.appcompat.app.AlertDialog.Builder mMiuixBuilder;

    public BuilderDelegate(Context context, miuix.appcompat.app.AlertDialog.Builder builder) {
        this(context, 0, builder);
    }

    public BuilderDelegate(Context context, int i, miuix.appcompat.app.AlertDialog.Builder builder) {
        super(context, i);
        this.mMiuixBuilder = builder;
    }

    @Override // androidx.appcompat.app.AlertDialog.Builder
    public AlertDialog.Builder setTitle(int i) {
        this.mMiuixBuilder.setTitle(i);
        return this;
    }

    @Override // androidx.appcompat.app.AlertDialog.Builder
    public AlertDialog.Builder setTitle(CharSequence charSequence) {
        this.mMiuixBuilder.setTitle(charSequence);
        return this;
    }

    @Override // androidx.appcompat.app.AlertDialog.Builder
    public AlertDialog.Builder setCustomTitle(View view) {
        this.mMiuixBuilder.setCustomTitle(view);
        return this;
    }

    @Override // androidx.appcompat.app.AlertDialog.Builder
    public AlertDialog.Builder setMessage(int i) {
        this.mMiuixBuilder.setMessage(i);
        return this;
    }

    @Override // androidx.appcompat.app.AlertDialog.Builder
    public AlertDialog.Builder setMessage(CharSequence charSequence) {
        this.mMiuixBuilder.setMessage(charSequence);
        return this;
    }

    @Override // androidx.appcompat.app.AlertDialog.Builder
    public AlertDialog.Builder setIcon(int i) {
        this.mMiuixBuilder.setIcon(i);
        return this;
    }

    @Override // androidx.appcompat.app.AlertDialog.Builder
    public AlertDialog.Builder setIcon(Drawable drawable) {
        this.mMiuixBuilder.setIcon(drawable);
        return this;
    }

    @Override // androidx.appcompat.app.AlertDialog.Builder
    public AlertDialog.Builder setIconAttribute(int i) {
        this.mMiuixBuilder.setIconAttribute(i);
        return this;
    }

    @Override // androidx.appcompat.app.AlertDialog.Builder
    public AlertDialog.Builder setPositiveButton(int i, DialogInterface.OnClickListener onClickListener) {
        this.mMiuixBuilder.setPositiveButton(i, onClickListener);
        return this;
    }

    @Override // androidx.appcompat.app.AlertDialog.Builder
    public AlertDialog.Builder setPositiveButton(CharSequence charSequence, DialogInterface.OnClickListener onClickListener) {
        this.mMiuixBuilder.setPositiveButton(charSequence, onClickListener);
        return this;
    }

    @Override // androidx.appcompat.app.AlertDialog.Builder
    public AlertDialog.Builder setNegativeButton(int i, DialogInterface.OnClickListener onClickListener) {
        this.mMiuixBuilder.setNegativeButton(i, onClickListener);
        return this;
    }

    @Override // androidx.appcompat.app.AlertDialog.Builder
    public AlertDialog.Builder setNegativeButton(CharSequence charSequence, DialogInterface.OnClickListener onClickListener) {
        this.mMiuixBuilder.setNegativeButton(charSequence, onClickListener);
        return this;
    }

    @Override // androidx.appcompat.app.AlertDialog.Builder
    public AlertDialog.Builder setNeutralButton(int i, DialogInterface.OnClickListener onClickListener) {
        this.mMiuixBuilder.setNeutralButton(i, onClickListener);
        return this;
    }

    @Override // androidx.appcompat.app.AlertDialog.Builder
    public AlertDialog.Builder setNeutralButton(CharSequence charSequence, DialogInterface.OnClickListener onClickListener) {
        this.mMiuixBuilder.setNeutralButton(charSequence, onClickListener);
        return this;
    }

    @Override // androidx.appcompat.app.AlertDialog.Builder
    public AlertDialog.Builder setCancelable(boolean z) {
        this.mMiuixBuilder.setCancelable(z);
        return this;
    }

    @Override // androidx.appcompat.app.AlertDialog.Builder
    public AlertDialog.Builder setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        this.mMiuixBuilder.setOnCancelListener(onCancelListener);
        return this;
    }

    @Override // androidx.appcompat.app.AlertDialog.Builder
    public AlertDialog.Builder setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.mMiuixBuilder.setOnDismissListener(onDismissListener);
        return this;
    }

    @Override // androidx.appcompat.app.AlertDialog.Builder
    public AlertDialog.Builder setOnKeyListener(DialogInterface.OnKeyListener onKeyListener) {
        this.mMiuixBuilder.setOnKeyListener(onKeyListener);
        return this;
    }

    @Override // androidx.appcompat.app.AlertDialog.Builder
    public AlertDialog.Builder setItems(int i, DialogInterface.OnClickListener onClickListener) {
        this.mMiuixBuilder.setItems(i, onClickListener);
        return this;
    }

    @Override // androidx.appcompat.app.AlertDialog.Builder
    public AlertDialog.Builder setItems(CharSequence[] charSequenceArr, DialogInterface.OnClickListener onClickListener) {
        this.mMiuixBuilder.setItems(charSequenceArr, onClickListener);
        return this;
    }

    @Override // androidx.appcompat.app.AlertDialog.Builder
    public AlertDialog.Builder setAdapter(ListAdapter listAdapter, DialogInterface.OnClickListener onClickListener) {
        this.mMiuixBuilder.setAdapter(listAdapter, onClickListener);
        return this;
    }

    @Override // androidx.appcompat.app.AlertDialog.Builder
    public AlertDialog.Builder setCursor(Cursor cursor, DialogInterface.OnClickListener onClickListener, String str) {
        this.mMiuixBuilder.setCursor(cursor, onClickListener, str);
        return this;
    }

    @Override // androidx.appcompat.app.AlertDialog.Builder
    public AlertDialog.Builder setMultiChoiceItems(int i, boolean[] zArr, DialogInterface.OnMultiChoiceClickListener onMultiChoiceClickListener) {
        this.mMiuixBuilder.setMultiChoiceItems(i, zArr, onMultiChoiceClickListener);
        return this;
    }

    @Override // androidx.appcompat.app.AlertDialog.Builder
    public AlertDialog.Builder setMultiChoiceItems(CharSequence[] charSequenceArr, boolean[] zArr, DialogInterface.OnMultiChoiceClickListener onMultiChoiceClickListener) {
        this.mMiuixBuilder.setMultiChoiceItems(charSequenceArr, zArr, onMultiChoiceClickListener);
        return this;
    }

    @Override // androidx.appcompat.app.AlertDialog.Builder
    public AlertDialog.Builder setMultiChoiceItems(Cursor cursor, String str, String str2, DialogInterface.OnMultiChoiceClickListener onMultiChoiceClickListener) {
        this.mMiuixBuilder.setMultiChoiceItems(cursor, str, str2, onMultiChoiceClickListener);
        return this;
    }

    @Override // androidx.appcompat.app.AlertDialog.Builder
    public AlertDialog.Builder setSingleChoiceItems(int i, int i2, DialogInterface.OnClickListener onClickListener) {
        this.mMiuixBuilder.setSingleChoiceItems(i, i2, onClickListener);
        return this;
    }

    @Override // androidx.appcompat.app.AlertDialog.Builder
    public AlertDialog.Builder setSingleChoiceItems(Cursor cursor, int i, String str, DialogInterface.OnClickListener onClickListener) {
        this.mMiuixBuilder.setSingleChoiceItems(cursor, i, str, onClickListener);
        return this;
    }

    @Override // androidx.appcompat.app.AlertDialog.Builder
    public AlertDialog.Builder setSingleChoiceItems(CharSequence[] charSequenceArr, int i, DialogInterface.OnClickListener onClickListener) {
        this.mMiuixBuilder.setSingleChoiceItems(charSequenceArr, i, onClickListener);
        return this;
    }

    @Override // androidx.appcompat.app.AlertDialog.Builder
    public AlertDialog.Builder setSingleChoiceItems(ListAdapter listAdapter, int i, DialogInterface.OnClickListener onClickListener) {
        this.mMiuixBuilder.setSingleChoiceItems(listAdapter, i, onClickListener);
        return this;
    }

    @Override // androidx.appcompat.app.AlertDialog.Builder
    public AlertDialog.Builder setOnItemSelectedListener(AdapterView.OnItemSelectedListener onItemSelectedListener) {
        this.mMiuixBuilder.setOnItemSelectedListener(onItemSelectedListener);
        return this;
    }

    @Override // androidx.appcompat.app.AlertDialog.Builder
    public AlertDialog.Builder setView(int i) {
        this.mMiuixBuilder.setView(i);
        return this;
    }

    @Override // androidx.appcompat.app.AlertDialog.Builder
    public AlertDialog.Builder setView(View view) {
        this.mMiuixBuilder.setView(view);
        return this;
    }
}
