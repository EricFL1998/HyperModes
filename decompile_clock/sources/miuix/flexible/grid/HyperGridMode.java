package miuix.flexible.grid;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: classes2.dex */
@Retention(RetentionPolicy.SOURCE)
public @interface HyperGridMode {
    public static final int DYNAMIC_COLUMN_FIXED_CELL = 2;
    public static final int DYNAMIC_COLUMN_FIXED_SPACING = 0;
    public static final int DYNAMIC_COLUMN_FIXED_SPACING_FULL = 1;
    public static final int FIXED_COLUMN = 4;
}
