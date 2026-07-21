package com.android.deskclock.settings.pref;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.deskclock.R;
import com.android.deskclock.util.permission.UserNoticeUtil;

/* JADX INFO: loaded from: classes.dex */
public class ClockUpdatePreference extends Preference {
    private ImageView mHolidayUpdateCircle;

    public ClockUpdatePreference(Context context) {
        this(context, null);
    }

    public ClockUpdatePreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ClockUpdatePreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    private void init() {
        setLayoutResource(R.layout.preference_value_holiady);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.mHolidayUpdateCircle = (ImageView) preferenceViewHolder.itemView.findViewById(R.id.holiday_update);
        if (UserNoticeUtil.isNetPermissionAgreed()) {
            this.mHolidayUpdateCircle.setVisibility(8);
        }
    }

    public void setCircleViewVisibility(int i) {
        this.mHolidayUpdateCircle.setVisibility(i);
    }
}
