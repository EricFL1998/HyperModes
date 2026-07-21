package miuix.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.preference.PreferenceViewHolder;
import miuix.view.HapticCompat;
import miuix.view.HapticFeedbackConstants;

/* JADX INFO: loaded from: classes3.dex */
public class CheckBoxPreference extends BaseCheckBoxPreference {
    private View mItemView;

    public CheckBoxPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public CheckBoxPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public CheckBoxPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CheckBoxPreference(Context context) {
        super(context);
    }

    @Override // miuix.preference.BaseCheckBoxPreference, androidx.preference.CheckBoxPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.mItemView = preferenceViewHolder.itemView;
        View viewFindViewById = preferenceViewHolder.findViewById(android.R.id.checkbox);
        if (!isAccessibilityEnabled() || viewFindViewById == null) {
            return;
        }
        viewFindViewById.setImportantForAccessibility(2);
    }

    @Override // androidx.preference.TwoStatePreference, androidx.preference.Preference
    protected void onClick() {
        super.onClick();
        View view = this.mItemView;
        if (view != null) {
            HapticCompat.performHapticFeedback(view, HapticFeedbackConstants.MIUI_BUTTON_SMALL, HapticFeedbackConstants.MIUI_TAP_NORMAL);
        }
    }
}
