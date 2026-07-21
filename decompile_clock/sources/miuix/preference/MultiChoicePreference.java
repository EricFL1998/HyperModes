package miuix.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.TextView;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.preference.PreferenceViewHolder;
import miuix.view.HapticCompat;
import miuix.view.HapticFeedbackConstants;

/* JADX INFO: loaded from: classes3.dex */
public class MultiChoicePreference extends BaseCheckBoxPreference implements Checkable {
    private boolean mChangeFromClick;
    private OnPreferenceChangeInternalListener mInternalListener;
    private View mItemView;
    private String mValue;

    public MultiChoicePreference(Context context) {
        this(context, null);
    }

    public MultiChoicePreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.multiChoicePreferenceStyle);
    }

    public MultiChoicePreference(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public MultiChoicePreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ChoicePreference, i, i2);
        this.mValue = typedArrayObtainStyledAttributes.getString(R.styleable.ChoicePreference_android_value);
        typedArrayObtainStyledAttributes.recycle();
    }

    @Override // androidx.preference.Preference
    public boolean callChangeListener(Object obj) {
        OnPreferenceChangeInternalListener onPreferenceChangeInternalListener = this.mInternalListener;
        boolean z = (onPreferenceChangeInternalListener != null ? onPreferenceChangeInternalListener.onPreferenceChangeInternal(this, obj) : true) && super.callChangeListener(obj);
        if (!z && this.mChangeFromClick) {
            this.mChangeFromClick = false;
        }
        return z;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // miuix.preference.BaseCheckBoxPreference, androidx.preference.CheckBoxPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View view = preferenceViewHolder.itemView;
        this.mItemView = view;
        View viewFindViewById = view.findViewById(android.R.id.title);
        if (viewFindViewById instanceof Checkable) {
            ((Checkable) viewFindViewById).setChecked(isChecked());
        }
        View viewFindViewById2 = view.findViewById(android.R.id.summary);
        if (viewFindViewById2 instanceof Checkable) {
            ((Checkable) viewFindViewById2).setChecked(isChecked());
        }
        View viewFindViewById3 = view.findViewById(android.R.id.checkbox);
        if (viewFindViewById3 != null && (viewFindViewById3 instanceof CompoundButton) && isChecked()) {
            setButtonChecked((CompoundButton) viewFindViewById3, this.mChangeFromClick);
            this.mChangeFromClick = false;
        }
        if (isAccessibilityEnabled()) {
            if (viewFindViewById3 != null) {
                viewFindViewById3.setImportantForAccessibility(2);
            }
            setAccessibilityDelegate(viewFindViewById, viewFindViewById2);
        }
    }

    private void setAccessibilityDelegate(final View view, final View view2) {
        if (view != null) {
            view.setImportantForAccessibility(2);
        }
        if (view2 != null) {
            view2.setImportantForAccessibility(2);
        }
        ViewCompat.setAccessibilityDelegate(this.mItemView, new AccessibilityDelegateCompat() { // from class: miuix.preference.MultiChoicePreference.1
            @Override // androidx.core.view.AccessibilityDelegateCompat
            public void onInitializeAccessibilityNodeInfo(View view3, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(view3, accessibilityNodeInfoCompat);
                accessibilityNodeInfoCompat.setClickable(true);
                accessibilityNodeInfoCompat.setCheckable(true);
                accessibilityNodeInfoCompat.setChecked(MultiChoicePreference.this.isChecked());
                accessibilityNodeInfoCompat.setClassName(CheckBox.class.getName());
                StringBuilder sb = new StringBuilder();
                View view4 = view;
                if (view4 != null && view4.getVisibility() == 0) {
                    View view5 = view;
                    if (view5 instanceof TextView) {
                        sb.append(((TextView) view5).getText());
                    }
                }
                View view6 = view2;
                if (view6 != null && view6.getVisibility() == 0 && (view2 instanceof TextView)) {
                    if (sb.length() > 0) {
                        sb.append(" ");
                    }
                    sb.append(((TextView) view2).getText());
                }
                if (sb.length() > 0) {
                    accessibilityNodeInfoCompat.setContentDescription(sb.toString());
                }
            }
        });
    }

    private void setButtonChecked(CompoundButton compoundButton, boolean z) {
        Drawable buttonDrawable = compoundButton.getButtonDrawable();
        if (buttonDrawable instanceof StateListDrawable) {
            Drawable current = buttonDrawable.getCurrent();
            if (current instanceof AnimatedVectorDrawable) {
                AnimatedVectorDrawable animatedVectorDrawable = (AnimatedVectorDrawable) current;
                if (z) {
                    if (animatedVectorDrawable.isRunning()) {
                        animatedVectorDrawable.stop();
                        animatedVectorDrawable.reset();
                    }
                    animatedVectorDrawable.start();
                    return;
                }
                if (animatedVectorDrawable.isRunning()) {
                    return;
                }
                animatedVectorDrawable.start();
                animatedVectorDrawable.stop();
            }
        }
    }

    @Override // androidx.preference.TwoStatePreference, androidx.preference.Preference
    protected void onClick() {
        View view;
        this.mChangeFromClick = true;
        super.onClick();
        if (!this.mChangeFromClick || (view = this.mItemView) == null) {
            return;
        }
        HapticCompat.performHapticFeedbackAsync(view, HapticFeedbackConstants.MIUI_BUTTON_SMALL, HapticFeedbackConstants.MIUI_TAP_NORMAL);
    }

    @Override // androidx.preference.Preference
    protected void notifyChanged() {
        super.notifyChanged();
        OnPreferenceChangeInternalListener onPreferenceChangeInternalListener = this.mInternalListener;
        if (onPreferenceChangeInternalListener != null) {
            onPreferenceChangeInternalListener.notifyPreferenceChangeInternal(this);
        }
    }

    void setOnPreferenceChangeInternalListener(OnPreferenceChangeInternalListener onPreferenceChangeInternalListener) {
        this.mInternalListener = onPreferenceChangeInternalListener;
    }

    public void setValue(String str) {
        this.mValue = str;
    }

    public String getValue() {
        return this.mValue;
    }

    @Override // androidx.preference.TwoStatePreference, android.widget.Checkable
    public void setChecked(boolean z) {
        super.setChecked(z);
    }

    @Override // android.widget.Checkable
    public void toggle() {
        setChecked(!isChecked());
    }
}
