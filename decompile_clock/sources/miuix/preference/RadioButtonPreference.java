package miuix.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.preference.PreferenceViewHolder;
import java.util.Objects;
import miuix.view.HapticCompat;
import miuix.view.HapticFeedbackConstants;

/* JADX INFO: loaded from: classes3.dex */
public class RadioButtonPreference extends BaseCheckBoxPreference implements Checkable, MessageQueue.IdleHandler {
    private static final int MIN_FALLBACK_LINE_SPACING_ALLOWED_VERSION = 28;
    private boolean mChangeFromClick;
    private int mClicked;
    private CompoundButton mCompoundButton;
    private boolean mEnableFallbackLineSpacing;
    private OnPreferenceChangeInternalListener mInternalListener;
    private boolean mIsInit;
    private boolean mIsNotifyChanged;
    private View mItemView;
    private Runnable mRunnable;
    private View mTitleView;

    @Override // android.os.MessageQueue.IdleHandler
    public boolean queueIdle() {
        return false;
    }

    public RadioButtonPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mEnableFallbackLineSpacing = true;
        Looper.myQueue().addIdleHandler(this);
    }

    public RadioButtonPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.radioButtonPreferenceStyle);
    }

    public RadioButtonPreference(Context context) {
        this(context, null);
    }

    @Override // androidx.preference.Preference
    public void onAttached() {
        super.onAttached();
        this.mIsInit = true;
        if (getParent() instanceof RadioSetPreferenceCategory) {
            setLayoutResource(R.layout.miuix_preference_flexible_radiobutton);
        } else {
            setLayoutResource(R.layout.miuix_preference_radiobutton_two_state_background_flexible);
        }
    }

    public void setTitleFallbackLineSpacing(boolean z) {
        this.mEnableFallbackLineSpacing = z;
        if (!(this.mTitleView instanceof TextView) || Build.VERSION.SDK_INT < 28) {
            return;
        }
        ((TextView) this.mTitleView).setFallbackLineSpacing(z);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // miuix.preference.BaseCheckBoxPreference, androidx.preference.CheckBoxPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View view = preferenceViewHolder.itemView;
        this.mItemView = view;
        View viewFindViewById = view.findViewById(android.R.id.title);
        this.mTitleView = viewFindViewById;
        if ((viewFindViewById instanceof TextView) && Build.VERSION.SDK_INT >= 28) {
            ((TextView) this.mTitleView).setFallbackLineSpacing(this.mEnableFallbackLineSpacing);
        }
        KeyEvent.Callback callback = this.mTitleView;
        if (callback instanceof Checkable) {
            ((Checkable) callback).setChecked(isChecked());
        }
        View viewFindViewById2 = view.findViewById(android.R.id.summary);
        if (viewFindViewById2 instanceof Checkable) {
            ((Checkable) viewFindViewById2).setChecked(isChecked());
        }
        View viewFindViewById3 = view.findViewById(android.R.id.checkbox);
        if (viewFindViewById3 != null && (viewFindViewById3 instanceof CompoundButton) && isChecked()) {
            CompoundButton compoundButton = (CompoundButton) viewFindViewById3;
            this.mCompoundButton = compoundButton;
            setButtonChecked(compoundButton, this.mChangeFromClick);
            this.mChangeFromClick = false;
        }
        if (isAccessibilityEnabled()) {
            if (viewFindViewById3 != null) {
                viewFindViewById3.setImportantForAccessibility(2);
            }
            setAccessibilityDelegate(view, viewFindViewById2);
        }
    }

    private void setAccessibilityDelegate(View view, final View view2) {
        ViewCompat.setAccessibilityDelegate(view, new AccessibilityDelegateCompat() { // from class: miuix.preference.RadioButtonPreference.1
            @Override // androidx.core.view.AccessibilityDelegateCompat
            public void onInitializeAccessibilityNodeInfo(View view3, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(view3, accessibilityNodeInfoCompat);
                accessibilityNodeInfoCompat.setCheckable(true);
                accessibilityNodeInfoCompat.setChecked(RadioButtonPreference.this.isChecked());
                StringBuilder sb = new StringBuilder();
                if (RadioButtonPreference.this.mTitleView instanceof TextView) {
                    RadioButtonPreference.this.mTitleView.setImportantForAccessibility(2);
                    sb.append(((TextView) RadioButtonPreference.this.mTitleView).getText());
                }
                View view4 = view2;
                if (view4 instanceof TextView) {
                    view4.setImportantForAccessibility(2);
                    if (sb.length() > 0) {
                        sb.append(" ");
                    }
                    sb.append(((TextView) view2).getText());
                }
                if (sb.length() > 0) {
                    accessibilityNodeInfoCompat.setContentDescription(sb.toString());
                }
                accessibilityNodeInfoCompat.setClassName(RadioButton.class.getName());
                accessibilityNodeInfoCompat.setClickable(true ^ RadioButtonPreference.this.isChecked());
                if (RadioButtonPreference.this.isChecked()) {
                    accessibilityNodeInfoCompat.removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK);
                }
            }
        });
    }

    private void setButtonChecked(CompoundButton compoundButton, boolean z) {
        Drawable buttonDrawable = compoundButton.getButtonDrawable();
        if (buttonDrawable instanceof StateListDrawable) {
            Drawable current = buttonDrawable.getCurrent();
            if (current instanceof AnimatedVectorDrawable) {
                final AnimatedVectorDrawable animatedVectorDrawable = (AnimatedVectorDrawable) current;
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
                if (this.mClicked <= 0 && this.mIsNotifyChanged && !this.mIsInit) {
                    animatedVectorDrawable.start();
                    Objects.requireNonNull(animatedVectorDrawable);
                    Runnable runnable = new Runnable() { // from class: miuix.preference.RadioButtonPreference$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            animatedVectorDrawable.stop();
                        }
                    };
                    this.mRunnable = runnable;
                    compoundButton.post(runnable);
                } else {
                    animatedVectorDrawable.start();
                    animatedVectorDrawable.stop();
                }
                this.mClicked = 0;
                this.mIsInit = false;
                this.mIsNotifyChanged = false;
            }
        }
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

    @Override // androidx.preference.TwoStatePreference, androidx.preference.Preference
    protected void onClick() {
        View view;
        this.mChangeFromClick = true;
        this.mClicked = 2;
        this.mIsInit = false;
        super.onClick();
        if (!this.mChangeFromClick || (view = this.mItemView) == null) {
            return;
        }
        HapticCompat.performHapticFeedbackAsync(view, HapticFeedbackConstants.MIUI_BUTTON_SMALL, HapticFeedbackConstants.MIUI_TAP_NORMAL);
    }

    @Override // androidx.preference.Preference
    protected void notifyChanged() {
        super.notifyChanged();
        int i = this.mClicked;
        if (i > 0) {
            this.mClicked = i - 1;
        }
        this.mIsNotifyChanged = true;
        OnPreferenceChangeInternalListener onPreferenceChangeInternalListener = this.mInternalListener;
        if (onPreferenceChangeInternalListener != null) {
            onPreferenceChangeInternalListener.notifyPreferenceChangeInternal(this);
        }
    }

    @Override // android.widget.Checkable
    public void toggle() {
        setChecked(!isChecked());
    }

    void setOnPreferenceChangeInternalListener(OnPreferenceChangeInternalListener onPreferenceChangeInternalListener) {
        this.mInternalListener = onPreferenceChangeInternalListener;
    }

    @Override // androidx.preference.Preference
    public void onDetached() {
        super.onDetached();
        Looper.myQueue().removeIdleHandler(this);
        SharedPreferences sharedPreferences = getSharedPreferences();
        if (sharedPreferences != null) {
            sharedPreferences.edit().remove(getKey()).apply();
        }
        CompoundButton compoundButton = this.mCompoundButton;
        if (compoundButton != null) {
            compoundButton.removeCallbacks(this.mRunnable);
            this.mCompoundButton = null;
        }
        this.mRunnable = null;
    }
}
