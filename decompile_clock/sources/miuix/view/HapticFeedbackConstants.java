package miuix.view;

import androidx.collection.SparseArrayCompat;

/* JADX INFO: loaded from: classes3.dex */
public class HapticFeedbackConstants {
    private static final String ILLEGAL_FEEDBACK = "IllegalFeedback";
    public static final int MIUI_KEYBOARD_CLICKY_DOWN_RTP = 193;
    public static final int MIUI_KEYBOARD_CLICKY_UP_RTP = 194;
    public static final int MIUI_KEYBOARD_LINEAR_DOWN_RTP = 195;
    public static final int MIUI_KEYBOARD_LINEAR_UP_RTP = 196;
    private static final SparseArrayCompat<String> NAMES = new SparseArrayCompat<>();
    static final int MIUI_HAPTIC_START = 268435456;
    static final int MIUI_HAPTIC_VERSION_1_START = MIUI_HAPTIC_START;
    public static final int MIUI_VIRTUAL_RELEASE = MIUI_HAPTIC_START;
    public static final int MIUI_TAP_NORMAL = 268435457;
    public static final int MIUI_TAP_LIGHT = 268435458;
    public static final int MIUI_FLICK = 268435459;
    public static final int MIUI_SWITCH = 268435460;
    public static final int MIUI_MESH_HEAVY = 268435461;
    public static final int MIUI_MESH_NORMAL = 268435462;
    public static final int MIUI_MESH_LIGHT = 268435463;
    public static final int MIUI_LONG_PRESS = 268435464;
    public static final int MIUI_POPUP_NORMAL = 268435465;
    public static final int MIUI_POPUP_LIGHT = 268435466;
    public static final int MIUI_PICK_UP = 268435467;
    public static final int MIUI_SCROLL_EDGE = 268435468;
    public static final int MIUI_TRIGGER_DRAWER = 268435469;
    public static final int MIUI_FLICK_LIGHT = 268435470;
    public static final int MIUI_HOLD = 268435471;
    static final int MIUI_HAPTIC_VERSION_1_END = 268435472;
    static final int MIUI_HAPTIC_VERSION_2_START = 268435472;
    public static final int MIUI_BOUNDARY_SPATIAL = 268435472;
    public static final int MIUI_BOUNDARY_TIME = 268435473;
    public static final int MIUI_BUTTON_LARGE = 268435474;
    public static final int MIUI_BUTTON_MIDDLE = 268435475;
    public static final int MIUI_BUTTON_SMALL = 268435476;
    public static final int MIUI_GEAR_LIGHT = 268435477;
    public static final int MIUI_GEAR_HEAVY = 268435478;
    public static final int MIUI_KEYBOARD = 268435479;
    public static final int MIUI_ALERT = 268435480;
    public static final int MIUI_ZAXIS_SWITCH = 268435481;
    static final int MIUI_HAPTIC_VERSION_2_END = 268435482;
    static final int MIUI_HAPTIC_END = 268435482;

    static {
        buildNames();
    }

    public static String nameOf(int i) {
        return NAMES.get(i, ILLEGAL_FEEDBACK);
    }

    private static void buildNames() {
        SparseArrayCompat<String> sparseArrayCompat = NAMES;
        sparseArrayCompat.append(MIUI_VIRTUAL_RELEASE, "MIUI_VIRTUAL_RELEASE");
        sparseArrayCompat.append(MIUI_TAP_NORMAL, "MIUI_TAP_NORMAL");
        sparseArrayCompat.append(MIUI_TAP_LIGHT, "MIUI_TAP_LIGHT");
        sparseArrayCompat.append(MIUI_FLICK, "MIUI_FLICK");
        sparseArrayCompat.append(MIUI_SWITCH, "MIUI_SWITCH");
        sparseArrayCompat.append(MIUI_MESH_HEAVY, "MIUI_MESH_HEAVY");
        sparseArrayCompat.append(MIUI_MESH_NORMAL, "MIUI_MESH_NORMAL");
        sparseArrayCompat.append(MIUI_MESH_LIGHT, "MIUI_MESH_LIGHT");
        sparseArrayCompat.append(MIUI_LONG_PRESS, "MIUI_LONG_PRESS");
        sparseArrayCompat.append(MIUI_POPUP_NORMAL, "MIUI_POPUP_NORMAL");
        sparseArrayCompat.append(MIUI_POPUP_LIGHT, "MIUI_POPUP_LIGHT");
        sparseArrayCompat.append(MIUI_PICK_UP, "MIUI_PICK_UP");
        sparseArrayCompat.append(MIUI_SCROLL_EDGE, "MIUI_SCROLL_EDGE");
        sparseArrayCompat.append(MIUI_TRIGGER_DRAWER, "MIUI_TRIGGER_DRAWER");
        sparseArrayCompat.append(MIUI_FLICK_LIGHT, "MIUI_FLICK_LIGHT");
        sparseArrayCompat.append(MIUI_HOLD, "MIUI_HOLD");
        sparseArrayCompat.append(MIUI_BOUNDARY_SPATIAL, "MIUI_BOUNDARY_SPATIAL");
        sparseArrayCompat.append(MIUI_BOUNDARY_TIME, "MIUI_BOUNDARY_TIME");
        sparseArrayCompat.append(MIUI_BUTTON_LARGE, "MIUI_BUTTON_LARGE");
        sparseArrayCompat.append(MIUI_BUTTON_MIDDLE, "MIUI_BUTTON_MIDDLE");
        sparseArrayCompat.append(MIUI_BUTTON_SMALL, "MIUI_BUTTON_SMALL");
        sparseArrayCompat.append(MIUI_GEAR_LIGHT, "MIUI_GEAR_LIGHT");
        sparseArrayCompat.append(MIUI_GEAR_HEAVY, "MIUI_GEAR_HEAVY");
        sparseArrayCompat.append(MIUI_KEYBOARD, "MIUI_KEYBOARD");
        sparseArrayCompat.append(MIUI_ALERT, "MIUI_ALERT");
        sparseArrayCompat.append(MIUI_ZAXIS_SWITCH, "MIUI_ZAXIS_SWITCH");
    }
}
