package miuix.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.Checkable;
import androidx.preference.Preference;

/* JADX INFO: loaded from: classes3.dex */
public class SingleChoicePreferenceCategory extends androidx.preference.PreferenceCategory {
    private static final String TAG = "SingleChoicePreference2";
    private boolean mCardGroupEnabled;
    private SingleChoiceHelper mCheckedChoice;
    private int mCheckedPosition;
    private Context mContext;
    private CharSequence[] mEntries;
    private CharSequence[] mEntryValues;
    private OnPreferenceChangeInternalListener mInternalListener;
    private CharSequence[] mSummaries;
    private String mValue;
    private boolean mValueSet;

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

    public SingleChoicePreferenceCategory(Context context) {
        this(context, null);
    }

    public SingleChoicePreferenceCategory(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.choiceCategoryPreferenceStyle);
    }

    public SingleChoicePreferenceCategory(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public SingleChoicePreferenceCategory(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mCheckedPosition = -1;
        this.mCheckedChoice = null;
        this.mInternalListener = new OnPreferenceChangeInternalListener() { // from class: miuix.preference.SingleChoicePreferenceCategory.1
            /* JADX WARN: Multi-variable type inference failed */
            @Override // miuix.preference.OnPreferenceChangeInternalListener
            public boolean onPreferenceChangeInternal(Preference preference, Object obj) {
                boolean zIsChecked = ((Checkable) preference).isChecked();
                Preference.OnPreferenceClickListener onPreferenceClickListener = SingleChoicePreferenceCategory.this.getOnPreferenceClickListener();
                if (onPreferenceClickListener != null) {
                    SingleChoicePreferenceCategory.this.checkPreferenceByInternal(preference, obj);
                    onPreferenceClickListener.onPreferenceClick(SingleChoicePreferenceCategory.this);
                }
                return !zIsChecked;
            }

            @Override // miuix.preference.OnPreferenceChangeInternalListener
            public void notifyPreferenceChangeInternal(Preference preference) {
                SingleChoiceHelper singleChoiceHelper = SingleChoicePreferenceCategory.this.parse(preference);
                SingleChoicePreferenceCategory.this.updateCheckedPreference(singleChoiceHelper);
                SingleChoicePreferenceCategory.this.updateCheckedPosition(singleChoiceHelper);
                SingleChoicePreferenceCategory singleChoicePreferenceCategory = SingleChoicePreferenceCategory.this;
                singleChoicePreferenceCategory.updateCheckedValue(singleChoiceHelper, singleChoicePreferenceCategory.mCheckedPosition);
            }
        };
        this.mContext = context;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ChoicePreferenceCategory, i, i2);
        this.mEntries = typedArrayObtainStyledAttributes.getTextArray(R.styleable.ChoicePreferenceCategory_android_entries);
        this.mEntryValues = typedArrayObtainStyledAttributes.getTextArray(R.styleable.ChoicePreferenceCategory_android_entryValues);
        this.mSummaries = typedArrayObtainStyledAttributes.getTextArray(R.styleable.ChoicePreferenceCategory_summaries);
        this.mCardGroupEnabled = typedArrayObtainStyledAttributes.getBoolean(R.styleable.ChoicePreferenceCategory_cardGroupEnabled, true);
        typedArrayObtainStyledAttributes.recycle();
    }

    public void setEntries(CharSequence[] charSequenceArr) {
        this.mEntries = charSequenceArr;
    }

    public void setEntries(int i) {
        setEntries(getContext().getResources().getTextArray(i));
    }

    public CharSequence[] getEntries() {
        return this.mEntries;
    }

    public void setEntryValues(CharSequence[] charSequenceArr) {
        this.mEntryValues = charSequenceArr;
    }

    public void setEntryValues(int i) {
        setEntryValues(getContext().getResources().getTextArray(i));
    }

    public CharSequence[] getEntryValues() {
        return this.mEntryValues;
    }

    public int getValueIndex() {
        return findIndexOfValue(this.mValue);
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
        updateCheckedValue(singleChoiceHelper, this.mCheckedPosition);
    }

    private void clearChecked() {
        SingleChoiceHelper singleChoiceHelper = this.mCheckedChoice;
        if (singleChoiceHelper != null) {
            singleChoiceHelper.setChecked(false);
        }
        this.mCheckedChoice = null;
        this.mCheckedPosition = -1;
    }

    @Override // androidx.preference.PreferenceGroup, androidx.preference.Preference
    public void onAttached() {
        super.onAttached();
        init();
    }

    private void init() {
        constructEntries();
    }

    private void constructEntries() {
        CharSequence[] charSequenceArr = this.mEntries;
        if (charSequenceArr != null) {
            int length = charSequenceArr.length;
            for (int i = 0; i < length; i++) {
                String str = (String) this.mEntries[i];
                String str2 = (String) this.mEntryValues[i];
                SingleChoicePreference singleChoicePreference = new SingleChoicePreference(this.mContext);
                singleChoicePreference.setTitle(str);
                singleChoicePreference.setValue(str2);
                CharSequence[] charSequenceArr2 = this.mSummaries;
                if (charSequenceArr2 != null) {
                    singleChoicePreference.setSummary((String) charSequenceArr2[i]);
                }
                addPreference(singleChoicePreference);
            }
        }
    }

    public void setValue(String str) {
        boolean z = !TextUtils.equals(this.mValue, str);
        if (z || !this.mValueSet) {
            this.mValue = str;
            this.mValueSet = true;
            persistString(str);
            if (z) {
                notifyChanged();
            }
        }
    }

    public String getValue() {
        return this.mValue;
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
        if (TextUtils.equals(this.mValue, singleChoiceHelper.getValue())) {
            singleChoiceHelper.setChecked(true);
        }
        return zAddPreference;
    }

    @Override // androidx.preference.PreferenceGroup
    public boolean removePreference(Preference preference) {
        return super.removePreference(preference);
    }

    public boolean getCardGroupEnabled() {
        return this.mCardGroupEnabled;
    }

    public void enableCardGroup(boolean z) {
        this.mCardGroupEnabled = z;
    }

    @Override // androidx.preference.Preference
    protected void onSetInitialValue(Object obj) {
        setValue(getPersistedString((String) obj));
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

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCheckedValue(SingleChoiceHelper singleChoiceHelper, int i) {
        if (singleChoiceHelper.isChecked()) {
            setValue(singleChoiceHelper.getValue());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SingleChoiceHelper parse(Preference preference) {
        if (preference instanceof SingleChoicePreference) {
            return new PreferenceSingleChoiceHelper((SingleChoicePreference) preference);
        }
        throw new IllegalArgumentException("Only SingleChoicePreference can be added to SingleChoicePreference2");
    }

    public int findIndexOfValue(String str) {
        if (this.mEntryValues == null) {
            return -1;
        }
        int i = 0;
        while (true) {
            CharSequence[] charSequenceArr = this.mEntryValues;
            if (i >= charSequenceArr.length) {
                return -1;
            }
            if (TextUtils.equals(charSequenceArr[i], str)) {
                return i;
            }
            i++;
        }
    }

    private static class PreferenceSingleChoiceHelper extends SingleChoiceHelper {
        SingleChoicePreference mPreference;

        PreferenceSingleChoiceHelper(SingleChoicePreference singleChoicePreference) {
            super(singleChoicePreference);
            this.mPreference = singleChoicePreference;
        }

        @Override // miuix.preference.SingleChoicePreferenceCategory.SingleChoiceHelper
        void setOnPreferenceChangeInternalListener(OnPreferenceChangeInternalListener onPreferenceChangeInternalListener) {
            this.mPreference.setOnPreferenceChangeInternalListener(onPreferenceChangeInternalListener);
        }

        @Override // miuix.preference.SingleChoicePreferenceCategory.SingleChoiceHelper
        Preference getPreference() {
            return this.mPreference;
        }

        @Override // miuix.preference.SingleChoicePreferenceCategory.SingleChoiceHelper
        String getValue() {
            return this.mPreference.getValue();
        }
    }

    private static abstract class SingleChoiceHelper implements Checkable {
        Checkable mCheckable;

        abstract Preference getPreference();

        abstract String getValue();

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
