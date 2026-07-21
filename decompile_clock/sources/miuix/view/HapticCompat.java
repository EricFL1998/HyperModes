package miuix.view;

import android.os.Looper;
import android.util.Log;
import android.view.View;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import miuix.HapticLog;
import miuix.core.util.SystemProperties;

/* JADX INFO: loaded from: classes3.dex */
public class HapticCompat {
    static final String TAG = "HapticCompat";
    public static String CURRENT_HAPTIC_VERSION = SystemProperties.get("sys.haptic.version", "1.0");
    private static List<HapticFeedbackProvider> sProviders = new ArrayList();
    private static final Executor sSingleThread = Executors.newSingleThreadExecutor();

    @Retention(RetentionPolicy.SOURCE)
    public @interface HapticVersion {
        public static final String HAPTIC_VERSION_1 = "1.0";
        public static final String HAPTIC_VERSION_2 = "2.0";
    }

    static {
        loadProviders("miuix.view.LinearVibrator", "miuix.view.ExtendedVibrator");
    }

    public static boolean performHapticFeedback(View view, int i) {
        if (view == null) {
            Log.e(TAG, "performHapticFeedback: view is null!");
            return false;
        }
        if (i < 268435456) {
            Log.i(TAG, String.format("perform haptic: 0x%08x", Integer.valueOf(i)));
            HapticLog.printTrace("performHapticFeedback view: " + view + ", feedbackConstant: " + i);
            return view.performHapticFeedback(i);
        }
        if (i > HapticFeedbackConstants.MIUI_HAPTIC_END) {
            Log.w(TAG, String.format("illegal feedback constant, should be in range [0x%08x..0x%08x]", 268435456, Integer.valueOf(HapticFeedbackConstants.MIUI_HAPTIC_END)));
            return false;
        }
        Iterator<HapticFeedbackProvider> it = sProviders.iterator();
        while (it.hasNext()) {
            if (it.next().performHapticFeedback(view, i)) {
                return true;
            }
        }
        return false;
    }

    public static boolean performHapticFeedback(View view, int i, int i2) {
        if (doesSupportHaptic(HapticVersion.HAPTIC_VERSION_2)) {
            if (checkHapticVersion2FeedBackConstant(i)) {
                return performHapticFeedback(view, i);
            }
            return false;
        }
        if (doesSupportHaptic("1.0")) {
            if (checkHapticVersion1FeedBackConstant(i2)) {
                return performHapticFeedback(view, i2);
            }
            return false;
        }
        Log.e(TAG, "Unexpected haptic version: " + CURRENT_HAPTIC_VERSION);
        return false;
    }

    private static boolean checkHapticVersion2FeedBackConstant(int i) {
        if (i >= HapticFeedbackConstants.MIUI_HAPTIC_VERSION_2_START && i <= HapticFeedbackConstants.MIUI_HAPTIC_VERSION_2_END) {
            return true;
        }
        Log.e(TAG, String.format("Illegal haptic version 2 feedback constant, should be in range [0x%08x..0x%08x]", Integer.valueOf(HapticFeedbackConstants.MIUI_HAPTIC_VERSION_2_START), Integer.valueOf(HapticFeedbackConstants.MIUI_HAPTIC_VERSION_2_END)));
        return false;
    }

    private static boolean checkHapticVersion1FeedBackConstant(int i) {
        if (i >= HapticFeedbackConstants.MIUI_HAPTIC_VERSION_1_START && i <= HapticFeedbackConstants.MIUI_HAPTIC_VERSION_1_END) {
            return true;
        }
        Log.e(TAG, String.format("Illegal haptic version 1 feedback constant, should be in range [0x%08x..0x%08x]", Integer.valueOf(HapticFeedbackConstants.MIUI_HAPTIC_VERSION_1_START), Integer.valueOf(HapticFeedbackConstants.MIUI_HAPTIC_VERSION_1_END)));
        return false;
    }

    private static class WeakReferenceHandler implements Runnable {
        private final int mFeedbackConstant;
        private final WeakReference<View> mViewReference;

        public WeakReferenceHandler(View view, int i) {
            this.mViewReference = new WeakReference<>(view);
            this.mFeedbackConstant = i;
        }

        @Override // java.lang.Runnable
        public void run() {
            View view = this.mViewReference.get();
            if (view == null || !view.isAttachedToWindow()) {
                return;
            }
            try {
                HapticCompat.performHapticFeedback(view, this.mFeedbackConstant);
            } catch (Exception unused) {
            }
        }
    }

    public static void performHapticFeedbackAsync(View view, int i) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            performHapticFeedback(view, i);
        } else {
            sSingleThread.execute(new WeakReferenceHandler(view, i));
        }
    }

    public static void performHapticFeedbackAsync(View view, int i, int i2) {
        if (doesSupportHaptic(HapticVersion.HAPTIC_VERSION_2)) {
            if (checkHapticVersion2FeedBackConstant(i)) {
                performHapticFeedbackAsync(view, i);
            }
        } else {
            if (doesSupportHaptic("1.0")) {
                if (checkHapticVersion1FeedBackConstant(i2)) {
                    performHapticFeedbackAsync(view, i2);
                    return;
                }
                return;
            }
            Log.e(TAG, "Unexpected haptic version: " + CURRENT_HAPTIC_VERSION);
        }
    }

    public static boolean supportLinearMotor(int i) {
        if (i < 268435456) {
            Log.i(TAG, String.format("perform haptic: 0x%08x", Integer.valueOf(i)));
            return false;
        }
        if (i > HapticFeedbackConstants.MIUI_HAPTIC_END) {
            Log.w(TAG, String.format("illegal feedback constant, should be in range [0x%08x..0x%08x]", 268435456, Integer.valueOf(HapticFeedbackConstants.MIUI_HAPTIC_END)));
            return false;
        }
        for (HapticFeedbackProvider hapticFeedbackProvider : sProviders) {
            if ((hapticFeedbackProvider instanceof LinearVibrator) && ((LinearVibrator) hapticFeedbackProvider).supportLinearMotor(i)) {
                return true;
            }
        }
        return false;
    }

    public static int obtainFeedBack(int i) {
        for (HapticFeedbackProvider hapticFeedbackProvider : sProviders) {
            if (hapticFeedbackProvider instanceof LinearVibrator) {
                return ((LinearVibrator) hapticFeedbackProvider).obtainFeedBack(i);
            }
        }
        return -1;
    }

    static void registerProvider(HapticFeedbackProvider hapticFeedbackProvider) {
        sProviders.add(hapticFeedbackProvider);
    }

    private static void loadProviders(String... strArr) {
        for (String str : strArr) {
            Log.i(TAG, "loading provider: " + str);
            try {
                Class.forName(str, true, HapticCompat.class.getClassLoader());
            } catch (ClassNotFoundException e) {
                Log.w(TAG, String.format("load provider %s failed.", str), e);
            }
        }
    }

    public static boolean doesSupportHaptic(String str) {
        return CURRENT_HAPTIC_VERSION.equals(str);
    }
}
