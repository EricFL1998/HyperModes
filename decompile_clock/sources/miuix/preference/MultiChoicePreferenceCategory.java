package miuix.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.Checkable;
import androidx.preference.Preference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/* JADX INFO: loaded from: classes3.dex */
public class MultiChoicePreferenceCategory extends androidx.preference.PreferenceCategory {
    private static final String TAG = "MultiChoicePreferenceCategory";
    private boolean cardGroupEnabled;
    private MultiChoiceHelper mCheckedChoice;
    private Context mContext;
    private CharSequence[] mEntries;
    private CharSequence[] mEntryValues;
    private OnPreferenceChangeInternalListener mInternalListener;
    private CharSequence[] mSummaries;
    private Set<String> mValues;

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePreferenceByInternal(Preference preference, Object obj) {
        Preference parent = preference.getParent() instanceof RadioSetPreferenceCategory ? preference.getParent() : preference;
        MultiChoiceHelper multiChoiceHelper = this.mCheckedChoice;
        if ((multiChoiceHelper == null || parent != multiChoiceHelper.getPreference()) && callChangeListenerByInternal(obj, parent)) {
            updateCheckablePreference(preference);
        }
    }

    private boolean callChangeListenerByInternal(Object obj, Preference preference) {
        return preference.getOnPreferenceChangeListener() == null || preference.getOnPreferenceChangeListener().onPreferenceChange(preference, obj);
    }

    public MultiChoicePreferenceCategory(Context context) {
        this(context, null);
    }

    public MultiChoicePreferenceCategory(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.choiceCategoryPreferenceStyle);
    }

    public MultiChoicePreferenceCategory(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public MultiChoicePreferenceCategory(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mValues = new HashSet();
        this.mCheckedChoice = null;
        this.mInternalListener = new OnPreferenceChangeInternalListener() { // from class: miuix.preference.MultiChoicePreferenceCategory.1
            @Override // miuix.preference.OnPreferenceChangeInternalListener
            public boolean onPreferenceChangeInternal(Preference preference, Object obj) {
                Preference.OnPreferenceClickListener onPreferenceClickListener = MultiChoicePreferenceCategory.this.getOnPreferenceClickListener();
                if (onPreferenceClickListener == null) {
                    return true;
                }
                MultiChoicePreferenceCategory.this.updatePreferenceByInternal(preference, obj);
                onPreferenceClickListener.onPreferenceClick(MultiChoicePreferenceCategory.this);
                return true;
            }

            @Override // miuix.preference.OnPreferenceChangeInternalListener
            public void notifyPreferenceChangeInternal(Preference preference) {
                MultiChoiceHelper multiChoiceHelper = MultiChoicePreferenceCategory.this.parse(preference);
                HashSet hashSet = new HashSet(MultiChoicePreferenceCategory.this.mValues);
                if (multiChoiceHelper.isChecked()) {
                    if (hashSet.contains(multiChoiceHelper.getValue())) {
                        return;
                    } else {
                        hashSet.add(multiChoiceHelper.getValue());
                    }
                } else if (!hashSet.contains(multiChoiceHelper.getValue())) {
                    return;
                } else {
                    hashSet.remove(multiChoiceHelper.getValue());
                }
                MultiChoicePreferenceCategory.this.setValues(hashSet);
            }
        };
        this.mContext = context;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ChoicePreferenceCategory, i, i2);
        this.mEntries = typedArrayObtainStyledAttributes.getTextArray(R.styleable.ChoicePreferenceCategory_android_entries);
        this.mEntryValues = typedArrayObtainStyledAttributes.getTextArray(R.styleable.ChoicePreferenceCategory_android_entryValues);
        this.mSummaries = typedArrayObtainStyledAttributes.getTextArray(R.styleable.ChoicePreferenceCategory_summaries);
        this.cardGroupEnabled = typedArrayObtainStyledAttributes.getBoolean(R.styleable.ChoicePreferenceCategory_cardGroupEnabled, true);
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

    public void updateCheckablePreference(Preference preference) {
        parse(preference).toggle();
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
        int length = this.mEntries.length;
        for (int i = 0; i < length; i++) {
            String str = (String) this.mEntries[i];
            String str2 = (String) this.mEntryValues[i];
            MultiChoicePreference multiChoicePreference = new MultiChoicePreference(this.mContext);
            multiChoicePreference.setTitle(str);
            multiChoicePreference.setValue(str2);
            CharSequence[] charSequenceArr = this.mSummaries;
            if (charSequenceArr != null) {
                multiChoicePreference.setSummary((String) charSequenceArr[i]);
            }
            addPreference(multiChoicePreference);
        }
    }

    public void setValues(Set<String> set) {
        this.mValues.clear();
        this.mValues.addAll(set);
        persistStringSet(set);
        notifyChanged();
    }

    public Set<String> getValues() {
        return this.mValues;
    }

    @Override // androidx.preference.PreferenceGroup
    public boolean addPreference(Preference preference) {
        MultiChoiceHelper multiChoiceHelper = parse(preference);
        boolean zAddPreference = super.addPreference(preference);
        if (zAddPreference) {
            multiChoiceHelper.setOnPreferenceChangeInternalListener(this.mInternalListener);
        }
        if (this.mValues.contains(((MultiChoicePreference) preference).getValue())) {
            multiChoiceHelper.setChecked(true);
        }
        return zAddPreference;
    }

    @Override // androidx.preference.PreferenceGroup
    public boolean removePreference(Preference preference) {
        return super.removePreference(preference);
    }

    public boolean getCardGroupEnabled() {
        return this.cardGroupEnabled;
    }

    public void enableCardGroup(boolean z) {
        this.cardGroupEnabled = z;
    }

    @Override // androidx.preference.Preference
    protected void onSetInitialValue(Object obj) {
        setValues(getPersistedStringSet((Set) obj));
    }

    private void updateCheckedPreference(MultiChoiceHelper multiChoiceHelper) {
        if (multiChoiceHelper.isChecked()) {
            MultiChoiceHelper multiChoiceHelper2 = this.mCheckedChoice;
            if (multiChoiceHelper2 != null && multiChoiceHelper2.getPreference() != multiChoiceHelper.getPreference()) {
                this.mCheckedChoice.setChecked(false);
            }
            this.mCheckedChoice = multiChoiceHelper;
        }
    }

    private void updateCheckedPosition(MultiChoiceHelper multiChoiceHelper) {
        if (multiChoiceHelper.isChecked()) {
            int preferenceCount = getPreferenceCount();
            for (int i = 0; i < preferenceCount && getPreference(i) != multiChoiceHelper.getPreference(); i++) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public MultiChoiceHelper parse(Preference preference) {
        if (preference instanceof MultiChoicePreference) {
            return new PreferenceSingleChoiceHelper((MultiChoicePreference) preference);
        }
        throw new IllegalArgumentException("Only SingleChoicePreference can be added to MultiChoicePreferenceCategory");
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

    @Override // androidx.preference.PreferenceGroup, androidx.preference.Preference
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelableOnSaveInstanceState = super.onSaveInstanceState();
        if (isPersistent()) {
            return parcelableOnSaveInstanceState;
        }
        SavedState savedState = new SavedState(parcelableOnSaveInstanceState);
        savedState.mValues = getValues();
        return savedState;
    }

    @Override // androidx.preference.PreferenceGroup, androidx.preference.Preference
    protected void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable == null || !parcelable.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        setValues(savedState.mValues);
    }

    private static class PreferenceSingleChoiceHelper extends MultiChoiceHelper {
        MultiChoicePreference mPreference;

        PreferenceSingleChoiceHelper(MultiChoicePreference multiChoicePreference) {
            super(multiChoicePreference);
            this.mPreference = multiChoicePreference;
        }

        @Override // miuix.preference.MultiChoicePreferenceCategory.MultiChoiceHelper
        void setOnPreferenceChangeInternalListener(OnPreferenceChangeInternalListener onPreferenceChangeInternalListener) {
            this.mPreference.setOnPreferenceChangeInternalListener(onPreferenceChangeInternalListener);
        }

        @Override // miuix.preference.MultiChoicePreferenceCategory.MultiChoiceHelper
        Preference getPreference() {
            return this.mPreference;
        }

        @Override // miuix.preference.MultiChoicePreferenceCategory.MultiChoiceHelper
        String getValue() {
            return this.mPreference.getValue();
        }
    }

    private static abstract class MultiChoiceHelper implements Checkable {
        Checkable mCheckable;

        abstract Preference getPreference();

        abstract String getValue();

        abstract void setOnPreferenceChangeInternalListener(OnPreferenceChangeInternalListener onPreferenceChangeInternalListener);

        MultiChoiceHelper(Checkable checkable) {
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

    private static class SavedState extends Preference.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: miuix.preference.MultiChoicePreferenceCategory.SavedState.1
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        Set<String> mValues;

        SavedState(Parcel parcel) {
            super(parcel);
            int i = parcel.readInt();
            this.mValues = new HashSet();
            String[] strArr = new String[i];
            parcel.readStringArray(strArr);
            Collections.addAll(this.mValues, strArr);
        }

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        @Override // android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.mValues.size());
            Set<String> set = this.mValues;
            parcel.writeStringArray((String[]) set.toArray(new String[set.size()]));
        }
    }
}
