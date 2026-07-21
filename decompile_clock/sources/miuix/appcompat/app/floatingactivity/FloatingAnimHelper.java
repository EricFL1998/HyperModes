package miuix.appcompat.app.floatingactivity;

import android.content.Context;
import android.util.Log;
import miuix.appcompat.R;
import miuix.appcompat.app.AppCompatActivity;
import miuix.autodensity.IDensity;
import miuix.core.util.WindowUtils;

/* JADX INFO: loaded from: classes2.dex */
public class FloatingAnimHelper {
    private static final String TAG = "FloatingAnimHelper";
    private static boolean sTransWithClipAnimSupported = false;

    public static void clearFloatingWindowAnim(AppCompatActivity appCompatActivity) {
    }

    static {
        try {
            Class.forName("android.view.animation.TranslateWithClipAnimation");
            sTransWithClipAnimSupported = true;
        } catch (ClassNotFoundException e) {
            Log.w(TAG, "Failed to get isSupportTransWithClipAnim attributes", e);
        }
    }

    public static void singleAppFloatingActivityEnter(AppCompatActivity appCompatActivity) {
        if (sTransWithClipAnimSupported) {
            preformFloatingExitAnimWithClip(appCompatActivity, appCompatActivity.isInFloatingWindowMode());
        } else {
            appCompatActivity.executeOpenEnterAnimation();
        }
    }

    public static void preformFloatingExitAnimWithClip(AppCompatActivity appCompatActivity, boolean z) {
        if (sTransWithClipAnimSupported) {
            if (z) {
                if (getAutoDensitySupportStatus(appCompatActivity)) {
                    if (isPortrait(appCompatActivity)) {
                        appCompatActivity.overridePendingTransition(R.anim.miuix_appcompat_floating_window_enter_anim_auto_dpi, R.anim.miuix_appcompat_floating_window_exit_anim_auto_dpi);
                        return;
                    } else {
                        appCompatActivity.overridePendingTransition(R.anim.miuix_appcompat_floating_window_enter_anim_auto_dpi_land, R.anim.miuix_appcompat_floating_window_exit_anim_auto_dpi_land);
                        return;
                    }
                }
                if (isPortrait(appCompatActivity)) {
                    appCompatActivity.overridePendingTransition(R.anim.miuix_appcompat_floating_window_enter_anim, R.anim.miuix_appcompat_floating_window_exit_anim);
                    return;
                } else {
                    appCompatActivity.overridePendingTransition(R.anim.miuix_appcompat_floating_window_enter_anim_land, R.anim.miuix_appcompat_floating_window_exit_anim_land);
                    return;
                }
            }
            appCompatActivity.overridePendingTransition(R.anim.miuix_appcompat_floating_window_anim_in_full_screen, R.anim.miuix_appcompat_floating_window_anim_out_full_screen);
        }
    }

    public static void preformFloatingEnterAnimWithClip(AppCompatActivity appCompatActivity, boolean z) {
        if (sTransWithClipAnimSupported) {
            if (z) {
                if (getAutoDensitySupportStatus(appCompatActivity)) {
                    if (isPortrait(appCompatActivity)) {
                        appCompatActivity.overridePendingTransition(R.anim.miuix_appcompat_floating_window_enter_anim_auto_dpi, R.anim.miuix_appcompat_floating_window_exit_anim_auto_dpi);
                        return;
                    } else {
                        appCompatActivity.overridePendingTransition(R.anim.miuix_appcompat_floating_window_enter_anim_auto_dpi_land, R.anim.miuix_appcompat_floating_window_exit_anim_auto_dpi_land);
                        return;
                    }
                }
                if (isPortrait(appCompatActivity)) {
                    appCompatActivity.overridePendingTransition(R.anim.miuix_appcompat_floating_window_enter_anim, R.anim.miuix_appcompat_floating_window_exit_anim);
                    return;
                } else {
                    appCompatActivity.overridePendingTransition(R.anim.miuix_appcompat_floating_window_enter_anim_land, R.anim.miuix_appcompat_floating_window_exit_anim_land);
                    return;
                }
            }
            appCompatActivity.overridePendingTransition(R.anim.miuix_appcompat_floating_window_anim_in_full_screen, R.anim.miuix_appcompat_floating_window_anim_out_full_screen);
        }
    }

    public static void singleAppFloatingActivityExit(AppCompatActivity appCompatActivity) {
        if (sTransWithClipAnimSupported) {
            if (appCompatActivity.isInFloatingWindowMode()) {
                if (getAutoDensitySupportStatus(appCompatActivity)) {
                    if (isPortrait(appCompatActivity)) {
                        appCompatActivity.overridePendingTransition(R.anim.miuix_appcompat_floating_window_enter_anim_auto_dpi, R.anim.miuix_appcompat_floating_window_exit_anim_auto_dpi);
                        return;
                    } else {
                        appCompatActivity.overridePendingTransition(R.anim.miuix_appcompat_floating_window_enter_anim_auto_dpi_land, R.anim.miuix_appcompat_floating_window_exit_anim_auto_dpi_land);
                        return;
                    }
                }
                if (isPortrait(appCompatActivity)) {
                    appCompatActivity.overridePendingTransition(R.anim.miuix_appcompat_floating_window_enter_anim, R.anim.miuix_appcompat_floating_window_exit_anim);
                    return;
                } else {
                    appCompatActivity.overridePendingTransition(R.anim.miuix_appcompat_floating_window_enter_anim_land, R.anim.miuix_appcompat_floating_window_exit_anim_land);
                    return;
                }
            }
            appCompatActivity.overridePendingTransition(R.anim.miuix_appcompat_floating_window_anim_in_full_screen, R.anim.miuix_appcompat_floating_window_anim_out_full_screen);
        }
    }

    public static void execFloatingWindowExitAnimRomNormal(AppCompatActivity appCompatActivity) {
        appCompatActivity.overridePendingTransition(R.anim.miuix_appcompat_floating_window_exit_anim_normal_rom_enter, R.anim.miuix_appcompat_floating_window_exit_anim_normal_rom_exit);
    }

    public static void execFloatingWindowEnterAnimRomNormal(AppCompatActivity appCompatActivity) {
        appCompatActivity.overridePendingTransition(R.anim.miuix_appcompat_floating_window_enter_anim_normal_rom_enter, R.anim.miuix_appcompat_floating_window_enter_anim_normal_rom_exit);
    }

    public static void markedPageIndex(AppCompatActivity appCompatActivity, int i) {
        appCompatActivity.getWindow().getDecorView().setTag(R.id.miuix_appcompat_floating_window_index, Integer.valueOf(i));
    }

    public static int obtainPageIndex(AppCompatActivity appCompatActivity) {
        Object tag = appCompatActivity.getWindow().getDecorView().getTag(R.id.miuix_appcompat_floating_window_index);
        if (tag instanceof Integer) {
            return ((Integer) tag).intValue();
        }
        return -1;
    }

    private static boolean isPortrait(Context context) {
        return WindowUtils.isPortrait(context);
    }

    public static boolean isSupportTransWithClipAnim() {
        return sTransWithClipAnimSupported;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static boolean getAutoDensitySupportStatus(AppCompatActivity appCompatActivity) {
        if (appCompatActivity instanceof IDensity) {
            return ((IDensity) appCompatActivity).shouldAdaptAutoDensity();
        }
        if (appCompatActivity.getApplication() instanceof IDensity) {
            return ((IDensity) appCompatActivity.getApplication()).shouldAdaptAutoDensity();
        }
        return false;
    }
}
