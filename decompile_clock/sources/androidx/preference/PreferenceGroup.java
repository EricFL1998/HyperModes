package androidx.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import androidx.collection.SimpleArrayMap;
import androidx.core.content.res.TypedArrayUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public abstract class PreferenceGroup extends Preference {
    private static final String TAG = "PreferenceGroup";
    private boolean mAttachedToHierarchy;
    private final Runnable mClearRecycleCacheRunnable;
    private int mCurrentPreferenceOrder;
    private final Handler mHandler;
    final SimpleArrayMap<String, Long> mIdRecycleCache;
    private int mInitialExpandedChildrenCount;
    private OnExpandButtonClickListener mOnExpandButtonClickListener;
    private boolean mOrderingAsAdded;
    private final List<Preference> mPreferences;

    public interface OnExpandButtonClickListener {
        void onExpandButtonClick();
    }

    public interface PreferencePositionCallback {
        int getPreferenceAdapterPosition(Preference preference);

        int getPreferenceAdapterPosition(String str);
    }

    protected boolean isOnSameScreenAsChildren() {
        return true;
    }

    public PreferenceGroup(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mIdRecycleCache = new SimpleArrayMap<>();
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mOrderingAsAdded = true;
        this.mCurrentPreferenceOrder = 0;
        this.mAttachedToHierarchy = false;
        this.mInitialExpandedChildrenCount = Integer.MAX_VALUE;
        this.mOnExpandButtonClickListener = null;
        this.mClearRecycleCacheRunnable = new Runnable() { // from class: androidx.preference.PreferenceGroup.1
            @Override // java.lang.Runnable
            public void run() {
                synchronized (this) {
                    PreferenceGroup.this.mIdRecycleCache.clear();
                }
            }
        };
        this.mPreferences = new ArrayList();
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.PreferenceGroup, i, i2);
        this.mOrderingAsAdded = TypedArrayUtils.getBoolean(typedArrayObtainStyledAttributes, R.styleable.PreferenceGroup_orderingFromXml, R.styleable.PreferenceGroup_orderingFromXml, true);
        if (typedArrayObtainStyledAttributes.hasValue(R.styleable.PreferenceGroup_initialExpandedChildrenCount)) {
            setInitialExpandedChildrenCount(TypedArrayUtils.getInt(typedArrayObtainStyledAttributes, R.styleable.PreferenceGroup_initialExpandedChildrenCount, R.styleable.PreferenceGroup_initialExpandedChildrenCount, Integer.MAX_VALUE));
        }
        typedArrayObtainStyledAttributes.recycle();
    }

    public PreferenceGroup(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public PreferenceGroup(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public void setOrderingAsAdded(boolean z) {
        this.mOrderingAsAdded = z;
    }

    public boolean isOrderingAsAdded() {
        return this.mOrderingAsAdded;
    }

    public void setInitialExpandedChildrenCount(int i) {
        if (i != Integer.MAX_VALUE && !hasKey()) {
            Log.e(TAG, getClass().getSimpleName() + " should have a key defined if it contains an expandable preference");
        }
        this.mInitialExpandedChildrenCount = i;
    }

    public int getInitialExpandedChildrenCount() {
        return this.mInitialExpandedChildrenCount;
    }

    public void addItemFromInflater(Preference preference) {
        addPreference(preference);
    }

    public int getPreferenceCount() {
        return this.mPreferences.size();
    }

    public Preference getPreference(int i) {
        return this.mPreferences.get(i);
    }

    public boolean addPreference(Preference preference) {
        long nextId;
        if (this.mPreferences.contains(preference)) {
            return true;
        }
        if (preference.getKey() != null) {
            PreferenceGroup parent = this;
            while (parent.getParent() != null) {
                parent = parent.getParent();
            }
            String key = preference.getKey();
            if (parent.findPreference(key) != null) {
                Log.e(TAG, "Found duplicated key: \"" + key + "\". This can cause unintended behaviour, please use unique keys for every preference.");
            }
        }
        if (preference.getOrder() == Integer.MAX_VALUE) {
            if (this.mOrderingAsAdded) {
                int i = this.mCurrentPreferenceOrder;
                this.mCurrentPreferenceOrder = i + 1;
                preference.setOrder(i);
            }
            if (preference instanceof PreferenceGroup) {
                ((PreferenceGroup) preference).setOrderingAsAdded(this.mOrderingAsAdded);
            }
        }
        int iBinarySearch = Collections.binarySearch(this.mPreferences, preference);
        if (iBinarySearch < 0) {
            iBinarySearch = (iBinarySearch * (-1)) - 1;
        }
        if (!onPrepareAddPreference(preference)) {
            return false;
        }
        synchronized (this) {
            this.mPreferences.add(iBinarySearch, preference);
        }
        PreferenceManager preferenceManager = getPreferenceManager();
        String key2 = preference.getKey();
        if (key2 != null && this.mIdRecycleCache.containsKey(key2)) {
            nextId = this.mIdRecycleCache.get(key2).longValue();
            this.mIdRecycleCache.remove(key2);
        } else {
            nextId = preferenceManager.getNextId();
        }
        preference.onAttachedToHierarchy(preferenceManager, nextId);
        preference.assignParent(this);
        if (this.mAttachedToHierarchy) {
            preference.onAttached();
        }
        notifyHierarchyChanged();
        return true;
    }

    public boolean removePreference(Preference preference) {
        boolean zRemovePreferenceInt = removePreferenceInt(preference);
        notifyHierarchyChanged();
        return zRemovePreferenceInt;
    }

    public boolean removePreferenceRecursively(CharSequence charSequence) {
        Preference preferenceFindPreference = findPreference(charSequence);
        if (preferenceFindPreference == null) {
            return false;
        }
        return preferenceFindPreference.getParent().removePreference(preferenceFindPreference);
    }

    private boolean removePreferenceInt(Preference preference) {
        boolean zRemove;
        synchronized (this) {
            preference.onPrepareForRemoval();
            if (preference.getParent() == this) {
                preference.assignParent(null);
            }
            zRemove = this.mPreferences.remove(preference);
            if (zRemove) {
                String key = preference.getKey();
                if (key != null) {
                    this.mIdRecycleCache.put(key, Long.valueOf(preference.getId()));
                    this.mHandler.removeCallbacks(this.mClearRecycleCacheRunnable);
                    this.mHandler.post(this.mClearRecycleCacheRunnable);
                }
                if (this.mAttachedToHierarchy) {
                    preference.onDetached();
                }
            }
        }
        return zRemove;
    }

    public void removeAll() {
        synchronized (this) {
            List<Preference> list = this.mPreferences;
            for (int size = list.size() - 1; size >= 0; size--) {
                removePreferenceInt(list.get(0));
            }
        }
        notifyHierarchyChanged();
    }

    protected boolean onPrepareAddPreference(Preference preference) {
        preference.onParentChanged(this, shouldDisableDependents());
        return true;
    }

    public <T extends Preference> T findPreference(CharSequence charSequence) {
        T t;
        if (charSequence == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        if (TextUtils.equals(getKey(), charSequence)) {
            return this;
        }
        int preferenceCount = getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            PreferenceGroup preferenceGroup = (T) getPreference(i);
            if (TextUtils.equals(preferenceGroup.getKey(), charSequence)) {
                return preferenceGroup;
            }
            if ((preferenceGroup instanceof PreferenceGroup) && (t = (T) preferenceGroup.findPreference(charSequence)) != null) {
                return t;
            }
        }
        return null;
    }

    public boolean isAttached() {
        return this.mAttachedToHierarchy;
    }

    public void setOnExpandButtonClickListener(OnExpandButtonClickListener onExpandButtonClickListener) {
        this.mOnExpandButtonClickListener = onExpandButtonClickListener;
    }

    public OnExpandButtonClickListener getOnExpandButtonClickListener() {
        return this.mOnExpandButtonClickListener;
    }

    @Override // androidx.preference.Preference
    public void onAttached() {
        super.onAttached();
        this.mAttachedToHierarchy = true;
        int preferenceCount = getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            getPreference(i).onAttached();
        }
    }

    @Override // androidx.preference.Preference
    public void onDetached() {
        super.onDetached();
        this.mAttachedToHierarchy = false;
        int preferenceCount = getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            getPreference(i).onDetached();
        }
    }

    @Override // androidx.preference.Preference
    public void notifyDependencyChange(boolean z) {
        super.notifyDependencyChange(z);
        int preferenceCount = getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            getPreference(i).onParentChanged(this, z);
        }
    }

    void sortPreferences() {
        synchronized (this) {
            Collections.sort(this.mPreferences);
        }
    }

    @Override // androidx.preference.Preference
    protected void dispatchSaveInstanceState(Bundle bundle) {
        super.dispatchSaveInstanceState(bundle);
        int preferenceCount = getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            getPreference(i).dispatchSaveInstanceState(bundle);
        }
    }

    @Override // androidx.preference.Preference
    protected void dispatchRestoreInstanceState(Bundle bundle) {
        super.dispatchRestoreInstanceState(bundle);
        int preferenceCount = getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            getPreference(i).dispatchRestoreInstanceState(bundle);
        }
    }

    @Override // androidx.preference.Preference
    protected Parcelable onSaveInstanceState() {
        return new SavedState(super.onSaveInstanceState(), this.mInitialExpandedChildrenCount);
    }

    @Override // androidx.preference.Preference
    protected void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable == null || !parcelable.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        SavedState savedState = (SavedState) parcelable;
        this.mInitialExpandedChildrenCount = savedState.mInitialExpandedChildrenCount;
        super.onRestoreInstanceState(savedState.getSuperState());
    }

    static class SavedState extends Preference.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: androidx.preference.PreferenceGroup.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        int mInitialExpandedChildrenCount;

        SavedState(Parcel parcel) {
            super(parcel);
            this.mInitialExpandedChildrenCount = parcel.readInt();
        }

        SavedState(Parcelable parcelable, int i) {
            super(parcelable);
            this.mInitialExpandedChildrenCount = i;
        }

        @Override // android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.mInitialExpandedChildrenCount);
        }
    }
}
