package miuix.util;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.os.VibrationAttributes;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import miui.util.HapticFeedbackUtil;
import miuix.HapticLog;
import miuix.core.util.SystemProperties;
import miuix.view.HapticCompat;
import miuix.view.PlatformConstants;

/* JADX INFO: loaded from: classes3.dex */
public class HapticFeedbackCompat {
    private static final String PHYSICAL_EMULATION_REASON = "USAGE_PHYSICAL_EMULATION";
    private static final int RTP_MIN_VALUE = 0;
    private static final int RTP_V1_MAX_VALUE = 160;
    private static final String TAG = "HapticFeedbackCompat";
    private static boolean mAvailable;
    private static boolean mCanCheckExtHaptic;
    private static boolean mCanStop;
    private static boolean mExtHapticAlways;
    private static boolean mIsSupportExtHapticWithReason;
    private static boolean mIsSupportHapticWithReason;
    private static boolean mPerformExtHapticFeedbackThreeParamsMethodExist;
    private static boolean mPerformExtHapticFeedbackTwoParamsMethodExist;
    private static boolean mPerformHapticFeedbackFourParamsMethod1Exist;
    private static boolean mPerformHapticFeedbackFourParamsMethod2Exist;
    private static final Executor sSingleThread = Executors.newSingleThreadExecutor();
    private HapticFeedbackUtil mHapticFeedbackUtil;

    static {
        if (PlatformConstants.VERSION >= 1) {
            try {
                mAvailable = HapticFeedbackUtil.isSupportLinearMotorVibrate();
            } catch (Throwable th) {
                android.util.Log.w(TAG, "MIUI Haptic Implementation is not available", th);
                mAvailable = false;
            }
            if (mAvailable) {
                try {
                    HapticFeedbackUtil.class.getMethod("performHapticFeedback", Integer.TYPE, Double.TYPE, String.class);
                    mIsSupportHapticWithReason = true;
                } catch (Throwable th2) {
                    android.util.Log.w(TAG, "Not support haptic with reason", th2);
                    mIsSupportHapticWithReason = false;
                }
                try {
                    HapticFeedbackUtil.class.getMethod("isSupportExtHapticFeedback", Integer.TYPE);
                    mCanCheckExtHaptic = true;
                } catch (Throwable unused) {
                    mCanCheckExtHaptic = false;
                }
                try {
                    HapticFeedbackUtil.class.getMethod("performExtHapticFeedback", Integer.TYPE, Boolean.TYPE);
                    mExtHapticAlways = true;
                } catch (Throwable unused2) {
                    mExtHapticAlways = false;
                }
                try {
                    HapticFeedbackUtil.class.getMethod("stop", new Class[0]);
                    mCanStop = true;
                } catch (Throwable unused3) {
                    mCanStop = false;
                }
                try {
                    HapticFeedbackUtil.class.getMethod("performExtHapticFeedback", Integer.TYPE, Double.TYPE, String.class);
                    mIsSupportExtHapticWithReason = true;
                } catch (Throwable th3) {
                    android.util.Log.w(TAG, "Not support ext haptic with reason", th3);
                    mIsSupportExtHapticWithReason = false;
                }
            }
        }
        if (PlatformConstants.romHapticVersion < 1.2d || Build.VERSION.SDK_INT < 30) {
            return;
        }
        try {
            HapticFeedbackUtil.class.getMethod("performExtHapticFeedback", VibrationAttributes.class, Integer.TYPE);
            mPerformExtHapticFeedbackTwoParamsMethodExist = true;
        } catch (Exception unused4) {
        }
        try {
            HapticFeedbackUtil.class.getMethod("performExtHapticFeedback", VibrationAttributes.class, Integer.TYPE, Boolean.TYPE);
            mPerformExtHapticFeedbackThreeParamsMethodExist = true;
        } catch (Exception unused5) {
        }
        try {
            HapticFeedbackUtil.class.getMethod("performHapticFeedback", VibrationAttributes.class, Integer.TYPE, Boolean.TYPE, Integer.TYPE);
            mPerformHapticFeedbackFourParamsMethod1Exist = true;
        } catch (Exception unused6) {
        }
        try {
            HapticFeedbackUtil.class.getMethod("performHapticFeedback", VibrationAttributes.class, Integer.TYPE, Double.TYPE, String.class);
            mPerformHapticFeedbackFourParamsMethod2Exist = true;
        } catch (Exception unused7) {
        }
    }

    @Deprecated
    public HapticFeedbackCompat(Context context, boolean z) {
        if (PlatformConstants.VERSION < 1) {
            android.util.Log.w(TAG, "MiuiHapticFeedbackConstants not found or not compatible for LinearVibrator.");
        } else if (!mAvailable) {
            android.util.Log.w(TAG, "linear motor is not supported in this platform.");
        } else {
            this.mHapticFeedbackUtil = new HapticFeedbackUtil(context, z);
        }
    }

    public HapticFeedbackCompat(Context context) {
        this(context, true);
    }

    public boolean supportLinearMotor() {
        return mAvailable;
    }

    public boolean supportLinearMotorWithReason() {
        return mIsSupportHapticWithReason;
    }

    /* JADX INFO: renamed from: performExtHapticFeedback, reason: merged with bridge method [inline-methods] */
    public boolean m1943x85658b2f(int i) {
        if (this.mHapticFeedbackUtil == null) {
            return false;
        }
        HapticLog.printTrace("performExtHapticFeedback: " + i);
        return this.mHapticFeedbackUtil.performExtHapticFeedback(i);
    }

    public void performExtHapticFeedbackAsync(final int i) {
        if (this.mHapticFeedbackUtil == null) {
            return;
        }
        if (Looper.getMainLooper() == Looper.myLooper()) {
            sSingleThread.execute(new Runnable() { // from class: miuix.util.HapticFeedbackCompat$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1943x85658b2f(i);
                }
            });
        } else {
            m1943x85658b2f(i);
        }
    }

    public boolean performExtHapticFeedback(int i, int i2) {
        if (this.mHapticFeedbackUtil == null) {
            return false;
        }
        HapticLog.printTrace("performExtHapticFeedback: audioAttributesUsage: " + i + ", effectId: " + i2);
        if (PlatformConstants.romHapticVersion >= 1.1d) {
            return this.mHapticFeedbackUtil.performExtHapticFeedback(i, i2);
        }
        return this.mHapticFeedbackUtil.performExtHapticFeedback(i2);
    }

    public boolean performExtHapticFeedback(VibrationAttributes vibrationAttributes, int i) {
        if (this.mHapticFeedbackUtil == null) {
            return false;
        }
        try {
            HapticLog.printTrace("performExtHapticFeedback: attributes: " + vibrationAttributes + ", effectId: " + i);
            if (PlatformConstants.romHapticVersion >= 1.2d && mPerformExtHapticFeedbackTwoParamsMethodExist) {
                return this.mHapticFeedbackUtil.performExtHapticFeedback(vibrationAttributes, i);
            }
            return this.mHapticFeedbackUtil.performExtHapticFeedback(i);
        } catch (Exception e) {
            android.util.Log.e(TAG, "Failed to perform ext haptic!", e);
            return false;
        }
    }

    public boolean performExtHapticFeedback(int i, boolean z) {
        if (this.mHapticFeedbackUtil == null) {
            return false;
        }
        HapticLog.printTrace("performExtHapticFeedback: effectId: " + i + ", always: " + z);
        if (mExtHapticAlways && z) {
            return this.mHapticFeedbackUtil.performExtHapticFeedback(i, true);
        }
        return this.mHapticFeedbackUtil.performExtHapticFeedback(i);
    }

    public boolean performExtHapticFeedback(int i, int i2, boolean z) {
        if (PlatformConstants.romHapticVersion >= 1.1d) {
            if (this.mHapticFeedbackUtil == null) {
                return false;
            }
            HapticLog.printTrace("performExtHapticFeedback: audioAttributesUsage: " + i + ", effectId: " + i2 + ", always: " + z);
            return this.mHapticFeedbackUtil.performExtHapticFeedback(i, i2, z);
        }
        return performExtHapticFeedback(i2, z);
    }

    public boolean performExtHapticFeedback(VibrationAttributes vibrationAttributes, int i, boolean z) {
        try {
            if (PlatformConstants.romHapticVersion >= 1.2d && mPerformExtHapticFeedbackThreeParamsMethodExist) {
                if (this.mHapticFeedbackUtil == null) {
                    return false;
                }
                HapticLog.printTrace("performExtHapticFeedback: attributes: " + vibrationAttributes + ", effectId: " + i + ", always: " + z);
                return this.mHapticFeedbackUtil.performExtHapticFeedback(vibrationAttributes, i, z);
            }
            return performExtHapticFeedback(i, z);
        } catch (Exception e) {
            android.util.Log.e(TAG, "Failed to perform ext haptic!", e);
            return false;
        }
    }

    public boolean performEmulationExtHaptic(int i, double d) {
        return performExtHapticFeedback(i, d, PHYSICAL_EMULATION_REASON);
    }

    public boolean performExtHapticFeedback(int i, double d, String str) {
        if (this.mHapticFeedbackUtil == null || !mIsSupportExtHapticWithReason) {
            return false;
        }
        HapticLog.printTrace("performExtHapticFeedback: effectId: " + i + ", suitIntensity: " + d + ", reason: " + str);
        return this.mHapticFeedbackUtil.performExtHapticFeedback(i, d, str);
    }

    public boolean isSupportExtHapticFeedback(int i) {
        HapticFeedbackUtil hapticFeedbackUtil = this.mHapticFeedbackUtil;
        if (hapticFeedbackUtil == null) {
            return false;
        }
        if (mCanCheckExtHaptic) {
            return hapticFeedbackUtil.isSupportExtHapticFeedback(i);
        }
        return i >= 0 && i <= 160;
    }

    public boolean performExtHapticFeedback(Uri uri, boolean z) {
        if (this.mHapticFeedbackUtil == null) {
            return false;
        }
        HapticLog.printTrace("performExtHapticFeedback: uri: " + uri + ", always: " + z);
        if (mCanCheckExtHaptic && z) {
            return this.mHapticFeedbackUtil.performExtHapticFeedback(uri, true);
        }
        return this.mHapticFeedbackUtil.performExtHapticFeedback(uri);
    }

    public boolean performExtHapticFeedback(Uri uri) {
        if (this.mHapticFeedbackUtil == null) {
            return false;
        }
        HapticLog.printTrace("performExtHapticFeedback: uri: " + uri);
        return this.mHapticFeedbackUtil.performExtHapticFeedback(uri);
    }

    public boolean performHapticFeedback(int i, int i2, boolean z) {
        return performHapticFeedback((VibrationAttributes) null, i, i2, z);
    }

    public boolean performHapticFeedback(VibrationAttributes vibrationAttributes, int i, int i2, boolean z) {
        int iObtainFeedBack;
        if (this.mHapticFeedbackUtil == null || (iObtainFeedBack = HapticCompat.obtainFeedBack(i)) == -1) {
            return false;
        }
        try {
            HapticLog.printTrace("performHapticFeedback: attributes: " + vibrationAttributes + ", effectId: " + i + ", effectStrength: " + i2 + ", always: " + z);
            if (PlatformConstants.romHapticVersion >= 1.2d && mPerformHapticFeedbackFourParamsMethod1Exist) {
                return this.mHapticFeedbackUtil.performHapticFeedback(vibrationAttributes, i, z, i2);
            }
            return this.mHapticFeedbackUtil.performHapticFeedback(iObtainFeedBack, z, i2);
        } catch (Exception e) {
            android.util.Log.e(TAG, "Failed to perform haptic!", e);
            return false;
        }
    }

    public boolean performHapticFeedback(int i, int i2) {
        return performHapticFeedback((VibrationAttributes) null, i, i2);
    }

    public boolean performHapticFeedback(VibrationAttributes vibrationAttributes, int i, int i2) {
        int iObtainFeedBack;
        if (this.mHapticFeedbackUtil != null && (iObtainFeedBack = HapticCompat.obtainFeedBack(i)) != -1) {
            try {
                HapticLog.printTrace("performHapticFeedback: attributes: " + vibrationAttributes + ", effectId: " + i + ", effectStrength: " + i2);
                if (PlatformConstants.romHapticVersion >= 1.2d && mPerformHapticFeedbackFourParamsMethod1Exist) {
                    return this.mHapticFeedbackUtil.performHapticFeedback(vibrationAttributes, iObtainFeedBack, false, i2);
                }
                return this.mHapticFeedbackUtil.performHapticFeedback(iObtainFeedBack, false, i2);
            } catch (Exception e) {
                android.util.Log.e(TAG, "Failed to perform haptic!", e);
            }
        }
        return false;
    }

    public boolean performHapticFeedback(int i, boolean z) {
        return performHapticFeedback((VibrationAttributes) null, i, z);
    }

    public boolean performHapticFeedback(VibrationAttributes vibrationAttributes, int i, boolean z) {
        int iObtainFeedBack;
        if (this.mHapticFeedbackUtil == null || (iObtainFeedBack = HapticCompat.obtainFeedBack(i)) == -1) {
            return false;
        }
        try {
            HapticLog.printTrace("performHapticFeedback: attributes: " + vibrationAttributes + ", effectId: " + i + ", always: " + z);
            if (PlatformConstants.romHapticVersion >= 1.2d && mPerformExtHapticFeedbackThreeParamsMethodExist) {
                return this.mHapticFeedbackUtil.performHapticFeedback(vibrationAttributes, iObtainFeedBack, z);
            }
            return this.mHapticFeedbackUtil.performHapticFeedback(iObtainFeedBack, z);
        } catch (Exception e) {
            android.util.Log.e(TAG, "Failed to perform haptic!", e);
            return false;
        }
    }

    public boolean performHapticFeedback(int i) {
        return performHapticFeedback((VibrationAttributes) null, i);
    }

    public boolean performHapticFeedback(VibrationAttributes vibrationAttributes, int i) {
        return performHapticFeedback(vibrationAttributes, i, false);
    }

    public void performHapticFeedbackAsync(final int i) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            performHapticFeedback(i);
        } else {
            sSingleThread.execute(new Runnable() { // from class: miuix.util.HapticFeedbackCompat.1
                @Override // java.lang.Runnable
                public void run() {
                    HapticFeedbackCompat.this.performHapticFeedback(i);
                }
            });
        }
    }

    public boolean performEmulationHaptic(int i, double d) {
        return performHapticFeedback(i, d, PHYSICAL_EMULATION_REASON);
    }

    public boolean performHapticFeedback(int i, double d, String str) {
        return performHapticFeedback((VibrationAttributes) null, i, d, str);
    }

    public boolean performHapticFeedback(VibrationAttributes vibrationAttributes, int i, double d, String str) {
        int iObtainFeedBack;
        if (this.mHapticFeedbackUtil == null || !mIsSupportHapticWithReason || (iObtainFeedBack = HapticCompat.obtainFeedBack(i)) == -1) {
            return false;
        }
        try {
            HapticLog.printTrace("performHapticFeedback: attributes: " + vibrationAttributes + ", effectId: " + i + ", suitIntensity: " + d + ", reason: " + str);
            if (PlatformConstants.romHapticVersion >= 1.2d && mPerformHapticFeedbackFourParamsMethod2Exist) {
                return this.mHapticFeedbackUtil.performHapticFeedback(vibrationAttributes, iObtainFeedBack, d, str);
            }
            return this.mHapticFeedbackUtil.performHapticFeedback(iObtainFeedBack, d, str);
        } catch (Exception e) {
            android.util.Log.e(TAG, "Failed to perform haptic!", e);
            return false;
        }
    }

    @Deprecated
    public void release() {
        HapticFeedbackUtil hapticFeedbackUtil = this.mHapticFeedbackUtil;
        if (hapticFeedbackUtil != null) {
            hapticFeedbackUtil.release();
        }
    }

    public void stop() {
        HapticFeedbackUtil hapticFeedbackUtil = this.mHapticFeedbackUtil;
        if (hapticFeedbackUtil != null) {
            if (mCanStop) {
                hapticFeedbackUtil.stop();
            } else {
                hapticFeedbackUtil.release();
            }
        }
    }

    public boolean supportKeyboardIntensity() {
        return SystemProperties.getBoolean("sys.haptic.intensityforkeyboard", false);
    }
}
