package com.android.deskclock.compat;

import android.provider.MiuiSettings;

/* JADX INFO: loaded from: classes.dex */
public class CoverUtil {

    public enum CoverType {
        X7_STYLE,
        A1_STYLE,
        A7_LATTICE,
        B7_FULL
    }

    public static CoverType getCoverType() {
        MiuiSettings.System.SmallWindowType smallWindowMode = MiuiSettings.System.getSmallWindowMode();
        if (smallWindowMode == null) {
            return null;
        }
        int i = AnonymousClass1.$SwitchMap$android$provider$MiuiSettings$System$SmallWindowType[smallWindowMode.ordinal()];
        if (i == 1) {
            return CoverType.X7_STYLE;
        }
        if (i == 2) {
            return CoverType.A1_STYLE;
        }
        if (i == 3) {
            return CoverType.A7_LATTICE;
        }
        if (i != 4) {
            return null;
        }
        return CoverType.B7_FULL;
    }

    /* JADX INFO: renamed from: com.android.deskclock.compat.CoverUtil$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$android$provider$MiuiSettings$System$SmallWindowType;

        static {
            int[] iArr = new int[MiuiSettings.System.SmallWindowType.values().length];
            $SwitchMap$android$provider$MiuiSettings$System$SmallWindowType = iArr;
            try {
                iArr[MiuiSettings.System.SmallWindowType.X7_STYLE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$android$provider$MiuiSettings$System$SmallWindowType[MiuiSettings.System.SmallWindowType.A1_STYLE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$android$provider$MiuiSettings$System$SmallWindowType[MiuiSettings.System.SmallWindowType.A7_LATTICE.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$android$provider$MiuiSettings$System$SmallWindowType[MiuiSettings.System.SmallWindowType.B7_FULL.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }
}
