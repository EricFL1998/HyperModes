package miuix.theme;

import android.graphics.Typeface;
import android.util.Log;
import android.widget.TextView;

/* JADX INFO: loaded from: classes3.dex */
public class Typography {
    public static Typeface MISANS = null;
    public static Typeface MISANS_BOLD = null;
    public static Typeface MISANS_DEMIBOLD = null;
    public static Typeface MISANS_EXTRALIGHT = null;
    public static Typeface MISANS_HEAVY = null;
    public static Typeface MISANS_LIGHT = null;
    public static Typeface MISANS_MEDIUM = null;
    public static Typeface MISANS_NORMAL = null;
    public static Typeface MISANS_SEMIBOLD = null;
    public static Typeface MISANS_THIN = null;
    private static final String TAG = "Typography";

    public static void applyMiSansThin(TextView textView) {
        try {
            if (MISANS_THIN == null) {
                MISANS_THIN = Typeface.create("sans-serif-thin", 0);
            }
            textView.setTypeface(MISANS_THIN);
        } catch (Exception e) {
            Log.w(TAG, "Failed to set [sans-serif-medium normal] typeface!", e);
            textView.setTypeface(Typeface.DEFAULT);
        }
    }

    public static void applyMiSansExtralight(TextView textView) {
        try {
            if (MISANS_EXTRALIGHT == null) {
                MISANS_EXTRALIGHT = Typeface.create("sans-serif-thin", 1);
            }
            textView.setTypeface(MISANS_EXTRALIGHT);
        } catch (Exception e) {
            Log.w(TAG, "Failed to set [sans-serif-medium normal] typeface!", e);
            textView.setTypeface(Typeface.DEFAULT);
        }
    }

    public static void applyMiSansLight(TextView textView) {
        try {
            if (MISANS_LIGHT == null) {
                MISANS_LIGHT = Typeface.create("sans-serif-light", 0);
            }
            textView.setTypeface(MISANS_LIGHT);
        } catch (Exception e) {
            Log.w(TAG, "Failed to set [sans-serif-medium normal] typeface!", e);
            textView.setTypeface(Typeface.DEFAULT);
        }
    }

    public static void applyMiSansNormal(TextView textView) {
        try {
            if (MISANS_NORMAL == null) {
                MISANS_NORMAL = Typeface.create("sans-serif-light", 1);
            }
            textView.setTypeface(MISANS_NORMAL);
        } catch (Exception e) {
            Log.w(TAG, "Failed to set [sans-serif-medium normal] typeface!", e);
            textView.setTypeface(Typeface.DEFAULT);
        }
    }

    public static void applyMiSansRegular(TextView textView) {
        applyMiSans(textView);
    }

    public static void applyMiSans(TextView textView) {
        try {
            if (MISANS == null) {
                MISANS = Typeface.create("sans-serif", 0);
            }
            textView.setTypeface(MISANS);
        } catch (Exception e) {
            Log.w(TAG, "Failed to set [sans-serif-medium normal] typeface!", e);
            textView.setTypeface(Typeface.DEFAULT);
        }
    }

    public static void applyMiSansMedium(TextView textView) {
        try {
            if (MISANS_MEDIUM == null) {
                MISANS_MEDIUM = Typeface.create("sans-serif-medium", 0);
            }
            textView.setTypeface(MISANS_MEDIUM);
        } catch (Exception e) {
            Log.w(TAG, "Failed to set [sans-serif-medium normal] typeface!", e);
            textView.setTypeface(Typeface.DEFAULT);
        }
    }

    public static void applyMiSansDemibold(TextView textView) {
        try {
            if (MISANS_DEMIBOLD == null) {
                MISANS_DEMIBOLD = Typeface.create("sans-serif-medium", 1);
            }
            textView.setTypeface(MISANS_DEMIBOLD);
        } catch (Exception e) {
            Log.w(TAG, "Failed to set [sans-serif-medium bold] typeface!", e);
            textView.setTypeface(Typeface.DEFAULT_BOLD);
        }
    }

    public static void applyMiSansSemibold(TextView textView) {
        try {
            if (MISANS_SEMIBOLD == null) {
                MISANS_SEMIBOLD = Typeface.create("mipro-semibold", 0);
            }
            textView.setTypeface(MISANS_SEMIBOLD);
        } catch (Exception e) {
            Log.w(TAG, "Failed to set [sans-serif-medium bold] typeface!", e);
            textView.setTypeface(Typeface.DEFAULT_BOLD);
        }
    }

    public static void applyMiSansBold(TextView textView) {
        try {
            if (MISANS_BOLD == null) {
                MISANS_BOLD = Typeface.create("mipro-bold", 0);
            }
            textView.setTypeface(MISANS_BOLD);
        } catch (Exception e) {
            Log.w(TAG, "Failed to set [sans-serif-medium bold] typeface!", e);
            textView.setTypeface(Typeface.DEFAULT_BOLD);
        }
    }

    public static void applyMiSansHeavy(TextView textView) {
        try {
            if (MISANS_HEAVY == null) {
                MISANS_HEAVY = Typeface.create("sans-serif-black", 0);
            }
            textView.setTypeface(MISANS_HEAVY);
        } catch (Exception e) {
            Log.w(TAG, "Failed to set [sans-serif-medium bold] typeface!", e);
            textView.setTypeface(Typeface.DEFAULT_BOLD);
        }
    }
}
