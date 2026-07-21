package miuix.flexible.template.level;

import android.content.Context;
import miuix.core.util.MiuixUIUtils;

/* JADX INFO: loaded from: classes2.dex */
public class FontLevelSupplier implements LevelSupplier {
    public static final int LEVEL_LARGE = 2;
    public static final int LEVEL_NORMAL = 1;
    private final Context mContext;

    public FontLevelSupplier(Context context) {
        this.mContext = context;
    }

    @Override // miuix.flexible.template.level.LevelSupplier
    public int getLevel() {
        return MiuixUIUtils.getFontLevel(this.mContext) == 1 ? 1 : 2;
    }
}
