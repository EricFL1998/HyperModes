package com.android.deskclock.view.list;

import android.R;
import android.view.View;
import android.widget.CheckBox;
import androidx.recyclerview.widget.RecyclerView;

/* JADX INFO: loaded from: classes.dex */
public class EditableViewHolder extends RecyclerView.ViewHolder implements ViewHolderEditableCallback {
    private CheckBox mMultiCheckBox;
    private boolean mStarted;

    public EditableViewHolder(View view) {
        super(view);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
        this.mMultiCheckBox = checkBox;
        checkBox.setClickable(false);
    }

    @Override // com.android.deskclock.view.list.ViewHolderEditableCallback
    public boolean hasAnimationStarted() {
        return this.mStarted;
    }

    public void onAnimationStart(boolean z) {
        this.mStarted = true;
        if (z) {
            this.mMultiCheckBox.setVisibility(0);
            this.mMultiCheckBox.setAlpha(0.0f);
            this.mMultiCheckBox.setScaleX(0.8f);
            this.mMultiCheckBox.setScaleY(0.8f);
            return;
        }
        this.mMultiCheckBox.setVisibility(8);
    }

    public void onAnimationUpdate(boolean z, float f) {
        if (!z) {
            f = 1.0f - f;
        }
        this.mMultiCheckBox.setAlpha(f);
        float f2 = (f * 0.2f) + 0.8f;
        this.mMultiCheckBox.setScaleX(f2);
        this.mMultiCheckBox.setScaleY(f2);
    }

    public void onAnimationStop(boolean z) {
        this.mStarted = false;
        this.mMultiCheckBox.setAlpha(1.0f);
        this.mMultiCheckBox.setScaleX(1.0f);
        this.mMultiCheckBox.setScaleY(1.0f);
        if (z) {
            return;
        }
        this.mMultiCheckBox.setVisibility(8);
    }

    public void onUpdateEditable(boolean z, boolean z2) {
        this.mMultiCheckBox.setChecked(z2);
        if (z) {
            this.itemView.setLongClickable(false);
            this.mMultiCheckBox.setVisibility(0);
        } else {
            this.itemView.setLongClickable(true);
            this.mMultiCheckBox.setVisibility(8);
        }
    }
}
