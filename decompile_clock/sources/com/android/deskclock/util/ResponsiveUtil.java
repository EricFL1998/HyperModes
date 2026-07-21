package com.android.deskclock.util;

import android.content.Context;
import miuix.responsive.map.ScreenSpec;

/* JADX INFO: loaded from: classes.dex */
public class ResponsiveUtil {
    public static boolean inExternalSplitScreen(ScreenSpec screenSpec, Context context) {
        int i = screenSpec.screenMode;
        if (i != 4097) {
            if (i == 4098) {
                return !Util.isInInternalScreen(context);
            }
            if (i != 4100) {
                return false;
            }
        }
        return true;
    }

    public static boolean inHalfMode(ScreenSpec screenSpec) {
        return 4098 == screenSpec.screenMode;
    }

    public static boolean inTwoThird(ScreenSpec screenSpec) {
        return 4100 == screenSpec.screenMode;
    }

    public static boolean inOneThird(ScreenSpec screenSpec) {
        return 4097 == screenSpec.screenMode;
    }

    public static boolean inFreeFormWindow(ScreenSpec screenSpec) {
        switch (screenSpec.screenMode) {
            case 8192:
            case 8193:
            case 8194:
            case 8195:
            case 8196:
                return true;
            default:
                return false;
        }
    }

    public static boolean inInternalSplitScreen(ScreenSpec screenSpec, Context context) {
        return Util.isInInternalScreen(context) && screenSpec.screenMode == 4098;
    }

    public static boolean inPadFreeFormWindow(ScreenSpec screenSpec) {
        switch (screenSpec.screenMode) {
            case 8192:
            case 8193:
            case 8194:
            case 8195:
            case 8196:
                return PadAdapterUtil.IS_PAD;
            default:
                return false;
        }
    }

    public static boolean inSplitScreen(ScreenSpec screenSpec, Context context) {
        return inExternalSplitScreen(screenSpec, context) || inInternalSplitScreen(screenSpec, context);
    }
}
