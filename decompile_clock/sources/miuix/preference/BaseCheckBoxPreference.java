package miuix.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import androidx.preference.PreferenceViewHolder;
import miuix.core.util.RomUtils;
import miuix.internal.util.AttributeResolver;

/* JADX INFO: loaded from: classes3.dex */
public class BaseCheckBoxPreference extends androidx.preference.CheckBoxPreference implements PreferenceBehavior {
    private boolean mAccessibilityEnable;
    private boolean mCardEnable;
    private boolean mClickable;
    private int mGroupItemType;
    private boolean mTouchAnimationEnable;

    public BaseCheckBoxPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(attributeSet);
    }

    public BaseCheckBoxPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init(attributeSet);
    }

    public BaseCheckBoxPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(attributeSet);
    }

    public BaseCheckBoxPreference(Context context) {
        super(context);
        init(null);
    }

    private void init(AttributeSet attributeSet) {
        int iResolveInt = AttributeResolver.resolveInt(getContext(), R.attr.preferenceCardStyleEnable, 1);
        boolean z = iResolveInt == 2 || (RomUtils.getHyperOsVersion() > 1 && iResolveInt == 1);
        if (attributeSet != null) {
            TypedArray typedArrayObtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R.styleable.BasePreference);
            this.mClickable = typedArrayObtainStyledAttributes.getBoolean(R.styleable.BasePreference_clickable, true);
            this.mTouchAnimationEnable = typedArrayObtainStyledAttributes.getBoolean(R.styleable.BasePreference_touchAnimationEnable, true);
            this.mCardEnable = typedArrayObtainStyledAttributes.getBoolean(R.styleable.BasePreference_cardEnable, z);
            this.mAccessibilityEnable = typedArrayObtainStyledAttributes.getBoolean(R.styleable.BasePreference_accessibilityEnable, true);
            this.mGroupItemType = typedArrayObtainStyledAttributes.getInteger(R.styleable.BasePreference_groupItemType, 0);
            typedArrayObtainStyledAttributes.recycle();
            return;
        }
        this.mClickable = true;
        this.mTouchAnimationEnable = true;
        this.mCardEnable = z;
        this.mAccessibilityEnable = true;
        this.mGroupItemType = 0;
    }

    @Override // miuix.preference.FolmeAnimationController
    public boolean isTouchAnimationEnable() {
        return this.mTouchAnimationEnable;
    }

    @Override // miuix.preference.PreferenceStyle
    public boolean enabledCardStyle() {
        return this.mCardEnable;
    }

    @Override // androidx.preference.CheckBoxPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.itemView.setClickable(this.mClickable);
    }

    @Override // miuix.preference.PreferenceBehavior
    public void setClickable(boolean z) {
        this.mClickable = z;
    }

    @Override // miuix.preference.PreferenceBehavior
    public void setCardStyleEnable(boolean z) {
        this.mCardEnable = z;
    }

    @Override // miuix.preference.PreferenceBehavior
    public void setTouchAnimationEnable(boolean z) {
        this.mTouchAnimationEnable = z;
    }

    @Override // miuix.preference.PreferenceAccessibility
    public void setAccessibilityEnabled(boolean z) {
        this.mAccessibilityEnable = z;
    }

    @Override // miuix.preference.PreferenceAccessibility
    public boolean isAccessibilityEnabled() {
        return this.mAccessibilityEnable;
    }

    @Override // miuix.preference.PreferencedynamicGroupController
    public void setGroupItemType(int i) {
        this.mGroupItemType = i;
    }

    @Override // miuix.preference.PreferencedynamicGroupController
    public int getGroupItemType() {
        return this.mGroupItemType;
    }
}
