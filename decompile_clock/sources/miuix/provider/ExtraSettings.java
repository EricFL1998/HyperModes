package miuix.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.Settings;

/* JADX INFO: loaded from: classes3.dex */
public class ExtraSettings {
    protected ExtraSettings() throws InstantiationException {
        throw new InstantiationException("Cannot instantiate utility class");
    }

    public static class System {
        public static final String DEFAULT_ALARM_ALERT = "default_alarm_alert";

        protected System() throws InstantiationException {
            throw new InstantiationException("Cannot instantiate utility class");
        }

        public static int getInt(ContentResolver contentResolver, String str) throws Settings.SettingNotFoundException {
            return Settings.System.getInt(contentResolver, str);
        }

        public static int getInt(ContentResolver contentResolver, String str, int i) {
            return Settings.System.getInt(contentResolver, str, i);
        }

        public static long getLong(ContentResolver contentResolver, String str) throws Settings.SettingNotFoundException {
            return Settings.System.getLong(contentResolver, str);
        }

        public static long getLong(ContentResolver contentResolver, String str, long j) {
            return Settings.System.getLong(contentResolver, str, j);
        }

        public static float getFloat(ContentResolver contentResolver, String str) throws Settings.SettingNotFoundException {
            return Settings.System.getFloat(contentResolver, str);
        }

        public static float getFloat(ContentResolver contentResolver, String str, float f) {
            return Settings.System.getFloat(contentResolver, str, f);
        }

        public static boolean getBoolean(ContentResolver contentResolver, String str) throws Settings.SettingNotFoundException {
            return getInt(contentResolver, str) != 0;
        }

        public static boolean getBoolean(ContentResolver contentResolver, String str, boolean z) {
            return getInt(contentResolver, str, z ? 1 : 0) != 0;
        }

        public static String getString(ContentResolver contentResolver, String str) {
            return Settings.System.getString(contentResolver, str);
        }

        public static String getString(ContentResolver contentResolver, String str, String str2) {
            String string = Settings.System.getString(contentResolver, str);
            return string == null ? str2 : string;
        }

        public static Uri getUriFor(String str) {
            return Settings.System.getUriFor(str);
        }

        public static boolean putInt(ContentResolver contentResolver, String str, int i) {
            return Settings.System.putInt(contentResolver, str, i);
        }

        public static boolean putLong(ContentResolver contentResolver, String str, long j) {
            return Settings.System.putLong(contentResolver, str, j);
        }

        public static boolean putFloat(ContentResolver contentResolver, String str, float f) {
            return Settings.System.putFloat(contentResolver, str, f);
        }

        public static boolean putBoolean(ContentResolver contentResolver, String str, boolean z) {
            return putInt(contentResolver, str, z ? 1 : 0);
        }

        public static boolean putString(ContentResolver contentResolver, String str, String str2) {
            return Settings.System.putString(contentResolver, str, str2);
        }
    }

    public static class Secure {
        public static final String PRIVACY_MODE_ENABLED = "privacy_mode_enabled";
        public static final String UPLOAD_LOG = "upload_log_pref";

        protected Secure() throws InstantiationException {
            throw new InstantiationException("Cannot instantiate utility class");
        }

        public static int getInt(ContentResolver contentResolver, String str) throws Settings.SettingNotFoundException {
            return Settings.Secure.getInt(contentResolver, str);
        }

        public static int getInt(ContentResolver contentResolver, String str, int i) {
            return Settings.Secure.getInt(contentResolver, str, i);
        }

        public static long getLong(ContentResolver contentResolver, String str) throws Settings.SettingNotFoundException {
            return Settings.Secure.getLong(contentResolver, str);
        }

        public static long getLong(ContentResolver contentResolver, String str, long j) {
            return Settings.Secure.getLong(contentResolver, str, j);
        }

        public static float getFloat(ContentResolver contentResolver, String str) throws Settings.SettingNotFoundException {
            return Settings.Secure.getFloat(contentResolver, str);
        }

        public static float getFloat(ContentResolver contentResolver, String str, float f) {
            return Settings.Secure.getFloat(contentResolver, str, f);
        }

        public static boolean getBoolean(ContentResolver contentResolver, String str) throws Settings.SettingNotFoundException {
            return getInt(contentResolver, str) != 0;
        }

        public static boolean getBoolean(ContentResolver contentResolver, String str, boolean z) {
            return getInt(contentResolver, str, z ? 1 : 0) != 0;
        }

        public static String getString(ContentResolver contentResolver, String str) {
            return Settings.Secure.getString(contentResolver, str);
        }

        public static String getString(ContentResolver contentResolver, String str, String str2) {
            String string = Settings.Secure.getString(contentResolver, str);
            return string == null ? str2 : string;
        }

        public static Uri getUriFor(String str) {
            return Settings.Secure.getUriFor(str);
        }

        public static boolean putInt(ContentResolver contentResolver, String str, int i) {
            return Settings.Secure.putInt(contentResolver, str, i);
        }

        public static boolean putLong(ContentResolver contentResolver, String str, long j) {
            return Settings.Secure.putLong(contentResolver, str, j);
        }

        public static boolean putFloat(ContentResolver contentResolver, String str, float f) {
            return Settings.Secure.putFloat(contentResolver, str, f);
        }

        public static boolean putBoolean(ContentResolver contentResolver, String str, boolean z) {
            return putInt(contentResolver, str, z ? 1 : 0);
        }

        public static boolean putString(ContentResolver contentResolver, String str, String str2) {
            return Settings.Secure.putString(contentResolver, str, str2);
        }
    }
}
