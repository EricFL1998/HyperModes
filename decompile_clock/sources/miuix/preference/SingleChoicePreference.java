package miuix.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.preference.PreferenceViewHolder;
import java.util.Objects;
import miuix.core.util.RomUtils;
import miuix.internal.util.AttributeResolver;
import miuix.view.HapticCompat;
import miuix.view.HapticFeedbackConstants;

/* JADX INFO: loaded from: classes3.dex */
public class SingleChoicePreference extends BaseCheckBoxPreference implements Checkable, FolmeAnimationController, PreferenceExtraPadding {
    private int mCardStyle;
    private boolean mChangeFromClick;
    private int mClicked;
    private CompoundButton mCompoundButton;
    private Context mContext;
    private OnPreferenceChangeInternalListener mInternalListener;
    private boolean mIsCardStyleEnable;
    private boolean mIsInit;
    private boolean mIsNotifyChanged;
    private View mItemView;
    private Runnable mRunnable;
    private String mValue;

    public SingleChoicePreference(Context context) {
        this(context, null);
    }

    public SingleChoicePreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.singleChoicePreferenceStyle);
    }

    public SingleChoicePreference(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public SingleChoicePreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        boolean z = true;
        this.mIsInit = true;
        this.mContext = context;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ChoicePreference, i, i2);
        this.mValue = typedArrayObtainStyledAttributes.getString(R.styleable.ChoicePreference_android_value);
        int iResolveInt = AttributeResolver.resolveInt(context, R.attr.preferenceCardStyleEnable, 1);
        this.mCardStyle = iResolveInt;
        if (iResolveInt != 2 && (RomUtils.getHyperOsVersion() <= 1 || this.mCardStyle != 1)) {
            z = false;
        }
        this.mIsCardStyleEnable = z;
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
        int i;
        super.onBindViewHolder(preferenceViewHolder);
        View view = preferenceViewHolder.itemView;
        this.mItemView = view;
        if (!isDisableAllCardStyle() && !this.mIsCardStyleEnable) {
            Context context = getContext();
            if (AttributeResolver.resolveBoolean(getContext(), miuix.appcompat.R.attr.isLightTheme, true)) {
                i = R.drawable.miuix_preference_single_choice_foregorund_light;
            } else {
                i = R.drawable.miuix_preference_single_choice_foregorund_dark;
            }
            Drawable drawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.miuix_preference_single_choice_background, context.getTheme());
            Drawable drawable2 = ResourcesCompat.getDrawable(context.getResources(), i, context.getTheme());
            view.setBackground(drawable);
            view.setForeground(drawable2);
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.mItemView.getLayoutParams();
            marginLayoutParams.setMargins(marginLayoutParams.leftMargin, 0, marginLayoutParams.rightMargin, (int) (context.getResources().getDisplayMetrics().density * 12.0f));
        }
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
            CompoundButton compoundButton = (CompoundButton) viewFindViewById3;
            this.mCompoundButton = compoundButton;
            setButtonChecked(compoundButton, this.mChangeFromClick);
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
        ViewCompat.setAccessibilityDelegate(this.mItemView, new AccessibilityDelegateCompat() { // from class: miuix.preference.SingleChoicePreference.1
            @Override // androidx.core.view.AccessibilityDelegateCompat
            public void onInitializeAccessibilityNodeInfo(View view3, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(view3, accessibilityNodeInfoCompat);
                accessibilityNodeInfoCompat.setCheckable(true);
                accessibilityNodeInfoCompat.setChecked(SingleChoicePreference.this.isChecked());
                StringBuilder sb = new StringBuilder();
                View view4 = view;
                if (view4 instanceof TextView) {
                    view4.setImportantForAccessibility(2);
                    sb.append(((TextView) view).getText());
                }
                View view5 = view2;
                if (view5 instanceof TextView) {
                    view5.setImportantForAccessibility(2);
                    if (sb.length() > 0) {
                        sb.append(" ");
                    }
                    sb.append(((TextView) view2).getText());
                }
                if (sb.length() > 0) {
                    accessibilityNodeInfoCompat.setContentDescription(sb.toString());
                }
                accessibilityNodeInfoCompat.setClassName(RadioButton.class.getName());
                accessibilityNodeInfoCompat.setClickable(true ^ SingleChoicePreference.this.isChecked());
                if (SingleChoicePreference.this.isChecked()) {
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
                    Runnable runnable = new Runnable() { // from class: miuix.preference.SingleChoicePreference$$ExternalSyntheticLambda0
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

    @Override // androidx.preference.Preference
    public void onDetached() {
        super.onDetached();
        CompoundButton compoundButton = this.mCompoundButton;
        if (compoundButton != null) {
            compoundButton.removeCallbacks(this.mRunnable);
            this.mCompoundButton = null;
        }
        this.mRunnable = null;
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

    @Override // miuix.preference.BaseCheckBoxPreference, miuix.preference.FolmeAnimationController
    public boolean isTouchAnimationEnable() {
        return isDisableAllCardStyle() || this.mIsCardStyleEnable;
    }

    @Override // miuix.preference.PreferenceExtraPadding
    public void onPreferenceExtraPadding(PreferenceViewHolder preferenceViewHolder, int i) {
        if (this.mIsCardStyleEnable) {
            return;
        }
        int dimension = ((int) this.mContext.getResources().getDimension(R.dimen.miuix_preference_item_margin_start)) + i;
        ((LayerDrawable) this.mItemView.getBackground()).setLayerInset(0, dimension, 0, dimension, 0);
        Drawable foreground = this.mItemView.getForeground();
        if (foreground instanceof LayerDrawable) {
            ((LayerDrawable) foreground).setLayerInset(0, dimension, 0, dimension, 0);
        }
    }

    private boolean isDisableAllCardStyle() {
        return -1 == this.mCardStyle;
    }
}
