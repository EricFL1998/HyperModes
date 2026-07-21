package miuix.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import androidx.preference.PreferenceViewHolder;
import miuix.core.util.RomUtils;

/* JADX INFO: loaded from: classes3.dex */
public class DividerPreference extends BasePreference {
    public DividerPreference(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, R.style.Miuix_Preference_DividerPreference);
    }

    public DividerPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.dividerPreferenceStyle);
    }

    public DividerPreference(Context context) {
        this(context, null);
    }

    public DividerPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init(attributeSet);
    }

    private void init(AttributeSet attributeSet) {
        TypedArray typedArrayObtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R.styleable.BasePreference);
        setClickable(typedArrayObtainStyledAttributes.getBoolean(R.styleable.BasePreference_clickable, false));
        setCardStyleEnable(typedArrayObtainStyledAttributes.getBoolean(R.styleable.BasePreference_cardEnable, false));
        setTouchAnimationEnable(typedArrayObtainStyledAttributes.getBoolean(R.styleable.BasePreference_touchAnimationEnable, false));
        typedArrayObtainStyledAttributes.recycle();
    }

    @Override // miuix.preference.BasePreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View view = preferenceViewHolder.itemView;
        TypedArray typedArrayObtainStyledAttributes = getContext().obtainStyledAttributes(new int[]{R.attr.preferenceCardStyleEnable});
        boolean z = false;
        int i = typedArrayObtainStyledAttributes.getInt(0, 1);
        if (i == 2 || (RomUtils.getHyperOsVersion() > 1 && i == 1)) {
            z = true;
        }
        typedArrayObtainStyledAttributes.recycle();
        if (z) {
            return;
        }
        view.setVisibility(8);
    }
}
