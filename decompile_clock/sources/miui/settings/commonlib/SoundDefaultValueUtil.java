package miui.settings.commonlib;

import android.content.res.Resources;
import android.util.Log;

/* JADX INFO: loaded from: classes2.dex */
public class SoundDefaultValueUtil {
    private static final String TAG = "SoundDefaultValueUtil";

    public static boolean getDeleteSoundEffectDefaultValue() {
        try {
            return Resources.getSystem().getBoolean(Resources.getSystem().getIdentifier("default_file_delete_sound_is_on", "bool", "android.miui"));
        } catch (Exception e) {
            Log.w(TAG, "getDeleteSoundEffectDefaultValue error =  " + e.toString());
            return true;
        }
    }

    public static boolean getAppDeleteSoundEffectDefaultValue() {
        try {
            return Resources.getSystem().getBoolean(Resources.getSystem().getIdentifier("default_app_delete_sound_is_on", "bool", "android.miui"));
        } catch (Exception e) {
            Log.w(TAG, "getAppDeleteSoundEffectDefaultValue error =  " + e.toString());
            return true;
        }
    }
}
