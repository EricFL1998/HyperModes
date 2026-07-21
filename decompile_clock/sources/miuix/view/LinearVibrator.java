package miuix.view;

import android.util.Log;
import android.view.View;
import androidx.collection.SparseArrayCompat;
import miui.util.HapticFeedbackUtil;
import miuix.HapticLog;

/* JADX INFO: loaded from: classes3.dex */
class LinearVibrator implements HapticFeedbackProvider {
    private static final String TAG = "LinearVibrator";
    private final SparseArrayCompat<Integer> mIds = new SparseArrayCompat<>();

    private LinearVibrator() {
        buildIds();
    }

    @Override // miuix.view.HapticFeedbackProvider
    public boolean performHapticFeedback(View view, int i) {
        int iIndexOfKey = this.mIds.indexOfKey(i);
        if (iIndexOfKey < 0) {
            Log.w(TAG, String.format("feedback(0x%08x-%s) is not found in current platform(v%d)", Integer.valueOf(i), HapticFeedbackConstants.nameOf(i), Integer.valueOf(PlatformConstants.VERSION)));
            return false;
        }
        int iIntValue = this.mIds.valueAt(iIndexOfKey).intValue();
        if (!HapticFeedbackUtil.isSupportLinearMotorVibrate(iIntValue)) {
            Log.w(TAG, String.format("unsupported feedback: 0x%08x. platform version: %d", Integer.valueOf(iIntValue), Integer.valueOf(PlatformConstants.VERSION)));
            return false;
        }
        HapticLog.printTrace("performHapticFeedback view: " + view + ", feedbackConstant: " + i);
        return view.performHapticFeedback(iIntValue);
    }

    int obtainFeedBack(int i) {
        int iIndexOfKey = this.mIds.indexOfKey(i);
        if (iIndexOfKey >= 0) {
            return this.mIds.valueAt(iIndexOfKey).intValue();
        }
        return -1;
    }

    public boolean supportLinearMotor(int i) {
        int iIndexOfKey = this.mIds.indexOfKey(i);
        if (iIndexOfKey < 0) {
            Log.w(TAG, String.format("feedback(0x%08x-%s) is not found in current platform(v%d)", Integer.valueOf(i), HapticFeedbackConstants.nameOf(i), Integer.valueOf(PlatformConstants.VERSION)));
            return false;
        }
        int iIntValue = this.mIds.valueAt(iIndexOfKey).intValue();
        if (!HapticFeedbackUtil.isSupportLinearMotorVibrate(iIntValue)) {
            Log.w(TAG, String.format("unsupported feedback: 0x%08x. platform version: %d", Integer.valueOf(iIntValue), Integer.valueOf(PlatformConstants.VERSION)));
            return false;
        }
        return HapticFeedbackUtil.isSupportLinearMotorVibrate(iIntValue);
    }

    private void buildIds() {
        this.mIds.append(HapticFeedbackConstants.MIUI_TAP_NORMAL, 268435456);
        this.mIds.append(HapticFeedbackConstants.MIUI_TAP_LIGHT, 268435457);
        this.mIds.append(HapticFeedbackConstants.MIUI_FLICK, 268435458);
        this.mIds.append(HapticFeedbackConstants.MIUI_SWITCH, 268435459);
        this.mIds.append(HapticFeedbackConstants.MIUI_MESH_HEAVY, 268435460);
        this.mIds.append(HapticFeedbackConstants.MIUI_MESH_NORMAL, 268435461);
        this.mIds.append(HapticFeedbackConstants.MIUI_MESH_LIGHT, 268435462);
        this.mIds.append(HapticFeedbackConstants.MIUI_LONG_PRESS, 268435463);
        this.mIds.append(HapticFeedbackConstants.MIUI_POPUP_NORMAL, 268435464);
        this.mIds.append(HapticFeedbackConstants.MIUI_POPUP_LIGHT, 268435465);
        if (PlatformConstants.VERSION < 2) {
            return;
        }
        this.mIds.append(HapticFeedbackConstants.MIUI_PICK_UP, 268435466);
        this.mIds.append(HapticFeedbackConstants.MIUI_SCROLL_EDGE, 268435467);
        this.mIds.append(HapticFeedbackConstants.MIUI_TRIGGER_DRAWER, 268435468);
        if (PlatformConstants.VERSION < 3) {
            return;
        }
        this.mIds.append(HapticFeedbackConstants.MIUI_FLICK_LIGHT, 268435469);
        if (PlatformConstants.VERSION < 4) {
            return;
        }
        this.mIds.append(HapticFeedbackConstants.MIUI_HOLD, 268435470);
        if (PlatformConstants.VERSION < 5) {
            return;
        }
        this.mIds.append(HapticFeedbackConstants.MIUI_BOUNDARY_SPATIAL, 268435471);
        this.mIds.append(HapticFeedbackConstants.MIUI_BOUNDARY_TIME, 268435472);
        this.mIds.append(HapticFeedbackConstants.MIUI_BUTTON_LARGE, 268435473);
        this.mIds.append(HapticFeedbackConstants.MIUI_BUTTON_MIDDLE, 268435474);
        this.mIds.append(HapticFeedbackConstants.MIUI_BUTTON_SMALL, 268435475);
        this.mIds.append(HapticFeedbackConstants.MIUI_GEAR_LIGHT, 268435476);
        this.mIds.append(HapticFeedbackConstants.MIUI_GEAR_HEAVY, 268435477);
        this.mIds.append(HapticFeedbackConstants.MIUI_KEYBOARD, 268435478);
        this.mIds.append(HapticFeedbackConstants.MIUI_ALERT, 268435479);
        this.mIds.append(HapticFeedbackConstants.MIUI_ZAXIS_SWITCH, 268435480);
    }

    static {
        initialize();
    }

    private static void initialize() {
        boolean zIsSupportLinearMotorVibrate;
        if (PlatformConstants.VERSION < 1) {
            Log.w(TAG, "MiuiHapticFeedbackConstants not found or not compatible for LinearVibrator.");
            return;
        }
        try {
            zIsSupportLinearMotorVibrate = HapticFeedbackUtil.isSupportLinearMotorVibrate();
        } catch (Throwable th) {
            Log.w(TAG, "MIUI Haptic Implementation is not available", th);
            zIsSupportLinearMotorVibrate = false;
        }
        if (!zIsSupportLinearMotorVibrate) {
            Log.w(TAG, "linear motor is not supported in this platform.");
        } else {
            HapticCompat.registerProvider(new LinearVibrator());
            Log.i(TAG, "setup LinearVibrator success.");
        }
    }
}
