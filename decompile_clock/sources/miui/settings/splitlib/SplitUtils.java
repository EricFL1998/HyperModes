package miui.settings.splitlib;

import android.content.Context;
import android.content.Intent;

/* JADX INFO: loaded from: classes2.dex */
public class SplitUtils {
    public static final String SETTINGS_MAIN_INTENT = "android.settings.SETTINGS";
    public static final String SPLIT_PAGE_INTENT_KEY = "split_page_intent";

    public static void startSettingsSplitActivity(Context context, Intent intent, String str) {
        startSettingsSplitActivity(context, intent, str, false);
    }

    public static void startSettingsSplitActivity(Context context, Intent intent, String str, boolean z) {
        context.startActivity(getSettingsSplitActivityIntent(context, intent, str, z));
    }

    public static Intent getSettingsSplitActivityIntent(Context context, Intent intent, String str) {
        return getSettingsSplitActivityIntent(context, intent, str, false);
    }

    public static Intent getSettingsSplitActivityIntent(Context context, Intent intent, String str, boolean z) {
        Intent intent2 = new Intent(SETTINGS_MAIN_INTENT);
        if (z) {
            intent2.addFlags(268435456);
        }
        intent2.putExtra(SPLIT_PAGE_INTENT_KEY, new SplitPageIntent(intent, str));
        return intent2;
    }

    public static Intent getSplitActivityIntent(Intent intent) {
        SplitPageIntent splitPageIntent;
        if (intent == null || (splitPageIntent = (SplitPageIntent) intent.getParcelableExtra(SPLIT_PAGE_INTENT_KEY)) == null) {
            return null;
        }
        return splitPageIntent.getIntent();
    }
}
