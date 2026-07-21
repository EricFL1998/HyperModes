package miuix.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import androidx.core.content.res.TypedArrayUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import miuix.appcompat.adapter.SpinnerDoubleLineContentAdapter;
import miuix.appcompat.internal.adapter.SpinnerCheckableArrayAdapter;
import miuix.appcompat.widget.Spinner;
import miuix.core.util.MiuixUIUtils;
import miuix.flexible.view.HyperCellLayout;

/* JADX INFO: loaded from: classes3.dex */
public class DropDownPreference extends BasePreference {
    private static final Class<?>[] ADAPTER_CONSTRUCTOR_SIGNATURE = {Context.class, AttributeSet.class};
    private static final CharSequence[] EMPTY = new CharSequence[0];
    private static final String TAG = "DropDownPreference";
    private ArrayAdapter mAdapter;
    private ArrayAdapter mContentAdapter;
    private float mDimAmount;
    private boolean mDimVisible;
    private CharSequence[] mEntries;
    private Drawable[] mEntryIcons;
    private CharSequence[] mEntryValues;
    private boolean mIconOnlyEnabled;
    private final AdapterView.OnItemSelectedListener mItemSelectedListener;
    private boolean mLargeFont;
    private Handler mNotifyHandler;
    private Spinner mSpinner;
    private String mValue;
    private boolean mValueSet;
    private PreferenceViewHolder mViewHolder;
    private AdapterView.OnItemClickListener onItemClickListener;

    /* JADX INFO: Access modifiers changed from: private */
    public void splitSpinnerTextAtLargeFont(int i) {
        CharSequence[] charSequenceArr;
        PreferenceViewHolder preferenceViewHolder = this.mViewHolder;
        if (preferenceViewHolder == null || preferenceViewHolder.itemView == null || !(this.mViewHolder.itemView instanceof HyperCellLayout) || !this.mLargeFont) {
            return;
        }
        CharSequence charSequence = (i < 0 || (charSequenceArr = this.mEntries) == null || i >= charSequenceArr.length) ? null : charSequenceArr[i];
        TextView textView = (TextView) this.mViewHolder.itemView.findViewById(android.R.id.text1);
        if (textView != null) {
            textView.setText(charSequence);
        }
    }

    public DropDownPreference(Context context) {
        this(context, null);
    }

    public DropDownPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.dropdownPreferenceStyle);
    }

    public DropDownPreference(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public DropDownPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mLargeFont = false;
        this.mDimAmount = Float.MAX_VALUE;
        this.mDimVisible = true;
        this.mIconOnlyEnabled = false;
        this.mNotifyHandler = new Handler();
        this.mItemSelectedListener = new AdapterView.OnItemSelectedListener() { // from class: miuix.preference.DropDownPreference.1
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i3, long j) {
                DropDownPreference.this.splitSpinnerTextAtLargeFont(i3);
                if (i3 >= 0 && i3 < DropDownPreference.this.mEntryValues.length) {
                    final String str = (String) DropDownPreference.this.mEntryValues[i3];
                    DropDownPreference.this.mNotifyHandler.post(new Runnable() { // from class: miuix.preference.DropDownPreference.1.1
                        @Override // java.lang.Runnable
                        public void run() {
                            if (str.equals(DropDownPreference.this.getValue()) || !DropDownPreference.this.callChangeListener(str)) {
                                return;
                            }
                            DropDownPreference.this.setValue(str);
                        }
                    });
                } else {
                    Log.d(DropDownPreference.TAG, "Illegal Position In Entry Values' Array. ");
                }
            }
        };
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.DropDownPreference, i, i2);
        String string = typedArrayObtainStyledAttributes.getString(R.styleable.DropDownPreference_adapter);
        boolean z = typedArrayObtainStyledAttributes.getBoolean(R.styleable.DropDownPreference_dimVisible, true);
        this.mIconOnlyEnabled = typedArrayObtainStyledAttributes.getBoolean(R.styleable.DropDownPreference_iconOnlyEnabled, false);
        typedArrayObtainStyledAttributes.recycle();
        if (!TextUtils.isEmpty(string)) {
            this.mContentAdapter = initAdapter(context, attributeSet, string);
        } else {
            this.mContentAdapter = new DropDownLayoutAdapter(context, attributeSet, i, i2);
        }
        this.mAdapter = createAdapter();
        constructEntries();
        setDimVisible(z);
    }

    @Override // androidx.preference.Preference
    public void onAttached() {
        super.onAttached();
        this.mLargeFont = MiuixUIUtils.getFontLevel(getContext()) == 2;
        int layoutResource = getLayoutResource();
        if (layoutResource == R.layout.miuix_preference_flexible_layout || layoutResource == R.layout.miuix_dropdown_preference_flexible_layout) {
            setLayoutResource(this.mLargeFont ? R.layout.miuix_dropdown_preference_flexible_layout : R.layout.miuix_preference_flexible_layout);
        }
    }

    private void constructEntries() {
        ArrayAdapter arrayAdapter = this.mContentAdapter;
        if (arrayAdapter instanceof DropDownLayoutAdapter) {
            this.mEntries = ((DropDownLayoutAdapter) arrayAdapter).getEntries();
            this.mEntryValues = ((DropDownLayoutAdapter) this.mContentAdapter).getEntryValues();
            this.mEntryIcons = ((DropDownLayoutAdapter) this.mContentAdapter).getEntryIcons();
            return;
        }
        int count = arrayAdapter.getCount();
        this.mEntries = new CharSequence[this.mContentAdapter.getCount()];
        for (int i = 0; i < count; i++) {
            this.mEntries[i] = this.mContentAdapter.getItem(i).toString();
        }
        this.mEntryValues = this.mEntries;
        this.mEntryIcons = null;
    }

    ArrayAdapter createAdapter() {
        return new SpinnerCheckableArrayAdapter(getContext(), this.mContentAdapter, new PreferenceCheckedProvider(this, this.mContentAdapter));
    }

    private ArrayAdapter initAdapter(Context context, AttributeSet attributeSet, String str) {
        try {
            Constructor constructor = context.getClassLoader().loadClass(str).asSubclass(ArrayAdapter.class).getConstructor(ADAPTER_CONSTRUCTOR_SIGNATURE);
            constructor.setAccessible(true);
            return (ArrayAdapter) constructor.newInstance(context, attributeSet);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Can't find Adapter: " + str, e);
        } catch (IllegalAccessException e2) {
            throw new IllegalStateException("Can't access non-public constructor " + str, e2);
        } catch (InstantiationException e3) {
            e = e3;
            throw new IllegalStateException("Could not instantiate the Adapter: " + str, e);
        } catch (NoSuchMethodException e4) {
            throw new IllegalStateException("Error creating Adapter " + str, e4);
        } catch (InvocationTargetException e5) {
            e = e5;
            throw new IllegalStateException("Could not instantiate the Adapter: " + str, e);
        }
    }

    @Override // androidx.preference.Preference
    protected Object onGetDefaultValue(TypedArray typedArray, int i) {
        return typedArray.getString(i);
    }

    public void setAdapter(ArrayAdapter arrayAdapter) {
        this.mContentAdapter = arrayAdapter;
        this.mAdapter = createAdapter();
        constructEntries();
    }

    public void setDimAmount(float f) {
        this.mDimAmount = f;
    }

    public void setDimVisible(boolean z) {
        this.mDimVisible = z;
    }

    public boolean getDimVisible() {
        return this.mDimVisible;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setIconOnlyEnabled(boolean z) {
        this.mIconOnlyEnabled = z;
        ArrayAdapter arrayAdapter = this.mContentAdapter;
        if (arrayAdapter instanceof DropDownLayoutAdapter) {
            ((DropDownLayoutAdapter) arrayAdapter).setIconOnlyEnabled(z);
            notifyChanged();
        }
    }

    public boolean isIconOnlyEnabled() {
        return this.mIconOnlyEnabled;
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

    @Override // androidx.preference.Preference
    protected void onSetInitialValue(Object obj) {
        setValue(getPersistedString((String) obj));
    }

    @Override // androidx.preference.Preference
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelableOnSaveInstanceState = super.onSaveInstanceState();
        if (isPersistent()) {
            return parcelableOnSaveInstanceState;
        }
        SavedState savedState = new SavedState(parcelableOnSaveInstanceState);
        savedState.mValue = getValue();
        return savedState;
    }

    @Override // androidx.preference.Preference
    protected void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable == null || !parcelable.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        setValue(savedState.mValue);
    }

    @Override // androidx.preference.Preference
    protected void notifyChanged() {
        super.notifyChanged();
        if (this.mAdapter != null) {
            this.mNotifyHandler.post(new Runnable() { // from class: miuix.preference.DropDownPreference.2
                @Override // java.lang.Runnable
                public void run() {
                    DropDownPreference.this.mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override // androidx.preference.Preference
    protected void performClick(View view) {
        Spinner spinner = this.mSpinner;
        if (spinner != null) {
            spinner.performClick();
            Log.d(TAG, "trigger from perform click");
        }
    }

    private void disableSpinnerClick(Spinner spinner) {
        spinner.setClickable(false);
        spinner.setLongClickable(false);
        spinner.setContextClickable(false);
    }

    @Override // miuix.preference.BasePreference, androidx.preference.Preference
    public void onBindViewHolder(final PreferenceViewHolder preferenceViewHolder) {
        this.mViewHolder = preferenceViewHolder;
        this.mLargeFont = MiuixUIUtils.getFontLevel(getContext()) == 2;
        if (this.mAdapter.getCount() > 0) {
            this.mSpinner = (Spinner) preferenceViewHolder.itemView.findViewById(R.id.spinner);
            replaceLayoutAtLargeFont(preferenceViewHolder);
            this.mSpinner.setImportantForAccessibility(2);
            disableSpinnerClick(this.mSpinner);
            this.mSpinner.setAdapter((SpinnerAdapter) this.mAdapter);
            this.mSpinner.setOnItemSelectedListener(null);
            this.mSpinner.setSelection(findSpinnerIndexOfValue(getValue()));
            this.mSpinner.post(new Runnable() { // from class: miuix.preference.DropDownPreference.3
                @Override // java.lang.Runnable
                public void run() {
                    DropDownPreference.this.showSelectedItemAtLargeFont(preferenceViewHolder);
                    DropDownPreference.this.mSpinner.setOnItemSelectedListener(DropDownPreference.this.mItemSelectedListener);
                }
            });
            this.mSpinner.setOnSpinnerDismissListener(new Spinner.OnSpinnerDismissListener() { // from class: miuix.preference.DropDownPreference.4
                @Override // miuix.appcompat.widget.Spinner.OnSpinnerDismissListener
                public void onSpinnerDismiss() {
                    preferenceViewHolder.itemView.setActivated(false);
                }
            });
            AdapterView.OnItemClickListener onItemClickListener = this.onItemClickListener;
            if (onItemClickListener != null) {
                this.mSpinner.setOnItemClickListener(onItemClickListener);
            }
            if (this.mDimVisible) {
                Spinner spinner = this.mSpinner;
                spinner.setWindowManagerFlags(2 | spinner.getWindowManagerFlag());
            } else {
                Spinner spinner2 = this.mSpinner;
                spinner2.setWindowManagerFlags(spinner2.getWindowManagerFlag() & (-3));
            }
            float f = this.mDimAmount;
            if (f != Float.MAX_VALUE) {
                this.mSpinner.setDimAmount(f);
            }
        }
        preferenceViewHolder.itemView.setOnTouchListener(new View.OnTouchListener() { // from class: miuix.preference.DropDownPreference.5
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0) {
                    view.setPressed(true);
                }
                if (motionEvent.getAction() == 1) {
                    preferenceViewHolder.itemView.setActivated(true);
                    if (DropDownPreference.this.mSpinner != null) {
                        DropDownPreference.this.mSpinner.performClick();
                        DropDownPreference.this.mSpinner.setActivated(false);
                    }
                    TextView textView = (TextView) preferenceViewHolder.itemView.findViewById(android.R.id.title);
                    if (textView != null) {
                        textView.setActivated(false);
                    }
                    TextView textView2 = (TextView) preferenceViewHolder.itemView.findViewById(android.R.id.summary);
                    if (textView2 != null) {
                        textView2.setActivated(false);
                    }
                }
                return false;
            }
        });
        super.onBindViewHolder(preferenceViewHolder);
    }

    private void replaceLayoutAtLargeFont(PreferenceViewHolder preferenceViewHolder) {
        if (preferenceViewHolder == null || preferenceViewHolder.itemView == null || !(preferenceViewHolder.itemView instanceof HyperCellLayout) || !this.mLargeFont) {
            return;
        }
        this.mAdapter = new SpinnerCheckableArrayAdapter(getContext(), R.layout.miuix_appcompat_simple_spinner_flexible_layout_integrated, this.mContentAdapter, new PreferenceCheckedProvider(this, this.mContentAdapter));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showSelectedItemAtLargeFont(PreferenceViewHolder preferenceViewHolder) {
        TextView textView;
        if (preferenceViewHolder == null || preferenceViewHolder.itemView == null || !(preferenceViewHolder.itemView instanceof HyperCellLayout) || !this.mLargeFont || (textView = (TextView) preferenceViewHolder.itemView.findViewById(android.R.id.text1)) == null) {
            return;
        }
        textView.setText((CharSequence) this.mSpinner.getSelectedItem());
    }

    public void setSummaries(CharSequence[] charSequenceArr) {
        ArrayAdapter arrayAdapter = this.mContentAdapter;
        if (arrayAdapter instanceof DropDownLayoutAdapter) {
            ((DropDownLayoutAdapter) arrayAdapter).setSummaries(charSequenceArr);
            notifyChanged();
        }
    }

    public CharSequence[] getSummaries() {
        ArrayAdapter arrayAdapter = this.mContentAdapter;
        if (arrayAdapter instanceof DropDownLayoutAdapter) {
            ((DropDownLayoutAdapter) arrayAdapter).getSummaries();
        }
        return EMPTY;
    }

    public void setEntryIcons(int[] iArr) {
        ArrayAdapter arrayAdapter = this.mContentAdapter;
        if (arrayAdapter instanceof DropDownLayoutAdapter) {
            ((DropDownLayoutAdapter) arrayAdapter).setEntryIcons(iArr);
            this.mEntryIcons = ((DropDownLayoutAdapter) this.mContentAdapter).getEntryIcons();
        }
        notifyChanged();
    }

    public void setEntryIcons(Drawable[] drawableArr) {
        ArrayAdapter arrayAdapter = this.mContentAdapter;
        if (arrayAdapter instanceof DropDownLayoutAdapter) {
            ((DropDownLayoutAdapter) arrayAdapter).setEntryIcons(drawableArr);
            this.mEntryIcons = ((DropDownLayoutAdapter) this.mContentAdapter).getEntryIcons();
        }
        notifyChanged();
    }

    public Drawable[] getEntryIcons() {
        return this.mEntryIcons;
    }

    public void setEntries(CharSequence[] charSequenceArr) {
        this.mEntries = charSequenceArr;
        ArrayAdapter arrayAdapter = this.mContentAdapter;
        if (arrayAdapter instanceof DropDownLayoutAdapter) {
            ((DropDownLayoutAdapter) arrayAdapter).setEntries(charSequenceArr);
        } else {
            arrayAdapter.clear();
            this.mContentAdapter.addAll(charSequenceArr);
            this.mEntryValues = this.mEntries;
        }
        Spinner spinner = this.mSpinner;
        if (spinner != null) {
            spinner.setSelection(findSpinnerIndexOfValue(getValue()));
        }
        notifyChanged();
    }

    public void setEntries(int i) {
        setEntries(getContext().getResources().getTextArray(i));
    }

    public CharSequence[] getEntries() {
        return this.mEntries;
    }

    public void setEntryValues(CharSequence[] charSequenceArr) {
        ArrayAdapter arrayAdapter = this.mContentAdapter;
        if (arrayAdapter instanceof DropDownLayoutAdapter) {
            ((DropDownLayoutAdapter) arrayAdapter).setEntryValues(charSequenceArr);
            this.mAdapter.notifyDataSetChanged();
            this.mEntryValues = charSequenceArr;
        }
    }

    public void setEntryValues(int i) {
        setEntryValues(getContext().getResources().getTextArray(i));
    }

    public CharSequence[] getEntryValues() {
        ArrayAdapter arrayAdapter = this.mContentAdapter;
        if (arrayAdapter instanceof DropDownLayoutAdapter) {
            return ((DropDownLayoutAdapter) arrayAdapter).getEntryValues();
        }
        return EMPTY;
    }

    public int getValueIndex() {
        return findIndexOfValue(this.mValue);
    }

    public void setValueIndex(int i) {
        if (i >= 0) {
            CharSequence[] charSequenceArr = this.mEntryValues;
            if (i < charSequenceArr.length) {
                setValue(charSequenceArr[i].toString());
                Spinner spinner = this.mSpinner;
                if (spinner != null) {
                    spinner.setSelection(i);
                    return;
                }
                return;
            }
        }
        Log.e(TAG, "Index out of range.");
    }

    public int findIndexOfValue(String str) {
        return findSpinnerIndexOfValue(str);
    }

    private int findSpinnerIndexOfValue(String str) {
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

    private static class SavedState extends Preference.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: miuix.preference.DropDownPreference.SavedState.1
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        String mValue;

        SavedState(Parcel parcel) {
            super(parcel);
            this.mValue = parcel.readString();
        }

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        @Override // android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeString(this.mValue);
        }
    }

    private static class PreferenceCheckedProvider implements SpinnerCheckableArrayAdapter.CheckedStateProvider {
        private ArrayAdapter mAdapter;
        private DropDownPreference mPreference;

        public PreferenceCheckedProvider(DropDownPreference dropDownPreference, ArrayAdapter arrayAdapter) {
            this.mPreference = dropDownPreference;
            this.mAdapter = arrayAdapter;
        }

        @Override // miuix.appcompat.internal.adapter.SpinnerCheckableArrayAdapter.CheckedStateProvider
        public boolean isChecked(int i) {
            if (i < this.mPreference.mEntryValues.length && i >= 0) {
                return TextUtils.equals(this.mPreference.getValue(), this.mPreference.mEntryValues[i]);
            }
            Log.e(DropDownPreference.TAG, "pos out of entries' length.");
            return false;
        }
    }

    private static class DropDownLayoutAdapter extends SpinnerDoubleLineContentAdapter {
        private CharSequence[] mValues;

        DropDownLayoutAdapter(Context context, AttributeSet attributeSet, int i, int i2) {
            int[] iArr;
            super(context, 0);
            TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.DropDownPreference, i, i2);
            this.mEntries = TypedArrayUtils.getTextArray(typedArrayObtainStyledAttributes, R.styleable.DropDownPreference_entries, 0);
            this.mValues = TypedArrayUtils.getTextArray(typedArrayObtainStyledAttributes, R.styleable.DropDownPreference_entryValues, 0);
            this.mSummaries = TypedArrayUtils.getTextArray(typedArrayObtainStyledAttributes, R.styleable.DropDownPreference_entrySummaries, 0);
            this.mIconOnlyEnabled = TypedArrayUtils.getBoolean(typedArrayObtainStyledAttributes, R.styleable.DropDownPreference_iconOnlyEnabled, 0, false);
            int resourceId = typedArrayObtainStyledAttributes.getResourceId(R.styleable.DropDownPreference_entryIcons, -1);
            typedArrayObtainStyledAttributes.recycle();
            if (resourceId > 0) {
                TypedArray typedArrayObtainTypedArray = context.getResources().obtainTypedArray(resourceId);
                iArr = new int[typedArrayObtainTypedArray.length()];
                for (int i3 = 0; i3 < typedArrayObtainTypedArray.length(); i3++) {
                    iArr[i3] = typedArrayObtainTypedArray.getResourceId(i3, 0);
                }
                typedArrayObtainTypedArray.recycle();
            } else {
                iArr = null;
            }
            setEntryIcons(iArr);
        }

        public void setEntryValues(CharSequence[] charSequenceArr) {
            this.mValues = charSequenceArr;
        }

        public CharSequence[] getEntryValues() {
            return this.mValues;
        }
    }
}
