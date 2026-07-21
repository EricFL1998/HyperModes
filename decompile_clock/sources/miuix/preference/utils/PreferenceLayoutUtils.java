package miuix.preference.utils;

import android.content.Context;
import androidx.preference.Preference;
import miuix.preference.PreferencedynamicGroupController;
import miuix.preference.R;

/* JADX INFO: loaded from: classes3.dex */
public class PreferenceLayoutUtils {
    public static int getExtraPaddingByLevel(Context context, int i) {
        if (i == 1) {
            return context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_two_state_extra_padding_horizontal_small);
        }
        if (i == 2) {
            return context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_two_state_extra_padding_horizontal_large);
        }
        if (i != 3) {
            return 0;
        }
        return context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_two_state_extra_padding_horizontal_huge);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static boolean isDynamicGroupItem(Preference preference) {
        int groupItemType;
        return (preference instanceof PreferencedynamicGroupController) && (groupItemType = ((PreferencedynamicGroupController) preference).getGroupItemType()) > 0 && groupItemType < 5;
    }
}
