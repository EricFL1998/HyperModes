package miuix.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Checkable;
import androidx.preference.Preference;

/* JADX INFO: loaded from: classes3.dex */
public class RadioButtonPreferenceCategory extends androidx.preference.PreferenceCategory {
    private static final String TAG = "RadioButtonPreferenceCategory";
    private SingleChoiceHelper mCheckedChoice;
    private int mCheckedPosition;
    private OnPreferenceChangeInternalListener mInternalListener;
    private boolean mIsNeedCardGroup;

    /* JADX INFO: Access modifiers changed from: private */
    public void checkPreferenceByInternal(Preference preference, Object obj) {
        Preference parent = preference.getParent() instanceof RadioSetPreferenceCategory ? preference.getParent() : preference;
        SingleChoiceHelper singleChoiceHelper = this.mCheckedChoice;
        if ((singleChoiceHelper == null || parent != singleChoiceHelper.getPreference()) && callChangeListenerByInternal(obj, parent)) {
            setCheckedPreference(preference);
        }
    }

    private boolean callChangeListenerByInternal(Object obj, Preference preference) {
        return preference.getOnPreferenceChangeListener() == null || preference.getOnPreferenceChangeListener().onPreferenceChange(preference, obj);
    }

    public RadioButtonPreferenceCategory(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mCheckedChoice = null;
        this.mCheckedPosition = -1;
        this.mInternalListener = new OnPreferenceChangeInternalListener() { // from class: miuix.preference.RadioButtonPreferenceCategory.1
            /* JADX WARN: Multi-variable type inference failed */
            @Override // miuix.preference.OnPreferenceChangeInternalListener
            public boolean onPreferenceChangeInternal(Preference preference, Object obj) {
                boolean zIsChecked = ((Checkable) preference).isChecked();
                Preference.OnPreferenceClickListener onPreferenceClickListener = RadioButtonPreferenceCategory.this.getOnPreferenceClickListener();
                if (onPreferenceClickListener != null) {
                    RadioButtonPreferenceCategory.this.checkPreferenceByInternal(preference, obj);
                    onPreferenceClickListener.onPreferenceClick(RadioButtonPreferenceCategory.this);
                }
                return !zIsChecked;
            }

            @Override // miuix.preference.OnPreferenceChangeInternalListener
            public void notifyPreferenceChangeInternal(Preference preference) {
                SingleChoiceHelper singleChoiceHelper = RadioButtonPreferenceCategory.this.parse(preference);
                RadioButtonPreferenceCategory.this.updateCheckedPreference(singleChoiceHelper);
                RadioButtonPreferenceCategory.this.updateCheckedPosition(singleChoiceHelper);
            }
        };
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.RadioButtonPreferenceCategory);
        this.mIsNeedCardGroup = typedArrayObtainStyledAttributes.getBoolean(R.styleable.RadioButtonPreferenceCategory_toCardGroup, false);
        typedArrayObtainStyledAttributes.recycle();
    }

    public RadioButtonPreferenceCategory(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.preferenceCategoryRadioStyle);
    }

    public RadioButtonPreferenceCategory(Context context) {
        this(context, null);
    }

    @Override // androidx.preference.PreferenceGroup
    public boolean addPreference(Preference preference) {
        SingleChoiceHelper singleChoiceHelper = parse(preference);
        boolean zAddPreference = super.addPreference(preference);
        if (zAddPreference) {
            singleChoiceHelper.setOnPreferenceChangeInternalListener(this.mInternalListener);
        }
        if (singleChoiceHelper.isChecked()) {
            if (this.mCheckedChoice != null) {
                throw new IllegalStateException("Already has a checked item, please check state of new add preference");
            }
            this.mCheckedChoice = singleChoiceHelper;
        }
        return zAddPreference;
    }

    @Override // androidx.preference.PreferenceGroup
    public void removeAll() {
        super.removeAll();
        this.mCheckedPosition = -1;
        this.mCheckedChoice = null;
    }

    @Override // androidx.preference.PreferenceGroup
    public boolean removePreference(Preference preference) {
        SingleChoiceHelper singleChoiceHelper = parse(preference);
        boolean zRemovePreference = super.removePreference(preference);
        if (zRemovePreference) {
            singleChoiceHelper.setOnPreferenceChangeInternalListener(null);
            if (singleChoiceHelper.isChecked()) {
                singleChoiceHelper.setChecked(false);
                this.mCheckedPosition = -1;
                this.mCheckedChoice = null;
            }
        }
        return zRemovePreference;
    }

    public void setCheckedPreference(Preference preference) {
        if (preference == null) {
            clearChecked();
            return;
        }
        SingleChoiceHelper singleChoiceHelper = parse(preference);
        if (singleChoiceHelper.isChecked()) {
            return;
        }
        setCheckedPreferenceInternal(singleChoiceHelper);
        updateCheckedPreference(singleChoiceHelper);
        updateCheckedPosition(singleChoiceHelper);
    }

    private void clearChecked() {
        SingleChoiceHelper singleChoiceHelper = this.mCheckedChoice;
        if (singleChoiceHelper != null) {
            singleChoiceHelper.setChecked(false);
        }
        this.mCheckedChoice = null;
        this.mCheckedPosition = -1;
    }

    private void setCheckedPreferenceInternal(SingleChoiceHelper singleChoiceHelper) {
        singleChoiceHelper.setChecked(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCheckedPreference(SingleChoiceHelper singleChoiceHelper) {
        if (singleChoiceHelper.isChecked()) {
            SingleChoiceHelper singleChoiceHelper2 = this.mCheckedChoice;
            if (singleChoiceHelper2 != null && singleChoiceHelper2.getPreference() != singleChoiceHelper.getPreference()) {
                this.mCheckedChoice.setChecked(false);
            }
            this.mCheckedChoice = singleChoiceHelper;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCheckedPosition(SingleChoiceHelper singleChoiceHelper) {
        if (singleChoiceHelper.isChecked()) {
            int preferenceCount = getPreferenceCount();
            for (int i = 0; i < preferenceCount; i++) {
                if (getPreference(i) == singleChoiceHelper.getPreference()) {
                    this.mCheckedPosition = i;
                    return;
                }
            }
        }
    }

    public void setCheckedPosition(int i) {
        SingleChoiceHelper singleChoiceHelper = parse(getPreference(i));
        if (singleChoiceHelper.isChecked()) {
            return;
        }
        setCheckedPreferenceInternal(singleChoiceHelper);
        updateCheckedPreference(singleChoiceHelper);
        this.mCheckedPosition = i;
    }

    public Preference getCheckedPreference() {
        SingleChoiceHelper singleChoiceHelper = this.mCheckedChoice;
        if (singleChoiceHelper == null) {
            return null;
        }
        return singleChoiceHelper.getPreference();
    }

    public int getCheckedPosition() {
        SingleChoiceHelper singleChoiceHelper;
        if (this.mCheckedPosition == -1 && (singleChoiceHelper = this.mCheckedChoice) != null) {
            updateCheckedPosition(singleChoiceHelper);
        }
        return this.mCheckedPosition;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SingleChoiceHelper parse(Preference preference) {
        if (preference instanceof RadioButtonPreference) {
            if (preference.getParent() instanceof RadioSetPreferenceCategory) {
                return new CategorySingleChoiceHelper((RadioSetPreferenceCategory) preference.getParent());
            }
            return new PreferenceSingleChoiceHelper((RadioButtonPreference) preference);
        }
        if (preference instanceof RadioSetPreferenceCategory) {
            return new CategorySingleChoiceHelper((RadioSetPreferenceCategory) preference);
        }
        throw new IllegalArgumentException("Only RadioButtonPreference or RadioSetPreferenceCategory can be added to RadioButtonPreferenceCategory");
    }

    public boolean isNeedCardGroup() {
        return this.mIsNeedCardGroup;
    }

    public void setToCardGroup(boolean z) {
        this.mIsNeedCardGroup = z;
    }

    class PreferenceSingleChoiceHelper extends SingleChoiceHelper {
        RadioButtonPreference mPreference;

        PreferenceSingleChoiceHelper(RadioButtonPreference radioButtonPreference) {
            super(radioButtonPreference);
            this.mPreference = radioButtonPreference;
        }

        @Override // miuix.preference.RadioButtonPreferenceCategory.SingleChoiceHelper
        public void setOnPreferenceChangeInternalListener(OnPreferenceChangeInternalListener onPreferenceChangeInternalListener) {
            this.mPreference.setOnPreferenceChangeInternalListener(onPreferenceChangeInternalListener);
        }

        @Override // miuix.preference.RadioButtonPreferenceCategory.SingleChoiceHelper
        public Preference getPreference() {
            return this.mPreference;
        }
    }

    class CategorySingleChoiceHelper extends SingleChoiceHelper {
        private RadioSetPreferenceCategory mCategory;

        CategorySingleChoiceHelper(RadioSetPreferenceCategory radioSetPreferenceCategory) {
            super(radioSetPreferenceCategory);
            this.mCategory = radioSetPreferenceCategory;
        }

        @Override // miuix.preference.RadioButtonPreferenceCategory.SingleChoiceHelper
        public void setOnPreferenceChangeInternalListener(OnPreferenceChangeInternalListener onPreferenceChangeInternalListener) {
            this.mCategory.setOnPreferenceChangeInternalListener(onPreferenceChangeInternalListener);
        }

        @Override // miuix.preference.RadioButtonPreferenceCategory.SingleChoiceHelper
        public Preference getPreference() {
            return this.mCategory;
        }

        @Override // miuix.preference.RadioButtonPreferenceCategory.SingleChoiceHelper, android.widget.Checkable
        public void setChecked(boolean z) {
            super.setChecked(z);
            if (this.mCategory.getPrimaryPreference() != null) {
                this.mCategory.getPrimaryPreference().setChecked(z);
            }
        }
    }

    abstract class SingleChoiceHelper implements Checkable {
        Checkable mCheckable;

        abstract Preference getPreference();

        abstract void setOnPreferenceChangeInternalListener(OnPreferenceChangeInternalListener onPreferenceChangeInternalListener);

        SingleChoiceHelper(Checkable checkable) {
            this.mCheckable = checkable;
        }

        @Override // android.widget.Checkable
        public void setChecked(boolean z) {
            this.mCheckable.setChecked(z);
        }

        @Override // android.widget.Checkable
        public boolean isChecked() {
            return this.mCheckable.isChecked();
        }

        @Override // android.widget.Checkable
        public void toggle() {
            setChecked(!isChecked());
        }
    }
}
