package miuix.appcompat.app.floatingactivity.helper;

import miuix.appcompat.app.AppCompatActivity;
import miuix.core.util.IntentUtils;
import miuix.os.Build;

/* JADX INFO: loaded from: classes2.dex */
public class FloatingHelperFactory {
    public static final int TYPE_FOLD = 2;
    public static final int TYPE_PAD = 1;
    public static final int TYPE_PHONE = 0;

    public static BaseFloatingActivityHelper get(AppCompatActivity appCompatActivity) {
        int floatingHelperType = getFloatingHelperType(appCompatActivity);
        if (floatingHelperType == 1) {
            return new PadFloatingActivityHelper(appCompatActivity);
        }
        if (floatingHelperType == 2) {
            return new FoldFloatingActivityHelper(appCompatActivity);
        }
        return new PhoneFloatingActivityHelper(appCompatActivity);
    }

    public static int getFloatingHelperType(AppCompatActivity appCompatActivity) {
        boolean zIsIntentFromSettingsSplit = IntentUtils.isIntentFromSettingsSplit(appCompatActivity.getIntent());
        if (zIsIntentFromSettingsSplit || !(Build.IS_FOLD_INSIDE || Build.IS_FOLD_OUTSIDE)) {
            return (zIsIntentFromSettingsSplit || !Build.IS_TABLET) ? 0 : 1;
        }
        return 2;
    }
}
